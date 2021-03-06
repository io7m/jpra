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

import com.io7m.jpra.model.names.FieldReference;
import com.io7m.jpra.model.names.TypeReference;
import com.io7m.jsx.SExpressionSymbolType;

/**
 * The type of name reference parsers.
 */

public interface JPRAReferenceParserType
{
  /**
   * Parse a type reference.
   *
   * @param se The input symbol
   *
   * @return A type reference
   *
   * @throws JPRACompilerParseException On parse errors
   */

  TypeReference parseTypeReference(
    SExpressionSymbolType se)
    throws JPRACompilerParseException;

  /**
   * Parse a field reference.
   *
   * @param se The input symbol
   *
   * @return A field reference
   *
   * @throws JPRACompilerParseException On parse errors
   */

  FieldReference parseFieldReference(
    SExpressionSymbolType se)
    throws JPRACompilerParseException;
}
