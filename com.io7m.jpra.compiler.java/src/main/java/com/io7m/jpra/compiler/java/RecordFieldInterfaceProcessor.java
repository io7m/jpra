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

import com.io7m.jpra.model.contexts.PackageContextType;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.jpra.model.types.TArray;
import com.io7m.jpra.model.types.TBooleanSet;
import com.io7m.jpra.model.types.TFloat;
import com.io7m.jpra.model.types.TIntegerType;
import com.io7m.jpra.model.types.TMatrix;
import com.io7m.jpra.model.types.TPacked;
import com.io7m.jpra.model.types.TRecord;
import com.io7m.jpra.model.types.TString;
import com.io7m.jpra.model.types.TVector;
import com.io7m.jpra.model.types.TypeMatcherType;
import com.io7m.jpra.runtime.java.JPRAStringCursorReadableType;
import com.io7m.jpra.runtime.java.JPRAStringCursorType;
import com.io7m.junreachable.UnimplementedCodeException;
import com.io7m.junreachable.UnreachableCodeException;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import io.vavr.collection.List;

import javax.lang.model.element.Modifier;
import java.math.BigInteger;
import java.util.Objects;

/**
 * A type matcher that produces interface methods for record fields.
 */

final class RecordFieldInterfaceProcessor
  implements TypeMatcherType<Void, UnreachableCodeException>
{
  private final TRecord.FieldValue field;
  private final TypeSpec.Builder class_builder;
  private final MethodSelection methods;

  RecordFieldInterfaceProcessor(
    final TRecord.FieldValue in_field,
    final TypeSpec.Builder in_class_builder,
    final MethodSelection in_methods)
  {
    this.field = Objects.requireNonNull(in_field, "Field");
    this.class_builder = Objects.requireNonNull(
      in_class_builder,
      "Class builder");
    this.methods = Objects.requireNonNull(in_methods, "Methods");

    if (this.methods.wantGetters()) {
      this.metaMethods();
    }
  }

  private void metaMethods()
  {
    {
      final String getter_name =
        JPRAGeneratedNames.getMetaOffsetTypeReadableName(this.field.getName());
      final MethodSpec.Builder getb = MethodSpec.methodBuilder(getter_name);
      getb.addJavadoc(
        "@return The offset in octets of the {@code $L} field, from the start"
          + " of the type",
        this.field.getName());
      getb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
      getb.returns(int.class);
      this.class_builder.addMethod(getb.build());
    }

    {
      final String getter_name =
        JPRAGeneratedNames.getMetaOffsetCursorReadableName(
          this.field.getName());
      final MethodSpec.Builder getb = MethodSpec.methodBuilder(getter_name);
      getb.addJavadoc(
        "@return The offset in octets of the {@code $L} field, from the cursor",
        this.field.getName());
      getb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
      getb.returns(int.class);
      this.class_builder.addMethod(getb.build());
    }

    {
      final String getter_name =
        JPRAGeneratedNames.getMetaTypeGetName(this.field.getName());
      final MethodSpec.Builder getb = MethodSpec.methodBuilder(getter_name);
      getb.addJavadoc(
        "@return The type of the {@code $L} field",
        this.field.getName());
      getb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
      getb.returns(
        JPRAClasses.getModelTypeForType(this.field.getType()));
      this.class_builder.addMethod(getb.build());
    }
  }

  @Override
  public Void matchArray(final TArray t)
  {
    // TODO: Generated method stub!
    throw new UnimplementedCodeException();
  }

  @Override
  public Void matchString(final TString t)
  {
    if (this.methods.wantGetters()) {
      final String getter_name =
        JPRAGeneratedNames.getGetterStringReadableName(this.field.getName());

      final MethodSpec.Builder getb = MethodSpec.methodBuilder(getter_name);
      getb.addJavadoc(
        "@return Read-only access to the {@code $L} field",
        this.field.getName());
      getb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
      getb.returns(JPRAStringCursorReadableType.class);
      this.class_builder.addMethod(getb.build());
    }

    if (this.methods.wantSetters()) {
      final String setter_name =
        JPRAGeneratedNames.getGetterStringWritableName(this.field.getName());

      final MethodSpec.Builder setb = MethodSpec.methodBuilder(setter_name);
      setb.addJavadoc(
        "@return Writable access to the {@code $L} field",
        this.field.getName());
      setb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
      setb.returns(JPRAStringCursorType.class);
      this.class_builder.addMethod(setb.build());
    }

    return null;
  }

  @Override
  public Void matchBooleanSet(final TBooleanSet t)
  {
    final List<FieldName> ordered = t.getFieldsInDeclarationOrder();
    for (final FieldName f : ordered) {
      if (this.methods.wantGetters()) {
        final String getter_name =
          JPRAGeneratedNames.getGetterBooleanSetName(this.field.getName(), f);
        final MethodSpec.Builder getb = MethodSpec.methodBuilder(getter_name);
        getb.addJavadoc(
          "@return The value of field {@code $L} of the boolean set $L",
          f.value(),
          this.field.getName());
        getb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
        getb.returns(boolean.class);
        this.class_builder.addMethod(getb.build());
      }

      if (this.methods.wantSetters()) {
        final String setter_name =
          JPRAGeneratedNames.getSetterBooleanSetName(this.field.getName(), f);
        final MethodSpec.Builder setb = MethodSpec.methodBuilder(setter_name);
        setb.addJavadoc(
          "Set the value of field {@code $L} of the boolean set $L\n",
          f.value(),
          this.field.getName());
        setb.addJavadoc("@param x The new value");
        setb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
        setb.addParameter(boolean.class, "x", Modifier.FINAL);
        this.class_builder.addMethod(setb.build());
      }
    }
    return null;
  }

  @Override
  public Void matchInteger(final TIntegerType t)
  {
    final RecordFieldInterfaceIntegerProcessor p =
      new RecordFieldInterfaceIntegerProcessor(
        this.field, this.class_builder, this.methods);
    return t.matchTypeInteger(p);
  }

  @Override
  public Void matchFloat(final TFloat t)
  {
    final BigInteger size = t.getSizeInBits().getValue();

    final String getter_name =
      JPRAGeneratedNames.getGetterName(this.field.getName());
    final String setter_name =
      JPRAGeneratedNames.getSetterName(this.field.getName());

    if (size.compareTo(BigInteger.valueOf(64L)) > 0) {
      throw new UnimplementedCodeException();
    }
    if (size.compareTo(BigInteger.valueOf(16L)) < 0) {
      throw new UnimplementedCodeException();
    }

    /*
      Determine the types used for values in the interface.
     */

    final boolean pack;
    final Class<?> itype;
    if (size.compareTo(BigInteger.valueOf(32L)) > 0) {
      itype = double.class;
      pack = false;
    } else if (size.compareTo(BigInteger.valueOf(16L)) > 0) {
      itype = float.class;
      pack = false;
    } else {

      /*
        16-bit floating point types use a raw unsigned integer value.
       */

      itype = char.class;
      pack = true;
    }

    if (pack) {
      if (this.methods.wantGetters()) {
        final MethodSpec.Builder getb = MethodSpec.methodBuilder(getter_name);
        getb.addJavadoc(
          "@return The value of the {@code $L} field", this.field.getName());
        getb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
        getb.returns(double.class);
        this.class_builder.addMethod(getb.build());
      }
      if (this.methods.wantSetters()) {
        final MethodSpec.Builder setb = MethodSpec.methodBuilder(setter_name);
        setb.addJavadoc(
          "Set the value of the {@code $L} field\n", this.field.getName());
        setb.addJavadoc("@param x The new value");
        setb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
        setb.addParameter(double.class, "x", Modifier.FINAL);
        setb.returns(void.class);
        this.class_builder.addMethod(setb.build());
      }
    } else {
      if (this.methods.wantGetters()) {
        final MethodSpec.Builder getb = MethodSpec.methodBuilder(getter_name);
        getb.addJavadoc(
          "@return The value of the {@code $L} field", this.field.getName());
        getb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
        getb.returns(itype);
        this.class_builder.addMethod(getb.build());
      }
      if (this.methods.wantSetters()) {
        final MethodSpec.Builder setb = MethodSpec.methodBuilder(setter_name);
        setb.addJavadoc(
          "Set the value of the {@code $L} field\n", this.field.getName());
        setb.addJavadoc("@param x The new value");
        setb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
        setb.addParameter(itype, "x", Modifier.FINAL);
        setb.returns(void.class);
        this.class_builder.addMethod(setb.build());
      }
    }

    return null;
  }

  @Override
  public Void matchVector(final TVector t)
  {
    final JPRAClasses.VectorsClasses c = JPRAClasses.getVectorClassesFor(t);

    if (this.methods.wantGetters()) {
      final String getter_name =
        JPRAGeneratedNames.getGetterVectorReadableName(this.field.getName());

      final MethodSpec.Builder getb = MethodSpec.methodBuilder(getter_name);
      getb.addJavadoc(
        "@return Read-only access to the {@code $L} field",
        this.field.getName());
      getb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
      getb.returns(c.getBaseReadable());
      this.class_builder.addMethod(getb.build());
    }

    if (this.methods.wantSetters()) {
      final String setter_name =
        JPRAGeneratedNames.getGetterVectorWritableName(this.field.getName());

      final MethodSpec.Builder setb = MethodSpec.methodBuilder(setter_name);
      setb.addJavadoc(
        "@return Writable access to the {@code $L} field",
        this.field.getName());
      setb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
      setb.returns(c.getBaseInterface());
      this.class_builder.addMethod(setb.build());
    }

    return null;
  }

  @Override
  public Void matchMatrix(final TMatrix t)
  {
    final JPRAClasses.MatrixClasses c = JPRAClasses.getMatrixClassesFor(t);

    if (this.methods.wantGetters()) {
      final String getter_name =
        JPRAGeneratedNames.getGetterVectorReadableName(this.field.getName());

      final MethodSpec.Builder getb = MethodSpec.methodBuilder(getter_name);
      getb.addJavadoc(
        "@return Read-only access to the {@code $L} field",
        this.field.getName());
      getb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
      getb.returns(c.getBaseReadable());
      this.class_builder.addMethod(getb.build());
    }

    if (this.methods.wantSetters()) {
      final String setter_name =
        JPRAGeneratedNames.getGetterVectorWritableName(this.field.getName());

      final MethodSpec.Builder setb = MethodSpec.methodBuilder(setter_name);
      setb.addJavadoc(
        "@return Writable access to the {@code $L} field",
        this.field.getName());
      setb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
      setb.returns(c.getBaseInterface());
      this.class_builder.addMethod(setb.build());
    }

    return null;
  }

  @Override
  public Void matchRecord(final TRecord t)
  {
    this.recordOrPackedMethods(t.getName(), t.getPackageContext());
    return null;
  }

  /**
   * Generate methods that return a readable or writable reference to a given
   * field of type {@code packed} or {@code record}.
   *
   * @param t_name  The name of the field type
   * @param pkg_ctx The target package context
   */

  private void recordOrPackedMethods(
    final TypeName t_name,
    final PackageContextType pkg_ctx)
  {
    if (this.methods.wantGetters()) {
      final String getter_name =
        JPRAGeneratedNames.getGetterRecordReadableName(this.field.getName());

      final String target_pack = pkg_ctx.getName().toString();
      final String target_class =
        JPRAGeneratedNames.getRecordInterfaceReadableName(t_name);
      final ClassName target = ClassName.get(target_pack, target_class);

      final MethodSpec.Builder getb = MethodSpec.methodBuilder(getter_name);
      getb.addJavadoc(
        "@return Read-only access to the {@code $L} field",
        this.field.getName());
      getb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
      getb.returns(target);
      this.class_builder.addMethod(getb.build());
    }

    if (this.methods.wantSetters()) {
      final String setter_name =
        JPRAGeneratedNames.getGetterRecordWritableName(this.field.getName());

      final String target_pack = pkg_ctx.getName().toString();
      final String target_class =
        JPRAGeneratedNames.getRecordInterfaceWritableName(t_name);
      final ClassName target = ClassName.get(target_pack, target_class);

      final MethodSpec.Builder setb = MethodSpec.methodBuilder(setter_name);
      setb.addJavadoc(
        "@return Writable access to the {@code $L} field",
        this.field.getName());
      setb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
      setb.returns(target);
      this.class_builder.addMethod(setb.build());
    }
  }

  @Override
  public Void matchPacked(final TPacked t)
  {
    this.recordOrPackedMethods(t.getName(), t.getPackageContext());
    return null;
  }
}
