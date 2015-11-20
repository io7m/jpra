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

import com.io7m.jpra.compiler.tests.java.generation.code.MatricesByteBuffered;
import com.io7m.jpra.compiler.tests.java.generation.code.MatricesType;
import com.io7m.jpra.runtime.java.JPRACursor1DByteBufferedChecked;
import com.io7m.jpra.runtime.java.JPRACursor1DByteBufferedUnchecked;
import com.io7m.jpra.runtime.java.JPRACursor1DType;
import com.io7m.jtensors.Matrix2x2DType;
import com.io7m.jtensors.Matrix2x2FType;
import com.io7m.jtensors.Matrix3x3DType;
import com.io7m.jtensors.Matrix3x3FType;
import com.io7m.jtensors.Matrix4x4DType;
import com.io7m.jtensors.Matrix4x4FType;
import com.io7m.jtensors.MatrixReadable2x2DType;
import com.io7m.jtensors.MatrixReadable2x2FType;
import com.io7m.jtensors.MatrixReadable3x3DType;
import com.io7m.jtensors.MatrixReadable3x3FType;
import com.io7m.jtensors.MatrixReadable4x4DType;
import com.io7m.jtensors.MatrixReadable4x4FType;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;

public final class MatricesTest
{
  private static void check2x2DZero(final MatrixReadable2x2DType m)
  {
    for (int row = 0; row < 2; ++row) {
      for (int col = 0; col < 2; ++col) {
        Assert.assertEquals(0.0, m.getRowColumnD(row, col), 0.0);
      }
    }
  }

  private static void check3x3DZero(final MatrixReadable3x3DType m)
  {
    for (int row = 0; row < 3; ++row) {
      for (int col = 0; col < 3; ++col) {
        Assert.assertEquals(0.0, m.getRowColumnD(row, col), 0.0);
      }
    }
  }

  private static void check4x4DZero(final MatrixReadable4x4DType m)
  {
    for (int row = 0; row < 4; ++row) {
      for (int col = 0; col < 4; ++col) {
        Assert.assertEquals(0.0, m.getRowColumnD(row, col), 0.0);
      }
    }
  }

  private static void check2x2FZero(final MatrixReadable2x2FType m)
  {
    for (int row = 0; row < 2; ++row) {
      for (int col = 0; col < 2; ++col) {
        Assert.assertEquals(0.0f, m.getRowColumnF(row, col), 0.0f);
      }
    }
  }

  private static void check3x3FZero(final MatrixReadable3x3FType m)
  {
    for (int row = 0; row < 3; ++row) {
      for (int col = 0; col < 3; ++col) {
        Assert.assertEquals(0.0f, m.getRowColumnF(row, col), 0.0f);
      }
    }
  }

  private static void check4x4FZero(final MatrixReadable4x4FType m)
  {
    for (int row = 0; row < 4; ++row) {
      for (int col = 0; col < 4; ++col) {
        Assert.assertEquals(0.0f, m.getRowColumnF(row, col), 0.0f);
      }
    }
  }

  @Test public void testSize()
  {
    final ByteBuffer buf = ByteBuffer.allocate(1024);
    final JPRACursor1DType<MatricesType> c =
      JPRACursor1DByteBufferedUnchecked.newCursor(
        buf,
        MatricesByteBuffered::newValueWithOffset);

    final MatricesType v = c.getElementView();
    Assert.assertEquals(348L, (long) v.sizeOctets());
  }

