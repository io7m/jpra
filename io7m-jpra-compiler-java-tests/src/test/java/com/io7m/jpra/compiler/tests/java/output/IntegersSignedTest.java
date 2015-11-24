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
  .IntegersSignedByteBuffered;
import com.io7m.jpra.compiler.tests.java.generation.code.IntegersSignedType;
import com.io7m.jpra.runtime.java.JPRACursor1DByteBufferedChecked;
import com.io7m.jpra.runtime.java.JPRACursor1DByteBufferedUnchecked;
import com.io7m.jpra.runtime.java.JPRACursor1DType;
import com.io7m.jpra.runtime.java.JPRATypeModel;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;

public final class IntegersSignedTest
{
  @Test public void testMeta()
  {
    final ByteBuffer buf = ByteBuffer.allocate(1024);
    final JPRACursor1DType<IntegersSignedType> c =
      JPRACursor1DByteBufferedUnchecked.newCursor(
        buf, IntegersSignedByteBuffered::newValueWithOffset);

    final IntegersSignedType v = c.getElementView();
    Assert.assertEquals(16L, (long) v.sizeOctets());

    Assert.assertEquals(0L, (long) v.metaS8OffsetFromType());
    Assert.assertEquals(2L, (long) v.metaS16OffsetFromType());
    Assert.assertEquals(4L, (long) v.metaS32OffsetFromType());
    Assert.assertEquals(8L, (long) v.metaS64OffsetFromType());

    Assert.assertEquals(0L, (long) v.metaS8OffsetFromCursor());
    Assert.assertEquals(2L, (long) v.metaS16OffsetFromCursor());
    Assert.assertEquals(4L, (long) v.metaS32OffsetFromCursor());
    Assert.assertEquals(8L, (long) v.metaS64OffsetFromCursor());

    Assert.assertEquals(
      JPRATypeModel.JPRAIntegerSigned.of(8), v.metaS8Type());
    Assert.assertEquals(
      JPRATypeModel.JPRAIntegerSigned.of(16), v.metaS16Type());
    Assert.assertEquals(
      JPRATypeModel.JPRAIntegerSigned.of(32), v.metaS32Type());
    Assert.assertEquals(
      JPRATypeModel.JPRAIntegerSigned.of(64), v.metaS64Type());
  }

  @Test public void testSetU8()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 16);
    final JPRACursor1DType<IntegersSignedType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, IntegersSignedByteBuffered::newValueWithOffset);
    final IntegersSignedType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      v.setS8((byte) (index + 1));
    }

    int offset = 0;
    for (int index = 0; index < 8; ++index) {
      Assert.assertEquals((long) (index + 1), (long) buf.get(offset + 0));
      BufferChecks.checkRangeInclusiveIsZero(buf, offset + 1, offset + 15);
      offset += 16;
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      Assert.assertEquals((long) (index + 1), (long) v.getS8());
    }
  }

  @Test public void testRangeU8()
  {
    final ByteBuffer buf = ByteBuffer.allocate(16);
    final JPRACursor1DType<IntegersSignedType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, IntegersSignedByteBuffered::newValueWithOffset);
    final IntegersSignedType v = c.getElementView();

    v.setS8(Byte.MAX_VALUE);
    Assert.assertEquals(Byte.MAX_VALUE, v.getS8());
  }

  @Test public void testSetU16()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 16);
    final JPRACursor1DType<IntegersSignedType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, IntegersSignedByteBuffered::newValueWithOffset);
    final IntegersSignedType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      v.setS16((short) (index + 1));
    }

    int offset = 0;
    for (int index = 0; index < 8; ++index) {
      BufferChecks.checkRangeInclusiveIsZero(buf, 0, 1);
      Assert.assertEquals((long) (index + 1), (long) buf.getShort(offset + 2));
      BufferChecks.checkRangeInclusiveIsZero(buf, 4, 15);
      offset += 16;
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      Assert.assertEquals((long) (index + 1), (long) v.getS16());
    }
  }

  @Test public void testRangeU16()
  {
    final ByteBuffer buf = ByteBuffer.allocate(16);
    final JPRACursor1DType<IntegersSignedType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, IntegersSignedByteBuffered::newValueWithOffset);
    final IntegersSignedType v = c.getElementView();

    v.setS16(Short.MAX_VALUE);
    Assert.assertEquals(Short.MAX_VALUE, v.getS16());
  }

  @Test public void testSetU32()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 16);
    final JPRACursor1DType<IntegersSignedType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, IntegersSignedByteBuffered::newValueWithOffset);
    final IntegersSignedType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      v.setS32(index + 1);
    }

    int offset = 0;
    for (int index = 0; index < 8; ++index) {
      BufferChecks.checkRangeInclusiveIsZero(buf, 0, 3);
      Assert.assertEquals(index + 1L, (long) buf.getInt(offset + 4));
      BufferChecks.checkRangeInclusiveIsZero(buf, 8, 15);
      offset += 16;
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      Assert.assertEquals((long) (index + 1), (long) v.getS32());
    }
  }

  @Test public void testRangeU32()
  {
    final ByteBuffer buf = ByteBuffer.allocate(16);
    final JPRACursor1DType<IntegersSignedType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, IntegersSignedByteBuffered::newValueWithOffset);
    final IntegersSignedType v = c.getElementView();

    v.setS32(Integer.MAX_VALUE);
    Assert.assertEquals(Integer.MAX_VALUE, v.getS32());
  }

  @Test public void testSetU64()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 16);
    final JPRACursor1DType<IntegersSignedType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, IntegersSignedByteBuffered::newValueWithOffset);
    final IntegersSignedType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      v.setS64(index + 1L);
    }

    int offset = 0;
    for (int index = 0; index < 8; ++index) {
      BufferChecks.checkRangeInclusiveIsZero(buf, 0, 7);
      Assert.assertEquals(index + 1L, buf.getLong(offset + 8));
      offset += 16;
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      Assert.assertEquals((long) (index + 1), v.getS64());
    }
  }

  @Test public void testRangeU64()
  {
    final ByteBuffer buf = ByteBuffer.allocate(16);
    final JPRACursor1DType<IntegersSignedType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, IntegersSignedByteBuffered::newValueWithOffset);
    final IntegersSignedType v = c.getElementView();

    v.setS64(Long.MAX_VALUE);
    Assert.assertEquals(Long.MAX_VALUE, v.getS64());
  }
}
