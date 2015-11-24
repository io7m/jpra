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

import java.nio.charset.Charset;

/**
 * The type of read-only pointers to strings.
 */

public interface JPRAStringCursorReadableType
{
  /**
   * @return The maximum length of the string in octets
   */

  int getMaximumLength();

  /**
   * @return The number of octets of the string that are actually being used
   */

  int getUsedLength();

  /**
   * @param index The byte index
   *
   * @return The byte at index {@code index} in the string
   *
   * @throws IndexOutOfBoundsException Iff {@code index >= getMaximumLength()}
   */

  byte getByte(int index)
    throws IndexOutOfBoundsException;

  /**
   * @param buf    The buffer to which string data will be written
   * @param offset The starting index of {@code buf} to which data will be
   *               written
   * @param length The number of octets to copy into {@code buf}
   *
   * @throws IndexOutOfBoundsException Iff {@code length > getMaximumLength()}
   */

  void getBytes(
    byte[] buf,
    int offset,
    int length)
    throws IndexOutOfBoundsException;

  /**
   * @return A freshly allocated string based on the current contents of the
   * string data
   */

  String getNewValue();

  /**
   * @return The string encoding
   */

  Charset getEncoding();
}
