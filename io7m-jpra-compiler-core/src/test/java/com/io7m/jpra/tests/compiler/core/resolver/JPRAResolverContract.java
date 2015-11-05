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
import com.io7m.jlexing.core.ImmutableLexicalPositionType;
import com.io7m.jpra.compiler.core.parser.JPRAParserType;
import com.io7m.jpra.compiler.core.resolver.JPRACompilerResolverException;
import com.io7m.jpra.compiler.core.resolver.JPRAResolverErrorCode;
import com.io7m.jpra.compiler.core.resolver.JPRAResolverType;
import com.io7m.jpra.model.contexts.GlobalContextType;
import com.io7m.jpra.model.contexts.GlobalContexts;
import com.io7m.jpra.model.contexts.PackageContextType;
import com.io7m.jpra.model.type_declarations.TypeDeclType;
import com.io7m.jpra.model.loading.JPRAModelLoadingException;
import com.io7m.jpra.model.loading.JPRAPackageLoaderType;
import com.io7m.jpra.model.names.PackageNameQualified;
import com.io7m.jpra.model.names.PackageNameUnqualified;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.jpra.model.statements.StatementPackageBegin;
import com.io7m.jpra.model.statements.StatementPackageEnd;
import com.io7m.jpra.model.statements.StatementPackageImport;
import com.io7m.jpra.model.types.TypeUserDefinedType;
import com.io7m.jsx.SExpressionType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableCauseMatcher;
import org.junit.rules.ExpectedException;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unchecked") public abstract class JPRAResolverContract
{
  @Rule public final ExpectedException expected = ExpectedException.none();

  protected abstract JPRAParserType newParser();

  protected abstract JPRAResolverType newResolver(final GlobalContextType c);

  protected abstract SExpressionType newFileSExpr(
    final String name);

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

  @Test public final void testTypeDeclNoCurrent()
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

  private static final class AlwaysEmptyLoader implements JPRAPackageLoaderType
  {
    @Override public PackageContextType evaluate(
      final GlobalContextType c,
      final PackageNameQualified p)
    {
      return new PackageContextType()
      {
        @Override public GlobalContextType getGlobalContext()
        {
          return c;
        }

        @Override public Map<TypeName, TypeUserDefinedType> getTypes()
        {
          return Collections.unmodifiableMap(new HashMap<>());
        }

        @Override public PackageNameQualified getName()
        {
          return p;
        }

        @Override
        public Optional<ImmutableLexicalPositionType<Path>>
        getLexicalInformation()
        {
          return p.getLexicalInformation();
        }
      };
    }
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
