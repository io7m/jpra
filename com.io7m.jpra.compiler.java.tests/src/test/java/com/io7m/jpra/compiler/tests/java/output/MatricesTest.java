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
import com.io7m.jpra.runtime.java.JPRATypeModel;
import com.io7m.jtensors.core.unparameterized.matrices.Matrix2x2D;
import com.io7m.jtensors.core.unparameterized.matrices.Matrix3x3D;
import com.io7m.jtensors.core.unparameterized.matrices.Matrix4x4D;
import com.io7m.jtensors.core.unparameterized.matrices.MatrixReadable2x2DType;
import com.io7m.jtensors.core.unparameterized.matrices.MatrixReadable3x3DType;
import com.io7m.jtensors.core.unparameterized.matrices.MatrixReadable4x4DType;
import com.io7m.jtensors.storage.api.unparameterized.matrices.MatrixStorage2x2Type;
import com.io7m.jtensors.storage.api.unparameterized.matrices.MatrixStorage3x3Type;
import com.io7m.jtensors.storage.api.unparameterized.matrices.MatrixStorage4x4Type;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;

public final class MatricesTest
{
  private static void check2x2DZero(final MatrixReadable2x2DType m)
  {
    for (int row = 0; row < 2; ++row) {
      for (int col = 0; col < 2; ++col) {
        Assert.assertEquals(0.0, m.rowColumn(row, col), 0.0);
      }
    }
  }

  private static void check3x3DZero(final MatrixReadable3x3DType m)
  {
    for (int row = 0; row < 3; ++row) {
      for (int col = 0; col < 3; ++col) {
        Assert.assertEquals(0.0, m.rowColumn(row, col), 0.0);
      }
    }
  }

  private static void check4x4DZero(final MatrixReadable4x4DType m)
  {
    for (int row = 0; row < 4; ++row) {
      for (int col = 0; col < 4; ++col) {
        Assert.assertEquals(0.0, m.rowColumn(row, col), 0.0);
      }
    }
  }

  private static void check2x2FZero(final MatrixReadable2x2DType m)
  {
    for (int row = 0; row < 2; ++row) {
      for (int col = 0; col < 2; ++col) {
        Assert.assertEquals(0.0, m.rowColumn(row, col), 0.0);
      }
    }
  }

  private static void check3x3FZero(final MatrixReadable3x3DType m)
  {
    for (int row = 0; row < 3; ++row) {
      for (int col = 0; col < 3; ++col) {
        Assert.assertEquals(0.0, m.rowColumn(row, col), 0.0);
      }
    }
  }

  private static void check4x4FZero(final MatrixReadable4x4DType m)
  {
    for (int row = 0; row < 4; ++row) {
      for (int col = 0; col < 4; ++col) {
        Assert.assertEquals(0.0, m.rowColumn(row, col), 0.0);
      }
    }
  }

