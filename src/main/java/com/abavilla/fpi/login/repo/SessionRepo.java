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

import com.abavilla.fpi.fw.repo.IMongoRepo;
import com.abavilla.fpi.login.entity.Session;
import io.smallrye.mutiny.Uni;

/**
 * Repository layer for managing the session table in DB.
 *
 * @author <a href="mailto:vincevillamora@gmail.com">Vince Villamora</a>
 */
@ApplicationScoped
public class SessionRepo implements IMongoRepo<Session> {

  /**
   * Finds an existing active session by username
   * @param username Username to find
   *
   * @return Session found
   */
  public Uni<Optional<Session>> findByUsername(String username){
    return find("username", username).firstResultOptional();
  }
}
