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
import com.io7m.jpra.model.names.TypeName;
import com.io7m.jpra.model.types.TPacked;
import com.io7m.jpra.model.types.TRecord;
import com.io7m.jpra.model.types.TType;
import com.io7m.jpra.runtime.java.JPRACursorByteReadableType;
import com.io7m.jpra.runtime.java.JPRAValueType;
import com.io7m.junreachable.UnimplementedCodeException;
import com.io7m.junreachable.UnreachableCodeException;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * The default implementation of the {@link JPRAJavaGeneratorType} interface.
 */

public final class JPRAJavaGenerator implements JPRAJavaGeneratorType
{
  private JPRAJavaGenerator()
  {

  }

  /**
   * @return A new generator
   */

  public static JPRAJavaGeneratorType newGenerator()
  {
    return new JPRAJavaGenerator();
  }

  @Override
  public String getRecordImplementationByteBufferedName(final TypeName t)
  {
    return JPRAGeneratedNames.getRecordImplementationByteBufferedName(t);
  }

  @Override public String getRecordInterfaceReadableName(final TypeName t)
  {
    return JPRAGeneratedNames.getRecordInterfaceReadableName(t);
  }

  @Override public String getRecordInterfaceWritableName(final TypeName t)
  {
    return JPRAGeneratedNames.getRecordInterfaceWritableName(t);
  }

  @Override public String getRecordInterfaceName(final TypeName t)
  {
    return JPRAGeneratedNames.getRecordInterfaceName(t);
  }

