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

package com.io7m.jpra.tests.compiler.core.parser;

import com.io7m.jeucreader.UnicodeCharacterReader;
import com.io7m.jeucreader.UnicodeCharacterReaderPushBackType;
import com.io7m.jpra.compiler.core.parser.JPRAParser;
import com.io7m.jpra.compiler.core.parser.JPRAParserType;
import com.io7m.jpra.compiler.core.parser.JPRAReferenceParser;
import com.io7m.jsx.SExpressionType;
import com.io7m.jsx.api.lexer.JSXLexerConfiguration;
import com.io7m.jsx.api.lexer.JSXLexerType;
import com.io7m.jsx.api.parser.JSXParserConfiguration;
import com.io7m.jsx.api.parser.JSXParserException;
import com.io7m.jsx.api.parser.JSXParserType;
import com.io7m.jsx.api.serializer.JSXSerializerType;
import com.io7m.jsx.lexer.JSXLexer;
import com.io7m.jsx.parser.JSXParser;
import com.io7m.jsx.serializer.JSXSerializerTrivial;
import com.io7m.junreachable.UnreachableCodeException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class JPRAParserTest extends JPRAParserContract
{
  private static JSXParserType newJSXParser(final InputStream s)
  {
    final InputStreamReader ir = new InputStreamReader(s);
    final UnicodeCharacterReaderPushBackType r =
      UnicodeCharacterReader.newReader(ir);

    final JSXLexerConfiguration.Builder lc =
      JSXLexerConfiguration.builder();
    lc.setNewlinesInQuotedStrings(false);
    lc.setSquareBrackets(true);

    final JSXLexerType lex = JSXLexer.newLexer(lc.build(), r);
    final JSXParserConfiguration.Builder pc =
      JSXParserConfiguration.builder();
    pc.setPreserveLexical(true);

    return JSXParser.newParser(pc.build(), lex);
  }

  @Override
  protected JPRAParserType newParser()
  {
    final JSXSerializerType serial = JSXSerializerTrivial.newSerializer();
    return JPRAParser.newParser(serial, JPRAReferenceParser.newParser(serial));
  }

  @Override
  protected SExpressionType newFileSExpr(final String name)
  {
    try {
      final InputStream s = JPRAParserTest.class.getResourceAsStream(name);
      final JSXParserType p = newJSXParser(s);
      return p.parseExpression();
    } catch (final JSXParserException | IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  @Override
  protected SExpressionType newStringSExpr(final String expr)
  {
    try {
      final InputStream s = new ByteArrayInputStream(expr.getBytes("UTF-8"));
      final JSXParserType p = newJSXParser(s);
      return p.parseExpression();
    } catch (final JSXParserException | IOException e) {
      throw new UnreachableCodeException(e);
    }
  }
}
