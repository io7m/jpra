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
import com.io7m.jpra.model.contexts.PackageContextType;
import com.io7m.jsx.SExpressionType;

import java.nio.file.Path;
import java.util.Optional;

/**
 * A pipeline that attaches together a parser, resolver, and type-checker.
 *
 * Typically, one pipeline is instantiated per compiled file.
 */

public interface JPRAPipelineType
{
  /**
   * An expression was received.
   *
   * @param e The expression
   *
   * @return A completed package, if the expression resulted in one
   *
   * @throws JPRACompilerException If any of the stages raise an exception
   */

  Optional<PackageContextType> onExpression(SExpressionType e)
    throws JPRACompilerException;

  /**
   * EOF was received.
   *
   * @param lex The last known lexical position, if any
   *
   * @throws JPRACompilerException If any of the stages raise an exception
   */

  void onEOF(Optional<LexicalPosition<Path>> lex)
    throws JPRACompilerException;
}
