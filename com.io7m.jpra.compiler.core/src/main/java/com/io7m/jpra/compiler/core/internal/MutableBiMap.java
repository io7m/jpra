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

package com.io7m.jpra.compiler.core.internal;

import java.util.HashMap;
import java.util.Objects;

/**
 * The default implementation of the {@link MutableBiMapType} interface.
 *
 * @param <A> The type of keys
 * @param <B> The type of values
 */

public final class MutableBiMap<A, B> implements MutableBiMapType<A, B>
{
  private final HashMap<A, B> map_ab;
  private final HashMap<B, A> map_ba;
  private final MutableBiMapType<B, A> inverse;

  private MutableBiMap()
  {
    this.map_ab = new HashMap<>();
    this.map_ba = new HashMap<>();

    this.inverse =
      new MutableBiMapType<>()
      {
        @Override
        public boolean isEmpty()
        {
          return MutableBiMap.this.isEmpty();
        }

        @Override
        public boolean containsKey(final B key)
        {
          return MutableBiMap.this.map_ba.containsKey(
            Objects.requireNonNull(key, "Key"));
        }

        @Override
        public A get(final B key)
        {
          return MutableBiMap.this.map_ba.get(
            Objects.requireNonNull(key, "Key"));
        }

        @Override
        public void put(
          final B key,
          final A value)
        {
          Objects.requireNonNull(key, "Key");
          Objects.requireNonNull(value, "Value");
          MutableBiMap.this.map_ba.put(key, value);
          MutableBiMap.this.map_ab.put(value, key);
        }

        @Override
        public void clear()
        {
          MutableBiMap.this.clear();
        }

        @Override
        public MutableBiMapType<A, B> inverse()
        {
          return MutableBiMap.this;
        }
      };
  }

  /**
   * Create a new empty map.
   *
   * @param <A> The type of keys
   * @param <B> The type of values
   *
   * @return A new empty map
   */

  public static <A, B> MutableBiMapType<A, B> create()
  {
    return new MutableBiMap<>();
  }

  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder("MutableBiMap{");
    sb.append(this.map_ab);
    sb.append('}');
    return sb.toString();
  }

  @Override
  public boolean equals(
    final Object o)
  {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }

    final MutableBiMap<?, ?> that = (MutableBiMap<?, ?>) o;
    return Objects.equals(this.map_ab, that.map_ab);
  }

  @Override
  public int hashCode()
  {
    return this.map_ab.hashCode();
  }

  @Override
  public boolean isEmpty()
  {
    return this.map_ab.isEmpty();
  }

  @Override
  public boolean containsKey(final A key)
  {
    return this.map_ab.containsKey(Objects.requireNonNull(key, "Key"));
  }

  @Override
  public B get(
    final A key)
  {
    return this.map_ab.get(Objects.requireNonNull(key, "Key"));
  }

  @Override
  public void put(
    final A key,
    final B value)
  {
    Objects.requireNonNull(key, "Key");
    Objects.requireNonNull(value, "Value");

    this.map_ab.put(key, value);
    this.map_ba.put(value, key);
  }

  @Override
  public void clear()
  {
    this.map_ba.clear();
    this.map_ab.clear();
  }

  @Override
  public MutableBiMapType<B, A> inverse()
  {
    return this.inverse;
  }
}
