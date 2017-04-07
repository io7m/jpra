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

package com.io7m.jpra.model.names;

import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.ModelElementType;
import net.jcip.annotations.Immutable;

import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type of field names.
 */

@Immutable
public final class FieldName implements ModelElementType
{
  /**
   * The pattern that defines a valid field name.
   */

  public static final Pattern PATTERN;

  private static final String PATTERN_TEXT;

  static {
    PATTERN_TEXT = "[\\p{IsLowercase}][\\p{IsLowercase}\\p{IsDigit}_]*";
    PATTERN = NullCheck.notNull(
      Pattern.compile(
        FieldName.PATTERN_TEXT, Pattern.UNICODE_CHARACTER_CLASS), "Pattern");
  }

  private final String value;
  private final Optional<LexicalPosition<Path>> lex;

  /**
   * Construct a field name.
   *
   * @param in_lex   The lexical information for the name
   * @param in_value The raw string value
   */

  public FieldName(
    final Optional<LexicalPosition<Path>> in_lex,
    final String in_value)
  {
    this.lex = NullCheck.notNull(in_lex, "Lexical information");
    this.value = NullCheck.notNull(in_value, "Value");

    final Matcher matcher = FieldName.PATTERN.matcher(this.value);
    Preconditions.checkPrecondition(
      in_value,
      matcher.matches(),
      s -> String.format(
        "Field names must match the pattern '%s'",
        PATTERN_TEXT));
  }

  @Override
  public boolean equals(final Object o)
  {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }

    final FieldName other = (FieldName) o;
    return this.value.equals(other.value);
  }

  /**
   * @return The raw string value
   */

  public String getValue()
  {
    return this.value;
  }

  @Override
  public String toString()
  {
    return this.value;
  }

  @Override
  public int hashCode()
  {
    return this.value.hashCode();
  }

  @Override
  public Optional<LexicalPosition<Path>> getLexicalInformation()
  {
    return this.lex;
  }
}
