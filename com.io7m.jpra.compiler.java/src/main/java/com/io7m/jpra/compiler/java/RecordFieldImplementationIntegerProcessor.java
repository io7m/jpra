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
import com.io7m.jpra.model.types.TIntegerSigned;
import com.io7m.jpra.model.types.TIntegerSignedNormalized;
import com.io7m.jpra.model.types.TIntegerUnsigned;
import com.io7m.jpra.model.types.TIntegerUnsignedNormalized;
import com.io7m.jpra.model.types.TRecord;
import com.io7m.jpra.model.types.TypeIntegerMatcherType;
import com.io7m.junreachable.UnimplementedCodeException;
import com.io7m.junreachable.UnreachableCodeException;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.math.BigInteger;
import java.util.Objects;

/**
 * A type matcher that produces implementation methods for a given integer type record fields.
 */

final class RecordFieldImplementationIntegerProcessor
  implements TypeIntegerMatcherType<Void, UnreachableCodeException>
{
  private final TRecord.FieldValue field;
  private final TypeSpec.Builder class_builder;

  RecordFieldImplementationIntegerProcessor(
    final TRecord.FieldValue in_field,
    final TypeSpec.Builder in_class_builder)
  {
    this.field = Objects.requireNonNull(in_field, "Field");
    this.class_builder = Objects.requireNonNull(
      in_class_builder,
      "Class builder");
  }

  /**
   * Generate a method to return a value in normalized floating point form.
   */

  private static MethodSpec generateNormalizedGetter(
    final boolean signed,
    final String getter_norm_name,
    final String getter_norm_raw_name,
    final String conv,
    final Class<?> nfp_class,
    final Class<?> r_class,
    final Class<?> r_type,
    final Class<?> i_type,
    final String m_of)
  {
    final MethodSpec.Builder getb = MethodSpec.methodBuilder(getter_norm_name);
    getb.addModifiers(Modifier.PUBLIC);
    getb.addAnnotation(Override.class);
    getb.returns(double.class);

    if (signed) {
      getb.addStatement("return $T.$N(this.$N())", nfp_class, m_of, getter_norm_raw_name);
    } else {

      /*
        Types of different sizes require explicit unsigned conversions.
       */

      getb.addStatement("final $T x = this.$N()", r_type, getter_norm_raw_name);
      if (!Objects.equals(i_type, r_type)) {
        getb.addStatement("final $T y = $T.$N(x)", i_type, r_class, conv);
        getb.addStatement("return $T.$N(y)", nfp_class, m_of);
      } else {
        getb.addStatement("return $T.$N(x)", nfp_class, m_of);
      }
    }

    return getb.build();
  }

  @Override
  public Void matchIntegerUnsigned(
    final TIntegerUnsigned t)
  {
    return this.onInteger(t.getSizeInBits().getValue());
  }

  @Override
  public Void matchIntegerSigned(
    final TIntegerSigned t)
  {
    return this.onInteger(t.getSizeInBits().getValue());
  }

  /**
   * Generate a set of methods for setting and retrieving integer values.
   *
   * @param size The size of the integer
   */

  private Void onInteger(final BigInteger size)
  {
    final String offset_constant =
      JPRAGeneratedNames.getOffsetConstantName(this.field.getName());
    final String getter_name =
      JPRAGeneratedNames.getGetterName(this.field.getName());
    final String setter_name =
      JPRAGeneratedNames.getSetterName(this.field.getName());

    if (size.compareTo(BigInteger.valueOf(64L)) > 0) {
      throw new UnimplementedCodeException();
    }

    /*
      Determine the type and methods used to put/get values to/from the
      underlying byte buffer.
     */

    final Class<?> itype;
    final String iget;
    final String iput;
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

    this.integerGetter(offset_constant, getter_name, iget, itype);
    this.integerSetter(offset_constant, setter_name, iput, itype);
    return null;
  }

  /**
   * Generate a method that sets the raw integer value of a field.
   *
   * @param offset_constant The offset constant
   * @param setter_name     The name of the resulting method
   * @param iput            The method that will be called on the underlying byte buffer
   * @param itype           The input type
   */

  private void integerSetter(
    final String offset_constant,
    final String setter_name,
    final String iput,
    final Class<?> itype)
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
    this.class_builder.addMethod(setb.build());
  }

  /**
   * Generate a method that retrieved the raw integer value of a field.
   *
   * @param offset_constant The offset constant
   * @param getter_name     The name of the resulting method
   * @param iget            The method that will be called on the underlying byte buffer
   * @param itype           The output type
   */

  private void integerGetter(
    final String offset_constant,
    final String getter_name,
    final String iget,
    final Class<?> itype)
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
    this.class_builder.addMethod(getb.build());
  }

  @Override
  public Void matchIntegerSignedNormalized(
    final TIntegerSignedNormalized t)
  {
    final BigInteger size = t.getSizeInBits().getValue();
    this.onIntegerNormalized(size, true);
    return null;
  }

  /**
   * Generate a set of methods for setting and retrieving normalized integer values.
   *
   * @param size   The size of the integer
   * @param signed {@code true} iff the integer type is signed
   */

  private void onIntegerNormalized(
    final BigInteger size,
    final boolean signed)
  {
    final String offset_constant =
      JPRAGeneratedNames.getOffsetConstantName(this.field.getName());
    final String getter_norm_name =
      JPRAGeneratedNames.getNormalizedGetterName(this.field.getName());
    final String setter_norm_name =
      JPRAGeneratedNames.getNormalizedSetterName(this.field.getName());
    final String getter_norm_raw_name =
      JPRAGeneratedNames.getNormalizedRawGetterName(this.field.getName());
    final String setter_norm_raw_name =
      JPRAGeneratedNames.getNormalizedRawSetterName(this.field.getName());

    if (size.compareTo(BigInteger.valueOf(64L)) > 0) {
      throw new UnimplementedCodeException();
    }

    final IntegerNormalizedTypes types =
      new IntegerNormalizedTypes(size, signed)
        .invoke();

    /*
      Construct names of the methods used to convert values to/from
      normalized fixed-point form.
     */

    final String m_to;
    final String m_of;
    if (signed) {
      m_to = String.format("toSignedNormalizedWithZero%s", size);
      m_of = String.format("fromSignedNormalizedWithZero%s", size);
    } else {
      m_to = String.format("toUnsignedNormalized%s", size);
      m_of = String.format("fromUnsignedNormalized%s", size);
    }

    /*
      Generate raw getters and setters.
     */

    this.integerGetter(
      offset_constant,
      getter_norm_raw_name,
      types.getIntegerGetName(),
      types.getReturnType());

    this.integerSetter(
      offset_constant,
      setter_norm_raw_name,
      types.getIntegerPutName(),
      types.getReturnType());

    this.class_builder.addMethod(generateNormalizedGetter(
      signed,
      getter_norm_name,
      getter_norm_raw_name,
      types.getIntegerConversionName(),
      types.getNfpClass(),
      types.getReturnClass(),
      types.getReturnType(),
      types.getInputType(),
      m_of));

    this.generatedNormalizedSetter(
      setter_norm_name, setter_norm_raw_name,
      types.getNfpClass(),
      types.getReturnType(), m_to);
  }

  /**
   * Generate a method to set a value in normalized floating point form.
   */

  private void generatedNormalizedSetter(
    final String setter_norm_name,
    final String setter_norm_raw_name,
    final Class<?> nfp_class,
    final Class<?> r_type,
    final String m_to)
  {
    final MethodSpec.Builder setb = MethodSpec.methodBuilder(setter_norm_name);
    setb.addModifiers(Modifier.PUBLIC);
    setb.addAnnotation(Override.class);
    setb.addParameter(double.class, "x", Modifier.FINAL);
    setb.addStatement(
      "this.$N(($T) $T.$N($N))",
      setter_norm_raw_name,
      r_type,
      nfp_class,
      m_to,
      "x");
    this.class_builder.addMethod(setb.build());
  }

  @Override
  public Void matchIntegerUnsignedNormalized(
    final TIntegerUnsignedNormalized t)
  {
    final BigInteger size = t.getSizeInBits().getValue();
    this.onIntegerNormalized(size, false);
    return null;
  }

  private static final class IntegerNormalizedTypes
  {
    private BigInteger size;
    private boolean signed;
    private String integer_put_name;
    private String integer_get_name;
    private String integer_conversion_name;
    private Class<?> nfp_class;
    private Class<?> return_class;
    private Class<?> return_type;
    private Class<?> input_type;

    IntegerNormalizedTypes(
      final BigInteger in_size,
      final boolean in_signed)
    {
      this.size = in_size;
      this.signed = in_signed;
    }

    String getIntegerPutName()
    {
      return this.integer_put_name;
    }

    String getIntegerGetName()
    {
      return this.integer_get_name;
    }

    String getIntegerConversionName()
    {
      return this.integer_conversion_name;
    }

    Class<?> getNfpClass()
    {
      return this.nfp_class;
    }

    Class<?> getReturnClass()
    {
      return this.return_class;
    }

    Class<?> getReturnType()
    {
      return this.return_type;
    }

    Class<?> getInputType()
    {
      return this.input_type;
    }

    /**
     * Determine the type and methods used to put/get values to/from the underlying byte buffer.
     * Additionally, a reference to the corresponding boxed type is necessary to allow for access to
     * functions to convert values to/from unsigned types.
     */

    IntegerNormalizedTypes invoke()
    {
      if (this.size.compareTo(BigInteger.valueOf(32L)) > 0) {
        this.return_type = long.class;
        this.input_type = long.class;
        this.return_class = Long.class;
        if (this.signed) {
          this.nfp_class = NFPSignedDoubleLong.class;
        } else {
          this.nfp_class = NFPUnsignedDoubleLong.class;
        }
        this.integer_put_name = "putLong";
        this.integer_get_name = "getLong";
        this.integer_conversion_name = "toUnsignedLong";
      } else if (this.size.compareTo(BigInteger.valueOf(16L)) > 0) {
        this.return_type = int.class;
        this.input_type = int.class;
        this.return_class = Integer.class;
        if (this.signed) {
          this.nfp_class = NFPSignedDoubleInt.class;
        } else {
          this.nfp_class = NFPUnsignedDoubleInt.class;
        }
        this.integer_put_name = "putInt";
        this.integer_get_name = "getInt";
        this.integer_conversion_name = "toUnsignedInt";
      } else if (this.size.compareTo(BigInteger.valueOf(8L)) > 0) {
        this.return_type = short.class;
        this.input_type = int.class;
        this.return_class = Short.class;
        if (this.signed) {
          this.nfp_class = NFPSignedDoubleInt.class;
        } else {
          this.nfp_class = NFPUnsignedDoubleInt.class;
        }
        this.integer_put_name = "putShort";
        this.integer_get_name = "getShort";
        this.integer_conversion_name = "toUnsignedInt";
      } else {
        this.return_type = byte.class;
        this.input_type = int.class;
        this.return_class = Byte.class;
        if (this.signed) {
          this.nfp_class = NFPSignedDoubleInt.class;
        } else {
          this.nfp_class = NFPUnsignedDoubleInt.class;
        }
        this.integer_put_name = "put";
        this.integer_get_name = "get";
        this.integer_conversion_name = "toUnsignedInt";
      }
      return this;
    }
  }
}
