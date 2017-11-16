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

import com.io7m.ieee754b16.Binary16;
import com.io7m.jpra.runtime.java.JPRACursor1DByteBufferedUnchecked;
import com.io7m.jpra.runtime.java.JPRACursor1DType;
import com.io7m.jpra.runtime.java.JPRATypeModel;
import com.io7m.jpra.tests.compiler.java.generation.code.FloatsByteBuffered;
import com.io7m.jpra.tests.compiler.java.generation.code.FloatsType;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;

public final class FloatsTest
{
  @Test
  public void testMeta()
  {
    final ByteBuffer buf = ByteBuffer.allocate(1024);
    final JPRACursor1DType<FloatsType> c =
      JPRACursor1DByteBufferedUnchecked.newCursor(
        buf, FloatsByteBuffered::newValueWithOffset);

    final FloatsType v = c.getElementView();
    Assert.assertEquals(16L, (long) v.sizeOctets());

    Assert.assertEquals(0L, (long) v.metaF16OffsetFromType());
    Assert.assertEquals(2L, (long) v.metaF32OffsetFromType());
    Assert.assertEquals(6L, (long) v.metaF64OffsetFromType());

    Assert.assertEquals(0L, (long) v.metaF16OffsetFromCursor());
    Assert.assertEquals(2L, (long) v.metaF32OffsetFromCursor());
    Assert.assertEquals(6L, (long) v.metaF64OffsetFromCursor());

    Assert.assertEquals(
      JPRATypeModel.JPRAFloat.of(16), v.metaF16Type());
    Assert.assertEquals(
      JPRATypeModel.JPRAFloat.of(32), v.metaF32Type());
    Assert.assertEquals(
      JPRATypeModel.JPRAFloat.of(64), v.metaF64Type());
  }

  @Test
  public void testSetF16()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 16);
    final JPRACursor1DType<FloatsType> c =
      JPRACursor1DByteBufferedUnchecked.newCursor(
        buf, FloatsByteBuffered::newValueWithOffset);
    final FloatsType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      v.setF16((double) index + 1.0);
    }

    int offset = 0;
    for (int index = 0; index < 8; ++index) {
      Assert.assertEquals(
        ((double) index + 1.0), Binary16.unpackDouble(
          buf.getChar(offset + 0)),
        0.001);

      Assert.assertEquals(0L, (long) buf.get(offset + 2));
      Assert.assertEquals(0L, (long) buf.get(offset + 3));
      Assert.assertEquals(0L, (long) buf.get(offset + 4));
      Assert.assertEquals(0L, (long) buf.get(offset + 5));
      Assert.assertEquals(0L, (long) buf.get(offset + 6));
      Assert.assertEquals(0L, (long) buf.get(offset + 7));
      Assert.assertEquals(0L, (long) buf.get(offset + 8));
      Assert.assertEquals(0L, (long) buf.get(offset + 9));
      Assert.assertEquals(0L, (long) buf.get(offset + 10));
      Assert.assertEquals(0L, (long) buf.get(offset + 11));
      Assert.assertEquals(0L, (long) buf.get(offset + 12));
      Assert.assertEquals(0L, (long) buf.get(offset + 13));
      Assert.assertEquals(0L, (long) buf.get(offset + 14));
      Assert.assertEquals(0L, (long) buf.get(offset + 15));
      offset += 16;
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      Assert.assertEquals((double) index + 1.0, v.getF16(), 0.001);
    }
  }

  @Test
  public void testSetF32()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 16);
    final JPRACursor1DType<FloatsType> c =
      JPRACursor1DByteBufferedUnchecked.newCursor(
        buf, FloatsByteBuffered::newValueWithOffset);
    final FloatsType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      v.setF32((float) index + 1.0f);
    }

    int offset = 0;
    for (int index = 0; index < 8; ++index) {
      Assert.assertEquals(0L, (long) buf.get(offset + 0));
      Assert.assertEquals(0L, (long) buf.get(offset + 1));
      Assert.assertEquals(
        (float) index + 1.0f,
        (float) (long) buf.getFloat(offset + 2), 0.0f);

      Assert.assertEquals(0L, (long) buf.get(offset + 6));
      Assert.assertEquals(0L, (long) buf.get(offset + 7));
      Assert.assertEquals(0L, (long) buf.get(offset + 8));
      Assert.assertEquals(0L, (long) buf.get(offset + 9));
      Assert.assertEquals(0L, (long) buf.get(offset + 10));
      Assert.assertEquals(0L, (long) buf.get(offset + 11));
      Assert.assertEquals(0L, (long) buf.get(offset + 12));
      Assert.assertEquals(0L, (long) buf.get(offset + 13));
      Assert.assertEquals(0L, (long) buf.get(offset + 14));
      Assert.assertEquals(0L, (long) buf.get(offset + 15));
      offset += 16;
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      Assert.assertEquals((float) index + 1.0f, v.getF32(), 0.0f);
    }
  }

  @Test
  public void testSetF64()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 16);
    final JPRACursor1DType<FloatsType> c =
      JPRACursor1DByteBufferedUnchecked.newCursor(
        buf, FloatsByteBuffered::newValueWithOffset);
    final FloatsType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      v.setF64((double) ((float) index + 1.0f));
    }

    int offset = 0;
    for (int index = 0; index < 8; ++index) {
      Assert.assertEquals(0L, (long) buf.get(offset + 0));
      Assert.assertEquals(0L, (long) buf.get(offset + 1));
      Assert.assertEquals(0L, (long) buf.get(offset + 2));
      Assert.assertEquals(0L, (long) buf.get(offset + 3));
      Assert.assertEquals(0L, (long) buf.get(offset + 4));
      Assert.assertEquals(0L, (long) buf.get(offset + 5));
      Assert.assertEquals(
        (double) index + 1.0,
        (double) (long) buf.getDouble(offset + 6), 0.0);
      Assert.assertEquals(0L, (long) buf.get(offset + 14));
      Assert.assertEquals(0L, (long) buf.get(offset + 15));
      offset += 16;
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      Assert.assertEquals((double) ((float) index + 1.0f), v.getF64(), 0.0);
    }
  }
}
