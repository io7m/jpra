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

import com.io7m.mutable.numbers.core.MutableLong;
import com.io7m.mutable.numbers.core.MutableLongType;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * A 1D cursor implementation that addresses values within a {@link ByteBuffer}
 * and performs bounds checking.
 *
 * @param <T> The type of addressed values
 */

public final class JPRACursor1DByteBufferedChecked<T extends JPRAValueType>
  implements JPRACursor1DType<T>
{
  private final T instance;
  private final int element_size;
  private final int index_max;
  private final MutableLong byte_offset;
  private int index;

  private JPRACursor1DByteBufferedChecked(
    final ByteBuffer in_buffer,
    final JPRAValueByteBufferedConstructorType<T> in_cons)
  {
    Objects.requireNonNull(in_buffer, "Buffer");
    this.byte_offset = MutableLong.create();
    this.instance = Objects.requireNonNull(
      Objects.requireNonNull(in_cons, "Constructor").create(in_buffer, this, 0),
      "Constructed value");

    this.element_size = this.instance.sizeOctets();
    if (this.element_size <= 0) {
      throw new IllegalArgumentException("Element size must be positive");
    }

    final int capacity = in_buffer.capacity();
    if (capacity < this.element_size) {
      throw new IllegalArgumentException(
        String.format(
          "Buffer of size %d is too small for one element of size %d",
          Integer.valueOf(capacity),
          Integer.valueOf(this.element_size)));
    }

    this.index = 0;
    this.index_max = (capacity / this.element_size) - 1;
    assert this.index_max >= 0;
  }

  /**
   * Construct a new cursor, assuming that the given buffer contains elements of
   * type {@code T}.
   *
   * @param in_buffer The byte buffer
   * @param in_cons   An element value constructor
   * @param <T>       The precise type of elements
   *
   * @return A new cursor
   */

  public static <T extends JPRAValueType> JPRACursor1DType<T> newCursor(
    final ByteBuffer in_buffer,
    final JPRAValueByteBufferedConstructorType<T> in_cons)
  {
    return new JPRACursor1DByteBufferedChecked<>(in_buffer, in_cons);
  }

  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder("[Cursor ");
    sb.append(this.index);
    sb.append('/');
    sb.append(this.index_max);
    sb.append(']');
    return sb.toString();
  }

  @Override
  public int getElementIndex()
  {
    return this.index;
  }

  @Override
  public void setElementIndex(final int new_index)
    throws IndexOutOfBoundsException
  {
    if (new_index <= this.index_max && new_index >= 0) {
      this.index = new_index;
      this.byte_offset.setValue(this.index * this.element_size);
    } else {
      throw new IndexOutOfBoundsException(
        String.format(
          "Index %d must be within the range [0, %d]",
          Integer.valueOf(this.index),
          Integer.valueOf(this.index_max)));
    }
  }

  @Override
  public T getElementView()
  {
    return this.instance;
  }

  @Override
  public MutableLongType getByteOffsetObservable()
  {
    return this.byte_offset;
  }
}
