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

import com.io7m.jnull.NullCheck;
import com.io7m.jpra.compiler.core.CompilerException;
import com.io7m.jsx.ListType;
import com.io7m.jsx.QuotedStringType;
import com.io7m.jsx.SExpressionType;
import com.io7m.jsx.SymbolType;
import com.io7m.jsx.lexer.Position;
import org.valid4j.Assertive;

import java.nio.file.Path;
import java.util.Optional;

/**
 * The type of exceptions raised by the parser.
 */

public final class CompilerParseException extends CompilerException
{
  private final ParseErrorCode code;

  private CompilerParseException(
    final Optional<Path> file,
    final Position position,
    final ParseErrorCode in_code,
    final String message)
  {
    super(file, position, message);
    this.code = NullCheck.notNull(in_code);
  }

  /**
   * @param e The expression
   *
   * @return A parser exception
   *
   * @see ParseErrorCode#EXPECTED_LIST_GOT_QUOTED_STRING
   */

  public static CompilerParseException expectedListGotQuotedString(
    final QuotedStringType e)
  {
    final StringBuilder mb = new StringBuilder(256);
    mb.append("Expected: A list");
    mb.append(System.lineSeparator());
    mb.append("Got: A quoted string '");
    mb.append(e.getText());
    mb.append("'");
    final String m = NullCheck.notNull(mb.toString());
    return new CompilerParseException(
      e.getFile(),
      e.getPosition(),
      ParseErrorCode.EXPECTED_LIST_GOT_QUOTED_STRING,
      m);
  }

  /**
   * @param e The expression
   *
   * @return A parser exception
   *
   * @see ParseErrorCode#EXPECTED_LIST_GOT_SYMBOL
   */

  public static CompilerParseException expectedListGotSymbol(
    final SymbolType e)
  {
    final StringBuilder mb = new StringBuilder(256);
    mb.append("Expected: A list");
    mb.append(System.lineSeparator());
    mb.append("Got: A symbol '");
    mb.append(e.getText());
    mb.append("'");
    final String m = NullCheck.notNull(mb.toString());
    return new CompilerParseException(
      e.getFile(), e.getPosition(), ParseErrorCode.EXPECTED_LIST_GOT_SYMBOL, m);
  }

  /**
   * @param e The expression
   *
   * @return A parser exception
   *
   * @see ParseErrorCode#EXPECTED_SYMBOL_GOT_LIST
   */

  public static CompilerParseException expectedSymbolGotList(final ListType e)
  {
    final StringBuilder mb = new StringBuilder(256);
    mb.append("Expected: A symbol");
    mb.append(System.lineSeparator());
    mb.append("Got: A list");
    final String m = NullCheck.notNull(mb.toString());
    return new CompilerParseException(
      e.getFile(), e.getPosition(), ParseErrorCode.EXPECTED_SYMBOL_GOT_LIST, m);
  }

  /**
   * @param e The expression
   *
   * @return A parser exception
   *
   * @see ParseErrorCode#EXPECTED_SYMBOL_GOT_QUOTED_STRING
   */

  public static CompilerParseException expectedSymbolGotQuotedString(
    final QuotedStringType e)
  {
    final StringBuilder mb = new StringBuilder(256);
    mb.append("Expected: A symbol");
    mb.append(System.lineSeparator());
    mb.append("Got: A quoted string '");
    mb.append(e.getText());
    mb.append("'");
    final String m = NullCheck.notNull(mb.toString());
    return new CompilerParseException(
      e.getFile(),
      e.getPosition(),
      ParseErrorCode.EXPECTED_SYMBOL_GOT_QUOTED_STRING,
      m);
  }

  /**
   * @param e The expression
   * @param s The exception message
   *
   * @return A parser exception
   *
   * @see ParseErrorCode#UNRECOGNIZED_KEYWORD
   */

  public static CompilerParseException unrecognizedKeyword(
    final SymbolType e,
    final String s)
  {
    return new CompilerParseException(
      e.getFile(), e.getPosition(), ParseErrorCode.UNRECOGNIZED_KEYWORD, s);
  }

  /**
   * @param e The expression
   *
   * @return A parser exception
   *
   * @see ParseErrorCode#EXPECTED_NON_EMPTY_LIST
   */

  public static CompilerParseException expectedNonEmptyList(
    final ListType e)
  {
    Assertive.require(e.isEmpty());

    final StringBuilder mb = new StringBuilder(256);
    mb.append("Expected: A non-empty list");
    mb.append(System.lineSeparator());
    mb.append("Got: An empty list");
    final String m = NullCheck.notNull(mb.toString());
    return new CompilerParseException(
      e.getFile(), e.getPosition(), ParseErrorCode.EXPECTED_NON_EMPTY_LIST, m);
  }

  /**
   * @param e       The expression
   * @param message The exception message
   *
   * @return A parser exception
   *
   * @see ParseErrorCode#BAD_PACKAGE_NAME
   */

  public static CompilerParseException badPackageName(
    final SymbolType e,
    final String message)
  {
    return new CompilerParseException(
      e.getFile(), e.getPosition(), ParseErrorCode.BAD_PACKAGE_NAME, message);
  }

  /**
   * @param e The expression
   * @param s The exception message
   *
   * @return A parser exception
   *
   * @see ParseErrorCode#SYNTAX_ERROR
   */

  public static CompilerParseException syntaxError(
    final SExpressionType e,
    final String s)
  {
    return new CompilerParseException(
      e.getFile(), e.getPosition(), ParseErrorCode.SYNTAX_ERROR, s);
  }

  /**
   * @param e The expression
   * @param s The exception message
   *
   * @return A parser exception
   *
   * @see ParseErrorCode#UNRECOGNIZED_TYPE_KEYWORD
   */

