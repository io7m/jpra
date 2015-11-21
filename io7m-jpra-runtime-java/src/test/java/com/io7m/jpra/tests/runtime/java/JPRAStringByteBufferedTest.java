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

import com.io7m.jpra.runtime.java.JPRACursorByteReadableType;
import com.io7m.jpra.runtime.java.JPRAStringByteBuffered;
import com.io7m.jpra.runtime.java.JPRAStringTruncation;
import com.io7m.jpra.runtime.java.JPRAStringType;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicLong;

public final class JPRAStringByteBufferedTest
{
  @Rule public ExpectedException expected = ExpectedException.none();

  @Test public void testIdentities()
  {
    final AtomicLong base = new AtomicLong(0L);
    final ByteBuffer buf = ByteBuffer.allocate(1000);
    final JPRACursorByteReadableType cursor = () -> base;
    final JPRAStringType s =
      JPRAStringByteBuffered.newString(
        buf,
        0,
        cursor,
        StandardCharsets.UTF_8,
        96);

    Assert.assertEquals(96L, (long) s.getMaximumLength());
    Assert.assertEquals(0L, (long) s.getUsedLength());
    Assert.assertEquals(StandardCharsets.UTF_8, s.getEncoding());
  }

  @Test public void testNegativeMaximum()
  {
    final AtomicLong base = new AtomicLong(0L);
    final ByteBuffer buf = ByteBuffer.allocate(1000);
    final JPRACursorByteReadableType cursor = () -> base;

    this.expected.expect(IllegalArgumentException.class);
    this.expected.expectMessage(new StringContains("Maximum length"));

    JPRAStringByteBuffered.newString(
      buf,
      0,
      cursor,
      StandardCharsets.UTF_8,
      -1);
  }

  @Test public void testNegativeOffset()
  {
    final AtomicLong base = new AtomicLong(0L);
    final ByteBuffer buf = ByteBuffer.allocate(1000);
    final JPRACursorByteReadableType cursor = () -> base;

    this.expected.expect(IllegalArgumentException.class);
    this.expected.expectMessage(new StringContains("Offset"));

    JPRAStringByteBuffered.newString(
      buf,
      -1,
      cursor,
      StandardCharsets.UTF_8,
      96);
  }

  @Test public void testUsedLength()
  {
    final AtomicLong base = new AtomicLong(0L);
    final ByteBuffer buf = ByteBuffer.allocate(1000);
    final JPRACursorByteReadableType cursor = () -> base;
    final JPRAStringType s =
      JPRAStringByteBuffered.newString(
        buf,
        0,
        cursor,
        StandardCharsets.UTF_8,
        96);

    for (int index = 0; index < 10; ++index) {
      final int offset = index * 100;
      base.set((long) offset);

      Assert.assertEquals(0L, (long) s.getUsedLength());
      buf.putInt(offset, -1);
      Assert.assertEquals(0L, (long) s.getUsedLength());
      buf.putInt(offset, 97);
      Assert.assertEquals(96L, (long) s.getUsedLength());
    }
  }

  @Test public void testNewValue()
  {
    final AtomicLong base = new AtomicLong(0L);
    final ByteBuffer buf = ByteBuffer.allocate(10 * (8 + 4));
    final JPRACursorByteReadableType cursor = () -> base;
    final JPRAStringType s =
      JPRAStringByteBuffered.newString(
        buf,
        0,
        cursor,
        StandardCharsets.UTF_8,
        8);

    for (int index = 0; index < 10; ++index) {
      final int offset = index * (8 + 4);
      base.set((long) offset);

      buf.putInt(offset + 0, 4);

      buf.put(offset + 4, (byte) 'A');
      buf.put(offset + 5, (byte) 'B');
      buf.put(offset + 6, (byte) 'C');
      buf.put(offset + 7, (byte) 'D');

      buf.put(offset + 8, (byte) 'A');
      buf.put(offset + 9, (byte) 'B');
      buf.put(offset + 10, (byte) 'C');
      buf.put(offset + 11, (byte) 'D');

      Assert.assertEquals(0L, (long) buf.position());
      Assert.assertEquals("ABCD", s.getNewValue());
      Assert.assertEquals(0L, (long) buf.position());

      buf.putInt(offset + 0, 8);
      Assert.assertEquals(0L, (long) buf.position());
      Assert.assertEquals("ABCDABCD", s.getNewValue());
      Assert.assertEquals(0L, (long) buf.position());
    }
  }

  @Test public void testGetBytes()
  {
    final AtomicLong base = new AtomicLong(0L);
    final ByteBuffer buf = ByteBuffer.allocate(10 * (8 + 4));
    final JPRACursorByteReadableType cursor = () -> base;
    final JPRAStringType s =
      JPRAStringByteBuffered.newString(
        buf,
        0,
        cursor,
        StandardCharsets.UTF_8,
        8);

    for (int index = 0; index < 10; ++index) {
      final int offset = index * (8 + 4);
      base.set((long) offset);

      buf.putInt(offset + 0, 4);
      buf.put(offset + 4, (byte) 'A');
      buf.put(offset + 5, (byte) 'B');
      buf.put(offset + 6, (byte) 'C');
      buf.put(offset + 7, (byte) 'D');
      buf.put(offset + 8, (byte) 'A');
      buf.put(offset + 9, (byte) 'B');
      buf.put(offset + 10, (byte) 'C');
      buf.put(offset + 11, (byte) 'D');

      Assert.assertEquals(0L, (long) buf.position());
      final byte[] b = new byte[8];
      s.getBytes(b, 0, 8);

      Assert.assertEquals((long) 'A', b[0]);
      Assert.assertEquals((long) 'B', b[1]);
      Assert.assertEquals((long) 'C', b[2]);
      Assert.assertEquals((long) 'D', b[3]);
      Assert.assertEquals((long) 'A', b[4]);
      Assert.assertEquals((long) 'B', b[5]);
      Assert.assertEquals((long) 'C', b[6]);
      Assert.assertEquals((long) 'D', b[7]);
      Assert.assertEquals(0L, (long) buf.position());
    }
  }

