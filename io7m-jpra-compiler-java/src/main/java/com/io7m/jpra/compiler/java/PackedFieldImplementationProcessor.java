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

package com.io7m.jpra.compiler.java;

import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.types.Size;
import com.io7m.jpra.model.types.SizeUnitBitsType;
import com.io7m.jpra.model.types.TArray;
import com.io7m.jpra.model.types.TBooleanSet;
import com.io7m.jpra.model.types.TFloat;
import com.io7m.jpra.model.types.TIntegerSigned;
import com.io7m.jpra.model.types.TIntegerSignedNormalized;
import com.io7m.jpra.model.types.TIntegerType;
import com.io7m.jpra.model.types.TIntegerUnsigned;
import com.io7m.jpra.model.types.TIntegerUnsignedNormalized;
import com.io7m.jpra.model.types.TMatrix;
import com.io7m.jpra.model.types.TPacked;
import com.io7m.jpra.model.types.TRecord;
import com.io7m.jpra.model.types.TString;
import com.io7m.jpra.model.types.TVector;
import com.io7m.jpra.model.types.TypeIntegerMatcherType;
import com.io7m.jpra.model.types.TypeMatcherType;
import com.io7m.jranges.RangeInclusiveB;
import com.io7m.junreachable.UnimplementedCodeException;
import com.io7m.junreachable.UnreachableCodeException;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.element.Modifier;
import java.math.BigInteger;

/**
 * A type matcher that produces implementation methods for a given packed
 * field.
 */

