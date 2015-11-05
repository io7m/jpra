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
    NullCheck.notNull(p);

    final ImmutableList<FieldName> es = p.getElements();
    final FieldName first = es.get(0);
    final ImmutableList<FieldName> rest = es.drop(1);

    return TRecords.typeForFieldPathActual(t, first, rest);
  }

  private static TypeLookupType typeForFieldPathActual(
    final TType rt,
    final FieldName name,
    final ImmutableList<FieldName> rest)
  {
    return rt.matchType(
      new TypeMatcherType<TypeLookupType, UnreachableCodeException>()
      {
        @Override public TypeLookupType matchArray(final TArray t)
        {
          return new TypeLookupFailed(t, name, rest);
        }

        @Override public TypeLookupType matchString(final TString t)
        {
          return new TypeLookupFailed(t, name, rest);
        }

        @Override public TypeLookupType matchBooleanSet(final TBooleanSet t)
        {
          return new TypeLookupFailed(t, name, rest);
        }

        @Override public TypeLookupType matchInteger(final TIntegerType t)
        {
          return new TypeLookupFailed(t, name, rest);
        }

        @Override public TypeLookupType matchFloat(final TFloat t)
        {
          return new TypeLookupFailed(t, name, rest);
        }

        @Override public TypeLookupType matchVector(final TVector t)
        {
          return new TypeLookupFailed(t, name, rest);
        }

        @Override public TypeLookupType matchMatrix(final TMatrix t)
        {
          return new TypeLookupFailed(t, name, rest);
        }

        @Override public TypeLookupType matchRecord(final TRecord t)
        {
          final ImmutableMap<FieldName, TRecord.FieldValue> by_name =
            t.getFieldsByName();

          if (!by_name.containsKey(name)) {
            return new TypeLookupFailed(t, name, rest);
          }

          final TRecord.FieldValue f = by_name.get(name);
          final FieldName next = rest.get(0);
          return TRecords.typeForFieldPathActual(
            f.getType(), next, rest.drop(1));
        }

        @Override public TypeLookupType matchPacked(final TPacked t)
        {
          final ImmutableMap<FieldName, TPacked.FieldValue> by_name =
            t.getFieldsByName();

          if (!by_name.containsKey(name)) {
            return new TypeLookupFailed(t, name, rest);
          }

          final TPacked.FieldValue f = by_name.get(name);
          final FieldName next = rest.get(0);
          return TRecords.typeForFieldPathActual(
            f.getType(), next, rest.drop(1));
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
    private final TType                    end;
    private final FieldName                name;
    private final ImmutableList<FieldName> rest;

    public TypeLookupFailed(
      final TType in_t,
      final FieldName in_name,
      final ImmutableList<FieldName> in_rest)
    {
      this.end = NullCheck.notNull(in_t);
      this.name = NullCheck.notNull(in_name);
      this.rest = NullCheck.notNull(in_rest);
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