  @Override public void generateRecordImplementation(
    final TRecord t,
    final OutputStream os)
    throws IOException
  {
    NullCheck.notNull(t);
    NullCheck.notNull(os);

    try (final OutputStreamWriter out = new OutputStreamWriter(os)) {
      final PackageContextType tp = t.getPackageContext();
      final TypeName t_name = t.getName();
      final String tn =
        JPRAGeneratedNames.getRecordImplementationByteBufferedName(t_name);
      final String in = JPRAGeneratedNames.getRecordInterfaceName(t_name);

      final String pack_name = tp.getName().toString();
      final ClassName imp_name = ClassName.get(pack_name, tn);
      final ClassName int_name = ClassName.get(pack_name, in);
      final ClassName ptr_class =
        ClassName.get(JPRACursorByteReadableType.class);

      final TypeSpec.Builder jcb = TypeSpec.classBuilder(tn);
      jcb.addSuperinterface(int_name);
      jcb.addModifiers(Modifier.PUBLIC, Modifier.FINAL);

      jcb.addField(
        ByteBuffer.class, "buffer", Modifier.PRIVATE, Modifier.FINAL);
      jcb.addField(
        int.class, "base_offset", Modifier.PRIVATE, Modifier.FINAL);
      jcb.addField(ptr_class, "pointer", Modifier.PRIVATE, Modifier.FINAL);

      {
        final ClassName cno = ClassName.get(Objects.class);
        final MethodSpec.Builder jmb = MethodSpec.constructorBuilder();
        jmb.addModifiers(Modifier.PRIVATE);
        jmb.addParameter(ByteBuffer.class, "in_buffer", Modifier.FINAL);
        jmb.addParameter(ptr_class, "in_pointer", Modifier.FINAL);
        jmb.addParameter(int.class, "in_base_offset", Modifier.FINAL);
        jmb.addStatement(
          "this.$N = $T.requireNonNull($N, $S)",
          "buffer",
          cno,
          "in_buffer",
          "Buffer");
        jmb.addStatement(
          "this.$N = $T.requireNonNull($N, $S)",
          "pointer",
          cno,
          "in_pointer",
          "Pointer");
        jmb.addStatement("this.$N = $N", "base_offset", "in_base_offset");

        for (final TRecord.FieldType f : t.getFieldsInDeclarationOrder()) {
          f.matchField(
            new TRecord.FieldMatcherType<Unit, UnreachableCodeException>()
            {
              @Override public Unit matchFieldValue(
                final TRecord.FieldValue f)
              {
                final TType t = f.getType();
                return t.matchType(
                  new RecordFieldImplementationConstructorProcessor(
                    f, jcb, jmb));
              }

              @Override public Unit matchFieldPaddingOctets(
                final TRecord.FieldPaddingOctets f)
              {
                return Unit.unit();
              }
            });
        }

        jcb.addMethod(jmb.build());
      }

      {
        final MethodSpec.Builder jmb =
          MethodSpec.methodBuilder("newValueWithOffset");
        jmb.addModifiers(Modifier.PUBLIC);
        jmb.addModifiers(Modifier.STATIC);
        jmb.returns(int_name);
        jmb.addParameter(ByteBuffer.class, "in_buffer", Modifier.FINAL);
        jmb.addParameter(ptr_class, "in_pointer", Modifier.FINAL);
        jmb.addParameter(int.class, "in_base_offset", Modifier.FINAL);
        jmb.addStatement(
          "return new $T($N, $N, $N)",
          imp_name,
          "in_buffer",
          "in_pointer",
          "in_base_offset");
        jcb.addMethod(jmb.build());
      }

      {
        final MethodSpec.Builder jmb = MethodSpec.methodBuilder("newValue");
        jmb.addModifiers(Modifier.PUBLIC);
        jmb.addModifiers(Modifier.STATIC);
        jmb.returns(int_name);
        jmb.addParameter(ByteBuffer.class, "in_buffer", Modifier.FINAL);
        jmb.addParameter(ptr_class, "in_pointer", Modifier.FINAL);
        jmb.addStatement(
          "return new $T($N, $N, 0)", imp_name, "in_buffer", "in_pointer");
        jcb.addMethod(jmb.build());
      }

      {
        final FieldSpec.Builder fb = FieldSpec.builder(
          int.class,
          "SIZE_OCTETS",
          Modifier.FINAL,
          Modifier.STATIC,
          Modifier.PRIVATE);
        fb.initializer(t.getSizeInOctets().getValue().toString());
        jcb.addField(fb.build());
      }

      {
        final MethodSpec.Builder jmb = MethodSpec.methodBuilder("sizeOctets");
        jmb.addModifiers(Modifier.PUBLIC);
        jmb.addAnnotation(Override.class);
        jmb.returns(int.class);
        jmb.addStatement("return SIZE_OCTETS");
        jcb.addMethod(jmb.build());
      }

      {
        final MethodSpec.Builder jmb =
          MethodSpec.methodBuilder("getByteOffsetFor");
        jmb.addModifiers(Modifier.PRIVATE);
        jmb.returns(int.class);
        jmb.addParameter(int.class, "field_offset");
        jmb.addStatement(
          "final int b = (int) this.$N.getByteOffset()", "pointer");
        jmb.addStatement(
          "return $N + this.$N + $N", "b", "base_offset", "field_offset");
        jcb.addMethod(jmb.build());
      }

      BigInteger offset = BigInteger.ZERO;
      for (final TRecord.FieldType f : t.getFieldsInDeclarationOrder()) {
        final BigInteger o = offset;
        f.matchField(
          new TRecord.FieldMatcherType<Unit, UnreachableCodeException>()
          {
            @Override public Unit matchFieldValue(
              final TRecord.FieldValue f)
            {
              final TType t = f.getType();
              return t.matchType(
                new RecordFieldImplementationProcessor(f, o, jcb));
            }

            @Override public Unit matchFieldPaddingOctets(
              final TRecord.FieldPaddingOctets f)
            {
              return Unit.unit();
            }
          });
        offset = offset.add(f.getSizeInOctets().getValue());
      }

      final JavaFile.Builder jfb = JavaFile.builder(pack_name, jcb.build());
      final JavaFile jf = jfb.build();
      jf.writeTo(out);
    }
  }

