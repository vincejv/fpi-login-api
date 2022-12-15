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

package com.abavilla.fpi.login.ext.dto;

import static com.abavilla.fpi.fw.util.FWConst.UNKNOWN_PREFIX;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.abavilla.fpi.fw.dto.AbsDto;
import com.abavilla.fpi.fw.entity.enums.IBaseEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Data transfer object containing the information regarding the login session.
 *
 * @author <a href="mailto:vincevillamora@gmail.com">Vince Villamora</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@RegisterForReflection
public class SessionDto extends AbsDto {

  @Getter
  @AllArgsConstructor
  public enum SessionStatus implements IBaseEnum {
    CREATED_USER(1, "Created user"),

    ESTABLISHED(2, "Session established"),

    PENDING_VERIFICATION(3, "Pending user verification"),

    BLOCKED_UNS(4, "Account blocked for unspecified reason"),

    BLOCKED_LEGAL(5, "Account has been blocked due to legal concerns"),

    BLOCKED_SUSPECT(6, "Account has been blocked due to suspicious activity"),

    DEACTIVATED(7, "Account has been deactivated as per user request"),

    INACTIVE(8, "Account has been inactive for a while thus it is deactivated"),

    PRIV_CLEAN(9, "Account has been requested to be removed from the system for privacy concerns"),

    UNKNOWN(-1, UNKNOWN_PREFIX);

    /**
     * Ordinal id to enum mapping
     */
    private static final Map<Integer, IBaseEnum> ENUM_MAP = new HashMap<>();

    static {
      for(SessionStatus w : EnumSet.allOf(SessionStatus.class))
        ENUM_MAP.put(w.getId(), w);
    }

    /**
     * The enum ordinal id
     */
    private final int id;

    /**
     * The enum value
     */
    private final String value;

    /**
     * Creates an enum based from given string value
     *
     * @param value the string value
     * @return the created enum
     */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static SessionStatus fromValue(String value) {
      return (SessionStatus) IBaseEnum.fromValue(value, ENUM_MAP, UNKNOWN);
    }

    /**
     * Creates an enum based from given an ordinal id
     *
     * @param id the ordinal id
     * @return the created enum
     */
    public static SessionStatus fromId(int id) {
      return (SessionStatus) IBaseEnum.fromId(id, ENUM_MAP, UNKNOWN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonValue
    public String toString() {
      return value;
    }
  }

  /**
   * Username used by session
   */
  private String username;

  /**
   * Token to be used for authenticating against protected resource
   */
  private String accessToken;

  /**
   * Current status of the session
   */
  private SessionStatus status;

  /**
   * Current status of the session to be displayed to the user
   */
  @JsonIgnore /* Ignored as the parent should hold the message */
  private String message;

  /**
   * Date and time when token will expire in UTC time
   */
  private String tokenExpiry;

  /**
   * Roles associated with the user provisioned in keycloak server
   */
  private List<String> roles;

}
