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

import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.map.ImmutableMap;
import com.io7m.jlexing.core.ImmutableLexicalPositionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.ModelElementType;
import com.io7m.jpra.model.Size;
import com.io7m.jpra.model.SizeUnitBitsType;
import com.io7m.jpra.model.identifiers.ResolvedFieldIdentifier;
import com.io7m.jpra.model.identifiers.ResolvedTypeIdentifier;
import org.valid4j.Assertive;

import java.nio.file.Path;
import java.util.Optional;

/**
 * A {@code record} type.
 */

public final class TRecord implements TType
{
  private final ResolvedTypeIdentifier                            ident;
  private final Size<SizeUnitBitsType>                            size_bits;
  private final ImmutableMap<ResolvedFieldIdentifier, FieldValue>
                                                                  fields_by_name;
  private final ImmutableList<FieldType>
                                                                  fields_by_order;

  /**
   * Construct a record type.
   *
   * @param in_fields_by_name  The fields by name
   * @param in_ident           The identifier
   * @param in_fields_by_order The fields in declaration order
   */

  public TRecord(
    final ImmutableMap<ResolvedFieldIdentifier, FieldValue> in_fields_by_name,
    final ResolvedTypeIdentifier in_ident,
    final ImmutableList<FieldType> in_fields_by_order)
  {
    this.fields_by_name = NullCheck.notNull(in_fields_by_name);
    this.ident = NullCheck.notNull(in_ident);
    this.fields_by_order = NullCheck.notNull(in_fields_by_order);

    Assertive.require(
      this.fields_by_order.size() >= this.fields_by_name.size());

    this.fields_by_order.selectInstancesOf(FieldValue.class).forEach(
      (Procedure<FieldValue>) f -> {
        Assertive.require(this.fields_by_name.containsKey(f.identifier));
        final FieldValue fr = this.fields_by_name.get(f.identifier);
        Assertive.require(fr.equals(f));
      });

    this.size_bits = this.fields_by_order.injectInto(
      Size.zero(), (s, f) -> s.add(f.getSize()));
  }

  @Override public Size<SizeUnitBitsType> getSize()
  {
    return this.size_bits;
  }

  @Override
  public <A, E extends Exception> A matchType(final TypeMatcherType<A, E> m)
    throws E
  {
    return m.matchRecord(this);
  }

  @Override
  public Optional<ImmutableLexicalPositionType<Path>> getLexicalInformation()
  {
    return this.ident.getLexicalInformation();
  }

  /**
   * The type of record fields.
   */

  public interface FieldType extends ModelElementType
  {
    /**
     * @return The size in bits
     */

    Size<SizeUnitBitsType> getSize();

    /**
     * Accept a matcher.
     *
     * @param m   The matcher
     * @param <A> The type of returned values
     * @param <E> The type of raised exceptions
     *
     * @return The value returned by {@code m}
     *
     * @throws E If {@code m} raises {@code E}
     */

    <A, E extends Exception> A matchField(
      final FieldMatcherType<A, E> m)
      throws E;
  }

  /**
   * The type of field matchers.
   *
   * @param <A> The type of returned values
   * @param <E> The type of raised exceptions
   */

  public interface FieldMatcherType<A, E extends Exception>
  {
    /**
     * Match a value field.
     *
     * @param f The field
     *
     * @return A value of {@code A}
     *
     * @throws E If required
     */

    A matchFieldValue(TRecord.FieldValue f)
      throws E;

    /**
     * Match a padding field.
     *
     * @param f The field
     *
     * @return A value of {@code A}
     *
     * @throws E If required
     */

    A matchFieldPaddingOctets(TRecord.FieldPaddingOctets f)
      throws E;
  }

  /**
   * A record value field.
   */

  public static final class FieldValue implements FieldType
  {
    private final ResolvedFieldIdentifier identifier;
    private final TType                   type;

    /**
     * Construct a field.
     *
     * @param in_identifier The identifier
     * @param in_type       The field type
     */

    public FieldValue(
      final ResolvedFieldIdentifier in_identifier,
      final TType in_type)
    {
      this.identifier = NullCheck.notNull(in_identifier);
      this.type = NullCheck.notNull(in_type);
    }

    @Override public Size<SizeUnitBitsType> getSize()
    {
      return this.type.getSize();
    }

    @Override
    public <A, E extends Exception> A matchField(final FieldMatcherType<A, E> m)
      throws E
    {
      return m.matchFieldValue(this);
    }

    @Override
    public Optional<ImmutableLexicalPositionType<Path>> getLexicalInformation()
    {
      return this.identifier.getLexicalInformation();
    }
  }

  /**
   * A record padding field.
   */

  public static final class FieldPaddingOctets implements FieldType
  {
    private final Size<SizeUnitBitsType>                       size_bits;
    private final Optional<ImmutableLexicalPositionType<Path>> lex;

    /**
     * Construct a field.
     *
     * @param in_size_bits The size in bits
     * @param in_lex       Lexical information
     */

    public FieldPaddingOctets(
      final Size<SizeUnitBitsType> in_size_bits,
      final Optional<ImmutableLexicalPositionType<Path>> in_lex)
    {
      this.size_bits = NullCheck.notNull(in_size_bits);
      this.lex = NullCheck.notNull(in_lex);
    }

    @Override public Size<SizeUnitBitsType> getSize()
    {
      return this.size_bits;
    }

    @Override
    public <A, E extends Exception> A matchField(final FieldMatcherType<A, E> m)
      throws E
    {
      return m.matchFieldPaddingOctets(this);
    }

    @Override
    public Optional<ImmutableLexicalPositionType<Path>> getLexicalInformation()
    {
      return this.lex;
    }
  }
}
