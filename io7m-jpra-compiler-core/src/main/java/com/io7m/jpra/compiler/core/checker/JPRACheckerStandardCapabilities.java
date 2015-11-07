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

package com.io7m.jpra.compiler.core.checker;

import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.set.ImmutableSet;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.factory.Sets;
import com.io7m.jfunctional.Pair;
import com.io7m.jranges.RangeInclusiveB;

import java.math.BigInteger;

/**
 * The default implementation of the {@link JPRACheckerCapabilitiesType}
 * interface, specifying the minimum capabilities that all backends are required
 * to support.
 */

public final class JPRACheckerStandardCapabilities
  implements JPRACheckerCapabilitiesType
{
  private final ImmutableList<RangeInclusiveB> packed_integer_sizes;
  private final ImmutableList<RangeInclusiveB> record_integer_sizes;
  private final ImmutableList<RangeInclusiveB> record_float_sizes;
  private final ImmutableSet<String>           encodings;
  private final ImmutableList<RangeInclusiveB> vector_sizes;
  private final ImmutableList<RangeInclusiveB> vector_float_sizes;
  private final ImmutableList<RangeInclusiveB> vector_integer_sizes;
  private final ImmutableList<Pair<RangeInclusiveB, RangeInclusiveB>>
                                               matrix_sizes;
  private final ImmutableList<RangeInclusiveB> matrix_float_sizes;

  private JPRACheckerStandardCapabilities()
  {
    final BigInteger b2 = BigInteger.valueOf(2L);
    final BigInteger b3 = BigInteger.valueOf(3L);
    final BigInteger b4 = BigInteger.valueOf(4L);
    final BigInteger b8 = BigInteger.valueOf(8L);
    final BigInteger b16 = BigInteger.valueOf(16L);
    final BigInteger b32 = BigInteger.valueOf(32L);
    final BigInteger b64 = BigInteger.valueOf(64L);

    {
      final MutableList<RangeInclusiveB> s = Lists.mutable.empty();
      s.add(new RangeInclusiveB(b2, b64));
      this.packed_integer_sizes = s.toImmutable();
    }

    {
      final MutableList<RangeInclusiveB> s = Lists.mutable.empty();
      s.add(new RangeInclusiveB(b8, b8));
      s.add(new RangeInclusiveB(b16, b16));
      s.add(new RangeInclusiveB(b32, b32));
      s.add(new RangeInclusiveB(b64, b64));
      this.record_integer_sizes = s.toImmutable();
    }

    {
      final MutableList<RangeInclusiveB> s = Lists.mutable.empty();
      s.add(new RangeInclusiveB(b16, b16));
      s.add(new RangeInclusiveB(b32, b32));
      s.add(new RangeInclusiveB(b64, b64));
      this.record_float_sizes = s.toImmutable();
    }

    {
      this.encodings = Sets.immutable.of("UTF-8");
    }

    {
      final MutableList<RangeInclusiveB> s = Lists.mutable.empty();
      s.add(new RangeInclusiveB(b2, b2));
      s.add(new RangeInclusiveB(b3, b3));
      s.add(new RangeInclusiveB(b4, b4));
      this.vector_sizes = s.toImmutable();
    }

    {
      final MutableList<RangeInclusiveB> s = Lists.mutable.empty();
      s.add(new RangeInclusiveB(b32, b32));
      s.add(new RangeInclusiveB(b64, b64));
      this.vector_float_sizes = s.toImmutable();
    }

    {
      final MutableList<RangeInclusiveB> s = Lists.mutable.empty();
      s.add(new RangeInclusiveB(b32, b32));
      s.add(new RangeInclusiveB(b64, b64));
      this.vector_integer_sizes = s.toImmutable();
    }

    {
      final MutableList<Pair<RangeInclusiveB, RangeInclusiveB>> s =
        Lists.mutable.empty();

      final RangeInclusiveB r2 = new RangeInclusiveB(b2, b2);
      final RangeInclusiveB r3 = new RangeInclusiveB(b3, b3);
      final RangeInclusiveB r4 = new RangeInclusiveB(b4, b4);
      s.add(Pair.pair(r2, r2));
      s.add(Pair.pair(r3, r3));
      s.add(Pair.pair(r4, r4));
      this.matrix_sizes = s.toImmutable();
    }

    {
      final MutableList<RangeInclusiveB> s = Lists.mutable.empty();
      s.add(new RangeInclusiveB(b32, b32));
      s.add(new RangeInclusiveB(b64, b64));
      this.matrix_float_sizes = s.toImmutable();
    }
  }

  /**
   * @return New capabilities
   */

  public static JPRACheckerCapabilitiesType newCapabilities()
  {
    return new JPRACheckerStandardCapabilities();
  }

  @Override
  public ImmutableList<RangeInclusiveB> getRecordIntegerSizeBitsSupported()
  {
    return this.record_integer_sizes;
  }

  @Override
  public boolean isRecordIntegerSizeBitsSupported(final BigInteger size)
  {
    return this.record_integer_sizes.anySatisfy(r -> r.includesValue(size));
  }

  @Override public boolean isRecordFloatSizeBitsSupported(final BigInteger size)
  {
    return this.record_float_sizes.anySatisfy(r -> r.includesValue(size));
  }

  @Override public boolean isVectorSizeElementsSupported(final BigInteger size)
  {
    return this.vector_sizes.anySatisfy(r -> r.includesValue(size));
  }

  @Override public boolean isMatrixSizeElementsSupported(
    final BigInteger width,
    final BigInteger height)
  {
    return this.matrix_sizes.anySatisfy(
      p -> {
        final RangeInclusiveB l = p.getLeft();
        final RangeInclusiveB r = p.getRight();
        return l.includesValue(width) && r.includesValue(height);
      });
  }

  @Override
  public ImmutableList<RangeInclusiveB> getRecordFloatSizeBitsSupported()
  {
    return this.record_float_sizes;
  }

  @Override public boolean isStringEncodingSupported(final String encoding)
  {
    return this.encodings.contains(encoding);
  }

  @Override public ImmutableSet<String> getStringEncodingsSupported()
  {
    return this.encodings;
  }

  @Override public ImmutableList<RangeInclusiveB> getVectorSizeSupported()
  {
    return this.vector_sizes;
  }

  @Override public boolean isVectorIntegerSizeSupported(final BigInteger size)
  {
    return this.vector_integer_sizes.anySatisfy(r -> r.includesValue(size));
  }

  @Override
  public ImmutableList<RangeInclusiveB> getVectorIntegerSizeSupported()
  {
    return this.vector_integer_sizes;
  }

  @Override public boolean isVectorFloatSizeSupported(final BigInteger size)
  {
    return this.vector_float_sizes.anySatisfy(r -> r.includesValue(size));
  }

  @Override public ImmutableList<RangeInclusiveB> getVectorFloatSizeSupported()
  {
    return this.vector_float_sizes;
  }

  @Override
  public ImmutableList<Pair<RangeInclusiveB, RangeInclusiveB>>
  getMatrixSizeElementsSupported()
  {
    return this.matrix_sizes;
  }

  @Override public boolean isMatrixIntegerSizeSupported(final BigInteger size)
  {
    return false;
  }

  @Override
  public ImmutableList<RangeInclusiveB> getMatrixIntegerSizeSupported()
  {
    return Lists.immutable.empty();
  }

  @Override public boolean isMatrixFloatSizeSupported(final BigInteger size)
  {
    return this.matrix_float_sizes.anySatisfy(r -> r.includesValue(size));
  }

  @Override public ImmutableList<RangeInclusiveB> getMatrixFloatSizeSupported()
  {
    return this.matrix_float_sizes;
  }

  @Override
  public ImmutableList<RangeInclusiveB> getPackedIntegerSizeBitsSupported()
  {
    return this.packed_integer_sizes;
  }

  @Override
  public boolean isPackedIntegerSizeBitsSupported(final BigInteger size)
  {
    return this.packed_integer_sizes.anySatisfy(r -> r.includesValue(size));
  }
}
