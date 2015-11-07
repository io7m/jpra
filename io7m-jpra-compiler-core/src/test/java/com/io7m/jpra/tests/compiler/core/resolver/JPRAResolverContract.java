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

package com.io7m.jpra.tests.compiler.core.resolver;

import com.gs.collections.impl.factory.Lists;
import com.io7m.jpra.compiler.core.parser.JPRAParserType;
import com.io7m.jpra.compiler.core.resolver.JPRACompilerResolverException;
import com.io7m.jpra.compiler.core.resolver.JPRAResolverErrorCode;
import com.io7m.jpra.compiler.core.resolver.JPRAResolverType;
import com.io7m.jpra.model.Unresolved;
import com.io7m.jpra.model.Untyped;
import com.io7m.jpra.model.contexts.GlobalContextType;
import com.io7m.jpra.model.contexts.GlobalContexts;
import com.io7m.jpra.model.contexts.PackageContextType;
import com.io7m.jpra.model.loading.JPRAModelLoadingException;
import com.io7m.jpra.model.loading.JPRAPackageLoaderType;
import com.io7m.jpra.model.names.IdentifierType;
import com.io7m.jpra.model.names.PackageNameQualified;
import com.io7m.jpra.model.names.PackageNameUnqualified;
import com.io7m.jpra.model.size_expressions.SizeExprConstant;
import com.io7m.jpra.model.size_expressions.SizeExprType;
import com.io7m.jpra.model.statements.StatementCommandType;
import com.io7m.jpra.model.statements.StatementPackageBegin;
import com.io7m.jpra.model.statements.StatementPackageEnd;
import com.io7m.jpra.model.statements.StatementPackageImport;
import com.io7m.jpra.model.type_declarations.TypeDeclType;
import com.io7m.jpra.model.type_expressions.TypeExprArray;
import com.io7m.jpra.model.type_expressions.TypeExprBooleanSet;
import com.io7m.jpra.model.type_expressions.TypeExprFloat;
import com.io7m.jpra.model.type_expressions.TypeExprIntegerSigned;
import com.io7m.jpra.model.type_expressions.TypeExprIntegerSignedNormalized;
import com.io7m.jpra.model.type_expressions.TypeExprIntegerUnsigned;
import com.io7m.jpra.model.type_expressions.TypeExprIntegerUnsignedNormalized;
import com.io7m.jpra.model.type_expressions.TypeExprMatrix;
import com.io7m.jpra.model.type_expressions.TypeExprString;
import com.io7m.jpra.model.type_expressions.TypeExprVector;
import com.io7m.jsx.SExpressionType;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableCauseMatcher;
import org.junit.rules.ExpectedException;

import java.math.BigInteger;

@SuppressWarnings("unchecked") public abstract class JPRAResolverContract
{
  @Rule public final ExpectedException expected = ExpectedException.none();

  private static <I, T> void checkSizeExpressionConstant(
    final SizeExprType<I, T> s,
    final BigInteger v)
  {
    final SizeExprConstant<I, T> sc = SizeExprConstant.class.cast(s);
    final BigInteger rv = sc.getValue();
    Assert.assertEquals(v, rv);
  }

  protected abstract JPRAParserType newParser();

  protected abstract JPRAResolverType newResolver(final GlobalContextType c);

  protected abstract SExpressionType newStringSExpr(
    final String expr);

