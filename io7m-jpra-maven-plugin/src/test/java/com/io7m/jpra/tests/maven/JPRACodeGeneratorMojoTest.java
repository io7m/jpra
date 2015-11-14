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

package com.io7m.jpra.tests.maven;

import io.takari.maven.testing.TestMavenRuntime;
import io.takari.maven.testing.TestResources;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

public final class JPRACodeGeneratorMojoTest
{
  @Rule public final TestResources resources = new TestResources();

  @Rule public final TestMavenRuntime maven = new TestMavenRuntime();

  @Test public void testEmpty()
    throws Exception
  {
    final File basedir = this.resources.getBasedir("empty");
    this.maven.executeMojo(basedir, "generate-java");
  }

  @Test(expected = MojoFailureException.class) public void testCompileFailure()
    throws Exception
  {
    final File basedir = this.resources.getBasedir("compile-failure");

    final Xpp3Dom p = new Xpp3Dom("param");
    p.setValue("com.io7m.bad");
    final Xpp3Dom ps = new Xpp3Dom("packages");
    ps.addChild(p);
    this.maven.executeMojo(basedir, "generate-java", ps);
  }

  @Test public void testCorrect0()
    throws Exception
  {
    final File basedir = this.resources.getBasedir("correct-0");

    final Xpp3Dom p = new Xpp3Dom("param");
    p.setValue("com.io7m.correct");
    final Xpp3Dom ps = new Xpp3Dom("packages");
    ps.addChild(p);
    this.maven.executeMojo(basedir, "generate-java", ps);

    TestResources.assertFilesPresent(
      basedir, "target/generated-sources/com/io7m/correct/TType.java");
    TestResources.assertFilesPresent(
      basedir, "target/generated-sources/com/io7m/correct/TByteBuffered.java");
    TestResources.assertFilesPresent(
      basedir, "target/generated-sources/com/io7m/correct/TReadableType.java");
    TestResources.assertFilesPresent(
      basedir, "target/generated-sources/com/io7m/correct/TWritableType.java");
  }
}
