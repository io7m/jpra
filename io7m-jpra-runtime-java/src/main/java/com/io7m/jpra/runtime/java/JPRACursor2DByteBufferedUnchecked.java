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

package com.io7m.jpra.runtime.java;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A 2D cursor implementation that addresses values within a {@link ByteBuffer}
 * and does not perform bounds checking.
 *
 * @param <T> The type of addressed values
 */

public final class JPRACursor2DByteBufferedUnchecked<T extends JPRAValueType>
  implements JPRACursor2DType<T>
{
  private final T          instance;
  private final int        element_size;
  private final int        width;
  private final int        height;
  private final int        row_byte_span;
  private final AtomicLong byte_offset;
  private       int        x;
  private       int        y;

  private JPRACursor2DByteBufferedUnchecked(
    final ByteBuffer in_buffer,
    final int in_width,
    final int in_height,
    final JPRAValueByteBufferedConstructorType<T> in_cons)
  {
    Objects.requireNonNull(in_buffer, "Buffer");
    this.byte_offset = new AtomicLong(0L);

    if (in_width <= 0) {
      throw new IllegalArgumentException(
        String.format("Width %d must be positive", Integer.valueOf(in_width)));
    }
    if (in_height <= 0) {
      throw new IllegalArgumentException(
        String.format(
          "Height %d must be positive", Integer.valueOf(in_height)));
    }
    this.width = in_width;
    this.height = in_height;

    this.instance = Objects.requireNonNull(
      Objects.requireNonNull(in_cons, "Constructor").create(in_buffer, this, 0),
      "Constructed value");

    this.element_size = this.instance.sizeOctets();
    if (this.element_size <= 0) {
      throw new IllegalArgumentException("Element size must be positive");
    }

    this.row_byte_span = this.width * this.element_size;
  }

  /**
   * Construct a new cursor, assuming that the given buffer contains a {@code
   * width * height} region of elements of type {@code T}.
   *
   * @param in_buffer The byte buffer
   * @param width     The width of the region
   * @param height    The height of the region
   * @param in_cons   An element value constructor
   * @param <T>       The precise type of elements
   *
   * @return A new cursor
   */

  public static <T extends JPRAValueType> JPRACursor2DType<T> newCursor(
    final ByteBuffer in_buffer,
    final int width,
    final int height,
    final JPRAValueByteBufferedConstructorType<T> in_cons)
  {
    return new JPRACursor2DByteBufferedUnchecked<>(
      in_buffer, width, height, in_cons);
  }

  @Override public T getElementView()
  {
    return this.instance;
  }

  @Override public AtomicLong getByteOffsetObservable()
  {
    return this.byte_offset;
  }

  @Override public int getElementX()
  {
    return this.x;
  }

  @Override public int getElementY()
  {
    return this.y;
  }

  @Override public void setElementPosition(
    final int in_x,
    final int in_y)
    throws IndexOutOfBoundsException
  {
    this.x = in_x;
    this.y = in_y;

    final long lx = (long) in_x;
    final long ly = (long) in_y;
    final long row_bytes = ly * (long) this.row_byte_span;
    final long col_bytes = lx * (long) this.element_size;
    this.byte_offset.set(row_bytes + col_bytes);
  }

  @Override public String toString()
  {
    final StringBuilder sb = new StringBuilder(128);
    sb.append("[Cursor ");
    sb.append(this.x);
    sb.append(",");
    sb.append(this.y);
    sb.append("/");
    sb.append(this.width - 1);
    sb.append(",");
    sb.append(this.height - 1);
    sb.append("]");
    return sb.toString();
  }
}
