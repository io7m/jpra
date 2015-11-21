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
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * An implementation of the {@link JPRAStringType} that accesses an underlying
 * {@link ByteBuffer}.
 */

public final class JPRAStringByteBuffered implements JPRAStringType
{
  private final ByteBuffer                 buffer;
  private final int                        max_length;
  private final JPRACursorByteReadableType cursor;
  private final int                        offset;
  private final Charset                    encoding;

  private JPRAStringByteBuffered(
    final ByteBuffer in_buffer,
    final int in_offset,
    final JPRACursorByteReadableType in_cursor,
    final Charset in_encoding,
    final int in_max_length)
  {
    this.buffer = Objects.requireNonNull(in_buffer, "Buffer");
    this.cursor = Objects.requireNonNull(in_cursor, "Cursor");
    this.encoding = Objects.requireNonNull(in_encoding, "Encoding");
    this.max_length = in_max_length;
    this.offset = in_offset;

    if (this.max_length <= 0) {
      final String message =
        String.format(
          "Maximum length %d must be positive",
          Integer.valueOf(this.max_length));
      throw new IllegalArgumentException(message);
    }
    if (this.offset < 0) {
      final String message =
        String.format(
          "Offset %d must be non-negative",
          Integer.valueOf(this.offset));
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Construct a new string pointer.
   *
   * @param in_buffer     The byte buffer
   * @param in_offset     The offset of this string from the base cursor
   * @param in_cursor     The base cursor
   * @param in_encoding   The string encoding
   * @param in_max_length The maximum string length
   *
   * @return A new string pointer
   */

  public static JPRAStringType newString(
    final ByteBuffer in_buffer,
    final int in_offset,
    final JPRACursorByteReadableType in_cursor,
    final Charset in_encoding,
    final int in_max_length)
  {
    return new JPRAStringByteBuffered(
      in_buffer,
      in_offset,
      in_cursor,
      in_encoding,
      in_max_length);
  }

  private int getOffsetForStringByte(final int i)
  {
    final int data_start = this.getOffsetForDataStart();
    return data_start + 4 + i;
  }

  private int getOffsetForDataStart()
  {
    final int base = this.cursor.getByteOffsetObservable().intValue();
    return base + this.offset;
  }

  @Override public int getMaximumLength()
  {
    return this.max_length;
  }

  @Override public int getUsedLength()
  {
    final int off = this.getOffsetForDataStart();
    final int val = this.buffer.getInt(off);
    return Math.min(this.max_length, Math.max(0, val));
  }

  @Override public byte getByte(final int index)
    throws IndexOutOfBoundsException
  {
    final int max = this.getMaximumLength();
    if (index >= max || index < 0) {
      final String message = String.format(
        "Index %d must be < %d && > 0",
        Integer.valueOf(index),
        Integer.valueOf(max));
      throw new IndexOutOfBoundsException(message);
    }

    return this.buffer.get(this.getOffsetForStringByte(index));
  }

  @Override public void getBytes(
    final byte[] buf,
    final int buf_offset,
    final int length)
    throws IndexOutOfBoundsException
  {
    final int max = this.getMaximumLength();
    if (length > max) {
      final String message = String.format(
        "Length %d must be < %d",
        Integer.valueOf(length),
        Integer.valueOf(max));
      throw new IndexOutOfBoundsException(message);
    }

    final int data_offset = this.getOffsetForStringByte(0);
    final int old_pos = this.buffer.position();
    this.buffer.position(data_offset);
    this.buffer.get(buf, buf_offset, length);
    this.buffer.position(old_pos);
  }

  @Override public String getNewValue()
  {
    final int used = this.getUsedLength();
    final byte[] buf = new byte[used];
    final int b_pos = this.getOffsetForStringByte(0);
    final int old_pos = this.buffer.position();
    this.buffer.position(b_pos);
    this.buffer.get(buf, 0, used);
    this.buffer.position(old_pos);
    return new String(buf, this.encoding);
  }

  @Override public Charset getEncoding()
  {
    return this.encoding;
  }

  @Override public void setValue(
    final String text,
    final JPRAStringTruncation trunc)
  {
    final byte[] bytes = text.getBytes(this.encoding);
    final int max = this.getMaximumLength();
    final int length = Math.min(bytes.length, max);

    switch (trunc) {
      case TRUNCATE: {
        break;
      }
      case REJECT: {
        if (bytes.length > max) {
          final String message = String.format(
            "Bytes length is %d, which must be less than %d",
            Integer.valueOf(bytes.length),
            Integer.valueOf(max));
          throw new IndexOutOfBoundsException(message);
        }
        break;
      }
    }

    final int len_offset = this.getOffsetForDataStart();
    this.buffer.putInt(len_offset, length);
    final int data_offset = len_offset + 4;
    final int old_pos = this.buffer.position();
    this.buffer.position(data_offset);
    this.buffer.put(bytes, 0, length);
    this.buffer.position(old_pos);
  }
}
