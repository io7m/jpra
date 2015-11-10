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

package com.io7m.jpra.tests.compiler.core.pipeline;

import com.gs.collections.impl.factory.Maps;
import com.io7m.jeucreader.UnicodeCharacterReader;
import com.io7m.jeucreader.UnicodeCharacterReaderPushBackType;
import com.io7m.jlexing.core.ImmutableLexicalPosition;
import com.io7m.jlexing.core.ImmutableLexicalPositionType;
import com.io7m.jpra.compiler.core.JPRACompilerException;
import com.io7m.jpra.compiler.core.checker.JPRAChecker;
import com.io7m.jpra.compiler.core.checker.JPRACheckerCapabilitiesType;
import com.io7m.jpra.compiler.core.checker.JPRACheckerStandardCapabilities;
import com.io7m.jpra.compiler.core.checker.JPRACheckerType;
import com.io7m.jpra.compiler.core.parser.JPRAParser;
import com.io7m.jpra.compiler.core.parser.JPRAParserType;
import com.io7m.jpra.compiler.core.parser.JPRAReferenceParser;
import com.io7m.jpra.compiler.core.parser.JPRAReferenceParserType;
import com.io7m.jpra.compiler.core.pipeline.JPRAPipeline;
import com.io7m.jpra.compiler.core.pipeline.JPRAPipelineType;
import com.io7m.jpra.compiler.core.resolver.JPRAResolver;
import com.io7m.jpra.compiler.core.resolver.JPRAResolverType;
import com.io7m.jpra.model.contexts.GlobalContextType;
import com.io7m.jpra.model.contexts.GlobalContexts;
import com.io7m.jpra.model.contexts.PackageContextType;
import com.io7m.jpra.model.loading.JPRAPackageLoaderType;
import com.io7m.jpra.model.names.PackageNameQualified;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.jpra.model.types.TypeUserDefinedType;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

public final class JPRAPipelineDemo
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(JPRAPipelineDemo.class);
  }

  private JPRAPipelineDemo()
  {
    throw new UnreachableCodeException();
  }

  public static void main(final String[] args)
    throws IOException
  {
    final JPRAPackageLoaderType loader = (c, p) -> new PackageContextType()
    {
      @Override public GlobalContextType getGlobalContext()
      {
        return c;
      }

      @Override public Map<TypeName, TypeUserDefinedType> getTypes()
      {
        return Maps.mutable.empty();
      }

      @Override public PackageNameQualified getName()
      {
        return p;
      }

      @Override
      public Optional<ImmutableLexicalPositionType<Path>>
      getLexicalInformation()
      {
        return Optional.empty();
      }
    };

    final JSXParserType sexpr = JPRAPipelineDemo.newJSXParser(System.in);
    final GlobalContextType c = GlobalContexts.newContext(loader);
    final JSXSerializerType serial = JSXSerializerTrivial.newSerializer();
    final JPRAReferenceParserType ref = JPRAReferenceParser.newParser(serial);
    final JPRAParserType parser = JPRAParser.newParser(serial, ref);
    final JPRAResolverType resolver = JPRAResolver.newResolver(c);
    final JPRACheckerCapabilitiesType caps =
      JPRACheckerStandardCapabilities.newCapabilities();
    final JPRACheckerType checker = JPRAChecker.newChecker(c, caps);
    final JPRAPipelineType pipe =
      JPRAPipeline.newPipeline(parser, resolver, checker);

    Optional<ImmutableLexicalPositionType<Path>> lex = Optional.empty();
    boolean done = false;
    while (!done) {
      try {
        System.out.print("jpra$ ");
        final Optional<SExpressionType> opt = sexpr.parseExpressionOrEOF();
        if (opt.isPresent()) {
          final SExpressionType s = opt.get();
          lex =
            s.getLexicalInformation().map(ImmutableLexicalPosition::newFrom);
          pipe.onExpression(s);
        } else {
          done = true;
          pipe.onEOF(lex);
        }
      } catch (final JSXParserException e) {
        JPRAPipelineDemo.LOG.error(
          "{}: {}", e.getLexicalInformation(), e.getMessage());
        System.out.println();
      } catch (final JPRACompilerException e) {
        final Optional<ImmutableLexicalPositionType<Path>> lex_opt =
          e.getLexicalInformation();
        final String error_name = e.getClass().getSimpleName();
        if (lex_opt.isPresent()) {
          final ImmutableLexicalPositionType<Path> elex = lex_opt.get();
          JPRAPipelineDemo.LOG.error(
            "{}: {}: {}", error_name, elex, e.getMessage());
        } else {
          JPRAPipelineDemo.LOG.error("{}: {}", error_name, e.getMessage());
        }
        System.out.println();
      }
    }
  }

  private static JSXParserType newJSXParser(final InputStream s)
  {
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

    return JSXParser.newParser(pc.build(), lex);
  }
}
