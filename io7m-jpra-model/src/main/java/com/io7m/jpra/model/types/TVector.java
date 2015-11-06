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
import net.jcip.annotations.Immutable;

import java.math.BigInteger;
import java.nio.file.Path;
import java.util.Optional;

/**
 * A {@code vector} type expression.
 */

@Immutable public final class TVector implements TType
{
  private final Optional<ImmutableLexicalPositionType<Path>> lex;
  private final TypeScalarType                               type;
  private final Size<?>                                      size;
  private final Size<SizeUnitBitsType>                       size_bits;

  /**
   * Construct a type expression.
   *
   * @param in_lex  Lexical information
   * @param in_size The number of elements in the vector
   * @param in_type The type of vector elements
   */

  public TVector(
    final Optional<ImmutableLexicalPositionType<Path>> in_lex,
    final Size<?> in_size,
    final TypeScalarType in_type)
  {
    this.lex = NullCheck.notNull(in_lex);
    this.size = NullCheck.notNull(in_size);
    this.type = NullCheck.notNull(in_type);

    final BigInteger ecv = this.size.getValue();
    final BigInteger etv = this.type.getSize().getValue();
    this.size_bits = new Size<>(ecv.multiply(etv));
  }

  @Override public Size<SizeUnitBitsType> getSize()
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

  @Override public <A, E extends Exception> A matchType(
    final TypeMatcherType<A, E> m)
    throws E
  {
    return m.matchVector(this);
  }

  @Override
  public Optional<ImmutableLexicalPositionType<Path>> getLexicalInformation()
  {
    return this.lex;
  }
}
