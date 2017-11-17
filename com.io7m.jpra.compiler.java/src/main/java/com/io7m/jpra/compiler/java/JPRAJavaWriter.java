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

package com.io7m.jpra.compiler.java;

import com.io7m.jpra.model.names.PackageNameQualified;
import com.io7m.jpra.model.names.PackageNameUnqualified;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.jpra.model.types.TPacked;
import com.io7m.jpra.model.types.TRecord;
import com.io7m.jpra.model.types.TypeUserDefinedMatcherType;
import com.io7m.jpra.model.types.TypeUserDefinedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * The default implementation of the {@link JPRAJavaWriterType} interface.
 */

public final class JPRAJavaWriter implements JPRAJavaWriterType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(JPRAJavaWriter.class);
  }

  private final JPRAJavaGeneratorType generator;

  private JPRAJavaWriter(
    final JPRAJavaGeneratorType in_generator)
  {
    this.generator = Objects.requireNonNull(in_generator, "Generator");
  }

  /**
   * @param in_generator A Java code generator
   *
   * @return A new writer
   */

  public static JPRAJavaWriterType newWriter(
    final JPRAJavaGeneratorType in_generator)
  {
    return new JPRAJavaWriter(in_generator);
  }

  /**
   * A function to return a path for the package {@code p_name}, based on {@code
   * base}.
   *
   * @param base   The base path
   * @param p_name The package name
   *
   * @return A path
   */

  public static Path getPathForPackage(
    final Path base,
    final PackageNameQualified p_name)
  {
    Path p = base;
    for (final PackageNameUnqualified e : p_name.getValue()) {
      p = p.resolve(e.value());
    }
    return p;
  }

  @Override
  public void writeType(
    final Path path,
    final TypeUserDefinedType t)
    throws IOException
  {
    Objects.requireNonNull(path, "path");
    Objects.requireNonNull(t, "t");

    final TypeName t_name = t.getName();
    final PackageNameQualified p_name = t.getPackageContext().getName();
    LOG.debug("exporting {}.{}", p_name, t_name);

    final Path pkg_path = getPathForPackage(path, p_name);
    Files.createDirectories(pkg_path);

    final JPRAJavaGeneratorType g = this.generator;
    t.matchTypeUserDefined(
      new TypeUserDefinedMatcherType<Void, IOException>()
      {
        @Override
        public Void matchRecord(final TRecord r)
          throws IOException
        {
          final Path c_file = pkg_path.resolve(
            g.getRecordImplementationByteBufferedName(t_name) + ".java");
          final Path r_file = pkg_path.resolve(
            g.getRecordInterfaceReadableName(t_name) + ".java");
          final Path w_file = pkg_path.resolve(
            g.getRecordInterfaceWritableName(t_name) + ".java");
          final Path i_file =
            pkg_path.resolve(g.getRecordInterfaceName(t_name) + ".java");

          LOG.debug("writing {}", c_file);
          try (OutputStream w = Files.newOutputStream(c_file)) {
            g.generateRecordImplementation(r, w);
          }
          LOG.debug("writing {}", r_file);
          try (OutputStream w = Files.newOutputStream(r_file)) {
            g.generateRecordInterfaceReadable(r, w);
          }
          LOG.debug("writing {}", w_file);
          try (OutputStream w = Files.newOutputStream(w_file)) {
            g.generateRecordInterfaceWritable(r, w);
          }
          LOG.debug("writing {}", i_file);
          try (OutputStream w = Files.newOutputStream(i_file)) {
            g.generateRecordInterface(r, w);
          }

          return null;
        }

        @Override
        public Void matchPacked(final TPacked r)
          throws IOException
        {
          final Path c_file = pkg_path.resolve(
            g.getPackedImplementationByteBufferedName(t_name) + ".java");
          final Path r_file = pkg_path.resolve(
            g.getPackedInterfaceReadableName(t_name) + ".java");
          final Path w_file = pkg_path.resolve(
            g.getPackedInterfaceWritableName(t_name) + ".java");
          final Path i_file =
            pkg_path.resolve(g.getPackedInterfaceName(t_name) + ".java");

          LOG.debug("writing {}", c_file);
          try (OutputStream w = Files.newOutputStream(c_file)) {
            g.generatePackedImplementation(r, w);
          }
          LOG.debug("writing {}", r_file);
          try (OutputStream w = Files.newOutputStream(r_file)) {
            g.generatePackedInterfaceReadable(r, w);
          }
          LOG.debug("writing {}", w_file);
          try (OutputStream w = Files.newOutputStream(w_file)) {
            g.generatePackedInterfaceWritable(r, w);
          }
          LOG.debug("writing {}", i_file);
          try (OutputStream w = Files.newOutputStream(i_file)) {
            g.generatePackedInterface(r, w);
          }

          return null;
        }
      });
  }
}
