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

import com.gs.collections.api.bimap.MutableBiMap;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.impl.factory.BiMaps;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.factory.Maps;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.Unresolved;
import com.io7m.jpra.model.ResolvedType;
import com.io7m.jpra.model.contexts.GlobalContextType;
import com.io7m.jpra.model.contexts.PackageContextType;
import com.io7m.jpra.model.loading.JPRAModelLoadingException;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.names.IdentifierType;
import com.io7m.jpra.model.names.PackageNameQualified;
import com.io7m.jpra.model.names.PackageNameUnqualified;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.jpra.model.size_expressions.SizeExprConstant;
import com.io7m.jpra.model.size_expressions.SizeExprInBits;
import com.io7m.jpra.model.size_expressions.SizeExprInOctets;
import com.io7m.jpra.model.size_expressions.SizeExprMatcherType;
import com.io7m.jpra.model.size_expressions.SizeExprType;
import com.io7m.jpra.model.statements.StatementPackageBegin;
import com.io7m.jpra.model.statements.StatementPackageEnd;
import com.io7m.jpra.model.statements.StatementPackageImport;
import com.io7m.jpra.model.type_declarations.RecordFieldDeclMatcherType;
import com.io7m.jpra.model.type_declarations.RecordFieldDeclPaddingOctets;
import com.io7m.jpra.model.type_declarations.RecordFieldDeclType;
import com.io7m.jpra.model.type_declarations.RecordFieldDeclValue;
import com.io7m.jpra.model.type_declarations.TypeDeclMatcherType;
import com.io7m.jpra.model.type_declarations.TypeDeclPacked;
import com.io7m.jpra.model.type_declarations.TypeDeclRecord;
import com.io7m.jpra.model.type_declarations.TypeDeclType;
import com.io7m.jpra.model.type_expressions.TypeExprType;
import com.io7m.junreachable.UnimplementedCodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valid4j.Assertive;

import java.util.Map;
import java.util.Optional;

/**
 * The default implementation of the {@link JPRAResolverType} interface.
 */

