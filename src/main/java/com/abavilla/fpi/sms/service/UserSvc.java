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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import com.abavilla.fpi.fw.exceptions.FPISvcEx;
import com.abavilla.fpi.fw.service.AbsRepoSvc;
import com.abavilla.fpi.fw.util.DateUtil;
import com.abavilla.fpi.sms.dto.LoginDto;
import com.abavilla.fpi.sms.dto.WebhookLoginDto;
import com.abavilla.fpi.sms.entity.User;
import com.abavilla.fpi.sms.entity.UserStatus;
import com.abavilla.fpi.sms.repo.UserRepo;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.RestResponse;
import org.keycloak.authorization.client.AuthzClient;

@ApplicationScoped
public class UserSvc extends AbsRepoSvc<LoginDto, User, UserRepo> {
  @Inject
  AuthzClient authzClient;

  public Uni<RestResponse<Void>> authorizedLogin(WebhookLoginDto loginDto) {
    var byMetaId = repo.findByMetaId(loginDto.getUsername());
    return byMetaId.chain(f -> {
      if (f.isEmpty()) {
        // register
        User user = new User();
        user.setMetaId(loginDto.getUsername());
        user.setMobile(loginDto.getMobile());
        user.setStatus(UserStatus.PENDING);
        user.setDateCreated(DateUtil.now());
        user.setDateUpdated(DateUtil.now());
        user.setRegistrationDate(DateUtil.now());
        user.setLastAccess(DateUtil.now());
        return repo.persist(user).map(ignored -> RestResponse.accepted());
      } else {
        // get current user
        User user = f.get();
        user.setLastAccess(DateUtil.now());
        user.setDateUpdated(DateUtil.now());
        if (user.getStatus() == UserStatus.VERIFIED) {
          return repo.persistOrUpdate(user).map(ignored -> RestResponse.ok());
        } else {
          throw new FPISvcEx("User not yet verified",
              Response.Status.FORBIDDEN.getStatusCode());
        }
      }
    });
  }
}
