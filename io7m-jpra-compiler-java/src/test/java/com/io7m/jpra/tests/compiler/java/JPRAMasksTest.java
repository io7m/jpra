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

package com.io7m.jpra.tests.compiler.java;

import com.io7m.jpra.compiler.java.JPRAMasks;
import org.hamcrest.core.StringStartsWith;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.valid4j.exceptions.RequireViolation;

public final class JPRAMasksTest
{
  @Rule public ExpectedException expected = ExpectedException.none();

  @Test public void testZeroError_0()
  {
    this.expected.expect(RequireViolation.class);
    this.expected.expectMessage(new StringStartsWith("size"));
    JPRAMasks.createZeroMask(-1, 0, 0);
  }

  @Test public void testZeroError_1()
  {
    this.expected.expect(RequireViolation.class);
    this.expected.expectMessage(new StringStartsWith("size"));
    JPRAMasks.createZeroMask(0, 0, 0);
  }

  @Test public void testZeroError_2()
  {
    this.expected.expect(RequireViolation.class);
    this.expected.expectMessage(new StringStartsWith("lsb"));
    JPRAMasks.createZeroMask(1, -1, 0);
  }

  @Test public void testZeroError_3()
  {
    this.expected.expect(RequireViolation.class);
    this.expected.expectMessage(new StringStartsWith("msb"));
    JPRAMasks.createZeroMask(1, 0, 1);
  }

  @Test public void testZeroError_4()
  {
    this.expected.expect(RequireViolation.class);
    this.expected.expectMessage(new StringStartsWith("lsb"));
    JPRAMasks.createZeroMask(2, 1, 0);
  }

  @Test public void testZero8_4msb()
  {
    Assert.assertEquals("0b11110000", JPRAMasks.createZeroMask(8, 0, 3));
  }

  @Test public void testZero8_4lsb()
  {
    Assert.assertEquals("0b00001111", JPRAMasks.createZeroMask(8, 4, 7));
  }

  @Test public void testZero8_0()
  {
    Assert.assertEquals("0b00000000", JPRAMasks.createZeroMask(8, 0, 7));
  }

  @Test public void testZero16_0()
  {
    Assert.assertEquals(
      "0b00000000_00000000", JPRAMasks.createZeroMask(16, 0, 15));
  }

  @Test public void testZero16_1()
  {
    Assert.assertEquals(
      "0b00000000_11111111", JPRAMasks.createZeroMask(16, 8, 15));
  }

  @Test public void testZero16_2()
  {
    Assert.assertEquals(
      "0b11111111_00000000", JPRAMasks.createZeroMask(16, 0, 7));
  }

  @Test public void testZero32_0()
  {
    Assert.assertEquals(
      "0b00000000_00000000_00000000_00000000",
      JPRAMasks.createZeroMask(32, 0, 31));
  }

  @Test public void testZero32_1()
  {
    Assert.assertEquals(
      "0b11111111_11111111_11111111_00000000",
      JPRAMasks.createZeroMask(32, 0, 7));
  }

  @Test public void testZero32_2()
  {
    Assert.assertEquals(
      "0b11111111_11111111_00000000_11111111",
      JPRAMasks.createZeroMask(32, 8, 15));
  }

  @Test public void testZero32_3()
  {
    Assert.assertEquals(
      "0b11111111_00000000_11111111_11111111",
      JPRAMasks.createZeroMask(32, 16, 23));
  }

  @Test public void testZero32_4()
  {
    Assert.assertEquals(
      "0b00000000_11111111_11111111_11111111",
      JPRAMasks.createZeroMask(32, 24, 31));
  }

  @Test public void testZero64_0()
  {
    Assert.assertEquals(
      "0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L",
      JPRAMasks.createZeroMask(64, 0, 63));
  }

  @Test public void testZero64_1()
  {
    Assert.assertEquals(
      "0b11111111_11111111_11111111_11111111_11111111_11111111_11111111_00000000L",
      JPRAMasks.createZeroMask(64, 0, 7));
  }

  @Test public void testZero64_2()
  {
    Assert.assertEquals(
      "0b11111111_11111111_11111111_11111111_11111111_11111111_00000000_11111111L",
      JPRAMasks.createZeroMask(64, 8, 15));
  }

  @Test public void testZero64_3()
  {
    Assert.assertEquals(
      "0b11111111_11111111_11111111_11111111_11111111_00000000_11111111_11111111L",
      JPRAMasks.createZeroMask(64, 16, 23));
  }

  @Test public void testZero64_4()
  {
    Assert.assertEquals(
      "0b11111111_11111111_11111111_11111111_00000000_11111111_11111111_11111111L",
      JPRAMasks.createZeroMask(64, 24, 31));
  }

  @Test public void testZero64_5()
  {
    Assert.assertEquals(
      "0b11111111_11111111_11111111_00000000_11111111_11111111_11111111_11111111L",
      JPRAMasks.createZeroMask(64, 32, 39));
  }

  @Test public void testZero64_6()
  {
    Assert.assertEquals(
      "0b11111111_11111111_00000000_11111111_11111111_11111111_11111111_11111111L",
      JPRAMasks.createZeroMask(64, 40, 47));
  }

  @Test public void testZero64_7()
  {
    Assert.assertEquals(
      "0b11111111_00000000_11111111_11111111_11111111_11111111_11111111_11111111L",
      JPRAMasks.createZeroMask(64, 48, 55));
  }

