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
import com.io7m.jranges.RangeInclusiveB;
import com.io7m.junreachable.UnreachableCodeException;
import io.vavr.collection.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

final class TPackedBuilder implements TPackedBuilderType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(TPackedBuilder.class);
  }

  private final ArrayList<TPacked.FieldType> type_fields_ordered;
  private final HashMap<FieldName, TPacked.FieldValue> type_fields_named;
  private final PackageContextType package_context;
  private final IdentifierType identifier;
  private final TypeName name;
  private final boolean finished;
  private final HashSet<IdentifierType> identifiers;

  TPackedBuilder(
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
  public void addPaddingBits(
    final LexicalPosition<URI> lex,
    final Size<SizeUnitBitsType> size)
  {
    Preconditions.checkPrecondition(
      !this.finished, "Builder must not have already finished");
    final TPacked.FieldPaddingBits v = new TPacked.FieldPaddingBits(size, lex);
    this.type_fields_ordered.add(v);
  }

  @Override
  public void addField(
    final FieldName in_name,
    final IdentifierType in_id,
    final TIntegerType in_type)
  {
    Preconditions.checkPrecondition(
      !this.finished, "Builder must not have already finished");
    Preconditions.checkPrecondition(
      !this.identifiers.contains(in_id), "Identifiers cannot be reused");

    final TPacked.FieldValue v = new TPacked.FieldValue(in_name, in_type);
    this.type_fields_named.put(in_name, v);
    this.type_fields_ordered.add(v);
    this.identifiers.add(in_id);
  }

  @Override
  public Size<SizeUnitBitsType> getCurrentSize()
  {
    return this.type_fields_ordered.stream()
      .map(TPacked.FieldType::getSize)
      .reduce(Size.zero(), Size::add);
  }

  @Override
  public TPacked build()
  {
    Preconditions.checkPrecondition(
      !this.finished, "Builder must not have already finished");

    final TPacked tr = new TPacked(
      this.package_context,
      this.identifier,
      this.name,
      io.vavr.collection.HashMap.ofAll(this.type_fields_named),
      List.ofAll(this.type_fields_ordered));

    final BigInteger size = tr.getSizeInBits().getValue();
    final AtomicReference<BigInteger> msb =
      new AtomicReference<>(size.subtract(BigInteger.ONE));

    for (final TPacked.FieldType f : this.type_fields_ordered) {
      f.matchField(new FieldAdder(tr, msb));
    }
    return tr;
  }

  private static final class FieldAdder
    implements TPacked.FieldMatcherType<Void, UnreachableCodeException>
  {
    private final TPacked tr;
    private final AtomicReference<BigInteger> msb;

    FieldAdder(
      final TPacked in_tr,
      final AtomicReference<BigInteger> in_msb)
    {
      this.tr = in_tr;
      this.msb = in_msb;
    }

    @Override
    public Void matchFieldValue(
      final TPacked.FieldValue f)
    {
      f.setOwner(this.tr);
      final BigInteger size = f.getSize().getValue();
      final BigInteger current_msb = this.msb.get();
      final BigInteger lsb =
        current_msb.subtract(size).add(BigInteger.ONE);

      LOG.trace("field lsb/msb: {}/{}", lsb, current_msb);
      f.setRange(RangeInclusiveB.of(lsb, current_msb));
      this.msb.set(lsb.subtract(BigInteger.ONE));
      return null;
    }

    @Override
    public Void matchFieldPaddingBits(
      final TPacked.FieldPaddingBits f)
    {
      f.setOwner(this.tr);
      final BigInteger size = f.getSize().getValue();
      final BigInteger current_msb = this.msb.get();
      final BigInteger lsb =
        current_msb.subtract(size).add(BigInteger.ONE);

      LOG.trace("field lsb/msb: {}/{}", lsb, current_msb);
      f.setRange(RangeInclusiveB.of(lsb, current_msb));
      this.msb.set(lsb.subtract(BigInteger.ONE));
      return null;
    }
  }
}
