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

package com.abavilla.fpi.login.entity;

import java.time.LocalDateTime;

import com.abavilla.fpi.fw.entity.mongo.AbsMongoItem;
import com.abavilla.fpi.login.ext.entity.ServiceStatus;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

/**
 * Entity containing the information about the authorized users who are able to access the system
 *
 * @author <a href="mailto:vincevillamora@gmail.com">Vince Villamora</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@BsonDiscriminator
@MongoEntity(collection="system_users")
public class User extends AbsMongoItem {

  /**
   * User id in Meta
   */
  private String metaId;

  /**
   * User id in Telegram
   */
  private String telegramId;

  /**
   * User id in Viber
   */
  private String viberId;

  /**
   * Mobile number
   */
  private String mobile;

  /**
   * Registration status
   */
  private UserStatus status;

  /**
   * Date and time when user first interacted or registered with system
   */
  private LocalDateTime registrationDate;

  /**
   * Date and time when user was verified
   */
  private LocalDateTime verifiedDate;

  /**
   * Date and time when user last interacted with system
   */
  private LocalDateTime lastAccess;

  /**
   * Name associated with the user
   */
  private PersonName name;

  /**
   * Fields indicating if the user has opted in or opted out of the service
   */
  private ServiceStatus svcStatus;

}
