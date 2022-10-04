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

package com.abavilla.fpi.sms.controller;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import com.abavilla.fpi.fw.controller.AbsBaseResource;
import com.abavilla.fpi.fw.dto.impl.RespDto;
import com.abavilla.fpi.fw.exceptions.FPISvcEx;
import com.abavilla.fpi.sms.dto.LoginDto;
import com.abavilla.fpi.sms.dto.WebhookLoginDto;
import com.abavilla.fpi.sms.entity.User;
import com.abavilla.fpi.sms.service.UserSvc;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.NoCache;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

/**
 * Resource for Login to FPI System without going through keycloak authentication server
 * URI Path: {@code "/fpi/login"}
 *
 * @author <a href="mailto:vincevillamora@gmail.com">Vince Villamora</a>
 */
@Path("/fpi/login")
public class UserResource extends AbsBaseResource<LoginDto, User, UserSvc> {

  @POST
  @NoCache
  @Path("trusted")
  public Uni<RestResponse<Void>> loginFromTrustedIdentityProvider(
      WebhookLoginDto loginDto,
      @QueryParam("refreshToken")Boolean refreshToken){
    return service.authorizedLogin(loginDto);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @ServerExceptionMapper
  protected RestResponse<RespDto<Object>> mapException(FPISvcEx x) {
    return super.mapException(x);
  }

}