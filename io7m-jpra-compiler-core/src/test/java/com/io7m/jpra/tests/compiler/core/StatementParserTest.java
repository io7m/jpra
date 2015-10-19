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

package com.io7m.jpra.tests.compiler.core;

import com.io7m.jeucreader.UnicodeCharacterReader;
import com.io7m.jeucreader.UnicodeCharacterReaderPushBackType;
import com.io7m.jpra.compiler.core.parser.StatementParser;
import com.io7m.jpra.compiler.core.parser.StatementParserType;
import com.io7m.jsx.lexer.Lexer;
import com.io7m.jsx.lexer.LexerConfiguration;
import com.io7m.jsx.lexer.LexerConfigurationBuilderType;
import com.io7m.jsx.lexer.LexerType;
import com.io7m.jsx.parser.Parser;
import com.io7m.jsx.parser.ParserConfiguration;
import com.io7m.jsx.parser.ParserConfigurationBuilderType;
import com.io7m.jsx.parser.ParserType;

import java.io.InputStream;
import java.io.InputStreamReader;

public final class StatementParserTest
  extends StatementParserContract<StatementParserType>
{
  @Override protected ParserType newSExpressionParser(final String name)
  {
    final InputStream s = StatementParserTest.class.getResourceAsStream(name);
    final InputStreamReader ir = new InputStreamReader(s);
    final UnicodeCharacterReaderPushBackType r =
      UnicodeCharacterReader.newReader(ir);

    final LexerConfigurationBuilderType lc = LexerConfiguration.newBuilder();
    lc.setNewlinesInQuotedStrings(false);
    lc.setSquareBrackets(true);

    final LexerType lex = Lexer.newLexer(lc.build(), r);
    final ParserConfigurationBuilderType pc = ParserConfiguration.newBuilder();
    pc.preserveLexicalInformation(true);

    return Parser.newParser(pc.build(), lex);
  }

  @Override protected StatementParserType newParser()
  {
    return StatementParser.newParser();
  }
}
