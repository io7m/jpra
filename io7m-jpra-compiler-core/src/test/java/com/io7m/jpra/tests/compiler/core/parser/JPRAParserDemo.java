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
import com.io7m.jlexing.core.ImmutableLexicalPositionType;
import com.io7m.jpra.compiler.core.JPRACompilerException;
import com.io7m.jpra.compiler.core.parser.JPRAAbstractParserEventListener;
import com.io7m.jpra.compiler.core.parser.JPRACompilerParseException;
import com.io7m.jpra.compiler.core.parser.JPRAParser;
import com.io7m.jpra.compiler.core.parser.JPRAParserType;
import com.io7m.jsx.SExpressionType;
import com.io7m.jsx.lexer.JSXLexer;
import com.io7m.jsx.lexer.JSXLexerConfiguration;
import com.io7m.jsx.lexer.JSXLexerConfigurationBuilderType;
import com.io7m.jsx.lexer.JSXLexerType;
import com.io7m.jsx.parser.JSXParser;
import com.io7m.jsx.parser.JSXParserConfiguration;
import com.io7m.jsx.parser.JSXParserConfigurationBuilderType;
import com.io7m.jsx.parser.JSXParserException;
import com.io7m.jsx.parser.JSXParserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Optional;

final class JPRAParserDemo
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(JPRAParserDemo.class);
  }

  private JPRAParserDemo()
  {

  }

  public static void main(final String[] args)
    throws IOException, JSXParserException, JPRACompilerParseException
  {
    try (InputStreamReader ir = new InputStreamReader(System.in)) {
      final UnicodeCharacterReaderPushBackType r =
        UnicodeCharacterReader.newReader(ir);

      final JSXLexerConfigurationBuilderType lc =
        JSXLexerConfiguration.newBuilder();
      lc.setNewlinesInQuotedStrings(false);
      lc.setSquareBrackets(true);

      final JSXLexerType lex = JSXLexer.newLexer(lc.build(), r);
      final JSXParserConfigurationBuilderType pc =
        JSXParserConfiguration.newBuilder();
      pc.preserveLexicalInformation(true);

      final JSXParserType p = JSXParser.newParser(pc.build(), lex);
      final JPRAParserType sp = JPRAParser.newParser();

      while (true) {
        try {
          final Optional<SExpressionType> s = p.parseExpressionOrEOF();
          if (s.isPresent()) {
            final SExpressionType ex = s.get();
            sp.parseStatement(
              ex, new JPRAAbstractParserEventListener()
              {
              });
          } else {
            break;
          }
        } catch (final JPRACompilerException e) {
          final Optional<ImmutableLexicalPositionType<Path>> lex_opt =
            e.getLexicalInformation();
          if (lex_opt.isPresent()) {
            final ImmutableLexicalPositionType<Path> ilex = lex_opt.get();
            JPRAParserDemo.LOG.error("{}: {}", ilex, e.getMessage());
          } else {
            JPRAParserDemo.LOG.error("{}", e.getMessage());
          }
        }
      }
    }
  }
}
