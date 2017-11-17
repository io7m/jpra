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

package com.io7m.jpra.tests.compiler.core.checker;

import com.io7m.jpra.compiler.core.checker.JPRACheckerCapabilitiesType;
import com.io7m.jpra.compiler.core.checker.JPRACheckerErrorCode;
import com.io7m.jpra.compiler.core.checker.JPRACheckerStandardCapabilities;
import com.io7m.jpra.compiler.core.checker.JPRACheckerType;
import com.io7m.jpra.compiler.core.parser.JPRAParserType;
import com.io7m.jpra.compiler.core.resolver.JPRAResolverType;
import com.io7m.jpra.model.Unresolved;
import com.io7m.jpra.model.Untyped;
import com.io7m.jpra.model.contexts.GlobalContextType;
import com.io7m.jpra.model.contexts.GlobalContexts;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.names.IdentifierType;
import com.io7m.jpra.model.statements.StatementPackageBegin;
import com.io7m.jpra.model.type_declarations.PackedFieldDeclType;
import com.io7m.jpra.model.type_declarations.PackedFieldDeclValue;
import com.io7m.jpra.model.type_declarations.RecordFieldDeclType;
import com.io7m.jpra.model.type_declarations.RecordFieldDeclValue;
import com.io7m.jpra.model.type_declarations.TypeDeclPacked;
import com.io7m.jpra.model.type_declarations.TypeDeclRecord;
import com.io7m.jpra.model.type_declarations.TypeDeclType;
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
import com.io7m.jpra.model.types.TArray;
import com.io7m.jpra.model.types.TBooleanSet;
import com.io7m.jpra.model.types.TFloat;
import com.io7m.jpra.model.types.TIntegerSigned;
import com.io7m.jpra.model.types.TIntegerSignedNormalized;
import com.io7m.jpra.model.types.TIntegerUnsigned;
import com.io7m.jpra.model.types.TIntegerUnsignedNormalized;
import com.io7m.jpra.model.types.TMatrix;
import com.io7m.jpra.model.types.TPacked;
import com.io7m.jpra.model.types.TRecord;
import com.io7m.jpra.model.types.TString;
import com.io7m.jpra.model.types.TType;
import com.io7m.jpra.model.types.TVector;
import com.io7m.jpra.tests.compiler.core.resolver.AlwaysEmptyLoader;
import com.io7m.jsx.SExpressionType;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigInteger;

@SuppressWarnings("unchecked")
public abstract class JPRACheckerContract
{
  @Rule public final ExpectedException expected = ExpectedException.none();

  protected abstract JPRAParserType newParser();

  protected abstract JPRAResolverType newResolver(
    GlobalContextType c);

  protected abstract JPRACheckerType newChecker(
    GlobalContextType c,
    JPRACheckerCapabilitiesType caps);

  protected abstract SExpressionType newStringSExpr(
    String expr);

  @Test
  public final void testTypeExprIntegerSignedSizeCorrect()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    final TypeExprType<IdentifierType, Untyped> te = r.resolveTypeExpression(
      p.parseTypeExpression(
        this.newStringSExpr("(integer signed 32)")));

