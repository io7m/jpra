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

package com.io7m.jpra.tests.compiler.core.driver;

import com.io7m.jlexing.core.ImmutableLexicalPositionType;
import com.io7m.jpra.compiler.core.JPRACompilerLexerException;
import com.io7m.jpra.compiler.core.checker.JPRACheckerCapabilitiesType;
import com.io7m.jpra.compiler.core.checker.JPRACheckerStandardCapabilities;
import com.io7m.jpra.compiler.core.driver.JPRADriverType;
import com.io7m.jpra.compiler.core.resolver.JPRACompilerResolverException;
import com.io7m.jpra.compiler.core.resolver.JPRAResolverErrorCode;
import com.io7m.jpra.core.JPRAException;
import com.io7m.jpra.model.contexts.GlobalContextType;
import com.io7m.jpra.model.contexts.PackageContextType;
import com.io7m.jpra.model.loading.JPRAModelCircularImportException;
import com.io7m.jpra.model.loading.JPRAModelLoadingException;
import com.io7m.jpra.model.names.PackageNameQualified;
import com.io7m.jpra.model.names.PackageNameUnqualified;
import com.io7m.jpra.tests.compiler.core.TypeSafeDiagnosingMatcherWith;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Queue;

public abstract class JPRADriverContract
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(JPRADriverContract.class);
  }

  @Rule public ExpectedException expected = ExpectedException.none();

  private static Path getFirstRoot(final FileSystem fs)
  {
    return fs.getRootDirectories().iterator().next();
  }

  private static void createFileFromResource(
    final Path file,
    final String s)
    throws IOException
  {
    final Class<JPRADriverContract> c = JPRADriverContract.class;

    try (final OutputStream os = Files.newOutputStream(file)) {
      try (final InputStream is = c.getResourceAsStream(s)) {
        final byte[] buffer = new byte[8192];
        while (true) {
          final int r = is.read(buffer);
          if (r == -1) {
            break;
          }
          os.write(buffer, 0, r);
        }
        os.flush();
      }
    }
  }

  protected abstract JPRADriverType getDriver(
    final Path base,
    final JPRACheckerCapabilitiesType caps);

  protected abstract FileSystem getFilesystem()
    throws IOException;

  @Test public final void testPackageNonexistent()
    throws Exception
  {
    final Optional<ImmutableLexicalPositionType<Path>> no_lex =
      Optional.empty();

    try (final FileSystem fs = this.getFilesystem()) {
      final Path base = JPRADriverContract.getFirstRoot(fs);
      final JPRACheckerCapabilitiesType caps =
        JPRACheckerStandardCapabilities.newCapabilities();

      final JPRADriverType d = this.getDriver(base, caps);

      this.expected.expect(
        new TypeSafeDiagnosingMatcherWith<JPRAModelLoadingException>(
          () -> {
            final GlobalContextType c = d.getGlobalContext();
            final Queue<JPRAException> q = c.getErrorQueue();
            JPRADriverContract.LOG.error("{}", q);

            Assert.assertEquals(1L, (long) q.size());

            {
              final JPRACompilerResolverException e =
                (JPRACompilerResolverException) q.poll();
              Assert.assertEquals(
                JPRAResolverErrorCode.PACKAGE_NONEXISTENT, e.getErrorCode());
            }

            Assert.assertEquals(0L, (long) q.size());
          }));

      d.compilePackage(
        PackageNameQualified.of(
          new PackageNameUnqualified(no_lex, "x"),
          new PackageNameUnqualified(no_lex, "y"),
          new PackageNameUnqualified(no_lex, "z")));
    }
  }

  @Test public final void testPackageCircular()
    throws Exception
  {
    final Optional<ImmutableLexicalPositionType<Path>> no_lex =
      Optional.empty();

    try (final FileSystem fs = this.getFilesystem()) {
      final Path base = JPRADriverContract.getFirstRoot(fs);
      final Path dir = base.resolve("x").resolve("y");
      Files.createDirectories(dir);
      final Path file_0 = dir.resolve("a.jpr");
      JPRADriverContract.createFileFromResource(file_0, "a.jpr");
      final Path file_1 = dir.resolve("b.jpr");
      JPRADriverContract.createFileFromResource(file_1, "b.jpr");

      final JPRACheckerCapabilitiesType caps =
        JPRACheckerStandardCapabilities.newCapabilities();

      final JPRADriverType d = this.getDriver(base, caps);

      this.expected.expect(
        new TypeSafeDiagnosingMatcherWith<JPRAModelLoadingException>(
          () -> {
            final GlobalContextType c = d.getGlobalContext();
            final Queue<JPRAException> q = c.getErrorQueue();
            JPRADriverContract.LOG.error("{}", q);

            Assert.assertEquals(3L, (long) q.size());

            {
              final JPRAModelCircularImportException e =
                (JPRAModelCircularImportException) q.poll();
              Assert.assertTrue(e.getMessage().contains("Circular import"));
            }

            {
              final JPRACompilerResolverException e =
                (JPRACompilerResolverException) q.poll();
              Assert.assertTrue(
                e.getMessage().contains("Error loading package"));
            }

            {
              final JPRACompilerResolverException e =
                (JPRACompilerResolverException) q.poll();
              Assert.assertTrue(
                e.getMessage().contains("Error loading package"));
            }

            Assert.assertEquals(0L, (long) q.size());
          }));

      d.compilePackage(
        PackageNameQualified.of(
          new PackageNameUnqualified(no_lex, "x"),
          new PackageNameUnqualified(no_lex, "y"),
          new PackageNameUnqualified(no_lex, "a")));
    }
  }

  @Test public final void testPackageEmpty()
    throws Exception
  {
    final Optional<ImmutableLexicalPositionType<Path>> no_lex =
      Optional.empty();

    try (final FileSystem fs = this.getFilesystem()) {
      final Path base = JPRADriverContract.getFirstRoot(fs);
      final Path dir = base.resolve("x").resolve("y");
      Files.createDirectories(dir);
      Files.createFile(dir.resolve("z.jpr"));

      final JPRACheckerCapabilitiesType caps =
        JPRACheckerStandardCapabilities.newCapabilities();

      final JPRADriverType d = this.getDriver(base, caps);

      this.expected.expect(
        new TypeSafeDiagnosingMatcherWith<JPRAModelLoadingException>(
          () -> {
            final GlobalContextType c = d.getGlobalContext();
            final Queue<JPRAException> q = c.getErrorQueue();
            JPRADriverContract.LOG.error("{}", q);

            {
              final JPRACompilerResolverException e =
                (JPRACompilerResolverException) q.poll();
              Assert.assertEquals(
                JPRAResolverErrorCode.EXPECTED_PACKAGE, e.getErrorCode());
            }

            Assert.assertEquals(0L, (long) q.size());
          }));

      d.compilePackage(
        PackageNameQualified.of(
          new PackageNameUnqualified(no_lex, "x"),
          new PackageNameUnqualified(no_lex, "y"),
          new PackageNameUnqualified(no_lex, "z")));
    }
  }

  @Test public final void testPackageUnparseable()
    throws Exception
  {
    final Optional<ImmutableLexicalPositionType<Path>> no_lex =
      Optional.empty();

    try (final FileSystem fs = this.getFilesystem()) {
      final Path base = JPRADriverContract.getFirstRoot(fs);
      final Path dir = base.resolve("x").resolve("y");
      Files.createDirectories(dir);
      final Path file = dir.resolve("z.jpr");
      JPRADriverContract.createFileFromResource(file, "badlex.jpr");

      final JPRACheckerCapabilitiesType caps =
        JPRACheckerStandardCapabilities.newCapabilities();
      final JPRADriverType d = this.getDriver(base, caps);

      this.expected.expect(
        new TypeSafeDiagnosingMatcherWith<JPRAModelLoadingException>(
          () -> {
            final GlobalContextType c = d.getGlobalContext();
            final Queue<JPRAException> q = c.getErrorQueue();
            JPRADriverContract.LOG.error("{}", q);

            {
              final JPRACompilerLexerException e =
                (JPRACompilerLexerException) q.poll();
            }

            {
              final JPRACompilerResolverException e =
                (JPRACompilerResolverException) q.poll();
              Assert.assertEquals(
                JPRAResolverErrorCode.EXPECTED_PACKAGE, e.getErrorCode());
            }

            Assert.assertEquals(0L, (long) q.size());
          }));

      d.compilePackage(
        PackageNameQualified.of(
          new PackageNameUnqualified(no_lex, "x"),
          new PackageNameUnqualified(no_lex, "y"),
          new PackageNameUnqualified(no_lex, "z")));
    }
  }

  @Test public final void testPackageTwice()
    throws Exception
  {
    final Optional<ImmutableLexicalPositionType<Path>> no_lex =
      Optional.empty();

    try (final FileSystem fs = this.getFilesystem()) {
      final Path base = JPRADriverContract.getFirstRoot(fs);
      final Path dir = base.resolve("x").resolve("y");
      Files.createDirectories(dir);
      final Path file = dir.resolve("z.jpr");
      JPRADriverContract.createFileFromResource(file, "z.jpr");

      final JPRACheckerCapabilitiesType caps =
        JPRACheckerStandardCapabilities.newCapabilities();
      final JPRADriverType d = this.getDriver(base, caps);

      final PackageContextType pc0 = d.compilePackage(
        PackageNameQualified.of(
          new PackageNameUnqualified(no_lex, "x"),
          new PackageNameUnqualified(no_lex, "y"),
          new PackageNameUnqualified(no_lex, "z")));

      final PackageContextType pc1 = d.compilePackage(
        PackageNameQualified.of(
          new PackageNameUnqualified(no_lex, "x"),
          new PackageNameUnqualified(no_lex, "y"),
          new PackageNameUnqualified(no_lex, "z")));

      Assert.assertSame(pc0, pc1);
    }
  }

  private static class LoggingCauseMatcher<T extends Throwable>
    extends TypeSafeDiagnosingMatcher<T>
  {
    @Override public void describeTo(final Description description)
    {

    }

    @Override protected boolean matchesSafely(
      final T item,
      final Description mismatchDescription)
    {
      JPRADriverContract.LOG.error("cause ", item);
      return true;
    }
  }
}
