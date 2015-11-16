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

package com.io7m.jpra.compiler.tests.java.output;

import org.junit.Assert;

import java.nio.ByteBuffer;

final class BufferChecks
{
  private BufferChecks()
  {
    throw new AssertionError();
  }

  /**
   * Check that all bytes in the given inclusive range are zero.
   */

  static void checkRangeInclusiveIsZero(
    final ByteBuffer b,
    final int from,
    final int to)
  {
    for (int index = from; index <= to; ++index) {
      final byte v = b.get(index);
      System.out.printf("[%d] %x\n", index, v);
      Assert.assertEquals(0L, (long) v);
    }
    System.out.printf("--\n");
  }
}
