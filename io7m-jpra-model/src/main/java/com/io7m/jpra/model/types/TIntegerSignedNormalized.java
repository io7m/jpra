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
import com.io7m.jpra.model.SizeUnitBitsType;
import net.jcip.annotations.Immutable;

import java.nio.file.Path;
import java.util.Optional;

/**
 * A {@code signed-normalized} type expression.
 */

@Immutable public final class TIntegerSignedNormalized implements TIntegerType
{
  private final Optional<ImmutableLexicalPositionType<Path>> lex;
  private final Size<SizeUnitBitsType>                       size;

  /**
   * Construct an {@code integer signed-normalized} type expression.
   *
   * @param in_lex  Lexical information
   * @param in_size The size in bits that will be used
   */

  public TIntegerSignedNormalized(
    final Optional<ImmutableLexicalPositionType<Path>> in_lex,
    final Size<SizeUnitBitsType> in_size)
  {
    this.lex = NullCheck.notNull(in_lex);
    this.size = NullCheck.notNull(in_size);
  }

  @Override public Size<SizeUnitBitsType> getSize()
  {
    return this.size;
  }

  @Override public <A, E extends Exception> A matchType(
    final TypeMatcherType<A, E> m)
    throws E
  {
    return m.matchInteger(this);
  }

  @Override public <A, E extends Exception> A matchTypeInteger(
    final TypeIntegerMatcherType<A, E> m)
    throws E
  {
    return m.matchIntegerSignedNormalized(this);
  }

  @Override public <A, E extends Exception> A matchTypeScalar(
    final TypeScalarMatcherType<A, E> m)
    throws E
  {
    return m.matchScalarInteger(this);
  }

  @Override
  public Optional<ImmutableLexicalPositionType<Path>> getLexicalInformation()
  {
    return this.lex;
  }
}