public final class JPRAResolver implements JPRAResolverType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(JPRAResolver.class);
  }

  private final GlobalContextType                                    context;
  private final MutableBiMap<PackageNameUnqualified, PackageNameQualified>
                                                                     import_names;
  private final MutableMap<PackageNameQualified, PackageContextType> imports;
  private final MutableMap<TypeName, TypeDeclType<ResolvedType>>
                                                                     current_types;
  private final MutableMap<FieldName, RecordFieldDeclValue<ResolvedType>>
                                                                     current_record_fields;
  private       Optional<PackageNameQualified>
                                                                     current_package;

  private JPRAResolver(
    final GlobalContextType c)
  {
    this.context = NullCheck.notNull(c);
    this.current_package = Optional.empty();
    this.current_record_fields = Maps.mutable.empty();
    this.current_types = Maps.mutable.empty();
    this.imports = Maps.mutable.empty();
    this.import_names = BiMaps.mutable.empty();
  }

  /**
   * @param c A global context
   *
   * @return A new resolver
   */

  public static JPRAResolverType newResolver(
    final GlobalContextType c)
  {
    return new JPRAResolver(c);
  }

  @Override public Optional<PackageNameQualified> resolveGetCurrentPackage()
  {
    return this.current_package;
  }

  @Override
  public Map<TypeName, TypeDeclType<ResolvedType>> resolveGetCurrentTypes()
  {
    return this.current_types.asUnmodifiable();
  }

  @Override public void resolvePackageBegin(
    final StatementPackageBegin<Unresolved> s)
    throws JPRACompilerResolverException
  {
    if (this.current_package.isPresent()) {
      throw JPRACompilerResolverException.nestedPackage(s.getPackageName());
    }

    final Map<PackageNameQualified, PackageContextType> existing =
      this.context.getPackages();

    final PackageNameQualified name = s.getPackageName();
    if (existing.containsKey(name)) {
      final PackageContextType p = existing.get(name);
      throw JPRACompilerResolverException.duplicatePackage(name, p.getName());
    }

    this.current_package = Optional.of(s.getPackageName());
  }

  @Override public void resolvePackageImport(
    final StatementPackageImport<Unresolved> s)
    throws JPRACompilerResolverException
  {
    if (!this.current_package.isPresent()) {
      throw JPRACompilerResolverException.noCurrentPackage(
        s.getLexicalInformation());
    }

    final PackageNameUnqualified s_new = s.getUsing();
    final PackageNameQualified q_name = s.getPackageName();
    if (this.import_names.containsKey(s_new)) {
      final PackageNameQualified q_existing = this.import_names.get(s_new);
      final MutableBiMap<PackageNameQualified, PackageNameUnqualified> ini =
        this.import_names.inverse();

      Assertive.require(ini.containsKey(q_existing));
      final PackageNameUnqualified s_existing = ini.get(q_existing);
      throw JPRACompilerResolverException.packageImportConflict(
        s_existing, s_new);
    }

    try {
      final PackageContextType p = this.context.getPackage(q_name);
      this.imports.put(q_name, p);
      this.import_names.put(s_new, q_name);
    } catch (final JPRAModelLoadingException e) {
      throw new JPRACompilerResolverException(
        s_new.getLexicalInformation(),
        JPRAResolverErrorCode.PACKAGE_LOADING_ERROR,
        e);
    }
  }

  @Override public void resolvePackageEnd(
    final StatementPackageEnd<Unresolved> s)
    throws JPRACompilerResolverException
  {
    if (!this.current_package.isPresent()) {
      throw JPRACompilerResolverException.noCurrentPackage(
        s.getLexicalInformation());
    }

    this.imports.clear();
    this.import_names.clear();
    this.current_types.clear();
    this.current_package = Optional.empty();
  }

  @Override public TypeDeclType<ResolvedType> resolveTypeDeclaration(
    final TypeDeclType<Unresolved> expr)
    throws JPRACompilerResolverException
  {
    if (!this.current_package.isPresent()) {
      throw JPRACompilerResolverException.noCurrentPackage(
        expr.getLexicalInformation());
    }

    final TypeDeclType<ResolvedType> rv = expr.matchTypeDeclaration(
      new TypeDeclMatcherType<Unresolved, TypeDeclType<ResolvedType>,
        JPRACompilerResolverException>()
      {
        @Override public TypeDeclType<ResolvedType> matchRecord(
          final TypeDeclRecord<Unresolved> t)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeDeclarationRecord(t);
        }

        @Override public TypeDeclType<ResolvedType> matchPacked(
          final TypeDeclPacked<Unresolved> t)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeDeclarationPacked(t);
        }
      });

    this.current_types.put(rv.getName(), rv);
    return rv;
  }

  private TypeDeclType<ResolvedType> resolveTypeDeclarationPacked(
    final TypeDeclPacked<Unresolved> t)
  {
    throw new UnimplementedCodeException();
  }

  private TypeDeclType<ResolvedType> resolveTypeDeclarationRecord(
    final TypeDeclRecord<Unresolved> t)
    throws JPRACompilerResolverException
  {
    final ImmutableList<RecordFieldDeclType<Unresolved>> o =
      t.getFieldsInDeclarationOrder();
    final MutableList<RecordFieldDeclType<ResolvedType>> by_order =
      Lists.mutable.empty();

    for (int index = 0; index < o.size(); ++index) {
      by_order.add(this.resolveTypeDeclarationRecordField(o.get(index)));
    }

    final TypeDeclRecord<ResolvedType> rv = new TypeDeclRecord<>(
      this.getResolvedFresh(),
      this.current_record_fields.toImmutable(),
      t.getName(),
      by_order.toImmutable());

    this.current_record_fields.clear();
    return rv;
  }

  private ResolvedType getResolvedFresh()
  {
    return new Resolved(this.context.getFreshIdentifier());
  }

  private RecordFieldDeclType<ResolvedType> resolveTypeDeclarationRecordField(
    final RecordFieldDeclType<Unresolved> rf)
    throws JPRACompilerResolverException
  {
    return rf.matchRecordFieldDeclaration(
      new RecordFieldDeclMatcherType<Unresolved,
        RecordFieldDeclType<ResolvedType>, JPRACompilerResolverException>()
      {
        @Override public RecordFieldDeclType<ResolvedType> matchPadding(
          final RecordFieldDeclPaddingOctets<Unresolved> r)
          throws JPRACompilerResolverException
        {
          return new RecordFieldDeclPaddingOctets<>(
            r.getLexicalInformation(),
            JPRAResolver.this.resolveSizeExpression(r.getSizeExpression()));
        }

        @Override public RecordFieldDeclType<ResolvedType> matchValue(
          final RecordFieldDeclValue<Unresolved> r)
          throws JPRACompilerResolverException
        {
          final RecordFieldDeclValue<ResolvedType> v =
            new RecordFieldDeclValue<>(
              JPRAResolver.this.getResolvedFresh(),
              r.getName(),
              JPRAResolver.this.resolveTypeExpression(r.getType()));

          JPRAResolver.this.current_record_fields.put(r.getName(), v);
          return v;
        }
      });
  }

  @Override public TypeExprType<ResolvedType> resolveTypeExpression(
    final TypeExprType<Unresolved> expr)
    throws JPRACompilerResolverException
  {
    // TODO: Generated method stub!
    throw new UnimplementedCodeException();
  }

  @Override public SizeExprType<ResolvedType> resolveSizeExpression(
    final SizeExprType<Unresolved> expr)
    throws JPRACompilerResolverException
  {
    return expr.matchSizeExpression(
      new SizeExprMatcherType<Unresolved, SizeExprType<ResolvedType>,
        JPRACompilerResolverException>()
      {
        @Override public SizeExprType<ResolvedType> matchConstant(
          final SizeExprConstant<Unresolved> s)
          throws JPRACompilerResolverException
        {
          return new SizeExprConstant<>(
            s.getLexicalInformation(), s.getValue());
        }

        @Override public SizeExprType<ResolvedType> matchInOctets(
          final SizeExprInOctets<Unresolved> s)
          throws JPRACompilerResolverException
        {
          return new SizeExprInOctets<>(
            JPRAResolver.this.resolveTypeExpression(s.getTypeExpression()));
        }

        @Override public SizeExprType<ResolvedType> matchInBits(
          final SizeExprInBits<Unresolved> s)
          throws JPRACompilerResolverException
        {
          return new SizeExprInBits<>(
            JPRAResolver.this.resolveTypeExpression(s.getTypeExpression()));
        }
      });
  }

  private static final class Resolved implements ResolvedType
  {
    private final IdentifierType id;

    Resolved(final IdentifierType in_id)
    {
      this.id = NullCheck.notNull(in_id);
    }

    @Override public IdentifierType getIdentifier()
    {
      return this.id;
    }
  }
}