  @Override public void generateRecordInterfaceReadable(
    final TRecord t,
    final OutputStream os)
    throws IOException
  {
    NullCheck.notNull(t);
    NullCheck.notNull(os);

    try (final OutputStreamWriter out = new OutputStreamWriter(os)) {
      final PackageContextType tp = t.getPackageContext();
      final TypeName tn = t.getName();
      final String name = this.getRecordInterfaceReadableName(tn);

      final TypeSpec.Builder jcb = TypeSpec.interfaceBuilder(name);
      jcb.addModifiers(Modifier.PUBLIC);
      jcb.addSuperinterface(JPRAValueType.class);

      for (final TRecord.FieldType f : t.getFieldsInDeclarationOrder()) {
        f.matchField(
          new TRecord.FieldMatcherType<Unit, UnreachableCodeException>()
          {
            @Override public Unit matchFieldValue(
              final TRecord.FieldValue f)
            {
              final TType t = f.getType();
              t.matchType(
                new RecordFieldInterfaceProcessor(
                  f, jcb, MethodSelection.GETTERS));
              return Unit.unit();
            }

            @Override public Unit matchFieldPaddingOctets(
              final TRecord.FieldPaddingOctets f)
            {
              return Unit.unit();
            }
          });
      }

      final TypeSpec jc = jcb.build();
      final JavaFile.Builder jfb =
        JavaFile.builder(tp.getName().toString(), jc);
      final JavaFile jf = jfb.build();
      jf.writeTo(out);
    }
  }

  @Override public void generateRecordInterfaceWritable(
    final TRecord t,
    final OutputStream os)
    throws IOException
  {
    NullCheck.notNull(t);
    NullCheck.notNull(os);

    try (final OutputStreamWriter out = new OutputStreamWriter(os)) {
      final PackageContextType tp = t.getPackageContext();
      final String tn = this.getRecordInterfaceWritableName(t.getName());

      final TypeSpec.Builder jcb = TypeSpec.interfaceBuilder(tn);
      jcb.addModifiers(Modifier.PUBLIC);
      jcb.addSuperinterface(JPRAValueType.class);

      for (final TRecord.FieldType f : t.getFieldsInDeclarationOrder()) {
        f.matchField(
          new TRecord.FieldMatcherType<Unit, UnreachableCodeException>()
          {
            @Override public Unit matchFieldValue(
              final TRecord.FieldValue f)
            {
              final TType t = f.getType();
              t.matchType(
                new RecordFieldInterfaceProcessor(
                  f, jcb, MethodSelection.SETTERS));
              return Unit.unit();
            }

            @Override public Unit matchFieldPaddingOctets(
              final TRecord.FieldPaddingOctets f)
            {
              return Unit.unit();
            }
          });
      }

      final TypeSpec jc = jcb.build();
      final JavaFile.Builder jfb =
        JavaFile.builder(tp.getName().toString(), jc);
      final JavaFile jf = jfb.build();
      jf.writeTo(out);
    }
  }

  @Override public void generateRecordInterface(
    final TRecord t,
    final OutputStream os)
    throws IOException
  {
    NullCheck.notNull(t);
    NullCheck.notNull(os);

    try (final OutputStreamWriter out = new OutputStreamWriter(os)) {
      final PackageContextType tp = t.getPackageContext();
      final TypeName t_name = t.getName();
      final String wtn = this.getRecordInterfaceWritableName(t_name);
      final String rtn = this.getRecordInterfaceReadableName(t_name);
      final String tn = JPRAGeneratedNames.getRecordInterfaceName(t_name);

      final TypeSpec.Builder jcb = TypeSpec.interfaceBuilder(tn);
      jcb.addModifiers(Modifier.PUBLIC);
      jcb.addSuperinterface(ClassName.get(tp.getName().toString(), wtn));
      jcb.addSuperinterface(ClassName.get(tp.getName().toString(), rtn));
      jcb.addSuperinterface(JPRAValueType.class);

      final TypeSpec jc = jcb.build();
      final JavaFile.Builder jfb =
        JavaFile.builder(tp.getName().toString(), jc);
      final JavaFile jf = jfb.build();
      jf.writeTo(out);
    }
  }

  @Override public void generatePacked(
    final TPacked t,
    final OutputStream os)
  {
    NullCheck.notNull(t);
    NullCheck.notNull(os);

    // TODO: Generated method stub!
    throw new UnimplementedCodeException();
  }

}
