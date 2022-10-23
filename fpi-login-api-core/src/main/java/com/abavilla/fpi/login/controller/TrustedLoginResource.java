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

package com.abavilla.fpi.login.controller;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import com.abavilla.fpi.fw.controller.AbsBaseResource;
import com.abavilla.fpi.fw.dto.IDto;
import com.abavilla.fpi.fw.dto.impl.RespDto;
import com.abavilla.fpi.fw.exceptions.FPISvcEx;
import com.abavilla.fpi.fw.util.DateUtil;
import com.abavilla.fpi.login.entity.User;
import com.abavilla.fpi.login.ext.dto.LoginDto;
import com.abavilla.fpi.login.ext.dto.SessionDto;
import com.abavilla.fpi.login.ext.dto.WebhookLoginDto;
import com.abavilla.fpi.login.service.TrustedLoginSvc;
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
public class TrustedLoginResource extends AbsBaseResource<LoginDto, User, TrustedLoginSvc> {

  @POST
  @NoCache
  @Path("trusted")
  public Uni<RestResponse<RespDto<SessionDto>>> loginFromTrustedIdentityProvider(
      WebhookLoginDto loginDto,
      @QueryParam("refreshToken")Boolean refreshToken){
    return service.authorizedLogin(loginDto).map(sessionDto -> {
      RespDto<SessionDto> resp = new RespDto<>();
      resp.setStatus(String.valueOf(sessionDto.getStatus()));
      resp.setTimestamp(DateUtil.nowAsStr());
      resp.setResp(sessionDto);
      return RestResponse.ok(resp);
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @ServerExceptionMapper
  protected RestResponse<RespDto<IDto>> mapException(FPISvcEx x) {
    return super.mapException(x);
  }

}