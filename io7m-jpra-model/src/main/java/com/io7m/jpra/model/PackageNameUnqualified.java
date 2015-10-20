/*
 * Copyright © 2015 <code@io7m.com> http://io7m.com
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

package com.io7m.jpra.model;

import com.io7m.jnull.NullCheck;
import net.jcip.annotations.Immutable;
import org.valid4j.Assertive;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An unqualified package name.
 */

@Immutable public final class PackageNameUnqualified implements ModelElementType
{
  private static final Pattern PATTERN;
  private static final String  PATTERN_TEXT;

  static {
    PATTERN_TEXT = "[[\\p{Alnum}&&[^\\p{Upper}]][_]]+";
    PATTERN = NullCheck.notNull(
      Pattern.compile(
        PackageNameUnqualified.PATTERN_TEXT, Pattern.UNICODE_CHARACTER_CLASS));
  }

  private final String value;

  /**
   * Construct a package name.
   *
   * @param in_value The raw package name
   */

  public PackageNameUnqualified(final String in_value)
  {
    this.value = NullCheck.notNull(in_value);

    final Matcher matcher = PackageNameUnqualified.PATTERN.matcher(this.value);
    Assertive.require(
      matcher.matches(),
      "Package names must match the pattern '%s'",
      PackageNameUnqualified.PATTERN_TEXT);
  }

  @Override public String toString()
  {
    return this.value;
  }

  /**
   * @return The raw package name
   */

  public String getValue()
  {
    return this.value;
  }

  @Override public boolean equals(final Object o)
  {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }

    final PackageNameUnqualified that = (PackageNameUnqualified) o;
    return this.value.equals(that.value);
  }

  @Override public int hashCode()
  {
    return this.value.hashCode();
  }
}
