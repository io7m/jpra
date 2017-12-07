/*
 * Copyright © 2017 <code@io7m.com> http://io7m.com
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

package com.io7m.jpra.compiler.core.bimap;

/**
 * A mutable bi-map.
 *
 * @param <A> The type of keys
 * @param <B> The type of values
 */

public interface MutableBiMapType<A, B>
{
  /**
   * @return {@code true} iff the map is empty
   */

  boolean isEmpty();

  /**
   * @param key The key
   *
   * @return {@code true} iff the map contains a mapping for key {@code key}
   */

  boolean containsKey(A key);

  /**
   * @param key The key
   *
   * @return The value for key {@code key}, or {@code null} if none exists
   */

  B get(A key);

  /**
   * Insert (or replace) a mapping from a key to a value.
   *
   * @param key   The key
   * @param value The value
   */

  void put(
    A key,
    B value);

  /**
   * Clear the map.
   */

  void clear();

  /**
   * A (writable) view of the current map containing mappings from values to
   * keys.
   *
   * @return An inverse view of the map
   */

  MutableBiMapType<B, A> inverse();
}
