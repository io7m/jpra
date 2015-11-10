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
import com.io7m.jnfp.core.NFPSignedDoubleInt;
import com.io7m.jnfp.core.NFPSignedDoubleLong;
import com.io7m.jnfp.core.NFPUnsignedDoubleInt;
import com.io7m.jnfp.core.NFPUnsignedDoubleLong;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.types.TIntegerSigned;
import com.io7m.jpra.model.types.TIntegerSignedNormalized;
import com.io7m.jpra.model.types.TIntegerUnsigned;
import com.io7m.jpra.model.types.TIntegerUnsignedNormalized;
import com.io7m.jpra.model.types.TRecord;
import com.io7m.jpra.model.types.TypeIntegerMatcherType;
import com.io7m.junreachable.UnimplementedCodeException;
import com.io7m.junreachable.UnreachableCodeException;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.math.BigInteger;

/**
 * A type matcher that produces implementation methods for a given integer type
 * record fields.
 */

final class RecordFieldImplementationIntegerProcessor
  implements TypeIntegerMatcherType<Unit, UnreachableCodeException>
{
  private final TRecord.FieldValue field;
  private final TypeSpec.Builder   class_builder;

  RecordFieldImplementationIntegerProcessor(
    final TRecord.FieldValue in_field,
    final TypeSpec.Builder in_class_builder)
  {
    this.field = NullCheck.notNull(in_field);
    this.class_builder = NullCheck.notNull(in_class_builder);
  }

  @Override public Unit matchIntegerUnsigned(
    final TIntegerUnsigned t)
  {
    return this.onInteger(t.getSizeInBits().getValue());
  }

  @Override public Unit matchIntegerSigned(
    final TIntegerSigned t)
  {
    return this.onInteger(t.getSizeInBits().getValue());
  }

  private Unit onInteger(final BigInteger size)
  {
    final String offset_constant =
      JPRAGeneratedNames.getOffsetConstantName(this.field.getName());
    final String getter_name =
      JPRAGeneratedNames.getGetterName(this.field.getName());
    final String setter_name =
      JPRAGeneratedNames.getSetterName(this.field.getName());

    final String iput;
    final String iget;
    final Class<?> itype;

    if (size.compareTo(BigInteger.valueOf(64L)) > 0) {
      throw new UnimplementedCodeException();
    }

    if (size.compareTo(BigInteger.valueOf(32L)) > 0) {
      itype = long.class;
      iput = "putLong";
      iget = "getLong";
    } else if (size.compareTo(BigInteger.valueOf(16L)) > 0) {
      itype = int.class;
      iput = "putInt";
      iget = "getInt";
    } else if (size.compareTo(BigInteger.valueOf(8L)) > 0) {
      itype = short.class;
      iput = "putShort";
      iget = "getShort";
    } else {
      itype = byte.class;
      iput = "put";
      iget = "get";
    }

    this.integerGetter(offset_constant, getter_name, iget, itype);
    this.integerSetter(offset_constant, setter_name, iput, itype);
    return Unit.unit();
  }

  private void integerSetter(
    final String offset_constant,
    final String setter_name,
    final String iput,
    final Class<?> itype)
  {
    final MethodSpec.Builder setb = MethodSpec.methodBuilder(setter_name);
    setb.addModifiers(Modifier.PUBLIC);
    setb.addAnnotation(Override.class);
    setb.addParameter(itype, "x", Modifier.FINAL);
    setb.returns(void.class);
    setb.addStatement(
      "this.$N.$N(this.getByteOffsetFor($N), $N)",
      "buffer",
      iput,
      offset_constant,
      "x");
    this.class_builder.addMethod(setb.build());
  }

  private void integerGetter(
    final String offset_constant,
    final String getter_name,
    final String iget,
    final Class<?> itype)
  {
    final MethodSpec.Builder getb = MethodSpec.methodBuilder(getter_name);
    getb.addModifiers(Modifier.PUBLIC);
    getb.addAnnotation(Override.class);
    getb.returns(itype);
    getb.addStatement(
      "return this.$N.$N(this.getByteOffsetFor($N))",
      "buffer",
      iget,
      offset_constant);
    this.class_builder.addMethod(getb.build());
  }

  @Override public Unit matchIntegerSignedNormalized(
    final TIntegerSignedNormalized t)
  {
    final BigInteger size = t.getSizeInBits().getValue();
    this.onIntegerNormalized(size, true);
    return Unit.unit();
  }

  private void onIntegerNormalized(
    final BigInteger size,
    final boolean signed)
  {
    final String offset_constant =
      JPRAGeneratedNames.getOffsetConstantName(this.field.getName());
    final String getter_norm_name =
      JPRAGeneratedNames.getNormalizedGetterName(this.field.getName());
    final String setter_norm_name =
      JPRAGeneratedNames.getNormalizedSetterName(this.field.getName());
    final String getter_norm_raw_name =
      JPRAGeneratedNames.getNormalizedRawGetterName(this.field.getName());
    final String setter_norm_raw_name =
      JPRAGeneratedNames.getNormalizedRawSetterName(this.field.getName());

    final Class<?> r_type;
    final Class<?> nfp_class;

    if (size.compareTo(BigInteger.valueOf(64L)) > 0) {
      throw new UnimplementedCodeException();
    }

    final String iput;
    final String iget;
    if (size.compareTo(BigInteger.valueOf(32L)) > 0) {
      r_type = long.class;
      if (signed) {
        nfp_class = NFPSignedDoubleLong.class;
      } else {
        nfp_class = NFPUnsignedDoubleLong.class;
      }
      iput = "putLong";
      iget = "getLong";
    } else if (size.compareTo(BigInteger.valueOf(16L)) > 0) {
      r_type = int.class;
      if (signed) {
        nfp_class = NFPSignedDoubleInt.class;
      } else {
        nfp_class = NFPUnsignedDoubleInt.class;
      }
      iput = "putInt";
      iget = "getInt";
    } else if (size.compareTo(BigInteger.valueOf(8L)) > 0) {
      r_type = short.class;
      if (signed) {
        nfp_class = NFPSignedDoubleInt.class;
      } else {
        nfp_class = NFPUnsignedDoubleInt.class;
      }
      iput = "putShort";
      iget = "getShort";
    } else {
      r_type = byte.class;
      if (signed) {
        nfp_class = NFPSignedDoubleInt.class;
      } else {
        nfp_class = NFPUnsignedDoubleInt.class;
      }
      iput = "put";
      iget = "get";
    }

    final String m_to;
    final String m_of;
    if (signed) {
      m_to = String.format("toSignedNormalizedWithZero%s", size);
      m_of = String.format("fromSignedNormalizedWithZero%s", size);
    } else {
      m_to = String.format("toUnsignedNormalized%s", size);
      m_of = String.format("fromUnsignedNormalized%s", size);
    }

    this.integerGetter(offset_constant, getter_norm_raw_name, iget, r_type);
    this.integerSetter(offset_constant, setter_norm_raw_name, iput, r_type);

    final MethodSpec.Builder getb = MethodSpec.methodBuilder(getter_norm_name);
    getb.addModifiers(Modifier.PUBLIC);
    getb.addAnnotation(Override.class);
    getb.returns(double.class);
    getb.addStatement(
      "return $T.$N(this.$N())", nfp_class, m_of, getter_norm_raw_name);
    this.class_builder.addMethod(getb.build());

    final MethodSpec.Builder setb = MethodSpec.methodBuilder(setter_norm_name);
    setb.addModifiers(Modifier.PUBLIC);
    setb.addAnnotation(Override.class);
    setb.addParameter(double.class, "x", Modifier.FINAL);
    setb.addStatement(
      "this.$N(($T) $T.$N($N))",
      setter_norm_raw_name,
      r_type,
      nfp_class,
      m_to,
      "x");
    this.class_builder.addMethod(setb.build());
  }

  @Override public Unit matchIntegerUnsignedNormalized(
    final TIntegerUnsignedNormalized t)
  {
    final BigInteger size = t.getSizeInBits().getValue();
    this.onIntegerNormalized(size, false);
    return Unit.unit();
  }
}