    final TypeExprIntegerSigned<IdentifierType, TType> e =
      TypeExprIntegerSigned.class.cast(ch.checkTypeExpression(te));
    final TIntegerSigned ti = TIntegerSigned.class.cast(e.getType());
    Assert.assertEquals(BigInteger.valueOf(32L), ti.getSizeInBits().getValue());
  }

  @Test
  public final void testTypeExprIntegerSignedNormalizedSizeCorrect()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    final TypeExprType<IdentifierType, Untyped> te = r.resolveTypeExpression(
      p.parseTypeExpression(
        this.newStringSExpr("(integer signed-normalized 32)")));

    final TypeExprIntegerSignedNormalized<IdentifierType, TType> e =
      TypeExprIntegerSignedNormalized.class.cast(ch.checkTypeExpression(te));
    final TIntegerSignedNormalized ti =
      TIntegerSignedNormalized.class.cast(e.getType());
    Assert.assertEquals(BigInteger.valueOf(32L), ti.getSizeInBits().getValue());
  }

  @Test
  public final void testTypeExprIntegerUnsignedSizeCorrect()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    final TypeExprType<IdentifierType, Untyped> te = r.resolveTypeExpression(
      p.parseTypeExpression(
        this.newStringSExpr("(integer unsigned 32)")));

    final TypeExprIntegerUnsigned<IdentifierType, TType> e =
      TypeExprIntegerUnsigned.class.cast(ch.checkTypeExpression(te));
    final TIntegerUnsigned ti = TIntegerUnsigned.class.cast(e.getType());
    Assert.assertEquals(BigInteger.valueOf(32L), ti.getSizeInBits().getValue());
  }

  @Test
  public final void testTypeExprIntegerUnsignedNormalizedSizeCorrect()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    final TypeExprType<IdentifierType, Untyped> te = r.resolveTypeExpression(
      p.parseTypeExpression(
        this.newStringSExpr("(integer unsigned-normalized 32)")));

    final TypeExprIntegerUnsignedNormalized<IdentifierType, TType> e =
      TypeExprIntegerUnsignedNormalized.class.cast(ch.checkTypeExpression(te));
    final TIntegerUnsignedNormalized ti =
      TIntegerUnsignedNormalized.class.cast(e.getType());
    Assert.assertEquals(BigInteger.valueOf(32L), ti.getSizeInBits().getValue());
  }

  @Test
  public final void testRecordTypeExprFloatSizeSupported()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    final TypeExprType<IdentifierType, Untyped> te = r.resolveTypeExpression(
      p.parseTypeExpression(
        this.newStringSExpr("(float 32)")));

    final TypeExprFloat<IdentifierType, TType> e =
      TypeExprFloat.class.cast(ch.checkTypeExpression(te));
    final TFloat ti = TFloat.class.cast(e.getType());
    Assert.assertEquals(BigInteger.valueOf(32L), ti.getSizeInBits().getValue());
  }

  @Test
  public final void testRecordTypeExprFloatSizeUnsupported()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    final TypeExprType<IdentifierType, Untyped> te = r.resolveTypeExpression(
      p.parseTypeExpression(
        this.newStringSExpr("(float 37)")));

    this.expected.expect(
      new JPRACompilerCheckerExceptionMatcher(
        JPRACheckerErrorCode.RECORD_FLOAT_SIZE_UNSUPPORTED));
    ch.checkTypeExpression(te);
  }

  @Test
  public final void testRecordTypeExprStringEncodingSupported()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    final TypeExprType<IdentifierType, Untyped> te = r.resolveTypeExpression(
      p.parseTypeExpression(
        this.newStringSExpr("(string 32 \"UTF-8\")")));

    final TypeExprString<IdentifierType, TType> e =
      TypeExprString.class.cast(ch.checkTypeExpression(te));
    final TString ti = TString.class.cast(e.getType());
    Assert.assertEquals(
      BigInteger.valueOf(288L),
      ti.getSizeInBits().getValue());
    Assert.assertEquals(
      BigInteger.valueOf(32L),
      ti.getMaximumStringLength().getValue());
    Assert.assertEquals("UTF-8", ti.getEncoding());
  }

  @Test
  public final void testRecordTypeExprStringEncodingUnsupported()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    final TypeExprType<IdentifierType, Untyped> te = r.resolveTypeExpression(
      p.parseTypeExpression(
        this.newStringSExpr("(string 32 \"unknown\")")));

    this.expected.expect(
      new JPRACompilerCheckerExceptionMatcher(
        JPRACheckerErrorCode.STRING_ENCODING_UNSUPPORTED));
    ch.checkTypeExpression(te);
  }

  @Test
  public final void testRecordTypeExprArray_0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    final TypeExprType<IdentifierType, Untyped> te = r.resolveTypeExpression(
      p.parseTypeExpression(
        this.newStringSExpr("(array [integer signed 32] 32)")));

    final TypeExprArray<IdentifierType, TType> e =
      TypeExprArray.class.cast(ch.checkTypeExpression(te));
    final TArray ta = TArray.class.cast(e.getType());
    Assert.assertEquals(
      BigInteger.valueOf(32L * 32L),
      ta.getSizeInBits().getValue());
    Assert.assertEquals(
      BigInteger.valueOf(32L), ta.getElementCount().getValue());
    Assert.assertEquals(TIntegerSigned.class, ta.getElementType().getClass());
  }

  @Test
  public final void testRecordTypeExprBooleanSet_0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    final TypeExprType<IdentifierType, Untyped> te = r.resolveTypeExpression(
      p.parseTypeExpression(
        this.newStringSExpr("(boolean-set 1 [x y z])")));

    final TypeExprBooleanSet<IdentifierType, TType> e =
      TypeExprBooleanSet.class.cast(ch.checkTypeExpression(te));
    final TBooleanSet ta = TBooleanSet.class.cast(e.getType());
    Assert.assertEquals(BigInteger.valueOf(8L), ta.getSizeInBits().getValue());
  }

  @Test
  public final void testRecordTypeExprBooleanSet_Error0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    final TypeExprType<IdentifierType, Untyped> te = r.resolveTypeExpression(
      p.parseTypeExpression(
        this.newStringSExpr("(boolean-set 0 [x y z])")));

    this.expected.expect(
      new JPRACompilerCheckerExceptionMatcher(
        JPRACheckerErrorCode.BOOLEAN_SET_SIZE_INVALID));
    ch.checkTypeExpression(te);
  }

  @Test
  public final void testRecordTypeExprBooleanSet_Error1()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    final TypeExprType<IdentifierType, Untyped> te = r.resolveTypeExpression(
      p.parseTypeExpression(
        this.newStringSExpr("(boolean-set 1 [f0 f1 f2 f3 f4 f5 f6 f7 f8])")));

    this.expected.expect(
      new JPRACompilerCheckerExceptionMatcher(
        JPRACheckerErrorCode.BOOLEAN_SET_SIZE_TOO_SMALL));
    ch.checkTypeExpression(te);
  }

  @Test
  public final void testRecordTypeExprBooleanSet_1()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    final TypeExprType<IdentifierType, Untyped> te = r.resolveTypeExpression(
      p.parseTypeExpression(
        this.newStringSExpr("(boolean-set 8 [x y z])")));

    final TypeExprBooleanSet<IdentifierType, TType> e =
      TypeExprBooleanSet.class.cast(ch.checkTypeExpression(te));
    final TBooleanSet ta = TBooleanSet.class.cast(e.getType());
    Assert.assertEquals(BigInteger.valueOf(64L), ta.getSizeInBits().getValue());
  }

  @Test
  public final void testTypeExprVectorSizeUnsupported()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    final TypeExprType<IdentifierType, Untyped> te = r.resolveTypeExpression(
      p.parseTypeExpression(
        this.newStringSExpr("(vector [integer signed 32] 5)")));

    this.expected.expect(
      new JPRACompilerCheckerExceptionMatcher(
        JPRACheckerErrorCode.VECTOR_SIZE_UNSUPPORTED));
    ch.checkTypeExpression(te);
  }

  @Test
  public final void testTypeExprVectorNonScalar()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    final TypeExprType<IdentifierType, Untyped> te = r.resolveTypeExpression(
      p.parseTypeExpression(
        this.newStringSExpr("(vector [boolean-set 1 (x)] 5)")));

    this.expected.expect(
      new JPRACompilerCheckerExceptionMatcher(
        JPRACheckerErrorCode.VECTOR_NON_SCALAR_TYPE));
    ch.checkTypeExpression(te);
  }

  @Test
  public final void testTypeExprVectorIntegerUnsupported()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    final TypeExprType<IdentifierType, Untyped> te = r.resolveTypeExpression(
      p.parseTypeExpression(
        this.newStringSExpr("(vector [integer signed 16] 4)")));

    this.expected.expect(
      new JPRACompilerCheckerExceptionMatcher(
        JPRACheckerErrorCode.VECTOR_SIZE_INTEGER_UNSUPPORTED));
    ch.checkTypeExpression(te);
  }

  @Test
  public final void testTypeExprVector_0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    final TypeExprType<IdentifierType, Untyped> te = r.resolveTypeExpression(
      p.parseTypeExpression(
        this.newStringSExpr("(vector [integer signed 32] 4)")));

    final TypeExprVector<IdentifierType, TType> e =
      TypeExprVector.class.cast(ch.checkTypeExpression(te));
    final TVector t = TVector.class.cast(e.getType());
    Assert.assertEquals(BigInteger.valueOf(4L), t.getElementCount().getValue());
    Assert.assertEquals(TIntegerSigned.class, t.getElementType().getClass());
  }

  @Test
  public final void testTypeExprVector_1()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    final TypeExprType<IdentifierType, Untyped> te = r.resolveTypeExpression(
      p.parseTypeExpression(
        this.newStringSExpr("(vector [float 32] 4)")));

    final TypeExprVector<IdentifierType, TType> e =
      TypeExprVector.class.cast(ch.checkTypeExpression(te));
    final TVector t = TVector.class.cast(e.getType());
    Assert.assertEquals(BigInteger.valueOf(4L), t.getElementCount().getValue());
    Assert.assertEquals(TFloat.class, t.getElementType().getClass());
  }

  @Test
  public final void testTypeExprMatrixUnsupported()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    final TypeExprType<IdentifierType, Untyped> te = r.resolveTypeExpression(
      p.parseTypeExpression(
        this.newStringSExpr("(matrix [float 32] 5 5)")));

    this.expected.expect(
      new JPRACompilerCheckerExceptionMatcher(
        JPRACheckerErrorCode.MATRIX_SIZE_UNSUPPORTED));
    ch.checkTypeExpression(te);
  }

  @Test
  public final void testTypeExprMatrixNonScalar()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    final TypeExprType<IdentifierType, Untyped> te = r.resolveTypeExpression(
      p.parseTypeExpression(
        this.newStringSExpr("(matrix [boolean-set 1 (x)] 4 4)")));

    this.expected.expect(
      new JPRACompilerCheckerExceptionMatcher(
        JPRACheckerErrorCode.MATRIX_NON_SCALAR_TYPE));
    ch.checkTypeExpression(te);
  }

  @Test
  public final void testTypeExprMatrixElementUnsupported_0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    final TypeExprType<IdentifierType, Untyped> te = r.resolveTypeExpression(
      p.parseTypeExpression(
        this.newStringSExpr("(matrix [float 16] 4 4)")));

    this.expected.expect(
      new JPRACompilerCheckerExceptionMatcher(
        JPRACheckerErrorCode.MATRIX_SIZE_FLOAT_UNSUPPORTED));
    ch.checkTypeExpression(te);
  }

  @Test
  public final void testTypeExprMatrixElementUnsupported_1()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    final TypeExprType<IdentifierType, Untyped> te = r.resolveTypeExpression(
      p.parseTypeExpression(
        this.newStringSExpr("(matrix [integer signed 32] 4 4)")));

    this.expected.expect(
      new JPRACompilerCheckerExceptionMatcher(
        JPRACheckerErrorCode.MATRIX_SIZE_INTEGER_UNSUPPORTED));
    ch.checkTypeExpression(te);
  }

  @Test
  public final void testTypeExprMatrix_0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    final TypeExprType<IdentifierType, Untyped> te = r.resolveTypeExpression(
      p.parseTypeExpression(
        this.newStringSExpr("(matrix [float 32] 4 4)")));

    final TypeExprMatrix<IdentifierType, TType> e =
      TypeExprMatrix.class.cast(ch.checkTypeExpression(te));
    final TMatrix t = TMatrix.class.cast(e.getType());
    Assert.assertEquals(BigInteger.valueOf(4L), t.getWidth().getValue());
    Assert.assertEquals(BigInteger.valueOf(4L), t.getHeight().getValue());
    Assert.assertEquals(TFloat.class, t.getElementType().getClass());
  }

  @Test
  public final void testTypeExprMatrix_1()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, new CapsSupportingIntegerMatrices(
        JPRACheckerStandardCapabilities.newCapabilities()));

    final TypeExprType<IdentifierType, Untyped> te = r.resolveTypeExpression(
      p.parseTypeExpression(
        this.newStringSExpr("(matrix [integer signed 32] 4 4)")));

    final TypeExprMatrix<IdentifierType, TType> e =
      TypeExprMatrix.class.cast(ch.checkTypeExpression(te));
    final TMatrix t = TMatrix.class.cast(e.getType());
    Assert.assertEquals(BigInteger.valueOf(4L), t.getWidth().getValue());
    Assert.assertEquals(BigInteger.valueOf(4L), t.getHeight().getValue());
    Assert.assertEquals(TIntegerSigned.class, t.getElementType().getClass());
  }

  @Test
  public final void testTypeDeclRecordField_0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    ch.checkPackageBegin(
      r.resolvePackageBegin(
        (StatementPackageBegin<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr("(package-begin x.y)"))));

    final TypeDeclType<IdentifierType, TType> td = ch.checkTypeDeclaration(
      r.resolveTypeDeclaration(
        (TypeDeclType<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr("(record T [(field x [integer signed 32])])"))));

    final TypeDeclRecord<IdentifierType, TType> tr =
      TypeDeclRecord.class.cast(td);

    final TRecord tt = TRecord.class.cast(tr.getType());
    this.checkRecordInvariants(tr, tt);
  }

  @Test
  public final void testTypeDeclRecordPadding_0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    ch.checkPackageBegin(
      r.resolvePackageBegin(
        (StatementPackageBegin<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr("(package-begin x.y)"))));

    final TypeDeclType<IdentifierType, TType> td = ch.checkTypeDeclaration(
      r.resolveTypeDeclaration(
        (TypeDeclType<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr("(record T [(padding-octets 8)])"))));

    final TypeDeclRecord<IdentifierType, TType> tr =
      TypeDeclRecord.class.cast(td);

    final TRecord tt = TRecord.class.cast(tr.getType());
    this.checkRecordInvariants(tr, tt);

    final TRecord.FieldPaddingOctets f = TRecord.FieldPaddingOctets.class.cast(
      tt.getFieldsInDeclarationOrder().get(0));
    Assert.assertEquals(
      BigInteger.valueOf(8L * 8L),
      f.getSizeInBits().getValue());
  }

  @Test
  public final void testTypeDeclRecordPaddingOctets_Error0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    ch.checkPackageBegin(
      r.resolvePackageBegin(
        (StatementPackageBegin<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr("(package-begin x.y)"))));

    this.expected.expect(
      new JPRACompilerCheckerExceptionMatcher(
        JPRACheckerErrorCode.PADDING_SIZE_INVALID));
    ch.checkTypeDeclaration(
      r.resolveTypeDeclaration(
        (TypeDeclType<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr("(record T [(padding-octets 0)])"))));
  }

  private void checkRecordInvariants(
    final TypeDeclRecord<IdentifierType, TType> tr,
    final TRecord tt)
  {
    Assert.assertEquals(tr.getName(), tt.getName());
    Assert.assertEquals(tr.getIdentifier(), tt.getIdentifier());

    final Map<FieldName, RecordFieldDeclValue<IdentifierType, TType>>
      tr_named = tr.getFieldsByName();
    final Map<FieldName, TRecord.FieldValue> tt_named =
      tt.getFieldsByName();
    final List<RecordFieldDeclType<IdentifierType, TType>> tr_order =
      tr.getFieldsInDeclarationOrder();
    final List<TRecord.FieldType> tt_order =
      tt.getFieldsInDeclarationOrder();

    Assert.assertEquals((long) tt_named.size(), (long) tr_named.size());
    Assert.assertEquals((long) tt_order.size(), (long) tr_order.size());

    for (int index = 0; index < tt_order.size(); ++index) {
      final TRecord.FieldType f = tt_order.get(index);
      Assert.assertEquals(f.getOwner(), tt);
    }

    tt_named.forEach((k, value) -> {
      Assert.assertTrue("Map contains " + k, tr_named.containsKey(k));
    });
  }

  @Test
  public final void testTypeExprName_0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    ch.checkPackageBegin(
      r.resolvePackageBegin(
        (StatementPackageBegin<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr("(package-begin x.y)"))));

    final TypeDeclType<IdentifierType, TType> td = ch.checkTypeDeclaration(
      r.resolveTypeDeclaration(
        (TypeDeclType<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr("(record T [])"))));

    final TypeExprName<IdentifierType, TType> te = TypeExprName.class.cast(
      ch.checkTypeExpression(
        r.resolveTypeExpression(
          p.parseTypeExpression(this.newStringSExpr("T")))));

    Assert.assertEquals(td.getIdentifier(), te.getIdentifier());
    Assert.assertEquals(td.getName(), te.getReference().type());
  }

  private void checkPackedInvariants(
    final TypeDeclPacked<IdentifierType, TType> tr,
    final TPacked tt)
  {
    Assert.assertEquals(tr.getName(), tt.getName());
    Assert.assertEquals(tr.getIdentifier(), tt.getIdentifier());

    final Map<FieldName, PackedFieldDeclValue<IdentifierType, TType>>
      tr_named = tr.getFieldsByName();
    final Map<FieldName, TPacked.FieldValue> tt_named =
      tt.getFieldsByName();
    final List<PackedFieldDeclType<IdentifierType, TType>> tr_order =
      tr.getFieldsInDeclarationOrder();
    final List<TPacked.FieldType> tt_order =
      tt.getFieldsInDeclarationOrder();

    Assert.assertEquals((long) tt_named.size(), (long) tr_named.size());
    Assert.assertEquals((long) tt_order.size(), (long) tr_order.size());

    for (int index = 0; index < tt_order.size(); ++index) {
      final TPacked.FieldType f = tt_order.get(index);
      Assert.assertEquals(f.getOwner(), tt);
    }

    tt_named.forEach((k, value) -> {
      Assert.assertTrue("Map contains " + k, tr_named.containsKey(k));
    });
  }

  @Test
  public final void testTypeDeclPackedField_0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    ch.checkPackageBegin(
      r.resolvePackageBegin(
        (StatementPackageBegin<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr("(package-begin x.y)"))));

    final TypeDeclType<IdentifierType, TType> td = ch.checkTypeDeclaration(
      r.resolveTypeDeclaration(
        (TypeDeclType<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr("(packed T [(field x [integer signed 32])])"))));

    final TypeDeclPacked<IdentifierType, TType> tr =
      TypeDeclPacked.class.cast(td);

    final TPacked tt = TPacked.class.cast(tr.getType());
    this.checkPackedInvariants(tr, tt);
  }

  @Test
  public final void testTypeDeclPackedPadding_0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    ch.checkPackageBegin(
      r.resolvePackageBegin(
        (StatementPackageBegin<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr("(package-begin x.y)"))));

    final TypeDeclType<IdentifierType, TType> td = ch.checkTypeDeclaration(
      r.resolveTypeDeclaration(
        (TypeDeclType<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr("(packed T [(padding-bits 8)])"))));

    final TypeDeclPacked<IdentifierType, TType> tr =
      TypeDeclPacked.class.cast(td);

    final TPacked tt = TPacked.class.cast(tr.getType());
    this.checkPackedInvariants(tr, tt);

    final TPacked.FieldPaddingBits f = TPacked.FieldPaddingBits.class.cast(
      tt.getFieldsInDeclarationOrder().get(0));
    Assert.assertEquals(BigInteger.valueOf(8L), f.getSize().getValue());
  }

  @Test
  public final void testTypeDeclPackedPaddingBits_Error0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    ch.checkPackageBegin(
      r.resolvePackageBegin(
        (StatementPackageBegin<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr("(package-begin x.y)"))));

    this.expected.expect(
      new JPRACompilerCheckerExceptionMatcher(
        JPRACheckerErrorCode.PADDING_SIZE_INVALID));
    ch.checkTypeDeclaration(
      r.resolveTypeDeclaration(
        (TypeDeclType<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr("(packed T [(padding-bits 0)])"))));
  }

  @Test
  public final void testTypeDeclPackedField_Error0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    ch.checkPackageBegin(
      r.resolvePackageBegin(
        (StatementPackageBegin<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr("(package-begin x.y)"))));

    this.expected.expect(
      new JPRACompilerCheckerExceptionMatcher(
        JPRACheckerErrorCode.PACKED_NON_INTEGER));
    ch.checkTypeDeclaration(
      r.resolveTypeDeclaration(
        (TypeDeclType<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr("(packed T [(field x [boolean-set 1 (x)])])"))));
  }

  @Test
  public final void testTypeDeclPackedSize_Error0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    ch.checkPackageBegin(
      r.resolvePackageBegin(
        (StatementPackageBegin<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr("(package-begin x.y)"))));

    this.expected.expect(
      new JPRACompilerCheckerExceptionMatcher(
        JPRACheckerErrorCode.PACKED_SIZE_NOT_SUPPORTED));
    ch.checkTypeDeclaration(
      r.resolveTypeDeclaration(
        (TypeDeclType<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr("(packed T [(field x [integer signed 4])])"))));
  }

  @Test
  public final void testRecordTypeExprIntegerSignedSizeUnsupported()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    ch.checkPackageBegin(
      r.resolvePackageBegin(
        (StatementPackageBegin<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr("(package-begin x.y)"))));

    this.expected.expect(
      new JPRACompilerCheckerExceptionMatcher(
        JPRACheckerErrorCode.RECORD_INTEGER_SIZE_UNSUPPORTED));
    ch.checkTypeDeclaration(
      r.resolveTypeDeclaration(
        (TypeDeclType<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr(
            "(record T [(field x [integer signed 128])])"))));
  }

  @Test
  public final void testRecordTypeExprIntegerUnsignedSizeUnsupported()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    ch.checkPackageBegin(
      r.resolvePackageBegin(
        (StatementPackageBegin<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr("(package-begin x.y)"))));

    this.expected.expect(
      new JPRACompilerCheckerExceptionMatcher(
        JPRACheckerErrorCode.RECORD_INTEGER_SIZE_UNSUPPORTED));
    ch.checkTypeDeclaration(
      r.resolveTypeDeclaration(
        (TypeDeclType<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr(
            "(record T [(field x [integer unsigned 128])])"))));
  }

  @Test
  public final void testRecordTypeExprIntegerUnsignedNormalizedSizeUnsupported()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    ch.checkPackageBegin(
      r.resolvePackageBegin(
        (StatementPackageBegin<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr("(package-begin x.y)"))));

    this.expected.expect(
      new JPRACompilerCheckerExceptionMatcher(
        JPRACheckerErrorCode.RECORD_INTEGER_SIZE_UNSUPPORTED));
    ch.checkTypeDeclaration(
      r.resolveTypeDeclaration(
        (TypeDeclType<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr(
            "(record T [(field x [integer unsigned-normalized 128])])"))));
  }

  @Test
  public final void testRecordTypeExprIntegerSignedNormalizedSizeUnsupported()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    ch.checkPackageBegin(
      r.resolvePackageBegin(
        (StatementPackageBegin<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr("(package-begin x.y)"))));

    this.expected.expect(
      new JPRACompilerCheckerExceptionMatcher(
        JPRACheckerErrorCode.RECORD_INTEGER_SIZE_UNSUPPORTED));
    ch.checkTypeDeclaration(
      r.resolveTypeDeclaration(
        (TypeDeclType<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr(
            "(record T [(field x [integer signed-normalized 128])])"))));
  }

  @Test
  public final void testPackedTypeExprIntegerSignedSizeUnsupported()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    ch.checkPackageBegin(
      r.resolvePackageBegin(
        (StatementPackageBegin<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr("(package-begin x.y)"))));

    this.expected.expect(
      new JPRACompilerCheckerExceptionMatcher(
        JPRACheckerErrorCode.RECORD_INTEGER_SIZE_UNSUPPORTED));
    ch.checkTypeDeclaration(
      r.resolveTypeDeclaration(
        (TypeDeclType<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr(
            "(packed T [(field x [integer signed 128])])"))));
  }

  @Test
  public final void testPackedTypeExprIntegerUnsignedSizeUnsupported()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    ch.checkPackageBegin(
      r.resolvePackageBegin(
        (StatementPackageBegin<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr("(package-begin x.y)"))));

    this.expected.expect(
      new JPRACompilerCheckerExceptionMatcher(
        JPRACheckerErrorCode.RECORD_INTEGER_SIZE_UNSUPPORTED));
    ch.checkTypeDeclaration(
      r.resolveTypeDeclaration(
        (TypeDeclType<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr(
            "(packed T [(field x [integer unsigned 128])])"))));
  }

  @Test
  public final void testPackedTypeExprIntegerUnsignedNormalizedSizeUnsupported()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    ch.checkPackageBegin(
      r.resolvePackageBegin(
        (StatementPackageBegin<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr("(package-begin x.y)"))));

    this.expected.expect(
      new JPRACompilerCheckerExceptionMatcher(
        JPRACheckerErrorCode.RECORD_INTEGER_SIZE_UNSUPPORTED));
    ch.checkTypeDeclaration(
      r.resolveTypeDeclaration(
        (TypeDeclType<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr(
            "(packed T [(field x [integer unsigned-normalized 128])])"))));
  }

  @Test
  public final void testPackedTypeExprIntegerSignedNormalizedSizeUnsupported()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);
    final JPRACheckerType ch = this.newChecker(
      c, JPRACheckerStandardCapabilities.newCapabilities());

    ch.checkPackageBegin(
      r.resolvePackageBegin(
        (StatementPackageBegin<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr("(package-begin x.y)"))));

    this.expected.expect(
      new JPRACompilerCheckerExceptionMatcher(
        JPRACheckerErrorCode.RECORD_INTEGER_SIZE_UNSUPPORTED));
    ch.checkTypeDeclaration(
      r.resolveTypeDeclaration(
        (TypeDeclType<Unresolved, Untyped>) p.parseStatement(
          this.newStringSExpr(
            "(packed T [(field x [integer signed-normalized 128])])"))));
  }
}
