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

package com.io7m.jpra.compiler.core.driver;

import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jeucreader.UnicodeCharacterReader;
import com.io7m.jeucreader.UnicodeCharacterReaderPushBackType;
import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jpra.compiler.core.JPRACompilerException;
import com.io7m.jpra.compiler.core.JPRACompilerLexerException;
import com.io7m.jpra.compiler.core.checker.JPRAChecker;
import com.io7m.jpra.compiler.core.checker.JPRACheckerCapabilitiesType;
import com.io7m.jpra.compiler.core.checker.JPRACheckerType;
import com.io7m.jpra.compiler.core.parser.JPRAParser;
import com.io7m.jpra.compiler.core.parser.JPRAParserType;
import com.io7m.jpra.compiler.core.parser.JPRAReferenceParser;
import com.io7m.jpra.compiler.core.parser.JPRAReferenceParserType;
import com.io7m.jpra.compiler.core.pipeline.JPRAPipeline;
import com.io7m.jpra.compiler.core.pipeline.JPRAPipelineType;
import com.io7m.jpra.compiler.core.resolver.JPRACompilerResolverException;
import com.io7m.jpra.compiler.core.resolver.JPRAResolver;
import com.io7m.jpra.compiler.core.resolver.JPRAResolverType;
import com.io7m.jpra.core.JPRAException;
import com.io7m.jpra.core.JPRAIOException;
import com.io7m.jpra.model.contexts.GlobalContextType;
import com.io7m.jpra.model.contexts.GlobalContexts;
import com.io7m.jpra.model.contexts.PackageContextType;
import com.io7m.jpra.model.loading.JPRAModelLoadingException;
import com.io7m.jpra.model.loading.JPRAPackageLoaderType;
import com.io7m.jpra.model.names.PackageNameQualified;
import com.io7m.jpra.model.names.PackageNameUnqualified;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;

/**
 * Default implementation of the {@link JPRADriverType} interface.
 */

public final class JPRADriver implements JPRADriverType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(JPRADriver.class);
  }

  private final GlobalContextType global;

  private JPRADriver(
    final Path in_base,
    final JPRACheckerCapabilitiesType in_caps)
  {
    this.global = GlobalContexts.newContext(new Loader(in_base, in_caps));
  }

  private static JSXParserType newJSXParser(
    final InputStream s,
    final Path file)
  {
    final InputStreamReader ir = new InputStreamReader(s);
    final UnicodeCharacterReaderPushBackType r =
      UnicodeCharacterReader.newReader(ir);

    final JSXLexerConfiguration.Builder lc =
      JSXLexerConfiguration.builder();
    lc.setNewlinesInQuotedStrings(false);
    lc.setSquareBrackets(true);
    lc.setFile(Optional.of(file.toUri()));

    final JSXLexerType lex = JSXLexer.newLexer(lc.build(), r);

    final JSXParserConfiguration.Builder pc =
      JSXParserConfiguration.builder();
    pc.setPreserveLexical(true);

    return JSXParser.newParser(pc.build(), lex);
  }

  /**
   * Construct a new driver.
   *
   * @param in_base The base source directory
   * @param in_caps The capabilities
   *
   * @return A new driver
   */

  public static JPRADriverType newDriver(
    final Path in_base,
    final JPRACheckerCapabilitiesType in_caps)
  {
    return new JPRADriver(in_base, in_caps);
  }

  @Override
  public PackageContextType compilePackage(final PackageNameQualified p)
    throws JPRAModelLoadingException
  {
    return this.global.loadPackage(p);
  }

  @Override
  public GlobalContextType getGlobalContext()
  {
    return this.global;
  }

  private static final class Loader implements JPRAPackageLoaderType
  {
    private final Path source_directory;
    private final JPRACheckerCapabilitiesType caps;

    private Loader(
      final Path in_base,
      final JPRACheckerCapabilitiesType in_caps)
    {
      this.source_directory = Objects.requireNonNull(in_base, "Path");
      this.caps = Objects.requireNonNull(in_caps, "Capabilities");
    }

    private Path fileForPackage(final PackageNameQualified p)
    {
      Path file = this.source_directory;
      for (final PackageNameUnqualified e : p.getValue()) {
        file = file.resolve(e.value());
      }
      return file.resolveSibling(file.getFileName() + ".jpr");
    }

    @Override
    public PackageContextType evaluate(
      final GlobalContextType c,
      final PackageNameQualified p)
      throws JPRAModelLoadingException
    {
      final JSXSerializerType serial = JSXSerializerTrivial.newSerializer();
      final JPRAReferenceParserType ref_parser =
        JPRAReferenceParser.newParser(serial);
      final JPRAParserType parser = JPRAParser.newParser(serial, ref_parser);
      final JPRAResolverType resolver = JPRAResolver.newResolver(
        c, Optional.of(p));
      final JPRACheckerType checker = JPRAChecker.newChecker(c, this.caps);
      final JPRAPipelineType pipe =
        JPRAPipeline.newPipeline(parser, resolver, checker);

      final Queue<JPRAException> error_queue = c.getErrorQueue();
      Optional<PackageContextType> pack_opt = Optional.empty();

      final Path file = this.fileForPackage(p);
      LOG.debug("loading package {} from {}", p, file);

      boolean error = false;
      try (InputStream is = Files.newInputStream(file)) {
        final JSXParserType sxp = newJSXParser(is, file);

        int line_max = 0;

        boolean done = false;
        while (!done) {
          try {
            final Optional<SExpressionType> e_opt = sxp.parseExpressionOrEOF();
            if (e_opt.isPresent()) {
              final SExpressionType s = e_opt.get();
              final LexicalPosition<URI> lex = s.lexical();
              line_max = Math.max(line_max, lex.line());

              /*
                The resolver is configured to only accept packages named
                {@code p}.
               */

              final Optional<PackageContextType> pr = pipe.onExpression(s);
              if (pr.isPresent()) {
                Preconditions.checkPrecondition(
                  !pack_opt.isPresent(), "Package must be present");
                pack_opt = pr;
              }
            } else {
              done = true;
              pipe.onEOF(LexicalPosition.of(line_max, 0, Optional.empty()));
            }
          } catch (final JPRACompilerException e) {
            error_queue.add(e);
            error = true;
          } catch (final IOException e) {
            error_queue.add(new JPRAIOException(e));
            done = true;
            error = true;
          } catch (final JSXParserException e) {
            error_queue.add(new JPRACompilerLexerException(e));
            error = true;
          }
        }
      } catch (final NoSuchFileException e) {
        error_queue.add(JPRACompilerResolverException.nonexistentPackage(p));
        error = true;
      } catch (final IOException e) {
        error_queue.add(new JPRAIOException(e));
        error = true;
      }

      if (error) {
        throw new JPRAModelLoadingException(
          String.format("Could not load package %s", p));
      }

      Preconditions.checkPrecondition(
        pack_opt.isPresent(), "Package must be present");
      return pack_opt.get();
    }
  }
}
