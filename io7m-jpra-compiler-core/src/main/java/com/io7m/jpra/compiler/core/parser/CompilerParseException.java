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

  public static CompilerParseException expectedSymbolGotList(final ListType le)
  {
    final StringBuilder mb = new StringBuilder(256);
    mb.append("Expected: A symbol");
    mb.append(System.lineSeparator());
    mb.append("Got: A list");
    final String m = NullCheck.notNull(mb.toString());
    return new CompilerParseException(
      le.getFile(),
      le.getPosition(),
      ParseErrorCode.EXPECTED_SYMBOL_GOT_LIST,
      m);
  }

  public static CompilerParseException expectedSymbolGotQuotedString(
    final QuotedStringType qe)
  {
    final StringBuilder mb = new StringBuilder(256);
    mb.append("Expected: A symbol");
    mb.append(System.lineSeparator());
    mb.append("Got: A quoted string '");
    mb.append(qe.getText());
    mb.append("'");
    final String m = NullCheck.notNull(mb.toString());
    return new CompilerParseException(
      qe.getFile(),
      qe.getPosition(),
      ParseErrorCode.EXPECTED_SYMBOL_GOT_QUOTED_STRING,
      m);
  }

  public static CompilerParseException unrecognizedKeyword(
    final SymbolType se,
    final String s)
  {
    return new CompilerParseException(
      se.getFile(), se.getPosition(), ParseErrorCode.UNRECOGNIZED_KEYWORD, s);
  }

  public static CompilerParseException expectedNonEmptyList(
    final ListType le)
  {
    Assertive.require(le.isEmpty());

    final StringBuilder mb = new StringBuilder(256);
    mb.append("Expected: A non-empty list");
    mb.append(System.lineSeparator());
    mb.append("Got: An empty list");
    final String m = NullCheck.notNull(mb.toString());
    return new CompilerParseException(
      le.getFile(),
      le.getPosition(),
      ParseErrorCode.EXPECTED_NON_EMPTY_LIST,
      m);
  }

  public static CompilerParseException badPackageName(
    final SymbolType se,
    final String message)
  {
    return new CompilerParseException(
      se.getFile(), se.getPosition(), ParseErrorCode.BAD_PACKAGE_NAME, message);
  }

  public static CompilerParseException syntaxError(
    final SExpressionType le,
    final String s)
  {
    return new CompilerParseException(
      le.getFile(), le.getPosition(), ParseErrorCode.SYNTAX_ERROR, s);
  }

  public static CompilerParseException unrecognizedTypeKeyword(
    final SymbolType se,
    final String s)
  {
    return new CompilerParseException(
      se.getFile(),
      se.getPosition(),
      ParseErrorCode.UNRECOGNIZED_TYPE_KEYWORD,
      s);
  }

  public static CompilerParseException unrecognizedIntegerTypeKeyword(
    final SymbolType se,
    final String s)
  {
    return new CompilerParseException(
      se.getFile(),
      se.getPosition(),
      ParseErrorCode.UNRECOGNIZED_INTEGER_TYPE_KEYWORD,
      s);
  }

  public static CompilerParseException expectedSymbolOrListGotQuotedString(
    final QuotedStringType qe)
  {
    final StringBuilder mb = new StringBuilder(256);
    mb.append("Expected: A list or a symbol");
    mb.append(System.lineSeparator());
    mb.append("Got: A quoted string '");
    mb.append(qe.getText());
    mb.append("'");
    final String m = NullCheck.notNull(mb.toString());
    return new CompilerParseException(
      qe.getFile(),
      qe.getPosition(),
      ParseErrorCode.EXPECTED_SYMBOL_OR_LIST_GOT_QUOTED_STRING,
      m);
  }

  public static CompilerParseException invalidIntegerConstant(
    final SymbolType se)
  {
    final StringBuilder mb = new StringBuilder(256);
    mb.append("Expected: An integer constant");
    mb.append(System.lineSeparator());
    mb.append("Got: A symbol '");
    mb.append(se.getText());
    mb.append("'");
    final String m = NullCheck.notNull(mb.toString());
    return new CompilerParseException(
      se.getFile(),
      se.getPosition(),
      ParseErrorCode.INVALID_INTEGER_CONSTANT,
      m);
  }

  public static CompilerParseException unrecognizedSizeFunction(
    final SymbolType se,
    final String s)
  {
    return new CompilerParseException(
      se.getFile(),
      se.getPosition(),
      ParseErrorCode.UNRECOGNIZED_SIZE_FUNCTION,
      s);
  }

  public static CompilerParseException semanticError(
    final SExpressionType e,
    final String s)
  {
    return new CompilerParseException(
      e.getFile(), e.getPosition(), ParseErrorCode.SEMANTIC_ERROR, s);
  }

  public static CompilerParseException badFieldName(
    final SymbolType se,
    final String message)
  {
    return new CompilerParseException(
      se.getFile(), se.getPosition(), ParseErrorCode.BAD_FIELD_NAME, message);
  }

  public static CompilerParseException duplicateFieldName(
    final SymbolType se,
    final String message)
  {
    return new CompilerParseException(
      se.getFile(),
      se.getPosition(),
      ParseErrorCode.DUPLICATE_FIELD_NAME,
      message);
  }

  public static CompilerParseException badTypeName(
    final SymbolType se,
    final String message)
  {
    return new CompilerParseException(
      se.getFile(), se.getPosition(), ParseErrorCode.BAD_TYPE_NAME, message);
  }

  public static CompilerParseException unrecognizedRecordFieldKeyword(
    final SymbolType se,
    final String message)
  {
    return new CompilerParseException(
      se.getFile(),
      se.getPosition(),
      ParseErrorCode.UNRECOGNIZED_RECORD_FIELD_KEYWORD,
      message);
  }

  public static CompilerParseException badTypeReference(
    final SymbolType se,
    final String s)
  {
    return new CompilerParseException(
      se.getFile(), se.getPosition(), ParseErrorCode.BAD_TYPE_REFERENCE, s);
  }

  public ParseErrorCode getErrorCode()
  {
    return this.code;
  }
}
