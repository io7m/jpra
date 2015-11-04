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

package com.io7m.jpra.tests.compiler.core.parser;

import com.io7m.jpra.compiler.core.parser.JPRAParseErrorCode;
import com.io7m.jpra.compiler.core.parser.JPRAParserType;
import com.io7m.jpra.model.names.TypeReference;
import com.io7m.jpra.model.size_expressions.SizeExprConstant;
import com.io7m.jpra.model.size_expressions.SizeExprInBits;
import com.io7m.jpra.model.size_expressions.SizeExprInOctets;
import com.io7m.jpra.model.size_expressions.SizeExprType;
import com.io7m.jpra.model.type_expressions.TypeExprArray;
import com.io7m.jpra.model.type_expressions.TypeExprBooleanSet;
import com.io7m.jpra.model.type_expressions.TypeExprFloat;
import com.io7m.jpra.model.type_expressions.TypeExprIntegerSigned;
import com.io7m.jpra.model.type_expressions.TypeExprIntegerSignedNormalized;
import com.io7m.jpra.model.type_expressions.TypeExprIntegerUnsigned;
import com.io7m.jpra.model.type_expressions.TypeExprIntegerUnsignedNormalized;
import com.io7m.jpra.model.type_expressions.TypeExprMatrix;
import com.io7m.jpra.model.type_expressions.TypeExprName;
import com.io7m.jpra.model.type_expressions.TypeExprString;
import com.io7m.jpra.model.type_expressions.TypeExprType;
import com.io7m.jpra.model.type_expressions.TypeExprVector;
import com.io7m.jsx.SExpressionType;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigInteger;

@SuppressWarnings("unchecked") public abstract class JPRAParserContract
{
  @Rule public final ExpectedException expected = ExpectedException.none();

  protected abstract JPRAParserType getParser();

  protected abstract SExpressionType newFileSExpr(
    final String name);

  protected abstract SExpressionType newStringSExpr(
    final String expr);

