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
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.compiler.core.JPRACompilerException;
import com.io7m.jsx.SExpressionListType;
import com.io7m.jsx.SExpressionQuotedStringType;
import com.io7m.jsx.SExpressionSymbolType;
import com.io7m.jsx.SExpressionType;
import org.valid4j.Assertive;

import java.nio.file.Path;
import java.util.Optional;

/**
 * The type of exceptions raised by the parser.
 */

public final class JPRACompilerParseException extends JPRACompilerException
{
  private final JPRAParseErrorCode code;

  private JPRACompilerParseException(
    final Optional<LexicalPosition<Path>> in_lex,
    final JPRAParseErrorCode in_code,
    final String message)
  {
    super(in_lex, message);
    this.code = NullCheck.notNull(in_code);
  }

  /**
   * @param e The expression
   *
   * @return A parser exception
   *
   * @see JPRAParseErrorCode#EXPECTED_LIST_GOT_QUOTED_STRING
   */

  public static JPRACompilerParseException expectedListGotQuotedString(
    final SExpressionQuotedStringType e)
  {
    final StringBuilder mb = new StringBuilder(256);
    mb.append("  Expected: A list");
    mb.append(System.lineSeparator());
    mb.append("  Got: A quoted string '");
    mb.append(e.text());
    mb.append("'");
    final String m = NullCheck.notNull(mb.toString());
    return new JPRACompilerParseException(
      e.lexical().map(LexicalPosition::copyOf),
      JPRAParseErrorCode.EXPECTED_LIST_GOT_QUOTED_STRING,
      m);
  }

  /**
   * @param e The expression
   *
   * @return A parser exception
   *
   * @see JPRAParseErrorCode#EXPECTED_LIST_GOT_SYMBOL
   */

  public static JPRACompilerParseException expectedListGotSymbol(
    final SExpressionSymbolType e)
  {
    final StringBuilder mb = new StringBuilder(256);
    mb.append("  Expected: A list");
    mb.append(System.lineSeparator());
    mb.append("  Got: A symbol '");
    mb.append(e.text());
    mb.append("'");
    final String m = NullCheck.notNull(mb.toString());
    return new JPRACompilerParseException(
      e.lexical().map(LexicalPosition::copyOf),
      JPRAParseErrorCode.EXPECTED_LIST_GOT_SYMBOL,
      m);
  }

  /**
   * @param e The expression
   *
   * @return A parser exception
   *
   * @see JPRAParseErrorCode#EXPECTED_SYMBOL_GOT_LIST
   */

  public static JPRACompilerParseException expectedSymbolGotList(
    final SExpressionListType e)
  {
    final StringBuilder mb = new StringBuilder(256);
    mb.append("  Expected: A symbol");
    mb.append(System.lineSeparator());
    mb.append("  Got: A list");
    final String m = NullCheck.notNull(mb.toString());
    return new JPRACompilerParseException(
      e.lexical().map(LexicalPosition::copyOf),
      JPRAParseErrorCode.EXPECTED_SYMBOL_GOT_LIST,
      m);
  }

  /**
   * @param e The expression
   *
   * @return A parser exception
   *
   * @see JPRAParseErrorCode#EXPECTED_SYMBOL_GOT_QUOTED_STRING
   */

  public static JPRACompilerParseException expectedSymbolGotQuotedString(
    final SExpressionQuotedStringType e)
  {
    final StringBuilder mb = new StringBuilder(256);
    mb.append("  Expected: A symbol");
    mb.append(System.lineSeparator());
    mb.append("  Got: A quoted string '");
    mb.append(e.text());
    mb.append("'");
    final String m = NullCheck.notNull(mb.toString());
    return new JPRACompilerParseException(
      e.lexical().map(LexicalPosition::copyOf),
      JPRAParseErrorCode.EXPECTED_SYMBOL_GOT_QUOTED_STRING,
      m);
  }

  /**
   * @param e The expression
   * @param s The exception message
   *
   * @return A parser exception
   *
   * @see JPRAParseErrorCode#UNRECOGNIZED_KEYWORD
   */