  @Test public void testZero64_8()
  {
    Assert.assertEquals(
      "0b00000000_11111111_11111111_11111111_11111111_11111111_11111111_11111111L",
      JPRAMasks.createZeroMask(64, 56, 63));
  }

  @Test public void testOneError_0()
  {
    this.expected.expect(RequireViolation.class);
    this.expected.expectMessage(new StringStartsWith("size"));
    JPRAMasks.createOneMask(-1, 0, 0);
  }

  @Test public void testOneError_1()
  {
    this.expected.expect(RequireViolation.class);
    this.expected.expectMessage(new StringStartsWith("size"));
    JPRAMasks.createOneMask(0, 0, 0);
  }

  @Test public void testOneError_2()
  {
    this.expected.expect(RequireViolation.class);
    this.expected.expectMessage(new StringStartsWith("lsb"));
    JPRAMasks.createOneMask(1, -1, 0);
  }

  @Test public void testOneError_3()
  {
    this.expected.expect(RequireViolation.class);
    this.expected.expectMessage(new StringStartsWith("msb"));
    JPRAMasks.createOneMask(1, 0, 1);
  }

  @Test public void testOneError_4()
  {
    this.expected.expect(RequireViolation.class);
    this.expected.expectMessage(new StringStartsWith("lsb"));
    JPRAMasks.createOneMask(2, 1, 0);
  }

  @Test public void testOne8_4msb()
  {
    Assert.assertEquals("0b00001111", JPRAMasks.createOneMask(8, 0, 3));
  }

  @Test public void testOne8_4lsb()
  {
    Assert.assertEquals("0b11110000", JPRAMasks.createOneMask(8, 4, 7));
  }

  @Test public void testOne8_0()
  {
    Assert.assertEquals("0b11111111", JPRAMasks.createOneMask(8, 0, 7));
  }

  @Test public void testOne16_0()
  {
    Assert.assertEquals(
      "0b11111111_11111111", JPRAMasks.createOneMask(16, 0, 15));
  }

  @Test public void testOne16_1()
  {
    Assert.assertEquals(
      "0b11111111_00000000", JPRAMasks.createOneMask(16, 8, 15));
  }

  @Test public void testOne16_2()
  {
    Assert.assertEquals(
      "0b00000000_11111111", JPRAMasks.createOneMask(16, 0, 7));
  }

  @Test public void testOne32_0()
  {
    Assert.assertEquals(
      "0b11111111_11111111_11111111_11111111",
      JPRAMasks.createOneMask(32, 0, 31));
  }

  @Test public void testOne32_1()
  {
    Assert.assertEquals(
      "0b00000000_00000000_00000000_11111111",
      JPRAMasks.createOneMask(32, 0, 7));
  }

  @Test public void testOne32_2()
  {
    Assert.assertEquals(
      "0b00000000_00000000_11111111_00000000",
      JPRAMasks.createOneMask(32, 8, 15));
  }

  @Test public void testOne32_3()
  {
    Assert.assertEquals(
      "0b00000000_11111111_00000000_00000000",
      JPRAMasks.createOneMask(32, 16, 23));
  }

  @Test public void testOne32_4()
  {
    Assert.assertEquals(
      "0b11111111_00000000_00000000_00000000",
      JPRAMasks.createOneMask(32, 24, 31));
  }

  @Test public void testOne64_0()
  {
    Assert.assertEquals(
      "0b11111111_11111111_11111111_11111111_11111111_11111111_11111111_11111111L",
      JPRAMasks.createOneMask(64, 0, 63));
  }

  @Test public void testOne64_1()
  {
    Assert.assertEquals(
      "0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_11111111L",
      JPRAMasks.createOneMask(64, 0, 7));
  }

  @Test public void testOne64_2()
  {
    Assert.assertEquals(
      "0b00000000_00000000_00000000_00000000_00000000_00000000_11111111_00000000L",
      JPRAMasks.createOneMask(64, 8, 15));
  }

  @Test public void testOne64_3()
  {
    Assert.assertEquals(
      "0b00000000_00000000_00000000_00000000_00000000_11111111_00000000_00000000L",
      JPRAMasks.createOneMask(64, 16, 23));
  }

  @Test public void testOne64_4()
  {
    Assert.assertEquals(
      "0b00000000_00000000_00000000_00000000_11111111_00000000_00000000_00000000L",
      JPRAMasks.createOneMask(64, 24, 31));
  }

  @Test public void testOne64_5()
  {
    Assert.assertEquals(
      "0b00000000_00000000_00000000_11111111_00000000_00000000_00000000_00000000L",
      JPRAMasks.createOneMask(64, 32, 39));
  }

  @Test public void testOne64_6()
  {
    Assert.assertEquals(
      "0b00000000_00000000_11111111_00000000_00000000_00000000_00000000_00000000L",
      JPRAMasks.createOneMask(64, 40, 47));
  }

  @Test public void testOne64_7()
  {
    Assert.assertEquals(
      "0b00000000_11111111_00000000_00000000_00000000_00000000_00000000_00000000L",
      JPRAMasks.createOneMask(64, 48, 55));
  }

  @Test public void testOne64_8()
  {
    Assert.assertEquals(
      "0b11111111_00000000_00000000_00000000_00000000_00000000_00000000_00000000L",
      JPRAMasks.createOneMask(64, 56, 63));
  }
}
