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

package com.abavilla.fpi.login.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.abavilla.fpi.fw.exceptions.FPISvcEx;
import com.abavilla.fpi.fw.service.AbsRepoSvc;
import com.abavilla.fpi.login.entity.User;
import com.abavilla.fpi.login.ext.dto.UserDto;
import com.abavilla.fpi.login.mapper.UserMapper;
import com.abavilla.fpi.login.repo.UserRepo;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.RestResponse;

/**
 * Service layer for creating and managing authorized system users.
 *
 * @author <a href="mailto:vincevillamora@gmail.com">Vince Villamora</a>
 */
@ApplicationScoped
public class UserSvc extends AbsRepoSvc<UserDto, User, UserRepo> {

  @Inject
  UserMapper userMapper;

  /**
   * {@inheritDoc}
   */
  @Override
  public UserDto mapToDto(User entity) {
    return userMapper.mapToDto(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public User mapToEntity(UserDto dto) {
    return userMapper.mapToEntity(dto);
  }

  /**
   * Retrieves {@link User} given the user's {@code metaId}.
   *
   * @param metaId the meta id
   * @return {@link UserDto}
   */
  public Uni<UserDto> getByMetaId(String metaId) {
    return repo.findByMetaId(metaId).chain(user -> {
      if (user.isPresent()) {
        return Uni.createFrom().item(this.mapToDto(user.get()));
      }
      throw new FPISvcEx(String.format("User with metaId %s was not found",
        RestResponse.StatusCode.NOT_FOUND));
    });
  }
}
