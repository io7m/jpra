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
import com.io7m.jpra.model.ModelElementType;
import net.jcip.annotations.Immutable;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The name of a union case.
 */

@Immutable
public final class UnionCaseName implements ModelElementType
{
  private static final Pattern PATTERN;
  private static final String PATTERN_TEXT;

  static {
    PATTERN_TEXT = "[\\p{IsUppercase}][\\p{IsAlphabetic}\\p{IsDigit}_]*";
    PATTERN = Objects.requireNonNull(Pattern.compile(
      PATTERN_TEXT, Pattern.UNICODE_CHARACTER_CLASS), "Pattern");
  }

  private final String value;
  private final Optional<LexicalPosition<Path>> lex;

  /**
   * Construct a case name.
   *
   * @param in_lex   The lexical information for the name
   * @param in_value The raw string value
   */

  public UnionCaseName(
    final Optional<LexicalPosition<Path>> in_lex,
    final String in_value)
  {
    this.lex = Objects.requireNonNull(in_lex, "Lexical information");
    this.value = Objects.requireNonNull(in_value, "Value");

    final Matcher matcher = PATTERN.matcher(this.value);
    Preconditions.checkPreconditionV(
      matcher.matches(),
      "Type names must match the pattern '%s'",
      PATTERN_TEXT);
  }

  /**
   * @return The raw string value
   */

  public String getValue()
  {
    return this.value;
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

    final UnionCaseName other = (UnionCaseName) o;
    return Objects.equals(this.value, other.value);
  }

  @Override
  public int hashCode()
  {
    return this.value.hashCode();
  }

  @Override
  public Optional<LexicalPosition<Path>> lexical()
  {
    return this.lex;
  }
}
