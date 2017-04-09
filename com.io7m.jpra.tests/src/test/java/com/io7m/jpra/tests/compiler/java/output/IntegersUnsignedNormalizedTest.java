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

package com.io7m.jpra.tests.compiler.java.output;

import com.io7m.jpra.runtime.java.JPRACursor1DByteBufferedChecked;
import com.io7m.jpra.runtime.java.JPRACursor1DByteBufferedUnchecked;
import com.io7m.jpra.runtime.java.JPRACursor1DType;
import com.io7m.jpra.runtime.java.JPRATypeModel;
import com.io7m.jpra.tests.compiler.java.generation.code.IntegersUnsignedNormalizedByteBuffered;
import com.io7m.jpra.tests.compiler.java.generation.code.IntegersUnsignedNormalizedType;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;

public final class IntegersUnsignedNormalizedTest
{
  @Test
  public void testMeta()
  {
    final ByteBuffer buf = ByteBuffer.allocate(1024);
    final JPRACursor1DType<IntegersUnsignedNormalizedType> c =
      JPRACursor1DByteBufferedUnchecked.newCursor(
        buf, IntegersUnsignedNormalizedByteBuffered::newValueWithOffset);

    final IntegersUnsignedNormalizedType v = c.getElementView();
    Assert.assertEquals(16L, (long) v.sizeOctets());

    Assert.assertEquals(0L, (long) v.metaUn8OffsetFromType());
    Assert.assertEquals(2L, (long) v.metaUn16OffsetFromType());
    Assert.assertEquals(4L, (long) v.metaUn32OffsetFromType());
    Assert.assertEquals(8L, (long) v.metaUn64OffsetFromType());

    Assert.assertEquals(0L, (long) v.metaUn8OffsetFromCursor());
    Assert.assertEquals(2L, (long) v.metaUn16OffsetFromCursor());
    Assert.assertEquals(4L, (long) v.metaUn32OffsetFromCursor());
    Assert.assertEquals(8L, (long) v.metaUn64OffsetFromCursor());

    Assert.assertEquals(
      JPRATypeModel.JPRAIntegerUnsignedNormalized.of(8), v.metaUn8Type());
    Assert.assertEquals(
      JPRATypeModel.JPRAIntegerUnsignedNormalized.of(16), v.metaUn16Type());
    Assert.assertEquals(
      JPRATypeModel.JPRAIntegerUnsignedNormalized.of(32), v.metaUn32Type());
    Assert.assertEquals(
      JPRATypeModel.JPRAIntegerUnsignedNormalized.of(64), v.metaUn64Type());
  }

