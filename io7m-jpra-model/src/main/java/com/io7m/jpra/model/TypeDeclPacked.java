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

package com.io7m.jpra.model;

import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.map.ImmutableMap;
import com.io7m.jnull.NullCheck;
import org.valid4j.Assertive;

import java.util.Optional;

/**
 * A {@code packed} type declaration.
 */

public final class TypeDeclPacked implements TypeDeclType
{
  private final TypeName                                      name;
  private final ImmutableList<PackedFieldDeclType>            fields_order;
  private final ImmutableMap<FieldName, PackedFieldDeclValue> fields_name;

  /**
   * Construct a type declaration.
   *
   * @param in_fields_name  The fields by name
   * @param in_name         The type name
   * @param in_fields_order The fields in declaration order
   */

  public TypeDeclPacked(
    final ImmutableMap<FieldName, PackedFieldDeclValue> in_fields_name,
    final TypeName in_name,
    final ImmutableList<PackedFieldDeclType> in_fields_order)
  {
    this.fields_name = NullCheck.notNull(in_fields_name);
    this.name = NullCheck.notNull(in_name);
    this.fields_order = NullCheck.notNull(in_fields_order);

    Assertive.require(
      this.fields_name.size() <= this.fields_order.size(),
      "Fields-by-name size %d > Fields-ordered size %d",
      Integer.valueOf(this.fields_name.size()),
      Integer.valueOf(this.fields_order.size()));

    this.fields_order.forEach(
      (Procedure<PackedFieldDeclType>) r -> {
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
    final TypeDeclMatcherType<A, E> m)
    throws E
  {
    return m.matchPacked(this);
  }
}
