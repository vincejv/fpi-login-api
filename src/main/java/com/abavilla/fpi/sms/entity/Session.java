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

package com.abavilla.fpi.sms.entity;

import java.time.LocalDateTime;

import com.abavilla.fpi.fw.entity.mongo.AbsMongoItem;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

/**
 * Entity containing the information regarding the login session.
 *
 * @author <a href="mailto:vincevillamora@gmail.com">Vince Villamora</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@BsonDiscriminator
@MongoEntity(collection="login_sessions")
public class Session extends AbsMongoItem {

  /**
   * Username used by session
   */
  private String username;

  /**
   * Hashed password to verify the credentials
   */
  private char[] password;

  /**
   * Token used to refresh an expired access token
   */
  private String refreshToken;

  /**
   * Token used to authenticate to protected resource
   */
  private String accessToken;

  /**
   * Date and time when {@link #refreshToken} token will expire based from Keycloak server
   */
  private LocalDateTime refreshTokenExpiry;

  /**
   * IP Address of client who established the session
   */
  private String ipAddress;

  /**
   * Browser User-agent of client who established the session
   */
  private String userAgent;
}
