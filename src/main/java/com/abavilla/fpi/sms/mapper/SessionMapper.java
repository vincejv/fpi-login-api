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

package com.abavilla.fpi.sms.mapper;

import java.time.LocalDateTime;

import com.abavilla.fpi.fw.mapper.IDtoToEntityMapper;
import com.abavilla.fpi.fw.util.DateUtil;
import com.abavilla.fpi.sms.dto.SessionDto;
import com.abavilla.fpi.sms.entity.Session;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

/**
 * Mapper used for converting between {@link SessionDto} and {@link Session} entity
 *
 * @author <a href="mailto:vincevillamora@gmail.com">Vince Villamora</a>
 */
@Mapper(componentModel = MappingConstants.ComponentModel.CDI,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface SessionMapper extends IDtoToEntityMapper<SessionDto, Session> {

  @Mappings(value = {
      @Mapping(target = "dateCreated", ignore = true),
      @Mapping(target = "dateUpdated", ignore = true),
      @Mapping(target = "id", ignore = true),
      @Mapping(target = "tokenExpiry", source = "refreshTokenExpiry", qualifiedByName = "ldtToUtcStr")
  })
  SessionDto mapToDto(Session entity);

  @Mappings(value = {
      @Mapping(target = "dateCreated", ignore = true),
      @Mapping(target = "dateUpdated", ignore = true),
      @Mapping(target = "id", ignore = true)
  })
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