  @Test
  public void testSetU8()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 16);
    final JPRACursor1DType<IntegersUnsignedNormalizedType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, IntegersUnsignedNormalizedByteBuffered::newValueWithOffset);
    final IntegersUnsignedNormalizedType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      v.setUn8Raw((byte) (index + 1));
    }

    int offset = 0;
    for (int index = 0; index < 8; ++index) {
      Assert.assertEquals((long) (index + 1), (long) buf.get(offset + 0));
      BufferChecks.checkRangeInclusiveIsZero(buf, offset + 1, offset + 15);
      offset += 16;
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      Assert.assertEquals((long) (index + 1), (long) v.getUn8Raw());
    }
  }

  @Test
  public void testRangeU8()
  {
    final ByteBuffer buf = ByteBuffer.allocate(16);
    final JPRACursor1DType<IntegersUnsignedNormalizedType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, IntegersUnsignedNormalizedByteBuffered::newValueWithOffset);
    final IntegersUnsignedNormalizedType v = c.getElementView();

    v.setUn8Raw((byte) 0b11111111);
    Assert.assertEquals(0b11111111L, Byte.toUnsignedLong(v.getUn8Raw()));

    v.setUn8(0.0);
    Assert.assertEquals(0L, v.getUn8Raw());
    Assert.assertEquals(0.0, v.getUn8(), 0.0);

    v.setUn8(1.0);
    Assert.assertEquals(0b11111111, Byte.toUnsignedLong(v.getUn8Raw()));
    Assert.assertEquals(1.0, v.getUn8(), 0.0);
  }

  @Test
  public void testSetU16()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 16);
    final JPRACursor1DType<IntegersUnsignedNormalizedType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, IntegersUnsignedNormalizedByteBuffered::newValueWithOffset);
    final IntegersUnsignedNormalizedType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      v.setUn16Raw((short) (index + 1));
    }

    int offset = 0;
    for (int index = 0; index < 8; ++index) {
      BufferChecks.checkRangeInclusiveIsZero(buf, offset + 0, offset + 1);
      Assert.assertEquals((long) (index + 1), (long) buf.getShort(offset + 2));
      BufferChecks.checkRangeInclusiveIsZero(buf, offset + 4, offset + 15);
      offset += 16;
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      Assert.assertEquals((long) (index + 1), (long) v.getUn16Raw());
    }
  }

  @Test
  public void testRangeU16()
  {
    final ByteBuffer buf = ByteBuffer.allocate(16);
    final JPRACursor1DType<IntegersUnsignedNormalizedType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, IntegersUnsignedNormalizedByteBuffered::newValueWithOffset);
    final IntegersUnsignedNormalizedType v = c.getElementView();

    v.setUn16Raw((short) 0b11111111_11111111);
    Assert.assertEquals(
      0b11111111_11111111L, Short.toUnsignedLong(v.getUn16Raw()));

    v.setUn16(0.0);
    Assert.assertEquals(0L, v.getUn16Raw());
    Assert.assertEquals(0.0, v.getUn16(), 0.0);

    v.setUn16(1.0);
    Assert.assertEquals(
      0b11111111_11111111, Short.toUnsignedLong(v.getUn16Raw()));
    Assert.assertEquals(1.0, v.getUn16(), 0.0);
  }

  @Test
  public void testSetU32()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 16);
    final JPRACursor1DType<IntegersUnsignedNormalizedType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, IntegersUnsignedNormalizedByteBuffered::newValueWithOffset);
    final IntegersUnsignedNormalizedType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      v.setUn32Raw(index + 1);
    }

    int offset = 0;
    for (int index = 0; index < 8; ++index) {
      BufferChecks.checkRangeInclusiveIsZero(buf, offset + 0, offset + 3);
      Assert.assertEquals(index + 1L, (long) buf.getInt(offset + 4));
      BufferChecks.checkRangeInclusiveIsZero(buf, offset + 8, offset + 15);
      offset += 16;
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      Assert.assertEquals((long) (index + 1), (long) v.getUn32Raw());
    }
  }

  @Test
  public void testRangeU32()
  {
    final ByteBuffer buf = ByteBuffer.allocate(16);
    final JPRACursor1DType<IntegersUnsignedNormalizedType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, IntegersUnsignedNormalizedByteBuffered::newValueWithOffset);
    final IntegersUnsignedNormalizedType v = c.getElementView();

    v.setUn32Raw(0b11111111_11111111_11111111_11111111);
    Assert.assertEquals(
      0b11111111_11111111_11111111_11111111L,
      Integer.toUnsignedLong(v.getUn32Raw()));

    v.setUn32(0.0);
    Assert.assertEquals(0L, v.getUn32Raw());
    Assert.assertEquals(0.0, v.getUn32(), 0.0);

    v.setUn32(1.0);
    Assert.assertEquals(
      0b11111111_11111111_11111111_11111111L,
      Integer.toUnsignedLong(v.getUn32Raw()));
    Assert.assertEquals(1.0, v.getUn32(), 0.0);
  }

  @Test
  public void testSetU64()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 16);
    final JPRACursor1DType<IntegersUnsignedNormalizedType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, IntegersUnsignedNormalizedByteBuffered::newValueWithOffset);
    final IntegersUnsignedNormalizedType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      v.setUn64Raw(index + 1L);
    }

    int offset = 0;
    for (int index = 0; index < 8; ++index) {
      BufferChecks.checkRangeInclusiveIsZero(buf, offset + 0, offset + 7);
      Assert.assertEquals(index + 1L, buf.getLong(offset + 8));
      offset += 16;
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      Assert.assertEquals((long) (index + 1), v.getUn64Raw());
    }
  }

  @Test
  public void testRangeU64()
  {
    final ByteBuffer buf = ByteBuffer.allocate(16);
    final JPRACursor1DType<IntegersUnsignedNormalizedType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, IntegersUnsignedNormalizedByteBuffered::newValueWithOffset);
    final IntegersUnsignedNormalizedType v = c.getElementView();

    v.setUn64Raw(
      0b11111111_11111111_11111111_11111111_11111111_11111111_11111111_11111111L);
    Assert.assertEquals(
      0b11111111_11111111_11111111_11111111_11111111_11111111_11111111_11111111L,
      v.getUn64Raw());

    v.setUn64(0.0);
    Assert.assertEquals(0L, v.getUn64Raw());
    Assert.assertEquals(0.0, v.getUn64(), 0.0);

    v.setUn64(1.0);
    Assert.assertEquals(
      0b11111111_11111111_11111111_11111111_11111111_11111111_11111111_11111111L,
      v.getUn64Raw());
    Assert.assertEquals(1.0, v.getUn64(), 0.0);
  }
}
