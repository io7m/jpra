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

package com.io7m.jpra.model.type_declarations;

import com.io7m.jlexing.core.ImmutableLexicalPositionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.size_expressions.SizeExprType;
import net.jcip.annotations.Immutable;

import java.nio.file.Path;
import java.util.Optional;

/**
 * A {@code record} field declaration that specifies a number of padding
 * octets.
 */

@Immutable public final class RecordFieldDeclPaddingOctets<S>
  implements RecordFieldDeclType<S>
{
  private final SizeExprType<S>                              size;
  private final Optional<ImmutableLexicalPositionType<Path>> lex;

  /**
   * Construct a field declaration.
   *
   * @param in_lex  The lexical information for the name
   * @param in_size The size expression
   */

  public RecordFieldDeclPaddingOctets(
    final Optional<ImmutableLexicalPositionType<Path>> in_lex,
    final SizeExprType<S> in_size)
  {
    this.lex = NullCheck.notNull(in_lex);
    this.size = NullCheck.notNull(in_size);
  }

  /**
   * @return The field size expression
   */

  public SizeExprType<S> getSizeExpression()
  {
    return this.size;
  }

  @Override public <A, E extends Exception> A matchRecordFieldDeclaration(
    final RecordFieldDeclMatcherType<S, A, E> m)
    throws E
  {
    return m.matchPadding(this);
  }

  @Override
  public Optional<ImmutableLexicalPositionType<Path>> getLexicalInformation()
  {
    return this.lex;
  }
}