  public static JPRACompilerParseException unrecognizedKeyword(
    final SExpressionSymbolType e,
    final String s)
  {
    return new JPRACompilerParseException(
      e.lexical().map(LexicalPosition::copyOf),
      JPRAParseErrorCode.UNRECOGNIZED_KEYWORD,
      s);
  }

  /**
   * @param e The expression
   *
   * @return A parser exception
   *
   * @see JPRAParseErrorCode#EXPECTED_NON_EMPTY_LIST
   */

  public static JPRACompilerParseException expectedNonEmptyList(
    final SExpressionListType e)
  {
    Assertive.require(e.size() == 0);

    final StringBuilder mb = new StringBuilder(256);
    mb.append("  Expected: A non-empty list");
    mb.append(System.lineSeparator());
    mb.append("  Got: An empty list");
    final String m = NullCheck.notNull(mb.toString());
    return new JPRACompilerParseException(
      e.lexical().map(LexicalPosition::copyOf),
      JPRAParseErrorCode.EXPECTED_NON_EMPTY_LIST,
      m);
  }

  /**
   * @param e       The expression
   * @param message The exception message
   *
   * @return A parser exception
   *
   * @see JPRAParseErrorCode#BAD_PACKAGE_NAME
   */

  public static JPRACompilerParseException badPackageName(
    final SExpressionSymbolType e,
    final String message)
  {
    return new JPRACompilerParseException(
      e.lexical().map(LexicalPosition::copyOf),
      JPRAParseErrorCode.BAD_PACKAGE_NAME,
      message);
  }

  /**
   * @param e The expression
   * @param s The exception message
   *
   * @return A parser exception
   *
   * @see JPRAParseErrorCode#SYNTAX_ERROR
   */

  public static JPRACompilerParseException syntaxError(
    final SExpressionType e,
    final String s)
  {
    return new JPRACompilerParseException(
      e.lexical().map(LexicalPosition::copyOf),
      JPRAParseErrorCode.SYNTAX_ERROR,
      s);
  }

  /**
   * @param e The expression
   * @param s The exception message
   *
   * @return A parser exception
   *
   * @see JPRAParseErrorCode#UNRECOGNIZED_TYPE_KEYWORD
   */

  public static JPRACompilerParseException unrecognizedTypeKeyword(
    final SExpressionSymbolType e,
    final String s)
  {
    return new JPRACompilerParseException(
      e.lexical().map(LexicalPosition::copyOf),
      JPRAParseErrorCode.UNRECOGNIZED_TYPE_KEYWORD,
      s);
  }

  /**
   * @param e The expression
   * @param s The exception message
   *
   * @return A parser exception
   *
   * @see JPRAParseErrorCode#UNRECOGNIZED_INTEGER_TYPE_KEYWORD
   */

  public static JPRACompilerParseException unrecognizedIntegerTypeKeyword(
    final SExpressionSymbolType e,
    final String s)
  {
    return new JPRACompilerParseException(
      e.lexical().map(LexicalPosition::copyOf),
      JPRAParseErrorCode.UNRECOGNIZED_INTEGER_TYPE_KEYWORD,
      s);
  }

  /**
   * @param e The expression
   *
   * @return A parser exception
   *
   * @see JPRAParseErrorCode#EXPECTED_SYMBOL_OR_LIST_GOT_QUOTED_STRING
   */

  public static JPRACompilerParseException expectedSymbolOrListGotQuotedString(
    final SExpressionQuotedStringType e)
  {
    final StringBuilder mb = new StringBuilder(256);
    mb.append("  Expected: A list or a symbol");
    mb.append(System.lineSeparator());
    mb.append("  Got: A quoted string '");
    mb.append(e.text());
    mb.append("'");
    final String m = NullCheck.notNull(mb.toString());
    return new JPRACompilerParseException(
      e.lexical().map(LexicalPosition::copyOf),
      JPRAParseErrorCode.EXPECTED_SYMBOL_OR_LIST_GOT_QUOTED_STRING,
      m);
  }

  /**
   * @param e The expression
   *
   * @return A parser exception
   *
   * @see JPRAParseErrorCode#INVALID_INTEGER_CONSTANT
   */

