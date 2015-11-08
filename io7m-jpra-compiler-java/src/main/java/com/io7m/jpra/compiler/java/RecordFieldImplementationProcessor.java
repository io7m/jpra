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

import com.gs.collections.api.list.ImmutableList;
import com.io7m.ieee754b16.Binary16;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
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
import com.io7m.junreachable.UnimplementedCodeException;
import com.io7m.junreachable.UnreachableCodeException;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.math.BigInteger;

/**
 * A type matcher that produces implementation methods for a given record
 * field.
 */

final class RecordFieldImplementationProcessor
  implements TypeMatcherType<Unit, UnreachableCodeException>
{
  private final TRecord.FieldValue field;
  private final BigInteger         offset;
  private final TypeSpec.Builder   class_builder;

  RecordFieldImplementationProcessor(
    final TRecord.FieldValue in_field,
    final BigInteger in_offset,
    final TypeSpec.Builder in_class_builder)
  {
    this.field = NullCheck.notNull(in_field);
    this.offset = NullCheck.notNull(in_offset);
    this.class_builder = NullCheck.notNull(in_class_builder);
  }

  @Override public Unit matchArray(
    final TArray t)
  {
    this.start();
    // TODO: Generated method stub!
    throw new UnimplementedCodeException();
  }

  @Override public Unit matchString(
    final TString t)
  {
    this.start();

    // TODO: Generated method stub!
    throw new UnimplementedCodeException();
  }

  @Override public Unit matchBooleanSet(
    final TBooleanSet t)
  {
    this.start();

    final ImmutableList<FieldName> ordered = t.getFieldsInDeclarationOrder();
    for (int index = 0; index < ordered.size(); ++index) {
      final FieldName f = ordered.get(index);

      final int octet = index / 8;
      final int bit = index % 8;

      final String getter_name =
        JPRAGeneratedNames.getGetterBooleanSetName(this.field.getName(), f);
      final String offset_name = JPRAGeneratedNames.getOffsetConstantName(
        this.field.getName());

      final String bin =
        String.format("0b%8s", Integer.toBinaryString(1 << bit))
          .replace(" ", "0");

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
      this.class_builder.addMethod(getb.build());

      final String setter_name =
        JPRAGeneratedNames.getGetterBooleanSetName(this.field.getName(), f);
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
      this.class_builder.addMethod(setb.build());
    }
    return Unit.unit();
  }

  @Override public Unit matchInteger(
    final TIntegerType t)
  {
    this.start();
    final RecordFieldImplementationIntegerProcessor p =
      new RecordFieldImplementationIntegerProcessor(
        this.field, this.class_builder);
    return t.matchTypeInteger(p);
  }

  @Override public Unit matchFloat(
    final TFloat t)
  {
    this.start();

    final BigInteger size = t.getSizeInBits().getValue();

    final String offset_constant =
      JPRAGeneratedNames.getOffsetConstantName(this.field.getName());
    final String getter_name =
      JPRAGeneratedNames.getGetterName(this.field.getName());
    final String setter_name =
      JPRAGeneratedNames.getSetterName(this.field.getName());

    final String iput;
    final String iget;
    final Class<?> itype;
    final boolean pack;

    if (size.compareTo(BigInteger.valueOf(64L)) > 0) {
      throw new UnimplementedCodeException();
    }
    if (size.compareTo(BigInteger.valueOf(16L)) < 0) {
      throw new UnimplementedCodeException();
    }

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

    if (pack) {
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
      this.class_builder.addMethod(getb.build());

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
      this.class_builder.addMethod(setb.build());
    } else {
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

    return Unit.unit();
  }

  @Override public Unit matchVector(
    final TVector t)
  {
    this.start();

    // TODO: Generated method stub!
    throw new UnimplementedCodeException();
  }

  @Override public Unit matchMatrix(
    final TMatrix t)
  {
    this.start();

    // TODO: Generated method stub!
    throw new UnimplementedCodeException();
  }

  @Override public Unit matchRecord(
    final TRecord t)
  {
    this.start();
    this.recordOrPackedMethods(t.getName(), t.getPackageContext());
    return Unit.unit();
  }

  private void recordOrPackedMethods(
    final TypeName t_name,
    final PackageContextType tp_ctx)
  {
    final String reader_name =
      JPRAGeneratedNames.getGetterRecordReadableName(t_name);
    final String writer_name =
      JPRAGeneratedNames.getGetterRecordWritableName(t_name);

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

  @Override public Unit matchPacked(
    final TPacked t)
  {
    this.start();
    this.recordOrPackedMethods(t.getName(), t.getPackageContext());
    return Unit.unit();
  }

  private void start()
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
