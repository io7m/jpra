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

package com.io7m.jpra.model.types;

import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.map.ImmutableMap;
import com.io7m.jfunctional.PartialFunctionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.names.FieldPath;
import com.io7m.junreachable.UnreachableCodeException;

public final class TRecords
{
  private TRecords()
  {
    throw new UnreachableCodeException();
  }

  public static TypeLookupType typeForFieldPath(
    final TRecord t,
    final FieldPath p)
  {
    NullCheck.notNull(t);
    return TRecords.typeForFieldPathActual(
      t, NullCheck.notNull(p).getElements());
  }

  private static TypeLookupType typeForFieldPathActual(
    final TType rt,
    final ImmutableList<FieldName> elements)
  {
    if (elements.isEmpty()) {
      return new TypeLookupSucceeded(rt);
    }

    return rt.matchType(
      new TypeMatcherType<TypeLookupType, UnreachableCodeException>()
      {
        @Override public TypeLookupType matchArray(final TArray t)
        {
          return new TypeLookupFailed(t);
        }

        @Override public TypeLookupType matchString(final TString t)
        {
          return new TypeLookupFailed(t);
        }

        @Override public TypeLookupType matchBooleanSet(final TBooleanSet t)
        {
          return new TypeLookupFailed(t);
        }

        @Override public TypeLookupType matchInteger(final TIntegerType t)
        {
          return new TypeLookupFailed(t);
        }

        @Override public TypeLookupType matchFloat(final TFloat t)
        {
          return new TypeLookupFailed(t);
        }

        @Override public TypeLookupType matchVector(final TVector t)
        {
          return new TypeLookupFailed(t);
        }

        @Override public TypeLookupType matchMatrix(final TMatrix t)
        {
          return new TypeLookupFailed(t);
        }

        @Override public TypeLookupType matchRecord(final TRecord t)
        {
          final ImmutableMap<FieldName, TRecord.FieldValue> by_name =
            t.getFieldsByName();

          final FieldName head = elements.get(0);
          if (!by_name.containsKey(head)) {
            return new TypeLookupFailed(t);
          }

          final TRecord.FieldValue f = by_name.get(head);
          return TRecords.typeForFieldPathActual(
            f.getType(), elements.drop(1));
        }

        @Override public TypeLookupType matchPacked(final TPacked t)
        {
          final ImmutableMap<FieldName, TPacked.FieldValue> by_name =
            t.getFieldsByName();

          final FieldName head = elements.get(0);
          if (!by_name.containsKey(head)) {
            return new TypeLookupFailed(t);
          }

          final TPacked.FieldValue f = by_name.get(head);
          return TRecords.typeForFieldPathActual(
            f.getType(), elements.drop(1));
        }
      });
  }

  public interface TypeLookupType
  {
    <A, E extends Exception> A matchTypeLookup(
      PartialFunctionType<TypeLookupSucceeded, A, E> success,
      PartialFunctionType<TypeLookupFailed, A, E> failure)
      throws E;
  }

  public static final class TypeLookupFailed implements TypeLookupType
  {
    private final TType result;

    public TypeLookupFailed(
      final TType in_result)
    {
      this.result = NullCheck.notNull(in_result);
    }

    @Override public <A, E extends Exception> A matchTypeLookup(
      final PartialFunctionType<TypeLookupSucceeded, A, E> success,
      final PartialFunctionType<TypeLookupFailed, A, E> failure)
      throws E
    {
      return failure.call(this);
    }
  }

  public static final class TypeLookupSucceeded implements TypeLookupType
  {
    private final TType result;

    public TypeLookupSucceeded(
      final TType in_result)
    {
      this.result = NullCheck.notNull(in_result);
    }

    @Override public <A, E extends Exception> A matchTypeLookup(
      final PartialFunctionType<TypeLookupSucceeded, A, E> success,
      final PartialFunctionType<TypeLookupFailed, A, E> failure)
      throws E
    {
      return success.call(this);
    }
  }

}
