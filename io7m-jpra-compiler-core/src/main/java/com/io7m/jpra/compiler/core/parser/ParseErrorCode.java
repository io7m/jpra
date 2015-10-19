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

public enum ParseErrorCode
{
  EXPECTED_LIST_GOT_QUOTED_STRING,
  EXPECTED_LIST_GOT_SYMBOL, EXPECTED_SYMBOL_GOT_LIST,
  EXPECTED_SYMBOL_GOT_QUOTED_STRING, UNRECOGNIZED_KEYWORD,
  EXPECTED_NON_EMPTY_LIST, BAD_PACKAGE_NAME, SYNTAX_ERROR,
  UNRECOGNIZED_TYPE_KEYWORD, UNRECOGNIZED_INTEGER_TYPE_KEYWORD,
  EXPECTED_SYMBOL_OR_LIST_GOT_QUOTED_STRING, INVALID_INTEGER_CONSTANT,
  UNRECOGNIZED_SIZE_FUNCTION, SEMANTIC_ERROR, BAD_FIELD_NAME,
  DUPLICATE_FIELD_NAME, BAD_TYPE_NAME, UNRECOGNIZED_RECORD_FIELD_KEYWORD,
  BAD_TYPE_REFERENCE,
}
