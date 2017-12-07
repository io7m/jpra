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

package com.io7m.jpra.model.types;

import java.math.BigInteger;
import java.util.Objects;

/**
 * A size value.
 *
 * @param <U> The unit of measurement
 */

public final class Size<U extends SizeUnitType>
{
  private final BigInteger value;

  /**
   * Construct a size value.
   *
   * @param in_value The size
   */

  public Size(final BigInteger in_value)
  {
    this.value = Objects.requireNonNull(in_value, "Value");
  }

  /**
   * @param <U> The unit of measurement
   *
   * @return The zero value
   */

  public static <U extends SizeUnitType> Size<U> zero()
  {
    return new Size<>(BigInteger.ZERO);
  }

  /**
   * @param size A size value in octets
   *
   * @return The original size value converted to bits
   */

  public static Size<SizeUnitBitsType> toBits(
    final Size<SizeUnitOctetsType> size)
  {
    return new Size<>(size.getValue().multiply(BigInteger.valueOf(8L)));
  }

  /**
   * Construct a size value of {@code x} units.
   *
   * @param x   The size value
   * @param <U> The precise unit type
   *
   * @return A new size value
   */

  public static <U extends SizeUnitType> Size<U> valueOf(final long x)
  {
    return new Size<>(BigInteger.valueOf(x));
  }

  @Override
  public boolean equals(final Object o)
  {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }

    final Size<?> size = (Size<?>) o;
    return Objects.equals(this.getValue(), size.getValue());
  }

  @Override
  public String toString()
  {
    return this.value.toString();
  }

  @Override
  public int hashCode()
  {
    return this.getValue().hashCode();
  }

  /**
   * @return The raw numeric value
   */

  public BigInteger getValue()
  {
    return this.value;
  }

  /**
   * @param size A size value
   *
   * @return The current value plus {@code size}
   */

  public Size<U> add(final Size<U> size)
  {
    return new Size<>(this.value.add(size.getValue()));
  }
}
