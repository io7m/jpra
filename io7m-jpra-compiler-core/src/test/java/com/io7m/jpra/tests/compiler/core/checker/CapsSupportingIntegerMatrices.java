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

package com.io7m.jpra.tests.compiler.core.checker;

import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.set.ImmutableSet;
import com.gs.collections.impl.factory.Lists;
import com.io7m.jfunctional.Pair;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.compiler.core.checker.JPRACheckerCapabilitiesType;
import com.io7m.jranges.RangeInclusiveB;

import java.math.BigInteger;

final class CapsSupportingIntegerMatrices implements JPRACheckerCapabilitiesType
{
  private final JPRACheckerCapabilitiesType delegate;

  CapsSupportingIntegerMatrices(
    final JPRACheckerCapabilitiesType in_delegate)
  {
    this.delegate = NullCheck.notNull(in_delegate);
  }

  @Override public ImmutableList<RangeInclusiveB> getMatrixFloatSizeSupported()
  {
    return this.delegate.getMatrixFloatSizeSupported();
  }

  @Override
  public ImmutableList<RangeInclusiveB> getPackedIntegerSizeBitsSupported()
  {
    return this.delegate.getPackedIntegerSizeBitsSupported();
  }

  @Override
  public boolean isPackedIntegerSizeBitsSupported(final BigInteger size)
  {
    return this.delegate.isPackedIntegerSizeBitsSupported(size);
  }

  @Override
  public ImmutableList<RangeInclusiveB> getMatrixIntegerSizeSupported()
  {
    return Lists.immutable.of(
      new RangeInclusiveB(
        BigInteger.valueOf(32L), BigInteger.valueOf(32L)));
  }

  @Override
  public ImmutableList<Pair<RangeInclusiveB, RangeInclusiveB>>
  getMatrixSizeElementsSupported()
  {
    return this.delegate.getMatrixSizeElementsSupported();
  }

  @Override
  public ImmutableList<RangeInclusiveB> getRecordFloatSizeBitsSupported()
  {
    return this.delegate.getRecordFloatSizeBitsSupported();
  }

  @Override
  public ImmutableList<RangeInclusiveB> getRecordIntegerSizeBitsSupported()
  {
    return this.delegate.getRecordIntegerSizeBitsSupported();
  }

  @Override public ImmutableSet<String> getStringEncodingsSupported()
  {
    return this.delegate.getStringEncodingsSupported();
  }

  @Override public ImmutableList<RangeInclusiveB> getVectorFloatSizeSupported()
  {
    return this.delegate.getVectorFloatSizeSupported();
  }

  @Override
  public ImmutableList<RangeInclusiveB> getVectorIntegerSizeSupported()
  {
    return this.delegate.getVectorIntegerSizeSupported();
  }

  @Override public ImmutableList<RangeInclusiveB> getVectorSizeSupported()
  {
    return this.delegate.getVectorSizeSupported();
  }

  @Override public boolean isMatrixFloatSizeSupported(final BigInteger size)
  {
    return this.delegate.isMatrixFloatSizeSupported(size);
  }

  @Override public boolean isMatrixIntegerSizeSupported(final BigInteger size)
  {
    return size.equals(BigInteger.valueOf(32L));
  }

  @Override public boolean isMatrixSizeElementsSupported(
    final BigInteger width,
    final BigInteger height)
  {
    return this.delegate.isMatrixSizeElementsSupported(width, height);
  }

  @Override public boolean isRecordFloatSizeBitsSupported(final BigInteger size)
  {
    return this.delegate.isRecordFloatSizeBitsSupported(size);
  }

  @Override
  public boolean isRecordIntegerSizeBitsSupported(final BigInteger size)
  {
    return this.delegate.isRecordIntegerSizeBitsSupported(size);
  }

  @Override public boolean isStringEncodingSupported(final String encoding)
  {
    return this.delegate.isStringEncodingSupported(encoding);
  }

  @Override public boolean isVectorFloatSizeSupported(final BigInteger size)
  {
    return this.delegate.isVectorFloatSizeSupported(size);
  }

  @Override public boolean isVectorIntegerSizeSupported(final BigInteger size)
  {
    return this.delegate.isVectorIntegerSizeSupported(size);
  }

  @Override public boolean isVectorSizeElementsSupported(final BigInteger size)
  {
    return this.delegate.isVectorSizeElementsSupported(size);
  }
}
