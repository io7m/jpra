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
  .BooleanSetsByteBuffered;
import com.io7m.jpra.compiler.tests.java.generation.code.BooleanSetsType;
import com.io7m.jpra.runtime.java.JPRACursor1DByteBufferedUnchecked;
import com.io7m.jpra.runtime.java.JPRACursor1DType;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;

public final class BooleanSetsTest
{
  private static void checkBit(
    final BooleanSetsType v,
    final int i)
  {
    Assert.assertEquals(i == 0, v.getB0A0());
    Assert.assertEquals(i == 1, v.getB0A1());
    Assert.assertEquals(i == 2, v.getB0A2());
    Assert.assertEquals(i == 3, v.getB0A3());
    Assert.assertEquals(i == 4, v.getB0A4());
    Assert.assertEquals(i == 5, v.getB0A5());
    Assert.assertEquals(i == 6, v.getB0A6());
    Assert.assertEquals(i == 7, v.getB0A7());
    Assert.assertEquals(i == 8, v.getB0A8());
    Assert.assertEquals(i == 9, v.getB0A9());
    Assert.assertEquals(i == 10, v.getB0A10());
    Assert.assertEquals(i == 11, v.getB0A11());
    Assert.assertEquals(i == 12, v.getB0A12());
    Assert.assertEquals(i == 13, v.getB0A13());
    Assert.assertEquals(i == 14, v.getB0A14());
    Assert.assertEquals(i == 15, v.getB0A15());

    Assert.assertEquals(i == 16, v.getB0B0());
    Assert.assertEquals(i == 17, v.getB0B1());
    Assert.assertEquals(i == 18, v.getB0B2());
    Assert.assertEquals(i == 19, v.getB0B3());
    Assert.assertEquals(i == 20, v.getB0B4());
    Assert.assertEquals(i == 21, v.getB0B5());
    Assert.assertEquals(i == 22, v.getB0B6());
    Assert.assertEquals(i == 23, v.getB0B7());
    Assert.assertEquals(i == 24, v.getB0B8());
    Assert.assertEquals(i == 25, v.getB0B9());
    Assert.assertEquals(i == 26, v.getB0B10());
    Assert.assertEquals(i == 27, v.getB0B11());
    Assert.assertEquals(i == 28, v.getB0B12());
    Assert.assertEquals(i == 29, v.getB0B13());
    Assert.assertEquals(i == 30, v.getB0B14());
    Assert.assertEquals(i == 31, v.getB0B15());
  }

  private static void setBit(
    final BooleanSetsType v,
    final int i)
  {
    v.setB0A0(i == 0);
    v.setB0A1(i == 1);
    v.setB0A2(i == 2);
    v.setB0A3(i == 3);
    v.setB0A4(i == 4);
    v.setB0A5(i == 5);
    v.setB0A6(i == 6);
    v.setB0A7(i == 7);
    v.setB0A8(i == 8);
    v.setB0A9(i == 9);
    v.setB0A10(i == 10);
    v.setB0A11(i == 11);
    v.setB0A12(i == 12);
    v.setB0A13(i == 13);
    v.setB0A14(i == 14);
    v.setB0A15(i == 15);

    v.setB0B0(i == 16);
    v.setB0B1(i == 17);
    v.setB0B2(i == 18);
    v.setB0B3(i == 19);
    v.setB0B4(i == 20);
    v.setB0B5(i == 21);
    v.setB0B6(i == 22);
    v.setB0B7(i == 23);
    v.setB0B8(i == 24);
    v.setB0B9(i == 25);
    v.setB0B10(i == 26);
    v.setB0B11(i == 27);
    v.setB0B12(i == 28);
    v.setB0B13(i == 29);
    v.setB0B14(i == 30);
    v.setB0B15(i == 31);
  }

