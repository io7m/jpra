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

import com.io7m.jlexing.core.LexicalPosition;

import java.math.BigInteger;
import java.net.URI;
import java.util.Objects;

/**
 * A {@code matrix} type expression.
 */

public final class TMatrix implements TType
{
  private final TypeScalarType type;
  private final Size<?> size_width;
  private final Size<?> size_height;
  private final LexicalPosition<URI> lex;
  private final Size<SizeUnitBitsType> size_bits;

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
    final LexicalPosition<URI> in_lex,
    final Size<?> in_size_width,
    final Size<?> in_size_height,
    final TypeScalarType in_type)
  {
    this.lex = Objects.requireNonNull(in_lex, "Lexical information");
    this.size_width = Objects.requireNonNull(in_size_width, "Size width");
    this.size_height = Objects.requireNonNull(in_size_height, "Size height");
    this.type = Objects.requireNonNull(in_type, "Type");

    final BigInteger sw = this.size_width.getValue();
    final BigInteger sh = this.size_height.getValue();
    final BigInteger ts = this.type.getSizeInBits().getValue();
    this.size_bits = new Size<>(sw.multiply(sh).multiply(ts));
  }

  /**
   * @return The number of rows in the matrix
   */

  public Size<?> getHeight()
  {
    return this.size_height;
  }

  /**
   * @return The number of columns in the matrix
   */

  public Size<?> getWidth()
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

  @Override
  public Size<SizeUnitBitsType> getSizeInBits()
  {
    return this.size_bits;
  }

  @Override
  public <A, E extends Exception> A matchType(
    final TypeMatcherType<A, E> m)
    throws E
  {
    return m.matchMatrix(this);
  }

  @Override
  public LexicalPosition<URI> lexical()
  {
    return this.lex;
  }

  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder("[matrix ");
    sb.append(this.type);
    sb.append(" ");
    sb.append(this.size_width.getValue());
    sb.append(" ");
    sb.append(this.size_height.getValue());
    sb.append("]");
    return sb.toString();
  }
}
