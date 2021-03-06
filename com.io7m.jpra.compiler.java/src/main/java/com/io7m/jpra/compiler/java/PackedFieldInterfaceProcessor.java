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

import com.io7m.jpra.model.names.FieldName;
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
import io.vavr.collection.List;

import javax.lang.model.element.Modifier;
import java.math.BigInteger;
import java.util.Objects;

/**
 * A type matcher that produces interface methods for packed fields.
 */

public final class PackedFieldInterfaceProcessor
  implements TypeMatcherType<Void, UnreachableCodeException>,
  TypeIntegerMatcherType<Void, UnreachableCodeException>
{
  private final TPacked.FieldValue field;
  private final TypeSpec.Builder class_builder;
  private final MethodSelection methods;

  PackedFieldInterfaceProcessor(
    final TPacked.FieldValue in_field,
    final TypeSpec.Builder in_class_builder,
    final MethodSelection in_methods)
  {
    this.field = Objects.requireNonNull(in_field, "Field");
    this.class_builder = Objects.requireNonNull(
      in_class_builder,
      "Class builder");
    this.methods = Objects.requireNonNull(in_methods, "Methods");
  }

  /**
   * Retrieve the type that should be used for the interfaces of non-normalized integers fields in
   * packed types. Generally, this is the smallest integer size larger than or equal to {@code int}
   * that can hold values of the given size.
   *
   * @param size The size in bits of the integer type
   *
   * @return A type
   */

  public static Class<?> getPackedIntegerTypeForSize(final BigInteger size)
  {
    if (size.compareTo(BigInteger.valueOf(64L)) > 0) {
      throw new UnimplementedCodeException();
    }

    final Class<?> itype;
    if (size.compareTo(BigInteger.valueOf(32L)) > 0) {
      itype = long.class;
    } else {
      itype = int.class;
    }
    return itype;
  }

  /**
   * Generate a {@code set} method that sets all packed fields at once.
   *
   * @param class_builder The class builder
   * @param ordered       The set of fields in the type
   */

  public static void generatedPackedAllMethodInterface(
    final TypeSpec.Builder class_builder,
    final List<TPacked.FieldType> ordered)
  {
    final MethodSpec.Builder setb = MethodSpec.methodBuilder("set");
    setb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
    setb.addJavadoc("Set the value of all fields.\n");

    for (final TPacked.FieldType f : ordered) {
      f.matchField(new FieldJavaDocAdder(setb));
    }

    for (final TPacked.FieldType f : ordered) {
      f.matchField(new PackedFieldTypeFinder(setb));
    }

    setb.returns(void.class);
    class_builder.addMethod(setb.build());
  }

  @Override
  public Void matchArray(final TArray t)
  {
    throw new UnreachableCodeException();
  }

  @Override
  public Void matchString(final TString t)
  {
    throw new UnreachableCodeException();
  }

  @Override
  public Void matchBooleanSet(final TBooleanSet t)
  {
    throw new UnreachableCodeException();
  }

  @Override
  public Void matchInteger(final TIntegerType t)
  {
    return t.matchTypeInteger(this);
  }

  @Override
  public Void matchFloat(final TFloat t)
  {
    throw new UnreachableCodeException();
  }

  @Override
  public Void matchVector(final TVector t)
  {
    throw new UnreachableCodeException();
  }

  @Override
  public Void matchMatrix(final TMatrix t)
  {
    throw new UnreachableCodeException();
  }

  @Override
  public Void matchRecord(final TRecord t)
  {
    throw new UnreachableCodeException();
  }

  @Override
  public Void matchPacked(final TPacked t)
  {
    throw new UnreachableCodeException();
  }

  @Override
  public Void matchIntegerUnsigned(
    final TIntegerUnsigned t)
  {
    return this.onInteger(t.getSizeInBits().getValue());
  }

  @Override
  public Void matchIntegerSigned(
    final TIntegerSigned t)
  {
    return this.onInteger(t.getSizeInBits().getValue());
  }

  @Override
  public Void matchIntegerSignedNormalized(
    final TIntegerSignedNormalized t)
  {
    return this.onIntegerNormalized(t.getSizeInBits().getValue());
  }

  @Override
  public Void matchIntegerUnsignedNormalized(
    final TIntegerUnsignedNormalized t)
  {
    return this.onIntegerNormalized(t.getSizeInBits().getValue());
  }

  private Void onInteger(final BigInteger size)
  {
    final String getter_name =
      JPRAGeneratedNames.getGetterName(this.field.getName());
    final String setter_name =
      JPRAGeneratedNames.getSetterName(this.field.getName());

    final Class<?> itype =
      getPackedIntegerTypeForSize(size);

    if (this.methods.wantGetters()) {
      final MethodSpec.Builder getb = MethodSpec.methodBuilder(getter_name);
      getb.addJavadoc("@return The value of the {@code $L} field", this.field.getName());
      getb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
      getb.returns(itype);
      this.class_builder.addMethod(getb.build());
    }

    if (this.methods.wantSetters()) {
      final MethodSpec.Builder setb = MethodSpec.methodBuilder(setter_name);
      setb.addJavadoc("Set the value of the {@code $L} field.\n", this.field.getName());
      setb.addJavadoc(
        "The $L least significant bits of {@code x} will be used.\n",
        this.field.getSize().getValue());
      setb.addJavadoc("@param x The new value");
      setb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
      setb.addParameter(itype, "x", Modifier.FINAL);
      setb.returns(void.class);
      this.class_builder.addMethod(setb.build());
    }
    return null;
  }

  private Void onIntegerNormalized(
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

    final Class<?> itype =
      getPackedIntegerTypeForSize(
        size);

    if (this.methods.wantGetters()) {
      {
        final MethodSpec.Builder getb =
          MethodSpec.methodBuilder(getter_norm_name);
        getb.addJavadoc("@return The value of the {@code $L} field", this.field.getName());
        getb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
        getb.returns(double.class);
        this.class_builder.addMethod(getb.build());
      }

      {
        final MethodSpec.Builder getb =
          MethodSpec.methodBuilder(getter_norm_raw_name);
        getb.addJavadoc("@return The value of the {@code $L} field", this.field.getName());
        getb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
        getb.returns(itype);
        this.class_builder.addMethod(getb.build());
      }
    }

    if (this.methods.wantSetters()) {
      {
        final MethodSpec.Builder setb =
          MethodSpec.methodBuilder(setter_norm_name);
        setb.addJavadoc(
          "Set the value of the {@code $L} field.\n", this.field.getName());
        setb.addJavadoc("@param x The new value (in the range {@code [0, 1]})");
        setb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
        setb.addParameter(double.class, "x", Modifier.FINAL);
        this.class_builder.addMethod(setb.build());
      }

      {
        final MethodSpec.Builder setb =
          MethodSpec.methodBuilder(setter_norm_raw_name);
        setb.addJavadoc("Set the value of the {@code $L} field.\n", this.field.getName());
        setb.addJavadoc(
          "The $L least significant bits of {@code x} will be used.\n",
          this.field.getSize().getValue());
        setb.addJavadoc("@param x The new value");
        setb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
        setb.addParameter(itype, "x", Modifier.FINAL);
        setb.returns(void.class);
        this.class_builder.addMethod(setb.build());
      }
    }

    return null;
  }

  private static final class FieldJavaDocAdder
    implements TPacked.FieldMatcherType<Void, UnreachableCodeException>
  {
    private final MethodSpec.Builder builder;

    FieldJavaDocAdder(final MethodSpec.Builder setb)
    {
      this.builder = setb;
    }

    @Override
    public Void matchFieldValue(final TPacked.FieldValue f)
    {
      final FieldName f_name = f.getName();
      this.builder.addJavadoc("@param $L The value for field {@code $L}\n", f_name, f_name);
      return null;
    }

    @Override
    public Void matchFieldPaddingBits(final TPacked.FieldPaddingBits f)
    {
      return null;
    }
  }

  private static final class PackedFieldTypeFinder
    implements TPacked.FieldMatcherType<Void, UnreachableCodeException>
  {
    private final MethodSpec.Builder builder;

    PackedFieldTypeFinder(final MethodSpec.Builder setb)
    {
      this.builder = setb;
    }

    @Override
    public Void matchFieldValue(final TPacked.FieldValue f)
    {
      final BigInteger f_size = f.getSize().getValue();
      final FieldName f_name = f.getName();
      final Class<?> f_type = f.getType().matchTypeInteger(new PackedIntegerTypeFinder(f_size));

      this.builder.addParameter(f_type, f_name.value(), Modifier.FINAL);
      return null;
    }

    @Override
    public Void matchFieldPaddingBits(final TPacked.FieldPaddingBits f)
    {
      return null;
    }

    private static final class PackedIntegerTypeFinder
      implements TypeIntegerMatcherType<Class<?>, UnreachableCodeException>
    {
      private final BigInteger f_size;

      PackedIntegerTypeFinder(final BigInteger size)
      {
        this.f_size = size;
      }

      @Override
      public Class<?> matchIntegerUnsigned(
        final TIntegerUnsigned t)
      {
        return getPackedIntegerTypeForSize(this.f_size);
      }

      @Override
      public Class<?> matchIntegerSigned(
        final TIntegerSigned t)
      {
        return getPackedIntegerTypeForSize(this.f_size);
      }

      @Override
      public Class<?> matchIntegerSignedNormalized(
        final TIntegerSignedNormalized t)
      {
        return double.class;
      }

      @Override
      public Class<?> matchIntegerUnsignedNormalized(
        final TIntegerUnsignedNormalized t)
      {
        return double.class;
      }
    }
  }
}
