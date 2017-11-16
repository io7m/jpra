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

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * An {@code unsigned} integer type expression.
 */

@Immutable
public final class TIntegerUnsigned implements TIntegerType
{
  private final Optional<LexicalPosition<Path>> lex;
  private final Size<SizeUnitBitsType> size;

  /**
   * Construct an {@code integer unsigned} type expression.
   *
   * @param in_lex  Lexical information
   * @param in_size The size in bits that will be used
   */

  public TIntegerUnsigned(
    final Optional<LexicalPosition<Path>> in_lex,
    final Size<SizeUnitBitsType> in_size)
  {
    this.lex = Objects.requireNonNull(in_lex, "Lexical information");
    this.size = Objects.requireNonNull(in_size, "Size");
  }

  @Override
  public Size<SizeUnitBitsType> getSizeInBits()
  {
    return this.size;
  }

  @Override
  public <A, E extends Exception> A matchType(
    final TypeMatcherType<A, E> m)
    throws E
  {
    return m.matchInteger(this);
  }

  @Override
  public <A, E extends Exception> A matchTypeInteger(
    final TypeIntegerMatcherType<A, E> m)
    throws E
  {
    return m.matchIntegerUnsigned(this);
  }

  @Override
  public <A, E extends Exception> A matchTypeScalar(
    final TypeScalarMatcherType<A, E> m)
    throws E
  {
    return m.matchScalarInteger(this);
  }

  @Override
  public Optional<LexicalPosition<Path>> getLexicalInformation()
  {
    return this.lex;
  }

  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder("[integer unsigned ");
    sb.append(this.size.getValue());
    sb.append("]");
    return sb.toString();
  }
}
