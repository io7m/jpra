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

import com.io7m.jlexing.core.ImmutableLexicalPositionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.ModelElementType;
import net.jcip.annotations.Immutable;
import org.valid4j.Assertive;

import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A type name.
 */

@Immutable public final class TypeName implements ModelElementType
{
  /**
   * The pattern that defines a valid type name.
   */

  public static final Pattern PATTERN;

  private static final String PATTERN_TEXT;

  static {
    PATTERN_TEXT = "[\\p{IsUppercase}][\\p{IsAlphabetic}\\p{IsDigit}_]*";
    PATTERN = NullCheck.notNull(
      Pattern.compile(
        TypeName.PATTERN_TEXT, Pattern.UNICODE_CHARACTER_CLASS));
  }

  private final String                                       value;
  private final Optional<ImmutableLexicalPositionType<Path>> lex;

  /**
   * Construct a type name.
   *
   * @param in_lex   The lexical information for the name
   * @param in_value The raw string value
   */

  public TypeName(
    final Optional<ImmutableLexicalPositionType<Path>> in_lex,
    final String in_value)
  {
    this.lex = NullCheck.notNull(in_lex);
    this.value = NullCheck.notNull(in_value);

    final Matcher matcher = TypeName.PATTERN.matcher(this.value);
    Assertive.require(
      matcher.matches(),
      "Type names must match the pattern '%s'",
      TypeName.PATTERN_TEXT);
  }

  /**
   * @return The raw string value
   */

  public String getValue()
  {
    return this.value;
  }

  @Override public String toString()
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

    final TypeName other = (TypeName) o;
    return this.value.equals(other.value);
  }

  @Override public int hashCode()
  {
    return this.value.hashCode();
  }

  @Override
  public Optional<ImmutableLexicalPositionType<Path>> getLexicalInformation()
  {
    return this.lex;
  }
}
