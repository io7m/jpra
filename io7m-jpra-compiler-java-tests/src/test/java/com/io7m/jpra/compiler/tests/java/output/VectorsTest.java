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

import com.io7m.jpra.compiler.tests.java.generation.code.VectorsByteBuffered;
import com.io7m.jpra.compiler.tests.java.generation.code.VectorsType;
import com.io7m.jpra.runtime.java.JPRACursor1DByteBufferedChecked;
import com.io7m.jpra.runtime.java.JPRACursor1DByteBufferedUnchecked;
import com.io7m.jpra.runtime.java.JPRACursor1DType;
import com.io7m.jpra.runtime.java.JPRATypeModel;
import com.io7m.jtensors.Vector2DType;
import com.io7m.jtensors.Vector2FType;
import com.io7m.jtensors.Vector2IType;
import com.io7m.jtensors.Vector2LType;
import com.io7m.jtensors.Vector3DType;
import com.io7m.jtensors.Vector3FType;
import com.io7m.jtensors.Vector3IType;
import com.io7m.jtensors.Vector3LType;
import com.io7m.jtensors.Vector4DType;
import com.io7m.jtensors.Vector4FType;
import com.io7m.jtensors.Vector4IType;
import com.io7m.jtensors.Vector4LType;
import com.io7m.jtensors.VectorReadable2DType;
import com.io7m.jtensors.VectorReadable2FType;
import com.io7m.jtensors.VectorReadable2IType;
import com.io7m.jtensors.VectorReadable2LType;
import com.io7m.jtensors.VectorReadable3DType;
import com.io7m.jtensors.VectorReadable3FType;
import com.io7m.jtensors.VectorReadable3IType;
import com.io7m.jtensors.VectorReadable3LType;
import com.io7m.jtensors.VectorReadable4DType;
import com.io7m.jtensors.VectorReadable4FType;
import com.io7m.jtensors.VectorReadable4IType;
import com.io7m.jtensors.VectorReadable4LType;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;

public final class VectorsTest
{
  static void checkV2DZero(final VectorReadable2DType v)
  {
    Assert.assertEquals(0.0, v.getXD(), 0.0);
    Assert.assertEquals(0.0, v.getYD(), 0.0);
  }

  static void checkV2FZero(final VectorReadable2FType v)
  {
    Assert.assertEquals(0.0, (double) v.getXF(), 0.0);
    Assert.assertEquals(0.0, (double) v.getYF(), 0.0);
  }

  static void checkV3DZero(final VectorReadable3DType v)
  {
    Assert.assertEquals(0.0, v.getXD(), 0.0);
    Assert.assertEquals(0.0, v.getYD(), 0.0);
    Assert.assertEquals(0.0, v.getZD(), 0.0);
  }

  static void checkV3FZero(final VectorReadable3FType v)
  {
    Assert.assertEquals(0.0, (double) v.getXF(), 0.0);
    Assert.assertEquals(0.0, (double) v.getYF(), 0.0);
    Assert.assertEquals(0.0, (double) v.getZF(), 0.0);
  }

  static void checkV4DZero(final VectorReadable4DType v)
  {
    Assert.assertEquals(0.0, v.getXD(), 0.0);
    Assert.assertEquals(0.0, v.getYD(), 0.0);
    Assert.assertEquals(0.0, v.getZD(), 0.0);
    Assert.assertEquals(0.0, v.getWD(), 0.0);
  }

  static void checkV4FZero(final VectorReadable4FType v)
  {
    Assert.assertEquals(0.0, (double) v.getXF(), 0.0);
    Assert.assertEquals(0.0, (double) v.getYF(), 0.0);
    Assert.assertEquals(0.0, (double) v.getZF(), 0.0);
    Assert.assertEquals(0.0, (double) v.getWF(), 0.0);
  }

  static void checkV2LZero(final VectorReadable2LType v)
  {
    Assert.assertEquals(0L, v.getXL());
    Assert.assertEquals(0L, v.getYL());
  }

  static void checkV2IZero(final VectorReadable2IType v)
  {
    Assert.assertEquals(0L, (long) v.getXI());
    Assert.assertEquals(0L, (long) v.getYI());
  }

  static void checkV3LZero(final VectorReadable3LType v)
  {
    Assert.assertEquals(0L, v.getXL());
    Assert.assertEquals(0L, v.getYL());
    Assert.assertEquals(0L, v.getZL());
  }