  @Test public final void testTypeExprIntegerUnsigned32_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(integer unsigned)");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprInteger_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(integer)");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprInteger_Error1()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(integer ())");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprInteger_Error2()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(integer \"x\")");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprInteger_Error3()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(integer signed \"x\")");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_SYMBOL_OR_LIST_GOT_QUOTED_STRING));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprInteger_Error4()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(integer signed ())");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_NON_EMPTY_LIST));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprInteger_Error5()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(integer raspberry 23)");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.UNRECOGNIZED_INTEGER_TYPE_KEYWORD));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprIntegerUnsigned32_Error1()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(integer unsigned q)");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.INVALID_INTEGER_CONSTANT));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprIntegerUnsigned32_Correct0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(integer unsigned 32)");
    final JPRAParserType p = this.getParser();

    final TypeExprType<?, ?, ?, ?, ?> t_raw = p.parseTypeExpression(e);
    Assert.assertEquals(TypeExprIntegerUnsigned.class, t_raw.getClass());
    final TypeExprIntegerUnsigned<?, ?, ?, ?, ?> t =
      TypeExprIntegerUnsigned.class.cast(t_raw);

    Assert.assertEquals(SizeExprConstant.class, t.getSize().getClass());
    final SizeExprConstant<?, ?, ?, ?, ?> s =
      SizeExprConstant.class.cast(t.getSize());
    Assert.assertEquals(BigInteger.valueOf(32L), s.getValue());
  }

  @Test public final void testTypeExprIntegerSigned32_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(integer signed)");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprIntegerSigned32_Error1()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(integer signed q)");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.INVALID_INTEGER_CONSTANT));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprIntegerSigned32_Correct0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(integer signed 32)");
    final JPRAParserType p = this.getParser();

    final TypeExprType<?, ?, ?, ?, ?> t_raw = p.parseTypeExpression(e);
    Assert.assertEquals(TypeExprIntegerSigned.class, t_raw.getClass());
    final TypeExprIntegerSigned<?, ?, ?, ?, ?> t =
      TypeExprIntegerSigned.class.cast(t_raw);

    Assert.assertEquals(SizeExprConstant.class, t.getSize().getClass());
    final SizeExprConstant<?, ?, ?, ?, ?> s =
      SizeExprConstant.class.cast(t.getSize());
    Assert.assertEquals(BigInteger.valueOf(32L), s.getValue());
  }

  @Test public final void testTypeExprIntegerSignedNormalized32_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr(
      "(integer signed-normalized)");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprIntegerSignedNormalized32_Error1()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr(
      "(integer signed-normalized q)");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.INVALID_INTEGER_CONSTANT));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprIntegerSignedNormalized32_Correct0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr(
      "(integer signed-normalized 32)");
    final JPRAParserType p = this.getParser();

    final TypeExprType<?, ?, ?, ?, ?> t_raw = p.parseTypeExpression(e);
    Assert.assertEquals(
      TypeExprIntegerSignedNormalized.class, t_raw.getClass());
    final TypeExprIntegerSignedNormalized<?, ?, ?, ?, ?> t =
      TypeExprIntegerSignedNormalized.class.cast(t_raw);

    Assert.assertEquals(SizeExprConstant.class, t.getSize().getClass());
    final SizeExprConstant<?, ?, ?, ?, ?> s =
      SizeExprConstant.class.cast(t.getSize());
    Assert.assertEquals(BigInteger.valueOf(32L), s.getValue());
  }

  @Test public final void testTypeExprIntegerUnsignedNormalized32_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr(
      "(integer unsigned-normalized)");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprIntegerUnsignedNormalized32_Error1()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr(
      "(integer unsigned-normalized q)");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.INVALID_INTEGER_CONSTANT));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprIntegerUnsignedNormalized32_Correct0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr(
      "(integer unsigned-normalized 32)");
    final JPRAParserType p = this.getParser();

    final TypeExprType<?, ?, ?, ?, ?> t_raw = p.parseTypeExpression(e);
    Assert.assertEquals(
      TypeExprIntegerUnsignedNormalized.class, t_raw.getClass());
    final TypeExprIntegerUnsignedNormalized<?, ?, ?, ?, ?> t =
      TypeExprIntegerUnsignedNormalized.class.cast(t_raw);

    Assert.assertEquals(SizeExprConstant.class, t.getSize().getClass());
    final SizeExprConstant<?, ?, ?, ?, ?> s =
      SizeExprConstant.class.cast(t.getSize());
    Assert.assertEquals(BigInteger.valueOf(32L), s.getValue());
  }

  @Test public final void testTypeExpr_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("()");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_NON_EMPTY_LIST));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExpr_Error1()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("\"\"");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_SYMBOL_OR_LIST_GOT_QUOTED_STRING));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExpr_Error2()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(unknown)");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.UNRECOGNIZED_TYPE_KEYWORD));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExpr_Error3()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(\"\")");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_SYMBOL_GOT_QUOTED_STRING));
    p.parseTypeExpression(e);
  }

  @Test public final void testSizeExpr_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("()");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_NON_EMPTY_LIST));
    p.parseSizeExpression(e);
  }

  @Test public final void testSizeExpr_Error1()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("\"\"");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_SYMBOL_OR_LIST_GOT_QUOTED_STRING));
    p.parseSizeExpression(e);
  }

  @Test public final void testSizeExpr_Error2()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(size-in-meters T)");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.UNRECOGNIZED_SIZE_FUNCTION));
    p.parseSizeExpression(e);
  }

  @Test public final void testSizeExpr_Constant_OK0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("32");
    final JPRAParserType p = this.getParser();

    final SizeExprType<?, ?, ?, ?, ?> s = p.parseSizeExpression(e);
    final SizeExprConstant<?, ?, ?, ?, ?> sc = SizeExprConstant.class.cast(s);
    Assert.assertEquals(BigInteger.valueOf(32L), sc.getValue());
  }

  @Test public final void testSizeExpr_Bits_OK0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(size-in-bits T)");
    final JPRAParserType p = this.getParser();

    final SizeExprType<?, ?, ?, ?, ?> s = p.parseSizeExpression(e);
    final SizeExprInBits<?, ?, ?, ?, ?> sb = SizeExprInBits.class.cast(s);
    final TypeExprName<?, ?, ?, ?, ?> tn =
      TypeExprName.class.cast(sb.getTypeExpression());
    final TypeReference tr = TypeReference.class.cast(tn.getName());
    Assert.assertEquals("T", tr.getType().getValue());
  }

  @Test public final void testSizeExpr_Bits_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(size-in-bits)");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseSizeExpression(e);
  }

  @Test public final void testSizeExpr_Octets_OK0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(size-in-octets T)");
    final JPRAParserType p = this.getParser();

    final SizeExprType<?, ?, ?, ?, ?> s = p.parseSizeExpression(e);
    final SizeExprInOctets<?, ?, ?, ?, ?> sb = SizeExprInOctets.class.cast(s);
    final TypeExprName<?, ?, ?, ?, ?> tn =
      TypeExprName.class.cast(sb.getTypeExpression());
    final TypeReference tr = TypeReference.class.cast(tn.getName());
    Assert.assertEquals("T", tr.getType().getValue());
  }

  @Test public final void testSizeExpr_Octets_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(size-in-octets)");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseSizeExpression(e);
  }

  @Test public final void testTypeExprFloat_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(float)");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprFloat_Error1()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(float ())");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_NON_EMPTY_LIST));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprFloat_Error2()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(float \"x\")");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_SYMBOL_OR_LIST_GOT_QUOTED_STRING));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprFloat_Correct0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(float 32)");
    final JPRAParserType p = this.getParser();

    final TypeExprType<?, ?, ?, ?, ?> t_raw = p.parseTypeExpression(e);
    Assert.assertEquals(TypeExprFloat.class, t_raw.getClass());
    final TypeExprFloat<?, ?, ?, ?, ?> t = TypeExprFloat.class.cast(t_raw);

    Assert.assertEquals(SizeExprConstant.class, t.getSize().getClass());
    final SizeExprConstant<?, ?, ?, ?, ?> s =
      SizeExprConstant.class.cast(t.getSize());
    Assert.assertEquals(BigInteger.valueOf(32L), s.getValue());
  }

  @Test public final void testTypeExprVector_Correct0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(vector T 3)");
    final JPRAParserType p = this.getParser();

    final TypeExprType<?, ?, ?, ?, ?> t_raw = p.parseTypeExpression(e);
    Assert.assertEquals(TypeExprVector.class, t_raw.getClass());
    final TypeExprVector<?, ?, ?, ?, ?> t = TypeExprVector.class.cast(t_raw);

    Assert.assertEquals(SizeExprConstant.class, t.getElementCount().getClass());
    final SizeExprConstant<?, ?, ?, ?, ?> s =
      SizeExprConstant.class.cast(t.getElementCount());
    Assert.assertEquals(BigInteger.valueOf(3L), s.getValue());
  }

  @Test public final void testTypeExprVector_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(vector)");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprVector_Error1()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(vector 32)");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprVector_Error2()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(vector T ())");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_NON_EMPTY_LIST));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprMatrix_Correct0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(matrix T 4 2)");
    final JPRAParserType p = this.getParser();

    final TypeExprType<?, ?, ?, ?, ?> t_raw = p.parseTypeExpression(e);
    Assert.assertEquals(TypeExprMatrix.class, t_raw.getClass());
    final TypeExprMatrix<?, ?, ?, ?, ?> t = TypeExprMatrix.class.cast(t_raw);

    Assert.assertEquals(SizeExprConstant.class, t.getWidth().getClass());
    final SizeExprConstant<?, ?, ?, ?, ?> w =
      SizeExprConstant.class.cast(t.getWidth());
    Assert.assertEquals(BigInteger.valueOf(4L), w.getValue());

    Assert.assertEquals(SizeExprConstant.class, t.getHeight().getClass());
    final SizeExprConstant<?, ?, ?, ?, ?> h =
      SizeExprConstant.class.cast(t.getHeight());
    Assert.assertEquals(BigInteger.valueOf(2L), h.getValue());
  }

  @Test public final void testTypeExprMatrix_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(matrix T)");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprMatrix_Error1()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(matrix T 2)");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprMatrix_Error2()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(matrix T 2 ())");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_NON_EMPTY_LIST));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprArray_Correct0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(array T 3)");
    final JPRAParserType p = this.getParser();

    final TypeExprType<?, ?, ?, ?, ?> t_raw = p.parseTypeExpression(e);
    Assert.assertEquals(TypeExprArray.class, t_raw.getClass());
    final TypeExprArray<?, ?, ?, ?, ?> t = TypeExprArray.class.cast(t_raw);

    Assert.assertEquals(SizeExprConstant.class, t.getElementCount().getClass());
    final SizeExprConstant<?, ?, ?, ?, ?> s =
      SizeExprConstant.class.cast(t.getElementCount());
    Assert.assertEquals(BigInteger.valueOf(3L), s.getValue());
  }

  @Test public final void testTypeExprArray_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(array)");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprArray_Error1()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(array 32)");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprArray_Error2()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(array T ())");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_NON_EMPTY_LIST));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprString_Correct0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(string 64 \"UTF-8\")");
    final JPRAParserType p = this.getParser();

    final TypeExprType<?, ?, ?, ?, ?> t_raw = p.parseTypeExpression(e);
    Assert.assertEquals(TypeExprString.class, t_raw.getClass());
    final TypeExprString<?, ?, ?, ?, ?> t = TypeExprString.class.cast(t_raw);
    Assert.assertEquals("UTF-8", t.getEncoding());

    Assert.assertEquals(SizeExprConstant.class, t.getSize().getClass());
    final SizeExprConstant<?, ?, ?, ?, ?> s =
      SizeExprConstant.class.cast(t.getSize());
    Assert.assertEquals(BigInteger.valueOf(64L), s.getValue());
  }

  @Test public final void testTypeExprString_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(string)");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprString_Error1()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(string 32)");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprString_Error2()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(string T ())");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprBooleanSet_Correct0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(boolean-set 1 (x y z))");
    final JPRAParserType p = this.getParser();

    final TypeExprType<?, ?, ?, ?, ?> t_raw = p.parseTypeExpression(e);
    Assert.assertEquals(TypeExprBooleanSet.class, t_raw.getClass());
    final TypeExprBooleanSet<?, ?, ?, ?, ?> t =
      TypeExprBooleanSet.class.cast(t_raw);

    Assert.assertEquals(SizeExprConstant.class, t.getSize().getClass());
    final SizeExprConstant<?, ?, ?, ?, ?> s =
      SizeExprConstant.class.cast(t.getSize());
    Assert.assertEquals(BigInteger.valueOf(1L), s.getValue());
  }

  @Test public final void testTypeExprBooleanSet_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(boolean-set)");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprBooleanSet_Error1()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(boolean-set x)");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprBooleanSet_Error2()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(boolean-set 1 \"\")");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_LIST_GOT_QUOTED_STRING));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprBooleanSet_Error3()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(boolean-set 1 (x x))");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.DUPLICATE_FIELD_NAME));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprBooleanSet_Error4()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(boolean-set 1 (T))");
    final JPRAParserType p = this.getParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.BAD_FIELD_NAME));
    p.parseTypeExpression(e);
  }
}
