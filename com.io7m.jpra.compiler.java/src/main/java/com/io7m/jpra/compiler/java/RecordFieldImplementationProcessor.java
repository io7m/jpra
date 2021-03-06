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

import com.io7m.ieee754b16.Binary16;
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
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import io.vavr.collection.List;

import javax.lang.model.element.Modifier;
import java.math.BigInteger;
import java.util.Objects;

/**
 * A type matcher that produces implementation methods for a given record field.
 */

final class RecordFieldImplementationProcessor
  implements TypeMatcherType<Void, UnreachableCodeException>
{
  private final TRecord.FieldValue field;
  private final BigInteger offset;
  private final TypeSpec.Builder class_builder;

  RecordFieldImplementationProcessor(
    final TRecord.FieldValue in_field,
    final BigInteger in_offset,
    final TypeSpec.Builder in_class_builder)
  {
    this.field = Objects.requireNonNull(in_field, "Field");
    this.offset = Objects.requireNonNull(in_offset, "Offset");
    this.class_builder = Objects.requireNonNull(
      in_class_builder,
      "Class builder");

    this.metaMethods();
  }

  private void metaMethods()
  {
    {
      final String getter_name =
        JPRAGeneratedNames.getMetaOffsetTypeReadableName(this.field.getName());
      final String offset_constant =
        JPRAGeneratedNames.getOffsetConstantName(this.field.getName());

      final MethodSpec.Builder getb = MethodSpec.methodBuilder(getter_name);
      getb.addModifiers(Modifier.PUBLIC);
      getb.addAnnotation(Override.class);
      getb.addStatement("return $N", offset_constant);
      getb.returns(int.class);
      this.class_builder.addMethod(getb.build());
    }

    {
      final String getter_name =
        JPRAGeneratedNames.getMetaOffsetStaticTypeReadableName(
          this.field.getName());
      final String offset_constant =
        JPRAGeneratedNames.getOffsetConstantName(this.field.getName());

      final MethodSpec.Builder getb = MethodSpec.methodBuilder(getter_name);

      {
        final StringBuilder sb = new StringBuilder(128);
        sb.append("@return The offset in octets of field ");
        sb.append("{@code ");
        sb.append(this.field.getName().value());
        sb.append("} from the start of the type\n");
        getb.addJavadoc(sb.toString());
      }

      getb.addModifiers(Modifier.PUBLIC);
      getb.addModifiers(Modifier.STATIC);
      getb.addStatement("return $N", offset_constant);
      getb.returns(int.class);
      this.class_builder.addMethod(getb.build());
    }

    {
      final String getter_name =
        JPRAGeneratedNames.getMetaOffsetCursorReadableName(this.field.getName());
      final String offset_constant =
        JPRAGeneratedNames.getOffsetConstantName(this.field.getName());

      final MethodSpec.Builder getb = MethodSpec.methodBuilder(getter_name);
      getb.addModifiers(Modifier.PUBLIC);
      getb.addAnnotation(Override.class);
      getb.addStatement("return $N + this.$N", offset_constant, "base_offset");
      getb.returns(int.class);
      this.class_builder.addMethod(getb.build());
    }

    {
      final String getter_name =
        JPRAGeneratedNames.getMetaTypeGetName(this.field.getName());
      final String field_name =
        JPRAGeneratedNames.getMetaTypeFieldName(this.field.getName());

      final MethodSpec.Builder getb = MethodSpec.methodBuilder(getter_name);
      getb.addModifiers(Modifier.PUBLIC);
      getb.addAnnotation(Override.class);
      getb.addStatement("return this.$N", field_name);
      getb.returns(
        JPRAClasses.getModelTypeForType(this.field.getType()));
      this.class_builder.addMethod(getb.build());
    }
  }

  @Override
  public Void matchArray(
    final TArray t)
  {
    this.generateFieldOffsetConstant();
    // TODO: Generated method stub!
    throw new UnimplementedCodeException();
  }

  @Override
  public Void matchString(
    final TString t)
  {
    this.generateFieldOffsetConstant();

    final String reader_name =
      JPRAGeneratedNames.getGetterStringReadableName(this.field.getName());
    final String writer_name =
      JPRAGeneratedNames.getGetterStringWritableName(this.field.getName());

    final String f_name = JPRAGeneratedNames.getFieldName(this.field.getName());

    final MethodSpec.Builder read_b = MethodSpec.methodBuilder(reader_name);
    read_b.addModifiers(Modifier.PUBLIC);
    read_b.addAnnotation(Override.class);
    read_b.returns(JPRAStringCursorReadableType.class);
    read_b.addStatement("return this.$N", f_name);
    this.class_builder.addMethod(read_b.build());

    final MethodSpec.Builder write_b = MethodSpec.methodBuilder(writer_name);
    write_b.addModifiers(Modifier.PUBLIC);
    write_b.addAnnotation(Override.class);
    write_b.returns(JPRAStringCursorType.class);
    write_b.addStatement("return this.$N", f_name);
    this.class_builder.addMethod(write_b.build());

    return null;
  }

  @Override
  public Void matchBooleanSet(
    final TBooleanSet t)
  {
    this.generateFieldOffsetConstant();

    /*
      Generate a get and set method for each field of the boolean set.
     */

    final List<FieldName> ordered = t.getFieldsInDeclarationOrder();
    for (int index = 0; index < ordered.size(); ++index) {
      final FieldName f = ordered.get(index);

      final int octet = index / 8;
      final int bit = 7 - (index % 8);
      final String bin =
        String.format("0b%8s", Integer.toBinaryString(1 << bit))
          .replace(" ", "0");

      final String offset_name = JPRAGeneratedNames.getOffsetConstantName(this.field.getName());
      this.class_builder.addMethod(booleanGetter(f, octet, bin, offset_name, this.field));
      this.class_builder.addMethod(booleanSetter(f, octet, bin, offset_name, this.field));
    }
    return null;
  }

  private static MethodSpec booleanSetter(
    final FieldName f,
    final int octet,
    final String bin,
    final String offset_name,
    final TRecord.FieldValue field)
  {
    final String setter_name =
      JPRAGeneratedNames.getSetterBooleanSetName(field.getName(), f);

    final MethodSpec.Builder setb = MethodSpec.methodBuilder(setter_name);
    setb.addAnnotation(Override.class);
    setb.addModifiers(Modifier.PUBLIC);
    setb.addParameter(boolean.class, "x", Modifier.FINAL);
    setb.addStatement(
      "final int i = ((int) this.getByteOffsetFor($N)) + $L",
      offset_name,
      Integer.valueOf(octet));
    setb.addStatement("final int k = this.buffer.get(i)");
    setb.addStatement("final int q");
    setb.beginControlFlow("if (x)");
    setb.addStatement("q = k | $L", bin);
    setb.nextControlFlow("else");
    setb.addStatement("q = k & ~$L", bin);
    setb.endControlFlow();
    setb.addStatement("this.buffer.put(i, (byte) (q & 0xff))");
    return setb.build();
  }

  private static MethodSpec booleanGetter(
    final FieldName f,
    final int octet,
    final String bin,
    final String offset_name,
    final TRecord.FieldValue field)
  {
    final String getter_name =
      JPRAGeneratedNames.getGetterBooleanSetName(field.getName(), f);

    final MethodSpec.Builder getb = MethodSpec.methodBuilder(getter_name);
    getb.addAnnotation(Override.class);
    getb.addModifiers(Modifier.PUBLIC);
    getb.returns(boolean.class);
    getb.addStatement(
      "final int i = ((int) this.getByteOffsetFor($N)) + $L",
      offset_name,
      Integer.valueOf(octet));
    getb.addStatement("final int k = this.buffer.get(i)");
    getb.addStatement("return (k & $L) == $L", bin, bin);
    return getb.build();
  }

  @Override
  public Void matchInteger(
    final TIntegerType t)
  {
    this.generateFieldOffsetConstant();
    final RecordFieldImplementationIntegerProcessor p =
      new RecordFieldImplementationIntegerProcessor(
        this.field, this.class_builder);
    return t.matchTypeInteger(p);
  }

  @Override
  public Void matchFloat(
    final TFloat t)
  {
    this.generateFieldOffsetConstant();

    final BigInteger size = t.getSizeInBits().getValue();

    final String offset_constant =
      JPRAGeneratedNames.getOffsetConstantName(this.field.getName());
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
      Determine the type and methods used to put/get values to/from the
      underlying byte buffer.
     */

    final boolean pack;
    final Class<?> itype;
    final String iget;
    final String iput;
    if (size.compareTo(BigInteger.valueOf(32L)) > 0) {
      itype = double.class;
      iput = "putDouble";
      iget = "getDouble";
      pack = false;
    } else if (size.compareTo(BigInteger.valueOf(16L)) > 0) {
      itype = float.class;
      iput = "putFloat";
      iget = "getFloat";
      pack = false;
    } else {
      itype = char.class;
      iput = "putChar";
      iget = "getChar";
      pack = true;
    }

    /*
      Some floating point sizes require packing into integers, as they
      have no direct representation in Java.
     */

    if (pack) {
      this.class_builder.addMethod(packedFloatGetter(offset_constant, getter_name, iget));
      this.class_builder.addMethod(packedFloatSetter(offset_constant, setter_name, iput));
    } else {
      this.class_builder.addMethod(unpackedFloatGetter(offset_constant, getter_name, itype, iget));
      this.class_builder.addMethod(unpackedFloatSetter(offset_constant, setter_name, itype, iput));
    }

    return null;
  }

  /**
   * Generate a method to insert floating point values into the byte buffer.
   */

  private static MethodSpec unpackedFloatSetter(
    final String offset_constant,
    final String setter_name,
    final Class<?> itype,
    final String iput)
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
    return setb.build();
  }

  /**
   * Generate a method to retrieve floating point values from the byte buffer.
   */

  private static MethodSpec unpackedFloatGetter(
    final String offset_constant,
    final String getter_name,
    final Class<?> itype,
    final String iget)
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
    return getb.build();
  }

  /**
   * Generate a method to pack values into the byte buffer.
   */

  private static MethodSpec packedFloatSetter(
    final String offset_constant,
    final String setter_name,
    final String iput)
  {
    final MethodSpec.Builder setb = MethodSpec.methodBuilder(setter_name);
    setb.addModifiers(Modifier.PUBLIC);
    setb.addAnnotation(Override.class);
    setb.addParameter(double.class, "x", Modifier.FINAL);
    setb.returns(void.class);
    setb.addStatement(
      "this.$N.$N(this.getByteOffsetFor($N), $T.packDouble($N))",
      "buffer",
      iput,
      offset_constant,
      Binary16.class,
      "x");
    return setb.build();
  }

  /**
   * Generate a method to unpack values from the byte buffer.
   */

  private static MethodSpec packedFloatGetter(
    final String offset_constant,
    final String getter_name,
    final String iget)
  {
    final MethodSpec.Builder getb = MethodSpec.methodBuilder(getter_name);
    getb.addModifiers(Modifier.PUBLIC);
    getb.addAnnotation(Override.class);
    getb.returns(double.class);
    getb.addStatement(
      "return $T.unpackDouble($N.$N(this.getByteOffsetFor($N)))",
      Binary16.class,
      "buffer",
      iget,
      offset_constant);
    return getb.build();
  }

  @Override
  public Void matchVector(
    final TVector t)
  {
    this.generateFieldOffsetConstant();

    final JPRAClasses.VectorsClasses c = JPRAClasses.getVectorClassesFor(t);

    final String reader_name =
      JPRAGeneratedNames.getGetterVectorReadableName(this.field.getName());
    final String writer_name =
      JPRAGeneratedNames.getGetterVectorWritableName(this.field.getName());

    final String f_name = JPRAGeneratedNames.getFieldName(this.field.getName());

    final MethodSpec.Builder read_b = MethodSpec.methodBuilder(reader_name);
    read_b.addModifiers(Modifier.PUBLIC);
    read_b.addAnnotation(Override.class);
    read_b.returns(c.getBaseReadable());
    read_b.addStatement("return this.$N", f_name);
    this.class_builder.addMethod(read_b.build());

    final MethodSpec.Builder write_b = MethodSpec.methodBuilder(writer_name);
    write_b.addModifiers(Modifier.PUBLIC);
    write_b.addAnnotation(Override.class);
    write_b.returns(c.getBaseInterface());
    write_b.addStatement("return this.$N", f_name);
    this.class_builder.addMethod(write_b.build());

    return null;
  }

  @Override
  public Void matchMatrix(
    final TMatrix t)
  {
    this.generateFieldOffsetConstant();

    final JPRAClasses.MatrixClasses c = JPRAClasses.getMatrixClassesFor(t);

    final String reader_name =
      JPRAGeneratedNames.getGetterMatrixReadableName(this.field.getName());
    final String writer_name =
      JPRAGeneratedNames.getGetterMatrixWritableName(this.field.getName());
    final String f_name =
      JPRAGeneratedNames.getFieldName(this.field.getName());

    final MethodSpec.Builder read_b = MethodSpec.methodBuilder(reader_name);
    read_b.addModifiers(Modifier.PUBLIC);
    read_b.addAnnotation(Override.class);
    read_b.returns(c.getBaseReadable());
    read_b.addStatement("return this.$N", f_name);
    this.class_builder.addMethod(read_b.build());

    final MethodSpec.Builder write_b = MethodSpec.methodBuilder(writer_name);
    write_b.addModifiers(Modifier.PUBLIC);
    write_b.addAnnotation(Override.class);
    write_b.returns(c.getBaseInterface());
    write_b.addStatement("return this.$N", f_name);
    this.class_builder.addMethod(write_b.build());

    return null;
  }

  @Override
  public Void matchRecord(
    final TRecord t)
  {
    this.generateFieldOffsetConstant();
    this.recordOrPackedMethods(t.getName(), t.getPackageContext());
    return null;
  }

  /**
   * Generate methods that return a readable or writable reference to a given field of type {@code
   * packed} or {@code record}.
   *
   * @param t_name The name of the field type
   * @param tp_ctx The target package context
   */

  private void recordOrPackedMethods(
    final TypeName t_name,
    final PackageContextType tp_ctx)
  {
    final String reader_name =
      JPRAGeneratedNames.getGetterRecordReadableName(this.field.getName());
    final String writer_name =
      JPRAGeneratedNames.getGetterRecordWritableName(this.field.getName());

    final String target_pack = tp_ctx.getName().toString();
    final String target_class_read =
      JPRAGeneratedNames.getRecordInterfaceReadableName(t_name);
    final ClassName target_read = ClassName.get(target_pack, target_class_read);

    final String target_class_write =
      JPRAGeneratedNames.getRecordInterfaceName(t_name);
    final ClassName target_write =
      ClassName.get(target_pack, target_class_write);

    final String f_name = JPRAGeneratedNames.getFieldName(this.field.getName());

    final MethodSpec.Builder read_b = MethodSpec.methodBuilder(reader_name);
    read_b.addModifiers(Modifier.PUBLIC);
    read_b.addAnnotation(Override.class);
    read_b.returns(target_read);
    read_b.addStatement("return this.$N", f_name);
    this.class_builder.addMethod(read_b.build());

    final MethodSpec.Builder write_b = MethodSpec.methodBuilder(writer_name);
    write_b.addModifiers(Modifier.PUBLIC);
    write_b.addAnnotation(Override.class);
    write_b.returns(target_write);
    write_b.addStatement("return this.$N", f_name);
    this.class_builder.addMethod(write_b.build());
  }

  @Override
  public Void matchPacked(
    final TPacked t)
  {
    this.generateFieldOffsetConstant();
    this.recordOrPackedMethods(t.getName(), t.getPackageContext());
    return null;
  }

  /**
   * Generate a static constant indicating the offset in octets of the field from the start of the
   * type.
   */

  private void generateFieldOffsetConstant()
  {
    final FieldSpec.Builder fb = FieldSpec.builder(
      int.class,
      JPRAGeneratedNames.getOffsetConstantName(this.field.getName()),
      Modifier.FINAL,
      Modifier.STATIC,
      Modifier.PRIVATE);
    fb.initializer(this.offset.toString());
    this.class_builder.addField(fb.build());
  }
}
