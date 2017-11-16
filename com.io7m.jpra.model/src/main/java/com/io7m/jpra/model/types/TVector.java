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
 * A {@code vector} type expression.
 */

@Immutable
public final class TVector implements TType
{
  private final Optional<LexicalPosition<Path>> lex;
  private final TypeScalarType type;
  private final Size<?> size;
  private final Size<SizeUnitBitsType> size_bits;

  /**
   * Construct a type expression.
   *
   * @param in_lex  Lexical information
   * @param in_size The number of elements in the vector
   * @param in_type The type of vector elements
   */

  public TVector(
    final Optional<LexicalPosition<Path>> in_lex,
    final Size<?> in_size,
    final TypeScalarType in_type)
  {
    this.lex = Objects.requireNonNull(in_lex, "Lexical information");
    this.size = Objects.requireNonNull(in_size, "Size");
    this.type = Objects.requireNonNull(in_type, "Type");

    final BigInteger ecv = this.size.getValue();
    final BigInteger etv = this.type.getSizeInBits().getValue();
    this.size_bits = new Size<>(ecv.multiply(etv));
  }

  @Override
  public Size<SizeUnitBitsType> getSizeInBits()
  {
    return this.size_bits;
  }

  /**
   * @return The number of elements in the vector
   */

  public Size<?> getElementCount()
  {
    return this.size;
  }

  /**
   * @return The type of elements
   */

  public TypeScalarType getElementType()
  {
    return this.type;
  }

  @Override
  public <A, E extends Exception> A matchType(
    final TypeMatcherType<A, E> m)
    throws E
  {
    return m.matchVector(this);
  }

  @Override
  public Optional<LexicalPosition<Path>> getLexicalInformation()
  {
    return this.lex;
  }

  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder("[vector ");
    sb.append(this.size.getValue());
    sb.append(" ");
    sb.append(this.type);
    sb.append("]");
    return sb.toString();
  }
}
