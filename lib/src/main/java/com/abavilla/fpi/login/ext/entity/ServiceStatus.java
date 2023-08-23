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

package com.abavilla.fpi.login.ext.entity;

import static com.abavilla.fpi.fw.util.FWConst.UNKNOWN_PREFIX;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.abavilla.fpi.fw.entity.enums.IBaseEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ServiceStatus implements IBaseEnum {
  UNKNOWN(-1, UNKNOWN_PREFIX),
  OPT_IN(1, "Opt-in"),
  OPT_OUT(2, "Opt-out")
  ;

  /**
   * Ordinal id to enum mapping
   */
  private static final Map<Integer, IBaseEnum> ENUM_MAP = new HashMap<>();

  static {
    for(ServiceStatus w : EnumSet.allOf(ServiceStatus.class))
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
  public static ServiceStatus fromValue(String value) {
    return (ServiceStatus) IBaseEnum.fromValue(value, ENUM_MAP, UNKNOWN);
  }

  /**
   * Creates an enum based from given an ordinal id
   *
   * @param id the ordinal id
   * @return the created enum
   */
  public static ServiceStatus fromId(int id) {
    return (ServiceStatus) IBaseEnum.fromId(id, ENUM_MAP, UNKNOWN);
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
