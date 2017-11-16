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
import com.io7m.jpra.model.names.PackageNameQualified;
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
import com.io7m.jpra.runtime.java.JPRAStringCursorByteBuffered;
import com.io7m.jpra.runtime.java.JPRAStringCursorType;
import com.io7m.jpra.runtime.java.JPRATypeModel;
import com.io7m.junreachable.UnreachableCodeException;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * A type matcher that produces constructor field assignment statements for a
 * given record field.
 */

final class RecordFieldImplementationConstructorProcessor
  implements TypeMatcherType<Void, UnreachableCodeException>
{
  private final TRecord.FieldValue field;
  private final TypeSpec.Builder class_builder;
  private final MethodSpec.Builder constructor_builder;

  RecordFieldImplementationConstructorProcessor(
    final TRecord.FieldValue in_field,
    final TypeSpec.Builder in_class_builder,
    final MethodSpec.Builder in_constructor_builder)
  {
    this.field = Objects.requireNonNull(in_field, "Field");
    this.class_builder = Objects.requireNonNull(
      in_class_builder,
      "Class builder");
    this.constructor_builder = Objects.requireNonNull(
      in_constructor_builder,
      "Constructor builder");
  }

  @Override
  public Void matchArray(final TArray t)
  {
    return null;
  }

  @Override
  public Void matchString(final TString t)
  {
    final FieldName f_name = this.field.getName();
    final String field_name = JPRAGeneratedNames.getFieldName(f_name);
    final String offset_name = JPRAGeneratedNames.getOffsetConstantName(f_name);

    this.constructor_builder.addStatement(
      "this.$N = $T.newString($N, $N + $N, $N, $T.forName($S), $L)",
      field_name,
      JPRAStringCursorByteBuffered.class,
      "in_buffer",
      offset_name,
      "in_base_offset",
      "in_pointer",
      Charset.class,
      t.getEncoding(),
      Integer.valueOf(t.getMaximumStringLength().getValue().intValue()));

    this.class_builder.addField(
      JPRAStringCursorType.class, field_name, Modifier.FINAL, Modifier.PRIVATE);

    /*
      Construct a meta type field, and assign a value to it.
     */

    final Class<JPRATypeModel.JPRAString> c =
      JPRATypeModel.JPRAString.class;
    final String meta_field_name =
      JPRAGeneratedNames.getMetaTypeFieldName(f_name);

    this.class_builder.addField(
      c,
      meta_field_name,
      Modifier.FINAL,
      Modifier.PRIVATE);

    this.constructor_builder.addStatement(
      "this.$N = $T.of($L, $S)",
      meta_field_name,
      c,
      t.getMaximumStringLength().getValue(), t.getEncoding());

    return null;
  }

  @Override
  public Void matchBooleanSet(final TBooleanSet t)
  {
    /*
      Construct a meta type field, and assign a value to it.
     */

    final Class<JPRATypeModel.JPRABooleanSet> c =
      JPRATypeModel.JPRABooleanSet.class;
    final FieldName f_name = this.field.getName();
    final String meta_field_name =
      JPRAGeneratedNames.getMetaTypeFieldName(f_name);

    this.class_builder.addField(
      c,
      meta_field_name,
      Modifier.FINAL,
      Modifier.PRIVATE);

    this.constructor_builder.addStatement(
      "this.$N = $T.of($L)",
      meta_field_name,
      c,
      t.getSizeInOctets().getValue());

    return null;
  }

  @Override
  public Void matchInteger(final TIntegerType t)
  {
    /*
      Construct a meta type field, and assign a value to it.
     */

    final FieldName f_name = this.field.getName();
    final ClassName c_name =
      JPRAClasses.getModelScalarTypeForScalarType(t);
    final String meta_field_name =
      JPRAGeneratedNames.getMetaTypeFieldName(f_name);

    this.class_builder.addField(
      c_name,
      meta_field_name,
      Modifier.FINAL,
      Modifier.PRIVATE);
    this.constructor_builder.addStatement(
      "this.$N = $T.of($L)",
      meta_field_name,
      c_name,
      t.getSizeInBits().getValue());

    return null;
  }

  @Override
  public Void matchFloat(final TFloat t)
  {
    /*
      Construct a meta type field, and assign a value to it.
     */

    final FieldName f_name = this.field.getName();
    final ClassName c_name =
      JPRAClasses.getModelScalarTypeForScalarType(t);
    final String meta_field_name =
      JPRAGeneratedNames.getMetaTypeFieldName(f_name);

    this.class_builder.addField(
      c_name,
      meta_field_name,
      Modifier.FINAL,
      Modifier.PRIVATE);
    this.constructor_builder.addStatement(
      "this.$N = $T.of($L)",
      meta_field_name,
      c_name,
      t.getSizeInBits().getValue());

    return null;
  }

  @Override
  public Void matchVector(final TVector t)
  {
    final JPRAClasses.VectorsClasses c = JPRAClasses.getVectorClassesFor(t);

    /*
      Construct a vector cursor field and assign value to it.
     */

    final FieldName f_name = this.field.getName();
    final String field_name = JPRAGeneratedNames.getFieldName(f_name);
    final String offset_name = JPRAGeneratedNames.getOffsetConstantName(f_name);
    this.constructor_builder.addStatement(
      "this.$N = $T.createWithBase($N, $N, $N + $N)",
      field_name,
      c.getBufferedConstructors(),
      "in_buffer",
      "in_pointer.getByteOffsetObservable()",
      "in_base_offset",
      offset_name);
    this.class_builder.addField(
      c.getBufferedInterface(), field_name, Modifier.FINAL, Modifier.PRIVATE);

    /*
      Construct a meta type field, and assign a value to it.
     */

    final ClassName et =
      JPRAClasses.getModelScalarTypeForScalarType(t.getElementType());
    final String meta_field_name =
      JPRAGeneratedNames.getMetaTypeFieldName(f_name);

    this.class_builder.addField(
      JPRATypeModel.JPRAVector.class,
      meta_field_name,
      Modifier.FINAL,
      Modifier.PRIVATE);

    this.constructor_builder.beginControlFlow("");
    this.constructor_builder.addStatement(
      "final $T et = $T.of($L)", et, et,
      t.getElementType().getSizeInBits().getValue());
    this.constructor_builder.addStatement(
      "this.$N = $T.of($L, et)",
      meta_field_name,
      JPRATypeModel.JPRAVector.class,
      t.getElementCount().getValue());
    this.constructor_builder.endControlFlow();

    return null;
  }

  @Override
  public Void matchMatrix(final TMatrix t)
  {
    final JPRAClasses.MatrixClasses c = JPRAClasses.getMatrixClassesFor(t);

    /*
      Construct a matrix cursor field and assign value to it.
     */

    final FieldName f_name = this.field.getName();
    final String field_name = JPRAGeneratedNames.getFieldName(f_name);
    final String offset_name = JPRAGeneratedNames.getOffsetConstantName(f_name);
    this.constructor_builder.addStatement(
      "this.$N = $T.createWithBase($N, $N, $N + $N)",
      field_name,
      c.getBufferedConstructors(),
      "in_buffer",
      "in_pointer.getByteOffsetObservable()",
      "in_base_offset",
      offset_name);

    this.class_builder.addField(
      c.getBufferedInterface(), field_name, Modifier.FINAL, Modifier.PRIVATE);

    /*
      Construct a meta type field, and assign a value to it.
     */

    final ClassName et =
      JPRAClasses.getModelScalarTypeForScalarType(t.getElementType());
    final String meta_field_name =
      JPRAGeneratedNames.getMetaTypeFieldName(f_name);

    this.class_builder.addField(
      JPRATypeModel.JPRAMatrix.class,
      meta_field_name,
      Modifier.FINAL,
      Modifier.PRIVATE);

    this.constructor_builder.beginControlFlow("");
    this.constructor_builder.addStatement(
      "final $T et = $T.of($L)", et, et,
      t.getElementType().getSizeInBits().getValue());
    this.constructor_builder.addStatement(
      "this.$N = $T.of($L, $L, et)",
      meta_field_name,
      JPRATypeModel.JPRAMatrix.class,
      t.getWidth().getValue(),
      t.getHeight().getValue());
    this.constructor_builder.endControlFlow();

    return null;
  }

  @Override
  public Void matchRecord(final TRecord t)
  {
    this.recordOrPackedField(t.getName(), t.getPackageContext());
    return null;
  }

  private void recordOrPackedField(
    final TypeName t_name,
    final PackageContextType pkg_ctxt)
  {
    /*
      Construct a record/packed cursor field and assign a value to it.
     */

    final String t_imp_name =
      JPRAGeneratedNames.getRecordImplementationByteBufferedName(t_name);
    final String t_int_name = JPRAGeneratedNames.getRecordInterfaceName(t_name);
    final FieldName f_name = this.field.getName();
    final String field_name = JPRAGeneratedNames.getFieldName(f_name);
    final String offset_name = JPRAGeneratedNames.getOffsetConstantName(f_name);
    this.constructor_builder.addStatement(
      "this.$N = $N.newValueWithOffset($N, $N, $N + $N)",
      field_name,
      t_imp_name,
      "in_buffer",
      "in_pointer",
      "in_base_offset",
      offset_name);

    final PackageNameQualified p = pkg_ctxt.getName();
    final ClassName t_cn = ClassName.get(p.toString(), t_int_name);
    this.class_builder.addField(
      t_cn, field_name, Modifier.FINAL, Modifier.PRIVATE);

    /*
      Construct a meta type field, and assign a value to it.
     */

    final String meta_field_name =
      JPRAGeneratedNames.getMetaTypeFieldName(f_name);

    this.class_builder.addField(
      JPRATypeModel.JPRAUserDefined.class,
      meta_field_name,
      Modifier.FINAL,
      Modifier.PRIVATE);
    this.constructor_builder.addStatement(
      "this.$N = $T.of($T.class)",
      meta_field_name,
      JPRATypeModel.JPRAUserDefined.class,
      t_cn);
  }

  @Override
  public Void matchPacked(final TPacked t)
  {
    this.recordOrPackedField(t.getName(), t.getPackageContext());
    return null;
  }
}
