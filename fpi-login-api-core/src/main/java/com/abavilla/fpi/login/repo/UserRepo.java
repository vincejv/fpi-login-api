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

package com.abavilla.fpi.login.repo;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import com.abavilla.fpi.fw.repo.AbsMongoRepo;
import com.abavilla.fpi.login.entity.User;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class UserRepo extends AbsMongoRepo<User> {

  public Uni<Optional<User>> findByMetaId(String metaId) {
    return find("{'metaId': ?1}", metaId).singleResultOptional();
  }

  public Uni<Optional<User>> findByTelegramId(String tgId) {
    return find("{'telegramId': ?1}", tgId).singleResultOptional();
  }

  public Uni<Optional<User>> findByViberId(String viberId) {
    return find("{'viberId': ?1}", viberId).singleResultOptional();
  }

}
