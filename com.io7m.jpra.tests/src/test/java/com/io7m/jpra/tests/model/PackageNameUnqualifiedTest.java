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

import com.io7m.jaffirm.core.PreconditionViolationException;
import com.io7m.jpra.model.names.PackageNameUnqualified;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public final class PackageNameUnqualifiedTest
{
  @Test
  public void testValid0()
  {
    final PackageNameUnqualified p =
      PackageNameUnqualified.of(Optional.empty(), "a");
    Assert.assertEquals("a", p.value());
  }

  @Test
  public void testValid1()
  {
    final PackageNameUnqualified p =
      PackageNameUnqualified.of(Optional.empty(), "a_");
    Assert.assertEquals("a_", p.value());
  }

  @Test
  public void testValid2()
  {
    final PackageNameUnqualified p =
      PackageNameUnqualified.of(Optional.empty(), "a1");
    Assert.assertEquals("a1", p.value());
  }

  @Test(expected = PreconditionViolationException.class)
  public void testInvalid0()
  {
    PackageNameUnqualified.of(Optional.empty(), "1");
  }

  @Test(expected = PreconditionViolationException.class)
  public void testInvalid1()
  {
    PackageNameUnqualified.of(Optional.empty(), "A");
  }

  @Test(expected = PreconditionViolationException.class)
  public void testInvalid2()
  {
    PackageNameUnqualified.of(Optional.empty(), "_");
  }

  @Test(expected = PreconditionViolationException.class)
  public void testInvalid3()
  {
    PackageNameUnqualified.of(Optional.empty(), "aA");
  }

  @Test(expected = PreconditionViolationException.class)
  public void testInvalid4()
  {
    PackageNameUnqualified.of(Optional.empty(), "");
  }
}
