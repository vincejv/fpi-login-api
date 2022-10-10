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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import com.abavilla.fpi.fw.exceptions.FPISvcEx;
import com.abavilla.fpi.fw.service.AbsRepoSvc;
import com.abavilla.fpi.fw.util.DateUtil;
import com.abavilla.fpi.login.dto.LoginDto;
import com.abavilla.fpi.login.dto.SessionDto;
import com.abavilla.fpi.login.dto.WebhookLoginDto;
import com.abavilla.fpi.login.entity.Session;
import com.abavilla.fpi.login.entity.User;
import com.abavilla.fpi.login.entity.UserStatus;
import com.abavilla.fpi.login.mapper.SessionMapper;
import com.abavilla.fpi.login.repo.SessionRepo;
import com.abavilla.fpi.login.repo.UserRepo;
import com.abavilla.fpi.login.util.LoginUtil;
import com.mongodb.DuplicateKeyException;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.RestResponse;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.representations.AccessTokenResponse;

@ApplicationScoped
public class TrustedLoginSvc extends AbsRepoSvc<LoginDto, User, UserRepo> {

  @ConfigProperty(name = "fpi.app-to-app.auth.trusted-key")
  String trustedKey;

  @Inject
  SessionRepo sessionRepo;

  /**
   * DTO to Entity mapper for {@link Session}
   */
  @Inject
  SessionMapper mapper;

  @Inject
  AuthzClient authzClient;

  public Uni<SessionDto> authorizedLogin(WebhookLoginDto loginDto) {
    var byMetaId = repo.findByMetaId(loginDto.getUsername());
    return byMetaId.chain(authorizedUser -> {
      if (authorizedUser.isEmpty()) {
        // register
        User user = new User();
        user.setMetaId(loginDto.getUsername());
        user.setMobile(loginDto.getMobile());
        user.setStatus(UserStatus.PENDING);
        user.setDateCreated(DateUtil.now());
        user.setDateUpdated(DateUtil.now());
        user.setRegistrationDate(DateUtil.now());
        user.setLastAccess(DateUtil.now());
        return repo.persist(user).map(ignored -> {
          SessionDto sessionDto = new SessionDto();
          sessionDto.setStatus(SessionDto.SessionStatus.CREATED_USER);
          return sessionDto;
        });
      } else {
        // get registered user
        User user = authorizedUser.get();
        user.setLastAccess(DateUtil.now());
        user.setDateUpdated(DateUtil.now());
        if (user.getStatus() == UserStatus.VERIFIED) {
          return repo.persistOrUpdate(user).chain(() -> {
            return sessionRepo
              .findByUsername(loginDto.getUsername()).chain(sessionOpt -> {
                Session session = sessionOpt.orElse(new Session());
                if (sessionOpt.isEmpty()) {
                  // create a new session
                  var tokenResponse = authzClient.obtainAccessToken(loginDto.getUsername(), trustedKey);
                  mapLoginToSession(session, loginDto, tokenResponse);
                  return sessionRepo.persistOrUpdate(session);
                } else {
                  if (!LoginUtil.verifyHash(trustedKey.toCharArray(), session.getPassword())) {
                    throw new FPISvcEx("Incorrect login",
                        RestResponse.StatusCode.UNAUTHORIZED);
                  }
                }
                return Uni.createFrom().item(session);
              })
              .map(savedSession -> {
                SessionDto sessionDto = mapper.mapToDto(savedSession);
                sessionDto.setStatus(SessionDto.SessionStatus.ESTABLISHED);
                return sessionDto;
              });
          }).onFailure(DuplicateKeyException.class).retry().withBackOff(
              Duration.ofSeconds(3)).withJitter(0.2).indefinitely();
        } else {
          throw new FPISvcEx("User not yet verified",
              Response.Status.FORBIDDEN.getStatusCode());
        }
      }
    });
  }

  /**
   * Creates a new {@link Session} from login credentials and authentication response.
   *
   * @param session Session to map
   * @param login Login credentials
   * @param auth Authentication response
   */
  private void mapLoginToSession(Session session, WebhookLoginDto login, AccessTokenResponse auth) {
    session.setUsername(login.getUsername());
    session.setPassword(LoginUtil.hashPassword(
        trustedKey.toCharArray()));
    session.setAccessToken(auth.getToken());
    session.setRefreshToken(auth.getRefreshToken());
    session.setDateCreated(DateUtil.now());
    session.setRefreshTokenExpiry(DateUtil.now()
        .plusSeconds(auth.getExpiresIn()));
  }

}
