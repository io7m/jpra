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

/**
 * The type of cursors that address 2D arrays of values of type {@code T}.
 *
 * @param <T> The type of addressed elements
 */

public interface JPRACursor2DType<T> extends JPRACursorByteReadableType,
  JPRACursorType<T>
{
  /**
   * @return The current element {@code x} position
   */

  int getElementX();

  /**
   * @return The current element {@code y} position
   */

  int getElementY();

  /**
   * <p>Point the cursor at element {@code (x, y)} of the array.</p>
   *
   * <p>Implementations are permitted to perform bounds checking, and must throw
   * {@link IndexOutOfBoundsException} on out-of-bounds indices.</p>
   *
   * @param x The {@code x} (horizontal, columns) position
   * @param y The {@code y} (vertical, rows) position
   *
   * @throws IndexOutOfBoundsException Iff the implementation performs bounds
   *                                   checking and the element is out of range
   */

  void setElementPosition(
    int x,
    int y)
    throws IndexOutOfBoundsException;

}
