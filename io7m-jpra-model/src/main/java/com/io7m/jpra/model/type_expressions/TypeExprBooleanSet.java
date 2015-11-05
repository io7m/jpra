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

package com.io7m.jpra.model.type_expressions;

import com.gs.collections.api.list.ImmutableList;
import com.io7m.jlexing.core.ImmutableLexicalPositionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.size_expressions.SizeExprType;

import java.nio.file.Path;
import java.util.Optional;

public final class TypeExprBooleanSet<I> implements TypeExprType<I>
{
  private final SizeExprType<I>                              size;
  private final ImmutableList<FieldName>                     fields;
  private final Optional<ImmutableLexicalPositionType<Path>> lex;
  private final I                                            data;

  /**
   * Construct a type expression.
   *
   * @param in_data   The supplemental data
   * @param in_lex    Lexical information
   * @param in_fields The fields of the set
   * @param in_size   The size in octets that will be used
   */

  public TypeExprBooleanSet(
    final I in_data,
    final Optional<ImmutableLexicalPositionType<Path>> in_lex,
    final ImmutableList<FieldName> in_fields,
    final SizeExprType<I> in_size)
  {
    this.lex = NullCheck.notNull(in_lex);
    this.fields = NullCheck.notNull(in_fields);
    this.size = NullCheck.notNull(in_size);
    this.data = NullCheck.notNull(in_data);
  }

  /**
   * @return The fields in declaration order
   */

  public ImmutableList<FieldName> getFieldsInDeclarationOrder()
  {
    return this.fields;
  }

  /**
   * @return The size expression denoting the size in octets
   */

  public SizeExprType<I> getSizeExpression()
  {
    return this.size;
  }

  @Override public I getData()
  {
    return this.data;
  }

  @Override public <A, E extends Exception> A matchType(
    final TypeExprMatcherType<I, A, E> m)
    throws E
  {
    return m.matchBooleanSet(this);
  }

  @Override
  public Optional<ImmutableLexicalPositionType<Path>> getLexicalInformation()
  {
    return this.lex;
  }
}
