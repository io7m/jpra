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

import com.gs.collections.api.list.ImmutableList;
import com.io7m.jlexing.core.ImmutableLexicalPositionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.names.PackageNameUnqualified;
import com.io7m.jpra.model.names.TypeName;
import org.valid4j.Assertive;

import java.nio.file.Path;
import java.util.Optional;

/**
 * A package-qualified reference to a type and field path.
 *
 * <p>Example: {@code p:T.f}</p>
 *
 * <p>Example: {@code p:T.f.g}</p>
 */

public final class TypeExprNamePTF implements TypeExprNameType
{
  private final PackageNameUnqualified   pack;
  private final TypeName                 type;
  private final ImmutableList<FieldName> field_path;

  /**
   * Construct a reference.
   *
   * @param in_pack The package name
   * @param in_type The type name
   * @param in_path The field path
   */

  public TypeExprNamePTF(
    final PackageNameUnqualified in_pack,
    final TypeName in_type,
    final ImmutableList<FieldName> in_path)
  {
    this.field_path = NullCheck.notNull(in_path);
    this.type = NullCheck.notNull(in_type);
    this.pack = NullCheck.notNull(in_pack);

    Assertive.require(!this.field_path.isEmpty(), "Field path cannot be empty");
  }

  /**
   * @return The field path
   */

  public ImmutableList<FieldName> getFieldPath()
  {
    return this.field_path;
  }

  /**
   * @return The package name
   */

  public PackageNameUnqualified getPackage()
  {
    return this.pack;
  }

  /**
   * @return The type name
   */

  public TypeName getType()
  {
    return this.type;
  }

  @Override public <A, E extends Exception> A matchTypeExpression(
    final TypeExprMatcherType<A, E> m)
    throws E
  {
    return m.matchReference(this);
  }

  @Override
  public Optional<ImmutableLexicalPositionType<Path>> getLexicalInformation()
  {
    return this.pack.getLexicalInformation();
  }

  @Override public <A, E extends Exception> A matchTypeNameExpression(
    final TypeExprNameMatcherType<A, E> m)
    throws E
  {
    return m.matchNamePTF(this);
  }
}
