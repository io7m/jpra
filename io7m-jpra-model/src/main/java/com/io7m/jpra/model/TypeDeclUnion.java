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

import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.map.ImmutableMap;
import com.io7m.jnull.NullCheck;
import org.valid4j.Assertive;

/**
 * A {@code union} declaration.
 */

public final class TypeDeclUnion implements TypeDeclType
{
  private final TypeName                               name;
  private final ImmutableList<UnionCase>               cases_order;
  private final ImmutableMap<UnionCaseName, UnionCase> cases_name;

  /**
   * Construct a declaration.
   *
   * @param in_cases_name  The cases by name
   * @param in_name        The type name
   * @param in_cases_order The cases in declaration order
   */

  public TypeDeclUnion(
    final ImmutableMap<UnionCaseName, UnionCase> in_cases_name,
    final TypeName in_name,
    final ImmutableList<UnionCase> in_cases_order)
  {
    this.cases_name = NullCheck.notNull(in_cases_name);
    this.name = NullCheck.notNull(in_name);
    this.cases_order = NullCheck.notNull(in_cases_order);

    Assertive.require(
      this.cases_name.size() == this.cases_order.size(),
      "Cases-by-name size %d != Cases-ordered size %d",
      Integer.valueOf(this.cases_name.size()),
      Integer.valueOf(this.cases_order.size()));

    this.cases_order.forEach(
      (Procedure<UnionCase>) each -> Assertive.require(
        this.cases_name.containsKey(each.getName())));
  }

  @Override public TypeName getName()
  {
    return this.name;
  }

  @Override public <A, E extends Exception> A matchTypeDeclaration(
    final TypeDeclMatcherType<A, E> m)
    throws E
  {
    return m.matchUnion(this);
  }
}
