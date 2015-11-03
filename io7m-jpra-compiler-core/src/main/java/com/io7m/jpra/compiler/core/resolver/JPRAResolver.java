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

package com.io7m.jpra.compiler.core.resolver;

import com.gs.collections.impl.factory.Maps;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.compiler.core.JPRACompilerException;
import com.io7m.jpra.compiler.core.parser.JPRAParserType;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.PackageImport;
import com.io7m.jpra.model.names.PackageNameQualified;
import com.io7m.jpra.model.names.PackageNameUnqualified;
import com.io7m.jpra.model.SizeUnitOctetsType;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.jpra.model.type_declarations.TypeDeclRecord;
import com.io7m.jpra.model.type_expressions.TypeExprType;
import org.valid4j.Assertive;

import java.util.Map;
import java.util.Optional;

public final class JPRAResolver implements JPRAResolverType
{
  private final Map<PackageNameQualified, PackageContext> packages;
  private final JPRAResolverEventListenerType             listener;
  private       Optional<PackageContext>                  current;

  private JPRAResolver(final JPRAResolverEventListenerType e)
  {
    this.packages = new UnifiedMap<>();
    this.current = Optional.empty();
    this.listener = NullCheck.notNull(e);
  }

  public static JPRAResolverType newResolver(
    final JPRAResolverEventListenerType e)
  {
    return new JPRAResolver(e);
  }

  @Override public void onREPLType(
    final JPRAParserType parser,
    final TypeExprType t)
  {

  }

  @Override public void onREPLSize(
    final JPRAParserType parser,
    final SizeExprType<?> t)
  {

  }

  @Override public void onPackageBegin(
    final JPRAParserType parser,
    final PackageNameQualified name)
    throws JPRACompilerException
  {
    if (this.current.isPresent()) {
      throw JPRACompilerResolverException.nestedPackage(name);
    }

    if (this.packages.containsKey(name)) {
      final PackageContext context = this.packages.get(name);
      throw JPRACompilerResolverException.duplicatePackage(name, context.name);
    }

    this.current = Optional.of(new PackageContext(name));
  }

  @Override public void onImport(
    final JPRAParserType parser,
    final PackageNameQualified p_name,
    final PackageNameUnqualified up_name)
    throws JPRACompilerException
  {
    if (!this.current.isPresent()) {
      throw JPRACompilerResolverException.noCurrentPackage(
        p_name.getLexicalInformation());
    }

    final PackageContext p = this.current.get();
    if (p.imports.containsKey(up_name)) {
      final PackageImport i = p.imports.get(up_name);
      throw JPRACompilerResolverException.packageImportConflict(up_name, i);
    }

    if (!this.packages.containsKey(p_name)) {
      throw JPRACompilerResolverException.nonexistentPackage(p_name);
    }

    p.imports.put(up_name, new PackageImport(p.name, p_name, up_name));
  }

  @Override public void onPackageEnd(final JPRAParserType parser)
    throws JPRACompilerException
  {
    if (!this.current.isPresent()) {
      throw JPRACompilerResolverException.noCurrentPackage(
        parser.getParsingPosition());
    }

    final PackageContext p = this.current.get();
    this.packages.put(p.name, p);
    this.current = Optional.empty();
  }

  @Override public void onRecordBegin(
    final JPRAParserType parser,
    final TypeName t)
    throws JPRACompilerException
  {
    if (!this.current.isPresent()) {
      throw JPRACompilerResolverException.noCurrentPackage(
        t.getLexicalInformation());
    }

    final PackageContext p = this.current.get();
    if (p.types.containsKey(t)) {
      final TypeDeclRecord o = p.types.get(t);
      throw JPRACompilerResolverException.duplicateType(t, o.getName());
    }

    p.current_type = Optional.of(new TypeContext(t));
  }

  @Override public void onRecordFieldPaddingOctets(
    final JPRAParserType parser,
    final SizeExprType<SizeUnitOctetsType> size)
    throws JPRACompilerException
  {

  }

  @Override public void onRecordFieldValue(
    final JPRAParserType parser,
    final FieldName name,
    final TypeExprType type)
  {

  }

  @Override public void onRecordEnd(final JPRAParserType parser)
    throws JPRACompilerException
  {
    Assertive.require(this.current.isPresent());
    final PackageContext p = this.current.get();
    Assertive.require(p.current_type.isPresent());
    final TypeContext t = p.current_type.get();
    Assertive.require(!p.types.containsKey(t.name));
  }

  private static final class PackageContext
  {
    private final PackageNameQualified                       name;
    private final Map<PackageNameUnqualified, PackageImport> imports;
    private final Map<TypeName, TypeDeclRecord>              types;
    private       Optional<TypeContext>                      current_type;

    PackageContext(final PackageNameQualified in_name)
    {
      this.name = NullCheck.notNull(in_name);
      this.imports = Maps.mutable.empty();
      this.types = Maps.mutable.empty();
      this.current_type = Optional.empty();
    }
  }

  private class TypeContext
  {
    private final TypeName name;

    TypeContext(final TypeName in_name)
    {
      this.name = NullCheck.notNull(in_name);
    }
  }
}
