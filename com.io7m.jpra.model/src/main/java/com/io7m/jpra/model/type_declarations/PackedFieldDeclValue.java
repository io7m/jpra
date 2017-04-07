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

package com.io7m.jpra.model.type_declarations;

import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.type_expressions.TypeExprType;
import net.jcip.annotations.Immutable;

import java.nio.file.Path;
import java.util.Optional;

/**
 * A {@code field} declaration in a {@code packed} type.
 *
 * @param <I> The type of identifiers
 * @param <T> The type of type information
 */

@Immutable
public final class PackedFieldDeclValue<I, T>
  implements PackedFieldDeclType<I, T>
{
  private final I identifier;
  private final FieldName name;
  private final TypeExprType<I, T> type;

  /**
   * Construct a {@code field} declaration.
   *
   * @param in_identifier The identifier
   * @param in_name       The name of the field
   * @param in_type       The type of the field
   */

  public PackedFieldDeclValue(
    final I in_identifier,
    final FieldName in_name,
    final TypeExprType<I, T> in_type)
  {
    this.identifier = NullCheck.notNull(in_identifier, "Identifier");
    this.name = NullCheck.notNull(in_name, "Name");
    this.type = NullCheck.notNull(in_type, "Type");
  }

  /**
   * @return The identifier
   */

  public I getIdentifier()
  {
    return this.identifier;
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

  public TypeExprType<I, T> getType()
  {
    return this.type;
  }

  @Override
  public <A, E extends Exception> A matchPackedFieldDeclaration(
    final PackedFieldDeclMatcherType<I, T, A, E> m)
    throws E
  {
    return m.matchValue(this);
  }

  @Override
  public Optional<LexicalPosition<Path>> getLexicalInformation()
  {
    return this.name.getLexicalInformation();
  }
}
