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

package com.abavilla.fpi.login.mapper;

import com.abavilla.fpi.bot.ext.entity.enums.BotSource;
import com.abavilla.fpi.fw.mapper.IDtoToEntityMapper;
import com.abavilla.fpi.login.entity.User;
import com.abavilla.fpi.login.ext.dto.UserDto;
import com.abavilla.fpi.login.ext.dto.WebhookLoginDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Condition;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper used for converting between {@link UserDto} and {@link User} entity
 *
 * @author <a href="mailto:vincevillamora@gmail.com">Vince Villamora</a>
 */
@Mapper(componentModel = MappingConstants.ComponentModel.CDI,
  injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper extends IDtoToEntityMapper<UserDto, User> {

  @BeanMapping(ignoreByDefault = true,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "metaId", source = "username",  conditionQualifiedByName = "isMetaId")
  @Mapping(target = "telegramId", source = "username",  conditionQualifiedByName = "isTelegramId")
  void mapLoginToUser(@MappingTarget User user, WebhookLoginDto login);

  @Named("isMetaId")
  @Condition
  default boolean isMetaId(WebhookLoginDto login) {
    return BotSource.fromValue(login.getBotSource()) == BotSource.FB_MSGR;
  }

  @Named("isTelegramId")
  @Condition
  default boolean isTelegramId(WebhookLoginDto login) {
    return BotSource.fromValue(login.getBotSource()) == BotSource.TELEGRAM;
  }

}
