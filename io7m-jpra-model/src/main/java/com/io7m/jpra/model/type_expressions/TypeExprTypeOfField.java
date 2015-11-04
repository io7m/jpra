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

package com.io7m.jpra.model.type_expressions;

import com.io7m.jlexing.core.ImmutableLexicalPositionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.names.FieldPath;
import com.io7m.jpra.model.names.FieldReference;

import java.nio.file.Path;
import java.util.Optional;

public final class TypeExprTypeOfField<S> implements TypeExprType<S>
{
  private final S              data;
  private final FieldReference field_reference;

  public TypeExprTypeOfField(
    final S in_data,
    final FieldReference in_field_reference)
  {
    this.field_reference = NullCheck.notNull(in_field_reference);
    this.data = NullCheck.notNull(in_data);
  }

  public FieldReference getFieldReference()
  {
    return this.field_reference;
  }

  @Override public S getData()
  {
    return this.data;
  }

  @Override public <A, E extends Exception> A matchType(
    final TypeExprMatcherType<S, A, E> m)
    throws E
  {
    return m.matchTypeOfField(this);
  }

  @Override
  public Optional<ImmutableLexicalPositionType<Path>> getLexicalInformation()
  {
    final FieldPath path = this.field_reference.getFieldPath();
    return path.getElements().get(0).getLexicalInformation();
  }
}
