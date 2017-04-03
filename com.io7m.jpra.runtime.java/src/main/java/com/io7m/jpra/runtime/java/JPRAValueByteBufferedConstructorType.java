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

/**
 * The type of constructors that yield {@code ByteBuffer}-backed values.
 *
 * @param <T> The precise type of values
 */

public interface JPRAValueByteBufferedConstructorType<T extends JPRAValueType>
{
  /**
   * Construct a new value.
   *
   * @param buffer The {@link ByteBuffer} containing one or more elements of
   *               {@code T}
   * @param cursor The cursor used to address {@code buffer}
   * @param offset The offset from {@code cursor} that will be used to construct
   *               {@code T}
   *
   * @return A new value
   */

  T create(
    ByteBuffer buffer,
    JPRACursorByteReadableType cursor,
    int offset);
}
