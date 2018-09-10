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

import com.io7m.jnfp.core.NFPSignedDoubleInt;
import com.io7m.jnfp.core.NFPSignedDoubleLong;
import com.io7m.jnfp.core.NFPUnsignedDoubleInt;
import com.io7m.jnfp.core.NFPUnsignedDoubleLong;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.types.Size;
import com.io7m.jpra.model.types.SizeUnitBitsType;
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
import com.io7m.jranges.RangeInclusiveB;
import com.io7m.junreachable.UnimplementedCodeException;
import com.io7m.junreachable.UnreachableCodeException;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import io.vavr.collection.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.element.Modifier;
import java.math.BigInteger;
import java.util.Objects;

/**
 * A type matcher that produces implementation methods for a given packed field.
 */

public final class PackedFieldImplementationProcessor
  implements TypeMatcherType<Void, UnreachableCodeException>,
  TypeIntegerMatcherType<Void, UnreachableCodeException>
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(PackedFieldImplementationProcessor.class);
  }

  private final TPacked.FieldValue field;
  private final TypeSpec.Builder class_builder;
  private final BigInteger offset_bits;

  PackedFieldImplementationProcessor(
    final TPacked.FieldValue in_field,
    final BigInteger in_offset_bits,
    final TypeSpec.Builder in_class_builder)
  {
    this.field = Objects.requireNonNull(in_field, "in_field");
    this.offset_bits = Objects.requireNonNull(in_offset_bits, "Offset_bits");
    this.class_builder = Objects.requireNonNull(
      in_class_builder,
      "Class builder");
  }

  /**
   * Generate a {@code set} method that sets all packed fields at once.
   *
   * @param class_builder The class builder
   * @param t             The packed type
   */

  static void generatedPackedAllMethodImplementation(
    final TypeSpec.Builder class_builder,
    final TPacked t)
  {
    final List<TPacked.FieldType> ordered =
      t.getFieldsInDeclarationOrder();
    final BigInteger container_size = t.getSizeInBits().getValue();

    final MethodSpec.Builder setb = MethodSpec.methodBuilder("set");
    setb.addModifiers(Modifier.PUBLIC);
    setb.addAnnotation(Override.class);

    final Class<?> arith_type;
    final Class<?> container_type;
    final String iput;

    if (Objects.equals(container_size, BigInteger.valueOf(64L))) {
      container_type = long.class;
      arith_type = long.class;
      iput = "putLong";
    } else if (Objects.equals(container_size, BigInteger.valueOf(32L))) {
      container_type = int.class;
      arith_type = int.class;
      iput = "putInt";
    } else if (Objects.equals(container_size, BigInteger.valueOf(16L))) {
      container_type = short.class;
      arith_type = int.class;
      iput = "putShort";
    } else {
      container_type = byte.class;
      arith_type = int.class;
      iput = "put";
    }

    setb.addStatement("$T result = 0", arith_type);

    ordered.filter(x -> x instanceof TPacked.FieldValue)
      .map(x -> (TPacked.FieldValue) x)
      .forEach(fv -> {

        final BigInteger f_size = fv.getSize().getValue();
        final FieldName f_name = fv.getName();
        final TIntegerType f_type = fv.getType();
        final Class<?> f_class = f_type.matchTypeInteger(new GetIntegerTypeClass(f_size));

        final String f_name_text = f_name.value();
        setb.addParameter(f_class, f_name_text, Modifier.FINAL);
        final BigInteger field_size = f_type.getSizeInBits().getValue();
        final String mask = JPRAMasks.createOneMask(
          container_size.intValue(), 0, field_size.intValue() - 1);
        final BigInteger shift = fv.getBitRange().lower();

        f_type.matchTypeInteger(
          new GenerateIntegerCode(setb, arith_type, f_name_text, mask, shift, field_size));
      });

    bufferWriteStatement(t, setb, container_type, iput, "result");
    setb.returns(void.class);
    class_builder.addMethod(setb.build());
  }

  private static void bufferReadStatement(
    final TPacked t,
    final MethodSpec.Builder setb)
  {
    setb.beginControlFlow("");
    setb.addStatement("final int off = this.getByteOffset()");

    final int bytes = t.getSizeInOctets().getValue().intValue();
    for (int index = 0; index < bytes; ++index) {
      setb.addStatement(
        "this.$N.put($L, this.$N.get(off + $L))",
        "pack_buffer",
        Integer.valueOf(index),
        "buffer",
        Integer.valueOf(index));
    }

    setb.endControlFlow();
  }

  private static void bufferWriteStatement(
    final TPacked t,
    final MethodSpec.Builder setb,
    final Class<?> container_type,
    final String iput,
    final String value)
  {
    setb.beginControlFlow("");
    setb.addStatement("final int off = this.getByteOffset()");
    setb.addStatement("this.$N.$N(0, ($T) $N)", "pack_buffer", iput, container_type, value);

    final int bytes = t.getSizeInOctets().getValue().intValue();
    for (int index = 0; index < bytes; ++index) {
      setb.addStatement(
        "this.$N.put(off + $L, this.$N.get($L))",
        "buffer",
        Integer.valueOf(index),
        "pack_buffer",
        Integer.valueOf(index));
    }

    setb.endControlFlow();
  }

  private static void onInteger(
    final MethodSpec.Builder setb,
    final Class<?> arith_type,
    final String f_name_text,
    final String mask,
    final BigInteger shift)
  {
    setb.addStatement("final $T $L_mask = $L", arith_type, f_name_text, mask);
    setb.addStatement(
      "final $T $L_valu = ($L & $L_mask) << $L",
      arith_type,
      f_name_text,
      f_name_text,
      f_name_text,
      shift);
    setb.addStatement("result |= $L_valu", f_name_text);
  }

  private static void onNormalized(
    final boolean signed,
    final BigInteger field_size,
    final MethodSpec.Builder setb,
    final Class<?> arith_type,
    final String f_name_text,
    final String mask,
    final BigInteger shift)
  {
    final Class<?> nfp_class = getNFPClassFromFieldSizeAndSign(field_size, signed);
    final String m_to = getNormalizedToMethod(field_size, signed);

    setb.addStatement(
      "final $T $L_conv = $T.$N($N)",
      arith_type,
      f_name_text,
      nfp_class,
      m_to,
      f_name_text);
    setb.addStatement(
      "final $T $L_valu = $L_conv << $L",
      arith_type,
      f_name_text,
      f_name_text,
      shift);
    setb.addStatement("result |= $L_valu", f_name_text);
  }

  private static Class<?> getNFPClassFromFieldSizeAndSign(
    final BigInteger field_size,
    final boolean signed)
  {
    if (field_size.compareTo(BigInteger.valueOf(64L)) > 0) {
      throw new UnimplementedCodeException();
    }

    final Class<?> nfp_class;
    if (field_size.compareTo(BigInteger.valueOf(32L)) > 0) {
      if (signed) {
        nfp_class = NFPSignedDoubleLong.class;
      } else {
        nfp_class = NFPUnsignedDoubleLong.class;
      }
    } else if (field_size.compareTo(BigInteger.valueOf(16L)) > 0) {
      if (signed) {
        nfp_class = NFPSignedDoubleInt.class;
      } else {
        nfp_class = NFPUnsignedDoubleInt.class;
      }
    } else if (field_size.compareTo(BigInteger.valueOf(8L)) > 0) {
      if (signed) {
        nfp_class = NFPSignedDoubleInt.class;
      } else {
        nfp_class = NFPUnsignedDoubleInt.class;
      }
    } else {
      if (signed) {
        nfp_class = NFPSignedDoubleInt.class;
      } else {
        nfp_class = NFPUnsignedDoubleInt.class;
      }
    }
    return nfp_class;
  }

  private static String getNormalizedFromMethod(
    final BigInteger field_size,
    final boolean signed)
  {
    if (signed) {
      return String.format("fromSignedNormalizedWithZero%s", field_size);
    }
    return String.format("fromUnsignedNormalized%s", field_size);
  }

  private static String getNormalizedToMethod(
    final BigInteger field_size,
    final boolean signed)
  {
    if (signed) {
      return String.format("toSignedNormalizedWithZero%s", field_size);
    }
    return String.format("toUnsignedNormalized%s", field_size);
  }

  @Override
  public Void matchIntegerUnsigned(
    final TIntegerUnsigned t)
  {
    return this.onInteger(t.getSizeInBits());
  }

  private Void onInteger(final Size<SizeUnitBitsType> size)
  {
    final BigInteger container_size =
      this.field.getOwner().getSizeInBits().getValue();
    final BigInteger field_size = size.getValue();

    final String getter_name =
      JPRAGeneratedNames.getGetterName(this.field.getName());
    final String setter_name =
      JPRAGeneratedNames.getSetterName(this.field.getName());

    return this.integerGetterSetter(
      this.field.getOwner(),
      container_size,
      field_size,
      getter_name,
      setter_name);
  }

  private Void integerGetterSetter(
    final TPacked t,
    final BigInteger container_size,
    final BigInteger field_size,
    final String getter_name,
    final String setter_name)
  {
    if (field_size.compareTo(BigInteger.valueOf(64L)) > 0) {
      throw new UnimplementedCodeException();
    }

    /*
      The implementation deals with three types:

      The "container" type is the type that will be used to actually store
      packed values; all of the fields of the packed record are packed into
      a value of this type.

      The "arith" type is the type that will be used to perform operations
      upon field values; this type will be {@code int} or larger, and no
      smaller than the container type.

      The "external" type is the type that the programmer will use to interact
      with the generated code. This will be {@code int} or larger, and no
      smaller than the field size.
     */

    final Class<?> external_type =
      findExternalTypeForFieldSize(field_size);

    final IntegerGetterSetterNamesAndTypes types =
      new IntegerGetterSetterNamesAndTypes(container_size)
        .invoke();

    final BigInteger shift =
      container_size.subtract(this.offset_bits).subtract(field_size);
    final RangeInclusiveB field_bit_range = this.field.getBitRange();
    final String container_mask = JPRAMasks.createZeroMask(
      container_size.intValue(),
      field_bit_range.lower().intValue(),
      field_bit_range.upper().intValue());
    final String field_mask = JPRAMasks.createOneMask(
      container_size.intValue(), 0, field_size.intValue() - 1);

    final MethodSpec.Builder getb = MethodSpec.methodBuilder(getter_name);
    getb.addModifiers(Modifier.PUBLIC);
    getb.returns(external_type);
    getb.addAnnotation(Override.class);
    bufferReadStatement(t, getb);
    getb.addStatement(
      "final $T read = this.$N.$N(0)",
      types.getContainerType(),
      "pack_buffer",
      types.getIntegerGetName());
    getb.addStatement("return ($T) ((read >>> $L) & $L)", external_type, shift, field_mask);
    this.class_builder.addMethod(getb.build());

    final MethodSpec.Builder setb = MethodSpec.methodBuilder(setter_name);
    setb.addModifiers(Modifier.PUBLIC);
    setb.addParameter(external_type, "x", Modifier.FINAL);
    setb.addAnnotation(Override.class);
    setb.returns(void.class);
    bufferReadStatement(t, setb);
    setb.addStatement("final $T result = this.$N.$N(0)", types.getContainerType(), "pack_buffer",
                      types.getIntegerGetName());
    setb.addStatement("final $T r_mask = $L", types.getArithmeticType(), container_mask);
    setb.addStatement("final $T x_mask = $L", types.getArithmeticType(), field_mask);
    setb.addStatement("final $T x_valu = (x & x_mask) << $L", types.getArithmeticType(), shift);
    setb.addStatement("final $T w_valu = (result & r_mask) | x_valu", types.getArithmeticType());

    bufferWriteStatement(this.field.getOwner(), setb, types.getContainerType(),
                         types.getIntegerPutName(), "w_valu");
    this.class_builder.addMethod(setb.build());
    return null;
  }

  private static Class<?> findExternalTypeForFieldSize(
    final BigInteger field_size)
  {
    if (field_size.compareTo(BigInteger.valueOf(32L)) > 0) {
      return long.class;
    }
    if (field_size.compareTo(BigInteger.valueOf(16L)) > 0) {
      return int.class;
    }
    if (field_size.compareTo(BigInteger.valueOf(8L)) > 0) {
      return int.class;
    }
    return int.class;
  }

  @Override
  public Void matchIntegerSigned(
    final TIntegerSigned t)
  {
    return this.onInteger(t.getSizeInBits());
  }

  @Override
  public Void matchIntegerSignedNormalized(
    final TIntegerSignedNormalized t)
  {
    return this.onIntegerNormalized(t.getSizeInBits().getValue(), true);
  }

  private Void onIntegerNormalized(
    final BigInteger field_size,
    final boolean signed)
  {
    final BigInteger container_size =
      this.field.getOwner().getSizeInBits().getValue();

    final String getter_norm_name =
      JPRAGeneratedNames.getNormalizedGetterName(this.field.getName());
    final String setter_norm_name =
      JPRAGeneratedNames.getNormalizedSetterName(this.field.getName());
    final String getter_norm_raw_name =
      JPRAGeneratedNames.getNormalizedRawGetterName(this.field.getName());
    final String setter_norm_raw_name =
      JPRAGeneratedNames.getNormalizedRawSetterName(this.field.getName());

    this.integerGetterSetter(
      this.field.getOwner(),
      container_size,
      field_size,
      getter_norm_raw_name,
      setter_norm_raw_name);

    final Class<?> nfp_class = getNFPClassFromFieldSizeAndSign(field_size, signed);
    final String m_to = getNormalizedToMethod(field_size, signed);
    final String m_of = getNormalizedFromMethod(field_size, signed);

    final MethodSpec.Builder getb = MethodSpec.methodBuilder(getter_norm_name);
    getb.addModifiers(Modifier.PUBLIC);
    getb.addAnnotation(Override.class);
    getb.returns(double.class);
    getb.addStatement("return $T.$N(this.$N())", nfp_class, m_of, getter_norm_raw_name);
    this.class_builder.addMethod(getb.build());

    final MethodSpec.Builder setb = MethodSpec.methodBuilder(setter_norm_name);
    setb.addModifiers(Modifier.PUBLIC);
    setb.addAnnotation(Override.class);
    setb.addParameter(double.class, "x", Modifier.FINAL);
    setb.addStatement("this.$N($T.$N($N))", setter_norm_raw_name, nfp_class, m_to, "x");
    this.class_builder.addMethod(setb.build());
    return null;
  }

  @Override
  public Void matchIntegerUnsignedNormalized(
    final TIntegerUnsignedNormalized t)
  {
    return this.onIntegerNormalized(t.getSizeInBits().getValue(), false);
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

  private static final class GetIntegerTypeClass
    implements TypeIntegerMatcherType<Class<?>, UnreachableCodeException>
  {
    private final BigInteger size;

    GetIntegerTypeClass(final BigInteger in_size)
    {
      this.size = in_size;
    }

    @Override
    public Class<?> matchIntegerUnsigned(
      final TIntegerUnsigned t)
    {
      return PackedFieldInterfaceProcessor.getPackedIntegerTypeForSize(this.size);
    }

    @Override
    public Class<?> matchIntegerSigned(
      final TIntegerSigned t)
    {
      return PackedFieldInterfaceProcessor.getPackedIntegerTypeForSize(this.size);
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

  private static final class GenerateIntegerCode
    implements TypeIntegerMatcherType<Void, UnreachableCodeException>
  {
    private final MethodSpec.Builder builder;
    private final Class<?> arith_type;
    private final String f_name_text;
    private final String mask;
    private final BigInteger shift;
    private final BigInteger field_size;

    GenerateIntegerCode(
      final MethodSpec.Builder in_builder,
      final Class<?> in_arithmetic_type,
      final String in_name_text,
      final String in_mask,
      final BigInteger in_shift,
      final BigInteger in_field_size)
    {
      this.builder = in_builder;
      this.arith_type = in_arithmetic_type;
      this.f_name_text = in_name_text;
      this.mask = in_mask;
      this.shift = in_shift;
      this.field_size = in_field_size;
    }

    @Override
    public Void matchIntegerUnsigned(
      final TIntegerUnsigned t)
    {
      onInteger(this.builder, this.arith_type, this.f_name_text, this.mask, this.shift);
      return null;
    }

    @Override
    public Void matchIntegerSigned(
      final TIntegerSigned t)
    {
      onInteger(this.builder, this.arith_type, this.f_name_text, this.mask, this.shift);
      return null;
    }

    @Override
    public Void matchIntegerSignedNormalized(
      final TIntegerSignedNormalized t)
    {
      onNormalized(
        true,
        this.field_size,
        this.builder,
        this.arith_type,
        this.f_name_text,
        this.mask,
        this.shift);
      return null;
    }

    @Override
    public Void matchIntegerUnsignedNormalized(
      final TIntegerUnsignedNormalized t)
    {
      onNormalized(
        false,
        this.field_size,
        this.builder,
        this.arith_type,
        this.f_name_text,
        this.mask,
        this.shift);
      return null;
    }
  }

  private static final class IntegerGetterSetterNamesAndTypes
  {
    private final BigInteger container_size;
    private String integer_get_name;
    private String integer_put_name;
    private Class<?> arithmetic_type;
    private Class<?> container_type;

    IntegerGetterSetterNamesAndTypes(
      final BigInteger in_container_size)
    {
      this.container_size = in_container_size;
    }

    String getIntegerGetName()
    {
      return this.integer_get_name;
    }

    String getIntegerPutName()
    {
      return this.integer_put_name;
    }

    Class<?> getArithmeticType()
    {
      return this.arithmetic_type;
    }

    Class<?> getContainerType()
    {
      return this.container_type;
    }

    IntegerGetterSetterNamesAndTypes invoke()
    {
      if (Objects.equals(this.container_size, BigInteger.valueOf(64L))) {
        this.container_type = long.class;
        this.arithmetic_type = long.class;
        this.integer_get_name = "getLong";
        this.integer_put_name = "putLong";
      } else if (Objects.equals(this.container_size, BigInteger.valueOf(32L))) {
        this.container_type = int.class;
        this.arithmetic_type = int.class;
        this.integer_get_name = "getInt";
        this.integer_put_name = "putInt";
      } else if (Objects.equals(this.container_size, BigInteger.valueOf(16L))) {
        this.container_type = short.class;
        this.arithmetic_type = int.class;
        this.integer_get_name = "getShort";
        this.integer_put_name = "putShort";
      } else {
        this.container_type = byte.class;
        this.arithmetic_type = int.class;
        this.integer_get_name = "get";
        this.integer_put_name = "put";
      }
      return this;
    }
  }
}
