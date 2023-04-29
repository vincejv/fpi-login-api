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
import com.abavilla.fpi.login.ext.dto.SessionDto;
import com.abavilla.fpi.login.ext.dto.WebhookLoginDto;
import io.smallrye.faulttolerance.api.ExponentialBackoff;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "login-api")
@RegisterClientHeaders(AppToAppPreAuth.class)
@RegisterProvider(value = ApiRepoExHandler.class)
@Retry(maxRetries = 8, retryOn = AuthApiSvcEx.class, delay = 3,
  delayUnit = ChronoUnit.SECONDS, jitter = 1500L)
@ExponentialBackoff(maxDelay = 25, maxDelayUnit = ChronoUnit.SECONDS)
public interface TrustedLoginApi extends IApi {

  /**
   * Perform a trusted login through {@code /fpi/login/trusted}
   * @param login Login credentials
   * @return {@link SessionDto} object
   */
  @POST
  @Path("trusted")
  Uni<RespDto<SessionDto>> webhookAuthenticate(WebhookLoginDto login);
}