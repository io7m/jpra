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

import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.map.ImmutableMap;
import com.io7m.jlexing.core.ImmutableLexicalPositionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.jpra.model.statements.StatementMatcherType;
import net.jcip.annotations.Immutable;
import org.valid4j.Assertive;

import java.nio.file.Path;
import java.util.Optional;

/**
 * A {@code record} type declaration.
 */

@Immutable public final class TypeDeclRecord<S> implements TypeDeclType<S>
{
  private final TypeName                                         name;
  private final ImmutableList<RecordFieldDeclType<S>>            fields_order;
  private final ImmutableMap<FieldName, RecordFieldDeclValue<S>> fields_name;
  private final S                                                data;

  /**
   * Construct a declaration.
   *
   * @param in_fields_name  The fields by name
   * @param in_name         The type name
   * @param in_fields_order The fields in declaration order
   */

  public TypeDeclRecord(
    final S in_data,
    final ImmutableMap<FieldName, RecordFieldDeclValue<S>> in_fields_name,
    final TypeName in_name,
    final ImmutableList<RecordFieldDeclType<S>> in_fields_order)
  {
    this.data = NullCheck.notNull(in_data);
    this.fields_name = NullCheck.notNull(in_fields_name);
    this.name = NullCheck.notNull(in_name);
    this.fields_order = NullCheck.notNull(in_fields_order);

    Assertive.require(
      this.fields_name.size() <= this.fields_order.size(),
      "Fields-by-name size %d > Fields-ordered size %d",
      Integer.valueOf(this.fields_name.size()),
      Integer.valueOf(this.fields_order.size()));

    this.fields_order.forEach(
      (Procedure<RecordFieldDeclType<S>>) r -> {
        final Optional<FieldName> r_name = RecordFieldDecl.name(r);
        if (r_name.isPresent()) {
          Assertive.require(this.fields_name.containsKey(r_name.get()));
        }
      });
  }

  /**
   * @return The fields by name
   */

  public ImmutableMap<FieldName, RecordFieldDeclValue<S>> getFieldsByName()
  {
    return this.fields_name;
  }

  /**
   * @return The fields in declaration order
   */

  public ImmutableList<RecordFieldDeclType<S>> getFieldsInDeclarationOrder()
  {
    return this.fields_order;
  }

  @Override public TypeName getName()
  {
    return this.name;
  }

  @Override public <A, E extends Exception> A matchTypeDeclaration(
    final TypeDeclMatcherType<S, A, E> m)
    throws E
  {
    return m.matchRecord(this);
  }

  @Override
  public Optional<ImmutableLexicalPositionType<Path>> getLexicalInformation()
  {
    return this.name.getLexicalInformation();
  }

  public S getData()
  {
    return this.data;
  }

  @Override public <A, E extends Exception> A matchStatement(
    final StatementMatcherType<S, A, E> m)
    throws E
  {
    return m.matchTypeDecl(this);
  }
}
