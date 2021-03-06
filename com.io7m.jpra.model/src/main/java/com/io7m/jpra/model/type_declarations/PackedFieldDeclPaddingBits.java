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

import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jpra.model.size_expressions.SizeExprType;

import java.net.URI;
import java.util.Objects;

/**
 * A {@code packed} type field that specifies a number of padding bits.
 *
 * @param <I> The type of identifiers
 * @param <T> The type of type information
 */

public final class PackedFieldDeclPaddingBits<I, T>
  implements PackedFieldDeclType<I, T>
{
  private final SizeExprType<I, T> size;
  private final LexicalPosition<URI> lex;

  /**
   * Construct a packed field declaration.
   *
   * @param in_lex  Lexical information
   * @param in_size The size expression
   */

  public PackedFieldDeclPaddingBits(
    final LexicalPosition<URI> in_lex,
    final SizeExprType<I, T> in_size)
  {
    this.lex = Objects.requireNonNull(in_lex, "Lexical information");
    this.size = Objects.requireNonNull(in_size, "Size");
  }

  /**
   * @return The size expression for the field
   */

  public SizeExprType<I, T> getSizeExpression()
  {
    return this.size;
  }

  @Override
  public <A, E extends Exception> A matchPackedFieldDeclaration(
    final PackedFieldDeclMatcherType<I, T, A, E> m)
    throws E
  {
    return m.matchPaddingBits(this);
  }

  @Override
  public LexicalPosition<URI> lexical()
  {
    return this.lex;
  }
}
