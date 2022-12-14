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

package com.abavilla.fpi.login.mapper;

import java.time.LocalDateTime;

import com.abavilla.fpi.fw.mapper.IDtoToEntityMapper;
import com.abavilla.fpi.fw.util.DateUtil;
import com.abavilla.fpi.login.entity.Session;
import com.abavilla.fpi.login.ext.dto.SessionDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

/**
 * Mapper used for converting between {@link SessionDto} and {@link Session} entity
 *
 * @author <a href="mailto:vincevillamora@gmail.com">Vince Villamora</a>
 */
@Mapper(componentModel = MappingConstants.ComponentModel.CDI,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface SessionMapper extends IDtoToEntityMapper<SessionDto, Session> {

  @Mapping(target = "dateCreated", ignore = true)
  @Mapping(target = "dateUpdated", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "tokenExpiry", source = "refreshTokenExpiry", qualifiedByName = "ldtToUtcStr")
  @Mapping(target = "roles", source = "keycloakRoles")
  SessionDto mapToDto(Session entity);

  @Mapping(target = "dateCreated", ignore = true)
  @Mapping(target = "dateUpdated", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "keycloakRoles", ignore = true)
  Session mapToEntity(SessionDto dto);

  /**
   * Converts {@link java.time.LocalDateTime} to string using the format specified in
   * {@link com.abavilla.fpi.fw.util.DateUtil#DEFAULT_TIMESTAMP_FORMAT_WITH_TIMEZONE}
   */
  @Named("ldtToUtcStr")
  default String ldtToUtcStr(LocalDateTime ldt) {
    return DateUtil.convertLdtToStr(ldt);
  }
}
