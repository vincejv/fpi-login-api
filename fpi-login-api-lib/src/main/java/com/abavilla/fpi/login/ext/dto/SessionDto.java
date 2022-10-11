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

import java.util.EnumSet;
import java.util.HashMap;
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
import org.apache.commons.lang3.StringUtils;

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
    UNKNOWN(-1, UNKNOWN_PREFIX);

    /**
     * Ordinal id to enum mapping
     */
    private static final Map<Integer, SessionStatus> ENUM_MAP = new HashMap<>();

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
    private String value;

    /**
     * Creates an enum based from given string value
     *
     * @param value the string value
     * @return the created enum
     */
    @JsonCreator
    public static SessionStatus fromValue(String value) {
      if (StringUtils.isBlank(value)) {
        return null;
      } else {
        return ENUM_MAP.values().stream().filter(enumItem ->
                StringUtils.equalsIgnoreCase(value, enumItem.getValue())).findAny()
            .orElseGet(() -> {
              var unknown = UNKNOWN;
              String enumValue = value;
              if (StringUtils.startsWithIgnoreCase(enumValue, UNKNOWN_PREFIX)) {
                enumValue = StringUtils.removeStart(enumValue, UNKNOWN_PREFIX);
              }
              unknown.value = UNKNOWN_PREFIX + enumValue;
              return unknown;
            });
      }
    }

    /**
     * Creates an enum based from given an ordinal id
     *
     * @param id the ordinal id
     * @return the created enum
     */
    public static SessionStatus fromId(int id) {
      return ENUM_MAP.values().stream().filter(enumItem ->
              id == enumItem.getId()).findAny()
          .orElseGet(() -> {
            var unknown = UNKNOWN;
            unknown.value = UNKNOWN_PREFIX + id;
            return unknown;
          });
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
   * Token to be used for authenticating against protected resource
   */
  private String accessToken;

  /**
   * Current status of the session
   */
  @JsonIgnore
  private SessionStatus status;

  /**
   * Date and time when token will expire in UTC time
   */
  private String tokenExpiry;
}