  public static CompilerParseException unrecognizedTypeKeyword(
    final SymbolType e,
    final String s)
  {
    return new CompilerParseException(
      e.getFile(),
      e.getPosition(),
      ParseErrorCode.UNRECOGNIZED_TYPE_KEYWORD,
      s);
  }

  /**
   * @param e The expression
   * @param s The exception message
   *
   * @return A parser exception
   *
   * @see ParseErrorCode#UNRECOGNIZED_INTEGER_TYPE_KEYWORD
   */

  public static CompilerParseException unrecognizedIntegerTypeKeyword(
    final SymbolType e,
    final String s)
  {
    return new CompilerParseException(
      e.getFile(),
      e.getPosition(),
      ParseErrorCode.UNRECOGNIZED_INTEGER_TYPE_KEYWORD,
      s);
  }

  /**
   * @param e The expression
   *
   * @return A parser exception
   *
   * @see ParseErrorCode#EXPECTED_SYMBOL_OR_LIST_GOT_QUOTED_STRING
   */

  public static CompilerParseException expectedSymbolOrListGotQuotedString(
    final QuotedStringType e)
  {
    final StringBuilder mb = new StringBuilder(256);
    mb.append("Expected: A list or a symbol");
    mb.append(System.lineSeparator());
    mb.append("Got: A quoted string '");
    mb.append(e.getText());
    mb.append("'");
    final String m = NullCheck.notNull(mb.toString());
    return new CompilerParseException(
      e.getFile(),
      e.getPosition(),
      ParseErrorCode.EXPECTED_SYMBOL_OR_LIST_GOT_QUOTED_STRING,
      m);
  }

  /**
   * @param e The expression
   *
   * @return A parser exception
   *
   * @see ParseErrorCode#INVALID_INTEGER_CONSTANT
   */

  public static CompilerParseException invalidIntegerConstant(
    final SymbolType e)
  {
    final StringBuilder mb = new StringBuilder(256);
    mb.append("Expected: An integer constant");
    mb.append(System.lineSeparator());
    mb.append("Got: A symbol '");
    mb.append(e.getText());
    mb.append("'");
    final String m = NullCheck.notNull(mb.toString());
    return new CompilerParseException(
      e.getFile(), e.getPosition(), ParseErrorCode.INVALID_INTEGER_CONSTANT, m);
  }

  /**
   * @param e The expression
   * @param s The exception message
   *
   * @return A parser exception
   *
   * @see ParseErrorCode#UNRECOGNIZED_SIZE_FUNCTION
   */

  public static CompilerParseException unrecognizedSizeFunction(
    final SymbolType e,
    final String s)
  {
    return new CompilerParseException(
      e.getFile(),
      e.getPosition(),
      ParseErrorCode.UNRECOGNIZED_SIZE_FUNCTION,
      s);
  }

  /**
   * @param e       The expression
   * @param message The exception message
   *
   * @return A parser exception
   *
   * @see ParseErrorCode#BAD_FIELD_NAME
   */

  public static CompilerParseException badFieldName(
    final SymbolType e,
    final String message)
  {
    return new CompilerParseException(
      e.getFile(), e.getPosition(), ParseErrorCode.BAD_FIELD_NAME, message);
  }

  /**
   * @param e       The expression
   * @param message The exception message
   *
   * @return A parser exception
   *
   * @see ParseErrorCode#DUPLICATE_FIELD_NAME
   */

  public static CompilerParseException duplicateFieldName(
    final SymbolType e,
    final String message)
  {
    return new CompilerParseException(
      e.getFile(),
      e.getPosition(),
      ParseErrorCode.DUPLICATE_FIELD_NAME,
      message);
  }

  /**
   * @param e       The expression
   * @param message The exception message
   *
   * @return A parser exception
   *
   * @see ParseErrorCode#BAD_TYPE_NAME
   */

  public static CompilerParseException badTypeName(
    final SymbolType e,
    final String message)
  {
    return new CompilerParseException(
      e.getFile(), e.getPosition(), ParseErrorCode.BAD_TYPE_NAME, message);
  }

  /**
   * @param e       The expression
   * @param message The exception message
   *
   * @return A parser exception
   *
   * @see ParseErrorCode#UNRECOGNIZED_RECORD_FIELD_KEYWORD
   */

  public static CompilerParseException unrecognizedRecordFieldKeyword(
    final SymbolType e,
    final String message)
  {
    return new CompilerParseException(
      e.getFile(),
      e.getPosition(),
      ParseErrorCode.UNRECOGNIZED_RECORD_FIELD_KEYWORD,
      message);
  }

  /**
   * @param e The expression
   * @param s The exception message
   *
   * @return A parser exception
   *
   * @see ParseErrorCode#BAD_TYPE_REFERENCE
   */

  public static CompilerParseException badTypeReference(
    final SymbolType e,
    final String s)
  {
    return new CompilerParseException(
      e.getFile(), e.getPosition(), ParseErrorCode.BAD_TYPE_REFERENCE, s);
  }

  /**
   * @param e The expression
   * @param s The exception message
   *
   * @return A parser exception
   *
   * @see ParseErrorCode#EXPECTED_SCALAR_TYPE_EXPRESSION
   */

  public static CompilerParseException expectedScalarTypeExpression(
    final SExpressionType e,
    final String s)
  {
    return new CompilerParseException(
      e.getFile(),
      e.getPosition(),
      ParseErrorCode.EXPECTED_SCALAR_TYPE_EXPRESSION,
      s);
  }

  /**
   * @return The parser error code
   */

  public ParseErrorCode getErrorCode()
  {
    return this.code;
  }
}
