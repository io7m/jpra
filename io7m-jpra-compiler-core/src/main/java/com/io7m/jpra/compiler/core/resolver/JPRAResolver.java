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

package com.io7m.jpra.compiler.core.resolver;

import com.io7m.jpra.model.Parsed;
import com.io7m.jpra.model.ResolvedType;
import com.io7m.jpra.model.size_expressions.SizeExprType;
import com.io7m.jpra.model.statements.StatementPackageBegin;
import com.io7m.jpra.model.statements.StatementPackageEnd;
import com.io7m.jpra.model.statements.StatementPackageImport;
import com.io7m.jpra.model.type_declarations.TypeDeclType;
import com.io7m.jpra.model.type_expressions.TypeExprType;
import com.io7m.junreachable.UnimplementedCodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default implementation of the {@link JPRAResolverType} interface.
 */

public final class JPRAResolver implements JPRAResolverType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(JPRAResolver.class);
  }

  private JPRAResolver()
  {

  }

  /**
   * @return A new resolver
   */

  public static JPRAResolverType newResolver()
  {
    return new JPRAResolver();
  }

  @Override public void resolvePackageBegin(
    final StatementPackageBegin<Parsed> s)
    throws JPRACompilerResolverException
  {
    // TODO: Generated method stub!
    throw new UnimplementedCodeException();
  }

  @Override public void resolvePackageImport(
    final StatementPackageImport<Parsed> s)
    throws JPRACompilerResolverException
  {
    // TODO: Generated method stub!
    throw new UnimplementedCodeException();
  }

  @Override public void resolvePackageEnd(
    final StatementPackageEnd<Parsed> s)
    throws JPRACompilerResolverException
  {
    // TODO: Generated method stub!
    throw new UnimplementedCodeException();
  }

  @Override public TypeDeclType<ResolvedType> resolveTypeDeclaration(
    final TypeDeclType<Parsed> expr)
    throws JPRACompilerResolverException
  {
    // TODO: Generated method stub!
    throw new UnimplementedCodeException();
  }

  @Override public TypeExprType<ResolvedType> resolveTypeExpression(
    final TypeExprType<Parsed> expr)
    throws JPRACompilerResolverException
  {
    // TODO: Generated method stub!
    throw new UnimplementedCodeException();
  }

  @Override public SizeExprType<ResolvedType> resolveSizeExpression(
    final SizeExprType<Parsed> expr)
    throws JPRACompilerResolverException
  {
    // TODO: Generated method stub!
    throw new UnimplementedCodeException();
  }
}
