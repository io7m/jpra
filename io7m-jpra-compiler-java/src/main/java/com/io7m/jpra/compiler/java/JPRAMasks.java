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

package com.io7m.jpra.compiler.java;

import com.io7m.junreachable.UnreachableCodeException;
import org.valid4j.Assertive;

import java.math.BigInteger;

/**
 * Functions to create bit masks.
 */

public final class JPRAMasks
{
  private JPRAMasks()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Create a mask of {@code size} bits, where the bits from the inclusive range
   * {@code [lsb, msb]} are set to {@code 0} and the rest are set to {@code 1}.
   *
   * @param size The mask size
   * @param lsb  The least significant bit index
   * @param msb  The most significant bit index
   *
   * @return A mask string
   */

  public static String createZeroMask(
    final int size,
    final int lsb,
    final int msb)
  {
    Assertive.require(size > 0, "size %d > 0", Integer.valueOf(size));
    Assertive.require(lsb >= 0, "lsb %d >= 0", Integer.valueOf(lsb));
    Assertive.require(
      msb < size,
      "msb %d < size %d",
      Integer.valueOf(msb),
      Integer.valueOf(size));
    Assertive.require(
      lsb <= msb,
      "lsb %d <= msb %d",
      Integer.valueOf(lsb),
      Integer.valueOf(msb));

    final BigInteger b2 = BigInteger.valueOf(2L);
    final BigInteger base = b2.pow(size).subtract(BigInteger.ONE);

    BigInteger v = base;
    for (int index = lsb; index <= msb; ++index) {
      v = v.clearBit(index);
    }

    final StringBuilder sb = new StringBuilder(size + 4);
    sb.append("0b");
    for (int index = size - 1; index >= 0; --index) {
      sb.append(v.testBit(index) ? '1' : '0');
      if (index > 0 && index % 8 == 0) {
        sb.append("_");
      }
    }
    if (size > 32) {
      sb.append("L");
    }
    return sb.toString();
  }

  /**
   * Create a mask of {@code size} bits, where the bits from the inclusive range
   * {@code [lsb, msb]} are set to {@code 0} and the rest are set to {@code 1}.
   *
   * @param size The mask size
   * @param lsb  The least significant bit index
   * @param msb  The most significant bit index
   *
   * @return A mask string
   */

  public static String createOneMask(
    final int size,
    final int lsb,
    final int msb)
  {
    Assertive.require(size > 0, "size %d > 0", Integer.valueOf(size));
    Assertive.require(lsb >= 0, "lsb %d >= 0", Integer.valueOf(lsb));
    Assertive.require(
      msb < size,
      "msb %d < size %d",
      Integer.valueOf(msb),
      Integer.valueOf(size));
    Assertive.require(
      lsb <= msb,
      "lsb %d <= msb %d",
      Integer.valueOf(lsb),
      Integer.valueOf(msb));

    BigInteger v = BigInteger.ZERO;
    for (int index = lsb; index <= msb; ++index) {
      v = v.setBit(index);
    }

    final StringBuilder sb = new StringBuilder(size + 4);
    sb.append("0b");
    for (int index = size - 1; index >= 0; --index) {
      sb.append(v.testBit(index) ? '1' : '0');
      if (index > 0 && index % 8 == 0) {
        sb.append("_");
      }
    }
    if (size > 32) {
      sb.append("L");
    }
    return sb.toString();
  }
}
