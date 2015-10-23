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

import com.io7m.jlexing.core.ImmutableLexicalPositionType;
import com.io7m.jpra.compiler.core.JPRACompilerException;
import com.io7m.jsx.SExpressionType;

import java.nio.file.Path;
import java.util.Optional;

/**
 * The type of statement parsers.
 */

public interface JPRAParserType
{
  /**
   * Parse a statement.
   *
   * @param e        A raw s-expression
   * @param listener A listener that will receive the results of parsing
   *
   * @throws JPRACompilerParseException On parse errors
   * @throws JPRACompilerException      Propagated from {@code listener}
   */

  void parseStatement(
    SExpressionType e,
    JPRAParserREPLEventListenerType listener)
    throws JPRACompilerException, JPRACompilerParseException;

  /**
   * @return The current parsing position, if the underlying lexer is providing
   * this information.
   */

  Optional<ImmutableLexicalPositionType<Path>> getParsingPosition();
}
