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
import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.jpra.model.statements.StatementMatcherType;
import net.jcip.annotations.Immutable;

import java.nio.file.Path;
import java.util.Optional;

/**
 * A {@code packed} type declaration.
 *
 * @param <I> The type of identifiers
 * @param <T> The type of type information
 */

@Immutable
public final class TypeDeclPacked<I, T> implements TypeDeclType<I, T>
{
  private final TypeName name;
  private final ImmutableList<PackedFieldDeclType<I, T>>
    fields_order;
  private final ImmutableMap<FieldName, PackedFieldDeclValue<I, T>> fields_name;
  private final I identifier;
  private final T type;

  /**
   * Construct a type declaration.
   *
   * @param in_identifier   The identifier
   * @param in_type         The type
   * @param in_fields_name  The fields by name
   * @param in_name         The type name
   * @param in_fields_order The fields in declaration order
   */

  public TypeDeclPacked(
    final I in_identifier,
    final T in_type,
    final ImmutableMap<FieldName, PackedFieldDeclValue<I, T>> in_fields_name,
    final TypeName in_name,
    final ImmutableList<PackedFieldDeclType<I, T>> in_fields_order)
  {
    this.identifier =
      NullCheck.notNull(in_identifier, "Identifier");
    this.type =
      NullCheck.notNull(in_type, "Type");
    this.fields_name =
      NullCheck.notNull(in_fields_name, "Field name");
    this.name =
      NullCheck.notNull(in_name, "Type name");
    this.fields_order =
      NullCheck.notNull(in_fields_order, "Fields ordered");

    Preconditions.checkPreconditionV(
      this.fields_name.size() <= this.fields_order.size(),
      "Fields-by-name size %d > Fields-ordered size %d",
      Integer.valueOf(this.fields_name.size()),
      Integer.valueOf(this.fields_order.size()));

    this.fields_order.forEach(
      (Procedure<PackedFieldDeclType<I, T>>) r -> {
        final Optional<FieldName> r_name = PackedFieldDecl.name(r);
        if (r_name.isPresent()) {
          final FieldName rn = r_name.get();
          Preconditions.checkPreconditionV(
            this.fields_name.containsKey(rn),
            "Fields must contain %s", rn);
        }
      });
  }

  @Override
  public TypeName getName()
  {
    return this.name;
  }

  @Override
  public <A, E extends Exception> A matchTypeDeclaration(
    final TypeDeclMatcherType<I, T, A, E> m)
    throws E
  {
    return m.matchPacked(this);
  }

  @Override
  public Optional<LexicalPosition<Path>> getLexicalInformation()
  {
    return this.name.getLexicalInformation();
  }

  @Override
  public T getType()
  {
    return this.type;
  }

  @Override
  public I getIdentifier()
  {
    return this.identifier;
  }

  @Override
  public <A, E extends Exception> A matchStatement(
    final StatementMatcherType<I, T, A, E> m)
    throws E
  {
    return m.matchTypeDecl(this);
  }

  /**
   * @return The fields in declaration order
   */

  public ImmutableList<PackedFieldDeclType<I, T>> getFieldsInDeclarationOrder()
  {
    return this.fields_order;
  }

  /**
   * @return The fields by name
   */

  public ImmutableMap<FieldName, PackedFieldDeclValue<I, T>> getFieldsByName()
  {
    return this.fields_name;
  }
}