  @Test
  public void testMeta()
  {
    final ByteBuffer buf = ByteBuffer.allocate(1024);
    final JPRACursor1DType<MatricesType> c =
      JPRACursor1DByteBufferedUnchecked.newCursor(
        buf,
        MatricesByteBuffered::newValueWithOffset);

    final MatricesType v = c.getElementView();
    Assert.assertEquals(348L, (long) v.sizeOctets());

    Assert.assertEquals(0L, (long) v.metaM2fOffsetFromType());
    Assert.assertEquals(16L, (long) v.metaM2dOffsetFromType());
    Assert.assertEquals(48L, (long) v.metaM3fOffsetFromType());
    Assert.assertEquals(84L, (long) v.metaM3dOffsetFromType());
    Assert.assertEquals(156L, (long) v.metaM4fOffsetFromType());
    Assert.assertEquals(220L, (long) v.metaM4dOffsetFromType());

    Assert.assertEquals(0L, (long) v.metaM2fOffsetFromCursor());
    Assert.assertEquals(16L, (long) v.metaM2dOffsetFromCursor());
    Assert.assertEquals(48L, (long) v.metaM3fOffsetFromCursor());
    Assert.assertEquals(84L, (long) v.metaM3dOffsetFromCursor());
    Assert.assertEquals(156L, (long) v.metaM4fOffsetFromCursor());
    Assert.assertEquals(220L, (long) v.metaM4dOffsetFromCursor());

    Assert.assertEquals(
      JPRATypeModel.JPRAMatrix.of(2, 2, JPRATypeModel.JPRAFloat.of(32)),
      v.metaM2fType());
    Assert.assertEquals(
      JPRATypeModel.JPRAMatrix.of(2, 2, JPRATypeModel.JPRAFloat.of(64)),
      v.metaM2dType());

    Assert.assertEquals(
      JPRATypeModel.JPRAMatrix.of(3, 3, JPRATypeModel.JPRAFloat.of(32)),
      v.metaM3fType());
    Assert.assertEquals(
      JPRATypeModel.JPRAMatrix.of(3, 3, JPRATypeModel.JPRAFloat.of(64)),
      v.metaM3dType());

    Assert.assertEquals(
      JPRATypeModel.JPRAMatrix.of(4, 4, JPRATypeModel.JPRAFloat.of(32)),
      v.metaM4fType());
    Assert.assertEquals(
      JPRATypeModel.JPRAMatrix.of(4, 4, JPRATypeModel.JPRAFloat.of(64)),
      v.metaM4dType());
  }

  @Test
  public void testV4x4D()
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
      final MatrixStorage4x4Type k = v.getM4dWritable();
      final double r0c0 = ((index * 10.0) + 1.0);
      final double r1c0 = ((index * 10.0) + 2.0);
      final double r2c0 = ((index * 10.0) + 3.0);
      final double r3c0 = ((index * 10.0) + 4.0);

      final double r0c1 = ((index * 10.0) + 10.0);
      final double r1c1 = ((index * 10.0) + 20.0);
      final double r2c1 = ((index * 10.0) + 30.0);
      final double r3c1 = ((index * 10.0) + 40.0);

      final double r0c2 = ((index * 10.0) + 100.0);
      final double r1c2 = ((index * 10.0) + 200.0);
      final double r2c2 = ((index * 10.0) + 300.0);
      final double r3c2 = ((index * 10.0) + 400.0);

      final double r0c3 = ((index * 10.0) + 1000.0);
      final double r1c3 = ((index * 10.0) + 2000.0);
      final double r2c3 = ((index * 10.0) + 3000.0);
      final double r3c3 = ((index * 10.0) + 4000.0);

