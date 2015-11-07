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
import com.io7m.jfunctional.Unit;
import com.io7m.jnfp.core.NFPSignedDoubleInt;
import com.io7m.jnfp.core.NFPSignedDoubleLong;
import com.io7m.jnfp.core.NFPUnsignedDoubleInt;
import com.io7m.jnfp.core.NFPUnsignedDoubleLong;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.contexts.PackageContextType;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.names.TypeName;
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
import com.io7m.jpra.model.types.TType;
import com.io7m.jpra.model.types.TVector;
import com.io7m.jpra.model.types.TypeIntegerMatcherType;
import com.io7m.jpra.model.types.TypeMatcherType;
import com.io7m.junreachable.UnimplementedCodeException;
import com.io7m.junreachable.UnreachableCodeException;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.apache.commons.lang3.text.WordUtils;

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

  private static String getOffsetConstantName(final FieldName name)
  {
    final StringBuilder sb = new StringBuilder(128);
    sb.append("FIELD_");
    sb.append(name.getValue().toUpperCase());
    sb.append("_OFFSET_OCTETS");
    return sb.toString();
  }

  private static String getGetterName(final FieldName name)
  {
    final String text =
      WordUtils.capitalize(name.toString()).replaceAll("_", "");
    final StringBuilder sb = new StringBuilder(128);
    sb.append("get");
    sb.append(text);
    return sb.toString();
  }

  private static String getSetterName(final FieldName name)
  {
    final String text =
      WordUtils.capitalize(name.toString()).replaceAll("_", "");
    final StringBuilder sb = new StringBuilder(128);
    sb.append("set");
    sb.append(text);
    return sb.toString();
  }

  private static String getNormalizedSetterName(final FieldName name)
  {
    final String text =
      WordUtils.capitalize(name.toString()).replaceAll("_", "");
    final StringBuilder sb = new StringBuilder(128);
    sb.append("set");
    sb.append(text);
    sb.append("N");
    return sb.toString();
  }

  private static String getNormalizedGetterName(final FieldName name)
  {
    final String text =
      WordUtils.capitalize(name.toString()).replaceAll("_", "");
    final StringBuilder sb = new StringBuilder(128);
    sb.append("get");
    sb.append(text);
    sb.append("N");
    return sb.toString();
  }

  private static String getInterfaceWritableName(final TypeName t)
  {
    return t.getValue() + "WritableType";
  }

  private static String getInterfaceReadableName(final TypeName tn)
  {
    return tn.getValue() + "ReadableType";
  }

  private static String getInterfaceName(final TypeName t_name)
  {
    return t_name.getValue() + "Type";
  }

  private static String getImplementationByteBufferedName(final TypeName t_name)
  {
    return t_name.getValue() + "ByteBuffered";
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
        JPRAJavaGenerator.getImplementationByteBufferedName(t_name);
      final String in = JPRAJavaGenerator.getInterfaceName(t_name);

      final ClassName int_name =
        ClassName.get(tp.getName().toString(), in);
      final TypeSpec.Builder jcb = TypeSpec.classBuilder(tn);
      jcb.addSuperinterface(int_name);
      jcb.addModifiers(Modifier.PUBLIC, Modifier.FINAL);
      jcb.addField(
        ByteBuffer.class, "buffer", Modifier.PRIVATE, Modifier.FINAL);

      {
        final ClassName cno = ClassName.get(Objects.class);
        final MethodSpec.Builder jmb = MethodSpec.constructorBuilder();
        jmb.addModifiers(Modifier.PRIVATE);
        jmb.addParameter(ByteBuffer.class, "in_buffer", Modifier.FINAL);
        jmb.addParameter(int.class, "in_base_index", Modifier.FINAL);
        jmb.addStatement(
          "this.$N = $T.requireNotNull($N, $S)",
          "buffer",
          cno,
          "in_buffer",
          "Buffer");
        jmb.addStatement("this.$N = $N", "base_index", "in_base_index");
        jcb.addMethod(jmb.build());
      }

      {
        final ClassName imp_name = ClassName.get(tp.getName().toString(), tn);
        final MethodSpec.Builder jmb = MethodSpec.methodBuilder("create");
        jmb.returns(int_name);
        jmb.addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        jmb.addParameter(ByteBuffer.class, "in_buffer", Modifier.FINAL);
        jmb.addParameter(int.class, "in_base_index", Modifier.FINAL);
        jmb.addStatement(
          "return new $T($N, $N)", imp_name, "in_buffer", "in_base_index");
        jcb.addMethod(jmb.build());
      }

      final FieldSpec.Builder fb = FieldSpec.builder(
        int.class,
        "SIZE_OCTETS",
        Modifier.FINAL,
        Modifier.STATIC,
        Modifier.PRIVATE);
      fb.initializer(t.getSizeInOctets().getValue().toString());
      jcb.addField(fb.build());

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
              t.matchType(new RecordFieldImplementationProcessor(f, o, jcb));
              return Unit.unit();
            }

            @Override public Unit matchFieldPaddingOctets(
              final TRecord.FieldPaddingOctets f)
            {
              return Unit.unit();
            }
          });
        offset = offset.add(f.getSizeInOctets().getValue());
      }

      final TypeSpec jc = jcb.build();

      final JavaFile.Builder jfb =
        JavaFile.builder(tp.getName().toString(), jc);

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
      final String name = JPRAJavaGenerator.getInterfaceReadableName(tn);

      final TypeSpec.Builder jcb = TypeSpec.interfaceBuilder(name);
      jcb.addModifiers(Modifier.PUBLIC);

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
      final String tn = JPRAJavaGenerator.getInterfaceWritableName(t.getName());

      final TypeSpec.Builder jcb = TypeSpec.interfaceBuilder(tn);
      jcb.addModifiers(Modifier.PUBLIC);

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
      final String wtn = JPRAJavaGenerator.getInterfaceWritableName(t_name);
      final String rtn = JPRAJavaGenerator.getInterfaceReadableName(t_name);
      final String tn = JPRAJavaGenerator.getInterfaceName(t_name);

      final TypeSpec.Builder jcb = TypeSpec.interfaceBuilder(tn);
      jcb.addModifiers(Modifier.PUBLIC);
      jcb.addSuperinterface(ClassName.get(tp.getName().toString(), wtn));
      jcb.addSuperinterface(ClassName.get(tp.getName().toString(), rtn));

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

  private enum MethodSelection
  {
    GETTERS(true, false),
    SETTERS(false, true),
    GETTERS_AND_SETTERS(true, true);

    private final boolean get;
    private final boolean set;

    MethodSelection(
      final boolean in_get,
      final boolean in_set)
    {
      this.get = in_get;
      this.set = in_set;
    }

    public boolean wantGetters()
    {
      return this.get;
    }

    public boolean wantSetters()
    {
      return this.set;
    }
  }

  private static final class RecordFieldImplementationProcessor
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

      // TODO: Generated method stub!
      throw new UnimplementedCodeException();
    }

    @Override public Unit matchInteger(
      final TIntegerType t)
    {
      this.start();
      final RecordFieldImplementationIntegerProcessor p =
        new RecordFieldImplementationIntegerProcessor(
          this.field, this.offset, this.class_builder);
      return t.matchTypeInteger(p);
    }

    @Override public Unit matchFloat(
      final TFloat t)
    {
      this.start();

      final BigInteger size = t.getSizeInBits().getValue();

      final String offset_constant =
        JPRAJavaGenerator.getOffsetConstantName(this.field.getName());
      final String getter_name =
        JPRAJavaGenerator.getGetterName(this.field.getName());
      final String setter_name =
        JPRAJavaGenerator.getSetterName(this.field.getName());

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
          "return $T.unpackDouble($N.$N(this.$N + $N))",
          Binary16.class,
          "buffer",
          iget,
          "base_index",
          offset_constant);
        this.class_builder.addMethod(getb.build());

        final MethodSpec.Builder setb = MethodSpec.methodBuilder(setter_name);
        setb.addModifiers(Modifier.PUBLIC);
        setb.addAnnotation(Override.class);
        setb.addParameter(double.class, "x", Modifier.FINAL);
        setb.returns(void.class);
        setb.addStatement(
          "this.$N.$N(this.$N + $N, $T.packDouble($N))",
          "buffer",
          iput,
          "base_index",
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
          "return this.$N.$N(this.$N + $N)",
          "buffer",
          iget,
          "base_index",
          offset_constant);
        this.class_builder.addMethod(getb.build());

        final MethodSpec.Builder setb = MethodSpec.methodBuilder(setter_name);
        setb.addModifiers(Modifier.PUBLIC);
        setb.addAnnotation(Override.class);
        setb.addParameter(itype, "x", Modifier.FINAL);
        setb.returns(void.class);
        setb.addStatement(
          "this.$N.$N(this.$N + $N, $N)",
          "buffer",
          iput,
          "base_index",
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

      // TODO: Generated method stub!
      throw new UnimplementedCodeException();
    }

    @Override public Unit matchPacked(
      final TPacked t)
    {
      this.start();

      // TODO: Generated method stub!
      throw new UnimplementedCodeException();
    }

    private void start()
    {
      final FieldSpec.Builder fb = FieldSpec.builder(
        int.class,
        JPRAJavaGenerator.getOffsetConstantName(this.field.getName()),
        Modifier.FINAL,
        Modifier.STATIC,
        Modifier.PRIVATE);
      fb.initializer(this.offset.toString());
      this.class_builder.addField(fb.build());
    }
  }

  private static final class RecordFieldImplementationIntegerProcessor
    implements TypeIntegerMatcherType<Unit, UnreachableCodeException>
  {
    private final TRecord.FieldValue field;
    private final BigInteger         offset;
    private final TypeSpec.Builder   class_builder;

    RecordFieldImplementationIntegerProcessor(
      final TRecord.FieldValue in_field,
      final BigInteger in_offset,
      final TypeSpec.Builder in_class_builder)
    {
      this.field = NullCheck.notNull(in_field);
      this.offset = NullCheck.notNull(in_offset);
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
        JPRAJavaGenerator.getOffsetConstantName(this.field.getName());
      final String getter_name =
        JPRAJavaGenerator.getGetterName(this.field.getName());
      final String setter_name =
        JPRAJavaGenerator.getSetterName(this.field.getName());

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
        iput = "putByte";
        iget = "getByte";
      }

      final MethodSpec.Builder getb = MethodSpec.methodBuilder(getter_name);
      getb.addModifiers(Modifier.PUBLIC);
      getb.addAnnotation(Override.class);
      getb.returns(itype);
      getb.addStatement(
        "return this.$N.$N(this.$N + $N)",
        "buffer",
        iget,
        "base_index",
        offset_constant);
      this.class_builder.addMethod(getb.build());

      final MethodSpec.Builder setb = MethodSpec.methodBuilder(setter_name);
      setb.addModifiers(Modifier.PUBLIC);
      setb.addAnnotation(Override.class);
      setb.addParameter(itype, "x", Modifier.FINAL);
      setb.returns(void.class);
      setb.addStatement(
        "this.$N.$N(this.$N + $N, $N)",
        "buffer",
        iput,
        "base_index",
        offset_constant,
        "x");
      this.class_builder.addMethod(setb.build());
      return Unit.unit();
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
      this.onInteger(size);

      final String getter_name =
        JPRAJavaGenerator.getGetterName(this.field.getName());
      final String setter_name =
        JPRAJavaGenerator.getSetterName(this.field.getName());
      final String getter_norm_name =
        JPRAJavaGenerator.getNormalizedGetterName(this.field.getName());
      final String setter_norm_name =
        JPRAJavaGenerator.getNormalizedSetterName(this.field.getName());

      final Class<?> rtype;
      final Class<?> nfp_class;

      if (size.compareTo(BigInteger.valueOf(64L)) > 0) {
        throw new UnimplementedCodeException();
      }

      if (size.compareTo(BigInteger.valueOf(32L)) > 0) {
        rtype = long.class;
        if (signed) {
          nfp_class = NFPSignedDoubleLong.class;
        } else {
          nfp_class = NFPUnsignedDoubleLong.class;
        }
      } else if (size.compareTo(BigInteger.valueOf(16L)) > 0) {
        rtype = int.class;
        if (signed) {
          nfp_class = NFPSignedDoubleInt.class;
        } else {
          nfp_class = NFPUnsignedDoubleInt.class;
        }
      } else if (size.compareTo(BigInteger.valueOf(8L)) > 0) {
        rtype = short.class;
        if (signed) {
          nfp_class = NFPSignedDoubleInt.class;
        } else {
          nfp_class = NFPUnsignedDoubleInt.class;
        }
      } else {
        rtype = byte.class;
        if (signed) {
          nfp_class = NFPSignedDoubleInt.class;
        } else {
          nfp_class = NFPUnsignedDoubleInt.class;
        }
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

      final MethodSpec.Builder getb =
        MethodSpec.methodBuilder(getter_norm_name);
      getb.addModifiers(Modifier.PUBLIC);
      getb.addAnnotation(Override.class);
      getb.returns(double.class);
      getb.addStatement(
        "return $T.$N(this.$N())", nfp_class, m_of, getter_name);
      this.class_builder.addMethod(getb.build());

      final MethodSpec.Builder setb =
        MethodSpec.methodBuilder(setter_norm_name);
      setb.addModifiers(Modifier.PUBLIC);
      setb.addAnnotation(Override.class);
      setb.addParameter(rtype, "x", Modifier.FINAL);
      setb.addStatement(
        "this.$N(($T) $T.$N($N))", setter_name, rtype, nfp_class, m_to, "x");
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

  private static final class RecordFieldInterfaceProcessor
    implements TypeMatcherType<Unit, UnreachableCodeException>
  {
    private final TRecord.FieldValue field;
    private final TypeSpec.Builder   class_builder;
    private final MethodSelection    methods;

    RecordFieldInterfaceProcessor(
      final TRecord.FieldValue in_field,
      final TypeSpec.Builder in_class_builder,
      final MethodSelection in_methods)
    {
      this.field = NullCheck.notNull(in_field);
      this.class_builder = NullCheck.notNull(in_class_builder);
      this.methods = NullCheck.notNull(in_methods);
    }

    @Override public Unit matchArray(final TArray t)
    {
      // TODO: Generated method stub!
      throw new UnimplementedCodeException();
    }

    @Override public Unit matchString(final TString t)
    {
      // TODO: Generated method stub!
      throw new UnimplementedCodeException();
    }

    @Override public Unit matchBooleanSet(final TBooleanSet t)
    {
      // TODO: Generated method stub!
      throw new UnimplementedCodeException();
    }

    @Override public Unit matchInteger(final TIntegerType t)
    {
      final RecordFieldInterfaceIntegerProcessor p =
        new RecordFieldInterfaceIntegerProcessor(
          this.field, this.class_builder, this.methods);
      return t.matchTypeInteger(p);
    }

    @Override public Unit matchFloat(final TFloat t)
    {
      final BigInteger size = t.getSizeInBits().getValue();

      final String getter_name =
        JPRAJavaGenerator.getGetterName(this.field.getName());
      final String setter_name =
        JPRAJavaGenerator.getSetterName(this.field.getName());

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
        pack = false;
      } else if (size.compareTo(BigInteger.valueOf(16L)) > 0) {
        itype = float.class;
        pack = false;
      } else {
        itype = char.class;
        pack = true;
      }

      if (pack) {
        if (this.methods.wantGetters()) {
          final MethodSpec.Builder getb = MethodSpec.methodBuilder(getter_name);
          getb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
          getb.returns(double.class);
          this.class_builder.addMethod(getb.build());
        }
        if (this.methods.wantSetters()) {
          final MethodSpec.Builder setb = MethodSpec.methodBuilder(setter_name);
          setb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
          setb.addParameter(double.class, "x", Modifier.FINAL);
          setb.returns(void.class);
          this.class_builder.addMethod(setb.build());
        }
      } else {
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
      }

      return Unit.unit();
    }

    @Override public Unit matchVector(final TVector t)
    {
      // TODO: Generated method stub!
      throw new UnimplementedCodeException();
    }

    @Override public Unit matchMatrix(final TMatrix t)
    {
      // TODO: Generated method stub!
      throw new UnimplementedCodeException();
    }

    @Override public Unit matchRecord(final TRecord t)
    {
      // TODO: Generated method stub!
      throw new UnimplementedCodeException();
    }

    @Override public Unit matchPacked(final TPacked t)
    {
      // TODO: Generated method stub!
      throw new UnimplementedCodeException();
    }
  }

  private static final class RecordFieldInterfaceIntegerProcessor
    implements TypeIntegerMatcherType<Unit, UnreachableCodeException>
  {
    private final TRecord.FieldValue field;
    private final TypeSpec.Builder   class_builder;
    private final MethodSelection    methods;

    RecordFieldInterfaceIntegerProcessor(
      final TRecord.FieldValue in_field,
      final TypeSpec.Builder in_class_builder,
      final MethodSelection in_methods)
    {
      this.field = NullCheck.notNull(in_field);
      this.class_builder = NullCheck.notNull(in_class_builder);
      this.methods = NullCheck.notNull(in_methods);
    }

    @Override public Unit matchIntegerUnsigned(final TIntegerUnsigned t)
    {
      return this.onInteger(t.getSizeInBits().getValue());
    }

    @Override public Unit matchIntegerSigned(final TIntegerSigned t)
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
        JPRAJavaGenerator.getGetterName(this.field.getName());
      final String setter_name =
        JPRAJavaGenerator.getSetterName(this.field.getName());

      final Class<?> itype;

      if (size.compareTo(BigInteger.valueOf(64L)) > 0) {
        throw new UnimplementedCodeException();
      }

      if (size.compareTo(BigInteger.valueOf(32L)) > 0) {
        itype = long.class;
      } else if (size.compareTo(BigInteger.valueOf(16L)) > 0) {
        itype = int.class;
      } else if (size.compareTo(BigInteger.valueOf(8L)) > 0) {
        itype = short.class;
      } else {
        itype = byte.class;
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
      this.onInteger(size);

      final String getter_norm_name =
        JPRAJavaGenerator.getNormalizedGetterName(this.field.getName());
      final String setter_norm_name =
        JPRAJavaGenerator.getNormalizedSetterName(this.field.getName());

      if (size.compareTo(BigInteger.valueOf(64L)) > 0) {
        throw new UnimplementedCodeException();
      }

      if (this.methods.wantGetters()) {
        final MethodSpec.Builder getb =
          MethodSpec.methodBuilder(getter_norm_name);
        getb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
        getb.returns(double.class);
        this.class_builder.addMethod(getb.build());
      }

      if (this.methods.wantSetters()) {
        final MethodSpec.Builder setb =
          MethodSpec.methodBuilder(setter_norm_name);
        setb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
        setb.addParameter(double.class, "x", Modifier.FINAL);
        this.class_builder.addMethod(setb.build());
      }

      return Unit.unit();
    }
  }
}
