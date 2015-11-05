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

package com.io7m.jpra.model.statements;

import com.io7m.jlexing.core.ImmutableLexicalPositionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.ModelElementType;
import com.io7m.jpra.model.type_expressions.TypeExprType;

import java.nio.file.Path;
import java.util.Optional;

public final class StatementCommandType<I>
  implements ModelElementType, StatementType<I>
{
  private final TypeExprType<I> expr;

  public StatementCommandType(
    final TypeExprType<I> e)
  {
    this.expr = NullCheck.notNull(e);
  }

  public TypeExprType<I> getExpression()
  {
    return this.expr;
  }

  @Override
  public Optional<ImmutableLexicalPositionType<Path>> getLexicalInformation()
  {
    return this.expr.getLexicalInformation();
  }

  @Override public <A, E extends Exception> A matchStatement(
    final StatementMatcherType<I, A, E> m)
    throws E
  {
    return m.matchShowType(this);
  }
}
