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

package com.io7m.jpra.tests.runtime.java;

import com.io7m.jpra.runtime.java.JPRACursor2DByteBufferedUnchecked;
import com.io7m.jpra.runtime.java.JPRACursor2DType;
import com.io7m.jpra.runtime.java.JPRAValueByteBufferedConstructorType;
import com.io7m.jpra.runtime.java.JPRAValueType;
import org.hamcrest.core.StringEndsWith;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.ByteBuffer;

public final class JPRACursor2DByteBufferedUncheckedTest
{
  @Rule public ExpectedException expected = ExpectedException.none();

  @Test public void testElementSizeZero()
  {
    final ByteBuffer buf = ByteBuffer.allocate(100);
    final JPRAValueByteBufferedConstructorType<JPRAValueType> cons =
      (buffer, cursor, offset) -> () -> 0;

    this.expected.expect(IllegalArgumentException.class);
    this.expected.expectMessage("Element size must be positive");
    JPRACursor2DByteBufferedUnchecked.newCursor(buf, 10, 10, cons);
  }

  @Test public void testWidthNegative()
  {
    final ByteBuffer buf = ByteBuffer.allocate(100);
    final JPRAValueByteBufferedConstructorType<JPRAValueType> cons =
      (buffer, cursor, offset) -> () -> 0;

    this.expected.expect(IllegalArgumentException.class);
    this.expected.expectMessage(new StringEndsWith(" must be positive"));
    JPRACursor2DByteBufferedUnchecked.newCursor(buf, -1, 10, cons);
  }

  @Test public void testHeightNegative()
  {
    final ByteBuffer buf = ByteBuffer.allocate(100);
    final JPRAValueByteBufferedConstructorType<JPRAValueType> cons =
      (buffer, cursor, offset) -> () -> 0;

    this.expected.expect(IllegalArgumentException.class);
    this.expected.expectMessage(new StringEndsWith(" must be positive"));
    JPRACursor2DByteBufferedUnchecked.newCursor(buf, 10, -1, cons);
  }

  @Test public void testBufferIdentity()
  {
    final ByteBuffer buf = ByteBuffer.allocate(100);
    final JPRAValueType v = () -> 1;
    final JPRAValueByteBufferedConstructorType<JPRAValueType> cons =
      (buffer, cursor, offset) -> v;

    final JPRACursor2DType<JPRAValueType> c =
      JPRACursor2DByteBufferedUnchecked.newCursor(buf, 10, 10, cons);

    Assert.assertEquals(0L, (long) c.getElementX());
    Assert.assertEquals(0L, (long) c.getElementY());
    Assert.assertEquals(v, c.getElementView());
    Assert.assertEquals(0L, c.getByteOffset());
  }

  @Test public void testBufferRowStartsIdentity()
  {
    final ByteBuffer buf = ByteBuffer.allocate(300);
    final JPRAValueType v = () -> 3;
    final JPRAValueByteBufferedConstructorType<JPRAValueType> cons =
      (buffer, cursor, offset) -> v;

    final JPRACursor2DType<JPRAValueType> c =
      JPRACursor2DByteBufferedUnchecked.newCursor(buf, 10, 10, cons);

    for (int row = 0; row < 10; ++row) {
      c.setElementPosition(0, row);

      Assert.assertEquals(0L, (long) c.getElementX());
      Assert.assertEquals((long) row, (long) c.getElementY());
      Assert.assertEquals(v, c.getElementView());
      Assert.assertEquals((long) row * (3L * 10L), c.getByteOffset());
      final String text = String.format(
        "[Cursor 0,%d/%d,%d]",
        Integer.valueOf(c.getElementY()),
        Integer.valueOf(9),
        Integer.valueOf(9));
      Assert.assertEquals(text, c.toString());
    }
  }

  @Test public void testBufferColumnStartsIdentity()
  {
    final ByteBuffer buf = ByteBuffer.allocate(300);
    final JPRAValueType v = () -> 3;
    final JPRAValueByteBufferedConstructorType<JPRAValueType> cons =
      (buffer, cursor, offset) -> v;

    final JPRACursor2DType<JPRAValueType> c =
      JPRACursor2DByteBufferedUnchecked.newCursor(buf, 10, 10, cons);

    for (int column = 0; column < 10; ++column) {
      c.setElementPosition(column, 0);

      Assert.assertEquals((long) column, (long) c.getElementX());
      Assert.assertEquals(0L, (long) c.getElementY());
      Assert.assertEquals(v, c.getElementView());
      Assert.assertEquals((long) column * 3L, c.getByteOffset());

      final String text = String.format(
        "[Cursor %d,0/%d,%d]",
        Integer.valueOf(c.getElementX()),
        Integer.valueOf(9),
        Integer.valueOf(9));
      Assert.assertEquals(text, c.toString());
    }
  }
}