  static void checkV3IZero(final VectorReadable3IType v)
  {
    Assert.assertEquals(0L, (long) v.getXI());
    Assert.assertEquals(0L, (long) v.getYI());
    Assert.assertEquals(0L, (long) v.getZI());
  }

  static void checkV4LZero(final VectorReadable4LType v)
  {
    Assert.assertEquals(0L, v.getXL());
    Assert.assertEquals(0L, v.getYL());
    Assert.assertEquals(0L, v.getZL());
    Assert.assertEquals(0L, v.getWL());
  }

  static void checkV4IZero(final VectorReadable4IType v)
  {
    Assert.assertEquals(0L, (long) v.getXI());
    Assert.assertEquals(0L, (long) v.getYI());
    Assert.assertEquals(0L, (long) v.getZI());
    Assert.assertEquals(0L, (long) v.getWI());
  }

  @Test public void testMeta()
  {
    final ByteBuffer buf = ByteBuffer.allocate(1024);
    final JPRACursor1DType<VectorsType> c =
      JPRACursor1DByteBufferedUnchecked.newCursor(
        buf, VectorsByteBuffered::newValueWithOffset);

    final VectorsType v = c.getElementView();
    Assert.assertEquals(234L, (long) v.sizeOctets());

    Assert.assertEquals(
      JPRATypeModel.JPRAVector.of(2, JPRATypeModel.JPRAFloat.of(16)),
      v.metaV2hType());
    Assert.assertEquals(
      JPRATypeModel.JPRAVector.of(2, JPRATypeModel.JPRAFloat.of(32)),
      v.metaV2fType());
    Assert.assertEquals(
      JPRATypeModel.JPRAVector.of(2, JPRATypeModel.JPRAFloat.of(64)),
      v.metaV2dType());
    Assert.assertEquals(
      JPRATypeModel.JPRAVector.of(2, JPRATypeModel.JPRAIntegerSigned.of(32)),
      v.metaV2iType());
    Assert.assertEquals(
      JPRATypeModel.JPRAVector.of(2, JPRATypeModel.JPRAIntegerSigned.of(64)),
      v.metaV2lType());

    Assert.assertEquals(
      JPRATypeModel.JPRAVector.of(3, JPRATypeModel.JPRAFloat.of(16)),
      v.metaV3hType());
    Assert.assertEquals(
      JPRATypeModel.JPRAVector.of(3, JPRATypeModel.JPRAFloat.of(32)),
      v.metaV3fType());
    Assert.assertEquals(
      JPRATypeModel.JPRAVector.of(3, JPRATypeModel.JPRAFloat.of(64)),
      v.metaV3dType());
    Assert.assertEquals(
      JPRATypeModel.JPRAVector.of(3, JPRATypeModel.JPRAIntegerSigned.of(32)),
      v.metaV3iType());
    Assert.assertEquals(
      JPRATypeModel.JPRAVector.of(3, JPRATypeModel.JPRAIntegerSigned.of(64)),
      v.metaV3lType());

    Assert.assertEquals(
      JPRATypeModel.JPRAVector.of(4, JPRATypeModel.JPRAFloat.of(16)),
      v.metaV4hType());
    Assert.assertEquals(
      JPRATypeModel.JPRAVector.of(4, JPRATypeModel.JPRAFloat.of(32)),
      v.metaV4fType());
    Assert.assertEquals(
      JPRATypeModel.JPRAVector.of(4, JPRATypeModel.JPRAFloat.of(64)),
      v.metaV4dType());
    Assert.assertEquals(
      JPRATypeModel.JPRAVector.of(4, JPRATypeModel.JPRAIntegerSigned.of(32)),
      v.metaV4iType());
    Assert.assertEquals(
      JPRATypeModel.JPRAVector.of(4, JPRATypeModel.JPRAIntegerSigned.of(64)),
      v.metaV4lType());
  }

