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

import com.gs.collections.api.list.ImmutableList;
import com.io7m.jeucreader.UnicodeCharacterReader;
import com.io7m.jeucreader.UnicodeCharacterReaderPushBackType;
import com.io7m.jpra.compiler.core.parser.JPRAParseErrorCode;
import com.io7m.jpra.compiler.core.parser.JPRATypeReferenceNameParser;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.type_expressions.TypeExprNameF;
import com.io7m.jpra.model.type_expressions.TypeExprNamePT;
import com.io7m.jpra.model.type_expressions.TypeExprNamePTF;
import com.io7m.jpra.model.type_expressions.TypeExprNameT;
import com.io7m.jpra.model.type_expressions.TypeExprNameTF;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.jsx.SExpressionSymbolType;
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
import com.io7m.jsx.serializer.JSXSerializerTrivial;
import com.io7m.jsx.serializer.JSXSerializerType;
import com.io7m.junreachable.UnreachableCodeException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class JPRATypeReferenceNameParserTest
{
  @Rule public ExpectedException expected = ExpectedException.none();

  private JSXSerializerType re;

  private static SExpressionType parseExpression(final String exp)
  {
    try {
      final InputStream s = new ByteArrayInputStream(exp.getBytes("UTF-8"));
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
      return jp.parseExpression();
    } catch (final JSXParserException | IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  private static JSXSerializerType getSerializer()
  {
    return JSXSerializerTrivial.newSerializer();
  }

  @Test public void testValidT0()
    throws Exception
  {
    final TypeExprNameT e = (TypeExprNameT) JPRATypeReferenceNameParser.onName(
      JPRATypeReferenceNameParserTest.getSerializer(),
      (SExpressionSymbolType) JPRATypeReferenceNameParserTest.parseExpression(
        "T"));

    final TypeName type = e.getName();
    Assert.assertEquals("T", type.getValue());
  }

  @Test public void testValidF0()
    throws Exception
  {
    final TypeExprNameF e = (TypeExprNameF) JPRATypeReferenceNameParser.onName(
      JPRATypeReferenceNameParserTest.getSerializer(),
      (SExpressionSymbolType) JPRATypeReferenceNameParserTest.parseExpression(
        "f"));

    final ImmutableList<FieldName> path = e.getFieldPath();
    Assert.assertEquals(1L, (long) path.size());
    Assert.assertEquals("f", path.get(0).getValue());
  }

  @Test public void testValidF1()
    throws Exception
  {
    final TypeExprNameF e = (TypeExprNameF) JPRATypeReferenceNameParser.onName(
      JPRATypeReferenceNameParserTest.getSerializer(),
      (SExpressionSymbolType) JPRATypeReferenceNameParserTest.parseExpression(
        "f.g"));

    final ImmutableList<FieldName> path = e.getFieldPath();
    Assert.assertEquals(2L, (long) path.size());
    Assert.assertEquals("f", path.get(0).getValue());
    Assert.assertEquals("g", path.get(1).getValue());
  }

  @Test public void testValidTF0()
    throws Exception
  {
    final TypeExprNameTF e = (TypeExprNameTF) JPRATypeReferenceNameParser
      .onName(
        JPRATypeReferenceNameParserTest.getSerializer(),
        (SExpressionSymbolType) JPRATypeReferenceNameParserTest.parseExpression(
          "T.f.g"));

    Assert.assertEquals("T", e.getType().getValue());
    final ImmutableList<FieldName> path = e.getFieldPath();
    Assert.assertEquals(2L, (long) path.size());
    Assert.assertEquals("f", path.get(0).getValue());
    Assert.assertEquals("g", path.get(1).getValue());
  }

  @Test public void testInvalidF0()
    throws Exception
  {
    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.BAD_TYPE_REFERENCE));

    JPRATypeReferenceNameParser.onName(
      JPRATypeReferenceNameParserTest.getSerializer(),
      (SExpressionSymbolType) JPRATypeReferenceNameParserTest.parseExpression(
        "1"));
  }

  @Test public void testInvalidF1()
    throws Exception
  {
    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.BAD_TYPE_REFERENCE));

    JPRATypeReferenceNameParser.onName(
      JPRATypeReferenceNameParserTest.getSerializer(),
      (SExpressionSymbolType) JPRATypeReferenceNameParserTest.parseExpression(
        "x.T"));
  }

  @Test public void testInvalidPT0()
    throws Exception
  {
    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.BAD_TYPE_REFERENCE));

    JPRATypeReferenceNameParser.onName(
      JPRATypeReferenceNameParserTest.getSerializer(),
      (SExpressionSymbolType) JPRATypeReferenceNameParserTest.parseExpression(
        "z:x"));
  }

  @Test public void testInvalidPT1()
    throws Exception
  {
    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.BAD_TYPE_REFERENCE));

    JPRATypeReferenceNameParser.onName(
      JPRATypeReferenceNameParserTest.getSerializer(),
      (SExpressionSymbolType) JPRATypeReferenceNameParserTest.parseExpression(
        "Z:x"));
  }

  @Test public void testInvalidPT2()
    throws Exception
  {
    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.BAD_TYPE_REFERENCE));

    JPRATypeReferenceNameParser.onName(
      JPRATypeReferenceNameParserTest.getSerializer(),
      (SExpressionSymbolType) JPRATypeReferenceNameParserTest.parseExpression(
        "Z:"));
  }

  @Test public void testValidPTF0()
    throws Exception
  {
    final TypeExprNamePTF e = (TypeExprNamePTF) JPRATypeReferenceNameParser.onName(

      JPRATypeReferenceNameParserTest.getSerializer(),
      (SExpressionSymbolType) JPRATypeReferenceNameParserTest.parseExpression(
        "p:T.f.g"));

    Assert.assertEquals("p", e.getPackage().getValue());
    Assert.assertEquals("T", e.getType().getValue());
    final ImmutableList<FieldName> path = e.getFieldPath();
    Assert.assertEquals(2L, (long) path.size());
    Assert.assertEquals("f", path.get(0).getValue());
    Assert.assertEquals("g", path.get(1).getValue());
  }

  @Test public void testValidPT0()
    throws Exception
  {
    final TypeExprNamePT e = (TypeExprNamePT) JPRATypeReferenceNameParser.onName(
      JPRATypeReferenceNameParserTest.getSerializer(),
      (SExpressionSymbolType) JPRATypeReferenceNameParserTest.parseExpression(
        "p:T"));

    Assert.assertEquals("p", e.getPackage().getValue());
    Assert.assertEquals("T", e.getType().getValue());
  }
}
