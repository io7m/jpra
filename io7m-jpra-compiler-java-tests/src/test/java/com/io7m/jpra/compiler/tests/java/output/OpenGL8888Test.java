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

import com.io7m.jpra.compiler.tests.java.generation.code.OpenGL8888ByteBuffered;
import com.io7m.jpra.compiler.tests.java.generation.code.OpenGL8888Type;
import com.io7m.jpra.runtime.java.JPRACursor1DByteBufferedChecked;
import com.io7m.jpra.runtime.java.JPRACursor1DByteBufferedUnchecked;
import com.io7m.jpra.runtime.java.JPRACursor1DType;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;

public final class OpenGL8888Test
{
  @Test
  public void testSize()
  {
    final ByteBuffer buf = ByteBuffer.allocate(1024);
    final JPRACursor1DType<OpenGL8888Type> c =
      JPRACursor1DByteBufferedUnchecked.newCursor(
        buf, OpenGL8888ByteBuffered::newValueWithOffset);

    final OpenGL8888Type v = c.getElementView();
    Assert.assertEquals(4L, (long) v.sizeOctets());
  }

  @Test
  public void testSetR()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 4);
    final JPRACursor1DType<OpenGL8888Type> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, OpenGL8888ByteBuffered::newValueWithOffset);
    final OpenGL8888Type v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      v.setRRaw((int) (byte) (index + 1));
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      Assert.assertEquals((long) (index + 1), (long) v.getRRaw());
      Assert.assertEquals(0L, (long) v.getGRaw());
      Assert.assertEquals(0L, (long) v.getBRaw());
      Assert.assertEquals(0L, (long) v.getARaw());
    }
  }

  @Test
  public void testSetB()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 4);
    final JPRACursor1DType<OpenGL8888Type> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, OpenGL8888ByteBuffered::newValueWithOffset);
    final OpenGL8888Type v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      v.setBRaw((int) (byte) (index + 1));
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      Assert.assertEquals(0L, (long) v.getRRaw());
      Assert.assertEquals(0L, (long) v.getGRaw());
      Assert.assertEquals((long) (index + 1), (long) v.getBRaw());
      Assert.assertEquals(0L, (long) v.getARaw());
    }
  }

  @Test
  public void testSetA()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 4);
    final JPRACursor1DType<OpenGL8888Type> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, OpenGL8888ByteBuffered::newValueWithOffset);
    final OpenGL8888Type v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      v.setARaw((int) (byte) (index + 1));
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      Assert.assertEquals(0L, (long) v.getRRaw());
      Assert.assertEquals(0L, (long) v.getGRaw());
      Assert.assertEquals(0L, (long) v.getBRaw());
      Assert.assertEquals((long) (index + 1), (long) v.getARaw());
    }
  }

  @Test
  public void testRangeR()
  {
    final ByteBuffer buf = ByteBuffer.allocate(16);
    final JPRACursor1DType<OpenGL8888Type> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, OpenGL8888ByteBuffered::newValueWithOffset);
    final OpenGL8888Type v = c.getElementView();

    v.setRRaw((int) (byte) 0b11111111);
    Assert.assertEquals(0b11111111L, (long) v.getRRaw());

    v.setR(0.0);
    Assert.assertEquals(0L, (long) v.getRRaw());
    Assert.assertEquals(0.0, v.getR(), 0.0);

    v.setR(1.0);
    Assert.assertEquals(0b11111111L, (long) v.getRRaw());
    Assert.assertEquals(1.0, v.getR(), 0.0);
  }

  @Test
  public void testRangeB()
  {
    final ByteBuffer buf = ByteBuffer.allocate(16);
    final JPRACursor1DType<OpenGL8888Type> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, OpenGL8888ByteBuffered::newValueWithOffset);
    final OpenGL8888Type v = c.getElementView();

    v.setBRaw((int) (byte) 0b11111111);
    Assert.assertEquals(0b11111111L, (long) v.getBRaw());

    v.setB(0.0);
    Assert.assertEquals(0L, (long) v.getBRaw());
    Assert.assertEquals(0.0, v.getB(), 0.0);

    v.setB(1.0);
    Assert.assertEquals(0b11111111L, (long) v.getBRaw());
    Assert.assertEquals(1.0, v.getB(), 0.0);
  }

  @Test
  public void testRangeA()
  {
    final ByteBuffer buf = ByteBuffer.allocate(16);
    final JPRACursor1DType<OpenGL8888Type> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, OpenGL8888ByteBuffered::newValueWithOffset);
    final OpenGL8888Type v = c.getElementView();

    v.setARaw((int) (byte) 0b11111111);
    Assert.assertEquals(0b11111111L, (long) v.getARaw());

    v.setA(0.0);
    Assert.assertEquals(0L, (long) v.getARaw());
    Assert.assertEquals(0.0, v.getA(), 0.0);

    v.setA(1.0);
    Assert.assertEquals(0b11111111L, (long) v.getARaw());
    Assert.assertEquals(1.0, v.getA(), 0.0);
  }

  @Test
  public void testSetG()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 4);
    final JPRACursor1DType<OpenGL8888Type> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, OpenGL8888ByteBuffered::newValueWithOffset);
    final OpenGL8888Type v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      v.setGRaw((int) (byte) (index + 1));
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      Assert.assertEquals(0L, (long) v.getRRaw());
      Assert.assertEquals((long) (index + 1), (long) v.getGRaw());
      Assert.assertEquals(0L, (long) v.getBRaw());
      Assert.assertEquals(0L, (long) v.getARaw());
    }
  }

  @Test
  public void testRangeG()
  {
    final ByteBuffer buf = ByteBuffer.allocate(16);
    final JPRACursor1DType<OpenGL8888Type> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, OpenGL8888ByteBuffered::newValueWithOffset);
    final OpenGL8888Type v = c.getElementView();

    v.setGRaw((int) (byte) 0b11111111);
    Assert.assertEquals(0b11111111L, (long) v.getGRaw());

    v.setG(0.0);
    Assert.assertEquals(0L, (long) v.getGRaw());
    Assert.assertEquals(0.0, v.getG(), 0.0);

    v.setG(1.0);
    Assert.assertEquals(0b11111111L, (long) v.getGRaw());
    Assert.assertEquals(1.0, v.getG(), 0.0);
  }

  @Test
  public void testStructureRGBA()
  {
    final ByteBuffer buf = ByteBuffer.allocate(16);
    final JPRACursor1DType<OpenGL8888Type> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, OpenGL8888ByteBuffered::newValueWithOffset);
    final OpenGL8888Type v = c.getElementView();

    v.setR(0.0);
    v.setG(0.0);
    v.setB(0.0);
    v.setA(0.0);
    Assert.assertEquals(0x00L, Byte.toUnsignedLong(buf.get(0)));
    Assert.assertEquals(0x00L, Byte.toUnsignedLong(buf.get(1)));
    Assert.assertEquals(0x00L, Byte.toUnsignedLong(buf.get(2)));
    Assert.assertEquals(0x00L, Byte.toUnsignedLong(buf.get(3)));

    v.setR(1.0);
    v.setG(0.0);
    v.setB(0.0);
    v.setA(0.0);
    Assert.assertEquals(0xffL, Byte.toUnsignedLong(buf.get(0)));
    Assert.assertEquals(0x00L, Byte.toUnsignedLong(buf.get(1)));
    Assert.assertEquals(0x00L, Byte.toUnsignedLong(buf.get(2)));
    Assert.assertEquals(0x00L, Byte.toUnsignedLong(buf.get(3)));

    v.setR(0.0);
    v.setG(1.0);
    v.setB(0.0);
    v.setA(0.0);
    Assert.assertEquals(0x00L, Byte.toUnsignedLong(buf.get(0)));
    Assert.assertEquals(0xffL, Byte.toUnsignedLong(buf.get(1)));
    Assert.assertEquals(0x00L, Byte.toUnsignedLong(buf.get(2)));
    Assert.assertEquals(0x00L, Byte.toUnsignedLong(buf.get(3)));

    v.setR(0.0);
    v.setG(0.0);
    v.setB(1.0);
    v.setA(0.0);
    Assert.assertEquals(0x00L, Byte.toUnsignedLong(buf.get(0)));
    Assert.assertEquals(0x00L, Byte.toUnsignedLong(buf.get(1)));
    Assert.assertEquals(0xffL, Byte.toUnsignedLong(buf.get(2)));
    Assert.assertEquals(0x00L, Byte.toUnsignedLong(buf.get(3)));

    v.setR(0.0);
    v.setG(0.0);
    v.setB(0.0);
    v.setA(1.0);
    Assert.assertEquals(0x00L, Byte.toUnsignedLong(buf.get(0)));
    Assert.assertEquals(0x00L, Byte.toUnsignedLong(buf.get(1)));
    Assert.assertEquals(0x00L, Byte.toUnsignedLong(buf.get(2)));
    Assert.assertEquals(0xffL, Byte.toUnsignedLong(buf.get(3)));

    v.set(0.0, 0.0, 0.0, 0.0);
    Assert.assertEquals(0x00L, Byte.toUnsignedLong(buf.get(0)));
    Assert.assertEquals(0x00L, Byte.toUnsignedLong(buf.get(1)));
    Assert.assertEquals(0x00L, Byte.toUnsignedLong(buf.get(2)));
    Assert.assertEquals(0x00L, Byte.toUnsignedLong(buf.get(3)));

    v.set(1.0, 0.0, 0.0, 0.0);
    Assert.assertEquals(0xffL, Byte.toUnsignedLong(buf.get(0)));
    Assert.assertEquals(0x00L, Byte.toUnsignedLong(buf.get(1)));
    Assert.assertEquals(0x00L, Byte.toUnsignedLong(buf.get(2)));
    Assert.assertEquals(0x00L, Byte.toUnsignedLong(buf.get(3)));

    v.set(0.0, 1.0, 0.0, 0.0);
    Assert.assertEquals(0x00L, Byte.toUnsignedLong(buf.get(0)));
    Assert.assertEquals(0xffL, Byte.toUnsignedLong(buf.get(1)));
    Assert.assertEquals(0x00L, Byte.toUnsignedLong(buf.get(2)));
    Assert.assertEquals(0x00L, Byte.toUnsignedLong(buf.get(3)));

    v.set(0.0, 0.0, 1.0, 0.0);
    Assert.assertEquals(0x00L, Byte.toUnsignedLong(buf.get(0)));
    Assert.assertEquals(0x00L, Byte.toUnsignedLong(buf.get(1)));
    Assert.assertEquals(0xffL, Byte.toUnsignedLong(buf.get(2)));
    Assert.assertEquals(0x00L, Byte.toUnsignedLong(buf.get(3)));

    v.set(0.0, 0.0, 0.0, 1.0);
    Assert.assertEquals(0x00L, Byte.toUnsignedLong(buf.get(0)));
    Assert.assertEquals(0x00L, Byte.toUnsignedLong(buf.get(1)));
    Assert.assertEquals(0x00L, Byte.toUnsignedLong(buf.get(2)));
    Assert.assertEquals(0xffL, Byte.toUnsignedLong(buf.get(3)));
  }
}
