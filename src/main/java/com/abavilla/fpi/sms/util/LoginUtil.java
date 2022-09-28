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

package com.abavilla.fpi.sms.util;

import java.security.SecureRandom;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.crypto.bcrypt.LongPasswordStrategies;

/**
 * Utility methods used by the Login API service
 *
 * @author <a href="mailto:vincevillamora@gmail.com">Vince Villamora</a>
 */
@Singleton
public class LoginUtil {

  /**
   * BCrypt hasher
   */
  private static BCrypt.Hasher bcrypt;

  /**
   * BCrypt verifier
   */
  private static BCrypt.Verifyer verifier;

  /**
   * Exponential cost (log2 factor) between {@link BCrypt#MIN_COST}
   * and {@link BCrypt#MAX_COST} e.g. 12 --&gt; 2^12 = 4,096 iterations
   */
  private final static int BCRYPT_HASH_COST = BCrypt.MIN_COST;

  /**
   * Initialize utility library.
   */
  @PostConstruct
  public void init() {
    var bcryptVersion = BCrypt.Version.VERSION_2Y;
    var strategy = LongPasswordStrategies.strict(bcryptVersion);
    bcrypt = BCrypt.with(bcryptVersion, new SecureRandom(), strategy);
    verifier = BCrypt.verifyer(bcryptVersion, strategy);
  }

  /**
   * Hashes password with BCrypt
   *
   * @param password Unhashed password
   * @return Hash password
   */
  public static char[] hashPassword(char[] password) {
    return bcrypt.hashToChar(BCRYPT_HASH_COST, password);
  }

  /**
   * Checks if hash matches with password.
   *
   * @param password Password
   * @param hash Hash
   * @return {@code true} if a match otherwise {@code false}
   */
  public static boolean verifyHash(char[] password, char[] hash) {
    return verifier.verifyStrict(password, hash).verified;
  }
}