  public static JPRACompilerParseException invalidIntegerConstant(
    final SExpressionSymbolType e)
  {
    final StringBuilder mb = new StringBuilder(256);
    mb.append("  Expected: An integer constant");
    mb.append(System.lineSeparator());
    mb.append("  Got: A symbol '");
    mb.append(e.text());
    mb.append("'");
    final String m = NullCheck.notNull(mb.toString());
    return new JPRACompilerParseException(
      e.lexical().map(LexicalPosition::copyOf),
      JPRAParseErrorCode.INVALID_INTEGER_CONSTANT,
      m);
  }

  /**
   * @param e The expression
   * @param s The exception message
   *
   * @return A parser exception
   *
   * @see JPRAParseErrorCode#UNRECOGNIZED_SIZE_FUNCTION
   */

  public static JPRACompilerParseException unrecognizedSizeFunction(
    final SExpressionSymbolType e,
    final String s)
  {
    return new JPRACompilerParseException(
      e.lexical().map(LexicalPosition::copyOf),
      JPRAParseErrorCode.UNRECOGNIZED_SIZE_FUNCTION,
      s);
  }

  /**
   * @param e       The expression
   * @param message The exception message
   *
   * @return A parser exception
   *
   * @see JPRAParseErrorCode#BAD_FIELD_NAME
   */

  public static JPRACompilerParseException badFieldName(
    final SExpressionSymbolType e,
    final String message)
  {
    return new JPRACompilerParseException(
      e.lexical().map(LexicalPosition::copyOf),
      JPRAParseErrorCode.BAD_FIELD_NAME,
      message);
  }

  /**
   * @param e       The expression
   * @param message The exception message
   *
   * @return A parser exception
   *
   * @see JPRAParseErrorCode#DUPLICATE_FIELD_NAME
   */

  public static JPRACompilerParseException duplicateFieldName(
    final SExpressionSymbolType e,
    final String message)
  {
    return new JPRACompilerParseException(
      e.lexical().map(LexicalPosition::copyOf),
      JPRAParseErrorCode.DUPLICATE_FIELD_NAME,
      message);
  }

  /**
   * @param e       The expression
   * @param message The exception message
   *
   * @return A parser exception
   *
   * @see JPRAParseErrorCode#BAD_TYPE_NAME
   */

  public static JPRACompilerParseException badTypeName(
    final SExpressionSymbolType e,
    final String message)
  {
    return new JPRACompilerParseException(
      e.lexical().map(LexicalPosition::copyOf),
      JPRAParseErrorCode.BAD_TYPE_NAME,
      message);
  }

  /**
   * @param e       The expression
   * @param message The exception message
   *
   * @return A parser exception
   *
   * @see JPRAParseErrorCode#UNRECOGNIZED_RECORD_FIELD_KEYWORD
   */

  public static JPRACompilerParseException unrecognizedRecordFieldKeyword(
    final SExpressionSymbolType e,
    final String message)
  {
    return new JPRACompilerParseException(
      e.lexical().map(LexicalPosition::copyOf),
      JPRAParseErrorCode.UNRECOGNIZED_RECORD_FIELD_KEYWORD,
      message);
  }

  /**
   * @param e The expression
   * @param s The exception message
   *
   * @return A parser exception
   *
   * @see JPRAParseErrorCode#BAD_TYPE_REFERENCE
   */

  public static JPRACompilerParseException badTypeReference(
    final SExpressionSymbolType e,
    final String s)
  {
    return new JPRACompilerParseException(
      e.lexical().map(LexicalPosition::copyOf),
      JPRAParseErrorCode.BAD_TYPE_REFERENCE,
      s);
  }

  /**
   * @return The parser error code
   */

  public JPRAParseErrorCode getErrorCode()
  {
    return this.code;
  }

  /**
   * @param e       The expression
   * @param message The exception message
   *
   * @return A parser exception
   *
   * @see JPRAParseErrorCode#UNRECOGNIZED_PACKED_FIELD_KEYWORD
   */

  public static JPRACompilerParseException unrecognizedPackedFieldKeyword(
    final SExpressionSymbolType e,
    final String message)
  {
    return new JPRACompilerParseException(
      e.lexical().map(LexicalPosition::copyOf),
      JPRAParseErrorCode.UNRECOGNIZED_PACKED_FIELD_KEYWORD,
      message);
  }
}
