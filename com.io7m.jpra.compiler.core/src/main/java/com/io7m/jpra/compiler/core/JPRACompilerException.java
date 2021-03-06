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

package com.io7m.jpra.compiler.core;

import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jlexing.core.LexicalType;
import com.io7m.jpra.core.JPRAException;

import java.net.URI;
import java.util.Objects;

/**
 * The root type of compiler exceptions.
 */

public abstract class JPRACompilerException
  extends JPRAException implements LexicalType<URI>
{
  private final LexicalPosition<URI> lex;

  /**
   * Construct an exception.
   *
   * @param in_lex  Lexical information
   * @param message The exception message
   */

  public JPRACompilerException(
    final LexicalPosition<URI> in_lex,
    final String message)
  {
    super(Objects.requireNonNull(message, "Message"));
    this.lex = Objects.requireNonNull(in_lex, "Lexical information");
  }

  /**
   * Construct an exception.
   *
   * @param in_lex Lexical information
   * @param cause  The cause
   */

  public JPRACompilerException(
    final LexicalPosition<URI> in_lex,
    final Exception cause)
  {
    super(Objects.requireNonNull(cause, "Cause"));
    this.lex = Objects.requireNonNull(in_lex, "Lexical information");
  }

  @Override
  public final LexicalPosition<URI> lexical()
  {
    return this.lex;
  }
}
