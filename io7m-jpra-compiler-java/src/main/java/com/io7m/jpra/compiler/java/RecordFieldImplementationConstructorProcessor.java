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
import com.io7m.junreachable.UnreachableCodeException;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

/**
 * A type matcher that produces constructor field assignment statements for a
 * given record field.
 */

final class RecordFieldImplementationConstructorProcessor
  implements TypeMatcherType<Unit, UnreachableCodeException>
{
  private final TRecord.FieldValue field;
  private final TypeSpec.Builder   class_builder;
  private final MethodSpec.Builder constructor_builder;

  RecordFieldImplementationConstructorProcessor(
    final TRecord.FieldValue in_field,
    final TypeSpec.Builder in_class_builder,
    final MethodSpec.Builder in_constructor_builder)
  {
    this.field = NullCheck.notNull(in_field);
    this.class_builder = NullCheck.notNull(in_class_builder);
    this.constructor_builder = NullCheck.notNull(in_constructor_builder);
  }

  @Override public Unit matchArray(final TArray t)
  {
    return Unit.unit();
  }

  @Override public Unit matchString(final TString t)
  {
    return Unit.unit();
  }

  @Override public Unit matchBooleanSet(final TBooleanSet t)
  {
    return Unit.unit();
  }

  @Override public Unit matchInteger(final TIntegerType t)
  {
    return Unit.unit();
  }

  @Override public Unit matchFloat(final TFloat t)
  {
    return Unit.unit();
  }

  @Override public Unit matchVector(final TVector t)
  {
    return Unit.unit();
  }

  @Override public Unit matchMatrix(final TMatrix t)
  {
    return Unit.unit();
  }

  @Override public Unit matchRecord(final TRecord t)
  {
    this.recordOrPackedField(t.getName(), t.getPackageContext());
    return Unit.unit();
  }

  private void recordOrPackedField(
    final TypeName t_name,
    final PackageContextType pkg_ctxt)
  {
    final String t_imp_name =
      JPRAGeneratedNames.getRecordImplementationByteBufferedName(t_name);
    final String t_int_name = JPRAGeneratedNames.getRecordInterfaceName(t_name);

    final FieldName f_name = this.field.getName();
    final String field_name = JPRAGeneratedNames.getFieldName(f_name);
    final String offset_name = JPRAGeneratedNames.getOffsetConstantName(f_name);
    this.constructor_builder.addStatement(
      "this.$N = $N.newValueWithOffset($N, $N, $N)",
      field_name,
      t_imp_name,
      "in_buffer",
      "in_pointer",
      offset_name);

    final PackageNameQualified p = pkg_ctxt.getName();
    final ClassName t_cn = ClassName.get(p.toString(), t_int_name);
    this.class_builder.addField(
      t_cn, field_name, Modifier.FINAL, Modifier.PRIVATE);
  }

  @Override public Unit matchPacked(final TPacked t)
  {
    this.recordOrPackedField(t.getName(), t.getPackageContext());
    return Unit.unit();
  }
}