  @Test public void testGetBytesOutOfBounds()
  {
    final AtomicLong base = new AtomicLong(0L);
    final ByteBuffer buf = ByteBuffer.allocate(8 + 4);
    final JPRACursorByteReadableType cursor = () -> base;
    final JPRAStringType s =
      JPRAStringByteBuffered.newString(
        buf,
        0,
        cursor,
        StandardCharsets.UTF_8,
        8);

    final byte[] b = new byte[16];

    this.expected.expect(IndexOutOfBoundsException.class);
    this.expected.expectMessage(new StringContains("Length"));
    s.getBytes(b, 0, 16);
  }

  @Test public void testSetValueTruncated()
  {
    final AtomicLong base = new AtomicLong(0L);
    final ByteBuffer buf = ByteBuffer.allocate(10 * (8 + 4));
    final JPRACursorByteReadableType cursor = () -> base;
    final JPRAStringType s =
      JPRAStringByteBuffered.newString(
        buf,
        0,
        cursor,
        StandardCharsets.UTF_8,
        8);

    for (int index = 0; index < 10; ++index) {
      final int offset = index * (8 + 4);
      base.set((long) offset);

      Assert.assertEquals(0L, (long) s.getUsedLength());
      Assert.assertEquals("", s.getNewValue());

      s.setValue("ABCD", JPRAStringTruncation.TRUNCATE);
      Assert.assertEquals(4L, (long) s.getUsedLength());
      Assert.assertEquals("ABCD", s.getNewValue());

      s.setValue("EFGHIJKLMNOPQRSTUVWXYZ", JPRAStringTruncation.TRUNCATE);
      Assert.assertEquals(8L, (long) s.getUsedLength());
      Assert.assertEquals("EFGHIJKL", s.getNewValue());
    }
  }

  @Test public void testSetValueRejected()
  {
    final AtomicLong base = new AtomicLong(0L);
    final ByteBuffer buf = ByteBuffer.allocate(8 + 4);
    final JPRACursorByteReadableType cursor = () -> base;
    final JPRAStringType s =
      JPRAStringByteBuffered.newString(
        buf,
        0,
        cursor,
        StandardCharsets.UTF_8,
        8);

    Assert.assertEquals(0L, (long) s.getUsedLength());
    Assert.assertEquals("", s.getNewValue());

    s.setValue("EFGH", JPRAStringTruncation.REJECT);
    Assert.assertEquals(4L, (long) s.getUsedLength());
    Assert.assertEquals("EFGH", s.getNewValue());

    this.expected.expect(IndexOutOfBoundsException.class);
    this.expected.expectMessage(new StringContains("Bytes length"));
    s.setValue("EFGHIJKLMNOPQRSTUVWXYZ", JPRAStringTruncation.REJECT);
  }

  @Test public void testGetByteRejected()
  {
    final AtomicLong base = new AtomicLong(0L);
    final ByteBuffer buf = ByteBuffer.allocate(8 + 4);
    final JPRACursorByteReadableType cursor = () -> base;
    final JPRAStringType s =
      JPRAStringByteBuffered.newString(
        buf,
        0,
        cursor,
        StandardCharsets.UTF_8,
        8);

    this.expected.expect(IndexOutOfBoundsException.class);
    this.expected.expectMessage(new StringContains("Index"));
    s.getByte(8);
  }

  @Test public void testGetByteRejectedNegative()
  {
    final AtomicLong base = new AtomicLong(0L);
    final ByteBuffer buf = ByteBuffer.allocate(8 + 4);
    final JPRACursorByteReadableType cursor = () -> base;
    final JPRAStringType s =
      JPRAStringByteBuffered.newString(
        buf,
        0,
        cursor,
        StandardCharsets.UTF_8,
        8);

    this.expected.expect(IndexOutOfBoundsException.class);
    this.expected.expectMessage(new StringContains("Index"));
    s.getByte(-1);
  }

  @Test public void testGetByte()
  {
    final AtomicLong base = new AtomicLong(0L);
    final ByteBuffer buf = ByteBuffer.allocate(8 + 4);
    final JPRACursorByteReadableType cursor = () -> base;
    final JPRAStringType s =
      JPRAStringByteBuffered.newString(
        buf,
        0,
        cursor,
        StandardCharsets.UTF_8,
        8);

    s.setValue("EFGH", JPRAStringTruncation.REJECT);
    Assert.assertEquals(4L, (long) s.getUsedLength());
    Assert.assertEquals("EFGH", s.getNewValue());

    Assert.assertEquals((long) 'E', (long) s.getByte(0));
    Assert.assertEquals((long) 'F', (long) s.getByte(1));
    Assert.assertEquals((long) 'G', (long) s.getByte(2));
    Assert.assertEquals((long) 'H', (long) s.getByte(3));
  }
}
