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
import javax.ws.rs.core.Context;

import com.abavilla.fpi.fw.controller.AbsBaseResource;
import com.abavilla.fpi.fw.dto.impl.RespDto;
import com.abavilla.fpi.fw.exceptions.FPISvcEx;
import com.abavilla.fpi.fw.util.HttpUtil;
import com.abavilla.fpi.login.dto.LoginDto;
import com.abavilla.fpi.login.dto.PasswordLoginDto;
import com.abavilla.fpi.login.dto.SessionDto;
import com.abavilla.fpi.login.entity.Session;
import com.abavilla.fpi.login.service.LoginSvc;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpServerRequest;
import org.apache.commons.lang3.BooleanUtils;
import org.jboss.resteasy.reactive.NoCache;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

/**
 * Resource for Login to FPI System
 * URI Path: {@code "/fpi/login"}
 *
 * @author <a href="mailto:vincevillamora@gmail.com">Vince Villamora</a>
 */
@Path("/fpi/login")
public class LoginResource extends AbsBaseResource<LoginDto, Session, LoginSvc> {

  /**
   * Context for accessing the information about the HTTP Request, like IP Address, User-Agent and other headers.
   */
  @Context
  HttpServerRequest request;

  /**
   * Endpoint to establish a new login session
   * @param loginDto Credentials used for authentication
   * @param refreshToken Flag to check if login will use a refresh token to refresh an expired access token
   *
   * @return {@link SessionDto} Session information
   */
  @POST
  @NoCache
  public Uni<SessionDto> login(PasswordLoginDto loginDto,
                               @QueryParam("refreshToken")Boolean refreshToken){
    loginDto.setUserAgent(HttpUtil.getUserAgent(request));
    loginDto.setRemoteAddress(HttpUtil.getClientIpAddr(request));
    if (refreshToken == null || BooleanUtils.isFalse(refreshToken))
      return service.login(loginDto);
    else
      return service.refreshToken(loginDto);
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