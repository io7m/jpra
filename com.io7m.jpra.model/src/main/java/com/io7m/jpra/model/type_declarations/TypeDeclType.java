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

import com.io7m.jpra.model.ModelElementType;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.jpra.model.statements.StatementType;

/**
 * The type of type declarations.
 *
 * @param <I> The type of identifiers
 * @param <T> The type of type information
 */

public interface TypeDeclType<I, T>
  extends ModelElementType, StatementType<I, T>
{
  /**
   * @return The type
   */

  T getType();

  /**
   * @return The identifier
   */

  I getIdentifier();

  /**
   * @return The type name
   */

  TypeName getName();

  /**
   * Accept a type declaration matcher.
   *
   * @param m   The matcher
   * @param <A> The type of returned values
   * @param <E> The type of raised exceptions
   *
   * @return The value returned by {@code m}
   *
   * @throws E If {@code m} raises {@code E}
   */

  <A, E extends Exception> A matchTypeDeclaration(
    TypeDeclMatcherType<I, T, A, E> m)
    throws E;
}
