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
import com.io7m.jpra.model.SizeUnitOctetsType;
import net.jcip.annotations.Immutable;

import java.nio.file.Path;
import java.util.Optional;

/**
 * A {@code string} type expression.
 */

@Immutable public final class TString implements TType
{
  private final String                                       encoding;
  private final Size<SizeUnitOctetsType>                     size;
  private final Optional<ImmutableLexicalPositionType<Path>> lex;
  private final Size<SizeUnitBitsType>                       size_bits;

  /**
   * Construct an expression.
   *
   * @param in_lex      Lexical information
   * @param in_encoding The string encoding
   * @param in_size     An expression denoting the maximum size of the string in
   *                    octets
   */

  public TString(
    final Optional<ImmutableLexicalPositionType<Path>> in_lex,
    final String in_encoding,
    final Size<SizeUnitOctetsType> in_size)
  {
    this.lex = NullCheck.notNull(in_lex);
    this.encoding = NullCheck.notNull(in_encoding);
    this.size = NullCheck.notNull(in_size);
    this.size_bits = Size.toBits(this.size);
  }

  /**
   * @return The string encoding
   */

  public String getEncoding()
  {
    return this.encoding;
  }

  @Override public Size<SizeUnitBitsType> getSize()
  {
    return this.size_bits;
  }

  @Override public <A, E extends Exception> A matchType(
    final TypeMatcherType<A, E> m)
    throws E
  {
    return m.matchString(this);
  }

  @Override
  public Optional<ImmutableLexicalPositionType<Path>> getLexicalInformation()
  {
    return this.lex;
  }
}