  @Test public final void testPackageNested()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);

    r.resolvePackageBegin(
      StatementPackageBegin.class.cast(
        p.parseStatement(this.newStringSExpr("(package-begin x.y.z)"))));
    this.expected.expect(
      new JPRACompilerResolverExceptionMatcher(
        JPRAResolverErrorCode.PACKAGE_NESTED));
    r.resolvePackageBegin(
      StatementPackageBegin.class.cast(
        p.parseStatement(this.newStringSExpr("(package-begin a.b.c)"))));
  }

  @Test public final void testPackageBegin()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);

    final StatementPackageBegin<Unresolved, Untyped> pb =
      StatementPackageBegin.class.cast(
        p.parseStatement(this.newStringSExpr("(package-begin x.y.z)")));
    final StatementPackageBegin<IdentifierType, Untyped> rp =
      r.resolvePackageBegin(pb);

    Assert.assertEquals(pb.getPackageName(), rp.getPackageName());
    Assert.assertEquals(pb.getLexicalInformation(), rp.getLexicalInformation());
  }

  @Test public final void testPackageDuplicate()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);

    c.getPackage(
      new PackageNameQualified(
        Lists.immutable.of(
          PackageNameUnqualified.of("x"),
          PackageNameUnqualified.of("y"),
          PackageNameUnqualified.of("z"))));

    this.expected.expect(
      new JPRACompilerResolverExceptionMatcher(
        JPRAResolverErrorCode.PACKAGE_DUPLICATE));
    r.resolvePackageBegin(
      StatementPackageBegin.class.cast(
        p.parseStatement(this.newStringSExpr("(package-begin x.y.z)"))));
  }

  @Test public final void testPackageEndNoCurrent()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);

    this.expected.expect(
      new JPRACompilerResolverExceptionMatcher(
        JPRAResolverErrorCode.NO_CURRENT_PACKAGE));
    r.resolvePackageEnd(
      StatementPackageEnd.class.cast(
        p.parseStatement(this.newStringSExpr("(package-end)"))));
  }

  @Test public final void testPackageImportNoCurrent()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);

    this.expected.expect(
      new JPRACompilerResolverExceptionMatcher(
        JPRAResolverErrorCode.NO_CURRENT_PACKAGE));
    r.resolvePackageImport(
      StatementPackageImport.class.cast(
        p.parseStatement(this.newStringSExpr("(import x.y.z as q)"))));
  }

  @Test public final void testPackageImportConflict()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);

    c.getPackage(
      new PackageNameQualified(
        Lists.immutable.of(
          PackageNameUnqualified.of("x"), PackageNameUnqualified.of("a"))));
    c.getPackage(
      new PackageNameQualified(
        Lists.immutable.of(
          PackageNameUnqualified.of("x"), PackageNameUnqualified.of("b"))));

    r.resolvePackageBegin(
      StatementPackageBegin.class.cast(
        p.parseStatement(this.newStringSExpr("(package-begin a.b.c)"))));
    r.resolvePackageImport(
      StatementPackageImport.class.cast(
        p.parseStatement(this.newStringSExpr("(import x.a as q)"))));

    this.expected.expect(
      new JPRACompilerResolverExceptionMatcher(
        JPRAResolverErrorCode.PACKAGE_IMPORT_CONFLICT));
    r.resolvePackageImport(
      StatementPackageImport.class.cast(
        p.parseStatement(this.newStringSExpr("(import x.b as q)"))));
  }

  @Test public final void testPackageImportNonexistent()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new NoPackagesLoader());
    final JPRAResolverType r = this.newResolver(c);

    r.resolvePackageBegin(
      StatementPackageBegin.class.cast(
        p.parseStatement(this.newStringSExpr("(package-begin a.b.c)"))));

    this.expected.expect(
      new ThrowableCauseMatcher<>(
        new ThrowableCauseMatcher<>(
          new JPRACompilerResolverExceptionMatcher(
            JPRAResolverErrorCode.PACKAGE_NONEXISTENT))));

    r.resolvePackageImport(
      StatementPackageImport.class.cast(
        p.parseStatement(this.newStringSExpr("(import x.a as q)"))));
  }

  @Test public final void testTypeDeclRecordNoCurrent()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);

    this.expected.expect(
      new JPRACompilerResolverExceptionMatcher(
        JPRAResolverErrorCode.NO_CURRENT_PACKAGE));
    r.resolveTypeDeclaration(
      TypeDeclType.class.cast(
        p.parseStatement(this.newStringSExpr("(record T ())"))));
  }

  @Test public final void testTypeDeclRecordNonexistentPackage0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);

    r.resolvePackageBegin(
      StatementPackageBegin.class.cast(
        p.parseStatement(this.newStringSExpr("(package-begin a.b.c)"))));

    this.expected.expect(
      new JPRACompilerResolverExceptionMatcher(
        JPRAResolverErrorCode.PACKAGE_REFERENCE_NONEXISTENT));
    r.resolveTypeDeclaration(
      TypeDeclType.class.cast(
        p.parseStatement(
          this.newStringSExpr("(record T [(field x p:T)])"))));
  }

  @Test public final void testTypeDeclRecordNonexistentPackageType0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);

    r.resolvePackageBegin(
      StatementPackageBegin.class.cast(
        p.parseStatement(this.newStringSExpr("(package-begin a.b.c)"))));
    r.resolvePackageImport(
      StatementPackageImport.class.cast(
        p.parseStatement(this.newStringSExpr("(import x.y as p)"))));

    this.expected.expect(
      new JPRACompilerResolverExceptionMatcher(
        JPRAResolverErrorCode.TYPE_NONEXISTENT));
    r.resolveTypeDeclaration(
      TypeDeclType.class.cast(
        p.parseStatement(
          this.newStringSExpr("(record T [(field x p:T)])"))));
  }

  @Test public final void testTypeDeclRecordNonexistentLocalType0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);

    r.resolvePackageBegin(
      StatementPackageBegin.class.cast(
        p.parseStatement(this.newStringSExpr("(package-begin a.b.c)"))));

    this.expected.expect(
      new JPRACompilerResolverExceptionMatcher(
        JPRAResolverErrorCode.TYPE_NONEXISTENT));
    r.resolveTypeDeclaration(
      TypeDeclType.class.cast(
        p.parseStatement(
          this.newStringSExpr("(record T [(field x U)])"))));
  }

  @Test public final void testTypeDeclRecordNonexistentLocalType1()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);

    r.resolvePackageBegin(
      StatementPackageBegin.class.cast(
        p.parseStatement(this.newStringSExpr("(package-begin a.b.c)"))));

    this.expected.expect(
      new JPRACompilerResolverExceptionMatcher(
        JPRAResolverErrorCode.TYPE_NONEXISTENT));
    r.resolveTypeDeclaration(
      TypeDeclType.class.cast(
        p.parseStatement(
          this.newStringSExpr("(record T [(field x T)])"))));
  }

  @Test public final void testTypeDeclRecordDuplicateType0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);

    r.resolvePackageBegin(
      StatementPackageBegin.class.cast(
        p.parseStatement(this.newStringSExpr("(package-begin a.b.c)"))));
    r.resolveTypeDeclaration(
      TypeDeclType.class.cast(
        p.parseStatement(
          this.newStringSExpr("(record T [(field x [integer signed 32])])"))));

    this.expected.expect(
      new JPRACompilerResolverExceptionMatcher(
        JPRAResolverErrorCode.TYPE_DUPLICATE));
    r.resolveTypeDeclaration(
      TypeDeclType.class.cast(
        p.parseStatement(
          this.newStringSExpr("(record T [(field x [integer signed 32])])"))));
  }

  @Test public final void testTypeExprName_Error0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);

    this.expected.expect(
      new JPRACompilerResolverExceptionMatcher(
        JPRAResolverErrorCode.TYPE_NONEXISTENT));
    r.resolveCommandType(
      StatementCommandType.class.cast(
        p.parseStatement(this.newStringSExpr("(:type T)"))));
  }

  @Test public final void testTypeExprIntegerSigned_0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);

    final StatementCommandType<IdentifierType, Untyped> ex =
      r.resolveCommandType(
        StatementCommandType.class.cast(
          p.parseStatement(
            this.newStringSExpr("(:type [integer signed 32])"))));

    final TypeExprIntegerSigned<IdentifierType, Untyped> ee =
      TypeExprIntegerSigned.class.cast(ex.getExpression());
    JPRAResolverContract.checkSizeExpressionConstant(
      ee.getSize(), BigInteger.valueOf(32L));
  }

  @Test public final void testTypeExprIntegerUnsigned_0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);

    final StatementCommandType<IdentifierType, Untyped> ex =
      r.resolveCommandType(
        StatementCommandType.class.cast(
          p.parseStatement(
            this.newStringSExpr("(:type [integer unsigned 32])"))));

    final TypeExprIntegerUnsigned<IdentifierType, Untyped> ee =
      TypeExprIntegerUnsigned.class.cast(ex.getExpression());
    JPRAResolverContract.checkSizeExpressionConstant(
      ee.getSize(), BigInteger.valueOf(32L));
  }

  @Test public final void testTypeExprIntegerUnsignedNormalized_0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);

    final StatementCommandType<IdentifierType, Untyped> ex =
      r.resolveCommandType(
        StatementCommandType.class.cast(
          p.parseStatement(
            this.newStringSExpr(
              "(:type [integer unsigned-normalized 32])"))));

    final TypeExprIntegerUnsignedNormalized<IdentifierType, Untyped> ee =
      TypeExprIntegerUnsignedNormalized.class.cast(ex.getExpression());
    JPRAResolverContract.checkSizeExpressionConstant(
      ee.getSize(), BigInteger.valueOf(32L));
  }

  @Test public final void testTypeExprIntegerSignedNormalized_0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);

    final StatementCommandType<IdentifierType, Untyped> ex =
      r.resolveCommandType(
        StatementCommandType.class.cast(
          p.parseStatement(
            this.newStringSExpr(
              "(:type [integer signed-normalized 32])"))));

    final TypeExprIntegerSignedNormalized<IdentifierType, Untyped> ee =
      TypeExprIntegerSignedNormalized.class.cast(ex.getExpression());
    JPRAResolverContract.checkSizeExpressionConstant(
      ee.getSize(), BigInteger.valueOf(32L));
  }

  @Test public final void testTypeExprFloat_0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);

    final StatementCommandType<IdentifierType, Untyped> ex =
      r.resolveCommandType(
        StatementCommandType.class.cast(
          p.parseStatement(
            this.newStringSExpr(
              "(:type [float 32])"))));

    final TypeExprFloat<IdentifierType, Untyped> ee =
      TypeExprFloat.class.cast(ex.getExpression());
    JPRAResolverContract.checkSizeExpressionConstant(
      ee.getSize(), BigInteger.valueOf(32L));
  }

  @Test public final void testTypeExprString_0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);

    final StatementCommandType<IdentifierType, Untyped> ex =
      r.resolveCommandType(
        StatementCommandType.class.cast(
          p.parseStatement(
            this.newStringSExpr(
              "(:type [string 32 \"UTF-8\"])"))));

    final TypeExprString<IdentifierType, Untyped> ee =
      TypeExprString.class.cast(ex.getExpression());
    JPRAResolverContract.checkSizeExpressionConstant(
      ee.getSize(), BigInteger.valueOf(32L));
  }

  @Test public final void testTypeExprVector_0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);

    final StatementCommandType<IdentifierType, Untyped> ex =
      r.resolveCommandType(
        StatementCommandType.class.cast(
          p.parseStatement(
            this.newStringSExpr(
              "(:type [vector (integer signed 32) 32])"))));

    final TypeExprVector<IdentifierType, Untyped> ee =
      TypeExprVector.class.cast(ex.getExpression());
    JPRAResolverContract.checkSizeExpressionConstant(
      ee.getElementCount(), BigInteger.valueOf(32L));
  }

  @Test public final void testTypeExprMatrix_0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);

    final StatementCommandType<IdentifierType, Untyped> ex =
      r.resolveCommandType(
        StatementCommandType.class.cast(
          p.parseStatement(
            this.newStringSExpr(
              "(:type [matrix (integer signed 32) 2 4])"))));

    final TypeExprMatrix<IdentifierType, Untyped> ee =
      TypeExprMatrix.class.cast(ex.getExpression());
    JPRAResolverContract.checkSizeExpressionConstant(
      ee.getWidth(), BigInteger.valueOf(2L));
    JPRAResolverContract.checkSizeExpressionConstant(
      ee.getHeight(), BigInteger.valueOf(4L));
  }

  @Test public final void testTypeExprBooleanSet_0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);

    final StatementCommandType<IdentifierType, Untyped> ex =
      r.resolveCommandType(
        StatementCommandType.class.cast(
          p.parseStatement(
            this.newStringSExpr(
              "(:type [boolean-set 1 (x)])"))));

    final TypeExprBooleanSet<IdentifierType, Untyped> ee =
      TypeExprBooleanSet.class.cast(ex.getExpression());
    JPRAResolverContract.checkSizeExpressionConstant(
      ee.getSizeExpression(), BigInteger.valueOf(1L));
  }

  @Test public final void testTypeExprArray_0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);

    final StatementCommandType<IdentifierType, Untyped> ex =
      r.resolveCommandType(
        StatementCommandType.class.cast(
          p.parseStatement(
            this.newStringSExpr(
              "(:type [array (integer signed 32) 64])"))));

    final TypeExprArray<IdentifierType, Untyped> ee =
      TypeExprArray.class.cast(ex.getExpression());
    JPRAResolverContract.checkSizeExpressionConstant(
      ee.getElementCount(), BigInteger.valueOf(64L));
  }

  @Test public final void testTypeDeclPackedNoCurrent()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);

    this.expected.expect(
      new JPRACompilerResolverExceptionMatcher(
        JPRAResolverErrorCode.NO_CURRENT_PACKAGE));
    r.resolveTypeDeclaration(
      TypeDeclType.class.cast(
        p.parseStatement(this.newStringSExpr("(packed T ())"))));
  }

  @Test public final void testTypeDeclPackedNonexistentPackage0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);

    r.resolvePackageBegin(
      StatementPackageBegin.class.cast(
        p.parseStatement(this.newStringSExpr("(package-begin a.b.c)"))));

    this.expected.expect(
      new JPRACompilerResolverExceptionMatcher(
        JPRAResolverErrorCode.PACKAGE_REFERENCE_NONEXISTENT));
    r.resolveTypeDeclaration(
      TypeDeclType.class.cast(
        p.parseStatement(
          this.newStringSExpr("(packed T [(field x p:T)])"))));
  }

  @Test public final void testTypeDeclPackedNonexistentPackageType0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);

    r.resolvePackageBegin(
      StatementPackageBegin.class.cast(
        p.parseStatement(this.newStringSExpr("(package-begin a.b.c)"))));
    r.resolvePackageImport(
      StatementPackageImport.class.cast(
        p.parseStatement(this.newStringSExpr("(import x.y as p)"))));

    this.expected.expect(
      new JPRACompilerResolverExceptionMatcher(
        JPRAResolverErrorCode.TYPE_NONEXISTENT));
    r.resolveTypeDeclaration(
      TypeDeclType.class.cast(
        p.parseStatement(
          this.newStringSExpr("(packed T [(field x p:T)])"))));
  }

  @Test public final void testTypeDeclPackedNonexistentLocalType0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);

    r.resolvePackageBegin(
      StatementPackageBegin.class.cast(
        p.parseStatement(this.newStringSExpr("(package-begin a.b.c)"))));

    this.expected.expect(
      new JPRACompilerResolverExceptionMatcher(
        JPRAResolverErrorCode.TYPE_NONEXISTENT));
    r.resolveTypeDeclaration(
      TypeDeclType.class.cast(
        p.parseStatement(
          this.newStringSExpr("(packed T [(field x U)])"))));
  }

  @Test public final void testTypeDeclPackedNonexistentLocalType1()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);

    r.resolvePackageBegin(
      StatementPackageBegin.class.cast(
        p.parseStatement(this.newStringSExpr("(package-begin a.b.c)"))));

    this.expected.expect(
      new JPRACompilerResolverExceptionMatcher(
        JPRAResolverErrorCode.TYPE_NONEXISTENT));
    r.resolveTypeDeclaration(
      TypeDeclType.class.cast(
        p.parseStatement(
          this.newStringSExpr("(packed T [(field x T)])"))));
  }

  @Test public final void testTypeDeclPackedDuplicateType0()
    throws Exception
  {
    final JPRAParserType p = this.newParser();
    final GlobalContextType c =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final JPRAResolverType r = this.newResolver(c);

    r.resolvePackageBegin(
      StatementPackageBegin.class.cast(
        p.parseStatement(this.newStringSExpr("(package-begin a.b.c)"))));
    r.resolveTypeDeclaration(
      TypeDeclType.class.cast(
        p.parseStatement(
          this.newStringSExpr("(packed T [(field x [integer signed 32])])"))));

    this.expected.expect(
      new JPRACompilerResolverExceptionMatcher(
        JPRAResolverErrorCode.TYPE_DUPLICATE));
    r.resolveTypeDeclaration(
      TypeDeclType.class.cast(
        p.parseStatement(
          this.newStringSExpr("(packed T [(field x [integer signed 32])])"))));
  }

  private static final class NoPackagesLoader implements JPRAPackageLoaderType
  {
    @Override public PackageContextType evaluate(
      final GlobalContextType c,
      final PackageNameQualified p)
      throws JPRAModelLoadingException
    {
      throw new JPRAModelLoadingException(
        new JPRACompilerResolverException(
          p.getLexicalInformation(),
          JPRAResolverErrorCode.PACKAGE_NONEXISTENT,
          "No such package"));
    }
  }

}
