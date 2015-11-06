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

import com.io7m.jpra.model.names.FieldName;
import com.io7m.junreachable.UnreachableCodeException;

import java.util.Optional;

/**
 * Functions over field declaration for {@code packed} types.
 */

public final class PackedFieldDecl
{
  private PackedFieldDecl()
  {
    throw new UnreachableCodeException();
  }

  /**
   * @param r   The field declaration
   * @param <I> The type of identifiers
   * @param <T> The type of type information
   *
   * @return The name defined for the given field declaration, if any
   */

  public static <I, T> Optional<FieldName> name(
    final PackedFieldDeclType<I, T> r)
  {
    return r.matchPackedFieldDeclaration(
      new PackedFieldDeclMatcherType<I, T, Optional<FieldName>,
        RuntimeException>()
      {
        @Override public Optional<FieldName> matchPaddingBits(
          final PackedFieldDeclPaddingBits<I, T> r)
        {
          return Optional.empty();
        }

        @Override public Optional<FieldName> matchValue(
          final PackedFieldDeclValue<I, T> r)
        {
          return Optional.of(r.getName());
        }
      });
  }
}
