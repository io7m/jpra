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

package com.io7m.jpra.model.type_expressions;

/**
 * @param <A>  The type of returned values
 * @param <E>  The type of raised exceptions
 */

public interface TypeExprMatcherType<I, A, E extends Exception>
{
  A matchExprIntegerSigned(
    TypeExprIntegerSigned<I> e)
    throws E;

  A matchExprIntegerSignedNormalized(
    TypeExprIntegerSignedNormalized<I> e)
    throws E;

  A matchExprIntegerUnsigned(
    TypeExprIntegerUnsigned<I> e)
    throws E;

  A matchExprIntegerUnsignedNormalized(
    TypeExprIntegerUnsignedNormalized<I> e)
    throws E;

  A matchExprArray(
    TypeExprArray<I> e)
    throws E;

  A matchExprFloat(
    TypeExprFloat<I> e)
    throws E;

  A matchExprVector(
    TypeExprVector<I> e)
    throws E;

  A matchExprMatrix(TypeExprMatrix<I> e)
    throws E;

  A matchExprString(TypeExprString<I> e)
    throws E;

  A matchName(TypeExprName<I> e)
    throws E;

  A matchTypeOfField(TypeExprTypeOfField<I> e)
    throws E;

  A matchBooleanSet(
    TypeExprBooleanSet<I> e)
    throws E;
}
