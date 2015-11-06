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

package com.io7m.jpra.model.statements;

import com.io7m.jpra.model.type_declarations.TypeDeclType;

/**
 * A statement matcher.
 *
 * @param <I> The type of identifiers
 * @param <T> The type of type information
 * @param <A> The type of returned values
 * @param <E> The type of raised exceptions
 */

public interface StatementMatcherType<I, T, A, E extends Exception>
{
  /**
   * Match a {@code package-begin} statement.
   *
   * @param s The statement
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchPackageBegin(
    StatementPackageBegin<I, T> s)
    throws E;

  /**
   * Match a {@code package-end} statement.
   *
   * @param s The statement
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchPackageEnd(
    StatementPackageEnd<I, T> s)
    throws E;

  /**
   * Match an {@code import} statement.
   *
   * @param s The statement
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchPackageImport(
    StatementPackageImport<I, T> s)
    throws E;

  /**
   * Match a type declaration statement.
   *
   * @param s The statement
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchTypeDecl(
    TypeDeclType<I, T> s)
    throws E;

  /**
   * Match a {@code :type} command.
   *
   * @param s The statement
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchShowType(
    StatementCommandType<I, T> s)
    throws E;

  /**
   * Match a {@code :size} command.
   *
   * @param s The statement
   *
   * @return A value of {@code A}
   *
   * @throws E If required
   */

  A matchShowSize(
    StatementCommandSize<I, T> s)
    throws E;
}