  @Test public void testSize()
  {
    final ByteBuffer buf = ByteBuffer.allocate(1024);
    final JPRACursor1DType<BooleanSetsType> c =
      JPRACursor1DByteBufferedUnchecked.newCursor(
        buf, BooleanSetsByteBuffered::newValueWithOffset);

    final BooleanSetsType v = c.getElementView();
    Assert.assertEquals(4L, (long) v.sizeOctets());
  }

  @Test public void testSetGetExhaustive()
  {
    final ByteBuffer buf = ByteBuffer.allocate(1 * 8);
    final JPRACursor1DType<BooleanSetsType> c =
      JPRACursor1DByteBufferedUnchecked.newCursor(
        buf, BooleanSetsByteBuffered::newValueWithOffset);
    final BooleanSetsType v = c.getElementView();

    for (int index = 0; index < 32; ++index) {
      BooleanSetsTest.setBit(v, index);
      BooleanSetsTest.checkBit(v, index);
    }
  }

  @Test public void testSetGet()
  {
    final ByteBuffer buf = ByteBuffer.allocate(4 * 8);
    final JPRACursor1DType<BooleanSetsType> c =
      JPRACursor1DByteBufferedUnchecked.newCursor(
        buf, BooleanSetsByteBuffered::newValueWithOffset);

    final BooleanSetsType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    int offset = 0;
    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      BufferChecks.checkRangeInclusiveIsZero(buf, offset + 1, offset + 3);

      Assert.assertFalse(v.getB0A0());
      Assert.assertFalse(v.getB0A1());
      Assert.assertFalse(v.getB0A2());
      Assert.assertFalse(v.getB0A3());
      Assert.assertFalse(v.getB0A4());
      Assert.assertFalse(v.getB0A5());
      Assert.assertFalse(v.getB0A6());
      Assert.assertFalse(v.getB0A7());
      Assert.assertFalse(v.getB0A8());
      Assert.assertFalse(v.getB0A9());
      Assert.assertFalse(v.getB0A10());
      Assert.assertFalse(v.getB0A11());
      Assert.assertFalse(v.getB0A12());
      Assert.assertFalse(v.getB0A13());
      Assert.assertFalse(v.getB0A14());
      Assert.assertFalse(v.getB0A15());

      v.setB0A0(true);
      Assert.assertTrue(v.getB0A0());
      Assert.assertFalse(v.getB0A1());
      Assert.assertFalse(v.getB0A2());
      Assert.assertFalse(v.getB0A3());
      Assert.assertFalse(v.getB0A4());
      Assert.assertFalse(v.getB0A5());
      Assert.assertFalse(v.getB0A6());
      Assert.assertFalse(v.getB0A7());
      Assert.assertFalse(v.getB0A8());
      Assert.assertFalse(v.getB0A9());
      Assert.assertFalse(v.getB0A10());
      Assert.assertFalse(v.getB0A11());
      Assert.assertFalse(v.getB0A12());
      Assert.assertFalse(v.getB0A13());
      Assert.assertFalse(v.getB0A14());
      Assert.assertFalse(v.getB0A15());

      Assert.assertEquals(
        0b10000000L, Byte.toUnsignedLong(buf.get(offset + 0)));
      v.setB0A0(false);

      Assert.assertFalse(v.getB0A0());
      Assert.assertFalse(v.getB0A1());
      Assert.assertFalse(v.getB0A2());
      Assert.assertFalse(v.getB0A3());
      Assert.assertFalse(v.getB0A4());
      Assert.assertFalse(v.getB0A5());
      Assert.assertFalse(v.getB0A6());
      Assert.assertFalse(v.getB0A7());
      Assert.assertFalse(v.getB0A8());
      Assert.assertFalse(v.getB0A9());
      Assert.assertFalse(v.getB0A10());
      Assert.assertFalse(v.getB0A11());
      Assert.assertFalse(v.getB0A12());
      Assert.assertFalse(v.getB0A13());
      Assert.assertFalse(v.getB0A14());
      Assert.assertFalse(v.getB0A15());

      offset += 4;
    }
  }
}
