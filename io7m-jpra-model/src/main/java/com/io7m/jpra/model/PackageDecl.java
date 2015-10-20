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
 * A package declaration.
 */

public final class PackageDecl
{
  private final PackageNameQualified                                name;
  private final ImmutableList<TypeDeclType>
                                                                    fields_order;
  private final ImmutableMap<TypeName, TypeDeclType>                fields_name;
  private final ImmutableMap<PackageNameUnqualified, PackageImport> imports;

  /**
   * Construct a package declaration.
   *
   * @param in_fields_name  The package fields by name
   * @param in_name         The name of the package
   * @param in_fields_order The package fields in declaration order
   * @param in_imports      The package imports
   */

  public PackageDecl(
    final ImmutableMap<TypeName, TypeDeclType> in_fields_name,
    final PackageNameQualified in_name,
    final ImmutableList<TypeDeclType> in_fields_order,
    final ImmutableMap<PackageNameUnqualified, PackageImport> in_imports)
  {
    this.fields_name = NullCheck.notNull(in_fields_name);
    this.name = NullCheck.notNull(in_name);
    this.fields_order = NullCheck.notNull(in_fields_order);
    this.imports = NullCheck.notNull(in_imports);

    Assertive.require(
      this.fields_name.size() <= this.fields_order.size(),
      "Fields-by-name size %d > Fields-ordered size %d",
      Integer.valueOf(this.fields_name.size()),
      Integer.valueOf(this.fields_order.size()));

    this.fields_order.forEach(
      (Procedure<TypeDeclType>) r -> {
        final TypeName r_name = r.getName();
        Assertive.require(this.fields_name.containsKey(r_name));
      });

    this.imports.forEachKeyValue(
      (p_name, imp) -> Assertive.require(
        imp.getUsing().equals(p_name),
        "Package name %s must match import name %s",
        p_name,
        imp.getUsing()));
  }

  /**
   * @return The package fields by name
   */

  public ImmutableMap<TypeName, TypeDeclType> getFieldsByName()
  {
    return this.fields_name;
  }

  /**
   * @return The package fields in declaration order
   */

  public ImmutableList<TypeDeclType> getFieldsOrdered()
  {
    return this.fields_order;
  }

  /**
   * @return The fully-qualified package name
   */

  public PackageNameQualified getName()
  {
    return this.name;
  }

  /**
   * @return The package imports
   */

  public ImmutableMap<PackageNameUnqualified, PackageImport> getImports()
  {
    return this.imports;
  }
}
