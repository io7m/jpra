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

/**
 * The set of parser error codes.
 */

public enum JPRAParseErrorCode
{
  /**
   * A quoted string was specified where a list was expected.
   */

  EXPECTED_LIST_GOT_QUOTED_STRING,

  /**
   * A symbol was specified where a list was expected.
   */

  EXPECTED_LIST_GOT_SYMBOL,

  /**
   * A list was specified where a symbol was expected.
   */

  EXPECTED_SYMBOL_GOT_LIST,

  /**
   * A quoted string was specified where a symbol was expected.
   */

  EXPECTED_SYMBOL_GOT_QUOTED_STRING,

  /**
   * An unrecognized keyword was specified.
   */

  UNRECOGNIZED_KEYWORD,

  /**
   * An empty list was specified where a non-empty list was required.
   */

  EXPECTED_NON_EMPTY_LIST,

  /**
   * An invalid package name was specified.
   */

  BAD_PACKAGE_NAME,

  /**
   * A syntax error for a specific expression was encountered.
   */

  SYNTAX_ERROR,

  /**
   * An unrecognized type keyword was specified.
   */

  UNRECOGNIZED_TYPE_KEYWORD,

  /**
   * An unrecognized integer type keyword was encountered.
   */

  UNRECOGNIZED_INTEGER_TYPE_KEYWORD,

  /**
   * A symbol or list was expected, but a quoted string was specified.
   */

  EXPECTED_SYMBOL_OR_LIST_GOT_QUOTED_STRING,

  /**
   * A malformed integer constant was specified.
   */

  INVALID_INTEGER_CONSTANT,

  /**
   * An unrecognized size function was specified.
   */

  UNRECOGNIZED_SIZE_FUNCTION,

  /**
   * An invalid field name was specified.
   */

  BAD_FIELD_NAME,

  /**
   * A type declaration contained a duplicate field.
   */

  DUPLICATE_FIELD_NAME,

  /**
   * An invalid type name was specified.
   */

  BAD_TYPE_NAME,

  /**
   * An unrecognized record field keyword was specified.
   */

  UNRECOGNIZED_RECORD_FIELD_KEYWORD,

  /**
   * An invalid type reference was specified.
   */

  BAD_TYPE_REFERENCE,

  /**
   * Expected a scalar type expression, but got something else.
   */

  EXPECTED_SCALAR_TYPE_EXPRESSION,
}
