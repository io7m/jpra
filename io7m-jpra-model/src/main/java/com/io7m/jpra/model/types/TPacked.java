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
import com.io7m.jnull.Nullable;
import com.io7m.jpra.model.ModelElementType;
import com.io7m.jpra.model.contexts.PackageContextType;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.names.IdentifierType;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.jranges.RangeInclusiveB;
import org.valid4j.Assertive;

import java.math.BigInteger;
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
  private final IdentifierType                      identifier;
  private final Size<SizeUnitOctetsType>            size_octets;

  TPacked(
    final PackageContextType in_package,
    final IdentifierType in_identifier,
    final TypeName in_name,
    final ImmutableMap<FieldName, FieldValue> in_fields_by_name,
    final ImmutableList<FieldType> in_fields_by_order)
  {
    this.package_ctx = NullCheck.notNull(in_package);
    this.identifier = NullCheck.notNull(in_identifier);
    this.name = NullCheck.notNull(in_name);
    this.fields_by_name = NullCheck.notNull(in_fields_by_name);
    this.fields_by_order = NullCheck.notNull(in_fields_by_order);

    Assertive.require(
      this.fields_by_order.size() >= this.fields_by_name.size());

    this.fields_by_order.selectInstancesOf(FieldValue.class).forEach(
      (Procedure<FieldValue>) f -> {
        final FieldName f_name = f.getName();
        Assertive.require(
          this.fields_by_name.containsKey(f_name),
          "Fields must contain %s (%s)",
          f,
          this.fields_by_name);
        final FieldValue fr = this.fields_by_name.get(f_name);
        Assertive.require(fr.equals(f));
      });

    this.size_bits = this.fields_by_order.injectInto(
      Size.zero(), (s, f) -> s.add(f.getSize()));

    final BigInteger sv = this.size_bits.getValue();
    final BigInteger b8 = BigInteger.valueOf(8L);
    Assertive.ensure(
      sv.remainder(b8).equals(BigInteger.ZERO),
      "Size %s must be divisible by 8",
      sv);
    this.size_octets = new Size<>(sv.divide(b8));
  }

  /**
   * Construct a new mutable record builder.
   *
   * @param in_package    The package context
   * @param in_identifier The type's identifier
   * @param in_ident      The type's name
   *
   * @return A new builder
   */

  public static TPackedBuilderType newBuilder(
    final PackageContextType in_package,
    final IdentifierType in_identifier,
    final TypeName in_ident)
  {
    NullCheck.notNull(in_package);
    NullCheck.notNull(in_identifier);
    NullCheck.notNull(in_ident);
    return new TPackedBuilder(in_package, in_identifier, in_ident);
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

  public ImmutableList<FieldType> getFieldsInDeclarationOrder()
  {
    return this.fields_by_order;
  }

  @Override public Size<SizeUnitBitsType> getSizeInBits()
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

  @Override public TypeName getName()
  {
    return this.name;
  }

  @Override public IdentifierType getIdentifier()
  {
    return this.identifier;
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

  @Override public String toString()
  {
    final StringBuilder sb = new StringBuilder("[packed ");
    sb.append(this.name);
    sb.append(" (");
    for (final FieldType o : this.fields_by_order) {
      sb.append(o);
    }
    sb.append(")]");
    return sb.toString();
  }

  /**
   * @return The size in octets of the packed type
   */

  public Size<SizeUnitOctetsType> getSizeInOctets()
  {
    return this.size_octets;
  }

  /**
   * The type of packed fields.
   */

  public interface FieldType extends ModelElementType
  {
    /**
     * @return The inclusive range of bits that this field spans
     */

    RangeInclusiveB getBitRange();

    /**
     * @return The owning type
     */

    TPacked getOwner();

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
    private final     FieldName       name;
    private final     TIntegerType    type;
    private @Nullable TPacked         owner;
    private @Nullable RangeInclusiveB range;

    FieldValue(
      final FieldName in_identifier,
      final TIntegerType in_type)
    {
      this.name = NullCheck.notNull(in_identifier);
      this.type = NullCheck.notNull(in_type);
    }

    @Override public RangeInclusiveB getBitRange()
    {
      return NullCheck.notNull(this.range);
    }

    @Override public TPacked getOwner()
    {
      return NullCheck.notNull(this.owner);
    }

    void setOwner(final TPacked in_owner)
    {
      this.owner = NullCheck.notNull(in_owner);
    }

    void setRange(final RangeInclusiveB in_range)
    {
      this.range = NullCheck.notNull(in_range);
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

    public TIntegerType getType()
    {
      return this.type;
    }

    @Override public Size<SizeUnitBitsType> getSize()
    {
      return this.type.getSizeInBits();
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

    @Override public String toString()
    {
      final StringBuilder sb = new StringBuilder("[field ");
      sb.append(this.name);
      sb.append(" ");
      sb.append(this.type);
      sb.append("]");
      return sb.toString();
    }
  }

  /**
   * A packed padding field.
   */

  public static final class FieldPaddingBits implements FieldType
  {
    private final     Size<SizeUnitBitsType>                       size_bits;
    private final     Optional<ImmutableLexicalPositionType<Path>> lex;
    private @Nullable TPacked                                      owner;
    private @Nullable RangeInclusiveB                              range;

    FieldPaddingBits(
      final Size<SizeUnitBitsType> in_size_bits,
      final Optional<ImmutableLexicalPositionType<Path>> in_lex)
    {
      this.size_bits = NullCheck.notNull(in_size_bits);
      this.lex = NullCheck.notNull(in_lex);
    }

    @Override public TPacked getOwner()
    {
      return NullCheck.notNull(this.owner);
    }

    void setOwner(final TPacked in_owner)
    {
      this.owner = NullCheck.notNull(in_owner);
    }

    @Override public RangeInclusiveB getBitRange()
    {
      return NullCheck.notNull(this.range);
    }

    void setRange(final RangeInclusiveB in_range)
    {
      this.range = NullCheck.notNull(in_range);
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

    @Override public String toString()
    {
      final StringBuilder sb = new StringBuilder("[padding-bits ");
      sb.append(this.size_bits.getValue());
      sb.append("]");
      return sb.toString();
    }
  }
}
