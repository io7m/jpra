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

package com.io7m.jpra.compiler.core.checker;

import com.io7m.jpra.model.Untyped;
import com.io7m.jpra.model.contexts.PackageContextType;
import com.io7m.jpra.model.names.IdentifierType;
import com.io7m.jpra.model.statements.StatementCommandType;
import com.io7m.jpra.model.statements.StatementPackageBegin;
import com.io7m.jpra.model.statements.StatementPackageEnd;
import com.io7m.jpra.model.type_declarations.TypeDeclType;
import com.io7m.jpra.model.type_expressions.TypeExprType;
import com.io7m.jpra.model.types.TType;

/**
 * The type of type checkers.
 */

public interface JPRACheckerType
{
  /**
   * Check a package begin statement.
   *
   * @param s The statement
   *
   * @throws JPRACompilerCheckerException On resolution errors
   */

  void checkPackageBegin(
    StatementPackageBegin<IdentifierType, Untyped> s)
    throws JPRACompilerCheckerException;

  /**
   * Complete the current package.
   *
   * @param s The statement
   *
   * @return A completed package context
   *
   * @throws JPRACompilerCheckerException On resolution errors
   */

  PackageContextType checkPackageEnd(
    StatementPackageEnd<IdentifierType, Untyped> s)
    throws JPRACompilerCheckerException;

  /**
   * Check a type declaration.
   *
   * @param decl The input declaration
   *
   * @return A type declaration
   *
   * @throws JPRACompilerCheckerException On type errors
   */

  TypeDeclType<IdentifierType, TType> checkTypeDeclaration(
    TypeDeclType<IdentifierType, Untyped> decl)
    throws JPRACompilerCheckerException;

  /**
   * Check a type expression.
   *
   * @param expr The input expression
   *
   * @return A type expression
   *
   * @throws JPRACompilerCheckerException On type errors
   */

  TypeExprType<IdentifierType, TType> checkTypeExpression(
    TypeExprType<IdentifierType, Untyped> expr)
    throws JPRACompilerCheckerException;

  /**
   * Check a {@code :type} command.
   *
   * @param s The statement
   *
   * @return A type expression
   *
   * @throws JPRACompilerCheckerException On type errors
   */

  StatementCommandType<IdentifierType, TType> checkCommandType(
    StatementCommandType<IdentifierType, Untyped> s)
    throws JPRACompilerCheckerException;
}
