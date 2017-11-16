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

package com.io7m.jpra.compiler.core.pipeline;

import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jpra.compiler.core.JPRACompilerException;
import com.io7m.jpra.compiler.core.checker.JPRACheckerType;
import com.io7m.jpra.compiler.core.parser.JPRAParserType;
import com.io7m.jpra.compiler.core.resolver.JPRAResolverType;
import com.io7m.jpra.model.Unresolved;
import com.io7m.jpra.model.Untyped;
import com.io7m.jpra.model.contexts.PackageContextType;
import com.io7m.jpra.model.names.IdentifierType;
import com.io7m.jpra.model.statements.StatementCommandSize;
import com.io7m.jpra.model.statements.StatementCommandType;
import com.io7m.jpra.model.statements.StatementMatcherType;
import com.io7m.jpra.model.statements.StatementPackageBegin;
import com.io7m.jpra.model.statements.StatementPackageEnd;
import com.io7m.jpra.model.statements.StatementPackageImport;
import com.io7m.jpra.model.type_declarations.TypeDeclType;
import com.io7m.jpra.model.types.TType;
import com.io7m.jsx.SExpressionType;
import com.io7m.junreachable.UnimplementedCodeException;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * Default implementation of the {@link JPRAPipelineType} interface.
 */

public final class JPRAPipeline implements JPRAPipelineType
{
  private final JPRAParserType parser;
  private final JPRAResolverType resolver;
  private final JPRACheckerType checker;

  private JPRAPipeline(
    final JPRAParserType in_parser,
    final JPRAResolverType in_resolver,
    final JPRACheckerType in_checker)
  {
    this.checker = Objects.requireNonNull(in_checker, "Checker");
    this.parser = Objects.requireNonNull(in_parser, "Parser");
    this.resolver = Objects.requireNonNull(in_resolver, "Resolver");
  }

  /**
   * Construct a new pipeline.
   *
   * @param in_parser   The parser
   * @param in_resolver The resolver
   * @param in_checker  The type checker
   *
   * @return A new pipeline
   */

  public static JPRAPipelineType newPipeline(
    final JPRAParserType in_parser,
    final JPRAResolverType in_resolver,
    final JPRACheckerType in_checker)
  {
    return new JPRAPipeline(in_parser, in_resolver, in_checker);
  }

  @Override
  public Optional<PackageContextType> onExpression(final SExpressionType e)
    throws JPRACompilerException
  {
    return this.parser.parseStatement(e).matchStatement(
      new StatementMatcherType<Unresolved, Untyped,
        Optional<PackageContextType>, JPRACompilerException>()
      {
        @Override
        public Optional<PackageContextType> matchPackageBegin(
          final StatementPackageBegin<Unresolved, Untyped> s)
          throws JPRACompilerException
        {
          final StatementPackageBegin<IdentifierType, Untyped> r =
            JPRAPipeline.this.resolver.resolvePackageBegin(s);
          JPRAPipeline.this.checker.checkPackageBegin(r);
          return Optional.empty();
        }

        @Override
        public Optional<PackageContextType> matchPackageEnd(
          final StatementPackageEnd<Unresolved, Untyped> s)
          throws JPRACompilerException
        {
          final StatementPackageEnd<IdentifierType, Untyped> r =
            JPRAPipeline.this.resolver.resolvePackageEnd(s);
          final PackageContextType c =
            JPRAPipeline.this.checker.checkPackageEnd(r);
          return Optional.of(c);
        }

        @Override
        public Optional<PackageContextType> matchPackageImport(
          final StatementPackageImport<Unresolved, Untyped> s)
          throws JPRACompilerException
        {
          final StatementPackageImport<IdentifierType, Untyped> r =
            JPRAPipeline.this.resolver.resolvePackageImport(s);
          return Optional.empty();
        }

        @Override
        public Optional<PackageContextType> matchTypeDecl(
          final TypeDeclType<Unresolved, Untyped> s)
          throws JPRACompilerException
        {
          final TypeDeclType<IdentifierType, Untyped> r =
            JPRAPipeline.this.resolver.resolveTypeDeclaration(s);
          JPRAPipeline.this.checker.checkTypeDeclaration(r);
          return Optional.empty();
        }

        @Override
        public Optional<PackageContextType> matchShowType(
          final StatementCommandType<Unresolved, Untyped> s)
          throws JPRACompilerException
        {
          final StatementCommandType<IdentifierType, Untyped> r =
            JPRAPipeline.this.resolver.resolveCommandType(s);
          final StatementCommandType<IdentifierType, TType> c =
            JPRAPipeline.this.checker.checkCommandType(r);

          System.out.println(c.getExpression().getType());
          return Optional.empty();
        }

        @Override
        public Optional<PackageContextType> matchShowSize(
          final StatementCommandSize<Unresolved, Untyped> s)
          throws JPRACompilerException
        {
          throw new UnimplementedCodeException();
        }
      });
  }

  @Override
  public void onEOF(
    final Optional<LexicalPosition<Path>> lex)
    throws JPRACompilerException
  {
    this.parser.parseEOF(lex);
    this.resolver.resolveEOF(lex);
  }
}