      k.setMatrix4x4D(Matrix4x4D.of(
        r0c0, r0c1, r0c2, r0c3,
        r1c0, r1c1, r1c2, r1c3,
        r2c0, r2c1, r2c2, r2c3,
        r3c0, r3c1, r3c2, r3c3));
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);

      MatricesTest.check2x2DZero(v.getM2dReadable());
      MatricesTest.check2x2FZero(v.getM2fReadable());
      MatricesTest.check3x3DZero(v.getM3dReadable());
      MatricesTest.check3x3FZero(v.getM3fReadable());
      MatricesTest.check4x4FZero(v.getM4fReadable());
      // MatricesTest.check4x4DZero(v.getM4dReadable());

      final MatrixReadable4x4DType k = v.getM4dReadable();

      Assert.assertEquals((index * 10.0) + 1.0, k.r0c0(), 0.0);
      Assert.assertEquals((index * 10.0) + 2.0, k.r1c0(), 0.0);
      Assert.assertEquals((index * 10.0) + 3.0, k.r2c0(), 0.0);
      Assert.assertEquals((index * 10.0) + 4.0, k.r3c0(), 0.0);

      Assert.assertEquals((index * 10.0) + 10.0, k.r0c1(), 0.0);
      Assert.assertEquals((index * 10.0) + 20.0, k.r1c1(), 0.0);
      Assert.assertEquals((index * 10.0) + 30.0, k.r2c1(), 0.0);
      Assert.assertEquals((index * 10.0) + 40.0, k.r3c1(), 0.0);

      Assert.assertEquals((index * 10.0) + 100.0, k.r0c2(), 0.0);
      Assert.assertEquals((index * 10.0) + 200.0, k.r1c2(), 0.0);
      Assert.assertEquals((index * 10.0) + 300.0, k.r2c2(), 0.0);
      Assert.assertEquals((index * 10.0) + 400.0, k.r3c2(), 0.0);

      Assert.assertEquals((index * 10.0) + 1000.0, k.r0c3(), 0.0);
      Assert.assertEquals((index * 10.0) + 2000.0, k.r1c3(), 0.0);
      Assert.assertEquals((index * 10.0) + 3000.0, k.r2c3(), 0.0);
      Assert.assertEquals((index * 10.0) + 4000.0, k.r3c3(), 0.0);
    }
  }

  @Test
  public void testV4x4F()
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
      final MatrixStorage4x4Type k = v.getM4fWritable();
      final double r0c0 = ((index * 10.0) + 1.0);
      final double r1c0 = ((index * 10.0) + 2.0);
      final double r2c0 = ((index * 10.0) + 3.0);
      final double r3c0 = ((index * 10.0) + 4.0);

      final double r0c1 = ((index * 10.0) + 10.0);
      final double r1c1 = ((index * 10.0) + 20.0);
      final double r2c1 = ((index * 10.0) + 30.0);
      final double r3c1 = ((index * 10.0) + 40.0);

      final double r0c2 = ((index * 10.0) + 100.0);
      final double r1c2 = ((index * 10.0) + 200.0);
      final double r2c2 = ((index * 10.0) + 300.0);
      final double r3c2 = ((index * 10.0) + 400.0);

      final double r0c3 = ((index * 10.0) + 1000.0);
      final double r1c3 = ((index * 10.0) + 2000.0);
      final double r2c3 = ((index * 10.0) + 3000.0);
      final double r3c3 = ((index * 10.0) + 4000.0);

      k.setMatrix4x4D(Matrix4x4D.of(
        r0c0, r0c1, r0c2, r0c3,
        r1c0, r1c1, r1c2, r1c3,
        r2c0, r2c1, r2c2, r2c3,
        r3c0, r3c1, r3c2, r3c3));
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);

      MatricesTest.check2x2DZero(v.getM2dReadable());
      MatricesTest.check2x2FZero(v.getM2fReadable());
      MatricesTest.check3x3DZero(v.getM3dReadable());
      MatricesTest.check3x3FZero(v.getM3fReadable());
      // MatricesTest.check4x4FZero(v.getM4fReadable());
      MatricesTest.check4x4DZero(v.getM4dReadable());

      final MatrixReadable4x4DType k = v.getM4fReadable();

      Assert.assertEquals((index * 10.0) + 1.0, k.r0c0(), 0.0);
      Assert.assertEquals((index * 10.0) + 2.0, k.r1c0(), 0.0);
      Assert.assertEquals((index * 10.0) + 3.0, k.r2c0(), 0.0);
      Assert.assertEquals((index * 10.0) + 4.0, k.r3c0(), 0.0);

      Assert.assertEquals((index * 10.0) + 10.0, k.r0c1(), 0.0);
      Assert.assertEquals((index * 10.0) + 20.0, k.r1c1(), 0.0);
      Assert.assertEquals((index * 10.0) + 30.0, k.r2c1(), 0.0);
      Assert.assertEquals((index * 10.0) + 40.0, k.r3c1(), 0.0);

      Assert.assertEquals((index * 10.0) + 100.0, k.r0c2(), 0.0);
      Assert.assertEquals((index * 10.0) + 200.0, k.r1c2(), 0.0);
      Assert.assertEquals((index * 10.0) + 300.0, k.r2c2(), 0.0);
      Assert.assertEquals((index * 10.0) + 400.0, k.r3c2(), 0.0);

      Assert.assertEquals((index * 10.0) + 1000.0, k.r0c3(), 0.0);
      Assert.assertEquals((index * 10.0) + 2000.0, k.r1c3(), 0.0);
      Assert.assertEquals((index * 10.0) + 3000.0, k.r2c3(), 0.0);
      Assert.assertEquals((index * 10.0) + 4000.0, k.r3c3(), 0.0);
    }
  }

  @Test
  public void testV3x3D()
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
      final MatrixStorage3x3Type k = v.getM3dWritable();
      final double r0c0 = ((index * 10.0) + 1.0);
      final double r1c0 = ((index * 10.0) + 2.0);
      final double r2c0 = ((index * 10.0) + 3.0);

      final double r0c1 = ((index * 10.0) + 10.0);
      final double r1c1 = ((index * 10.0) + 20.0);
      final double r2c1 = ((index * 10.0) + 30.0);

      final double r0c2 = ((index * 10.0) + 100.0);
      final double r1c2 = ((index * 10.0) + 200.0);
      final double r2c2 = ((index * 10.0) + 300.0);

      k.setMatrix3x3D(Matrix3x3D.of(
        r0c0, r0c1, r0c2,
        r1c0, r1c1, r1c2,
        r2c0, r2c1, r2c2));
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);

      MatricesTest.check2x2DZero(v.getM2dReadable());
      MatricesTest.check2x2FZero(v.getM2fReadable());
      // MatricesTest.check3x3DZero(v.getM3dReadable());
      MatricesTest.check3x3FZero(v.getM3fReadable());
      MatricesTest.check4x4FZero(v.getM4fReadable());
      MatricesTest.check4x4DZero(v.getM4dReadable());

      final MatrixReadable3x3DType k = v.getM3dReadable();

      Assert.assertEquals((index * 10.0) + 1.0, k.r0c0(), 0.0);
      Assert.assertEquals((index * 10.0) + 2.0, k.r1c0(), 0.0);
      Assert.assertEquals((index * 10.0) + 3.0, k.r2c0(), 0.0);

      Assert.assertEquals((index * 10.0) + 10.0, k.r0c1(), 0.0);
      Assert.assertEquals((index * 10.0) + 20.0, k.r1c1(), 0.0);
      Assert.assertEquals((index * 10.0) + 30.0, k.r2c1(), 0.0);

      Assert.assertEquals((index * 10.0) + 100.0, k.r0c2(), 0.0);
      Assert.assertEquals((index * 10.0) + 200.0, k.r1c2(), 0.0);
      Assert.assertEquals((index * 10.0) + 300.0, k.r2c2(), 0.0);
    }
  }

  @Test
  public void testV3x3F()
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
      final MatrixStorage3x3Type k = v.getM3fWritable();
      final double r0c0 = ((index * 10.0) + 1.0);
      final double r1c0 = ((index * 10.0) + 2.0);
      final double r2c0 = ((index * 10.0) + 3.0);

      final double r0c1 = ((index * 10.0) + 10.0);
      final double r1c1 = ((index * 10.0) + 20.0);
      final double r2c1 = ((index * 10.0) + 30.0);

      final double r0c2 = ((index * 10.0) + 100.0);
      final double r1c2 = ((index * 10.0) + 200.0);
      final double r2c2 = ((index * 10.0) + 300.0);

      k.setMatrix3x3D(Matrix3x3D.of(
        r0c0, r0c1, r0c2,
        r1c0, r1c1, r1c2,
        r2c0, r2c1, r2c2));
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);

      MatricesTest.check2x2DZero(v.getM2dReadable());
      MatricesTest.check2x2FZero(v.getM2fReadable());
      MatricesTest.check3x3DZero(v.getM3dReadable());
      // MatricesTest.check3x3FZero(v.getM3fReadable());
      MatricesTest.check4x4FZero(v.getM4fReadable());
      MatricesTest.check4x4DZero(v.getM4dReadable());

      final MatrixReadable3x3DType k = v.getM3fReadable();

      Assert.assertEquals((index * 10.0) + 1.0, k.r0c0(), 0.0);
      Assert.assertEquals((index * 10.0) + 2.0, k.r1c0(), 0.0);
      Assert.assertEquals((index * 10.0) + 3.0, k.r2c0(), 0.0);

      Assert.assertEquals((index * 10.0) + 10.0, k.r0c1(), 0.0);
      Assert.assertEquals((index * 10.0) + 20.0, k.r1c1(), 0.0);
      Assert.assertEquals((index * 10.0) + 30.0, k.r2c1(), 0.0);

      Assert.assertEquals((index * 10.0) + 100.0, k.r0c2(), 0.0);
      Assert.assertEquals((index * 10.0) + 200.0, k.r1c2(), 0.0);
      Assert.assertEquals((index * 10.0) + 300.0, k.r2c2(), 0.0);
    }
  }

  @Test
  public void testV2x2D()
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
      final MatrixStorage2x2Type k = v.getM2dWritable();
      final double r0c0 = ((index * 10.0) + 1.0);
      final double r1c0 = ((index * 10.0) + 2.0);

      final double r0c1 = ((index * 10.0) + 10.0);
      final double r1c1 = ((index * 10.0) + 20.0);

      k.setMatrix2x2D(Matrix2x2D.of(
        r0c0, r0c1,
        r1c0, r1c1));
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);

      // MatricesTest.check2x2DZero(v.getM2dReadable());
      MatricesTest.check2x2FZero(v.getM2fReadable());
      MatricesTest.check3x3DZero(v.getM3dReadable());
      MatricesTest.check3x3FZero(v.getM3fReadable());
      MatricesTest.check4x4FZero(v.getM4fReadable());
      MatricesTest.check4x4DZero(v.getM4dReadable());

      final MatrixReadable2x2DType k = v.getM2dReadable();

      Assert.assertEquals((index * 10.0) + 1.0, k.r0c0(), 0.0);
      Assert.assertEquals((index * 10.0) + 2.0, k.r1c0(), 0.0);

      Assert.assertEquals((index * 10.0) + 10.0, k.r0c1(), 0.0);
      Assert.assertEquals((index * 10.0) + 20.0, k.r1c1(), 0.0);
    }
  }

  @Test
  public void testV2x2F()
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
      final MatrixStorage2x2Type k = v.getM2fWritable();
      final double r0c0 = ((index * 10.0) + 1.0);
      final double r1c0 = ((index * 10.0) + 2.0);

      final double r0c1 = ((index * 10.0) + 10.0);
      final double r1c1 = ((index * 10.0) + 20.0);

      k.setMatrix2x2D(Matrix2x2D.of(
        r0c0, r0c1,
        r1c0, r1c1));
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);

      MatricesTest.check2x2DZero(v.getM2dReadable());
      // MatricesTest.check2x2FZero(v.getM2fReadable());
      MatricesTest.check3x3DZero(v.getM3dReadable());
      MatricesTest.check3x3FZero(v.getM3fReadable());
      MatricesTest.check4x4FZero(v.getM4fReadable());
      MatricesTest.check4x4DZero(v.getM4dReadable());

      final MatrixReadable2x2DType k = v.getM2fReadable();

      Assert.assertEquals((index * 10.0) + 1.0, k.r0c0(), 0.0);
      Assert.assertEquals((index * 10.0) + 2.0, k.r1c0(), 0.0);

      Assert.assertEquals((index * 10.0) + 10.0, k.r0c1(), 0.0);
      Assert.assertEquals((index * 10.0) + 20.0, k.r1c1(), 0.0);
    }
  }
}
