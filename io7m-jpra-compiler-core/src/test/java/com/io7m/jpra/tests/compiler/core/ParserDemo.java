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
import com.io7m.jpra.compiler.core.CompilerException;
import com.io7m.jpra.compiler.core.LexicalContextType;
import com.io7m.jpra.compiler.core.parser.CompilerParseException;
import com.io7m.jpra.compiler.core.parser.StatementParser;
import com.io7m.jpra.compiler.core.parser.StatementParserREPLEventListenerType;
import com.io7m.jpra.compiler.core.parser.StatementParserType;
import com.io7m.jpra.model.PackageNameQualified;
import com.io7m.jpra.model.PackageNameUnqualified;
import com.io7m.jpra.model.SizeExprType;
import com.io7m.jpra.model.TypeDeclRecord;
import com.io7m.jpra.model.TypeExprType;
import com.io7m.jsx.SExpressionType;
import com.io7m.jsx.lexer.Lexer;
import com.io7m.jsx.lexer.LexerConfiguration;
import com.io7m.jsx.lexer.LexerConfigurationBuilderType;
import com.io7m.jsx.lexer.LexerType;
import com.io7m.jsx.lexer.Position;
import com.io7m.jsx.parser.Parser;
import com.io7m.jsx.parser.ParserConfiguration;
import com.io7m.jsx.parser.ParserConfigurationBuilderType;
import com.io7m.jsx.parser.ParserException;
import com.io7m.jsx.parser.ParserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Optional;

final class ParserDemo
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(ParserDemo.class);
  }

  private ParserDemo()
  {

  }

  public static void main(final String[] args)
    throws IOException, ParserException, CompilerParseException
  {
    try (InputStreamReader ir = new InputStreamReader(System.in)) {
      final UnicodeCharacterReaderPushBackType r =
        UnicodeCharacterReader.newReader(ir);

      final LexerConfigurationBuilderType lc = LexerConfiguration.newBuilder();
      lc.setNewlinesInQuotedStrings(false);
      lc.setSquareBrackets(true);

      final LexerType lex = Lexer.newLexer(lc.build(), r);
      final ParserConfigurationBuilderType pc =
        ParserConfiguration.newBuilder();
      pc.preserveLexicalInformation(true);

      final ParserType p = Parser.newParser(pc.build(), lex);
      final StatementParserType sp = StatementParser.newParser();

      while (true) {
        try {
          final Optional<SExpressionType> s = p.parseExpressionOrEOF();
          if (s.isPresent()) {
            final SExpressionType ex = s.get();
            sp.parseStatement(
              ex, new StatementParserREPLEventListenerType()
              {
                @Override public void onREPLType(
                  final LexicalContextType context,
                  final TypeExprType t)
                {
                  ParserDemo.LOG.debug("onREPLType: {}", t);
                }

                @Override public void onREPLSize(
                  final LexicalContextType context,
                  final SizeExprType<?> t)
                {
                  ParserDemo.LOG.debug("onREPLSize: {}", t);
                }

                @Override public void onPackageBegin(
                  final LexicalContextType context,
                  final PackageNameQualified name)
                  throws CompilerParseException
                {
                  ParserDemo.LOG.debug("onPackageBegin: {}", name);
                }

                @Override public void onImport(
                  final LexicalContextType context,
                  final PackageNameQualified p_name,
                  final PackageNameUnqualified up_name)
                  throws CompilerParseException
                {
                  ParserDemo.LOG.debug("onImport: {} as {}", p_name, up_name);
                }

                @Override
                public void onPackageEnd(final LexicalContextType context)
                  throws CompilerParseException
                {
                  ParserDemo.LOG.debug("onPackageEnd");
                }

                @Override public void onRecord(
                  final LexicalContextType context,
                  final TypeDeclRecord r)
                  throws CompilerParseException
                {
                  ParserDemo.LOG.debug("onRecord: {}", r);
                }
              });
          } else {
            break;
          }
        } catch (final CompilerException e) {
          final Optional<Path> file = e.getFile();
          final Position position = e.getPosition();
          if (file.isPresent()) {
            ParserDemo.LOG.error(
              "{}:{}:{}: {}",
              file.get(),
              Integer.valueOf(position.getLine()),
              Integer.valueOf(position.getColumn()),
              e.getMessage());
          } else {
            ParserDemo.LOG.error(
              "<stdin>:{}:{}: {}",
              Integer.valueOf(position.getLine()),
              Integer.valueOf(position.getColumn()),
              e.getMessage());
          }
        }
      }
    }
  }
}
