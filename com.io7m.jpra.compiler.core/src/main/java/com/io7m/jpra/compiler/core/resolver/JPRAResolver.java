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

import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jpra.compiler.core.internal.MutableBiMap;
import com.io7m.jpra.compiler.core.internal.MutableBiMapType;
import com.io7m.jpra.model.Unresolved;
import com.io7m.jpra.model.Untyped;
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
import io.vavr.collection.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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

  private final GlobalContextType context;
  private final MutableBiMapType<PackageNameUnqualified, PackageNameQualified> import_names;
  private final HashMap<PackageNameQualified, PackageContextType> imports;
  private final HashMap<TypeName, TypeDeclType<IdentifierType, Untyped>> current_types;
  private final HashMap<FieldName, RecordFieldDeclValue<IdentifierType, Untyped>> current_record_fields;
  private final HashMap<FieldName, PackedFieldDeclValue<IdentifierType, Untyped>> current_packed_fields;

  private final Optional<PackageNameQualified> expected_package;
  private boolean expected_received;
  private Optional<PackageNameQualified> current_package;

  private JPRAResolver(
    final GlobalContextType c,
    final Optional<PackageNameQualified> in_expected_package)
  {
    this.context =
      Objects.requireNonNull(c, "Context");
    this.expected_package =
      Objects.requireNonNull(in_expected_package, "Expected package");

    this.expected_received = false;
    this.current_package = Optional.empty();
    this.current_record_fields = new HashMap<>();
    this.current_packed_fields = new HashMap<>();
    this.current_types = new HashMap<>();
    this.imports = new HashMap<>();
    this.import_names = MutableBiMap.create();
  }

  /**
   * Create a new resolver. The resolver may optionally raise errors if the
   * package it resolves turns out not to be {@code in_expected_package}.
   *
   * @param c                   A global context
   * @param in_expected_package The expected package, if any
   *
   * @return A new resolver
   */

  public static JPRAResolverType newResolver(
    final GlobalContextType c,
    final Optional<PackageNameQualified> in_expected_package)
  {
    return new JPRAResolver(c, in_expected_package);
  }

  private static SizeExprType<IdentifierType, Untyped> resolveSizeExprConstant(
    final SizeExprConstant<Unresolved, Untyped> s)
  {
    return new SizeExprConstant<>(s.getLexicalInformation(), s.getValue());
  }

  @Override
  public Optional<PackageNameQualified> resolveGetCurrentPackage()
  {
    return this.current_package;
  }

  @Override
  public Map<TypeName, TypeDeclType<IdentifierType, Untyped>>
  resolveGetCurrentTypes()
  {
    return Collections.unmodifiableMap(this.current_types);
  }

  @Override
  public StatementPackageBegin<IdentifierType, Untyped> resolvePackageBegin(
    final StatementPackageBegin<Unresolved, Untyped> s)
    throws JPRACompilerResolverException
  {
    if (this.current_package.isPresent()) {
      throw JPRACompilerResolverException.nestedPackage(s.getPackageName());
    }

    Preconditions.checkPrecondition(
      this.current_types.isEmpty(),
      "Types must be empty");
    Preconditions.checkPrecondition(
      this.current_record_fields.isEmpty(),
      "Record fields must be empty");
    Preconditions.checkPrecondition(
      this.current_packed_fields.isEmpty(),
      "Packed fields must be empty");
    Preconditions.checkPrecondition(
      this.imports.isEmpty(),
      "Imports must be empty");
    Preconditions.checkPrecondition(
      this.import_names.isEmpty(),
      "Import names must be empty");

    if (this.expected_package.isPresent()) {
      final PackageNameQualified got = s.getPackageName();
      final PackageNameQualified expected = this.expected_package.get();
      if (!Objects.equals(got, expected)) {
        throw JPRACompilerResolverException.unexpectedPackage(expected, got);
      }
      this.expected_received = true;
    }

    final Map<PackageNameQualified, PackageContextType> existing =
      this.context.getPackages();

    final PackageNameQualified name = s.getPackageName();
    if (existing.containsKey(name)) {
      final PackageContextType p = existing.get(name);
      throw JPRACompilerResolverException.duplicatePackage(name, p.getName());
    }

    LOG.debug("start package {}", name);

    this.current_package = Optional.of(name);
    return new StatementPackageBegin<>(name);
  }

  @Override
  public StatementPackageImport<IdentifierType, Untyped> resolvePackageImport(
    final StatementPackageImport<Unresolved, Untyped> s)
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
      final MutableBiMapType<PackageNameQualified, PackageNameUnqualified> ini =
        this.import_names.inverse();

      Preconditions.checkPreconditionV(
        ini.containsKey(q_existing),
        "Import names must contain %s", q_existing);

      final PackageNameUnqualified s_existing = ini.get(q_existing);
      throw JPRACompilerResolverException
        .packageImportConflict(s_existing, s_new);
    }

    try {
      LOG.debug(
        "package {} imports {}", this.current_package.get(), q_name);

      final PackageContextType p = this.context.loadPackage(q_name);
      this.imports.put(q_name, p);
      this.import_names.put(s_new, q_name);
      return new StatementPackageImport<>(s.getPackageName(), s.getUsing());
    } catch (final JPRAModelLoadingException e) {
      throw new JPRACompilerResolverException(
        s_new.getLexicalInformation(),
        JPRAResolverErrorCode.PACKAGE_LOADING_ERROR,
        "Error loading package");
    }
  }

  @Override
  public StatementPackageEnd<IdentifierType, Untyped> resolvePackageEnd(
    final StatementPackageEnd<Unresolved, Untyped> s)
    throws JPRACompilerResolverException
  {
    if (!this.current_package.isPresent()) {
      throw JPRACompilerResolverException.noCurrentPackage(
        s.getLexicalInformation());
    }

    LOG.debug("end package {}", this.current_package.get());

    this.imports.clear();
    this.import_names.clear();
    this.current_types.clear();
    this.current_package = Optional.empty();

    return new StatementPackageEnd<>(s.getLexicalInformation());
  }

  @Override
  public TypeDeclType<IdentifierType, Untyped> resolveTypeDeclaration(
    final TypeDeclType<Unresolved, Untyped> expr)
    throws JPRACompilerResolverException
  {
    if (!this.current_package.isPresent()) {
      throw JPRACompilerResolverException.noCurrentPackage(
        expr.getLexicalInformation());
    }

    Preconditions.checkPreconditionV(
      this.current_packed_fields.isEmpty(),
      "Packed fields must be empty");
    Preconditions.checkPreconditionV(
      this.current_record_fields.isEmpty(),
      "Record fields must be empty");

    try {
      final TypeDeclType<IdentifierType, Untyped> rv =
        expr.matchTypeDeclaration(
          new TypeDeclMatcherType<Unresolved, Untyped,
            TypeDeclType<IdentifierType, Untyped>,
            JPRACompilerResolverException>()
          {
            @Override
            public TypeDeclType<IdentifierType, Untyped> matchRecord(
              final TypeDeclRecord<Unresolved, Untyped> t)
              throws JPRACompilerResolverException
            {
              return JPRAResolver.this.resolveTypeDeclarationRecord(t);
            }

            @Override
            public TypeDeclType<IdentifierType, Untyped> matchPacked(
              final TypeDeclPacked<Unresolved, Untyped> t)
              throws JPRACompilerResolverException
            {
              return JPRAResolver.this.resolveTypeDeclarationPacked(t);
            }
          });

      final TypeName t_name = rv.getName();
      if (this.current_types.containsKey(t_name)) {
        final TypeDeclType<IdentifierType, Untyped> o =
          this.current_types.get(t_name);
        throw JPRACompilerResolverException.duplicateType(t_name, o.getName());
      }

      this.current_types.put(t_name, rv);
      return rv;
    } finally {
      this.current_packed_fields.clear();
      this.current_record_fields.clear();
    }
  }

  private TypeDeclType<IdentifierType, Untyped> resolveTypeDeclarationPacked(
    final TypeDeclPacked<Unresolved, Untyped> t)
    throws JPRACompilerResolverException
  {
    final List<PackedFieldDeclType<Unresolved, Untyped>> o =
      t.getFieldsInDeclarationOrder();
    final ArrayList<PackedFieldDeclType<IdentifierType, Untyped>> by_order =
      new ArrayList<>();

    for (int index = 0; index < o.size(); ++index) {
      by_order.add(this.resolveTypeDeclPackedField(o.get(index)));
    }

    final TypeDeclPacked<IdentifierType, Untyped> rv =
      new TypeDeclPacked<>(
        this.context.getFreshIdentifier(),
        Untyped.get(),
        io.vavr.collection.HashMap.ofAll(this.current_packed_fields),
        t.getName(),
        List.ofAll(by_order));

    this.current_packed_fields.clear();
    return rv;
  }

  private PackedFieldDeclType<IdentifierType, Untyped>
  resolveTypeDeclPackedField(
    final PackedFieldDeclType<Unresolved, Untyped> f)
    throws JPRACompilerResolverException
  {
    return f.matchPackedFieldDeclaration(
      new PackedFieldDeclMatcherType<Unresolved, Untyped,
        PackedFieldDeclType<IdentifierType, Untyped>,
        JPRACompilerResolverException>()
      {
        @Override
        public PackedFieldDeclType<IdentifierType, Untyped> matchPaddingBits(
          final PackedFieldDeclPaddingBits<Unresolved, Untyped> r)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeDeclPackedFieldPaddingBits(r);
        }

        @Override
        public PackedFieldDeclType<IdentifierType, Untyped> matchValue(
          final PackedFieldDeclValue<Unresolved, Untyped> r)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeDeclPackedFieldValue(r);
        }
      });
  }

  private PackedFieldDeclType<IdentifierType, Untyped>
  resolveTypeDeclPackedFieldPaddingBits(
    final PackedFieldDeclPaddingBits<Unresolved, Untyped> r)
    throws JPRACompilerResolverException
  {
    return new PackedFieldDeclPaddingBits<>(
      r.getLexicalInformation(),
      this.resolveSizeExpression(r.getSizeExpression()));
  }

  private PackedFieldDeclType<IdentifierType, Untyped>
  resolveTypeDeclPackedFieldValue(
    final PackedFieldDeclValue<Unresolved, Untyped> r)
    throws JPRACompilerResolverException
  {
    final PackedFieldDeclValue<IdentifierType, Untyped> v =
      new PackedFieldDeclValue<>(
        this.context.getFreshIdentifier(),
        r.getName(),
        this.resolveTypeExpression(r.getType()));

    this.current_packed_fields.put(v.getName(), v);
    return v;
  }

  private TypeDeclType<IdentifierType, Untyped> resolveTypeDeclarationRecord(
    final TypeDeclRecord<Unresolved, Untyped> t)
    throws JPRACompilerResolverException
  {
    final List<RecordFieldDeclType<Unresolved, Untyped>> o =
      t.getFieldsInDeclarationOrder();
    final ArrayList<RecordFieldDeclType<IdentifierType, Untyped>> by_order =
      new ArrayList<>();

    for (int index = 0; index < o.size(); ++index) {
      by_order.add(this.resolveTypeDeclRecordField(o.get(index)));
    }

    final TypeDeclRecord<IdentifierType, Untyped> rv = new TypeDeclRecord<>(
      this.context.getFreshIdentifier(),
      Untyped.get(),
      io.vavr.collection.HashMap.ofAll(this.current_record_fields),
      t.getName(),
      List.ofAll(by_order));

    this.current_record_fields.clear();
    return rv;
  }

  private RecordFieldDeclType<IdentifierType, Untyped>
  resolveTypeDeclRecordField(
    final RecordFieldDeclType<Unresolved, Untyped> rf)
    throws JPRACompilerResolverException
  {
    return rf.matchRecordFieldDeclaration(
      new RecordFieldDeclMatcherType<Unresolved, Untyped,
        RecordFieldDeclType<IdentifierType, Untyped>,
        JPRACompilerResolverException>()
      {
        @Override
        public RecordFieldDeclType<IdentifierType, Untyped> matchPadding(
          final RecordFieldDeclPaddingOctets<Unresolved, Untyped> r)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeDeclRecordFieldPaddingOctets(r);
        }

        @Override
        public RecordFieldDeclType<IdentifierType, Untyped> matchValue(
          final RecordFieldDeclValue<Unresolved, Untyped> r)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeRecordFieldDeclValue(r);
        }
      });
  }

  private RecordFieldDeclType<IdentifierType, Untyped>
  resolveTypeDeclRecordFieldPaddingOctets(
    final RecordFieldDeclPaddingOctets<Unresolved, Untyped> r)
    throws JPRACompilerResolverException
  {
    return new RecordFieldDeclPaddingOctets<>(
      r.getLexicalInformation(),
      this.resolveSizeExpression(r.getSizeExpression()));
  }

  private RecordFieldDeclType<IdentifierType, Untyped>
  resolveTypeRecordFieldDeclValue(
    final RecordFieldDeclValue<Unresolved, Untyped> r)
    throws JPRACompilerResolverException
  {
    final RecordFieldDeclValue<IdentifierType, Untyped> v =
      new RecordFieldDeclValue<>(
        this.context.getFreshIdentifier(),
        r.getName(),
        this.resolveTypeExpression(r.getType()));

    this.current_record_fields.put(r.getName(), v);
    return v;
  }

  @Override
  public TypeExprType<IdentifierType, Untyped> resolveTypeExpression(
    final TypeExprType<Unresolved, Untyped> expr)
    throws JPRACompilerResolverException
  {
    return expr.matchType(
      new TypeExprMatcherType<Unresolved, Untyped,
        TypeExprType<IdentifierType, Untyped>, JPRACompilerResolverException>()
      {
        @Override
        public TypeExprType<IdentifierType, Untyped> matchExprIntegerSigned(
          final TypeExprIntegerSigned<Unresolved, Untyped> e)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeExprIntegerSigned(e);
        }

        @Override
        public TypeExprType<IdentifierType, Untyped>
        matchExprIntegerSignedNormalized(
          final TypeExprIntegerSignedNormalized<Unresolved, Untyped> e)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeExprIntegerSignedNormalized(e);
        }

        @Override
        public TypeExprType<IdentifierType, Untyped> matchExprIntegerUnsigned(
          final TypeExprIntegerUnsigned<Unresolved, Untyped> e)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeExprIntegerUnsigned(e);
        }

        @Override
        public TypeExprType<IdentifierType, Untyped>
        matchExprIntegerUnsignedNormalized(
          final TypeExprIntegerUnsignedNormalized<Unresolved, Untyped> e)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeExprIntegerUnsignedNormalized(e);
        }

        @Override
        public TypeExprType<IdentifierType, Untyped> matchExprArray(
          final TypeExprArray<Unresolved, Untyped> e)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeExprArray(e);
        }

        @Override
        public TypeExprType<IdentifierType, Untyped> matchExprFloat(
          final TypeExprFloat<Unresolved, Untyped> e)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeExprFloat(e);
        }

        @Override
        public TypeExprType<IdentifierType, Untyped> matchExprVector(
          final TypeExprVector<Unresolved, Untyped> e)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeExprVector(e);
        }

        @Override
        public TypeExprType<IdentifierType, Untyped> matchExprMatrix(
          final TypeExprMatrix<Unresolved, Untyped> e)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeExprMatrix(e);
        }

        @Override
        public TypeExprType<IdentifierType, Untyped> matchExprString(
          final TypeExprString<Unresolved, Untyped> e)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeExprString(e);
        }

        @Override
        public TypeExprType<IdentifierType, Untyped> matchName(
          final TypeExprName<Unresolved, Untyped> e)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeExprName(e);
        }

        @Override
        public TypeExprType<IdentifierType, Untyped> matchTypeOfField(
          final TypeExprTypeOfField<Unresolved, Untyped> e)
          throws JPRACompilerResolverException
        {
          // TODO: Generated method stub!
          throw new UnimplementedCodeException();
        }

        @Override
        public TypeExprType<IdentifierType, Untyped> matchBooleanSet(
          final TypeExprBooleanSet<Unresolved, Untyped> e)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveTypeExprBooleanSet(e);
        }
      });
  }

  private TypeExprType<IdentifierType, Untyped> resolveTypeExprName(
    final TypeExprName<Unresolved, Untyped> e)
    throws JPRACompilerResolverException
  {
    final TypeReference ref = e.getReference();
    return new TypeExprName<>(
      JPRAResolver.this.resolveName(ref), Untyped.get(), ref);
  }

  private TypeExprType<IdentifierType, Untyped> resolveTypeExprIntegerSigned(
    final TypeExprIntegerSigned<Unresolved, Untyped> e)
    throws JPRACompilerResolverException
  {
    return new TypeExprIntegerSigned<>(
      Untyped.get(),
      e.getLexicalInformation(),
      this.resolveSizeExpression(e.getSize()));
  }

  private TypeExprType<IdentifierType, Untyped>
  resolveTypeExprIntegerSignedNormalized(
    final TypeExprIntegerSignedNormalized<Unresolved, Untyped> e)
    throws JPRACompilerResolverException
  {
    return new TypeExprIntegerSignedNormalized<>(
      Untyped.get(),
      e.getLexicalInformation(),
      this.resolveSizeExpression(e.getSize()));
  }

  private TypeExprType<IdentifierType, Untyped> resolveTypeExprIntegerUnsigned(
    final TypeExprIntegerUnsigned<Unresolved, Untyped> e)
    throws JPRACompilerResolverException
  {
    return new TypeExprIntegerUnsigned<>(
      Untyped.get(),
      e.getLexicalInformation(),
      this.resolveSizeExpression(e.getSize()));
  }

  private TypeExprType<IdentifierType, Untyped>
  resolveTypeExprIntegerUnsignedNormalized(
    final TypeExprIntegerUnsignedNormalized<Unresolved, Untyped> e)
    throws JPRACompilerResolverException
  {
    return new TypeExprIntegerUnsignedNormalized<>(
      Untyped.get(),
      e.getLexicalInformation(),
      this.resolveSizeExpression(e.getSize()));
  }

  private TypeExprType<IdentifierType, Untyped> resolveTypeExprBooleanSet(
    final TypeExprBooleanSet<Unresolved, Untyped> e)
    throws JPRACompilerResolverException
  {
    return new TypeExprBooleanSet<>(
      Untyped.get(),
      e.getLexicalInformation(),
      e.getFieldsInDeclarationOrder(),
      this.resolveSizeExpression(e.getSizeExpression()));
  }

  private TypeExprType<IdentifierType, Untyped> resolveTypeExprFloat(
    final TypeExprFloat<Unresolved, Untyped> e)
    throws JPRACompilerResolverException
  {
    return new TypeExprFloat<>(
      Untyped.get(),
      e.getLexicalInformation(),
      this.resolveSizeExpression(e.getSize()));
  }

  private TypeExprType<IdentifierType, Untyped> resolveTypeExprVector(
    final TypeExprVector<Unresolved, Untyped> e)
    throws JPRACompilerResolverException
  {
    return new TypeExprVector<>(
      Untyped.get(),
      e.getLexicalInformation(),
      this.resolveSizeExpression(e.getElementCount()),
      this.resolveTypeExpression(e.getElementType()));
  }

  private TypeExprType<IdentifierType, Untyped> resolveTypeExprString(
    final TypeExprString<Unresolved, Untyped> e)
    throws JPRACompilerResolverException
  {
    return new TypeExprString<>(
      Untyped.get(),
      e.getLexicalInformation(),
      this.resolveSizeExpression(e.getSize()),
      e.getEncoding());
  }

  private TypeExprType<IdentifierType, Untyped> resolveTypeExprMatrix(
    final TypeExprMatrix<Unresolved, Untyped> e)
    throws JPRACompilerResolverException
  {
    return new TypeExprMatrix<>(
      Untyped.get(),
      e.getLexicalInformation(),
      this.resolveSizeExpression(e.getWidth()),
      this.resolveSizeExpression(e.getHeight()),
      this.resolveTypeExpression(e.getElementType()));
  }

  private TypeExprType<IdentifierType, Untyped> resolveTypeExprArray(
    final TypeExprArray<Unresolved, Untyped> e)
    throws JPRACompilerResolverException
  {
    return new TypeExprArray<>(
      Untyped.get(),
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
        LOG.debug("nonexistent package reference: {}", p_name);
        throw JPRACompilerResolverException.nonexistentPackageReference(p_name);
      }

      final PackageNameQualified q_name = this.import_names.get(p_name);
      Preconditions.checkPreconditionV(
        this.imports.containsKey(q_name),
        "Imports must contain %s",
        q_name);

      final PackageContextType p = this.imports.get(q_name);
      final Map<TypeName, TypeUserDefinedType> pt = p.getTypes();

      if (!pt.containsKey(t_name)) {
        LOG.debug("nonexistent type: {}", t_name);
        throw JPRACompilerResolverException.nonexistentType(
          Optional.of(q_name), t_name);
      }

      return pt.get(t_name).getIdentifier();
    }

    if (!this.current_types.containsKey(t_name)) {
      LOG.debug("nonexistent type: {}", t_name);
      throw JPRACompilerResolverException.nonexistentType(
        this.current_package, t_name);
    }

    return this.current_types.get(t_name).getIdentifier();
  }

  @Override
  public SizeExprType<IdentifierType, Untyped> resolveSizeExpression(
    final SizeExprType<Unresolved, Untyped> expr)
    throws JPRACompilerResolverException
  {
    return expr.matchSizeExpression(
      new SizeExprMatcherType<Unresolved, Untyped,
        SizeExprType<IdentifierType, Untyped>, JPRACompilerResolverException>()
      {
        @Override
        public SizeExprType<IdentifierType, Untyped> matchConstant(
          final SizeExprConstant<Unresolved, Untyped> s)
          throws JPRACompilerResolverException
        {
          return resolveSizeExprConstant(s);
        }

        @Override
        public SizeExprType<IdentifierType, Untyped> matchInOctets(
          final SizeExprInOctets<Unresolved, Untyped> s)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveSizeExprInOctets(s);
        }

        @Override
        public SizeExprType<IdentifierType, Untyped> matchInBits(
          final SizeExprInBits<Unresolved, Untyped> s)
          throws JPRACompilerResolverException
        {
          return JPRAResolver.this.resolveSizeExprInBits(s);
        }
      });
  }

  @Override
  public StatementCommandType<IdentifierType, Untyped> resolveCommandType(
    final StatementCommandType<Unresolved, Untyped> s)
    throws JPRACompilerResolverException
  {
    return new StatementCommandType<>(
      this.resolveTypeExpression(s.getExpression()));
  }

  @Override
  public void resolveEOF(
    final Optional<LexicalPosition<Path>> lex)
    throws JPRACompilerResolverException
  {
    if (this.current_package.isPresent()) {
      throw JPRACompilerResolverException.unexpectedEOF(lex);
    }

    if (this.expected_package.isPresent()) {
      if (!this.expected_received) {
        throw JPRACompilerResolverException.expectedPackage(
          this.expected_package.get());
      }
    }
  }

  private SizeExprType<IdentifierType, Untyped> resolveSizeExprInBits(
    final SizeExprInBits<Unresolved, Untyped> s)
    throws JPRACompilerResolverException
  {
    return new SizeExprInBits<>(
      this.resolveTypeExpression(s.getTypeExpression()));
  }

  private SizeExprType<IdentifierType, Untyped> resolveSizeExprInOctets(
    final SizeExprInOctets<Unresolved, Untyped> s)
    throws JPRACompilerResolverException
  {
    return new SizeExprInOctets<>(
      this.resolveTypeExpression(s.getTypeExpression()));
  }
}