public final class PackedFieldImplementationProcessor
  implements TypeMatcherType<Unit, UnreachableCodeException>,
  TypeIntegerMatcherType<Unit, UnreachableCodeException>
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(PackedFieldImplementationProcessor.class);
  }

  private final TPacked.FieldValue field;
  private final TypeSpec.Builder   class_builder;
  private final BigInteger         offset_bits;

  PackedFieldImplementationProcessor(
    final TPacked.FieldValue in_field,
    final BigInteger in_offset_bits,
    final TypeSpec.Builder in_class_builder)
  {
    this.field = NullCheck.notNull(in_field);
    this.offset_bits = NullCheck.notNull(in_offset_bits);
    this.class_builder = NullCheck.notNull(in_class_builder);
  }

  @Override public Unit matchIntegerUnsigned(
    final TIntegerUnsigned t)
  {
    return this.onInteger(t.getSizeInBits());
  }

  private Unit onInteger(final Size<SizeUnitBitsType> size)
  {
    final BigInteger container_size =
      this.field.getOwner().getSizeInBits().getValue();
    final BigInteger field_size = size.getValue();

    final String getter_name =
      JPRAGeneratedNames.getGetterName(this.field.getName());
    final String setter_name =
      JPRAGeneratedNames.getSetterName(this.field.getName());

    if (field_size.compareTo(BigInteger.valueOf(64L)) > 0) {
      throw new UnimplementedCodeException();
    }

    /**
     * The implementation deals with three types:
     *
     * The "container" type is the type that will be used to actually store
     * packed values; all of the fields of the packed record are packed into
     * a value of this type.
     *
     * The "arith" type is the type that will be used to perform operations
     * upon field values; this type will be {@code int} or larger, and no
     * smaller than the container type.
     *
     * The "external" type is the type that the programmer will use to interact
     * with the generated code. This will be {@code int} or larger, and no
     * smaller than the field size.
     */

    final Class<?> container_type;
    final Class<?> arith_type;
    final Class<?> external_type;

    if (field_size.compareTo(BigInteger.valueOf(32L)) > 0) {
      external_type = long.class;
    } else if (field_size.compareTo(BigInteger.valueOf(16L)) > 0) {
      external_type = int.class;
    } else if (field_size.compareTo(BigInteger.valueOf(8L)) > 0) {
      external_type = int.class;
    } else {
      external_type = int.class;
    }

    final String iget;
    final String iput;
    if (container_size.equals(BigInteger.valueOf(64L))) {
      container_type = long.class;
      arith_type = long.class;
      iget = "getLong";
      iput = "putLong";
    } else if (container_size.equals(BigInteger.valueOf(32L))) {
      container_type = int.class;
      arith_type = int.class;
      iget = "getInt";
      iput = "putInt";
    } else if (container_size.equals(BigInteger.valueOf(16L))) {
      container_type = short.class;
      arith_type = int.class;
      iget = "getShort";
      iput = "putShort";
    } else {
      container_type = byte.class;
      arith_type = int.class;
      iget = "get";
      iput = "put";
    }

    final BigInteger shift =
      container_size.subtract(this.offset_bits).subtract(field_size);
    final RangeInclusiveB field_bit_range = this.field.getBitRange();
    final String container_mask = JPRAMasks.createZeroMask(
      container_size.intValue(),
      field_bit_range.getLower().intValue(),
      field_bit_range.getUpper().intValue());
    final String field_mask = JPRAMasks.createOneMask(
      container_size.intValue(), 0, field_size.intValue() - 1);

    final MethodSpec.Builder getb = MethodSpec.methodBuilder(getter_name);
    getb.addModifiers(Modifier.PUBLIC);
    getb.returns(external_type);
    getb.addAnnotation(Override.class);
    getb.addStatement(
      "final $T read = this.$N.$N(this.getByteOffset())",
      container_type,
      "buffer",
      iget);
    getb.addStatement(
      "return ($T) ((read >>> $L) & $L)", external_type, shift, field_mask);
    this.class_builder.addMethod(getb.build());

    final MethodSpec.Builder setb = MethodSpec.methodBuilder(setter_name);
    setb.addModifiers(Modifier.PUBLIC);
    setb.addParameter(external_type, "x", Modifier.FINAL);
    setb.addAnnotation(Override.class);
    setb.returns(void.class);
    setb.addStatement(
      "final $T r_valu = this.$N.$N(this.getByteOffset())",
      container_type,
      "buffer",
      iget);
    setb.addStatement(
      "final $T r_mask = $L", arith_type, container_mask);
    setb.addStatement(
      "final $T x_mask = $L", arith_type, field_mask);
    setb.addStatement(
      "final $T x_valu = (x & x_mask) << $L", arith_type, shift);
    setb.addStatement(
      "final $T w_valu = (r_valu & r_mask) | x_valu", arith_type);
    setb.addStatement(
      "this.$N.$N(this.getByteOffset(), ($T) w_valu)",
      "buffer",
      iput,
      container_type);
    this.class_builder.addMethod(setb.build());
    return Unit.unit();
  }

  @Override public Unit matchIntegerSigned(
    final TIntegerSigned t)
  {
    return this.onInteger(t.getSizeInBits());
  }

  @Override public Unit matchIntegerSignedNormalized(
    final TIntegerSignedNormalized t)
  {
    // TODO: Generated method stub!
    throw new UnimplementedCodeException();
  }

  @Override public Unit matchIntegerUnsignedNormalized(
    final TIntegerUnsignedNormalized t)
  {
    // TODO: Generated method stub!
    throw new UnimplementedCodeException();
  }

  @Override public Unit matchArray(final TArray t)
  {
    throw new UnreachableCodeException();
  }

  @Override public Unit matchString(final TString t)
  {
    throw new UnreachableCodeException();
  }

  @Override public Unit matchBooleanSet(final TBooleanSet t)
  {
    throw new UnreachableCodeException();
  }

  @Override public Unit matchInteger(final TIntegerType t)
  {
    return t.matchTypeInteger(this);
  }

  @Override public Unit matchFloat(final TFloat t)
  {
    throw new UnreachableCodeException();
  }

  @Override public Unit matchVector(final TVector t)
  {
    throw new UnreachableCodeException();
  }

  @Override public Unit matchMatrix(final TMatrix t)
  {
    throw new UnreachableCodeException();
  }

  @Override public Unit matchRecord(final TRecord t)
  {
    throw new UnreachableCodeException();
  }

  @Override public Unit matchPacked(final TPacked t)
  {
    throw new UnreachableCodeException();
  }
}
