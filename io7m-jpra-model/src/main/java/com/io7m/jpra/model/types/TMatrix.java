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

package com.io7m.jpra.model.types;

import com.io7m.jlexing.core.ImmutableLexicalPositionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.Size;
import net.jcip.annotations.Immutable;

import java.nio.file.Path;
import java.util.Optional;

/**
 * A {@code matrix} type expression.
 */

@Immutable public final class TMatrix implements TType
{
  private final TypeScalarType                               type;
  private final Size<?>                                      size_width;
  private final Size<?>                                      size_height;
  private final Optional<ImmutableLexicalPositionType<Path>> lex;

  /**
   * Construct an {@code integer unsigned} type expression.
   *
   * @param in_lex         Lexical information
   * @param in_size_width  An expression denoting the number of columns in the
   *                       matrix
   * @param in_size_height An expression denoting the number of rows in the
   *                       matrix
   * @param in_type        The type of the matrix elements
   */

  public TMatrix(
    final Optional<ImmutableLexicalPositionType<Path>> in_lex,
    final Size<?> in_size_width,
    final Size<?> in_size_height,
    final TypeScalarType in_type)
  {
    this.lex = NullCheck.notNull(in_lex);
    this.size_width = NullCheck.notNull(in_size_width);
    this.size_height = NullCheck.notNull(in_size_height);
    this.type = NullCheck.notNull(in_type);
  }

  /**
   * @return The number of rows in the matrix
   */

  public Size<?> getHeightExpression()
  {
    return this.size_height;
  }

  /**
   * @return The number of columns in the matrix
   */

  public Size<?> getWidthExpression()
  {
    return this.size_width;
  }

  /**
   * @return The type of matrix elements
   */

  public TypeScalarType getElementType()
  {
    return this.type;
  }

  @Override public <A, E extends Exception> A matchType(
    final TypeMatcherType<A, E> m)
    throws E
  {
    return m.matchMatrix(this);
  }

  @Override
  public Optional<ImmutableLexicalPositionType<Path>> getLexicalInformation()
  {
    return this.lex;
  }
}
