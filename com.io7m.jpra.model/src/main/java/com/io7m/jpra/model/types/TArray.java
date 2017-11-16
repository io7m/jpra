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
import net.jcip.annotations.Immutable;

import java.math.BigInteger;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * An {@code array} type expression.
 */

@Immutable
public final class TArray implements TType
{
  private final Size<?> element_count;
  private final Optional<LexicalPosition<Path>> lex;
  private final TType element_type;
  private final Size<SizeUnitBitsType> size_bits;

  /**
   * Construct an {@code array} type expression.
   *
   * @param in_lex  Lexical information
   * @param in_size A size expression denoting the number of array elements
   * @param in_type The element type
   */

  public TArray(
    final Optional<LexicalPosition<Path>> in_lex,
    final Size<?> in_size,
    final TType in_type)
  {
    this.lex = Objects.requireNonNull(in_lex, "Lexical information");
    this.element_count = Objects.requireNonNull(in_size, "Size");
    this.element_type = Objects.requireNonNull(in_type, "Type");

    final BigInteger ecv = this.element_count.getValue();
    final BigInteger etv = this.element_type.getSizeInBits().getValue();
    this.size_bits = new Size<>(ecv.multiply(etv));
  }

  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder("[array ");
    sb.append(this.element_count.getValue());
    sb.append(" ");
    sb.append(this.element_type);
    sb.append("]");
    return sb.toString();
  }

  /**
   * @return The type of elements
   */

  public TType getElementType()
  {
    return this.element_type;
  }

  /**
   * @return The number of elements
   */

  public Size<?> getElementCount()
  {
    return this.element_count;
  }

  @Override
  public Optional<LexicalPosition<Path>> getLexicalInformation()
  {
    return this.lex;
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
    return m.matchArray(this);
  }
}
