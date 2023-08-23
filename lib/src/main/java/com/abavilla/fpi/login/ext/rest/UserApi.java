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

import java.time.temporal.ChronoUnit;

import com.abavilla.fpi.fw.dto.impl.RespDto;
import com.abavilla.fpi.fw.exceptions.AuthApiSvcEx;
import com.abavilla.fpi.fw.exceptions.handler.ApiRepoExHandler;
import com.abavilla.fpi.fw.rest.IApi;
import com.abavilla.fpi.login.ext.dto.UserDto;
import io.smallrye.faulttolerance.api.ExponentialBackoff;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "user-api")
@RegisterClientHeaders(AppToAppPreAuth.class)
@RegisterProvider(value = ApiRepoExHandler.class)
@Retry(maxRetries = 8, retryOn = AuthApiSvcEx.class, delay = 3,
  delayUnit = ChronoUnit.SECONDS, jitter = 1500L)
@ExponentialBackoff(maxDelay = 25, maxDelayUnit = ChronoUnit.SECONDS)
public interface UserApi extends IApi {

  /**
   * Retrieves {@link UserDto} given the user's {@code metaId}.
   *
   * @param metaId the meta id
   * @return {@link UserDto}
   */
  @GET
  Uni<RespDto<UserDto>> getByMetaId(@QueryParam("metaId") String metaId);

  /**
   * Retrieves {@link UserDto} given the user's {@code mobileNo}.
   *
   * @param mobile the mobile number
   * @return {@link UserDto}
   */
  @GET
  Uni<RespDto<UserDto>> getByMobile(@QueryParam("mobile") String mobile);

  /**
   * Retrieves {@link UserDto} given the user's {@code id}.
   *
   * @param userId the user id
   * @return {@link UserDto}
   */
  @GET
  Uni<RespDto<UserDto>> getById(@QueryParam("id") String userId);

  /**
   * Patches {@link UserDto} given the user's {@code id}.
   *
   * @param userId the user id
   * @return {@link UserDto}
   */
  @Path("{id}")
  @PATCH
  Uni<RespDto<UserDto>> patchById(@PathParam("id") String userId, UserDto patch);

}
