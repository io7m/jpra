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

import com.io7m.jpra.runtime.java.JPRACursor1DByteBufferedUnchecked;
import com.io7m.jpra.runtime.java.JPRACursor1DType;
import com.io7m.jpra.runtime.java.JPRAValueByteBufferedConstructorType;
import com.io7m.jpra.runtime.java.JPRAValueType;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.ByteBuffer;

public final class JPRACursor1DByteBufferedUncheckedTest
{
  @Rule public ExpectedException expected = ExpectedException.none();

  @Test public void testElementSizeZero()
  {
    final ByteBuffer buf = ByteBuffer.allocate(100);
    final JPRAValueByteBufferedConstructorType<JPRAValueType> cons =
      (buffer, cursor, offset) -> () -> 0;

    this.expected.expect(IllegalArgumentException.class);
    this.expected.expectMessage("Element size must be positive");
    JPRACursor1DByteBufferedUnchecked.newCursor(buf, cons);
  }

  @Test public void testBufferIdentity()
  {
    final ByteBuffer buf = ByteBuffer.allocate(100);
    final JPRAValueType v = () -> 1;
    final JPRAValueByteBufferedConstructorType<JPRAValueType> cons =
      (buffer, cursor, offset) -> v;

    final JPRACursor1DType<JPRAValueType> c =
      JPRACursor1DByteBufferedUnchecked.newCursor(buf, cons);
    Assert.assertEquals(0L, (long) c.getElementIndex());
    Assert.assertEquals(0L, c.getByteOffset());
    Assert.assertEquals(v, c.getElementView());
    Assert.assertEquals("[Cursor 0]", c.toString());
  }

  @Test public void testBufferSetIdentity()
  {
    final ByteBuffer buf = ByteBuffer.allocate(200);
    final JPRAValueByteBufferedConstructorType<JPRAValueType> cons =
      (buffer, cursor, offset) -> () -> 2;

    final JPRACursor1DType<JPRAValueType> c =
      JPRACursor1DByteBufferedUnchecked.newCursor(buf, cons);

    for (int index = 0; index < 100; ++index) {
      c.setElementIndex(index);
      Assert.assertEquals((long) index, (long) c.getElementIndex());
      Assert.assertEquals((long) (index * 2), c.getByteOffset());
      Assert.assertEquals("[Cursor " + index + "]", c.toString());
    }
  }
}
