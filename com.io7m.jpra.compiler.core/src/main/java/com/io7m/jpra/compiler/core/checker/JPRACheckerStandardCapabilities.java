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
import java.util.Collection;

/**
 * The default implementation of the {@link JPRACheckerCapabilitiesType}
 * interface, specifying the minimum capabilities that all backends are required
 * to support.
 */

public final class JPRACheckerStandardCapabilities
  implements JPRACheckerCapabilitiesType
{
  private static final BigInteger BIG_1 = BigInteger.valueOf(1L);
  private static final BigInteger BIG_2 = BigInteger.valueOf(2L);
  private static final BigInteger BIG_64 = BigInteger.valueOf(64L);
  private static final BigInteger BIG_32 = BigInteger.valueOf(32L);
  private static final BigInteger BIG_16 = BigInteger.valueOf(16L);
  private static final BigInteger BIG_8 = BigInteger.valueOf(8L);
  private static final BigInteger BIG_4 = BigInteger.valueOf(4L);
  private static final BigInteger BIG_3 = BigInteger.valueOf(3L);

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
    this.packed_integer_sizes = makePackedIntegerSizes();
    this.record_integer_sizes = makeRecordIntegerSizes();
    this.record_float_sizes = makeRecordFloatSizes();
    this.encodings = HashSet.of("UTF-8");
    this.vector_sizes = makeVectorSizes();
    this.vector_float_sizes = makeVectorFloatSizes();
    this.vector_integer_sizes = makeVectorIntegerSizes();
    this.matrix_sizes = makeMatrixSizes();
    this.matrix_float_sizes = makeMatrixFloatSizes();
    this.packed_sizes = makePackedSizes();
  }

  private static List<RangeInclusiveB> makePackedSizes()
  {
    final Collection<RangeInclusiveB> s = new ArrayList<>(4);
    s.add(RangeInclusiveB.of(BIG_8, BIG_8));
    s.add(RangeInclusiveB.of(BIG_16, BIG_16));
    s.add(RangeInclusiveB.of(BIG_32, BIG_32));
    s.add(RangeInclusiveB.of(BIG_64, BIG_64));
    return List.ofAll(s);
  }

  private static List<RangeInclusiveB> makeMatrixFloatSizes()
  {
    final Collection<RangeInclusiveB> s = new ArrayList<>(2);
    s.add(RangeInclusiveB.of(BIG_32, BIG_32));
    s.add(RangeInclusiveB.of(BIG_64, BIG_64));
    return List.ofAll(s);
  }

  private static List<Tuple2<RangeInclusiveB, RangeInclusiveB>> makeMatrixSizes()
  {
    final Collection<Tuple2<RangeInclusiveB, RangeInclusiveB>> s = new ArrayList<>(3);
    final RangeInclusiveB r2 = RangeInclusiveB.of(BIG_2, BIG_2);
    s.add(Tuple.of(r2, r2));
    final RangeInclusiveB r3 = RangeInclusiveB.of(BIG_3, BIG_3);
    s.add(Tuple.of(r3, r3));
    final RangeInclusiveB r4 = RangeInclusiveB.of(BIG_4, BIG_4);
    s.add(Tuple.of(r4, r4));
    return List.ofAll(s);
  }

  private static List<RangeInclusiveB> makeVectorIntegerSizes()
  {
    final Collection<RangeInclusiveB> s = new ArrayList<>(2);
    s.add(RangeInclusiveB.of(BIG_32, BIG_32));
    s.add(RangeInclusiveB.of(BIG_64, BIG_64));
    return List.ofAll(s);
  }

  private static List<RangeInclusiveB> makeVectorFloatSizes()
  {
    final Collection<RangeInclusiveB> s = new ArrayList<>(3);
    s.add(RangeInclusiveB.of(BIG_16, BIG_16));
    s.add(RangeInclusiveB.of(BIG_32, BIG_32));
    s.add(RangeInclusiveB.of(BIG_64, BIG_64));
    return List.ofAll(s);
  }

  private static List<RangeInclusiveB> makeVectorSizes()
  {
    final Collection<RangeInclusiveB> s = new ArrayList<>(3);
    s.add(RangeInclusiveB.of(BIG_2, BIG_2));
    s.add(RangeInclusiveB.of(BIG_3, BIG_3));
    s.add(RangeInclusiveB.of(BIG_4, BIG_4));
    return List.ofAll(s);
  }

  private static List<RangeInclusiveB> makeRecordFloatSizes()
  {
    final Collection<RangeInclusiveB> s = new ArrayList<>(3);
    s.add(RangeInclusiveB.of(BIG_16, BIG_16));
    s.add(RangeInclusiveB.of(BIG_32, BIG_32));
    s.add(RangeInclusiveB.of(BIG_64, BIG_64));
    return List.ofAll(s);
  }

  private static List<RangeInclusiveB> makeRecordIntegerSizes()
  {
    final Collection<RangeInclusiveB> s = new ArrayList<>(4);
    s.add(RangeInclusiveB.of(BIG_8, BIG_8));
    s.add(RangeInclusiveB.of(BIG_16, BIG_16));
    s.add(RangeInclusiveB.of(BIG_32, BIG_32));
    s.add(RangeInclusiveB.of(BIG_64, BIG_64));
    return List.ofAll(s);
  }

  private static List<RangeInclusiveB> makePackedIntegerSizes()
  {
    final Collection<RangeInclusiveB> s = new ArrayList<>(1);
    s.add(RangeInclusiveB.of(BIG_1, BIG_64));
    return List.ofAll(s);
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
