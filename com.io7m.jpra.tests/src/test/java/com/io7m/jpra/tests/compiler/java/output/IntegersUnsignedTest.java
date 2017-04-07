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
import com.io7m.jpra.tests.compiler.java.generation.code.IntegersUnsignedByteBuffered;
import com.io7m.jpra.tests.compiler.java.generation.code.IntegersUnsignedType;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;

public final class IntegersUnsignedTest
{
  @Test
  public void testMeta()
  {
    final ByteBuffer buf = ByteBuffer.allocate(1024);
    final JPRACursor1DType<IntegersUnsignedType> c =
      JPRACursor1DByteBufferedUnchecked.newCursor(
        buf, IntegersUnsignedByteBuffered::newValueWithOffset);

    final IntegersUnsignedType v = c.getElementView();
    Assert.assertEquals(16L, (long) v.sizeOctets());

    Assert.assertEquals(0L, (long) v.metaU8OffsetFromType());
    Assert.assertEquals(2L, (long) v.metaU16OffsetFromType());
    Assert.assertEquals(4L, (long) v.metaU32OffsetFromType());
    Assert.assertEquals(8L, (long) v.metaU64OffsetFromType());

    Assert.assertEquals(0L, (long) v.metaU8OffsetFromCursor());
    Assert.assertEquals(2L, (long) v.metaU16OffsetFromCursor());
    Assert.assertEquals(4L, (long) v.metaU32OffsetFromCursor());
    Assert.assertEquals(8L, (long) v.metaU64OffsetFromCursor());

    Assert.assertEquals(
      JPRATypeModel.JPRAIntegerUnsigned.of(8), v.metaU8Type());
    Assert.assertEquals(
      JPRATypeModel.JPRAIntegerUnsigned.of(16), v.metaU16Type());
    Assert.assertEquals(
      JPRATypeModel.JPRAIntegerUnsigned.of(32), v.metaU32Type());
    Assert.assertEquals(
      JPRATypeModel.JPRAIntegerUnsigned.of(64), v.metaU64Type());
  }

  @Test
  public void testSetU8()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 16);
    final JPRACursor1DType<IntegersUnsignedType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, IntegersUnsignedByteBuffered::newValueWithOffset);
    final IntegersUnsignedType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      v.setU8((byte) (index + 1));
    }

    int offset = 0;
    for (int index = 0; index < 8; ++index) {
      Assert.assertEquals((long) (index + 1), (long) buf.get(offset + 0));
      BufferChecks.checkRangeInclusiveIsZero(buf, offset + 1, offset + 15);
      offset += 16;
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      Assert.assertEquals((long) (index + 1), (long) v.getU8());
    }
  }

  @Test
  public void testRangeU8()
  {
    final ByteBuffer buf = ByteBuffer.allocate(16);
    final JPRACursor1DType<IntegersUnsignedType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, IntegersUnsignedByteBuffered::newValueWithOffset);
    final IntegersUnsignedType v = c.getElementView();

    v.setU8((byte) 0b11111111);
    Assert.assertEquals(0b11111111L, Byte.toUnsignedLong(v.getU8()));
  }

  @Test
  public void testSetU16()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 16);
    final JPRACursor1DType<IntegersUnsignedType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, IntegersUnsignedByteBuffered::newValueWithOffset);
    final IntegersUnsignedType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      v.setU16((short) (index + 1));
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
      Assert.assertEquals((long) (index + 1), (long) v.getU16());
    }
  }

  @Test
  public void testRangeU16()
  {
    final ByteBuffer buf = ByteBuffer.allocate(16);
    final JPRACursor1DType<IntegersUnsignedType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, IntegersUnsignedByteBuffered::newValueWithOffset);
    final IntegersUnsignedType v = c.getElementView();

    v.setU16((short) 0b11111111_11111111);
    Assert.assertEquals(0b11111111_11111111L, Short.toUnsignedLong(v.getU16()));
  }

  @Test
  public void testSetU32()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 16);
    final JPRACursor1DType<IntegersUnsignedType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, IntegersUnsignedByteBuffered::newValueWithOffset);
    final IntegersUnsignedType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      v.setU32(index + 1);
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
      Assert.assertEquals((long) (index + 1), (long) v.getU32());
    }
  }

  @Test
  public void testRangeU32()
  {
    final ByteBuffer buf = ByteBuffer.allocate(16);
    final JPRACursor1DType<IntegersUnsignedType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, IntegersUnsignedByteBuffered::newValueWithOffset);
    final IntegersUnsignedType v = c.getElementView();

    v.setU32(0b11111111_11111111_11111111_11111111);
    Assert.assertEquals(
      0b11111111_11111111_11111111_11111111L,
      Integer.toUnsignedLong(v.getU32()));
  }

  @Test
  public void testSetU64()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 16);
    final JPRACursor1DType<IntegersUnsignedType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, IntegersUnsignedByteBuffered::newValueWithOffset);
    final IntegersUnsignedType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      v.setU64(index + 1L);
    }

    int offset = 0;
    for (int index = 0; index < 8; ++index) {
      BufferChecks.checkRangeInclusiveIsZero(buf, offset + 0, offset + 7);
      Assert.assertEquals(index + 1L, buf.getLong(offset + 8));
      offset += 16;
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      Assert.assertEquals((long) (index + 1), v.getU64());
    }
  }

  @Test
  public void testRangeU64()
  {
    final ByteBuffer buf = ByteBuffer.allocate(16);
    final JPRACursor1DType<IntegersUnsignedType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, IntegersUnsignedByteBuffered::newValueWithOffset);
    final IntegersUnsignedType v = c.getElementView();

    v.setU64(
      0b11111111_11111111_11111111_11111111_11111111_11111111_11111111_11111111L);
    Assert.assertEquals(
      0b11111111_11111111_11111111_11111111_11111111_11111111_11111111_11111111L,
      v.getU64());
  }
}
