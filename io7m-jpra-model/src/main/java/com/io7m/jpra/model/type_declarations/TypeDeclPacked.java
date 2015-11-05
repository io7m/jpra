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
 * A {@code packed} type declaration.
 */

@Immutable public final class TypeDeclPacked<I> implements TypeDeclType<I>
{
  private final TypeName                                         name;
  private final ImmutableList<PackedFieldDeclType<I>>            fields_order;
  private final ImmutableMap<FieldName, PackedFieldDeclValue<I>> fields_name;
  private final I                                                identifier;

  /**
   * Construct a type declaration.
   *
   * @param in_identifier   The identifier
   * @param in_fields_name  The fields by name
   * @param in_name         The type name
   * @param in_fields_order The fields in declaration order
   */

  public TypeDeclPacked(
    final I in_identifier,
    final ImmutableMap<FieldName, PackedFieldDeclValue<I>> in_fields_name,
    final TypeName in_name,
    final ImmutableList<PackedFieldDeclType<I>> in_fields_order)
  {
    this.identifier = NullCheck.notNull(in_identifier);
    this.fields_name = NullCheck.notNull(in_fields_name);
    this.name = NullCheck.notNull(in_name);
    this.fields_order = NullCheck.notNull(in_fields_order);

    Assertive.require(
      this.fields_name.size() <= this.fields_order.size(),
      "Fields-by-name size %d > Fields-ordered size %d",
      Integer.valueOf(this.fields_name.size()),
      Integer.valueOf(this.fields_order.size()));

    this.fields_order.forEach(
      (Procedure<PackedFieldDeclType<I>>) r -> {
        final Optional<FieldName> r_name = PackedFieldDecl.name(r);
        if (r_name.isPresent()) {
          Assertive.require(this.fields_name.containsKey(r_name.get()));
        }
      });
  }

  @Override public TypeName getName()
  {
    return this.name;
  }

  @Override public <A, E extends Exception> A matchTypeDeclaration(
    final TypeDeclMatcherType<I, A, E> m)
    throws E
  {
    return m.matchPacked(this);
  }

  @Override
  public Optional<ImmutableLexicalPositionType<Path>> getLexicalInformation()
  {
    return this.name.getLexicalInformation();
  }

  @Override public I getIdentifier()
  {
    return this.identifier;
  }

  @Override public <A, E extends Exception> A matchStatement(
    final StatementMatcherType<I, A, E> m)
    throws E
  {
    return m.matchTypeDecl(this);
  }

  /**
   * @return The fields in declaration order
   */

  public ImmutableList<PackedFieldDeclType<I>> getFieldsInDeclarationOrder()
  {
    return this.fields_order;
  }
}
