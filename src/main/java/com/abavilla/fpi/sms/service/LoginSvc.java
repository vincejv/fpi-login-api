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

package com.abavilla.fpi.sms.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;

import com.abavilla.fpi.fw.service.AbsSvc;
import com.abavilla.fpi.sms.dto.LoginDto;
import com.abavilla.fpi.sms.dto.SessionDto;
import com.abavilla.fpi.sms.entity.Session;
import com.abavilla.fpi.sms.mapper.SessionMapper;
import com.abavilla.fpi.sms.repo.SessionRepo;
import com.abavilla.fpi.sms.util.LoginConst;
import com.abavilla.fpi.sms.util.LoginUtil;
import io.smallrye.mutiny.Uni;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.util.HttpResponseException;
import org.keycloak.representations.AccessTokenResponse;

/**
 * Service layer for creating and managing login sessions.
 *
 * @author <a href="mailto:vincevillamora@gmail.com">Vince Villamora</a>
 */
@ApplicationScoped
public class LoginSvc extends AbsSvc<SessionDto, Session> {

  /**
   * Advance repo for operating in {@link Session} entities.
   */
  @Inject
  SessionRepo advRepo;

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
  public Uni<SessionDto> login(LoginDto login) {
    Uni<Optional<Session>> byUsername = advRepo.findByUsername(login.getUsername());
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
    }).map(this::mapToDto);
  }

  /**
   * Force to retrieve a new access token from authentication server.
   *
   * @param login Login credentials
   * @return {@link SessionDto} object
   */
  public Uni<SessionDto> refreshToken(LoginDto login) {
    Uni<Optional<Session>> byUsername = advRepo.findByUsername(login.getUsername());
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
    }).map(this::mapToDto);
  }

  /**
   * Creates a new {@link Session} from login credentials and authentication response.
   *
   * @param session Session to map
   * @param login Login credentials
   * @param auth Authentication response
   */
  private void mapLoginToSession(Session session, LoginDto login, AccessTokenResponse auth) {
    session.setUsername(login.getUsername());
    session.setPassword(LoginUtil.hashPassword(
        login.getPassword().toCharArray()));
    session.setAccessToken(auth.getToken());
    session.setRefreshToken(auth.getRefreshToken());
    session.setDateCreated(LocalDateTime.now(ZoneOffset.UTC));
    session.setIpAddress(login.getRemoteAddress());
    session.setUserAgent(login.getUserAgent());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SessionDto mapToDto(Session entity) {
    return mapper.mapToDto(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Session mapToEntity(SessionDto dto) {
    return mapper.mapToEntity(dto);
  }
}
