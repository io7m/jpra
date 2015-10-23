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

package com.io7m.jpra.tests.compiler.core.resolver;

import com.io7m.jeucreader.UnicodeCharacterReader;
import com.io7m.jeucreader.UnicodeCharacterReaderPushBackType;
import com.io7m.jpra.compiler.core.JPRACompilerException;
import com.io7m.jpra.compiler.core.parser.JPRAParser;
import com.io7m.jpra.compiler.core.parser.JPRAParserType;
import com.io7m.jpra.compiler.core.resolver.JPRAResolverErrorCode;
import com.io7m.jpra.compiler.core.resolver.JPRAResolverEventListenerType;
import com.io7m.jpra.compiler.core.resolver.JPRAResolverType;
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
import com.io7m.junreachable.UnreachableCodeException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

public abstract class JPRAResolverContract<R extends JPRAResolverType>
{
  @Rule public ExpectedException expected = ExpectedException.none();

  protected abstract R newResolver(JPRAResolverEventListenerType e);

  @Test public final void testPackageBeginInvalid0()
    throws Exception
  {
    final R c = this.newResolver(new CheckedListener());

    this.expected.expect(
      new JPRACompilerResolverExceptionMatcher(
        JPRAResolverErrorCode.PACKAGE_NESTED));

    this.runResolver("t-package-begin-invalid-0.jpr", c);
  }

  @Test public final void testPackageBeginInvalid1()
    throws Exception
  {
    final R c = this.newResolver(new CheckedListener());

    this.expected.expect(
      new JPRACompilerResolverExceptionMatcher(
        JPRAResolverErrorCode.PACKAGE_DUPLICATE));

    this.runResolver("t-package-begin-invalid-1.jpr", c);
  }

  @Test public final void testPackageEndInvalid0()
    throws Exception
  {
    final R c = this.newResolver(new CheckedListener());

    this.expected.expect(
      new JPRACompilerResolverExceptionMatcher(
        JPRAResolverErrorCode.NO_CURRENT_PACKAGE));

    this.runResolver("t-package-end-invalid-0.jpr", c);
  }

  @Test public final void testPackageImportInvalid0()
    throws Exception
  {
    final R c = this.newResolver(new CheckedListener());

    this.expected.expect(
      new JPRACompilerResolverExceptionMatcher(
        JPRAResolverErrorCode.NO_CURRENT_PACKAGE));

    this.runResolver("t-package-import-invalid-0.jpr", c);
  }

  @Test public final void testPackageImportInvalid1()
    throws Exception
  {
    final R c = this.newResolver(new CheckedListener());

    this.expected.expect(
      new JPRACompilerResolverExceptionMatcher(
        JPRAResolverErrorCode.PACKAGE_NONEXISTENT));

    this.runResolver("t-package-import-invalid-1.jpr", c);
  }

  @Test public final void testPackageImportInvalid2()
    throws Exception
  {
    final R c = this.newResolver(new CheckedListener());

    this.expected.expect(
      new JPRACompilerResolverExceptionMatcher(
        JPRAResolverErrorCode.PACKAGE_IMPORT_CONFLICT));

    this.runResolver("t-package-import-invalid-2.jpr", c);
  }

  private void runResolver(
    final String file,
    final R resolver)
    throws JPRACompilerException
  {
    final InputStream s = JPRAResolverContract.class.getResourceAsStream(file);
    final InputStreamReader ir = new InputStreamReader(s);
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

    final JSXParserType jp = JSXParser.newParser(pc.build(), lex);
    final JPRAParserType sp = JPRAParser.newParser();

    while (true) {
      try {
        final Optional<SExpressionType> exp = jp.parseExpressionOrEOF();
        if (!exp.isPresent()) {
          return;
        }
        final SExpressionType e = exp.get();
        sp.parseStatement(e, resolver);
      } catch (final JSXParserException | IOException ex) {
        throw new UnreachableCodeException(ex);
      }
    }
  }

  @Test public final void testTypeRecordInvalid0()
    throws Exception
  {
    final R c = this.newResolver(new CheckedListener());

    this.expected.expect(
      new JPRACompilerResolverExceptionMatcher(
        JPRAResolverErrorCode.NO_CURRENT_PACKAGE));

    this.runResolver("t-type-record-invalid-0.jpr", c);
  }

  @Test public final void testTypeRecordInvalid1()
    throws Exception
  {
    final R c = this.newResolver(new CheckedListener());

    this.expected.expect(
      new JPRACompilerResolverExceptionMatcher(
        JPRAResolverErrorCode.TYPE_DUPLICATE));

    this.runResolver("t-type-record-invalid-1.jpr", c);
  }

  private final static class CheckedListener
    implements JPRAResolverEventListenerType
  {

  }
}
