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

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

/**
 * BSON Codec for decoding and encoding {@code char[]} arrays.
 * Used by MongoDB driver.
 *
 * @author <a href="mailto:vincevillamora@gmail.com">Vince Villamora</a>
 */
public class CharArrayCodec implements Codec<char[]> {

  /**
   * {@inheritDoc}
   */
  @Override
  public void encode(BsonWriter writer, char[] value, EncoderContext encoderContext) {
    writer.writeString(String.valueOf(value));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public char[] decode(BsonReader reader, DecoderContext decoderContext) {
    return reader.readString().toCharArray();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<char[]> getEncoderClass() {
    return char[].class;
  }
}
