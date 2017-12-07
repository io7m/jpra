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

import com.io7m.jranges.RangeInclusiveB;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Set;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * The default implementation of the {@link JPRACheckerCapabilitiesType}
 * interface, specifying the minimum capabilities that all backends are required
 * to support.
 */

public final class JPRACheckerStandardCapabilities
  implements JPRACheckerCapabilitiesType
{
  private final List<RangeInclusiveB> packed_integer_sizes;
  private final List<RangeInclusiveB> record_integer_sizes;
  private final List<RangeInclusiveB> record_float_sizes;
  private final Set<String> encodings;
  private final List<RangeInclusiveB> vector_sizes;
  private final List<RangeInclusiveB> vector_float_sizes;
  private final List<RangeInclusiveB> vector_integer_sizes;
  private final List<Tuple2<RangeInclusiveB, RangeInclusiveB>> matrix_sizes;
  private final List<RangeInclusiveB> matrix_float_sizes;
  private final List<RangeInclusiveB> packed_sizes;

  private JPRACheckerStandardCapabilities()
  {
    final BigInteger b1 = BigInteger.valueOf(1L);
    final BigInteger b2 = BigInteger.valueOf(2L);
    final BigInteger b64 = BigInteger.valueOf(64L);

    {
      final ArrayList<RangeInclusiveB> s = new ArrayList<>();
      s.add(RangeInclusiveB.of(b1, b64));
      this.packed_integer_sizes = List.ofAll(s);
    }

    final BigInteger b32 = BigInteger.valueOf(32L);
    final BigInteger b16 = BigInteger.valueOf(16L);
    final BigInteger b8 = BigInteger.valueOf(8L);
    {
      final ArrayList<RangeInclusiveB> s = new ArrayList<>();
      s.add(RangeInclusiveB.of(b8, b8));
      s.add(RangeInclusiveB.of(b16, b16));
      s.add(RangeInclusiveB.of(b32, b32));
      s.add(RangeInclusiveB.of(b64, b64));
      this.record_integer_sizes = List.ofAll(s);
    }

    {
      final ArrayList<RangeInclusiveB> s = new ArrayList<>();
      s.add(RangeInclusiveB.of(b16, b16));
      s.add(RangeInclusiveB.of(b32, b32));
      s.add(RangeInclusiveB.of(b64, b64));
      this.record_float_sizes = List.ofAll(s);
    }

    {
      this.encodings = HashSet.of("UTF-8");
    }

    final BigInteger b4 = BigInteger.valueOf(4L);
    final BigInteger b3 = BigInteger.valueOf(3L);
    {
      final ArrayList<RangeInclusiveB> s = new ArrayList<>();
      s.add(RangeInclusiveB.of(b2, b2));
      s.add(RangeInclusiveB.of(b3, b3));
      s.add(RangeInclusiveB.of(b4, b4));
      this.vector_sizes = List.ofAll(s);
    }

    {
      final ArrayList<RangeInclusiveB> s = new ArrayList<>();
      s.add(RangeInclusiveB.of(b16, b16));
      s.add(RangeInclusiveB.of(b32, b32));
      s.add(RangeInclusiveB.of(b64, b64));
      this.vector_float_sizes = List.ofAll(s);
    }

    {
      final ArrayList<RangeInclusiveB> s = new ArrayList<>();
      s.add(RangeInclusiveB.of(b32, b32));
      s.add(RangeInclusiveB.of(b64, b64));
      this.vector_integer_sizes = List.ofAll(s);
    }

    {
      final ArrayList<Tuple2<RangeInclusiveB, RangeInclusiveB>> s =
        new ArrayList<>();

      final RangeInclusiveB r2 = RangeInclusiveB.of(b2, b2);
      s.add(Tuple.of(r2, r2));
      final RangeInclusiveB r3 = RangeInclusiveB.of(b3, b3);
      s.add(Tuple.of(r3, r3));
      final RangeInclusiveB r4 = RangeInclusiveB.of(b4, b4);
      s.add(Tuple.of(r4, r4));
      this.matrix_sizes = List.ofAll(s);
    }

    {
      final ArrayList<RangeInclusiveB> s = new ArrayList<>();
      s.add(RangeInclusiveB.of(b32, b32));
      s.add(RangeInclusiveB.of(b64, b64));
      this.matrix_float_sizes = List.ofAll(s);
    }

    {
      final ArrayList<RangeInclusiveB> s = new ArrayList<>();
      s.add(RangeInclusiveB.of(b8, b8));
      s.add(RangeInclusiveB.of(b16, b16));
      s.add(RangeInclusiveB.of(b32, b32));
      s.add(RangeInclusiveB.of(b64, b64));
      this.packed_sizes = List.ofAll(s);
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
  public List<RangeInclusiveB> getRecordIntegerSizeBitsSupported()
  {
    return this.record_integer_sizes;
  }

  @Override
  public boolean isRecordIntegerSizeBitsSupported(final BigInteger size)
  {
    return this.record_integer_sizes.find(r -> r.includesValue(size)).isDefined();
  }

  @Override
  public boolean isRecordFloatSizeBitsSupported(final BigInteger size)
  {
    return this.record_float_sizes.find(r -> r.includesValue(size)).isDefined();
  }

  @Override
  public boolean isVectorSizeElementsSupported(final BigInteger size)
  {
    return this.vector_sizes.find(r -> r.includesValue(size)).isDefined();
  }

  @Override
  public boolean isMatrixSizeElementsSupported(
    final BigInteger width,
    final BigInteger height)
  {
    return this.matrix_sizes.find(
      p -> {
        final RangeInclusiveB l = p._1;
        final RangeInclusiveB r = p._2;
        return l.includesValue(width) && r.includesValue(height);
      }).isDefined();
  }

  @Override
  public List<RangeInclusiveB> getRecordFloatSizeBitsSupported()
  {
    return this.record_float_sizes;
  }

  @Override
  public boolean isStringEncodingSupported(final String encoding)
  {
    return this.encodings.contains(encoding);
  }

  @Override
  public Set<String> getStringEncodingsSupported()
  {
    return this.encodings;
  }

  @Override
  public List<RangeInclusiveB> getVectorSizeSupported()
  {
    return this.vector_sizes;
  }

  @Override
  public boolean isVectorIntegerSizeSupported(final BigInteger size)
  {
    return this.vector_integer_sizes.find(r -> r.includesValue(size)).isDefined();
  }

  @Override
  public List<RangeInclusiveB> getVectorIntegerSizeSupported()
  {
    return this.vector_integer_sizes;
  }

  @Override
  public boolean isVectorFloatSizeSupported(final BigInteger size)
  {
    return this.vector_float_sizes.find(r -> r.includesValue(size)).isDefined();
  }

  @Override
  public List<RangeInclusiveB> getVectorFloatSizeSupported()
  {
    return this.vector_float_sizes;
  }

  @Override
  public List<Tuple2<RangeInclusiveB, RangeInclusiveB>>
  getMatrixSizeElementsSupported()
  {
    return this.matrix_sizes;
  }

  @Override
  public boolean isMatrixIntegerSizeSupported(final BigInteger size)
  {
    return false;
  }

  @Override
  public List<RangeInclusiveB> getMatrixIntegerSizeSupported()
  {
    return List.empty();
  }

  @Override
  public boolean isMatrixFloatSizeSupported(final BigInteger size)
  {
    return this.matrix_float_sizes.find(r -> r.includesValue(size)).isDefined();
  }

  @Override
  public List<RangeInclusiveB> getMatrixFloatSizeSupported()
  {
    return this.matrix_float_sizes;
  }

  @Override
  public List<RangeInclusiveB> getPackedIntegerSizeBitsSupported()
  {
    return this.packed_integer_sizes;
  }

  @Override
  public boolean isPackedIntegerSizeBitsSupported(final BigInteger size)
  {
    return this.packed_integer_sizes.find(r -> r.includesValue(size)).isDefined();
  }

  @Override
  public List<RangeInclusiveB> getPackedSizeBitsSupported()
  {
    return this.packed_sizes;
  }

  @Override
  public boolean isPackedSizeBitsSupported(final BigInteger size)
  {
    return this.packed_sizes.find(r -> r.includesValue(size)).isDefined();
  }
}
