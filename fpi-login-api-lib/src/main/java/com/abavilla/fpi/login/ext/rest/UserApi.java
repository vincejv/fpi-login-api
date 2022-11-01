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

package com.abavilla.fpi.login.ext.rest;

import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;

import com.abavilla.fpi.fw.dto.impl.RespDto;
import com.abavilla.fpi.fw.exceptions.handler.ApiRepoExHandler;
import com.abavilla.fpi.fw.rest.IApi;
import com.abavilla.fpi.login.ext.dto.UserDto;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "user-api")
@RegisterClientHeaders(AppToAppPreAuth.class)
@RegisterProvider(value = ApiRepoExHandler.class)
public interface UserApi extends IApi {

  /**
   * Retrieves {@link UserDto} given the user's {@code metaId}.
   *
   * @param metaId the meta id
   * @return {@link UserDto}
   */
  @GET
  Uni<RespDto<UserDto>> getByMetaId(@QueryParam("metaId") String metaId);

}
