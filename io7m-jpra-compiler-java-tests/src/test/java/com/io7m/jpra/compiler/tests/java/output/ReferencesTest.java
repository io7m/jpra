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

import com.io7m.jpra.compiler.tests.java.generation.code
  .IntegersUnsignedReadableType;
import com.io7m.jpra.compiler.tests.java.generation.code
  .IntegersUnsignedWritableType;
import com.io7m.jpra.compiler.tests.java.generation.code.ReferencesByteBuffered;
import com.io7m.jpra.compiler.tests.java.generation.code.ReferencesType;
import com.io7m.jpra.runtime.java.JPRACursor1DByteBufferedUnchecked;
import com.io7m.jpra.runtime.java.JPRACursor1DType;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;

public final class ReferencesTest
{
  @Test public void testReferences()
  {
    final ByteBuffer buf = ByteBuffer.allocate(1024);
    final JPRACursor1DType<ReferencesType> c =
      JPRACursor1DByteBufferedUnchecked.newCursor(
        buf, ReferencesByteBuffered::newValueWithOffset);

    final ReferencesType v = c.getElementView();
    Assert.assertEquals(16L * 3L, (long) v.sizeOctets());
  }

  @Test public void testSetGet()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 16 * 3);
    final JPRACursor1DType<ReferencesType> c =
      JPRACursor1DByteBufferedUnchecked.newCursor(
        buf, ReferencesByteBuffered::newValueWithOffset);
    final ReferencesType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      final IntegersUnsignedWritableType w = v.getR1Writable();
      w.setU8((byte) (index + 1));
      w.setU16((short) (index + 1));
      w.setU32(index + 1);
      w.setU64((long) (index + 1));
    }

    int offset = 0;
    for (int index = 0; index < 8; ++index) {
      BufferChecks.checkRangeInclusiveIsZero(buf, offset + 0, offset + 15);
      BufferChecks.checkRangeInclusiveIsZero(buf, offset + 32, offset + 47);
      offset += 16 * 3;
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);

      final IntegersUnsignedReadableType r0 = v.getR0Readable();
      Assert.assertEquals(0L, (long) r0.getU8());
      Assert.assertEquals(0L, (long) r0.getU16());
      Assert.assertEquals(0L, (long) r0.getU32());
      Assert.assertEquals(0L, r0.getU64());

      final IntegersUnsignedReadableType r1 = v.getR1Readable();
      Assert.assertEquals((long) (index + 1), (long) r1.getU8());
      Assert.assertEquals((long) (index + 1), (long) r1.getU16());
      Assert.assertEquals((long) (index + 1), (long) r1.getU32());
      Assert.assertEquals((long) (index + 1), r1.getU64());

      final IntegersUnsignedReadableType r2 = v.getR2Readable();
      Assert.assertEquals(0L, (long) r2.getU8());
      Assert.assertEquals(0L, (long) r2.getU16());
      Assert.assertEquals(0L, (long) r2.getU32());
      Assert.assertEquals(0L, r2.getU64());
    }
  }
}
