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

package com.io7m.jpra.model.types;


import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jpra.model.contexts.PackageContextType;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.names.IdentifierType;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.junreachable.UnreachableCodeException;
import io.vavr.collection.List;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

final class TRecordBuilder implements TRecordBuilderType
{
  private final ArrayList<TRecord.FieldType> type_fields_ordered;
  private final HashMap<FieldName, TRecord.FieldValue> type_fields_named;
  private final PackageContextType package_context;
  private final IdentifierType identifier;
  private final TypeName name;
  private final boolean finished;
  private final HashSet<IdentifierType> identifiers;

  TRecordBuilder(
    final PackageContextType in_package,
    final IdentifierType in_identifier,
    final TypeName in_ident)
  {
    this.package_context = Objects.requireNonNull(in_package, "Package");
    this.identifier = Objects.requireNonNull(in_identifier, "Identifier");
    this.name = Objects.requireNonNull(in_ident, "Type name");

    this.type_fields_ordered = new ArrayList<>();
    this.type_fields_named = new HashMap<>();
    this.identifiers = new HashSet<>();

    this.identifiers.add(this.identifier);
    this.finished = false;
  }

  @Override
  public void addPaddingOctets(
    final LexicalPosition<URI> lex,
    final Size<SizeUnitOctetsType> size)
  {
    Preconditions.checkPrecondition(
      !this.finished, "Builder must not have already finished");
    final TRecord.FieldPaddingOctets v =
      new TRecord.FieldPaddingOctets(size, lex);
    this.type_fields_ordered.add(v);
  }

  @Override
  public void addField(
    final FieldName in_name,
    final IdentifierType in_id,
    final TType in_type)
  {
    Preconditions.checkPrecondition(
      !this.finished, "Builder must not have already finished");
    Preconditions.checkPrecondition(
      !this.identifiers.contains(in_id), "Identifiers cannot be reused");

    final TRecord.FieldValue v = new TRecord.FieldValue(in_name, in_type);
    this.type_fields_named.put(in_name, v);
    this.type_fields_ordered.add(v);
    this.identifiers.add(in_id);
  }

  @Override
  public TRecord build()
  {
    Preconditions.checkPrecondition(
      !this.finished, "Builder must not have already finished");

    final TRecord tr = new TRecord(
      this.package_context,
      this.identifier,
      this.name,
      io.vavr.collection.HashMap.ofAll(this.type_fields_named),
      List.ofAll(this.type_fields_ordered));

    for (final TRecord.FieldType f : this.type_fields_ordered) {
      f.matchField(
        new TRecord.FieldMatcherType<Void, UnreachableCodeException>()
        {
          @Override
          public Void matchFieldValue(
            final TRecord.FieldValue f)
          {
            f.setOwner(tr);
            return null;
          }

          @Override
          public Void matchFieldPaddingOctets(
            final TRecord.FieldPaddingOctets f)
          {
            f.setOwner(tr);
            return null;
          }
        });
    }
    return tr;
  }
}
