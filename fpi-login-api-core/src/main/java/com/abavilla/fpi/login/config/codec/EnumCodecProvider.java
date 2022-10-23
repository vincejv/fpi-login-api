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

package com.abavilla.fpi.login.config.codec;

import com.abavilla.fpi.fw.config.codec.IEnumCodecProvider;
import com.abavilla.fpi.login.entity.UserStatus;
import org.bson.codecs.Codec;

/**
 * MongoDB Codec registry, contains all the codec for classes that doesn't
 * work by default with default POJO codec for MongoDb driver.
 *
 * @author <a href="mailto:vincevillamora@gmail.com">Vince Villamora</a>
 */
public class EnumCodecProvider implements IEnumCodecProvider {
  /**
   * Provides the codec mapping definition
   *
   * @param clazz Class to decode/encode
   * @return {@link Codec} to use
   * @param <T> Type of {@link Codec}
   */
  @Override
  public <T> Codec<T> getCodecProvider(Class<T> clazz) {
    if (clazz == char[].class) {
      return (Codec<T>) new CharArrayCodec();
    } else if (clazz == UserStatus.class) {
      return (Codec<T>) new UserStatusCodec();
    }
    return null; // Don't throw here, this tells Mongo this provider doesn't provide a decoder for the requested clazz
  }
}
