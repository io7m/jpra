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
import com.io7m.jpra.tests.compiler.java.generation.code.IntegersSignedNormalizedByteBuffered;
import com.io7m.jpra.tests.compiler.java.generation.code.IntegersSignedNormalizedType;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;

public final class IntegersSignedNormalizedTest
{
  @Test public void testMeta()
  {
    final ByteBuffer buf = ByteBuffer.allocate(1024);
    final JPRACursor1DType<IntegersSignedNormalizedType> c =
      JPRACursor1DByteBufferedUnchecked.newCursor(
        buf, IntegersSignedNormalizedByteBuffered::newValueWithOffset);

    final IntegersSignedNormalizedType v = c.getElementView();
    Assert.assertEquals(16L, (long) v.sizeOctets());

    Assert.assertEquals(0L, (long) v.metaSn8OffsetFromType());
    Assert.assertEquals(2L, (long) v.metaSn16OffsetFromType());
    Assert.assertEquals(4L, (long) v.metaSn32OffsetFromType());
    Assert.assertEquals(8L, (long) v.metaSn64OffsetFromType());

    Assert.assertEquals(0L, (long) v.metaSn8OffsetFromCursor());
    Assert.assertEquals(2L, (long) v.metaSn16OffsetFromCursor());
    Assert.assertEquals(4L, (long) v.metaSn32OffsetFromCursor());
    Assert.assertEquals(8L, (long) v.metaSn64OffsetFromCursor());

    Assert.assertEquals(
      JPRATypeModel.JPRAIntegerSignedNormalized.of(8), v.metaSn8Type());
    Assert.assertEquals(
      JPRATypeModel.JPRAIntegerSignedNormalized.of(16), v.metaSn16Type());
    Assert.assertEquals(
      JPRATypeModel.JPRAIntegerSignedNormalized.of(32), v.metaSn32Type());
    Assert.assertEquals(
      JPRATypeModel.JPRAIntegerSignedNormalized.of(64), v.metaSn64Type());
  }

  @Test public void testSetU8()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 16);
    final JPRACursor1DType<IntegersSignedNormalizedType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, IntegersSignedNormalizedByteBuffered::newValueWithOffset);
    final IntegersSignedNormalizedType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      v.setSn8Raw((byte) (index + 1));
    }

    int offset = 0;
    for (int index = 0; index < 8; ++index) {
      Assert.assertEquals((long) (index + 1), (long) buf.get(offset + 0));
      BufferChecks.checkRangeInclusiveIsZero(buf, offset + 1, offset + 15);
      offset += 16;
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      Assert.assertEquals((long) (index + 1), (long) v.getSn8Raw());
    }
  }

  @Test public void testRangeU8()
  {
    final ByteBuffer buf = ByteBuffer.allocate(16);
    final JPRACursor1DType<IntegersSignedNormalizedType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, IntegersSignedNormalizedByteBuffered::newValueWithOffset);
    final IntegersSignedNormalizedType v = c.getElementView();

    v.setSn8Raw(Byte.MAX_VALUE);
    Assert.assertEquals((long) Byte.MAX_VALUE, (long) v.getSn8Raw());
  }

  @Test public void testSetU16()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 16);
    final JPRACursor1DType<IntegersSignedNormalizedType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, IntegersSignedNormalizedByteBuffered::newValueWithOffset);
    final IntegersSignedNormalizedType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      v.setSn16Raw((short) (index + 1));
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
      Assert.assertEquals((long) (index + 1), (long) v.getSn16Raw());
    }
  }

  @Test public void testRangeU16()
  {
    final ByteBuffer buf = ByteBuffer.allocate(16);
    final JPRACursor1DType<IntegersSignedNormalizedType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, IntegersSignedNormalizedByteBuffered::newValueWithOffset);
    final IntegersSignedNormalizedType v = c.getElementView();

    v.setSn16Raw(Short.MAX_VALUE);
    Assert.assertEquals((long) Short.MAX_VALUE, (long) v.getSn16Raw());
  }

  @Test public void testSetU32()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 16);
    final JPRACursor1DType<IntegersSignedNormalizedType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, IntegersSignedNormalizedByteBuffered::newValueWithOffset);
    final IntegersSignedNormalizedType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      v.setSn32Raw(index + 1);
    }

    int offset = 0;
    for (int index = 0; index < 8; ++index) {
      BufferChecks.checkRangeInclusiveIsZero(buf, offset + 0, offset + 3);
      Assert.assertEquals((long) index + 1L, (long) buf.getInt(offset + 4));
      BufferChecks.checkRangeInclusiveIsZero(buf, offset + 8, offset + 15);
      offset += 16;
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      Assert.assertEquals((long) (index + 1), (long) v.getSn32Raw());
    }
  }

  @Test public void testRangeU32()
  {
    final ByteBuffer buf = ByteBuffer.allocate(16);
    final JPRACursor1DType<IntegersSignedNormalizedType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, IntegersSignedNormalizedByteBuffered::newValueWithOffset);
    final IntegersSignedNormalizedType v = c.getElementView();

    v.setSn32Raw(Integer.MAX_VALUE);
    Assert.assertEquals((long) Integer.MAX_VALUE, (long) v.getSn32Raw());
  }

  @Test public void testSetU64()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 16);
    final JPRACursor1DType<IntegersSignedNormalizedType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, IntegersSignedNormalizedByteBuffered::newValueWithOffset);
    final IntegersSignedNormalizedType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      v.setSn64Raw((long) index + 1L);
    }

    int offset = 0;
    for (int index = 0; index < 8; ++index) {
      BufferChecks.checkRangeInclusiveIsZero(buf, offset + 0, offset + 7);
      Assert.assertEquals((long) index + 1L, buf.getLong(offset + 8));
      offset += 16;
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      Assert.assertEquals((long) (index + 1), v.getSn64Raw());
    }
  }

  @Test public void testRangeU64()
  {
    final ByteBuffer buf = ByteBuffer.allocate(16);
    final JPRACursor1DType<IntegersSignedNormalizedType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, IntegersSignedNormalizedByteBuffered::newValueWithOffset);
    final IntegersSignedNormalizedType v = c.getElementView();

    v.setSn64Raw(Long.MAX_VALUE);
    Assert.assertEquals(Long.MAX_VALUE, v.getSn64Raw());
  }
}
