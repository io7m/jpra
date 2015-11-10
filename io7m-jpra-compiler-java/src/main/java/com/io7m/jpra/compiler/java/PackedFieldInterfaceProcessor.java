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
import com.io7m.junreachable.UnimplementedCodeException;
import com.io7m.junreachable.UnreachableCodeException;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.math.BigInteger;

/**
 * A type matcher that produces interface methods for packed fields.
 */

public final class PackedFieldInterfaceProcessor
  implements TypeMatcherType<Unit, UnreachableCodeException>,
  TypeIntegerMatcherType<Unit, UnreachableCodeException>
{
  private final TPacked.FieldValue field;
  private final TypeSpec.Builder   class_builder;
  private final MethodSelection    methods;

  PackedFieldInterfaceProcessor(
    final TPacked.FieldValue in_field,
    final TypeSpec.Builder in_class_builder,
    final MethodSelection in_methods)
  {
    this.field = NullCheck.notNull(in_field);
    this.class_builder = NullCheck.notNull(in_class_builder);
    this.methods = NullCheck.notNull(in_methods);
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

  @Override public Unit matchIntegerSignedNormalized(
    final TIntegerSignedNormalized t)
  {
    return this.onIntegerNormalized(t.getSizeInBits().getValue());
  }

  @Override public Unit matchIntegerUnsignedNormalized(
    final TIntegerUnsignedNormalized t)
  {
    return this.onIntegerNormalized(t.getSizeInBits().getValue());
  }

  private Unit onInteger(final BigInteger size)
  {
    final String getter_name =
      JPRAGeneratedNames.getGetterName(this.field.getName());
    final String setter_name =
      JPRAGeneratedNames.getSetterName(this.field.getName());

    if (size.compareTo(BigInteger.valueOf(64L)) > 0) {
      throw new UnimplementedCodeException();
    }

    final Class<?> itype;
    if (size.compareTo(BigInteger.valueOf(32L)) > 0) {
      itype = long.class;
    } else {
      itype = int.class;
    }

    if (this.methods.wantGetters()) {
      final MethodSpec.Builder getb = MethodSpec.methodBuilder(getter_name);
      getb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
      getb.returns(itype);
      this.class_builder.addMethod(getb.build());
    }

    if (this.methods.wantSetters()) {
      final MethodSpec.Builder setb = MethodSpec.methodBuilder(setter_name);
      setb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
      setb.addParameter(itype, "x", Modifier.FINAL);
      setb.returns(void.class);
      this.class_builder.addMethod(setb.build());
    }
    return Unit.unit();
  }

  private Unit onIntegerNormalized(
    final BigInteger size)
  {
    final String getter_norm_name =
      JPRAGeneratedNames.getNormalizedGetterName(this.field.getName());
    final String setter_norm_name =
      JPRAGeneratedNames.getNormalizedSetterName(this.field.getName());
    final String getter_norm_raw_name =
      JPRAGeneratedNames.getNormalizedRawGetterName(this.field.getName());
    final String setter_norm_raw_name =
      JPRAGeneratedNames.getNormalizedRawSetterName(this.field.getName());

    if (size.compareTo(BigInteger.valueOf(64L)) > 0) {
      throw new UnimplementedCodeException();
    }

    final Class<?> itype;
    if (size.compareTo(BigInteger.valueOf(32L)) > 0) {
      itype = long.class;
    } else {
      itype = int.class;
    }

    if (this.methods.wantGetters()) {
      {
        final MethodSpec.Builder getb =
          MethodSpec.methodBuilder(getter_norm_name);
        getb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
        getb.returns(double.class);
        this.class_builder.addMethod(getb.build());
      }

      {
        final MethodSpec.Builder getb =
          MethodSpec.methodBuilder(getter_norm_raw_name);
        getb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
        getb.returns(itype);
        this.class_builder.addMethod(getb.build());
      }
    }

    if (this.methods.wantSetters()) {
      {
        final MethodSpec.Builder setb =
          MethodSpec.methodBuilder(setter_norm_name);
        setb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
        setb.addParameter(double.class, "x", Modifier.FINAL);
        this.class_builder.addMethod(setb.build());
      }

      {
        final MethodSpec.Builder setb =
          MethodSpec.methodBuilder(setter_norm_raw_name);
        setb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
        setb.addParameter(itype, "x", Modifier.FINAL);
        setb.returns(void.class);
        this.class_builder.addMethod(setb.build());
      }
    }

    return Unit.unit();
  }
}
