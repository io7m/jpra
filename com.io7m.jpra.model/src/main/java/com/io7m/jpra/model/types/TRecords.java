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

import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.names.FieldPath;
import com.io7m.junreachable.UnreachableCodeException;
import io.vavr.collection.List;
import io.vavr.collection.Map;

import java.util.Objects;

/**
 * Functions over record types.
 */

public final class TRecords
{
  private TRecords()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Lookup a type based on a field path.
   *
   * @param t The starting type
   * @param p The path
   *
   * @return A lookup result
   */

  public static TypeLookupType typeForFieldPath(
    final TRecord t,
    final FieldPath p)
  {
    Objects.requireNonNull(t, "Record");
    Objects.requireNonNull(p, "Field path");

    final List<FieldName> es = p.elements();
    final FieldName first = es.get(0);
    final List<FieldName> rest = es.drop(1);

    return typeForFieldPathActual(t, first, rest);
  }

  private static TypeLookupType typeForFieldPathActual(
    final TType rt,
    final FieldName name,
    final List<FieldName> rest)
  {
    return rt.matchType(new TypeForFieldPathMapper(name, rest));
  }

  /**
   * The type of partial functions.
   *
   * @param <A> The type of domain values
   * @param <B> The type of codomain values
   * @param <E> The type of raised exceptions
   */

  @FunctionalInterface
  public interface PartialFunctionType<A, B, E extends Exception>
  {
    /**
     * Apply the function
     *
     * @param x The input
     *
     * @return The output
     *
     * @throws E If required
     */

    B apply(A x)
      throws E;
  }

  /**
   * The type of lookup results.
   */

  public interface TypeLookupType
  {
    /**
     * Evaluate functions to inspect the lookup result.
     *
     * @param <A>     The type of returned values
     * @param <E>     The type of raised exceptions
     * @param success Evaluated if this result denotes success
     * @param failure Evaluated if this result denotes failure
     *
     * @return The value returned by {@code m}
     *
     * @throws E If {@code m} raises {@code E}
     */

    <A, E extends Exception> A matchTypeLookup(
      PartialFunctionType<TypeLookupSucceeded, A, E> success,
      PartialFunctionType<TypeLookupFailed, A, E> failure)
      throws E;
  }

  /**
   * The type of lookup failures.
   */

  public static final class TypeLookupFailed implements TypeLookupType
  {
    private final TType end;
    private final FieldName name;
    private final List<FieldName> rest;

    /**
     * Construct a failure.
     *
     * @param in_t    The last type encountered before the failure
     * @param in_name The last name
     * @param in_rest The remaining path elements
     */

    public TypeLookupFailed(
      final TType in_t,
      final FieldName in_name,
      final List<FieldName> in_rest)
    {
      this.end = Objects.requireNonNull(in_t, "Type");
      this.name = Objects.requireNonNull(in_name, "Field name");
      this.rest = Objects.requireNonNull(in_rest, "Field names");
    }

    @Override
    public <A, E extends Exception> A matchTypeLookup(
      final PartialFunctionType<TypeLookupSucceeded, A, E> success,
      final PartialFunctionType<TypeLookupFailed, A, E> failure)
      throws E
    {
      return failure.apply(this);
    }
  }

  /**
   * The type of successful lookups.
   */

  public static final class TypeLookupSucceeded implements TypeLookupType
  {
    private final TType result;

    /**
     * Construct a success result.
     *
     * @param in_result The resulting type
     */

    public TypeLookupSucceeded(
      final TType in_result)
    {
      this.result = Objects.requireNonNull(in_result, "Type");
    }

    @Override
    public <A, E extends Exception> A matchTypeLookup(
      final PartialFunctionType<TypeLookupSucceeded, A, E> success,
      final PartialFunctionType<TypeLookupFailed, A, E> failure)
      throws E
    {
      return success.apply(this);
    }
  }

  private static final class TypeForFieldPathMapper
    implements TypeMatcherType<TypeLookupType, UnreachableCodeException>
  {
    private final FieldName name;
    private final List<FieldName> rest;

    TypeForFieldPathMapper(
      final FieldName in_name,
      final List<FieldName> in_rest)
    {
      this.name = in_name;
      this.rest = in_rest;
    }

    @Override
    public TypeLookupType matchArray(final TArray t)
    {
      return new TypeLookupFailed(t, this.name, this.rest);
    }

    @Override
    public TypeLookupType matchString(final TString t)
    {
      return new TypeLookupFailed(t, this.name, this.rest);
    }

    @Override
    public TypeLookupType matchBooleanSet(final TBooleanSet t)
    {
      return new TypeLookupFailed(t, this.name, this.rest);
    }

    @Override
    public TypeLookupType matchInteger(final TIntegerType t)
    {
      return new TypeLookupFailed(t, this.name, this.rest);
    }

    @Override
    public TypeLookupType matchFloat(final TFloat t)
    {
      return new TypeLookupFailed(t, this.name, this.rest);
    }

    @Override
    public TypeLookupType matchVector(final TVector t)
    {
      return new TypeLookupFailed(t, this.name, this.rest);
    }

    @Override
    public TypeLookupType matchMatrix(final TMatrix t)
    {
      return new TypeLookupFailed(t, this.name, this.rest);
    }

    @Override
    public TypeLookupType matchRecord(final TRecord t)
    {
      final Map<FieldName, TRecord.FieldValue> by_name =
        t.getFieldsByName();

      if (!by_name.containsKey(this.name)) {
        return new TypeLookupFailed(t, this.name, this.rest);
      }

      final TRecord.FieldValue f = by_name.get(this.name).get();
      final FieldName next = this.rest.get(0);
      return typeForFieldPathActual(
        f.getType(), next, this.rest.drop(1));
    }

    @Override
    public TypeLookupType matchPacked(final TPacked t)
    {
      final Map<FieldName, TPacked.FieldValue> by_name =
        t.getFieldsByName();

      if (!by_name.containsKey(this.name)) {
        return new TypeLookupFailed(t, this.name, this.rest);
      }

      final TPacked.FieldValue f = by_name.get(this.name).get();
      final FieldName next = this.rest.get(0);
      return typeForFieldPathActual(
        f.getType(), next, this.rest.drop(1));
    }
  }
}
