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
import com.io7m.jpra.model.names.TypeName;
import com.io7m.jpra.model.types.Size;
import com.io7m.jpra.model.types.SizeUnitOctetsType;
import com.io7m.jpra.model.types.TPacked;
import com.io7m.jpra.model.types.TRecord;
import com.io7m.jpra.model.types.TType;
import com.io7m.jpra.runtime.java.JPRACursorByteReadableType;
import com.io7m.jpra.runtime.java.JPRAValueType;
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
import java.nio.ByteOrder;
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

  private static void generateRecordFieldMethods(
    final TRecord t,
    final TypeSpec.Builder jcb)
  {
    BigInteger offset = BigInteger.ZERO;
    for (final TRecord.FieldType f : t.getFieldsInDeclarationOrder()) {
      final BigInteger o = offset;
      f.matchField(
        new TRecord.FieldMatcherType<Void, UnreachableCodeException>()
        {
          @Override
          public Void matchFieldValue(
            final TRecord.FieldValue f)
          {
            final TType t = f.getType();
            return t.matchType(new RecordFieldImplementationProcessor(
              f,
              o,
              jcb));
          }

          @Override
          public Void matchFieldPaddingOctets(
            final TRecord.FieldPaddingOctets f)
          {
            return null;
          }
        });
      offset = offset.add(f.getSizeInOctets().getValue());
    }
  }

  private static void generateRecordByteOffsetMethod(
    final TypeSpec.Builder jcb)
  {
    final MethodSpec.Builder jmb = MethodSpec.methodBuilder("getByteOffsetFor");
    jmb.addModifiers(Modifier.PRIVATE);
    jmb.returns(int.class);
    jmb.addParameter(int.class, "field_offset", Modifier.FINAL);
    jmb.addStatement(
      "final int b = (int) this.$N.getByteOffsetObservable().value()",
      "pointer");
    jmb.addStatement(
      "return $N + this.$N + $N", "b", "base_offset", "field_offset");
    jcb.addMethod(jmb.build());
  }

  private static void generatePackedByteOffsetMethod(
    final TypeSpec.Builder jcb)
  {
    final MethodSpec.Builder jmb = MethodSpec.methodBuilder("getByteOffset");
    jmb.addModifiers(Modifier.PRIVATE);
    jmb.returns(int.class);
    jmb.addStatement(
      "final int b = (int) this.$N.getByteOffsetObservable().value()",
      "pointer");
    jmb.addStatement(
      "return $N + this.$N", "b", "base_offset");
    jcb.addMethod(jmb.build());
  }

  private static void generateSizeMethods(
    final TypeSpec.Builder jcb,
    final Size<SizeUnitOctetsType> size)
  {
    final FieldSpec.Builder fb = FieldSpec.builder(
      int.class,
      "SIZE_OCTETS",
      Modifier.FINAL,
      Modifier.STATIC,
      Modifier.PRIVATE);

    fb.initializer(size.getValue().toString());
    jcb.addField(fb.build());

    {
      final MethodSpec.Builder jmb = MethodSpec.methodBuilder("sizeOctets");
      jmb.addModifiers(Modifier.PUBLIC);
      jmb.addAnnotation(Override.class);
      jmb.returns(int.class);
      jmb.addStatement("return SIZE_OCTETS");
      jcb.addMethod(jmb.build());
    }

    {
      final MethodSpec.Builder jmb = MethodSpec.methodBuilder("sizeInOctets");
      jmb.addJavadoc("@return The size of the type in octets\n");
      jmb.addModifiers(Modifier.PUBLIC);
      jmb.addModifiers(Modifier.STATIC);
      jmb.returns(int.class);
      jmb.addStatement("return SIZE_OCTETS");
      jcb.addMethod(jmb.build());
    }
  }

  private static void generateRecordFactoryMethods(
    final ClassName imp_name,
    final ClassName int_name,
    final ClassName ptr_class,
    final TypeSpec.Builder jcb)
  {
    {
      final MethodSpec.Builder jmb =
        MethodSpec.methodBuilder("newValueWithOffset");
      jmb.addJavadoc("Construct a view of a type.\n");
      jmb.addJavadoc("@param in_buffer A byte buffer\n");
      jmb.addJavadoc("@param in_pointer A cursor\n");
      jmb.addJavadoc("@param in_base_offset The base offset from the cursor\n");
      jmb.addJavadoc("@return A view of a type\n");
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
      jmb.addJavadoc("Construct a view of a type.\n");
      jmb.addJavadoc("@param in_buffer A byte buffer\n");
      jmb.addJavadoc("@param in_pointer A cursor\n");
      jmb.addJavadoc("@return A view of a type\n");
      jmb.addModifiers(Modifier.PUBLIC);
      jmb.addModifiers(Modifier.STATIC);
      jmb.returns(int_name);
      jmb.addParameter(ByteBuffer.class, "in_buffer", Modifier.FINAL);
      jmb.addParameter(ptr_class, "in_pointer", Modifier.FINAL);
      jmb.addStatement(
        "return new $T($N, $N, 0)", imp_name, "in_buffer", "in_pointer");
      jcb.addMethod(jmb.build());
    }
  }

  private static void generatePackedFactoryMethods(
    final ClassName imp_name,
    final ClassName int_name,
    final ClassName ptr_class,
    final TypeSpec.Builder jcb)
  {
    {
      final MethodSpec.Builder jmb =
        MethodSpec.methodBuilder("newValueWithOffset");
      jmb.addJavadoc("Construct a view of a type.\n");
      jmb.addJavadoc("@param in_buffer A byte buffer\n");
      jmb.addJavadoc("@param in_pointer A cursor\n");
      jmb.addJavadoc("@param in_base_offset The base offset from the cursor\n");
      jmb.addJavadoc("@return A view of a type\n");
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
      jmb.addJavadoc("Construct a view of a type.\n");
      jmb.addJavadoc("@param in_buffer A byte buffer\n");
      jmb.addJavadoc("@param in_pointer A cursor\n");
      jmb.addJavadoc("@return A view of a type\n");
      jmb.addModifiers(Modifier.PUBLIC);
      jmb.addModifiers(Modifier.STATIC);
      jmb.returns(int_name);
      jmb.addParameter(ByteBuffer.class, "in_buffer", Modifier.FINAL);
      jmb.addParameter(ptr_class, "in_pointer", Modifier.FINAL);
      jmb.addStatement(
        "return new $T($N, $N, 0)", imp_name, "in_buffer", "in_pointer");
      jcb.addMethod(jmb.build());
    }
  }

  private static void generateRecordConstructor(
    final TRecord t,
    final ClassName ptr_class,
    final TypeSpec.Builder jcb)
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
        new TRecord.FieldMatcherType<Void, UnreachableCodeException>()
        {
          @Override
          public Void matchFieldValue(
            final TRecord.FieldValue f)
          {
            final TType t = f.getType();
            return t.matchType(
              new RecordFieldImplementationConstructorProcessor(
                f, jcb, jmb));
          }

          @Override
          public Void matchFieldPaddingOctets(
            final TRecord.FieldPaddingOctets f)
          {
            return null;
          }
        });
    }

    jcb.addMethod(jmb.build());
  }

  private static void generatePackedConstructor(
    final TPacked t,
    final ClassName ptr_class,
    final TypeSpec.Builder jcb)
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
      "this.$N = ByteBuffer.allocate($L)",
      "pack_buffer",
      Integer.valueOf(t.getSizeInOctets().getValue().intValue()));
    jmb.addStatement(
      "this.$N.order($T.$L)",
      "pack_buffer",
      ByteOrder.class,
      ByteOrder.BIG_ENDIAN);

    jmb.addStatement(
      "this.$N = $T.requireNonNull($N, $S)",
      "pointer",
      cno,
      "in_pointer",
      "Pointer");
    jmb.addStatement("this.$N = $N", "base_offset", "in_base_offset");
    jcb.addMethod(jmb.build());
  }

  @Override
  public String getRecordImplementationByteBufferedName(final TypeName t)
  {
    return JPRAGeneratedNames.getRecordImplementationByteBufferedName(t);
  }

  @Override
  public String getRecordInterfaceReadableName(final TypeName t)
  {
    return JPRAGeneratedNames.getRecordInterfaceReadableName(t);
  }

  @Override
  public String getRecordInterfaceWritableName(final TypeName t)
  {
    return JPRAGeneratedNames.getRecordInterfaceWritableName(t);
  }

  @Override
  public String getRecordInterfaceName(final TypeName t)
  {
    return JPRAGeneratedNames.getRecordInterfaceName(t);
  }

  @Override
  public String getPackedImplementationByteBufferedName(final TypeName t)
  {
    return JPRAGeneratedNames.getPackedImplementationByteBufferedName(t);
  }

  @Override
  public String getPackedInterfaceReadableName(final TypeName t)
  {
    return JPRAGeneratedNames.getPackedInterfaceReadableName(t);
  }

  @Override
  public String getPackedInterfaceWritableName(final TypeName t)
  {
    return JPRAGeneratedNames.getPackedInterfaceWritableName(t);
  }

  @Override
  public String getPackedInterfaceName(final TypeName t)
  {
    return JPRAGeneratedNames.getPackedInterfaceName(t);
  }

  @Override
  public void generateRecordImplementation(
    final TRecord t,
    final OutputStream os)
    throws IOException
  {
    Objects.requireNonNull(t, "t");
    Objects.requireNonNull(os, "os");

    try (OutputStreamWriter out = new OutputStreamWriter(os)) {
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
      jcb.addJavadoc(
        "A {@code ByteBuffer} based implementation of the {@code $L} record type.",
        t_name);
      jcb.addSuperinterface(int_name);
      jcb.addModifiers(Modifier.PUBLIC, Modifier.FINAL);

      jcb.addField(
        ByteBuffer.class, "buffer", Modifier.PRIVATE, Modifier.FINAL);
      jcb.addField(
        int.class, "base_offset", Modifier.PRIVATE, Modifier.FINAL);
      jcb.addField(ptr_class, "pointer", Modifier.PRIVATE, Modifier.FINAL);

      generateRecordConstructor(t, ptr_class, jcb);
      generateRecordFactoryMethods(
        imp_name, int_name, ptr_class, jcb);
      generateSizeMethods(jcb, t.getSizeInOctets());
      generateRecordByteOffsetMethod(jcb);
      generateRecordFieldMethods(t, jcb);

      final JavaFile.Builder jfb = JavaFile.builder(pack_name, jcb.build());
      final JavaFile jf = jfb.build();
      jf.writeTo(out);
    }
  }

  @Override
  public void generateRecordInterfaceReadable(
    final TRecord t,
    final OutputStream os)
    throws IOException
  {
    Objects.requireNonNull(t, "t");
    Objects.requireNonNull(os, "os");

    try (OutputStreamWriter out = new OutputStreamWriter(os)) {
      final PackageContextType tp = t.getPackageContext();
      final TypeName tn = t.getName();
      final String name = this.getRecordInterfaceReadableName(tn);
      final String pack_name = tp.getName().toString();

      final TypeSpec.Builder jcb = TypeSpec.interfaceBuilder(name);
      jcb.addJavadoc(
        "The readable interface to values of the {@code $L} record type.",
        tn);
      jcb.addModifiers(Modifier.PUBLIC);
      jcb.addSuperinterface(JPRAValueType.class);

      for (final TRecord.FieldType f : t.getFieldsInDeclarationOrder()) {
        f.matchField(
          new TRecord.FieldMatcherType<Void, UnreachableCodeException>()
          {
            @Override
            public Void matchFieldValue(
              final TRecord.FieldValue f)
            {
              final TType t = f.getType();
              t.matchType(
                new RecordFieldInterfaceProcessor(
                  f, jcb, MethodSelection.GETTERS));
              return null;
            }

            @Override
            public Void matchFieldPaddingOctets(
              final TRecord.FieldPaddingOctets f)
            {
              return null;
            }
          });
      }

      final TypeSpec jc = jcb.build();
      final JavaFile.Builder jfb = JavaFile.builder(pack_name, jc);
      final JavaFile jf = jfb.build();
      jf.writeTo(out);
    }
  }

  @Override
  public void generateRecordInterfaceWritable(
    final TRecord t,
    final OutputStream os)
    throws IOException
  {
    Objects.requireNonNull(t, "t");
    Objects.requireNonNull(os, "os");

    try (OutputStreamWriter out = new OutputStreamWriter(os)) {
      final PackageContextType tp = t.getPackageContext();
      final String tn = this.getRecordInterfaceWritableName(t.getName());

      final TypeSpec.Builder jcb = TypeSpec.interfaceBuilder(tn);
      jcb.addJavadoc(
        "The writable interface to values of the {@code $L} record type.",
        tn);
      jcb.addModifiers(Modifier.PUBLIC);
      jcb.addSuperinterface(JPRAValueType.class);

      for (final TRecord.FieldType f : t.getFieldsInDeclarationOrder()) {
        f.matchField(
          new TRecord.FieldMatcherType<Void, UnreachableCodeException>()
          {
            @Override
            public Void matchFieldValue(
              final TRecord.FieldValue f)
            {
              final TType t = f.getType();
              t.matchType(
                new RecordFieldInterfaceProcessor(
                  f, jcb, MethodSelection.SETTERS));
              return null;
            }

            @Override
            public Void matchFieldPaddingOctets(
              final TRecord.FieldPaddingOctets f)
            {
              return null;
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

  @Override
  public void generateRecordInterface(
    final TRecord t,
    final OutputStream os)
    throws IOException
  {
    Objects.requireNonNull(t, "t");
    Objects.requireNonNull(os, "os");

    try (OutputStreamWriter out = new OutputStreamWriter(os)) {
      final PackageContextType tp = t.getPackageContext();
      final TypeName t_name = t.getName();
      final String pack_name = tp.getName().toString();

      final String wtn =
        JPRAGeneratedNames.getRecordInterfaceWritableName(t_name);
      final String rtn =
        JPRAGeneratedNames.getRecordInterfaceReadableName(t_name);
      final String tn = JPRAGeneratedNames.getRecordInterfaceName(t_name);

      final TypeSpec.Builder jcb = TypeSpec.interfaceBuilder(tn);
      jcb.addJavadoc(
        "The interface to values of the {@code $L} record type.", t_name);
      jcb.addModifiers(Modifier.PUBLIC);
      jcb.addSuperinterface(ClassName.get(pack_name, wtn));
      jcb.addSuperinterface(ClassName.get(pack_name, rtn));
      jcb.addSuperinterface(JPRAValueType.class);

      final TypeSpec jc = jcb.build();
      final JavaFile.Builder jfb = JavaFile.builder(pack_name, jc);
      final JavaFile jf = jfb.build();
      jf.writeTo(out);
    }
  }

  @Override
  public void generatePackedImplementation(
    final TPacked t,
    final OutputStream os)
    throws IOException
  {
    Objects.requireNonNull(t, "t");
    Objects.requireNonNull(os, "os");

    try (OutputStreamWriter out = new OutputStreamWriter(os)) {
      final PackageContextType tp = t.getPackageContext();
      final TypeName t_name = t.getName();
      final String tn =
        JPRAGeneratedNames.getPackedImplementationByteBufferedName(t_name);
      final String in = JPRAGeneratedNames.getPackedInterfaceName(t_name);

      final String pack_name = tp.getName().toString();
      final ClassName imp_name = ClassName.get(pack_name, tn);
      final ClassName int_name = ClassName.get(pack_name, in);
      final ClassName ptr_class =
        ClassName.get(JPRACursorByteReadableType.class);

      final TypeSpec.Builder jcb = TypeSpec.classBuilder(tn);
      jcb.addJavadoc(
        "A {@code ByteBuffer} based implementation of the {@code $L} packed type.",
        t_name);
      jcb.addSuperinterface(int_name);
      jcb.addModifiers(Modifier.PUBLIC, Modifier.FINAL);

      jcb.addField(
        ByteBuffer.class, "buffer", Modifier.PRIVATE, Modifier.FINAL);
      jcb.addField(
        ByteBuffer.class, "pack_buffer", Modifier.PRIVATE, Modifier.FINAL);
      jcb.addField(
        int.class, "base_offset", Modifier.PRIVATE, Modifier.FINAL);
      jcb.addField(ptr_class, "pointer", Modifier.PRIVATE, Modifier.FINAL);

      generatePackedConstructor(t, ptr_class, jcb);
      generatePackedFactoryMethods(
        imp_name, int_name, ptr_class, jcb);
      generateSizeMethods(jcb, t.getSizeInOctets());
      generatePackedByteOffsetMethod(jcb);

      BigInteger offset = BigInteger.valueOf(0L);
      for (final TPacked.FieldType f : t.getFieldsInDeclarationOrder()) {
        final BigInteger o = offset;
        f.matchField(
          new TPacked.FieldMatcherType<Void, UnreachableCodeException>()
          {
            @Override
            public Void matchFieldValue(
              final TPacked.FieldValue f)
            {
              final TType t = f.getType();
              return t.matchType(new PackedFieldImplementationProcessor(
                f,
                o,
                jcb));
            }

            @Override
            public Void matchFieldPaddingBits(
              final TPacked.FieldPaddingBits f)
            {
              return null;
            }
          });
        offset = offset.add(f.getSize().getValue());
      }

      PackedFieldImplementationProcessor.generatedPackedAllMethodImplementation(
        jcb, t);

      final JavaFile.Builder jfb = JavaFile.builder(pack_name, jcb.build());
      final JavaFile jf = jfb.build();
      jf.writeTo(out);
    }
  }

  @Override
  public void generatePackedInterfaceReadable(
    final TPacked t,
    final OutputStream os)
    throws IOException
  {
    Objects.requireNonNull(t, "t");
    Objects.requireNonNull(os, "os");

    try (OutputStreamWriter out = new OutputStreamWriter(os)) {
      final PackageContextType tp = t.getPackageContext();
      final TypeName tn = t.getName();
      final String name = this.getPackedInterfaceReadableName(tn);
      final String pack_name = tp.getName().toString();

      final TypeSpec.Builder jcb = TypeSpec.interfaceBuilder(name);
      jcb.addJavadoc(
        "The readable interface to values of the {@code $L} packed type.",
        tn);
      jcb.addModifiers(Modifier.PUBLIC);
      jcb.addSuperinterface(JPRAValueType.class);

      for (final TPacked.FieldType f : t.getFieldsInDeclarationOrder()) {
        f.matchField(
          new TPacked.FieldMatcherType<Void, UnreachableCodeException>()
          {
            @Override
            public Void matchFieldValue(
              final TPacked.FieldValue f)
            {
              final TType t = f.getType();
              return t.matchType(
                new PackedFieldInterfaceProcessor(
                  f, jcb, MethodSelection.GETTERS));
            }

            @Override
            public Void matchFieldPaddingBits(
              final TPacked.FieldPaddingBits f)
            {
              return null;
            }
          });
      }

      final TypeSpec jc = jcb.build();
      final JavaFile.Builder jfb = JavaFile.builder(pack_name, jc);
      final JavaFile jf = jfb.build();
      jf.writeTo(out);
    }
  }

  @Override
  public void generatePackedInterfaceWritable(
    final TPacked t,
    final OutputStream os)
    throws IOException
  {
    Objects.requireNonNull(t, "t");
    Objects.requireNonNull(os, "os");

    try (OutputStreamWriter out = new OutputStreamWriter(os)) {
      final PackageContextType tp = t.getPackageContext();
      final String tn = this.getPackedInterfaceWritableName(t.getName());
      final String pack_name = tp.getName().toString();

      final TypeSpec.Builder jcb = TypeSpec.interfaceBuilder(tn);
      jcb.addJavadoc(
        "The writable interface to values of the {@code $L} packed type.",
        tn);
      jcb.addModifiers(Modifier.PUBLIC);
      jcb.addSuperinterface(JPRAValueType.class);

      for (final TPacked.FieldType f : t.getFieldsInDeclarationOrder()) {
        f.matchField(
          new TPacked.FieldMatcherType<Void, UnreachableCodeException>()
          {
            @Override
            public Void matchFieldValue(
              final TPacked.FieldValue f)
            {
              final TType t = f.getType();
              return t.matchType(
                new PackedFieldInterfaceProcessor(
                  f, jcb, MethodSelection.SETTERS));
            }

            @Override
            public Void matchFieldPaddingBits(
              final TPacked.FieldPaddingBits f)
            {
              return null;
            }
          });
      }

      PackedFieldInterfaceProcessor.generatedPackedAllMethodInterface(
        jcb, t.getFieldsInDeclarationOrder());

      final TypeSpec jc = jcb.build();
      final JavaFile.Builder jfb = JavaFile.builder(pack_name, jc);
      final JavaFile jf = jfb.build();
      jf.writeTo(out);
    }
  }

  @Override
  public void generatePackedInterface(
    final TPacked t,
    final OutputStream os)
    throws IOException
  {
    Objects.requireNonNull(t, "t");
    Objects.requireNonNull(os, "os");

    try (OutputStreamWriter out = new OutputStreamWriter(os)) {
      final PackageContextType tp = t.getPackageContext();
      final TypeName t_name = t.getName();
      final String pack_name = tp.getName().toString();

      final String wtn =
        JPRAGeneratedNames.getPackedInterfaceWritableName(t_name);
      final String rtn =
        JPRAGeneratedNames.getPackedInterfaceReadableName(t_name);
      final String tn = JPRAGeneratedNames.getPackedInterfaceName(t_name);

      final TypeSpec.Builder jcb = TypeSpec.interfaceBuilder(tn);
      jcb.addJavadoc(
        "The interface to values of the {@code $L} packed type.", t_name);
      jcb.addModifiers(Modifier.PUBLIC);
      jcb.addSuperinterface(ClassName.get(pack_name, wtn));
      jcb.addSuperinterface(ClassName.get(pack_name, rtn));
      jcb.addSuperinterface(JPRAValueType.class);

      final TypeSpec jc = jcb.build();
      final JavaFile.Builder jfb = JavaFile.builder(pack_name, jc);
      final JavaFile jf = jfb.build();
      jf.writeTo(out);
    }
  }
}
