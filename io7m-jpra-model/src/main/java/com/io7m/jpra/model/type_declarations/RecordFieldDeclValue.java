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

import com.io7m.jlexing.core.ImmutableLexicalPositionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.type_expressions.TypeExprType;
import net.jcip.annotations.Immutable;

import java.nio.file.Path;
import java.util.Optional;

/**
 * A {@code record} {@code field} declaration.
 */

@Immutable public final class RecordFieldDeclValue<S>
  implements RecordFieldDeclType<S>
{
  private final FieldName       name;
  private final TypeExprType<S> type;
  private final S               data;

  /**
   * Construct a field declaration.
   *
   * @param in_data Supplemental data
   * @param in_name The field name
   * @param in_type The field type
   */

  public RecordFieldDeclValue(
    final S in_data,
    final FieldName in_name,
    final TypeExprType<S> in_type)
  {
    this.data = NullCheck.notNull(in_data);
    this.name = NullCheck.notNull(in_name);
    this.type = NullCheck.notNull(in_type);
  }

  public S getData()
  {
    return this.data;
  }

  /**
   * @return The field type
   */

  public TypeExprType<S> getType()
  {
    return this.type;
  }

  @Override public <A, E extends Exception> A matchRecordFieldDeclaration(
    final RecordFieldDeclMatcherType<S, A, E> m)
    throws E
  {
    return m.matchValue(this);
  }

  /**
   * @return The field name
   */

  public FieldName getName()
  {
    return this.name;
  }

  @Override
  public Optional<ImmutableLexicalPositionType<Path>> getLexicalInformation()
  {
    return this.name.getLexicalInformation();
  }
}
