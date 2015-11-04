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
 * @param <TN> The type of identifiers
 * @param <FN> The type of field identifiers
 * @param <T>  The type of evaluated types
 * @param <A>  The type of returned values
 * @param <E>  The type of raised exceptions
 */

public interface TypeExprMatcherType<TN, TR, FN, FR, T, A, E extends Exception>
{
  A matchExprIntegerSigned(
    TypeExprIntegerSigned<TN, TR, FN, FR, T> e)
    throws E;

  A matchExprIntegerSignedNormalized(
    TypeExprIntegerSignedNormalized<TN, TR, FN, FR, T> e)
    throws E;

  A matchExprIntegerUnsigned(
    TypeExprIntegerUnsigned<TN, TR, FN, FR, T> e)
    throws E;

  A matchExprIntegerUnsignedNormalized(
    TypeExprIntegerUnsignedNormalized<TN, TR, FN, FR, T> e)
    throws E;

  A matchExprArray(
    TypeExprArray<TN, TR, FN, FR, T> e)
    throws E;

  A matchExprFloat(
    TypeExprFloat<TN, TR, FN, FR, T> e)
    throws E;

  A matchExprVector(
    TypeExprVector<TN, TR, FN, FR, T> e)
    throws E;

  A matchExprMatrix(TypeExprMatrix<TN, TR, FN, FR, T> e)
    throws E;

  A matchExprString(TypeExprString<TN, TR, FN, FR, T> e)
    throws E;

  A matchName(TypeExprName<TN, TR, FN, FR, T> e)
    throws E;

  A matchTypeOfField(TypeExprTypeOfField<TN, TR, FN, FR, T> e)
    throws E;

  A matchBooleanSet(
    TypeExprBooleanSet<TN, TR, FN, FR, T> e)
    throws E;
}
