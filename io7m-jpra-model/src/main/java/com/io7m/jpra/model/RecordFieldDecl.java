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

package com.io7m.jpra.model;

import com.io7m.junreachable.UnreachableCodeException;

import java.util.Optional;

/**
 * Functions ove record field declarations.
 */

public final class RecordFieldDecl
{
  private RecordFieldDecl()
  {
    throw new UnreachableCodeException();
  }

  /**
   * @param r The field declaration
   *
   * @return The name defined for the given field declaration, if any
   */

  public static Optional<FieldName> name(final RecordFieldDeclType r)
  {
    return r.matchRecordFieldDeclaration(
      new RecordFieldDeclMatcherType<Optional<FieldName>, RuntimeException>()
      {
        @Override public Optional<FieldName> matchPadding(
          final RecordFieldDeclPaddingOctets r)
        {
          return Optional.empty();
        }

        @Override
        public Optional<FieldName> matchValue(final RecordFieldDeclValue r)
        {
          return Optional.of(r.getName());
        }
      });
  }
}