  @Test public void testV4x4D()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 348);
    final JPRACursor1DType<MatricesType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf,
        MatricesByteBuffered::newValueWithOffset);
    final MatricesType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      final Matrix4x4DType k = v.getM4dWritable();
      k.setR0C0D((index * 10.0) + 1.0);
      k.setR1C0D((index * 10.0) + 2.0);
      k.setR2C0D((index * 10.0) + 3.0);
      k.setR3C0D((index * 10.0) + 4.0);

      k.setR0C1D((index * 10.0) + 10.0);
      k.setR1C1D((index * 10.0) + 20.0);
      k.setR2C1D((index * 10.0) + 30.0);
      k.setR3C1D((index * 10.0) + 40.0);

      k.setR0C2D((index * 10.0) + 100.0);
      k.setR1C2D((index * 10.0) + 200.0);
      k.setR2C2D((index * 10.0) + 300.0);
      k.setR3C2D((index * 10.0) + 400.0);

      k.setR0C3D((index * 10.0) + 1000.0);
      k.setR1C3D((index * 10.0) + 2000.0);
      k.setR2C3D((index * 10.0) + 3000.0);
      k.setR3C3D((index * 10.0) + 4000.0);
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);

      MatricesTest.check2x2DZero(v.getM2dReadable());
      MatricesTest.check2x2FZero(v.getM2fReadable());
      MatricesTest.check3x3DZero(v.getM3dReadable());
      MatricesTest.check3x3FZero(v.getM3fReadable());
      MatricesTest.check4x4FZero(v.getM4fReadable());
      // check4x4DZero(v.getM4dReadable());

      final MatrixReadable4x4DType k = v.getM4dReadable();

      Assert.assertEquals((index * 10.0) + 1.0, k.getR0C0D(), 0.0);
      Assert.assertEquals((index * 10.0) + 2.0, k.getR1C0D(), 0.0);
      Assert.assertEquals((index * 10.0) + 3.0, k.getR2C0D(), 0.0);
      Assert.assertEquals((index * 10.0) + 4.0, k.getR3C0D(), 0.0);

      Assert.assertEquals((index * 10.0) + 10.0, k.getR0C1D(), 0.0);
      Assert.assertEquals((index * 10.0) + 20.0, k.getR1C1D(), 0.0);
      Assert.assertEquals((index * 10.0) + 30.0, k.getR2C1D(), 0.0);
      Assert.assertEquals((index * 10.0) + 40.0, k.getR3C1D(), 0.0);

      Assert.assertEquals((index * 10.0) + 100.0, k.getR0C2D(), 0.0);
      Assert.assertEquals((index * 10.0) + 200.0, k.getR1C2D(), 0.0);
      Assert.assertEquals((index * 10.0) + 300.0, k.getR2C2D(), 0.0);
      Assert.assertEquals((index * 10.0) + 400.0, k.getR3C2D(), 0.0);

      Assert.assertEquals((index * 10.0) + 1000.0, k.getR0C3D(), 0.0);
      Assert.assertEquals((index * 10.0) + 2000.0, k.getR1C3D(), 0.0);
      Assert.assertEquals((index * 10.0) + 3000.0, k.getR2C3D(), 0.0);
      Assert.assertEquals((index * 10.0) + 4000.0, k.getR3C3D(), 0.0);
    }
  }

  @Test public void testV4x4F()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 348);
    final JPRACursor1DType<MatricesType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf,
        MatricesByteBuffered::newValueWithOffset);
    final MatricesType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      final Matrix4x4FType k = v.getM4fWritable();
      k.setR0C0F((index * 10.0f) + 1.0f);
      k.setR1C0F((index * 10.0f) + 2.0f);
      k.setR2C0F((index * 10.0f) + 3.0f);
      k.setR3C0F((index * 10.0f) + 4.0f);

      k.setR0C1F((index * 10.0f) + 10.0f);
      k.setR1C1F((index * 10.0f) + 20.0f);
      k.setR2C1F((index * 10.0f) + 30.0f);
      k.setR3C1F((index * 10.0f) + 40.0f);

      k.setR0C2F((index * 10.0f) + 100.0f);
      k.setR1C2F((index * 10.0f) + 200.0f);
      k.setR2C2F((index * 10.0f) + 300.0f);
      k.setR3C2F((index * 10.0f) + 400.0f);

      k.setR0C3F((index * 10.0f) + 1000.0f);
      k.setR1C3F((index * 10.0f) + 2000.0f);
      k.setR2C3F((index * 10.0f) + 3000.0f);
      k.setR3C3F((index * 10.0f) + 4000.0f);
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);

      MatricesTest.check2x2DZero(v.getM2dReadable());
      MatricesTest.check2x2FZero(v.getM2fReadable());
      MatricesTest.check3x3DZero(v.getM3dReadable());
      MatricesTest.check3x3FZero(v.getM3fReadable());
      // MatricesTest.check4x4FZero(v.getM4fReadable());
      check4x4DZero(v.getM4dReadable());

      final MatrixReadable4x4FType k = v.getM4fReadable();

      Assert.assertEquals((index * 10.0) + 1.0, k.getR0C0F(), 0.0);
      Assert.assertEquals((index * 10.0) + 2.0, k.getR1C0F(), 0.0);
      Assert.assertEquals((index * 10.0) + 3.0, k.getR2C0F(), 0.0);
      Assert.assertEquals((index * 10.0) + 4.0, k.getR3C0F(), 0.0);

      Assert.assertEquals((index * 10.0) + 10.0, k.getR0C1F(), 0.0);
      Assert.assertEquals((index * 10.0) + 20.0, k.getR1C1F(), 0.0);
      Assert.assertEquals((index * 10.0) + 30.0, k.getR2C1F(), 0.0);
      Assert.assertEquals((index * 10.0) + 40.0, k.getR3C1F(), 0.0);

      Assert.assertEquals((index * 10.0) + 100.0, k.getR0C2F(), 0.0);
      Assert.assertEquals((index * 10.0) + 200.0, k.getR1C2F(), 0.0);
      Assert.assertEquals((index * 10.0) + 300.0, k.getR2C2F(), 0.0);
      Assert.assertEquals((index * 10.0) + 400.0, k.getR3C2F(), 0.0);

      Assert.assertEquals((index * 10.0) + 1000.0, k.getR0C3F(), 0.0);
      Assert.assertEquals((index * 10.0) + 2000.0, k.getR1C3F(), 0.0);
      Assert.assertEquals((index * 10.0) + 3000.0, k.getR2C3F(), 0.0);
      Assert.assertEquals((index * 10.0) + 4000.0, k.getR3C3F(), 0.0);
    }
  }

  @Test public void testV3x3D()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 348);
    final JPRACursor1DType<MatricesType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf,
        MatricesByteBuffered::newValueWithOffset);
    final MatricesType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      final Matrix3x3DType k = v.getM3dWritable();
      k.setR0C0D((index * 10.0) + 1.0);
      k.setR1C0D((index * 10.0) + 2.0);
      k.setR2C0D((index * 10.0) + 3.0);

      k.setR0C1D((index * 10.0) + 10.0);
      k.setR1C1D((index * 10.0) + 20.0);
      k.setR2C1D((index * 10.0) + 30.0);

      k.setR0C2D((index * 10.0) + 100.0);
      k.setR1C2D((index * 10.0) + 200.0);
      k.setR2C2D((index * 10.0) + 300.0);
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);

      MatricesTest.check2x2DZero(v.getM2dReadable());
      MatricesTest.check2x2FZero(v.getM2fReadable());
      // MatricesTest.check3x3DZero(v.getM3dReadable());
      MatricesTest.check3x3FZero(v.getM3fReadable());
      MatricesTest.check4x4FZero(v.getM4fReadable());
      check4x4DZero(v.getM4dReadable());

      final MatrixReadable3x3DType k = v.getM3dReadable();

      Assert.assertEquals((index * 10.0) + 1.0, k.getR0C0D(), 0.0);
      Assert.assertEquals((index * 10.0) + 2.0, k.getR1C0D(), 0.0);
      Assert.assertEquals((index * 10.0) + 3.0, k.getR2C0D(), 0.0);

      Assert.assertEquals((index * 10.0) + 10.0, k.getR0C1D(), 0.0);
      Assert.assertEquals((index * 10.0) + 20.0, k.getR1C1D(), 0.0);
      Assert.assertEquals((index * 10.0) + 30.0, k.getR2C1D(), 0.0);

      Assert.assertEquals((index * 10.0) + 100.0, k.getR0C2D(), 0.0);
      Assert.assertEquals((index * 10.0) + 200.0, k.getR1C2D(), 0.0);
      Assert.assertEquals((index * 10.0) + 300.0, k.getR2C2D(), 0.0);
    }
  }

  @Test public void testV3x3F()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 348);
    final JPRACursor1DType<MatricesType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf,
        MatricesByteBuffered::newValueWithOffset);
    final MatricesType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      final Matrix3x3FType k = v.getM3fWritable();
      k.setR0C0F((index * 10.0f) + 1.0f);
      k.setR1C0F((index * 10.0f) + 2.0f);
      k.setR2C0F((index * 10.0f) + 3.0f);

      k.setR0C1F((index * 10.0f) + 10.0f);
      k.setR1C1F((index * 10.0f) + 20.0f);
      k.setR2C1F((index * 10.0f) + 30.0f);

      k.setR0C2F((index * 10.0f) + 100.0f);
      k.setR1C2F((index * 10.0f) + 200.0f);
      k.setR2C2F((index * 10.0f) + 300.0f);
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);

      MatricesTest.check2x2DZero(v.getM2dReadable());
      MatricesTest.check2x2FZero(v.getM2fReadable());
      MatricesTest.check3x3DZero(v.getM3dReadable());
      // MatricesTest.check3x3FZero(v.getM3fReadable());
      MatricesTest.check4x4FZero(v.getM4fReadable());
      check4x4DZero(v.getM4dReadable());

      final MatrixReadable3x3FType k = v.getM3fReadable();

      Assert.assertEquals((index * 10.0) + 1.0, k.getR0C0F(), 0.0);
      Assert.assertEquals((index * 10.0) + 2.0, k.getR1C0F(), 0.0);
      Assert.assertEquals((index * 10.0) + 3.0, k.getR2C0F(), 0.0);

      Assert.assertEquals((index * 10.0) + 10.0, k.getR0C1F(), 0.0);
      Assert.assertEquals((index * 10.0) + 20.0, k.getR1C1F(), 0.0);
      Assert.assertEquals((index * 10.0) + 30.0, k.getR2C1F(), 0.0);

      Assert.assertEquals((index * 10.0) + 100.0, k.getR0C2F(), 0.0);
      Assert.assertEquals((index * 10.0) + 200.0, k.getR1C2F(), 0.0);
      Assert.assertEquals((index * 10.0) + 300.0, k.getR2C2F(), 0.0);
    }
  }

  @Test public void testV2x2D()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 348);
    final JPRACursor1DType<MatricesType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf,
        MatricesByteBuffered::newValueWithOffset);
    final MatricesType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      final Matrix2x2DType k = v.getM2dWritable();
      k.setR0C0D((index * 10.0) + 1.0);
      k.setR1C0D((index * 10.0) + 2.0);

      k.setR0C1D((index * 10.0) + 10.0);
      k.setR1C1D((index * 10.0) + 20.0);
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);

      // MatricesTest.check2x2DZero(v.getM2dReadable());
      MatricesTest.check2x2FZero(v.getM2fReadable());
      MatricesTest.check3x3DZero(v.getM3dReadable());
      MatricesTest.check2x2FZero(v.getM3fReadable());
      MatricesTest.check4x4FZero(v.getM4fReadable());
      check4x4DZero(v.getM4dReadable());

      final MatrixReadable2x2DType k = v.getM2dReadable();

      Assert.assertEquals((index * 10.0) + 1.0, k.getR0C0D(), 0.0);
      Assert.assertEquals((index * 10.0) + 2.0, k.getR1C0D(), 0.0);

      Assert.assertEquals((index * 10.0) + 10.0, k.getR0C1D(), 0.0);
      Assert.assertEquals((index * 10.0) + 20.0, k.getR1C1D(), 0.0);
    }
  }

  @Test public void testV2x2F()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 348);
    final JPRACursor1DType<MatricesType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf,
        MatricesByteBuffered::newValueWithOffset);
    final MatricesType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      final Matrix2x2FType k = v.getM2fWritable();
      k.setR0C0F((index * 10.0f) + 1.0f);
      k.setR1C0F((index * 10.0f) + 2.0f);

      k.setR0C1F((index * 10.0f) + 10.0f);
      k.setR1C1F((index * 10.0f) + 20.0f);
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);

      MatricesTest.check2x2DZero(v.getM2dReadable());
      // MatricesTest.check2x2FZero(v.getM2fReadable());
      MatricesTest.check3x3DZero(v.getM3dReadable());
      MatricesTest.check3x3FZero(v.getM3fReadable());
      MatricesTest.check4x4FZero(v.getM4fReadable());
      check4x4DZero(v.getM4dReadable());

      final MatrixReadable2x2FType k = v.getM2fReadable();

      Assert.assertEquals((index * 10.0) + 1.0, k.getR0C0F(), 0.0);
      Assert.assertEquals((index * 10.0) + 2.0, k.getR1C0F(), 0.0);

      Assert.assertEquals((index * 10.0) + 10.0, k.getR0C1F(), 0.0);
      Assert.assertEquals((index * 10.0) + 20.0, k.getR1C1F(), 0.0);
    }
  }
}
