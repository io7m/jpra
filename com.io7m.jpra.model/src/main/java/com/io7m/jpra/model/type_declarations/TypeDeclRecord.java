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

import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.jpra.model.statements.StatementMatcherType;
import io.vavr.collection.List;
import io.vavr.collection.Map;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@code record} type declaration.
 *
 * @param <I> The type of identifiers
 * @param <T> The type of type information
 */

public final class TypeDeclRecord<I, T> implements TypeDeclType<I, T>
{
  private final TypeName name;
  private final List<RecordFieldDeclType<I, T>> fields_order;
  private final Map<FieldName, RecordFieldDeclValue<I, T>> fields_name;
  private final I identifier;
  private final T type;

  /**
   * Construct a declaration.
   *
   * @param in_identifier   The identifier
   * @param in_type         The type information
   * @param in_fields_name  The fields by name
   * @param in_name         The type name
   * @param in_fields_order The fields in declaration order
   */

  public TypeDeclRecord(
    final I in_identifier,
    final T in_type,
    final Map<FieldName, RecordFieldDeclValue<I, T>> in_fields_name,
    final TypeName in_name,
    final List<RecordFieldDeclType<I, T>> in_fields_order)
  {
    this.identifier =
      Objects.requireNonNull(in_identifier, "Identifier");
    this.type =
      Objects.requireNonNull(in_type, "Type");
    this.fields_name =
      Objects.requireNonNull(in_fields_name, "Fields by name");
    this.name =
      Objects.requireNonNull(in_name, "Type name");
    this.fields_order =
      Objects.requireNonNull(in_fields_order, "Fields in order");

    Preconditions.checkPreconditionV(
      this.fields_name.size() <= this.fields_order.size(),
      "Fields-by-name size %d > Fields-ordered size %d",
      Integer.valueOf(this.fields_name.size()),
      Integer.valueOf(this.fields_order.size()));

    this.fields_order.forEach(r -> {
      final Optional<FieldName> r_name_opt = RecordFieldDecl.name(r);
      r_name_opt.ifPresent(r_name -> Preconditions.checkPreconditionV(
        this.fields_name.containsKey(r_name),
        "Fields must contain %s",
        r_name));
    });
  }

  /**
   * @return The fields by name
   */

  public Map<FieldName, RecordFieldDeclValue<I, T>> getFieldsByName()
  {
    return this.fields_name;
  }

  /**
   * @return The fields in declaration order
   */

  public List<RecordFieldDeclType<I, T>> getFieldsInDeclarationOrder()
  {
    return this.fields_order;
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
    return m.matchRecord(this);
  }

  @Override
  public LexicalPosition<URI> lexical()
  {
    return this.name.lexical();
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
}
