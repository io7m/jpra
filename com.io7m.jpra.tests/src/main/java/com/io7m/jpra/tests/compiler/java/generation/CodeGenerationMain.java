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

package com.io7m.jpra.tests.compiler.java.generation;

import com.gs.collections.impl.factory.Lists;
import com.io7m.jpra.compiler.core.JPRAProblemFormatter;
import com.io7m.jpra.compiler.core.JPRAProblemFormatterType;
import com.io7m.jpra.compiler.core.checker.JPRACheckerStandardCapabilities;
import com.io7m.jpra.compiler.core.driver.JPRADriver;
import com.io7m.jpra.compiler.core.driver.JPRADriverType;
import com.io7m.jpra.compiler.java.JPRAJavaGenerator;
import com.io7m.jpra.compiler.java.JPRAJavaGeneratorType;
import com.io7m.jpra.compiler.java.JPRAJavaWriter;
import com.io7m.jpra.compiler.java.JPRAJavaWriterType;
import com.io7m.jpra.core.JPRAException;
import com.io7m.jpra.model.contexts.GlobalContextType;
import com.io7m.jpra.model.contexts.PackageContextType;
import com.io7m.jpra.model.loading.JPRAModelLoadingException;
import com.io7m.jpra.model.names.PackageNameQualified;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.jpra.model.types.TypeUserDefinedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Generate code for the test suite.
 */

public final class CodeGenerationMain
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(CodeGenerationMain.class);
  }

  private CodeGenerationMain()
  {
    throw new AssertionError("Unreachable code!");
  }

  /**
   * Main entry point.
   *
   * @param args Command line arguments
   *
   * @throws IOException On compilation errors
   */

  public static void main(final String[] args)
    throws IOException
  {
    if (args.length != 2) {
      throw new IllegalArgumentException("usage: source target");
    }

    final Path source_directory = Paths.get(args[0]).toAbsolutePath();
    final Path target_directory = Paths.get(args[1]).toAbsolutePath();

    LOG.debug("source directory: {}", source_directory);
    LOG.debug("target directory: {}", target_directory);

    final List<PackageNameQualified> pack_names = Lists.mutable.empty();
    pack_names.add(
      PackageNameQualified.valueOf(
        "com.io7m.jpra.tests.compiler.java.generation.code"));

    final JPRAJavaGeneratorType gen = JPRAJavaGenerator.newGenerator();
    final JPRAProblemFormatterType fmt = JPRAProblemFormatter.newFormatter();
    final JPRAJavaWriterType writer = JPRAJavaWriter.newWriter(gen);

    boolean error = false;
    final JPRADriverType driver = JPRADriver.newDriver(
      source_directory, JPRACheckerStandardCapabilities.newCapabilities());
    final GlobalContextType gc = driver.getGlobalContext();

    for (final PackageNameQualified pack_name : pack_names) {
      LOG.debug("checking {}", pack_name);

      try {
        driver.compilePackage(pack_name);
      } catch (final JPRAModelLoadingException e) {
        error = true;
      }

      final Queue<JPRAException> q = gc.getErrorQueue();
      final Iterator<JPRAException> iter = q.iterator();
      while (iter.hasNext()) {
        final JPRAException e = iter.next();
        fmt.onJPRAException(System.err, e);
        iter.remove();
        error = true;
      }
    }

    if (!error) {
      LOG.debug("generating code");

      final Map<PackageNameQualified, PackageContextType> packs =
        gc.getPackages();

      for (final PackageNameQualified pack_name : pack_names) {
        final PackageContextType pack = packs.get(pack_name);
        final Map<TypeName, TypeUserDefinedType> types = pack.getTypes();
        for (final TypeName t_name : types.keySet()) {
          final TypeUserDefinedType type = types.get(t_name);
          try {
            writer.writeType(target_directory, type);
          } catch (final IOException e) {
            error = true;
            System.err.printf("i/o error: %s", e);
            System.err.println();
          }
        }
      }
    }

    System.err.flush();
    if (error) {
      throw new IOException("Failed compilation");
    }
  }
}