  @Test public void testV4D()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 234);
    final JPRACursor1DType<VectorsType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, VectorsByteBuffered::newValueWithOffset);
    final VectorsType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      final Vector4DType k = v.getV4dWritable();
      final double x = (index * 10.0) + 1.0;
      final double y = (index * 100.0) + 2.0;
      final double z = (index * 1000.0) + 3.0;
      final double w = (index * 10000.0) + 4.0;
      k.set4D(x, y, z, w);
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);

      VectorsTest.checkV2DZero(v.getV2hReadable());
      VectorsTest.checkV3DZero(v.getV3hReadable());
      VectorsTest.checkV4DZero(v.getV4hReadable());

      VectorsTest.checkV2DZero(v.getV2dReadable());
      VectorsTest.checkV3DZero(v.getV3dReadable());
      // VectorsTest.checkV4DZero(v.getV4dReadable());

      VectorsTest.checkV2FZero(v.getV2fReadable());
      VectorsTest.checkV3FZero(v.getV3fReadable());
      VectorsTest.checkV4FZero(v.getV4fReadable());

      VectorsTest.checkV2LZero(v.getV2lReadable());
      VectorsTest.checkV3LZero(v.getV3lReadable());
      VectorsTest.checkV4LZero(v.getV4lReadable());

      VectorsTest.checkV2IZero(v.getV2iReadable());
      VectorsTest.checkV3IZero(v.getV3iReadable());
      VectorsTest.checkV4IZero(v.getV4iReadable());

      final VectorReadable4DType k = v.getV4dReadable();
      final double x = (index * 10.0) + 1.0;
      final double y = (index * 100.0) + 2.0;
      final double z = (index * 1000.0) + 3.0;
      final double w = (index * 10000.0) + 4.0;

      Assert.assertEquals(x, k.getXD(), 0.0);
      Assert.assertEquals(y, k.getYD(), 0.0);
      Assert.assertEquals(z, k.getZD(), 0.0);
      Assert.assertEquals(w, k.getWD(), 0.0);
    }
  }

  @Test public void testV4H()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 234);
    final JPRACursor1DType<VectorsType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, VectorsByteBuffered::newValueWithOffset);
    final VectorsType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      final Vector4DType k = v.getV4hWritable();
      final double x = (index * 10.0) + 1.0;
      final double y = (index * 20.0) + 2.0;
      final double z = (index * 30.0) + 3.0;
      final double w = (index * 40.0) + 4.0;
      k.set4D(x, y, z, w);
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);

      VectorsTest.checkV2DZero(v.getV2hReadable());
      VectorsTest.checkV3DZero(v.getV3hReadable());
      // VectorsTest.checkV4DZero(v.getV4hReadable());

      VectorsTest.checkV2DZero(v.getV2dReadable());
      VectorsTest.checkV3DZero(v.getV3dReadable());
      VectorsTest.checkV4DZero(v.getV4dReadable());

      VectorsTest.checkV2FZero(v.getV2fReadable());
      VectorsTest.checkV3FZero(v.getV3fReadable());
      VectorsTest.checkV4FZero(v.getV4fReadable());

      VectorsTest.checkV2LZero(v.getV2lReadable());
      VectorsTest.checkV3LZero(v.getV3lReadable());
      VectorsTest.checkV4LZero(v.getV4lReadable());

      VectorsTest.checkV2IZero(v.getV2iReadable());
      VectorsTest.checkV3IZero(v.getV3iReadable());
      VectorsTest.checkV4IZero(v.getV4iReadable());

      final VectorReadable4DType k = v.getV4hReadable();
      final double x = (index * 10.0) + 1.0;
      final double y = (index * 20.0) + 2.0;
      final double z = (index * 30.0) + 3.0;
      final double w = (index * 40.0) + 4.0;

      Assert.assertEquals(x, k.getXD(), 0.0);
      Assert.assertEquals(y, k.getYD(), 0.0);
      Assert.assertEquals(z, k.getZD(), 0.0);
      Assert.assertEquals(w, k.getWD(), 0.0);
    }
  }

  @Test public void testV3D()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 234);
    final JPRACursor1DType<VectorsType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, VectorsByteBuffered::newValueWithOffset);
    final VectorsType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      final Vector3DType k = v.getV3dWritable();
      final double x = (index * 10.0) + 1.0;
      final double y = (index * 100.0) + 2.0;
      final double z = (index * 1000.0) + 3.0;
      k.set3D(x, y, z);
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);

      VectorsTest.checkV2DZero(v.getV2hReadable());
      VectorsTest.checkV3DZero(v.getV3hReadable());
      VectorsTest.checkV4DZero(v.getV4hReadable());

      VectorsTest.checkV2DZero(v.getV2dReadable());
      // VectorsTest.checkV3DZero(v.getV3dReadable());
      VectorsTest.checkV4DZero(v.getV4dReadable());

      VectorsTest.checkV2FZero(v.getV2fReadable());
      VectorsTest.checkV3FZero(v.getV3fReadable());
      VectorsTest.checkV4FZero(v.getV4fReadable());

      VectorsTest.checkV2LZero(v.getV2lReadable());
      VectorsTest.checkV3LZero(v.getV3lReadable());
      VectorsTest.checkV4LZero(v.getV4lReadable());

      VectorsTest.checkV2IZero(v.getV2iReadable());
      VectorsTest.checkV3IZero(v.getV3iReadable());
      VectorsTest.checkV4IZero(v.getV4iReadable());

      final VectorReadable3DType k = v.getV3dReadable();
      final double x = (index * 10.0) + 1.0;
      final double y = (index * 100.0) + 2.0;
      final double z = (index * 1000.0) + 3.0;

      Assert.assertEquals(x, k.getXD(), 0.0);
      Assert.assertEquals(y, k.getYD(), 0.0);
      Assert.assertEquals(z, k.getZD(), 0.0);
    }
  }

  @Test public void testV3H()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 234);
    final JPRACursor1DType<VectorsType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, VectorsByteBuffered::newValueWithOffset);
    final VectorsType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      final Vector3DType k = v.getV3hWritable();
      final double x = (index * 10.0) + 1.0;
      final double y = (index * 20.0) + 2.0;
      final double z = (index * 30.0) + 3.0;
      k.set3D(x, y, z);
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);

      VectorsTest.checkV2DZero(v.getV2hReadable());
      // VectorsTest.checkV3DZero(v.getV3hReadable());
      VectorsTest.checkV4DZero(v.getV4hReadable());

      VectorsTest.checkV2DZero(v.getV2dReadable());
      VectorsTest.checkV3DZero(v.getV3dReadable());
      VectorsTest.checkV4DZero(v.getV4dReadable());

      VectorsTest.checkV2FZero(v.getV2fReadable());
      VectorsTest.checkV3FZero(v.getV3fReadable());
      VectorsTest.checkV4FZero(v.getV4fReadable());

      VectorsTest.checkV2LZero(v.getV2lReadable());
      VectorsTest.checkV3LZero(v.getV3lReadable());
      VectorsTest.checkV4LZero(v.getV4lReadable());

      VectorsTest.checkV2IZero(v.getV2iReadable());
      VectorsTest.checkV3IZero(v.getV3iReadable());
      VectorsTest.checkV4IZero(v.getV4iReadable());

      final VectorReadable3DType k = v.getV3hReadable();
      final double x = (index * 10.0) + 1.0;
      final double y = (index * 20.0) + 2.0;
      final double z = (index * 30.0) + 3.0;

      Assert.assertEquals(x, k.getXD(), 0.0);
      Assert.assertEquals(y, k.getYD(), 0.0);
      Assert.assertEquals(z, k.getZD(), 0.0);
    }
  }

  @Test public void testV2D()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 234);
    final JPRACursor1DType<VectorsType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, VectorsByteBuffered::newValueWithOffset);
    final VectorsType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      final Vector2DType k = v.getV2dWritable();
      final double x = (index * 10.0) + 1.0;
      final double y = (index * 100.0) + 2.0;
      k.set2D(x, y);
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);

      VectorsTest.checkV2DZero(v.getV2hReadable());
      VectorsTest.checkV3DZero(v.getV3hReadable());
      VectorsTest.checkV4DZero(v.getV4hReadable());

      // VectorsTest.checkV2DZero(v.getV2dReadable());
      VectorsTest.checkV3DZero(v.getV3dReadable());
      VectorsTest.checkV4DZero(v.getV4dReadable());

      VectorsTest.checkV2FZero(v.getV2fReadable());
      VectorsTest.checkV3FZero(v.getV3fReadable());
      VectorsTest.checkV4FZero(v.getV4fReadable());

      VectorsTest.checkV2LZero(v.getV2lReadable());
      VectorsTest.checkV3LZero(v.getV3lReadable());
      VectorsTest.checkV4LZero(v.getV4lReadable());

      VectorsTest.checkV2IZero(v.getV2iReadable());
      VectorsTest.checkV3IZero(v.getV3iReadable());
      VectorsTest.checkV4IZero(v.getV4iReadable());

      final VectorReadable2DType k = v.getV2dReadable();
      final double x = (index * 10.0) + 1.0;
      final double y = (index * 100.0) + 2.0;

      Assert.assertEquals(x, k.getXD(), 0.0);
      Assert.assertEquals(y, k.getYD(), 0.0);
    }
  }

  @Test public void testV2H()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 234);
    final JPRACursor1DType<VectorsType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, VectorsByteBuffered::newValueWithOffset);
    final VectorsType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      final Vector2DType k = v.getV2hWritable();
      final double x = (index * 10.0) + 1.0;
      final double y = (index * 20.0) + 2.0;
      k.set2D(x, y);
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);

      // VectorsTest.checkV2DZero(v.getV2hReadable());
      VectorsTest.checkV3DZero(v.getV3hReadable());
      VectorsTest.checkV4DZero(v.getV4hReadable());

      VectorsTest.checkV2DZero(v.getV2dReadable());
      VectorsTest.checkV3DZero(v.getV3dReadable());
      VectorsTest.checkV4DZero(v.getV4dReadable());

      VectorsTest.checkV2FZero(v.getV2fReadable());
      VectorsTest.checkV3FZero(v.getV3fReadable());
      VectorsTest.checkV4FZero(v.getV4fReadable());

      VectorsTest.checkV2LZero(v.getV2lReadable());
      VectorsTest.checkV3LZero(v.getV3lReadable());
      VectorsTest.checkV4LZero(v.getV4lReadable());

      VectorsTest.checkV2IZero(v.getV2iReadable());
      VectorsTest.checkV3IZero(v.getV3iReadable());
      VectorsTest.checkV4IZero(v.getV4iReadable());

      final VectorReadable2DType k = v.getV2hReadable();
      final double x = (index * 10.0) + 1.0;
      final double y = (index * 20.0) + 2.0;

      Assert.assertEquals(x, k.getXD(), 0.0);
      Assert.assertEquals(y, k.getYD(), 0.0);
    }
  }

  @Test public void testV4F()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 234);
    final JPRACursor1DType<VectorsType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, VectorsByteBuffered::newValueWithOffset);
    final VectorsType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      final Vector4FType k = v.getV4fWritable();
      final float x = (index * 10.0f) + 1.0f;
      final float y = (index * 100.0f) + 2.0f;
      final float z = (index * 1000.0f) + 3.0f;
      final float w = (index * 10000.0f) + 4.0f;
      k.set4F(x, y, z, w);
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);

      VectorsTest.checkV2DZero(v.getV2hReadable());
      VectorsTest.checkV3DZero(v.getV3hReadable());
      VectorsTest.checkV4DZero(v.getV4hReadable());

      VectorsTest.checkV2DZero(v.getV2dReadable());
      VectorsTest.checkV3DZero(v.getV3dReadable());
      VectorsTest.checkV4DZero(v.getV4dReadable());

      VectorsTest.checkV2FZero(v.getV2fReadable());
      VectorsTest.checkV3FZero(v.getV3fReadable());
      // VectorsTest.checkV4FZero(v.getV4fReadable());

      VectorsTest.checkV2LZero(v.getV2lReadable());
      VectorsTest.checkV3LZero(v.getV3lReadable());
      VectorsTest.checkV4LZero(v.getV4lReadable());

      VectorsTest.checkV2IZero(v.getV2iReadable());
      VectorsTest.checkV3IZero(v.getV3iReadable());
      VectorsTest.checkV4IZero(v.getV4iReadable());

      final VectorReadable4FType k = v.getV4fReadable();
      final float x = (index * 10.0f) + 1.0f;
      final float y = (index * 100.0f) + 2.0f;
      final float z = (index * 1000.0f) + 3.0f;
      final float w = (index * 10000.0f) + 4.0f;

      Assert.assertEquals(x, k.getXF(), 0.0f);
      Assert.assertEquals(y, k.getYF(), 0.0f);
      Assert.assertEquals(z, k.getZF(), 0.0f);
      Assert.assertEquals(w, k.getWF(), 0.0f);
    }
  }

  @Test public void testV3F()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 234);
    final JPRACursor1DType<VectorsType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, VectorsByteBuffered::newValueWithOffset);
    final VectorsType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      final Vector3FType k = v.getV3fWritable();
      final float x = (index * 10.0f) + 1.0f;
      final float y = (index * 100.0f) + 2.0f;
      final float z = (index * 1000.0f) + 3.0f;
      k.set3F(x, y, z);
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);

      VectorsTest.checkV2DZero(v.getV2hReadable());
      VectorsTest.checkV3DZero(v.getV3hReadable());
      VectorsTest.checkV4DZero(v.getV4hReadable());

      VectorsTest.checkV2DZero(v.getV2dReadable());
      VectorsTest.checkV3DZero(v.getV3dReadable());
      VectorsTest.checkV4DZero(v.getV4dReadable());

      VectorsTest.checkV2FZero(v.getV2fReadable());
      // VectorsTest.checkV3FZero(v.getV3fReadable());
      VectorsTest.checkV4FZero(v.getV4fReadable());

      VectorsTest.checkV2LZero(v.getV2lReadable());
      VectorsTest.checkV3LZero(v.getV3lReadable());
      VectorsTest.checkV4LZero(v.getV4lReadable());

      VectorsTest.checkV2IZero(v.getV2iReadable());
      VectorsTest.checkV3IZero(v.getV3iReadable());
      VectorsTest.checkV4IZero(v.getV4iReadable());

      final VectorReadable3FType k = v.getV3fReadable();
      final float x = (index * 10.0f) + 1.0f;
      final float y = (index * 100.0f) + 2.0f;
      final float z = (index * 1000.0f) + 3.0f;

      Assert.assertEquals(x, k.getXF(), 0.0f);
      Assert.assertEquals(y, k.getYF(), 0.0f);
      Assert.assertEquals(z, k.getZF(), 0.0f);
    }
  }

  @Test public void testV2F()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 234);
    final JPRACursor1DType<VectorsType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, VectorsByteBuffered::newValueWithOffset);
    final VectorsType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      final Vector2FType k = v.getV2fWritable();
      final float x = (index * 10.0f) + 1.0f;
      final float y = (index * 100.0f) + 2.0f;
      k.set2F(x, y);
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);

      VectorsTest.checkV2DZero(v.getV2hReadable());
      VectorsTest.checkV3DZero(v.getV3hReadable());
      VectorsTest.checkV4DZero(v.getV4hReadable());

      VectorsTest.checkV2DZero(v.getV2dReadable());
      VectorsTest.checkV3DZero(v.getV3dReadable());
      VectorsTest.checkV4DZero(v.getV4dReadable());

      // VectorsTest.checkV2FZero(v.getV2fReadable());
      VectorsTest.checkV3FZero(v.getV3fReadable());
      VectorsTest.checkV4FZero(v.getV4fReadable());

      VectorsTest.checkV2LZero(v.getV2lReadable());
      VectorsTest.checkV3LZero(v.getV3lReadable());
      VectorsTest.checkV4LZero(v.getV4lReadable());

      VectorsTest.checkV2IZero(v.getV2iReadable());
      VectorsTest.checkV3IZero(v.getV3iReadable());
      VectorsTest.checkV4IZero(v.getV4iReadable());

      final VectorReadable2FType k = v.getV2fReadable();
      final float x = (index * 10.0f) + 1.0f;
      final float y = (index * 100.0f) + 2.0f;

      Assert.assertEquals(x, k.getXF(), 0.0f);
      Assert.assertEquals(y, k.getYF(), 0.0f);
    }
  }

  @Test public void testV4L()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 234);
    final JPRACursor1DType<VectorsType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, VectorsByteBuffered::newValueWithOffset);
    final VectorsType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      final Vector4LType k = v.getV4lWritable();
      final long x = (index * 10) + 1;
      final long y = (index * 100) + 2;
      final long z = (index * 1000) + 3;
      final long w = (index * 10000) + 4;
      k.set4L(x, y, z, w);
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);

      VectorsTest.checkV2DZero(v.getV2hReadable());
      VectorsTest.checkV3DZero(v.getV3hReadable());
      VectorsTest.checkV4DZero(v.getV4hReadable());

      VectorsTest.checkV2DZero(v.getV2dReadable());
      VectorsTest.checkV3DZero(v.getV3dReadable());
      VectorsTest.checkV4DZero(v.getV4dReadable());

      VectorsTest.checkV2FZero(v.getV2fReadable());
      VectorsTest.checkV3FZero(v.getV3fReadable());
      VectorsTest.checkV4FZero(v.getV4fReadable());

      VectorsTest.checkV2LZero(v.getV2lReadable());
      VectorsTest.checkV3LZero(v.getV3lReadable());
      // VectorsTest.checkV4LZero(v.getV4lReadable());

      VectorsTest.checkV2IZero(v.getV2iReadable());
      VectorsTest.checkV3IZero(v.getV3iReadable());
      VectorsTest.checkV4IZero(v.getV4iReadable());

      final VectorReadable4LType k = v.getV4lReadable();
      final long x = (index * 10) + 1;
      final long y = (index * 100) + 2;
      final long z = (index * 1000) + 3;
      final long w = (index * 10000) + 4;

      Assert.assertEquals(x, k.getXL());
      Assert.assertEquals(y, k.getYL());
      Assert.assertEquals(z, k.getZL());
      Assert.assertEquals(w, k.getWL());
    }
  }

  @Test public void testV3L()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 234);
    final JPRACursor1DType<VectorsType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, VectorsByteBuffered::newValueWithOffset);
    final VectorsType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      final Vector3LType k = v.getV3lWritable();
      final long x = (index * 10) + 1;
      final long y = (index * 100) + 2;
      final long z = (index * 1000) + 3;
      k.set3L(x, y, z);
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);

      VectorsTest.checkV2DZero(v.getV2hReadable());
      VectorsTest.checkV3DZero(v.getV3hReadable());
      VectorsTest.checkV4DZero(v.getV4hReadable());

      VectorsTest.checkV2DZero(v.getV2dReadable());
      VectorsTest.checkV3DZero(v.getV3dReadable());
      VectorsTest.checkV4DZero(v.getV4dReadable());

      VectorsTest.checkV2FZero(v.getV2fReadable());
      VectorsTest.checkV3FZero(v.getV3fReadable());
      VectorsTest.checkV4FZero(v.getV4fReadable());

      VectorsTest.checkV2LZero(v.getV2lReadable());
      // VectorsTest.checkV3LZero(v.getV3lReadable());
      VectorsTest.checkV4LZero(v.getV4lReadable());

      VectorsTest.checkV2IZero(v.getV2iReadable());
      VectorsTest.checkV3IZero(v.getV3iReadable());
      VectorsTest.checkV4IZero(v.getV4iReadable());

      final VectorReadable3LType k = v.getV3lReadable();
      final long x = (index * 10) + 1;
      final long y = (index * 100) + 2;
      final long z = (index * 1000) + 3;

      Assert.assertEquals(x, k.getXL());
      Assert.assertEquals(y, k.getYL());
      Assert.assertEquals(z, k.getZL());
    }
  }

  @Test public void testV2L()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 234);
    final JPRACursor1DType<VectorsType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, VectorsByteBuffered::newValueWithOffset);
    final VectorsType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      final Vector2LType k = v.getV2lWritable();
      final long x = (index * 10) + 1;
      final long y = (index * 100) + 2;
      k.set2L(x, y);
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);

      VectorsTest.checkV2DZero(v.getV2hReadable());
      VectorsTest.checkV3DZero(v.getV3hReadable());
      VectorsTest.checkV4DZero(v.getV4hReadable());

      VectorsTest.checkV2DZero(v.getV2dReadable());
      VectorsTest.checkV3DZero(v.getV3dReadable());
      VectorsTest.checkV4DZero(v.getV4dReadable());

      VectorsTest.checkV2FZero(v.getV2fReadable());
      VectorsTest.checkV3FZero(v.getV3fReadable());
      VectorsTest.checkV4FZero(v.getV4fReadable());

      // VectorsTest.checkV2LZero(v.getV2lReadable());
      VectorsTest.checkV3LZero(v.getV3lReadable());
      VectorsTest.checkV4LZero(v.getV4lReadable());

      VectorsTest.checkV2IZero(v.getV2iReadable());
      VectorsTest.checkV3IZero(v.getV3iReadable());
      VectorsTest.checkV4IZero(v.getV4iReadable());

      final VectorReadable2LType k = v.getV2lReadable();
      final long x = (index * 10) + 1;
      final long y = (index * 100) + 2;

      Assert.assertEquals(x, k.getXL());
      Assert.assertEquals(y, k.getYL());
    }
  }

  @Test public void testV4I()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 234);
    final JPRACursor1DType<VectorsType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, VectorsByteBuffered::newValueWithOffset);
    final VectorsType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      final Vector4IType k = v.getV4iWritable();
      final int x = (index * 10) + 1;
      final int y = (index * 100) + 2;
      final int z = (index * 1000) + 3;
      final int w = (index * 10000) + 4;
      k.set4I(x, y, z, w);
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);

      VectorsTest.checkV2DZero(v.getV2hReadable());
      VectorsTest.checkV3DZero(v.getV3hReadable());
      VectorsTest.checkV4DZero(v.getV4hReadable());

      VectorsTest.checkV2DZero(v.getV2dReadable());
      VectorsTest.checkV3DZero(v.getV3dReadable());
      VectorsTest.checkV4DZero(v.getV4dReadable());

      VectorsTest.checkV2FZero(v.getV2fReadable());
      VectorsTest.checkV3FZero(v.getV3fReadable());
      VectorsTest.checkV4FZero(v.getV4fReadable());

      VectorsTest.checkV2LZero(v.getV2lReadable());
      VectorsTest.checkV3LZero(v.getV3lReadable());
      VectorsTest.checkV4LZero(v.getV4lReadable());

      VectorsTest.checkV2IZero(v.getV2iReadable());
      VectorsTest.checkV3IZero(v.getV3iReadable());
      // VectorsTest.checkV4IZero(v.getV4iReadable());

      final VectorReadable4IType k = v.getV4iReadable();
      final int x = (index * 10) + 1;
      final int y = (index * 100) + 2;
      final int z = (index * 1000) + 3;
      final int w = (index * 10000) + 4;

      Assert.assertEquals(x, k.getXI());
      Assert.assertEquals(y, k.getYI());
      Assert.assertEquals(z, k.getZI());
      Assert.assertEquals(w, k.getWI());
    }
  }

  @Test public void testV3I()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 234);
    final JPRACursor1DType<VectorsType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, VectorsByteBuffered::newValueWithOffset);
    final VectorsType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      final Vector3IType k = v.getV3iWritable();
      final int x = (index * 10) + 1;
      final int y = (index * 100) + 2;
      final int z = (index * 1000) + 3;
      k.set3I(x, y, z);
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);

      VectorsTest.checkV2DZero(v.getV2hReadable());
      VectorsTest.checkV3DZero(v.getV3hReadable());
      VectorsTest.checkV4DZero(v.getV4hReadable());

      VectorsTest.checkV2DZero(v.getV2dReadable());
      VectorsTest.checkV3DZero(v.getV3dReadable());
      VectorsTest.checkV4DZero(v.getV4dReadable());

      VectorsTest.checkV2FZero(v.getV2fReadable());
      VectorsTest.checkV3FZero(v.getV3fReadable());
      VectorsTest.checkV4FZero(v.getV4fReadable());

      VectorsTest.checkV2LZero(v.getV2lReadable());
      VectorsTest.checkV3LZero(v.getV3lReadable());
      VectorsTest.checkV4LZero(v.getV4lReadable());

      VectorsTest.checkV2IZero(v.getV2iReadable());
      // VectorsTest.checkV3IZero(v.getV3iReadable());
      VectorsTest.checkV4IZero(v.getV4iReadable());

      final VectorReadable3IType k = v.getV3iReadable();
      final int x = (index * 10) + 1;
      final int y = (index * 100) + 2;
      final int z = (index * 1000) + 3;

      Assert.assertEquals(x, k.getXI());
      Assert.assertEquals(y, k.getYI());
      Assert.assertEquals(z, k.getZI());
    }
  }

  @Test public void testV2I()
  {
    final ByteBuffer buf = ByteBuffer.allocate(8 * 234);
    final JPRACursor1DType<VectorsType> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf, VectorsByteBuffered::newValueWithOffset);
    final VectorsType v = c.getElementView();

    Assert.assertEquals(0L, (long) c.getElementIndex());

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);
      final Vector2IType k = v.getV2iWritable();
      final int x = (index * 10) + 1;
      final int y = (index * 100) + 2;
      k.set2I(x, y);
    }

    for (int index = 0; index < 8; ++index) {
      c.setElementIndex(index);

      VectorsTest.checkV2DZero(v.getV2hReadable());
      VectorsTest.checkV3DZero(v.getV3hReadable());
      VectorsTest.checkV4DZero(v.getV4hReadable());

      VectorsTest.checkV2DZero(v.getV2dReadable());
      VectorsTest.checkV3DZero(v.getV3dReadable());
      VectorsTest.checkV4DZero(v.getV4dReadable());

      VectorsTest.checkV2FZero(v.getV2fReadable());
      VectorsTest.checkV3FZero(v.getV3fReadable());
      VectorsTest.checkV4FZero(v.getV4fReadable());

      VectorsTest.checkV2LZero(v.getV2lReadable());
      VectorsTest.checkV3LZero(v.getV3lReadable());
      VectorsTest.checkV4LZero(v.getV4lReadable());

      // VectorsTest.checkV2IZero(v.getV2iReadable());
      VectorsTest.checkV3IZero(v.getV3iReadable());
      VectorsTest.checkV4IZero(v.getV4iReadable());

      final VectorReadable2IType k = v.getV2iReadable();
      final int x = (index * 10) + 1;
      final int y = (index * 100) + 2;

      Assert.assertEquals(x, k.getXI());
      Assert.assertEquals(y, k.getYI());
    }
  }
}
