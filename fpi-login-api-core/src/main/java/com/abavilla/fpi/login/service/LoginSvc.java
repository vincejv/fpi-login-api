/******************************************************************************
 * FPI Application - Abavilla                                                 *
 * Copyright (C) 2022  Vince Jerald Villamora                                 *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.     *
 ******************************************************************************/

package com.abavilla.fpi.login.service;

import java.time.Duration;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;

import com.abavilla.fpi.fw.service.AbsRepoSvc;
import com.abavilla.fpi.fw.util.DateUtil;
import com.abavilla.fpi.login.entity.Session;
import com.abavilla.fpi.login.ext.dto.LoginDto;
import com.abavilla.fpi.login.ext.dto.PasswordLoginDto;
import com.abavilla.fpi.login.ext.dto.SessionDto;
import com.abavilla.fpi.login.mapper.SessionMapper;
import com.abavilla.fpi.login.repo.SessionRepo;
import com.abavilla.fpi.login.util.LoginConst;
import com.abavilla.fpi.login.util.LoginUtil;
import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.util.HttpResponseException;
import org.keycloak.representations.AccessTokenResponse;

/**
 * Service layer for creating and managing login sessions.
 *
 * @author <a href="mailto:vincevillamora@gmail.com">Vince Villamora</a>
 */
@ApplicationScoped
public class LoginSvc extends AbsRepoSvc<LoginDto, Session, SessionRepo> {

  @ConfigProperty(name = "session.grace-period")
  Long tokenGracePeriod;

  /**
   * Client used for authorizing with Keycloak server.
   */
  @Inject
  AuthzClient authzClient;

  /**
   * DTO to Entity mapper for {@link Session}
   */
  @Inject
  SessionMapper mapper;

  /**
   * Performs login, if existing session is detected, return currently established token.
   *
   * @param login {@link LoginDto} Object containing credentials
   * @return {@link SessionDto} Session information
   */
  public Uni<SessionDto> login(PasswordLoginDto login) {
    Uni<Optional<Session>> byUsername = repo.findByUsername(login.getUsername());
    return byUsername.chain(sessionOpt -> {
      if (sessionOpt.isEmpty()) {
        AccessTokenResponse auth = null;
        try {
          auth = authzClient.obtainAccessToken(login.getUsername(), login.getPassword());
        } catch (HttpResponseException ex) {
          return Uni.createFrom().failure(
              new NotAuthorizedException(LoginConst.INVALID_USER_CREDENTIALS));
        }
        Session newSession = new Session();
        mapLoginToSession(newSession, login, auth);
        return repo.persist(newSession);
      } else if (LoginUtil.verifyHash(login.getPassword().toCharArray(),
          sessionOpt.get().getPassword())) {
        return Uni.createFrom().item(sessionOpt.get());
      } else {
        return Uni.createFrom().failure(
            new NotAuthorizedException(LoginConst.INVALID_USER_CREDENTIALS));
      }
    })
    .onFailure(ex -> ex instanceof MongoWriteException wEx &&
        wEx.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY))
    .retry().withBackOff(
      Duration.ofSeconds(3)).withJitter(0.2).indefinitely()
    .map(mapper::mapToDto);
  }

  /**
   * Force to retrieve a new access token from authentication server.
   *
   * @param login Login credentials
   * @return {@link SessionDto} object
   */
  public Uni<SessionDto> refreshToken(PasswordLoginDto login) {
    Uni<Optional<Session>> byUsername = repo.findByUsername(login.getUsername());
    return byUsername.chain(sessionOpt -> {
      AccessTokenResponse auth = null;

      try {
        auth = authzClient.obtainAccessToken(login.getUsername(), login.getPassword());
      } catch (HttpResponseException ex) {
        return Uni.createFrom().failure(
            new NotAuthorizedException(LoginConst.INVALID_USER_CREDENTIALS));
      }

      Session session = sessionOpt.orElse(new Session());
      mapLoginToSession(session, login, auth);
      return repo.persistOrUpdate(session);
    }).map(mapper::mapToDto);
  }

  /**
   * Creates a new {@link Session} from login credentials and authentication response.
   *
   * @param session Session to map
   * @param login Login credentials
   * @param auth Authentication response
   */
  private void mapLoginToSession(Session session, PasswordLoginDto login, AccessTokenResponse auth) {
    session.setUsername(login.getUsername());
    session.setPassword(LoginUtil.hashPassword(
        login.getPassword().toCharArray()));
    session.setAccessToken(auth.getToken());
    session.setRefreshToken(auth.getRefreshToken());
    session.setDateCreated(DateUtil.now());
    session.setIpAddress(login.getRemoteAddress());
    session.setUserAgent(login.getUserAgent());
    session.setRefreshTokenExpiry(DateUtil.now()
        .plusSeconds(auth.getExpiresIn() - tokenGracePeriod));
  }

}
