/*
 * Copyright © 2017 <code@io7m.com> http://io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jpra.model.names;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Functions over type names.
 */

public final class TypeNames
{
  /**
   * The pattern that defines a valid type name.
   */

  public static final Pattern PATTERN;

  private static final String PATTERN_TEXT;

  static {
    PATTERN_TEXT = "[\\p{IsUppercase}][\\p{IsAlphabetic}\\p{IsDigit}_]*";
    PATTERN = Objects.requireNonNull(Pattern.compile(
      PATTERN_TEXT, Pattern.UNICODE_CHARACTER_CLASS), "Pattern");
  }

  private TypeNames()
  {

  }

  /**
   * @param name The raw name
   *
   * @return {@code true} iff the given name is a valid type name
   */

  public static boolean isValid(
    final String name)
  {
    return PATTERN.matcher(Objects.requireNonNull(name, "Name")).matches();
  }
}
