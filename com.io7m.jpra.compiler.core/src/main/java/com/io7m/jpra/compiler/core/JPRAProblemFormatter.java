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

package com.io7m.jpra.compiler.core;

import com.gs.collections.api.list.ImmutableList;
import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jpra.compiler.core.checker.JPRACompilerCheckerException;
import com.io7m.jpra.compiler.core.parser.JPRACompilerParseException;
import com.io7m.jpra.compiler.core.resolver.JPRACompilerResolverException;
import com.io7m.jpra.core.JPRAException;
import com.io7m.jpra.core.JPRAIOException;
import com.io7m.jpra.model.PackageImport;
import com.io7m.jpra.model.loading.JPRAModelCircularImportException;
import com.io7m.junreachable.UnreachableCodeException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Default implementation of the {@link JPRAProblemFormatterType} interface.
 */

public final class JPRAProblemFormatter implements JPRAProblemFormatterType
{
  private JPRAProblemFormatter()
  {

  }

  /**
   * @return A new formatter
   */

  public static JPRAProblemFormatterType newFormatter()
  {
    return new JPRAProblemFormatter();
  }

  private static void printLex(
    final PrintWriter w,
    final Optional<LexicalPosition<Path>> lex)
  {
    lex.ifPresent(
      p -> {
        p.file().ifPresent(
          f -> {
            w.print(f);
            w.print(":");
          });
        w.print(p.line() + 1);
        w.print(":");
        w.print(p.column());
        w.print(": ");
      });
  }

  private static void onCompilerException(
    final PrintWriter w,
    final JPRACompilerException e)
  {
    printLex(w, e.getLexicalInformation());

    if (e instanceof JPRACompilerLexerException) {
      w.print("lexical error: ");
      w.println(e.getMessage());
      return;
    }

    if (e instanceof JPRACompilerParseException) {
      w.print("parse error: ");
      w.println(e.getMessage());
      return;
    }

    if (e instanceof JPRACompilerResolverException) {
      w.print("name resolution error: ");
      w.println(e.getMessage());
      return;
    }

    if (e instanceof JPRACompilerCheckerException) {
      w.print("type error: ");
      w.println(e.getMessage());
      return;
    }

    throw new UnreachableCodeException();
  }

  private static void onCircularImportException(
    final PrintWriter w,
    final JPRAModelCircularImportException e)
  {
    final ImmutableList<PackageImport> imports = e.getImports();

    printLex(
      w, imports.get(0).getTo().getLexicalInformation());

    w.println("error: circular import:");

    for (int index = 0; index < imports.size(); ++index) {
      final PackageImport i = imports.get(index);
      w.print("  → import ");
      w.print(i.getTo());
      w.print(" at ");

      final Optional<LexicalPosition<Path>> lex =
        i.getTo().getLexicalInformation();
      lex.ifPresent(
        p -> {
          p.file().ifPresent(
            f -> {
              w.print(f);
              w.print(": ");
            });
          w.print(p.line() + 1);
          w.print(":");
          w.print(p.column());
          w.print("");
        });
      w.println();
    }
  }

  @Override
  public void onJPRAException(
    final OutputStream os,
    final JPRAException e)
  {
    final PrintWriter w = new PrintWriter(os);
    try {
      if (e instanceof JPRACompilerException) {
        onCompilerException(w, (JPRACompilerException) e);
        return;
      }

      if (e instanceof JPRAIOException) {
        w.print("i/o error: ");
        final IOException cause = (IOException) e.getCause();
        w.println(cause.getMessage());
        return;
      }

      if (e instanceof JPRAModelCircularImportException) {
        onCircularImportException(
          w, (JPRAModelCircularImportException) e);
        return;
      }

      w.print("error: ");
      w.println(e.getMessage());
    } finally {
      w.flush();
    }
  }
}
