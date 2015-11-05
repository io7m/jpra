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
import com.io7m.jpra.model.contexts.GlobalContextType;
import com.io7m.jpra.model.contexts.PackageContextType;
import com.io7m.jpra.model.loading.JPRAModelLoadingException;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.names.IdentifierType;
import com.io7m.jpra.model.names.PackageNameQualified;
import com.io7m.jpra.model.names.PackageNameUnqualified;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.jpra.model.names.TypeReference;
import com.io7m.jpra.model.size_expressions.SizeExprConstant;
import com.io7m.jpra.model.size_expressions.SizeExprInBits;
import com.io7m.jpra.model.size_expressions.SizeExprInOctets;
import com.io7m.jpra.model.size_expressions.SizeExprMatcherType;
import com.io7m.jpra.model.size_expressions.SizeExprType;
import com.io7m.jpra.model.statements.StatementCommandType;
import com.io7m.jpra.model.statements.StatementPackageBegin;
import com.io7m.jpra.model.statements.StatementPackageEnd;
import com.io7m.jpra.model.statements.StatementPackageImport;
import com.io7m.jpra.model.type_declarations.PackedFieldDeclMatcherType;
import com.io7m.jpra.model.type_declarations.PackedFieldDeclPaddingBits;
import com.io7m.jpra.model.type_declarations.PackedFieldDeclType;
import com.io7m.jpra.model.type_declarations.PackedFieldDeclValue;
import com.io7m.jpra.model.type_declarations.RecordFieldDeclMatcherType;
import com.io7m.jpra.model.type_declarations.RecordFieldDeclPaddingOctets;
import com.io7m.jpra.model.type_declarations.RecordFieldDeclType;
import com.io7m.jpra.model.type_declarations.RecordFieldDeclValue;
import com.io7m.jpra.model.type_declarations.TypeDeclMatcherType;
import com.io7m.jpra.model.type_declarations.TypeDeclPacked;
import com.io7m.jpra.model.type_declarations.TypeDeclRecord;
import com.io7m.jpra.model.type_declarations.TypeDeclType;
import com.io7m.jpra.model.type_expressions.TypeExprArray;
import com.io7m.jpra.model.type_expressions.TypeExprBooleanSet;
import com.io7m.jpra.model.type_expressions.TypeExprFloat;
import com.io7m.jpra.model.type_expressions.TypeExprIntegerSigned;
import com.io7m.jpra.model.type_expressions.TypeExprIntegerSignedNormalized;
import com.io7m.jpra.model.type_expressions.TypeExprIntegerUnsigned;
import com.io7m.jpra.model.type_expressions.TypeExprIntegerUnsignedNormalized;
import com.io7m.jpra.model.type_expressions.TypeExprMatcherType;
import com.io7m.jpra.model.type_expressions.TypeExprMatrix;
import com.io7m.jpra.model.type_expressions.TypeExprName;
import com.io7m.jpra.model.type_expressions.TypeExprString;
import com.io7m.jpra.model.type_expressions.TypeExprType;
import com.io7m.jpra.model.type_expressions.TypeExprTypeOfField;
import com.io7m.jpra.model.type_expressions.TypeExprVector;
import com.io7m.jpra.model.types.TypeUserDefinedType;
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
  private final MutableMap<TypeName, TypeDeclType<IdentifierType>>
                                                                     current_types;
  private final MutableMap<FieldName, RecordFieldDeclValue<IdentifierType>>
                                                                     current_record_fields;
  private final MutableMap<FieldName, PackedFieldDeclValue<IdentifierType>>
                                                                     current_packed_fields;
  private       Optional<PackageNameQualified>
                                                                     current_package;

  private JPRAResolver(
    final GlobalContextType c)
  {
    this.context = NullCheck.notNull(c);
    this.current_package = Optional.empty();
    this.current_record_fields = Maps.mutable.empty();
    this.current_packed_fields = Maps.mutable.empty();
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
  public Map<TypeName, TypeDeclType<IdentifierType>> resolveGetCurrentTypes()
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

  @Override public TypeDeclType<IdentifierType> resolveTypeDeclaration(
    final TypeDeclType<Unresolved> expr)
    throws JPRACompilerResolverException
  {
    if (!this.current_package.isPresent()) {
      throw JPRACompilerResolverException.noCurrentPackage(
        expr.getLexicalInformation());
    }

    final TypeDeclType<IdentifierType> rv = expr.matchTypeDeclaration(
      new TypeDeclMatcherType<Unresolved, TypeDeclType<IdentifierType>,
        JPRACompilerResolverException>()
      {
        @Override public TypeDeclType<IdentifierType> matchRecord(
          final TypeDeclRecord<Unresolved> t)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeDeclarationRecord(t);
        }

        @Override public TypeDeclType<IdentifierType> matchPacked(
          final TypeDeclPacked<Unresolved> t)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeDeclarationPacked(t);
        }
      });

    final TypeName t_name = rv.getName();
    if (this.current_types.containsKey(t_name)) {
      final TypeDeclType<IdentifierType> o = this.current_types.get(t_name);
      throw JPRACompilerResolverException.duplicateType(t_name, o.getName());
    }

    this.current_types.put(t_name, rv);
    return rv;
  }

  private TypeDeclType<IdentifierType> resolveTypeDeclarationPacked(
    final TypeDeclPacked<Unresolved> t)
    throws JPRACompilerResolverException
  {
    final ImmutableList<PackedFieldDeclType<Unresolved>> o =
      t.getFieldsInDeclarationOrder();
    final MutableList<PackedFieldDeclType<IdentifierType>> by_order =
      Lists.mutable.empty();

    for (int index = 0; index < o.size(); ++index) {
      by_order.add(this.resolveTypeDeclPackedField(o.get(index)));
    }

    final TypeDeclPacked<IdentifierType> rv = new TypeDeclPacked<>(
      this.context.getFreshIdentifier(),
      this.current_packed_fields.toImmutable(),
      t.getName(),
      by_order.toImmutable());

    this.current_record_fields.clear();
    return rv;
  }

  private PackedFieldDeclType<IdentifierType> resolveTypeDeclPackedField(
    final PackedFieldDeclType<Unresolved> f)
    throws JPRACompilerResolverException
  {
    return f.matchPackedFieldDeclaration(
      new PackedFieldDeclMatcherType<Unresolved,
        PackedFieldDeclType<IdentifierType>, JPRACompilerResolverException>()
      {
        @Override public PackedFieldDeclType<IdentifierType> matchPaddingBits(
          final PackedFieldDeclPaddingBits<Unresolved> r)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeDeclPackedFieldPaddingBits(r);
        }

        @Override public PackedFieldDeclType<IdentifierType> matchValue(
          final PackedFieldDeclValue<Unresolved> r)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeDeclPackedFieldValue(r);
        }
      });
  }

  private PackedFieldDeclType<IdentifierType>
  resolveTypeDeclPackedFieldPaddingBits(
    final PackedFieldDeclPaddingBits<Unresolved> r)
    throws JPRACompilerResolverException
  {
    return new PackedFieldDeclPaddingBits<>(
      r.getLexicalInformation(),
      this.resolveSizeExpression(r.getSizeExpression()));
  }

  private PackedFieldDeclType<IdentifierType> resolveTypeDeclPackedFieldValue(
    final PackedFieldDeclValue<Unresolved> r)
    throws JPRACompilerResolverException
  {
    return new PackedFieldDeclValue<>(
      this.context.getFreshIdentifier(),
      r.getName(),
      this.resolveTypeExpression(r.getType()));
  }

  private TypeDeclType<IdentifierType> resolveTypeDeclarationRecord(
    final TypeDeclRecord<Unresolved> t)
    throws JPRACompilerResolverException
  {
    final ImmutableList<RecordFieldDeclType<Unresolved>> o =
      t.getFieldsInDeclarationOrder();
    final MutableList<RecordFieldDeclType<IdentifierType>> by_order =
      Lists.mutable.empty();

    for (int index = 0; index < o.size(); ++index) {
      by_order.add(this.resolveTypeDeclRecordField(o.get(index)));
    }

    final TypeDeclRecord<IdentifierType> rv = new TypeDeclRecord<>(
      this.context.getFreshIdentifier(),
      this.current_record_fields.toImmutable(),
      t.getName(),
      by_order.toImmutable());

    this.current_record_fields.clear();
    return rv;
  }

  private RecordFieldDeclType<IdentifierType> resolveTypeDeclRecordField(
    final RecordFieldDeclType<Unresolved> rf)
    throws JPRACompilerResolverException
  {
    return rf.matchRecordFieldDeclaration(
      new RecordFieldDeclMatcherType<Unresolved,
        RecordFieldDeclType<IdentifierType>, JPRACompilerResolverException>()
      {
        @Override public RecordFieldDeclType<IdentifierType> matchPadding(
          final RecordFieldDeclPaddingOctets<Unresolved> r)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeDeclRecordFieldPaddingOctets(r);
        }

        @Override public RecordFieldDeclType<IdentifierType> matchValue(
          final RecordFieldDeclValue<Unresolved> r)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeRecordFieldDeclValue(r);
        }
      });
  }

  private RecordFieldDeclType<IdentifierType>
  resolveTypeDeclRecordFieldPaddingOctets(
    final RecordFieldDeclPaddingOctets<Unresolved> r)
    throws JPRACompilerResolverException
  {
    return new RecordFieldDeclPaddingOctets<>(
      r.getLexicalInformation(),
      this.resolveSizeExpression(r.getSizeExpression()));
  }

  private RecordFieldDeclType<IdentifierType> resolveTypeRecordFieldDeclValue(
    final RecordFieldDeclValue<Unresolved> r)
    throws JPRACompilerResolverException
  {
    final RecordFieldDeclValue<IdentifierType> v = new RecordFieldDeclValue<>(
      this.context.getFreshIdentifier(),
      r.getName(),
      this.resolveTypeExpression(r.getType()));

    this.current_record_fields.put(r.getName(), v);
    return v;
  }

  @Override public TypeExprType<IdentifierType> resolveTypeExpression(
    final TypeExprType<Unresolved> expr)
    throws JPRACompilerResolverException
  {
    return expr.matchType(
      new TypeExprMatcherType<Unresolved, TypeExprType<IdentifierType>,
        JPRACompilerResolverException>()
      {
        @Override public TypeExprType<IdentifierType> matchExprIntegerSigned(
          final TypeExprIntegerSigned<Unresolved> e)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeExprIntegerSigned(e);
        }

        @Override
        public TypeExprType<IdentifierType> matchExprIntegerSignedNormalized(
          final TypeExprIntegerSignedNormalized<Unresolved> e)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeExprIntegerSignedNormalized(e);
        }

        @Override public TypeExprType<IdentifierType> matchExprIntegerUnsigned(
          final TypeExprIntegerUnsigned<Unresolved> e)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeExprIntegerUnsigned(e);
        }

        @Override
        public TypeExprType<IdentifierType> matchExprIntegerUnsignedNormalized(
          final TypeExprIntegerUnsignedNormalized<Unresolved> e)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeExprIntegerUnsignedNormalized(e);
        }

        @Override public TypeExprType<IdentifierType> matchExprArray(
          final TypeExprArray<Unresolved> e)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeExprArray(e);
        }

        @Override public TypeExprType<IdentifierType> matchExprFloat(
          final TypeExprFloat<Unresolved> e)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeExprFloat(e);
        }

        @Override public TypeExprType<IdentifierType> matchExprVector(
          final TypeExprVector<Unresolved> e)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeExprVector(e);
        }

        @Override public TypeExprType<IdentifierType> matchExprMatrix(
          final TypeExprMatrix<Unresolved> e)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeExprMatrix(e);
        }

        @Override public TypeExprType<IdentifierType> matchExprString(
          final TypeExprString<Unresolved> e)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeExprString(e);
        }

        @Override public TypeExprType<IdentifierType> matchName(
          final TypeExprName<Unresolved> e)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeExprName(e);
        }

        @Override public TypeExprType<IdentifierType> matchTypeOfField(
          final TypeExprTypeOfField<Unresolved> e)
          throws JPRACompilerResolverException
        {
          // TODO: Generated method stub!
          throw new UnimplementedCodeException();
        }

        @Override public TypeExprType<IdentifierType> matchBooleanSet(
          final TypeExprBooleanSet<Unresolved> e)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeExprBooleanSet(e);
        }
      });
  }

  private TypeExprType<IdentifierType> resolveTypeExprName(
    final TypeExprName<Unresolved> e)
    throws JPRACompilerResolverException
  {
    final TypeReference ref = e.getReference();
    return new TypeExprName<>(JPRAResolver.this.resolveName(ref), ref);
  }

  private TypeExprType<IdentifierType> resolveTypeExprIntegerSigned(
    final TypeExprIntegerSigned<Unresolved> e)
    throws JPRACompilerResolverException
  {
    return new TypeExprIntegerSigned<>(
      e.getLexicalInformation(), this.resolveSizeExpression(e.getSize()));
  }

  private TypeExprType<IdentifierType> resolveTypeExprIntegerSignedNormalized(
    final TypeExprIntegerSignedNormalized<Unresolved> e)
    throws JPRACompilerResolverException
  {
    return new TypeExprIntegerSignedNormalized<>(
      e.getLexicalInformation(), this.resolveSizeExpression(e.getSize()));
  }

  private TypeExprType<IdentifierType> resolveTypeExprIntegerUnsigned(
    final TypeExprIntegerUnsigned<Unresolved> e)
    throws JPRACompilerResolverException
  {
    return new TypeExprIntegerUnsigned<>(
      e.getLexicalInformation(), this.resolveSizeExpression(e.getSize()));
  }

  private TypeExprType<IdentifierType> resolveTypeExprIntegerUnsignedNormalized(
    final TypeExprIntegerUnsignedNormalized<Unresolved> e)
    throws JPRACompilerResolverException
  {
    return new TypeExprIntegerUnsignedNormalized<>(
      e.getLexicalInformation(), this.resolveSizeExpression(e.getSize()));
  }

  private TypeExprType<IdentifierType> resolveTypeExprBooleanSet(
    final TypeExprBooleanSet<Unresolved> e)
    throws JPRACompilerResolverException
  {
    return new TypeExprBooleanSet<>(
      e.getLexicalInformation(),
      e.getFieldsInDeclarationOrder(),
      this.resolveSizeExpression(e.getSizeExpression()));
  }

  private TypeExprType<IdentifierType> resolveTypeExprFloat(
    final TypeExprFloat<Unresolved> e)
    throws JPRACompilerResolverException
  {
    return new TypeExprFloat<>(
      e.getLexicalInformation(), this.resolveSizeExpression(e.getSize()));
  }

  private TypeExprType<IdentifierType> resolveTypeExprVector(
    final TypeExprVector<Unresolved> e)
    throws JPRACompilerResolverException
  {
    return new TypeExprVector<>(
      e.getLexicalInformation(),
      this.resolveSizeExpression(e.getElementCount()),
      this.resolveTypeExpression(e.getElementType()));
  }

  private TypeExprType<IdentifierType> resolveTypeExprString(
    final TypeExprString<Unresolved> e)
    throws JPRACompilerResolverException
  {
    return new TypeExprString<>(
      e.getLexicalInformation(),
      this.resolveSizeExpression(e.getSize()),
      e.getEncoding());
  }

  private TypeExprType<IdentifierType> resolveTypeExprMatrix(
    final TypeExprMatrix<Unresolved> e)
    throws JPRACompilerResolverException
  {
    return new TypeExprMatrix<>(
      e.getLexicalInformation(),
      this.resolveSizeExpression(e.getWidth()),
      this.resolveSizeExpression(e.getHeight()),
      this.resolveTypeExpression(e.getElementType()));
  }

  private TypeExprType<IdentifierType> resolveTypeExprArray(
    final TypeExprArray<Unresolved> e)
    throws JPRACompilerResolverException
  {
    return new TypeExprArray<>(
      e.getLexicalInformation(),
      this.resolveSizeExpression(e.getElementCount()),
      this.resolveTypeExpression(e.getElementType()));
  }

  private IdentifierType resolveName(
    final TypeReference ref)
    throws JPRACompilerResolverException
  {
    final TypeName t_name = ref.getType();
    final Optional<PackageNameUnqualified> pack_opt = ref.getPackage();
    if (pack_opt.isPresent()) {
      final PackageNameUnqualified p_name = pack_opt.get();
      if (!this.import_names.containsKey(p_name)) {
        throw JPRACompilerResolverException.nonexistentPackageReference(p_name);
      }

      final PackageNameQualified q_name = this.import_names.get(p_name);
      Assertive.require(this.imports.containsKey(q_name));

      final PackageContextType p = this.imports.get(q_name);
      final Map<TypeName, TypeUserDefinedType> pt = p.getTypes();

      if (!pt.containsKey(t_name)) {
        throw JPRACompilerResolverException.nonexistentType(
          Optional.of(q_name), t_name);
      }

      return pt.get(t_name).getIdentifier();
    }

    if (!this.current_types.containsKey(t_name)) {
      throw JPRACompilerResolverException.nonexistentType(
        this.current_package, t_name);
    }

    return this.current_types.get(t_name).getIdentifier();
  }

  @Override public SizeExprType<IdentifierType> resolveSizeExpression(
    final SizeExprType<Unresolved> expr)
    throws JPRACompilerResolverException
  {
    return expr.matchSizeExpression(
      new SizeExprMatcherType<Unresolved, SizeExprType<IdentifierType>,
        JPRACompilerResolverException>()
      {
        @Override public SizeExprType<IdentifierType> matchConstant(
          final SizeExprConstant<Unresolved> s)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveSizeExprConstant(s);
        }

        @Override public SizeExprType<IdentifierType> matchInOctets(
          final SizeExprInOctets<Unresolved> s)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveSizeExprInOctets(s);
        }

        @Override public SizeExprType<IdentifierType> matchInBits(
          final SizeExprInBits<Unresolved> s)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveSizeExprInBits(s);
        }
      });
  }

  @Override public StatementCommandType<IdentifierType> resolveCommandType(
    final StatementCommandType<Unresolved> s)
    throws JPRACompilerResolverException
  {
    return new StatementCommandType<>(
      this.resolveTypeExpression(s.getExpression()));
  }

  private SizeExprType<IdentifierType> resolveSizeExprInBits(
    final SizeExprInBits<Unresolved> s)
    throws JPRACompilerResolverException
  {
    return new SizeExprInBits<>(
      this.resolveTypeExpression(s.getTypeExpression()));
  }

  private SizeExprType<IdentifierType> resolveSizeExprInOctets(
    final SizeExprInOctets<Unresolved> s)
    throws JPRACompilerResolverException
  {
    return new SizeExprInOctets<>(
      this.resolveTypeExpression(s.getTypeExpression()));
  }

  private SizeExprType<IdentifierType> resolveSizeExprConstant(
    final SizeExprConstant<Unresolved> s)
  {
    return new SizeExprConstant<>(s.getLexicalInformation(), s.getValue());
  }
}
