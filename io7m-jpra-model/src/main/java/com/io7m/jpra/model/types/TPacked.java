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
import com.io7m.jpra.model.contexts.PackageContextType;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.names.TypeName;
import org.valid4j.Assertive;

import java.nio.file.Path;
import java.util.Optional;

/**
 * A {@code packed} type.
 */

public final class TPacked implements TType, TypeUserDefinedType
{
  private final TypeName                            name;
  private final Size<SizeUnitBitsType>              size_bits;
  private final ImmutableMap<FieldName, FieldValue> fields_by_name;
  private final ImmutableList<FieldType>            fields_by_order;
  private final PackageContextType                  package_ctx;

  /**
   * Construct a record type.
   *
   * @param in_package         The package context
   * @param in_name            The type name
   * @param in_fields_by_name  The fields by name
   * @param in_fields_by_order The fields in declaration order
   */

  public TPacked(
    final PackageContextType in_package,
    final TypeName in_name,
    final ImmutableMap<FieldName, FieldValue> in_fields_by_name,
    final ImmutableList<FieldType> in_fields_by_order)
  {
    this.package_ctx = NullCheck.notNull(in_package);
    this.name = NullCheck.notNull(in_name);
    this.fields_by_name = NullCheck.notNull(in_fields_by_name);
    this.fields_by_order = NullCheck.notNull(in_fields_by_order);

    Assertive.require(
      this.fields_by_order.size() >= this.fields_by_name.size());

    this.fields_by_order.selectInstancesOf(FieldValue.class).forEach(
      (Procedure<FieldValue>) f -> {
        Assertive.require(this.fields_by_name.containsKey(f));
        final FieldValue fr = this.fields_by_name.get(f);
        Assertive.require(fr.equals(f));
      });

    this.size_bits = this.fields_by_order.injectInto(
      Size.zero(), (s, f) -> s.add(f.getSize()));
  }

  /**
   * @return The subset of fields that have names
   */

  public ImmutableMap<FieldName, FieldValue> getFieldsByName()
  {
    return this.fields_by_name;
  }

  /**
   * @return All fields in declaration order
   */

  public ImmutableList<FieldType> getFieldsByOrder()
  {
    return this.fields_by_order;
  }

  @Override public Size<SizeUnitBitsType> getSize()
  {
    return this.size_bits;
  }

  @Override
  public <A, E extends Exception> A matchType(final TypeMatcherType<A, E> m)
    throws E
  {
    return m.matchPacked(this);
  }

  @Override
  public Optional<ImmutableLexicalPositionType<Path>> getLexicalInformation()
  {
    return this.name.getLexicalInformation();
  }

  @Override public PackageContextType getPackageContext()
  {
    return this.package_ctx;
  }

  @Override public <A, E extends Exception> A matchTypeUserDefined(
    final TypeUserDefinedMatcherType<A, E> m)
    throws E
  {
    return m.matchPacked(this);
  }

  /**
   * The type of packed fields.
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

    A matchFieldValue(TPacked.FieldValue f)
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

    A matchFieldPaddingBits(FieldPaddingBits f)
      throws E;
  }

  /**
   * A packed value field.
   */

  public static final class FieldValue implements FieldType
  {
    private final TPacked   owner;
    private final FieldName name;
    private final TType     type;

    /**
     * Construct a field.
     *
     * @param in_owner      The owning type
     * @param in_identifier The name
     * @param in_type       The field type
     */

    public FieldValue(
      final TPacked in_owner,
      final FieldName in_identifier,
      final TType in_type)
    {
      this.owner = NullCheck.notNull(in_owner);
      this.name = NullCheck.notNull(in_identifier);
      this.type = NullCheck.notNull(in_type);
    }

    /**
     * @return The owning type
     */

    public TPacked getOwner()
    {
      return this.owner;
    }

    /**
     * @return The field name
     */

    public FieldName getName()
    {
      return this.name;
    }

    /**
     * @return The field type
     */

    public TType getType()
    {
      return this.type;
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
      return this.name.getLexicalInformation();
    }
  }

  /**
   * A packed padding field.
   */

  public static final class FieldPaddingBits implements FieldType
  {
    private final Size<SizeUnitBitsType>                       size_bits;
    private final Optional<ImmutableLexicalPositionType<Path>> lex;
    private final TPacked                                      owner;

    /**
     * Construct a field.
     *
     * @param in_owner     The owning type
     * @param in_size_bits The size in bits
     * @param in_lex       Lexical information
     */

    public FieldPaddingBits(
      final TPacked in_owner,
      final Size<SizeUnitBitsType> in_size_bits,
      final Optional<ImmutableLexicalPositionType<Path>> in_lex)
    {
      this.owner = NullCheck.notNull(in_owner);
      this.size_bits = NullCheck.notNull(in_size_bits);
      this.lex = NullCheck.notNull(in_lex);
    }

    /**
     * @return The owning type
     */

    public TPacked getOwner()
    {
      return this.owner;
    }

    @Override public Size<SizeUnitBitsType> getSize()
    {
      return this.size_bits;
    }

    @Override
    public <A, E extends Exception> A matchField(final FieldMatcherType<A, E> m)
      throws E
    {
      return m.matchFieldPaddingBits(this);
    }

    @Override
    public Optional<ImmutableLexicalPositionType<Path>> getLexicalInformation()
    {
      return this.lex;
    }
  }
}
