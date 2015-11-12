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

import com.io7m.jpra.maven.JPRACodeGeneratorMojo;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

public final class JPRACodeGeneratorMojoTest
{
  @Rule public MojoRule rule = new MojoRule()
  {
    @Override protected void before()
      throws Throwable
    {

    }

    @Override protected void after()
    {

    }
  };

  @Test public void testSomething()
    throws Exception
  {
    final File pom = new File(
      "src/test/resources/com/io7m/jpra/tests/maven/project0/pom.xml");
    Assert.assertNotNull(pom);
    Assert.assertTrue(pom.exists());

    final JPRACodeGeneratorMojo mojo =
      (JPRACodeGeneratorMojo) this.rule.lookupMojo("generate-java", pom);
    Assert.assertNotNull(mojo);
    mojo.execute();
  }
}
