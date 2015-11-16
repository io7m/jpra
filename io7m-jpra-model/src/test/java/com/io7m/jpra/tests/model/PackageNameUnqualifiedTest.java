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

package com.io7m.jpra.tests.model;

import com.io7m.jpra.model.names.PackageNameUnqualified;
import org.junit.Assert;
import org.junit.Test;
import org.valid4j.exceptions.RequireViolation;

import java.util.Optional;

public final class PackageNameUnqualifiedTest
{
  @Test public void testValid0()
  {
    final PackageNameUnqualified p =
      new PackageNameUnqualified(Optional.empty(), "a");
    Assert.assertEquals("a", p.getValue());
  }

  @Test public void testValid1()
  {
    final PackageNameUnqualified p =
      new PackageNameUnqualified(Optional.empty(), "a_");
    Assert.assertEquals("a_", p.getValue());
  }

  @Test public void testValid2()
  {
    final PackageNameUnqualified p =
      new PackageNameUnqualified(Optional.empty(), "a1");
    Assert.assertEquals("a1", p.getValue());
  }

  @Test(expected = RequireViolation.class) public void testInvalid0()
  {
    new PackageNameUnqualified(Optional.empty(), "1");
  }

  @Test(expected = RequireViolation.class) public void testInvalid1()
  {
    new PackageNameUnqualified(Optional.empty(), "A");
  }

  @Test(expected = RequireViolation.class) public void testInvalid2()
  {
    new PackageNameUnqualified(Optional.empty(), "_");
  }

  @Test(expected = RequireViolation.class) public void testInvalid3()
  {
    new PackageNameUnqualified(Optional.empty(), "aA");
  }

  @Test(expected = RequireViolation.class) public void testInvalid4()
  {
    new PackageNameUnqualified(Optional.empty(), "");
  }
}
