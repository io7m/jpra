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
import com.io7m.jpra.model.SizeExprType;
import com.io7m.jpra.model.SizeUnitOctetsType;
import net.jcip.annotations.Immutable;

import java.nio.file.Path;
import java.util.Optional;

/**
 * A {@code boolean-set} type.
 */

@Immutable public final class TypeExprBooleanSet implements TypeExprType
{
  private final SizeExprType<SizeUnitOctetsType>             size;
  private final ImmutableList<FieldName>                     fields;
  private final Optional<ImmutableLexicalPositionType<Path>> lex;

  private TypeExprBooleanSet(
    final Optional<ImmutableLexicalPositionType<Path>> in_lex,
    final ImmutableList<FieldName> in_fields,
    final SizeExprType<SizeUnitOctetsType> in_size)
  {
    this.lex = NullCheck.notNull(in_lex);
    this.fields = NullCheck.notNull(in_fields);
    this.size = NullCheck.notNull(in_size);
  }

  /**
   * Construct a {@code boolean-set} type expression.
   *
   * @param in_fields The fields of the set
   * @param in_size   The size in octets that will be used
   *
   * @return A type expression
   */

  public static TypeExprBooleanSet newSet(
    final ImmutableList<FieldName> in_fields,
    final SizeExprType<SizeUnitOctetsType> in_size)
  {
    return new TypeExprBooleanSet(Optional.empty(), in_fields, in_size);
  }

  /**
   * Construct a {@code boolean-set} type expression.
   *
   * @param in_lex    Lexical information
   * @param in_fields The fields of the set
   * @param in_size   The size in octets that will be used
   *
   * @return A type expression
   */

  public static TypeExprBooleanSet newSetWithLex(
    final ImmutableLexicalPositionType<Path> in_lex,
    final ImmutableList<FieldName> in_fields,
    final SizeExprType<SizeUnitOctetsType> in_size)
  {
    return new TypeExprBooleanSet(Optional.of(in_lex), in_fields, in_size);
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

  public SizeExprType<SizeUnitOctetsType> getSizeExpression()
  {
    return this.size;
  }

  @Override public <A, E extends Exception> A matchTypeExpression(
    final TypeExprMatcherType<A, E> m)
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
