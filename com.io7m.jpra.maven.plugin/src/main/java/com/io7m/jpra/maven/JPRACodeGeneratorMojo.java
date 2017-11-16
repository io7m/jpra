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

package com.io7m.jpra.maven;

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
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

// CHECKSTYLE:OFF

@Mojo(name = "generate-java", requiresProject = true)
public final class JPRACodeGeneratorMojo extends AbstractMojo
{
  /**
   * The list of packages that will be exported to Java source code.
   */

  @Parameter
  private final ArrayList<String> packages = new ArrayList<>();

  @Parameter(defaultValue = "${project}")
  private MavenProject project;

  /**
   * The directory that will contain source files.
   */

  @Parameter(defaultValue = "${project.basedir}/src/main/jpra")
  private File sourceDirectory;

  /**
   * The directory that will contain generated Java files.
   */

  @Parameter(defaultValue = "${project.build.directory}/generated-sources")
  private File targetDirectory;

  /**
   * Construct a plugin.
   */

  public JPRACodeGeneratorMojo()
  {

  }

  @Override
  public void execute()
    throws MojoExecutionException, MojoFailureException
  {
    final Log logger = this.getLog();

    final List<PackageNameQualified> pack_names = this.packages.stream()
      .map(PackageNameQualified::valueOf)
      .collect(Collectors.toList());

    final JPRAJavaGeneratorType gen = JPRAJavaGenerator.newGenerator();
    final JPRAProblemFormatterType fmt = JPRAProblemFormatter.newFormatter();
    final JPRAJavaWriterType writer = JPRAJavaWriter.newWriter(gen);

    final JPRADriverType driver = JPRADriver.newDriver(
      this.sourceDirectory.toPath(),
      JPRACheckerStandardCapabilities.newCapabilities());
    final GlobalContextType gc = driver.getGlobalContext();

    /*
      Check all listed packages, printing all errors for each.
     */

    boolean failed = false;
    for (final PackageNameQualified pack_name : pack_names) {
      logger.debug("checking " + pack_name);

      try {
        driver.compilePackage(pack_name);
      } catch (final JPRAModelLoadingException e) {
        failed = true;
      }

      final Queue<JPRAException> q = gc.getErrorQueue();
      final Iterator<JPRAException> iter = q.iterator();
      while (iter.hasNext()) {
        final JPRAException e = iter.next();
        fmt.onJPRAException(System.err, e);
        iter.remove();
        failed = true;
      }
    }

    /*
      Generate code if all of the packages above compiled correctly.
     */

    if (!failed) {
      logger.debug("generating code");

      final Map<PackageNameQualified, PackageContextType> packs =
        gc.getPackages();

      for (final PackageNameQualified pack_name : pack_names) {
        final PackageContextType pack = packs.get(pack_name);
        final Map<TypeName, TypeUserDefinedType> types = pack.getTypes();
        for (final TypeName t_name : types.keySet()) {
          final TypeUserDefinedType type = types.get(t_name);
          try {
            writer.writeType(this.targetDirectory.toPath(), type);
          } catch (final IOException e) {
            failed = true;
            System.err.printf("i/o failed: %s", e);
            System.err.println();
          }
        }
      }
    }

    /*
      Fail the build on errors.
     */

    if (failed) {
      throw new MojoFailureException("Plugin failed due to one or more errors");
    }

    this.project.addCompileSourceRoot(this.targetDirectory.toString());
  }
}
