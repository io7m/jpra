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
import com.io7m.jlexing.core.ImmutableLexicalPositionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.names.FieldName;
import net.jcip.annotations.Immutable;

import java.nio.file.Path;
import java.util.Optional;

/**
 * A {@code boolean-set} type.
 */

@Immutable public final class TBooleanSet implements TType
{
  private final Size<SizeUnitOctetsType>                     size;
  private final ImmutableList<FieldName>                     fields;
  private final Optional<ImmutableLexicalPositionType<Path>> lex;
  private final Size<SizeUnitBitsType>                       size_bits;

  /**
   * Construct a boolean set type.
   *
   * @param in_lex    Lexical information, if any
   * @param in_fields The fields
   * @param in_size   The size
   */

  public TBooleanSet(
    final Optional<ImmutableLexicalPositionType<Path>> in_lex,
    final ImmutableList<FieldName> in_fields,
    final Size<SizeUnitOctetsType> in_size)
  {
    this.lex = NullCheck.notNull(in_lex);
    this.fields = NullCheck.notNull(in_fields);
    this.size = NullCheck.notNull(in_size);
    this.size_bits = Size.toBits(this.size);
  }

  /**
   * @return The size in octets of the set
   */

  public Size<SizeUnitOctetsType> getSizeInOctets()
  {
    return this.size;
  }

  /**
   * @return The fields in declaration order
   */

  public ImmutableList<FieldName> getFieldsInDeclarationOrder()
  {
    return this.fields;
  }

  @Override public Size<SizeUnitBitsType> getSizeInBits()
  {
    return this.size_bits;
  }

  @Override public <A, E extends Exception> A matchType(
    final TypeMatcherType<A, E> m)
    throws E
  {
    return m.matchBooleanSet(this);
  }

  @Override
  public Optional<ImmutableLexicalPositionType<Path>> getLexicalInformation()
  {
    return this.lex;
  }
}
