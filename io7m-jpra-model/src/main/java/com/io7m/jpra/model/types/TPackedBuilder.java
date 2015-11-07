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

import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.factory.Maps;
import com.gs.collections.impl.factory.Sets;
import com.io7m.jfunctional.Unit;
import com.io7m.jlexing.core.ImmutableLexicalPositionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.contexts.PackageContextType;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.names.IdentifierType;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.junreachable.UnreachableCodeException;
import org.valid4j.Assertive;

import java.nio.file.Path;
import java.util.Optional;

final class TPackedBuilder implements TPackedBuilderType
{
  private final MutableList<TPacked.FieldType>            type_fields_ordered;
  private final MutableMap<FieldName, TPacked.FieldValue> type_fields_named;
  private final PackageContextType                        package_context;
  private final IdentifierType                            identifier;
  private final TypeName                                  name;
  private final boolean                                   finished;
  private final MutableSet<IdentifierType>                identifiers;

  TPackedBuilder(
    final PackageContextType in_package,
    final IdentifierType in_identifier,
    final TypeName in_ident)
  {
    this.package_context = NullCheck.notNull(in_package);
    this.identifier = NullCheck.notNull(in_identifier);
    this.name = NullCheck.notNull(in_ident);

    this.type_fields_ordered = Lists.mutable.empty();
    this.type_fields_named = Maps.mutable.empty();
    this.identifiers = Sets.mutable.empty();

    this.identifiers.add(this.identifier);
    this.finished = false;
  }

  @Override public void addPaddingBits(
    final Optional<ImmutableLexicalPositionType<Path>> lex,
    final Size<SizeUnitBitsType> size)
  {
    Assertive.require(
      !this.finished, "Builder must not have already finished");
    final TPacked.FieldPaddingBits v = new TPacked.FieldPaddingBits(size, lex);
    this.type_fields_ordered.add(v);
  }

  @Override public void addField(
    final FieldName in_name,
    final IdentifierType in_id,
    final TIntegerType in_type)
  {
    Assertive.require(
      !this.finished, "Builder must not have already finished");
    Assertive.require(
      !this.identifiers.contains(in_id), "Identifiers cannot be reused");

    final TPacked.FieldValue v = new TPacked.FieldValue(in_name, in_type);
    this.type_fields_named.put(in_name, v);
    this.type_fields_ordered.add(v);
    this.identifiers.add(in_id);
  }

  @Override public Size<SizeUnitBitsType> getCurrentSize()
  {
    return this.type_fields_ordered.injectInto(
      Size.zero(), (s, f) -> s.add(f.getSize()));
  }

  @Override public TPacked build()
  {
    Assertive.require(
      !this.finished, "Builder must not have already finished");

    final TPacked tr = new TPacked(
      this.package_context,
      this.identifier,
      this.name,
      this.type_fields_named.toImmutable(),
      this.type_fields_ordered.toImmutable());

    for (final TPacked.FieldType f : this.type_fields_ordered) {
      f.matchField(
        new TPacked.FieldMatcherType<Unit, UnreachableCodeException>()
        {
          @Override public Unit matchFieldValue(
            final TPacked.FieldValue f)
          {
            f.setOwner(tr);
            return Unit.unit();
          }

          @Override public Unit matchFieldPaddingBits(
            final TPacked.FieldPaddingBits f)
          {
            f.setOwner(tr);
            return Unit.unit();
          }
        });
    }
    return tr;
  }
}