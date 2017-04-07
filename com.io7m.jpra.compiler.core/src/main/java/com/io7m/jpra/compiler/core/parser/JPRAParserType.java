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

package com.io7m.jpra.compiler.core.parser;

import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jpra.model.Unresolved;
import com.io7m.jpra.model.Untyped;
import com.io7m.jpra.model.size_expressions.SizeExprType;
import com.io7m.jpra.model.statements.StatementType;
import com.io7m.jpra.model.type_expressions.TypeExprType;
import com.io7m.jsx.SExpressionType;

import java.nio.file.Path;
import java.util.Optional;

/**
 * The type of parsers.
 */

public interface JPRAParserType
{
  /**
   * Parse a statement.
   *
   * @param expr The input expression
   *
   * @return A statement
   *
   * @throws JPRACompilerParseException On parse errors
   */

  StatementType<Unresolved, Untyped> parseStatement(
    SExpressionType expr)
    throws JPRACompilerParseException;

  /**
   * Parse a type expression.
   *
   * @param expr The input expression
   *
   * @return A type expression
   *
   * @throws JPRACompilerParseException On parse errors
   */

  TypeExprType<Unresolved, Untyped> parseTypeExpression(
    SExpressionType expr)
    throws JPRACompilerParseException;

  /**
   * Parse a size expression.
   *
   * @param expr The input expression
   *
   * @return A type expression
   *
   * @throws JPRACompilerParseException On parse errors
   */

  SizeExprType<Unresolved, Untyped> parseSizeExpression(
    SExpressionType expr)
    throws JPRACompilerParseException;

  /**
   * EOF has been encountered.
   *
   * @param lex Lexical information, if any
   * @throws JPRACompilerParseException On parse errors
   */

  void parseEOF(
    Optional<LexicalPosition<Path>> lex)
    throws JPRACompilerParseException;
}
