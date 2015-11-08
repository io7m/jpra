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
import com.io7m.jnfp.core.NFPSignedDoubleInt;
import com.io7m.jnfp.core.NFPSignedDoubleLong;
import com.io7m.jnfp.core.NFPUnsignedDoubleInt;
import com.io7m.jnfp.core.NFPUnsignedDoubleLong;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.contexts.PackageContextType;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.names.PackageNameQualified;
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
import com.io7m.jpra.runtime.java.JPRACursorByteReadableType;
import com.io7m.jpra.runtime.java.JPRAValueType;
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
    final String raw = name.toString();
    final String text = JPRAJavaGenerator.getRecased(raw);
    final StringBuilder sb = new StringBuilder(128);
    sb.append("get");
    sb.append(text);
    return sb.toString();
  }

  private static String getRecased(final String raw)
  {
    final String spaced = raw.replaceAll("_", " ");
    final String capped = WordUtils.capitalize(spaced);
    return capped.replaceAll(" ", "");
  }

  private static String getSetterName(final FieldName name)
  {
    final String text = JPRAJavaGenerator.getRecased(name.toString());
    final StringBuilder sb = new StringBuilder(128);
    sb.append("set");
    sb.append(text);
    return sb.toString();
  }

  private static String getNormalizedSetterName(final FieldName name)
  {
    final String text = JPRAJavaGenerator.getRecased(name.toString());
    final StringBuilder sb = new StringBuilder(128);
    sb.append("set");
    sb.append(text);
    sb.append("N");
    return sb.toString();
  }

  private static String getNormalizedGetterName(final FieldName name)
  {
    final String text = JPRAJavaGenerator.getRecased(name.toString());
    final StringBuilder sb = new StringBuilder(128);
    sb.append("get");
    sb.append(text);
    sb.append("N");
    return sb.toString();
  }

  private static String getGetterBooleanSetName(
    final FieldName base_name,
    final FieldName field_name)
  {
    final String base_text = JPRAJavaGenerator.getRecased(base_name.toString());
    final String field_text =
      JPRAJavaGenerator.getRecased(field_name.toString());

    final StringBuilder sb = new StringBuilder(128);
    sb.append("get");
    sb.append(base_text);
    sb.append(field_text);
    return sb.toString();
  }

  private static String getSetterBooleanSetName(
    final FieldName base_name,
    final FieldName field_name)
  {
    final String base_text = JPRAJavaGenerator.getRecased(base_name.toString());
    final String field_text =
      JPRAJavaGenerator.getRecased(field_name.toString());

    final StringBuilder sb = new StringBuilder(128);
    sb.append("set");
    sb.append(base_text);
    sb.append(field_text);
    return sb.toString();
  }

  private static String getGetterRecordReadableName(final TypeName name)
  {
    final String text = JPRAJavaGenerator.getRecased(name.toString());
    final StringBuilder sb = new StringBuilder(128);
    sb.append("get");
    sb.append(text);
    sb.append("Readable");
    return sb.toString();
  }

  private static String getGetterRecordWritableName(final TypeName name)
  {
    final String text = JPRAJavaGenerator.getRecased(name.toString());
    final StringBuilder sb = new StringBuilder(128);
    sb.append("get");
    sb.append(text);
    sb.append("Writable");
    return sb.toString();
  }

  @Override
  public String getRecordImplementationByteBufferedName(final TypeName t)
  {
    return t.getValue() + "ByteBuffered";
  }

  @Override public String getRecordInterfaceReadableName(final TypeName t)
  {
    return t.getValue() + "ReadableType";
  }

  @Override public String getRecordInterfaceWritableName(final TypeName t)
  {
    return t.getValue() + "WritableType";
  }

  @Override public String getRecordInterfaceName(final TypeName t)
  {
    return t.getValue() + "Type";
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
      final String tn = this.getRecordImplementationByteBufferedName(t_name);
      final String in = this.getRecordInterfaceName(t_name);

      final ClassName imp_name = ClassName.get(tp.getName().toString(), tn);
      final ClassName int_name = ClassName.get(tp.getName().toString(), in);
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

      final FieldSpec.Builder fb = FieldSpec.builder(
        int.class,
        "SIZE_OCTETS",
        Modifier.FINAL,
        Modifier.STATIC,
        Modifier.PRIVATE);
      fb.initializer(t.getSizeInOctets().getValue().toString());
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

      final JavaFile.Builder jfb =
        JavaFile.builder(tp.getName().toString(), jcb.build());

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
      final String tn = this.getRecordInterfaceName(t_name);

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

  private String getFieldName(final FieldName f_name)
  {
    return String.format("field_%s", f_name.getValue());
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
        iput = "put";
        iget = "get";
      }

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

      final Class<?> r_type;
      final Class<?> nfp_class;

      if (size.compareTo(BigInteger.valueOf(64L)) > 0) {
        throw new UnimplementedCodeException();
      }

      if (size.compareTo(BigInteger.valueOf(32L)) > 0) {
        r_type = long.class;
        if (signed) {
          nfp_class = NFPSignedDoubleLong.class;
        } else {
          nfp_class = NFPUnsignedDoubleLong.class;
        }
      } else if (size.compareTo(BigInteger.valueOf(16L)) > 0) {
        r_type = int.class;
        if (signed) {
          nfp_class = NFPSignedDoubleInt.class;
        } else {
          nfp_class = NFPUnsignedDoubleInt.class;
        }
      } else if (size.compareTo(BigInteger.valueOf(8L)) > 0) {
        r_type = short.class;
        if (signed) {
          nfp_class = NFPSignedDoubleInt.class;
        } else {
          nfp_class = NFPUnsignedDoubleInt.class;
        }
      } else {
        r_type = byte.class;
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
      setb.addParameter(double.class, "x", Modifier.FINAL);
      setb.addStatement(
        "this.$N(($T) $T.$N($N))", setter_name, r_type, nfp_class, m_to, "x");
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

  private final class RecordFieldImplementationConstructorProcessor
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
      final String t_imp_name =
        JPRAJavaGenerator.this.getRecordImplementationByteBufferedName(
          t.getName());
      final String t_int_name =
        JPRAJavaGenerator.this.getRecordInterfaceName(t.getName());

      final FieldName f_name = this.field.getName();
      final String field_name = JPRAJavaGenerator.this.getFieldName(f_name);
      final String offset_name =
        JPRAJavaGenerator.getOffsetConstantName(f_name);
      this.constructor_builder.addStatement(
        "this.$N = $N.newValueWithOffset($N, $N, $N)",
        field_name,
        t_imp_name,
        "in_buffer",
        "in_pointer",
        offset_name);

      final PackageNameQualified p = t.getPackageContext().getName();
      final ClassName t_cn = ClassName.get(p.toString(), t_int_name);
      this.class_builder.addField(
        t_cn, field_name, Modifier.FINAL, Modifier.PRIVATE);

      return Unit.unit();
    }

    @Override public Unit matchPacked(final TPacked t)
    {
      return Unit.unit();
    }
  }

  private final class RecordFieldImplementationProcessor
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
          JPRAJavaGenerator.getGetterBooleanSetName(this.field.getName(), f);
        final String offset_name = JPRAJavaGenerator.getOffsetConstantName(
          this.field.getName());

        final MethodSpec.Builder getb = MethodSpec.methodBuilder(getter_name);
        getb.addAnnotation(Override.class);
        getb.addModifiers(Modifier.PUBLIC);
        getb.returns(boolean.class);
        getb.addStatement("final int z = 1 << $L", Integer.valueOf(bit));
        getb.addStatement(
          "final int i = ((int) this.getByteOffsetFor($N)) + $L",
          offset_name,
          Integer.valueOf(octet));
        getb.addStatement("final int k = this.buffer.get(i)");
        getb.addStatement("return (k & z) == z");
        this.class_builder.addMethod(getb.build());

        final String setter_name =
          JPRAJavaGenerator.getGetterBooleanSetName(this.field.getName(), f);
        final MethodSpec.Builder setb = MethodSpec.methodBuilder(setter_name);
        setb.addAnnotation(Override.class);
        setb.addModifiers(Modifier.PUBLIC);
        setb.addParameter(boolean.class, "x", Modifier.FINAL);
        setb.addStatement("final int z = 1 << $L", Integer.valueOf(bit));
        setb.addStatement(
          "final int i = ((int) this.getByteOffsetFor($N)) + $L",
          offset_name,
          Integer.valueOf(octet));
        setb.addStatement("final int k = this.buffer.get(i)");
        setb.addStatement("final int q = k | z");
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

      final String reader_name =
        JPRAJavaGenerator.getGetterRecordReadableName(t.getName());
      final String writer_name =
        JPRAJavaGenerator.getGetterRecordWritableName(t.getName());

      final String target_pack = t.getPackageContext().getName().toString();
      final String target_class_read =
        JPRAJavaGenerator.this.getRecordInterfaceReadableName(t.getName());
      final ClassName target_read =
        ClassName.get(target_pack, target_class_read);

      final String target_class_write =
        JPRAJavaGenerator.this.getRecordInterfaceName(t.getName());
      final ClassName target_write =
        ClassName.get(target_pack, target_class_write);

      final String f_name =
        JPRAJavaGenerator.this.getFieldName(this.field.getName());

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

      return Unit.unit();
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

  private final class RecordFieldInterfaceProcessor
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
      final ImmutableList<FieldName> ordered = t.getFieldsInDeclarationOrder();
      for (final FieldName f : ordered) {
        if (this.methods.wantGetters()) {
          final String getter_name =
            JPRAJavaGenerator.getGetterBooleanSetName(this.field.getName(), f);
          final MethodSpec.Builder getb = MethodSpec.methodBuilder(getter_name);
          getb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
          getb.returns(boolean.class);
          this.class_builder.addMethod(getb.build());
        }

        if (this.methods.wantSetters()) {
          final String setter_name =
            JPRAJavaGenerator.getGetterBooleanSetName(this.field.getName(), f);
          final MethodSpec.Builder setb = MethodSpec.methodBuilder(setter_name);
          setb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
          setb.addParameter(boolean.class, "x", Modifier.FINAL);
          this.class_builder.addMethod(setb.build());
        }
      }
      return Unit.unit();
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
      if (this.methods.wantGetters()) {
        final String getter_name =
          JPRAJavaGenerator.getGetterRecordReadableName(t.getName());

        final String target_pack = t.getPackageContext().getName().toString();
        final String target_class =
          JPRAJavaGenerator.this.getRecordInterfaceReadableName(t.getName());
        final ClassName target = ClassName.get(target_pack, target_class);

        final MethodSpec.Builder getb = MethodSpec.methodBuilder(getter_name);
        getb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
        getb.returns(target);
        this.class_builder.addMethod(getb.build());
      }

      if (this.methods.wantSetters()) {
        final String setter_name =
          JPRAJavaGenerator.getGetterRecordWritableName(t.getName());

        final String target_pack = t.getPackageContext().getName().toString();
        final String target_class =
          JPRAJavaGenerator.this.getRecordInterfaceWritableName(t.getName());
        final ClassName target = ClassName.get(target_pack, target_class);

        final MethodSpec.Builder setb = MethodSpec.methodBuilder(setter_name);
        setb.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
        setb.returns(target);
        this.class_builder.addMethod(setb.build());
      }

      return Unit.unit();
    }

    @Override public Unit matchPacked(final TPacked t)
    {
      // TODO: Generated method stub!
      throw new UnimplementedCodeException();
    }
  }
}
