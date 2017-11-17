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

import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jnull.Nullable;
import com.io7m.jpra.model.ModelElementType;
import com.io7m.jpra.model.contexts.PackageContextType;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.names.IdentifierType;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.jranges.RangeInclusiveB;
import io.vavr.collection.List;
import io.vavr.collection.Map;

import java.math.BigInteger;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@code packed} type.
 */

public final class TPacked implements TType, TypeUserDefinedType
{
  private final TypeName name;
  private final Size<SizeUnitBitsType> size_bits;
  private final Map<FieldName, FieldValue> fields_by_name;
  private final List<FieldType> fields_by_order;
  private final PackageContextType package_ctx;
  private final IdentifierType identifier;
  private final Size<SizeUnitOctetsType> size_octets;

  TPacked(
    final PackageContextType in_package,
    final IdentifierType in_identifier,
    final TypeName in_name,
    final Map<FieldName, FieldValue> in_fields_by_name,
    final List<FieldType> in_fields_by_order)
  {
    this.package_ctx =
      Objects.requireNonNull(in_package, "Package");
    this.identifier =
      Objects.requireNonNull(in_identifier, "Identifier");
    this.fields_by_name =
      Objects.requireNonNull(in_fields_by_name, "Fields by name");
    this.name =
      Objects.requireNonNull(in_name, "Type name");
    this.fields_by_order =
      Objects.requireNonNull(in_fields_by_order, "Fields in order");

    Preconditions.checkPreconditionV(
      this.fields_by_order.size() >= this.fields_by_name.size(),
      "Ordered field count %d must be >= named field count %d",
      Integer.valueOf(this.fields_by_order.size()),
      Integer.valueOf(this.fields_by_name.size()));

    this.fields_by_order
      .filter(x -> x instanceof FieldValue)
      .map(x -> (FieldValue) x)
      .forEach(f -> {
        final FieldName f_name = f.getName();
        Preconditions.checkPreconditionV(
          this.fields_by_name.containsKey(f.name),
          "Named fields must contain %s", f.name);
        final FieldValue fr = this.fields_by_name.get(f_name).get();
        Preconditions.checkPrecondition(
          Objects.equals(fr, f), "Field value must match");
      });

    this.size_bits = this.fields_by_order
      .map(TPacked.FieldType::getSize)
      .fold(Size.zero(), Size::add);

    final BigInteger sv = this.size_bits.getValue();
    final BigInteger b8 = BigInteger.valueOf(8L);
    Preconditions.checkPreconditionV(
      Objects.equals(sv.remainder(b8), BigInteger.ZERO),
      "Size %s must be divisible by 8", sv);
    this.size_octets = new Size<>(sv.divide(b8));
  }

  /**
   * Construct a new mutable record builder.
   *
   * @param in_package    The package context
   * @param in_identifier The type's identifier
   * @param in_type_name  The type's name
   *
   * @return A new builder
   */

  public static TPackedBuilderType newBuilder(
    final PackageContextType in_package,
    final IdentifierType in_identifier,
    final TypeName in_type_name)
  {
    Objects.requireNonNull(in_package, "Package");
    Objects.requireNonNull(in_identifier, "Identifier");
    Objects.requireNonNull(in_type_name, "Type name");
    return new TPackedBuilder(in_package, in_identifier, in_type_name);
  }

  /**
   * @return The subset of fields that have names
   */

  public Map<FieldName, FieldValue> getFieldsByName()
  {
    return this.fields_by_name;
  }

  /**
   * @return All fields in declaration order
   */

  public List<FieldType> getFieldsInDeclarationOrder()
  {
    return this.fields_by_order;
  }

  @Override
  public Size<SizeUnitBitsType> getSizeInBits()
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
  public Optional<LexicalPosition<Path>> lexical()
  {
    return this.name.lexical();
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

  @Override
  public <A, E extends Exception> A matchTypeUserDefined(
    final TypeUserDefinedMatcherType<A, E> m)
    throws E
  {
    return m.matchPacked(this);
  }

  @Override
  public String toString()
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
      FieldMatcherType<A, E> m)
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
    private final FieldName name;
    private final TIntegerType type;
    private @Nullable TPacked owner;
    private @Nullable RangeInclusiveB range;

    FieldValue(
      final FieldName in_identifier,
      final TIntegerType in_type)
    {
      this.name = Objects.requireNonNull(in_identifier, "Identifier");
      this.type = Objects.requireNonNull(in_type, "Type");
    }

    @Override
    public RangeInclusiveB getBitRange()
    {
      return Objects.requireNonNull(this.range, "Range");
    }

    @Override
    public TPacked getOwner()
    {
      return Objects.requireNonNull(this.owner, "Owner");
    }

    void setOwner(final TPacked in_owner)
    {
      this.owner = Objects.requireNonNull(in_owner, "Owner");
    }

    void setRange(final RangeInclusiveB in_range)
    {
      this.range = Objects.requireNonNull(in_range, "Range");
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

    @Override
    public Size<SizeUnitBitsType> getSize()
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
    public Optional<LexicalPosition<Path>> lexical()
    {
      return this.name.lexical();
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
  }

  /**
   * A packed padding field.
   */

  public static final class FieldPaddingBits implements FieldType
  {
    private final Size<SizeUnitBitsType> size_bits;
    private final Optional<LexicalPosition<Path>> lex;
    private @Nullable TPacked owner;
    private @Nullable RangeInclusiveB range;

    FieldPaddingBits(
      final Size<SizeUnitBitsType> in_size_bits,
      final Optional<LexicalPosition<Path>> in_lex)
    {
      this.size_bits =
        Objects.requireNonNull(in_size_bits, "Size bits");
      this.lex =
        Objects.requireNonNull(in_lex, "Lexical information");
    }

    @Override
    public TPacked getOwner()
    {
      return Objects.requireNonNull(this.owner, "Owner");
    }

    void setOwner(final TPacked in_owner)
    {
      this.owner = Objects.requireNonNull(in_owner, "Owner");
    }

    @Override
    public RangeInclusiveB getBitRange()
    {
      return Objects.requireNonNull(this.range, "Range");
    }

    void setRange(final RangeInclusiveB in_range)
    {
      this.range = Objects.requireNonNull(in_range, "Range");
    }

    @Override
    public Size<SizeUnitBitsType> getSize()
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
    public Optional<LexicalPosition<Path>> lexical()
    {
      return this.lex;
    }

    @Override
    public String toString()
    {
      final StringBuilder sb = new StringBuilder("[padding-bits ");
      sb.append(this.size_bits.getValue());
      sb.append("]");
      return sb.toString();
    }
  }
}
