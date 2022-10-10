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

package com.abavilla.fpi.login.rest.ext;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import com.abavilla.fpi.login.dto.PasswordLoginDto;
import io.quarkus.rest.client.reactive.ReactiveClientHeadersFactory;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * HTTP Header configuration for {@link TrustedLoginApi} resource.
 *
 * @author <a href="mailto:vincevillamora@gmail.com">Vince Villamora</a>
 */
@ApplicationScoped
public class TrustedLoginPreAuth extends ReactiveClientHeadersFactory {

  /**
   * API Key for SMS service access
   */
  @ConfigProperty(name = "fpi.app-to-app.auth.username")
  String apiKey;

  /**
   * Secret Key for SMS service access
   */
  @ConfigProperty(name = "fpi.app-to-app.auth.password")
  String secretKey;

  /**
   * Login resource
   */
  @RestClient
  LoginApi loginRepo;

  /**
   * {@inheritDoc}
   */
  @Override
  public Uni<MultivaluedMap<String, String>> getHeaders(MultivaluedMap<String, String> incomingHeaders, MultivaluedMap<String, String> clientOutgoingHeaders) {
    return authenticate().chain(token ->{
      MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
      headers.add("Authorization", token);
      return Uni.createFrom().item(headers);
    });
  }

  /**
   * Get a token for authentication.
   *
   * @return Session token
   */
  public Uni<String> authenticate() {
    var creds = new PasswordLoginDto();
    creds.setUsername(apiKey);
    creds.setPassword(secretKey);

    return loginRepo.authenticate(creds)
        .chain(sessionDto ->
            Uni.createFrom().item("Bearer " +
                sessionDto.getAccessToken()));
  }
}
