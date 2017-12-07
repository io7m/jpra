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
import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jpra.model.names.UnionCaseName;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.util.Optional;

public final class UnionCaseNameTest
{
  static final LexicalPosition<URI> LEX_ZERO =
    LexicalPosition.of(0, 0, Optional.empty());

  @Test
  public void testValid0()
  {
    final UnionCaseName p = UnionCaseName.of(LEX_ZERO, "A");
    Assert.assertEquals("A", p.value());
  }

  @Test
  public void testValid1()
  {
    final UnionCaseName p = UnionCaseName.of(LEX_ZERO, "A_");
    Assert.assertEquals("A_", p.value());
  }

  @Test
  public void testValid2()
  {
    final UnionCaseName p = UnionCaseName.of(LEX_ZERO, "A1");
    Assert.assertEquals("A1", p.value());
  }

  @Test
  public void testValid3()
  {
    final UnionCaseName p = UnionCaseName.of(LEX_ZERO, "AA");
    Assert.assertEquals("AA", p.value());
  }

  @Test(expected = PreconditionViolationException.class)
  public void testInvalid0()
  {
    UnionCaseName.of(LEX_ZERO, "1");
  }

  @Test(expected = PreconditionViolationException.class)
  public void testInvalid1()
  {
    UnionCaseName.of(LEX_ZERO, "a");
  }

  @Test(expected = PreconditionViolationException.class)
  public void testInvalid2()
  {
    UnionCaseName.of(LEX_ZERO, "_");
  }

  @Test(expected = PreconditionViolationException.class)
  public void testInvalid3()
  {
    UnionCaseName.of(LEX_ZERO, "aA");
  }

  @Test(expected = PreconditionViolationException.class)
  public void testInvalid4()
  {
    UnionCaseName.of(LEX_ZERO, "");
  }

}
