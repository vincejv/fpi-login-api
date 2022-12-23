/*
 * *****************************************************************************
 *  * FPI Application - Abavilla                                                 *
 *  * Copyright (C) 2022  Vince Jerald Villamora                                 *
 *  *                                                                            *
 *  * This program is free software: you can redistribute it and/or modify       *
 *  * it under the terms of the GNU General Public License as published by       *
 *  * the Free Software Foundation, either version 3 of the License, or          *
 *  * (at your option) any later version.                                        *
 *  *                                                                            *
 *  * This program is distributed in the hope that it will be useful,            *
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 *  * GNU General Public License for more details.                               *
 *  *                                                                            *
 *  * You should have received a copy of the GNU General Public License          *
 *  * along with this program.  If not, see <https://www.gnu.org/licenses/>.     *
 *  *****************************************************************************
 */

package com.abavilla.fpi.login.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import com.abavilla.fpi.fw.controller.AbsBaseResource;
import com.abavilla.fpi.fw.dto.IDto;
import com.abavilla.fpi.fw.dto.impl.RespDto;
import com.abavilla.fpi.fw.exceptions.FPISvcEx;
import com.abavilla.fpi.fw.util.DateUtil;
import com.abavilla.fpi.login.entity.User;
import com.abavilla.fpi.login.ext.dto.UserDto;
import com.abavilla.fpi.login.service.UserSvc;
import io.smallrye.mutiny.Uni;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

/**
 * Resource for managing authorized system users in FPI system.
 * URI Path: {@code "/fpi/user"}
 *
 * @author <a href="mailto:vincevillamora@gmail.com">Vince Villamora</a>
 */
@Path("/fpi/user")
public class UserResource extends AbsBaseResource<UserDto, User, UserSvc> {

  /**
   * Retrieves {@link UserDto} given the user's {@code metaId} or {@code userId}, if multiple id's are given,
   * prioritizes the specific platform id
   *
   * @param metaId the meta id
   * @param userId the user id
   * @return {@link UserDto}
   */
  @GET
  public Uni<RespDto<UserDto>> getById(@QueryParam("metaId") String metaId,
                                       @QueryParam("id") String userId,
                                       @QueryParam("mobile") String mobile) {
    Uni<UserDto> userLookup;
    if (StringUtils.isNotBlank(metaId)) {
      userLookup = service.getByMetaId(metaId);
    } else if (StringUtils.isNotBlank(userId)) {
      userLookup = service.get(userId);
    } else {
      userLookup = service.getByMobile(mobile);
    }
    return userLookup.map(user -> {
      RespDto<UserDto> resp = new RespDto<>();
      resp.setTimestamp(DateUtil.nowAsStr());
      resp.setResp(user);
      resp.setStatus(RestResponse.Status.FOUND.getReasonPhrase());
      return resp;
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
