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
import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jpra.model.ModelElementType;
import com.io7m.jpra.model.contexts.PackageContextType;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.names.IdentifierType;
import com.io7m.jpra.model.names.TypeName;

import java.math.BigInteger;
import java.nio.file.Path;
import java.util.Optional;

/**
 * A {@code record} type.
 */

public final class TRecord implements TType, TypeUserDefinedType
{
  private final TypeName name;
  private final Size<SizeUnitBitsType> size_bits;
  private final ImmutableMap<FieldName, FieldValue> fields_by_name;
  private final ImmutableList<FieldType> fields_by_order;
  private final PackageContextType package_ctx;
  private final IdentifierType identifier;
  private final Size<SizeUnitOctetsType> size_octets;

  TRecord(
    final PackageContextType in_package,
    final IdentifierType in_identifier,
    final TypeName in_ident,
    final ImmutableMap<FieldName, FieldValue> in_fields_by_name,
    final ImmutableList<FieldType> in_fields_by_order)
  {
    this.package_ctx =
      NullCheck.notNull(in_package, "Package");
    this.identifier =
      NullCheck.notNull(in_identifier, "Identifier");
    this.fields_by_name =
      NullCheck.notNull(in_fields_by_name, "Fields by name");
    this.name =
      NullCheck.notNull(in_ident, "Identifier");
    this.fields_by_order =
      NullCheck.notNull(in_fields_by_order, "Fields in order");

    Preconditions.checkPreconditionV(
      this.fields_by_order.size() >= this.fields_by_name.size(),
      "Ordered field count %d must be >= named field count %d",
      Integer.valueOf(this.fields_by_order.size()),
      Integer.valueOf(this.fields_by_name.size()));

    this.fields_by_order.selectInstancesOf(FieldValue.class).forEach(
      (Procedure<FieldValue>) f -> {
        Preconditions.checkPreconditionV(
          this.fields_by_name.containsKey(f.name),
          "Named fields must contain %s", f.name);
        final FieldValue fr = this.fields_by_name.get(f.name);
        Preconditions.checkPrecondition(
          fr.equals(f), "Field value must match");
      });

    this.size_bits = this.fields_by_order.injectInto(
      Size.zero(), (s, f) -> s.add(f.getSizeInBits()));

    final BigInteger b8 = BigInteger.valueOf(8L);
    final BigInteger br = this.size_bits.getValue().remainder(b8);
    Preconditions.checkPreconditionV(
      br.equals(BigInteger.ZERO),
      "Size %s must be divisible by 8", this.size_bits);

    this.size_octets = new Size<>(this.size_bits.getValue().divide(b8));
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

  public static TRecordBuilderType newBuilder(
    final PackageContextType in_package,
    final IdentifierType in_identifier,
    final TypeName in_ident)
  {
    NullCheck.notNull(in_package, "Package context");
    NullCheck.notNull(in_identifier, "Identifier");
    NullCheck.notNull(in_ident, "Type name");
    return new TRecordBuilder(in_package, in_identifier, in_ident);
  }

  @Override
  public TypeName getName()
  {
    return this.name;
  }

  @Override
  public IdentifierType getIdentifier()
  {
    return this.identifier;
  }

  @Override
  public PackageContextType getPackageContext()
  {
    return this.package_ctx;
  }

  /**
   * @return The size of the record in octets
   */

  public Size<SizeUnitOctetsType> getSizeInOctets()
  {
    return this.size_octets;
  }

  @Override
  public <A, E extends Exception> A matchTypeUserDefined(
    final TypeUserDefinedMatcherType<A, E> m)
    throws E
  {
    return m.matchRecord(this);
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

  @Override
  public Size<SizeUnitBitsType> getSizeInBits()
  {
    return this.size_bits;
  }

  @Override
  public <A, E extends Exception> A matchType(
    final TypeMatcherType<A, E> m)
    throws E
  {
    return m.matchRecord(this);
  }

  @Override
  public Optional<LexicalPosition<Path>> getLexicalInformation()
  {
    return this.name.getLexicalInformation();
  }

  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder("[record ");
    sb.append(this.name);
    sb.append(" (");
    for (final FieldType o : this.fields_by_order) {
      sb.append(o);
    }
    sb.append(")]");
    return sb.toString();
  }

  /**
   * The type of record fields.
   */

  public interface FieldType extends ModelElementType
  {
    /**
     * @return The owning type
     */

    TRecord getOwner();

    /**
     * @return The size in bits
     */

    Size<SizeUnitBitsType> getSizeInBits();

    /**
     * @return The size in octets
     */

    Size<SizeUnitOctetsType> getSizeInOctets();

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
    private final FieldName name;
    private final TType type;
    private final Size<SizeUnitOctetsType> size_octets;
    private @Nullable TRecord owner;

    FieldValue(
      final FieldName in_name,
      final TType in_type)
    {
      this.name = NullCheck.notNull(in_name, "Name");
      this.type = NullCheck.notNull(in_type, "Type");

      final Size<SizeUnitBitsType> bits = this.type.getSizeInBits();
      final BigInteger b8 = BigInteger.valueOf(8L);
      final BigInteger br = bits.getValue().remainder(b8);
      Preconditions.checkPrecondition(
        br,
        br.equals(BigInteger.ZERO),
        s -> String.format("Size %s must be divisible by 8", s));

      this.size_octets = new Size<>(bits.getValue().divide(b8));
    }

    @Override
    public TRecord getOwner()
    {
      return NullCheck.notNull(this.owner, "Owner");
    }

    void setOwner(
      final TRecord in_owner)
    {
      this.owner = NullCheck.notNull(in_owner, "Owner");
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

    @Override
    public Size<SizeUnitBitsType> getSizeInBits()
    {
      return this.type.getSizeInBits();
    }

    @Override
    public <A, E extends Exception> A matchField(
      final FieldMatcherType<A, E> m)
      throws E
    {
      return m.matchFieldValue(this);
    }

    @Override
    public Optional<LexicalPosition<Path>> getLexicalInformation()
    {
      return this.name.getLexicalInformation();
    }

    @Override
    public String toString()
    {
      final StringBuilder sb = new StringBuilder("[field ");
      sb.append(this.name);
      sb.append(" ");
      sb.append(this.type);
      sb.append("]");
      return sb.toString();
    }

    @Override
    public Size<SizeUnitOctetsType> getSizeInOctets()
    {
      return this.size_octets;
    }
  }

  /**
   * A record padding field.
   */

  public static final class FieldPaddingOctets implements FieldType
  {
    private final Size<SizeUnitBitsType> size_bits;
    private final Optional<LexicalPosition<Path>> lex;
    private final Size<SizeUnitOctetsType> size_octets;
    private @Nullable TRecord owner;

    /**
     * Construct a field.
     *
     * @param in_size_octets The size in octets
     * @param in_lex         Lexical information
     */

    FieldPaddingOctets(
      final Size<SizeUnitOctetsType> in_size_octets,
      final Optional<LexicalPosition<Path>> in_lex)
    {
      this.size_octets = NullCheck.notNull(in_size_octets, "Size");
      this.size_bits = Size.toBits(this.size_octets);
      this.lex = NullCheck.notNull(in_lex, "Lexical information");
    }

    /**
     * @return The owning type
     */

    @Override
    public TRecord getOwner()
    {
      return NullCheck.notNull(this.owner, "Owner");
    }

    void setOwner(
      final TRecord in_owner)
    {
      this.owner = NullCheck.notNull(in_owner, "Owner");
    }

    @Override
    public Size<SizeUnitBitsType> getSizeInBits()
    {
      return this.size_bits;
    }

    @Override
    public Size<SizeUnitOctetsType> getSizeInOctets()
    {
      return this.size_octets;
    }

    @Override
    public <A, E extends Exception> A matchField(
      final FieldMatcherType<A, E> m)
      throws E
    {
      return m.matchFieldPaddingOctets(this);
    }

    @Override
    public Optional<LexicalPosition<Path>> getLexicalInformation()
    {
      return this.lex;
    }

    @Override
    public String toString()
    {
      final StringBuilder sb = new StringBuilder("[padding-octets ");
      sb.append(this.size_octets.getValue());
      sb.append("]");
      return sb.toString();
    }
  }
}
