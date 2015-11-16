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

import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.map.ImmutableMap;
import com.io7m.jpra.compiler.core.parser.JPRAParseErrorCode;
import com.io7m.jpra.compiler.core.parser.JPRAParserType;
import com.io7m.jpra.model.Unresolved;
import com.io7m.jpra.model.Untyped;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.jpra.model.names.TypeReference;
import com.io7m.jpra.model.size_expressions.SizeExprConstant;
import com.io7m.jpra.model.size_expressions.SizeExprInBits;
import com.io7m.jpra.model.size_expressions.SizeExprInOctets;
import com.io7m.jpra.model.size_expressions.SizeExprType;
import com.io7m.jpra.model.statements.StatementCommandSize;
import com.io7m.jpra.model.statements.StatementCommandType;
import com.io7m.jpra.model.statements.StatementPackageBegin;
import com.io7m.jpra.model.statements.StatementPackageEnd;
import com.io7m.jpra.model.statements.StatementPackageImport;
import com.io7m.jpra.model.statements.StatementType;
import com.io7m.jpra.model.type_declarations.PackedFieldDeclPaddingBits;
import com.io7m.jpra.model.type_declarations.PackedFieldDeclType;
import com.io7m.jpra.model.type_declarations.PackedFieldDeclValue;
import com.io7m.jpra.model.type_declarations.RecordFieldDeclPaddingOctets;
import com.io7m.jpra.model.type_declarations.RecordFieldDeclType;
import com.io7m.jpra.model.type_declarations.RecordFieldDeclValue;
import com.io7m.jpra.model.type_declarations.TypeDeclPacked;
import com.io7m.jpra.model.type_declarations.TypeDeclRecord;
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
import java.util.Optional;

@SuppressWarnings("unchecked") public abstract class JPRAParserContract
{
  @Rule public final ExpectedException expected = ExpectedException.none();

  protected abstract JPRAParserType newParser();

  protected abstract SExpressionType newFileSExpr(
    final String name);

  protected abstract SExpressionType newStringSExpr(
    final String expr);

  @Test public final void testTypeExprIntegerUnsigned32_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(integer unsigned)");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprInteger_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(integer)");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprInteger_Error1()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(integer ())");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprInteger_Error2()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(integer \"x\")");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprInteger_Error3()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(integer signed \"x\")");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_SYMBOL_OR_LIST_GOT_QUOTED_STRING));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprInteger_Error4()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(integer signed ())");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_NON_EMPTY_LIST));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprInteger_Error5()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(integer raspberry 23)");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.UNRECOGNIZED_INTEGER_TYPE_KEYWORD));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprIntegerUnsigned32_Error1()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(integer unsigned q)");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.INVALID_INTEGER_CONSTANT));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprIntegerUnsigned32_Correct0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(integer unsigned 32)");
    final JPRAParserType p = this.newParser();

    final TypeExprType<Unresolved, Untyped> t_raw = p.parseTypeExpression(e);
    Assert.assertEquals(TypeExprIntegerUnsigned.class, t_raw.getClass());
    final TypeExprIntegerUnsigned<Unresolved, Untyped> t =
      TypeExprIntegerUnsigned.class.cast(t_raw);

    Assert.assertEquals(SizeExprConstant.class, t.getSize().getClass());
    final SizeExprConstant<Unresolved, Untyped> s =
      SizeExprConstant.class.cast(t.getSize());
    Assert.assertEquals(BigInteger.valueOf(32L), s.getValue());
  }

  @Test public final void testTypeExprIntegerSigned32_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(integer signed)");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprIntegerSigned32_Error1()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(integer signed q)");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.INVALID_INTEGER_CONSTANT));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprIntegerSigned32_Correct0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(integer signed 32)");
    final JPRAParserType p = this.newParser();

    final TypeExprType<Unresolved, Untyped> t_raw = p.parseTypeExpression(e);
    Assert.assertEquals(TypeExprIntegerSigned.class, t_raw.getClass());
    final TypeExprIntegerSigned<Unresolved, Untyped> t =
      TypeExprIntegerSigned.class.cast(t_raw);

    Assert.assertEquals(SizeExprConstant.class, t.getSize().getClass());
    final SizeExprConstant<Unresolved, Untyped> s =
      SizeExprConstant.class.cast(t.getSize());
    Assert.assertEquals(BigInteger.valueOf(32L), s.getValue());
  }

  @Test public final void testTypeExprIntegerSignedNormalized32_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr(
      "(integer signed-normalized)");
    final JPRAParserType p = this.newParser();

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
    final JPRAParserType p = this.newParser();

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
    final JPRAParserType p = this.newParser();

    final TypeExprType<Unresolved, Untyped> t_raw = p.parseTypeExpression(e);
    Assert.assertEquals(
      TypeExprIntegerSignedNormalized.class, t_raw.getClass());
    final TypeExprIntegerSignedNormalized<Unresolved, Untyped> t =
      TypeExprIntegerSignedNormalized.class.cast(t_raw);

    Assert.assertEquals(SizeExprConstant.class, t.getSize().getClass());
    final SizeExprConstant<Unresolved, Untyped> s =
      SizeExprConstant.class.cast(t.getSize());
    Assert.assertEquals(BigInteger.valueOf(32L), s.getValue());
  }

  @Test public final void testTypeExprIntegerUnsignedNormalized32_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr(
      "(integer unsigned-normalized)");
    final JPRAParserType p = this.newParser();

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
    final JPRAParserType p = this.newParser();

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
    final JPRAParserType p = this.newParser();

    final TypeExprType<Unresolved, Untyped> t_raw = p.parseTypeExpression(e);
    Assert.assertEquals(
      TypeExprIntegerUnsignedNormalized.class, t_raw.getClass());
    final TypeExprIntegerUnsignedNormalized<Unresolved, Untyped> t =
      TypeExprIntegerUnsignedNormalized.class.cast(t_raw);

    Assert.assertEquals(SizeExprConstant.class, t.getSize().getClass());
    final SizeExprConstant<Unresolved, Untyped> s =
      SizeExprConstant.class.cast(t.getSize());
    Assert.assertEquals(BigInteger.valueOf(32L), s.getValue());
  }

  @Test public final void testTypeExpr_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("()");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_NON_EMPTY_LIST));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExpr_Error1()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("\"\"");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_SYMBOL_OR_LIST_GOT_QUOTED_STRING));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExpr_Error2()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(unknown)");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.UNRECOGNIZED_TYPE_KEYWORD));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExpr_Error3()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(\"\")");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_SYMBOL_GOT_QUOTED_STRING));
    p.parseTypeExpression(e);
  }

  @Test public final void testSizeExpr_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("()");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_NON_EMPTY_LIST));
    p.parseSizeExpression(e);
  }

  @Test public final void testSizeExpr_Error1()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("\"\"");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_SYMBOL_OR_LIST_GOT_QUOTED_STRING));
    p.parseSizeExpression(e);
  }

  @Test public final void testSizeExpr_Error2()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(size-in-meters T)");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.UNRECOGNIZED_SIZE_FUNCTION));
    p.parseSizeExpression(e);
  }

  @Test public final void testSizeExpr_Constant_OK0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("32");
    final JPRAParserType p = this.newParser();

    final SizeExprType<Unresolved, Untyped> s = p.parseSizeExpression(e);
    final SizeExprConstant<Unresolved, Untyped> sc =
      SizeExprConstant.class.cast(s);
    Assert.assertEquals(BigInteger.valueOf(32L), sc.getValue());
  }

  @Test public final void testSizeExpr_Bits_OK0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(size-in-bits T)");
    final JPRAParserType p = this.newParser();

    final SizeExprType<Unresolved, Untyped> s = p.parseSizeExpression(e);
    final SizeExprInBits<Unresolved, Untyped> sb = SizeExprInBits.class.cast(s);
    final TypeExprName<Unresolved, Untyped> tn =
      TypeExprName.class.cast(sb.getTypeExpression());
    final TypeReference tr = TypeReference.class.cast(tn.getReference());
    Assert.assertEquals("T", tr.getType().getValue());
  }

  @Test public final void testSizeExpr_Bits_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(size-in-bits)");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseSizeExpression(e);
  }

  @Test public final void testSizeExpr_Octets_OK0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(size-in-octets T)");
    final JPRAParserType p = this.newParser();

    final SizeExprType<Unresolved, Untyped> s = p.parseSizeExpression(e);
    final SizeExprInOctets<Unresolved, Untyped> sb =
      SizeExprInOctets.class.cast(s);
    final TypeExprName<Unresolved, Untyped> tn =
      TypeExprName.class.cast(sb.getTypeExpression());
    final TypeReference tr = TypeReference.class.cast(tn.getReference());
    Assert.assertEquals("T", tr.getType().getValue());
  }

  @Test public final void testSizeExpr_Octets_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(size-in-octets)");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseSizeExpression(e);
  }

  @Test public final void testTypeExprFloat_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(float)");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprFloat_Error1()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(float ())");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_NON_EMPTY_LIST));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprFloat_Error2()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(float \"x\")");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_SYMBOL_OR_LIST_GOT_QUOTED_STRING));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprFloat_Correct0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(float 32)");
    final JPRAParserType p = this.newParser();

    final TypeExprType<Unresolved, Untyped> t_raw = p.parseTypeExpression(e);
    Assert.assertEquals(TypeExprFloat.class, t_raw.getClass());
    final TypeExprFloat<Unresolved, Untyped> t =
      TypeExprFloat.class.cast(t_raw);

    Assert.assertEquals(SizeExprConstant.class, t.getSize().getClass());
    final SizeExprConstant<Unresolved, Untyped> s =
      SizeExprConstant.class.cast(t.getSize());
    Assert.assertEquals(BigInteger.valueOf(32L), s.getValue());
  }

  @Test public final void testTypeExprVector_Correct0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(vector T 3)");
    final JPRAParserType p = this.newParser();

    final TypeExprType<Unresolved, Untyped> t_raw = p.parseTypeExpression(e);
    Assert.assertEquals(TypeExprVector.class, t_raw.getClass());
    final TypeExprVector<Unresolved, Untyped> t =
      TypeExprVector.class.cast(t_raw);

    Assert.assertEquals(SizeExprConstant.class, t.getElementCount().getClass());
    final SizeExprConstant<Unresolved, Untyped> s =
      SizeExprConstant.class.cast(t.getElementCount());
    Assert.assertEquals(BigInteger.valueOf(3L), s.getValue());
  }

  @Test public final void testTypeExprVector_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(vector)");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprVector_Error1()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(vector 32)");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprVector_Error2()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(vector T ())");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_NON_EMPTY_LIST));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprMatrix_Correct0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(matrix T 4 2)");
    final JPRAParserType p = this.newParser();

    final TypeExprType<Unresolved, Untyped> t_raw = p.parseTypeExpression(e);
    Assert.assertEquals(TypeExprMatrix.class, t_raw.getClass());
    final TypeExprMatrix<Unresolved, Untyped> t =
      TypeExprMatrix.class.cast(t_raw);

    Assert.assertEquals(SizeExprConstant.class, t.getWidth().getClass());
    final SizeExprConstant<Unresolved, Untyped> w =
      SizeExprConstant.class.cast(t.getWidth());
    Assert.assertEquals(BigInteger.valueOf(4L), w.getValue());

    Assert.assertEquals(SizeExprConstant.class, t.getHeight().getClass());
    final SizeExprConstant<Unresolved, Untyped> h =
      SizeExprConstant.class.cast(t.getHeight());
    Assert.assertEquals(BigInteger.valueOf(2L), h.getValue());
  }

  @Test public final void testTypeExprMatrix_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(matrix T)");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprMatrix_Error1()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(matrix T 2)");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprMatrix_Error2()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(matrix T 2 ())");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_NON_EMPTY_LIST));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprArray_Correct0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(array T 3)");
    final JPRAParserType p = this.newParser();

    final TypeExprType<Unresolved, Untyped> t_raw = p.parseTypeExpression(e);
    Assert.assertEquals(TypeExprArray.class, t_raw.getClass());
    final TypeExprArray<Unresolved, Untyped> t =
      TypeExprArray.class.cast(t_raw);

    Assert.assertEquals(SizeExprConstant.class, t.getElementCount().getClass());
    final SizeExprConstant<Unresolved, Untyped> s =
      SizeExprConstant.class.cast(t.getElementCount());
    Assert.assertEquals(BigInteger.valueOf(3L), s.getValue());
  }

  @Test public final void testTypeExprArray_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(array)");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprArray_Error1()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(array 32)");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprArray_Error2()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(array T ())");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_NON_EMPTY_LIST));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprString_Correct0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(string 64 \"UTF-8\")");
    final JPRAParserType p = this.newParser();

    final TypeExprType<Unresolved, Untyped> t_raw = p.parseTypeExpression(e);
    Assert.assertEquals(TypeExprString.class, t_raw.getClass());
    final TypeExprString<Unresolved, Untyped> t =
      TypeExprString.class.cast(t_raw);
    Assert.assertEquals("UTF-8", t.getEncoding());

    Assert.assertEquals(SizeExprConstant.class, t.getSize().getClass());
    final SizeExprConstant<Unresolved, Untyped> s =
      SizeExprConstant.class.cast(t.getSize());
    Assert.assertEquals(BigInteger.valueOf(64L), s.getValue());
  }

  @Test public final void testTypeExprString_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(string)");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprString_Error1()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(string 32)");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprString_Error2()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(string T ())");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprBooleanSet_Correct0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(boolean-set 1 (x y z))");
    final JPRAParserType p = this.newParser();

    final TypeExprType<Unresolved, Untyped> t_raw = p.parseTypeExpression(e);
    Assert.assertEquals(TypeExprBooleanSet.class, t_raw.getClass());
    final TypeExprBooleanSet<Unresolved, Untyped> t =
      TypeExprBooleanSet.class.cast(t_raw);

    Assert.assertEquals(
      SizeExprConstant.class, t.getSizeExpression().getClass());
    final SizeExprConstant<Unresolved, Untyped> s =
      SizeExprConstant.class.cast(t.getSizeExpression());
    Assert.assertEquals(BigInteger.valueOf(1L), s.getValue());
  }

  @Test public final void testTypeExprBooleanSet_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(boolean-set)");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprBooleanSet_Error1()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(boolean-set x)");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprBooleanSet_Error2()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(boolean-set 1 \"\")");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_LIST_GOT_QUOTED_STRING));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprBooleanSet_Error3()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(boolean-set 1 (x x))");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.DUPLICATE_FIELD_NAME));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprBooleanSet_Error4()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(boolean-set 1 (T))");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.BAD_FIELD_NAME));
    p.parseTypeExpression(e);
  }

  @Test public final void testTypeExprBooleanSet_Error5()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(boolean-set 1 x)");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_LIST_GOT_SYMBOL));
    p.parseTypeExpression(e);
  }

  @Test public final void testPackageBegin_OK0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(package-begin x.y.z)");
    final JPRAParserType p = this.newParser();

    final StatementType<Unresolved, Untyped> st = p.parseStatement(e);
    final StatementPackageBegin<Unresolved, Untyped> pb =
      StatementPackageBegin.class.cast(st);
    Assert.assertEquals("x.y.z", pb.getPackageName().toString());
  }

  @Test public final void testPackageImport_OK0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(import x.y.z as q)");
    final JPRAParserType p = this.newParser();

    final StatementType<Unresolved, Untyped> st = p.parseStatement(e);
    final StatementPackageImport<Unresolved, Untyped> pi =
      StatementPackageImport.class.cast(st);
    Assert.assertEquals("x.y.z", pi.getPackageName().toString());
    Assert.assertEquals("q", pi.getUsing().toString());
  }

  @Test public final void testPackageEnd_OK0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(package-end)");
    final JPRAParserType p = this.newParser();

    final StatementType<Unresolved, Untyped> st = p.parseStatement(e);
    final StatementPackageEnd<Unresolved, Untyped> pb =
      StatementPackageEnd.class.cast(st);
  }

  @Test public final void testPackageImport_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(import)");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseStatement(e);
  }

  @Test public final void testPackageImport_Error1()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(import x.y.z)");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseStatement(e);
  }

  @Test public final void testPackageImport_Error2()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(import x.y.z as)");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseStatement(e);
  }

  @Test public final void testPackageImport_Error3()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(import x.y.z T q)");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseStatement(e);
  }

  @Test public final void testPackageBegin_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(package-begin)");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseStatement(e);
  }

  @Test public final void testPackageBegin_Error1()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(package-begin T)");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.BAD_PACKAGE_NAME));
    p.parseStatement(e);
  }

  @Test public final void testPackageEnd_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(package-end T)");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseStatement(e);
  }

  @Test public final void testStatement_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("()");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_NON_EMPTY_LIST));
    p.parseStatement(e);
  }

  @Test public final void testStatement_Error1()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("x");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_LIST_GOT_SYMBOL));
    p.parseStatement(e);
  }

  @Test public final void testStatement_Error2()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("\"x\"");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_LIST_GOT_QUOTED_STRING));
    p.parseStatement(e);
  }

  @Test public final void testRecordField_Error0()
    throws Exception
  {
    final SExpressionType e = this.newFileSExpr("t-record-field-invalid-0.jpr");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseStatement(e);
  }

  @Test public final void testRecordField_Error1()
    throws Exception
  {
    final SExpressionType e = this.newFileSExpr("t-record-field-invalid-1.jpr");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.BAD_FIELD_NAME));
    p.parseStatement(e);
  }

  @Test public final void testRecordField_Error3()
    throws Exception
  {
    final SExpressionType e = this.newFileSExpr("t-record-field-invalid-3.jpr");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.BAD_TYPE_REFERENCE));
    p.parseStatement(e);
  }

  @Test public final void testRecordField_Error6()
    throws Exception
  {
    final SExpressionType e = this.newFileSExpr("t-record-field-invalid-6.jpr");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_LIST_GOT_QUOTED_STRING));
    p.parseStatement(e);
  }

  @Test public final void testRecord_Error0()
    throws Exception
  {
    final SExpressionType e = this.newFileSExpr("t-record-invalid-0.jpr");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseStatement(e);
  }

  @Test public final void testRecord_Error1()
    throws Exception
  {
    final SExpressionType e = this.newFileSExpr("t-record-invalid-1.jpr");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseStatement(e);
  }

  @Test public final void testRecord_Error2()
    throws Exception
  {
    final SExpressionType e = this.newFileSExpr("t-record-invalid-2.jpr");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.BAD_TYPE_NAME));
    p.parseStatement(e);
  }

  @Test public final void testRecord_Error3()
    throws Exception
  {
    final SExpressionType e = this.newFileSExpr("t-record-invalid-3.jpr");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_LIST_GOT_SYMBOL));
    p.parseStatement(e);
  }

  @Test public final void testRecord_Error4()
    throws Exception
  {
    final SExpressionType e = this.newFileSExpr("t-record-invalid-4.jpr");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.UNRECOGNIZED_RECORD_FIELD_KEYWORD));
    p.parseStatement(e);
  }

  @Test public final void testRecord_Error5()
    throws Exception
  {
    final SExpressionType e = this.newFileSExpr("t-record-invalid-5.jpr");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_NON_EMPTY_LIST));
    p.parseStatement(e);
  }

  @Test public final void testRecord_Error6()
    throws Exception
  {
    final SExpressionType e = this.newFileSExpr("t-record-invalid-6.jpr");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.DUPLICATE_FIELD_NAME));
    p.parseStatement(e);
  }

  @Test public final void testRecordPaddingOctets_Error0()
    throws Exception
  {
    final SExpressionType e = this.newFileSExpr(
      "t-record-padding-octets-invalid-0.jpr");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseStatement(e);
  }

  @Test public final void testRecord_OK0()
    throws Exception
  {
    final SExpressionType e = this.newFileSExpr(
      "t-record-0.jpr");
    final JPRAParserType p = this.newParser();

    final StatementType<Unresolved, Untyped> s = p.parseStatement(e);
    final TypeDeclRecord<Unresolved, Untyped> d = TypeDeclRecord.class.cast(s);

    final ImmutableList<RecordFieldDeclType<Unresolved, Untyped>> field_order =
      d.getFieldsInDeclarationOrder();
    final ImmutableMap<FieldName, RecordFieldDeclValue<Unresolved, Untyped>>
      field_names = d.getFieldsByName();

    Assert.assertEquals(1L, (long) field_order.size());
    Assert.assertEquals(1L, (long) field_names.size());

    final FieldName f_name = new FieldName(Optional.empty(), "f0");
    final RecordFieldDeclValue<Unresolved, Untyped> f0 =
      field_names.get(f_name);
    final RecordFieldDeclType<Unresolved, Untyped> f1 = field_order.get(0);
    Assert.assertSame(f0, f1);

    Assert.assertEquals(f_name, f0.getName());
  }

  @Test public final void testRecord_OK1()
    throws Exception
  {
    final SExpressionType e = this.newFileSExpr(
      "t-record-1.jpr");
    final JPRAParserType p = this.newParser();

    final StatementType<Unresolved, Untyped> s = p.parseStatement(e);
    final TypeDeclRecord<Unresolved, Untyped> d = TypeDeclRecord.class.cast(s);

    final ImmutableList<RecordFieldDeclType<Unresolved, Untyped>> field_order =
      d.getFieldsInDeclarationOrder();
    final ImmutableMap<FieldName, RecordFieldDeclValue<Unresolved, Untyped>>
      field_names = d.getFieldsByName();

    Assert.assertEquals(1L, (long) field_order.size());
    Assert.assertEquals(0L, (long) field_names.size());

    final RecordFieldDeclType<Unresolved, Untyped> f1 = field_order.get(0);
    final RecordFieldDeclPaddingOctets<Unresolved, Untyped> fp =
      RecordFieldDeclPaddingOctets.class.cast(f1);

    final SizeExprConstant<?, ?> size =
      SizeExprConstant.class.cast(fp.getSizeExpression());
    Assert.assertEquals(BigInteger.valueOf(4L), size.getValue());
  }

  @Test public final void testCommandSize_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(:size)");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseStatement(e);
  }

  @Test public final void testCommandSize_Error1()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(:size 32 32)");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseStatement(e);
  }

  @Test public final void testCommandSize_OK0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(:size 32)");
    final JPRAParserType p = this.newParser();

    final StatementCommandSize<Unresolved, Untyped> s =
      StatementCommandSize.class.cast(p.parseStatement(e));
    final SizeExprConstant<Unresolved, Untyped> sc =
      SizeExprConstant.class.cast(s.getExpression());
    Assert.assertEquals(BigInteger.valueOf(32L), sc.getValue());
  }

  @Test public final void testCommandType_Error0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(:type)");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseStatement(e);
  }

  @Test public final void testCommandType_Error1()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(:type T T)");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseStatement(e);
  }

  @Test public final void testCommandType_OK0()
    throws Exception
  {
    final SExpressionType e = this.newStringSExpr("(:type T)");
    final JPRAParserType p = this.newParser();

    final StatementCommandType<Unresolved, Untyped> s =
      StatementCommandType.class.cast(p.parseStatement(e));
    final TypeExprName<Unresolved, Untyped> tn =
      TypeExprName.class.cast(s.getExpression());
    final TypeReference ref = tn.getReference();
    Assert.assertEquals(new TypeName(Optional.empty(), "T"), ref.getType());
    Assert.assertEquals(Optional.empty(), ref.getPackage());
  }


















  @Test public final void testPacked_Error0()
    throws Exception
  {
    final SExpressionType e = this.newFileSExpr("t-packed-invalid-0.jpr");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseStatement(e);
  }

  @Test public final void testPacked_Error1()
    throws Exception
  {
    final SExpressionType e = this.newFileSExpr("t-packed-invalid-1.jpr");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseStatement(e);
  }

  @Test public final void testPacked_Error2()
    throws Exception
  {
    final SExpressionType e = this.newFileSExpr("t-packed-invalid-2.jpr");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.BAD_TYPE_NAME));
    p.parseStatement(e);
  }

  @Test public final void testPacked_Error3()
    throws Exception
  {
    final SExpressionType e = this.newFileSExpr("t-packed-invalid-3.jpr");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_LIST_GOT_SYMBOL));
    p.parseStatement(e);
  }

  @Test public final void testPacked_Error4()
    throws Exception
  {
    final SExpressionType e = this.newFileSExpr("t-packed-invalid-4.jpr");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.UNRECOGNIZED_PACKED_FIELD_KEYWORD));
    p.parseStatement(e);
  }

  @Test public final void testPacked_Error5()
    throws Exception
  {
    final SExpressionType e = this.newFileSExpr("t-packed-invalid-5.jpr");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_NON_EMPTY_LIST));
    p.parseStatement(e);
  }

  @Test public final void testPacked_Error6()
    throws Exception
  {
    final SExpressionType e = this.newFileSExpr("t-packed-invalid-6.jpr");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.DUPLICATE_FIELD_NAME));
    p.parseStatement(e);
  }

  @Test public final void testPackedPaddingBits_Error0()
    throws Exception
  {
    final SExpressionType e = this.newFileSExpr(
      "t-packed-padding-bits-invalid-0.jpr");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseStatement(e);
  }

  @Test public final void testPacked_OK0()
    throws Exception
  {
    final SExpressionType e = this.newFileSExpr("t-packed-0.jpr");
    final JPRAParserType p = this.newParser();

    final StatementType<Unresolved, Untyped> s = p.parseStatement(e);
    final TypeDeclPacked<Unresolved, Untyped> d = TypeDeclPacked.class.cast(s);

    final ImmutableList<PackedFieldDeclType<Unresolved, Untyped>> field_order =
      d.getFieldsInDeclarationOrder();
    final ImmutableMap<FieldName, PackedFieldDeclValue<Unresolved, Untyped>>
      field_names = d.getFieldsByName();

    Assert.assertEquals(1L, (long) field_order.size());
    Assert.assertEquals(1L, (long) field_names.size());

    final FieldName f_name = new FieldName(Optional.empty(), "f0");
    final PackedFieldDeclValue<Unresolved, Untyped> f0 =
      field_names.get(f_name);
    final PackedFieldDeclType<Unresolved, Untyped> f1 = field_order.get(0);
    Assert.assertSame(f0, f1);

    Assert.assertEquals(f_name, f0.getName());
  }

  @Test public final void testPacked_OK1()
    throws Exception
  {
    final SExpressionType e = this.newFileSExpr("t-packed-1.jpr");
    final JPRAParserType p = this.newParser();

    final StatementType<Unresolved, Untyped> s = p.parseStatement(e);
    final TypeDeclPacked<Unresolved, Untyped> d = TypeDeclPacked.class.cast(s);

    final ImmutableList<PackedFieldDeclType<Unresolved, Untyped>> field_order =
      d.getFieldsInDeclarationOrder();
    final ImmutableMap<FieldName, PackedFieldDeclValue<Unresolved, Untyped>>
      field_names = d.getFieldsByName();

    Assert.assertEquals(1L, (long) field_order.size());
    Assert.assertEquals(0L, (long) field_names.size());

    final PackedFieldDeclType<Unresolved, Untyped> f1 = field_order.get(0);
    final PackedFieldDeclPaddingBits<Unresolved, Untyped> fp =
      PackedFieldDeclPaddingBits.class.cast(f1);

    final SizeExprConstant<?, ?> size =
      SizeExprConstant.class.cast(fp.getSizeExpression());
    Assert.assertEquals(BigInteger.valueOf(4L), size.getValue());
  }






  @Test public final void testPackedField_Error0()
    throws Exception
  {
    final SExpressionType e = this.newFileSExpr("t-packed-field-invalid-0.jpr");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));
    p.parseStatement(e);
  }

  @Test public final void testPackedField_Error1()
    throws Exception
  {
    final SExpressionType e = this.newFileSExpr("t-packed-field-invalid-1.jpr");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.BAD_FIELD_NAME));
    p.parseStatement(e);
  }

  @Test public final void testPackedField_Error3()
    throws Exception
  {
    final SExpressionType e = this.newFileSExpr("t-packed-field-invalid-3.jpr");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.BAD_TYPE_REFERENCE));
    p.parseStatement(e);
  }

  @Test public final void testPackedField_Error6()
    throws Exception
  {
    final SExpressionType e = this.newFileSExpr("t-packed-field-invalid-6.jpr");
    final JPRAParserType p = this.newParser();

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_LIST_GOT_QUOTED_STRING));
    p.parseStatement(e);
  }
}
