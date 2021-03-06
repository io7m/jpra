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

package com.io7m.jpra.compiler.core.checker;

import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jpra.model.Untyped;
import com.io7m.jpra.model.contexts.GlobalContextType;
import com.io7m.jpra.model.contexts.PackageContextType;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.names.IdentifierType;
import com.io7m.jpra.model.names.PackageNameQualified;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.jpra.model.size_expressions.SizeExprConstant;
import com.io7m.jpra.model.size_expressions.SizeExprInBits;
import com.io7m.jpra.model.size_expressions.SizeExprInOctets;
import com.io7m.jpra.model.size_expressions.SizeExprMatcherType;
import com.io7m.jpra.model.size_expressions.SizeExprType;
import com.io7m.jpra.model.statements.StatementCommandType;
import com.io7m.jpra.model.statements.StatementPackageBegin;
import com.io7m.jpra.model.statements.StatementPackageEnd;
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
import com.io7m.jpra.model.types.Size;
import com.io7m.jpra.model.types.SizeUnitBitsType;
import com.io7m.jpra.model.types.SizeUnitOctetsType;
import com.io7m.jpra.model.types.SizeUnitType;
import com.io7m.jpra.model.types.TArray;
import com.io7m.jpra.model.types.TBooleanSet;
import com.io7m.jpra.model.types.TFloat;
import com.io7m.jpra.model.types.TIntegerSigned;
import com.io7m.jpra.model.types.TIntegerSignedNormalized;
import com.io7m.jpra.model.types.TIntegerType;
import com.io7m.jpra.model.types.TIntegerUnsigned;
import com.io7m.jpra.model.types.TIntegerUnsignedNormalized;
import com.io7m.jpra.model.types.TMatrix;
import com.io7m.jpra.model.types.TPacked;
import com.io7m.jpra.model.types.TPackedBuilderType;
import com.io7m.jpra.model.types.TRecord;
import com.io7m.jpra.model.types.TRecordBuilderType;
import com.io7m.jpra.model.types.TString;
import com.io7m.jpra.model.types.TType;
import com.io7m.jpra.model.types.TVector;
import com.io7m.jpra.model.types.TypeScalarMatcherType;
import com.io7m.jpra.model.types.TypeScalarType;
import com.io7m.jpra.model.types.TypeUserDefinedType;
import com.io7m.jranges.RangeInclusiveB;
import com.io7m.junreachable.UnimplementedCodeException;
import com.io7m.junreachable.UnreachableCodeException;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;

import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * The default implementation of the {@link JPRACheckerType} interface.
 */

// Class Data Abstraction Coupling: Unavoidable?
// CHECKSTYLE:OFF
public final class JPRAChecker implements JPRACheckerType
{
  // CHECKSTYLE:ON
  private static final RangeInclusiveB BOOLEAN_SET_SIZES;

  static {
    BOOLEAN_SET_SIZES =
      RangeInclusiveB.of(BigInteger.ONE, BigInteger.valueOf(128L));
  }

  private final GlobalContextType context;
  private final JPRACheckerCapabilitiesType caps;
  private PackageContext package_ctx;
  private Optional<PackageNameQualified> current_package;
  private TypeExpressionContext type_context;

  private JPRAChecker(
    final GlobalContextType c,
    final JPRACheckerCapabilitiesType in_caps)
  {
    this.context = Objects.requireNonNull(c, "Context");
    this.caps = Objects.requireNonNull(in_caps, "Capabilities");
    this.type_context = TypeExpressionContext.NONE;
  }

  /**
   * @param c    A global context
   * @param caps The capabilities that will be enforced
   *
   * @return A new resolver
   */

  public static JPRACheckerType newChecker(
    final GlobalContextType c,
    final JPRACheckerCapabilitiesType caps)
  {
    return new JPRAChecker(c, caps);
  }

  private static <T extends SizeUnitType> Size<T> evaluateSize(
    final SizeExprType<IdentifierType, TType> se)
  {
    return se.matchSizeExpression(new CheckEvaluateSizeExpression<>());
  }

  private static SizeExprType<IdentifierType, TType> checkSizeExprConstant(
    final SizeExprConstant<IdentifierType, Untyped> s)
  {
    return new SizeExprConstant<>(s.lexical(), s.getValue());
  }

  @Override
  public void checkPackageBegin(
    final StatementPackageBegin<IdentifierType, Untyped> s)
  {
    Objects.requireNonNull(s, "Statement");
    final PackageNameQualified name = s.getPackageName();
    this.current_package = Optional.of(name);
    this.package_ctx = new PackageContext(this.context, name);
  }

  @Override
  public PackageContextType checkPackageEnd(
    final StatementPackageEnd<IdentifierType, Untyped> s)
  {
    Objects.requireNonNull(s, "Statement");
    this.current_package = Optional.empty();
    return this.package_ctx;
  }

  @Override
  public TypeDeclType<IdentifierType, TType> checkTypeDeclaration(
    final TypeDeclType<IdentifierType, Untyped> decl)
    throws JPRACompilerCheckerException
  {
    Objects.requireNonNull(decl, "Declaration");

    try {
      final TypeDeclType<IdentifierType, TType> rv = decl.matchTypeDeclaration(
        new TypeDeclMatcherType<IdentifierType, Untyped,
          TypeDeclType<IdentifierType, TType>, JPRACompilerCheckerException>()
        {
          @Override
          public TypeDeclType<IdentifierType, TType> matchRecord(
            final TypeDeclRecord<IdentifierType, Untyped> t)
            throws JPRACompilerCheckerException
          {
            return JPRAChecker.this.checkTypeDeclRecord(t);
          }

          @Override
          public TypeDeclType<IdentifierType, TType> matchPacked(
            final TypeDeclPacked<IdentifierType, Untyped> t)
            throws JPRACompilerCheckerException
          {
            return JPRAChecker.this.checkTypeDeclPacked(t);
          }
        });

      final TType tt = rv.getType();
      Preconditions.checkPreconditionV(
        tt instanceof TypeUserDefinedType,
        "Type must be an instance of %s",
        TypeUserDefinedType.class);
      this.context.putType((TypeUserDefinedType) tt);
      this.package_ctx.putType((TypeUserDefinedType) tt);
      return rv;
    } finally {
      this.type_context = TypeExpressionContext.NONE;
    }
  }

  private TypeDeclPacked<IdentifierType, TType> checkTypeDeclPacked(
    final TypeDeclPacked<IdentifierType, Untyped> t)
    throws JPRACompilerCheckerException
  {
    this.type_context = TypeExpressionContext.PACKED;

    final io.vavr.collection.Map<FieldName, PackedFieldDeclValue<IdentifierType, Untyped>>
      orig_named = t.getFieldsByName();
    final java.util.HashMap<FieldName, PackedFieldDeclValue<IdentifierType, TType>>
      fields_named = new java.util.HashMap<>();
    final List<PackedFieldDeclType<IdentifierType, Untyped>>
      orig_ordered = t.getFieldsInDeclarationOrder();
    final ArrayList<PackedFieldDeclType<IdentifierType, TType>>
      fields_ordered = new ArrayList<>();

    final TPackedBuilderType b =
      TPacked.newBuilder(this.package_ctx, t.getIdentifier(), t.getName());

    for (int index = 0; index < orig_ordered.size(); ++index) {
      orig_ordered.get(index).matchPackedFieldDeclaration(
        new PackedFieldDeclMatcherType<IdentifierType, Untyped,
          PackedFieldDeclType<IdentifierType, TType>,
          JPRACompilerCheckerException>()
        {
          @Override
          public PackedFieldDeclType<IdentifierType, TType> matchPaddingBits(
            final PackedFieldDeclPaddingBits<IdentifierType, Untyped> r)
            throws JPRACompilerCheckerException
          {
            return JPRAChecker.this.checkTypeDeclPackedFieldPaddingBits(
              r, fields_ordered, b);
          }

          @Override
          public PackedFieldDeclType<IdentifierType, TType> matchValue(
            final PackedFieldDeclValue<IdentifierType, Untyped> r)
            throws JPRACompilerCheckerException
          {
            return JPRAChecker.this.checkTypeDeclPackedFieldValue(
              r, fields_ordered, fields_named, b);
          }
        });
    }

    Preconditions.checkPreconditionV(
      fields_ordered.size() == orig_ordered.size(),
      "%d == %d",
      Integer.valueOf(fields_ordered.size()),
      Integer.valueOf(orig_ordered.size()));
    Preconditions.checkPreconditionV(
      fields_named.size() == orig_named.size(), "%d == %d",
      Integer.valueOf(fields_named.size()),
      Integer.valueOf(orig_named.size()));

    fields_named.forEach((k, value) -> {
      Preconditions.checkPreconditionV(
        orig_named.containsKey(k), "Names must contain %s", k);
    });

    final BigInteger sv = b.getCurrentSize().getValue();
    if (!this.caps.isPackedSizeBitsSupported(sv)) {
      throw JPRACompilerCheckerException.packedSizeNotSupported(
        t.getName(), sv, this.caps.getPackedSizeBitsSupported());
    }

    final TPacked type = b.build();
    Preconditions.checkPreconditionV(
      type.getFieldsInDeclarationOrder().size() == orig_ordered.size(),
      "%d == %d",
      Integer.valueOf(type.getFieldsInDeclarationOrder().size()),
      Integer.valueOf(orig_ordered.size()));

    Preconditions.checkPreconditionV(
      type.getFieldsByName().size() == orig_named.size(),
      "%d == %d",
      Integer.valueOf(type.getFieldsByName().size()),
      Integer.valueOf(orig_named.size()));

    type.getFieldsByName().forEach((k, value) -> {
      Preconditions.checkPreconditionV(
        orig_named.containsKey(k), "Names must contain %s", k);
    });

    return new TypeDeclPacked<>(
      t.getIdentifier(),
      type,
      HashMap.ofAll(fields_named),
      t.getName(),
      List.ofAll(fields_ordered));
  }

  private PackedFieldDeclType<IdentifierType, TType>
  checkTypeDeclPackedFieldValue(
    final PackedFieldDeclValue<IdentifierType, Untyped> r,
    final ArrayList<PackedFieldDeclType<IdentifierType, TType>>
      fields_ordered,
    final java.util.HashMap<FieldName, PackedFieldDeclValue<IdentifierType, TType>>
      fields_named,
    final TPackedBuilderType b)
    throws JPRACompilerCheckerException
  {
    final PackedFieldDeclValue<IdentifierType, TType> rv =
      this.checkPackedFieldValue(r);
    final TypeExprType<IdentifierType, TType> rvt = rv.getType();

    final TType rvtt = rvt.getType();
    if (!(rvtt instanceof TIntegerType)) {
      throw JPRACompilerCheckerException.packedNonIntegerType(r, rvt);
    }
    final TIntegerType rvti = (TIntegerType) rvtt;

    fields_ordered.add(rv);
    fields_named.put(rv.getName(), rv);
    b.addField(rv.getName(), rv.getIdentifier(), rvti);
    return rv;
  }

  private PackedFieldDeclValue<IdentifierType, TType> checkPackedFieldValue(
    final PackedFieldDeclValue<IdentifierType, Untyped> r)
    throws JPRACompilerCheckerException
  {
    final TypeExprType<IdentifierType, TType> type =
      this.checkTypeExpression(r.getType());
    return new PackedFieldDeclValue<>(r.getIdentifier(), r.getName(), type);
  }

  private PackedFieldDeclType<IdentifierType, TType>
  checkTypeDeclPackedFieldPaddingBits(
    final PackedFieldDeclPaddingBits<IdentifierType, Untyped> r,
    final ArrayList<PackedFieldDeclType<IdentifierType, TType>>
      fields_ordered,
    final TPackedBuilderType b)
    throws JPRACompilerCheckerException
  {
    final PackedFieldDeclPaddingBits<IdentifierType, TType> rv =
      this.checkPackedFieldPaddingBits(r);
    final Size<SizeUnitBitsType> size =
      evaluateSize(rv.getSizeExpression());

    if (size.getValue().compareTo(BigInteger.ZERO) <= 0) {
      throw JPRACompilerCheckerException.paddingSizeInvalid(
        r.lexical(), size.getValue());
    }

    fields_ordered.add(rv);
    b.addPaddingBits(r.lexical(), size);
    return rv;
  }

  private PackedFieldDeclPaddingBits<IdentifierType, TType>
  checkPackedFieldPaddingBits(
    final PackedFieldDeclPaddingBits<IdentifierType, Untyped> r)
    throws JPRACompilerCheckerException
  {
    final SizeExprType<IdentifierType, TType> size =
      this.checkSizeExpr(r.getSizeExpression());
    return new PackedFieldDeclPaddingBits<>(r.lexical(), size);
  }

  private TypeDeclRecord<IdentifierType, TType> checkTypeDeclRecord(
    final TypeDeclRecord<IdentifierType, Untyped> t)
    throws JPRACompilerCheckerException
  {
    this.type_context = TypeExpressionContext.RECORD;

    final io.vavr.collection.Map<FieldName, RecordFieldDeclValue<IdentifierType, Untyped>>
      orig_named = t.getFieldsByName();
    final java.util.HashMap<FieldName, RecordFieldDeclValue<IdentifierType, TType>>
      fields_named = new java.util.HashMap<>();
    final List<RecordFieldDeclType<IdentifierType, Untyped>>
      orig_ordered = t.getFieldsInDeclarationOrder();
    final ArrayList<RecordFieldDeclType<IdentifierType, TType>>
      fields_ordered = new ArrayList<>();

    final TRecordBuilderType b =
      TRecord.newBuilder(this.package_ctx, t.getIdentifier(), t.getName());

    for (int index = 0; index < orig_ordered.size(); ++index) {
      orig_ordered.get(index).matchRecordFieldDeclaration(
        new RecordFieldDeclMatcherType<IdentifierType, Untyped,
          RecordFieldDeclType<IdentifierType, TType>,
          JPRACompilerCheckerException>()
        {
          @Override
          public RecordFieldDeclType<IdentifierType, TType> matchPadding(
            final RecordFieldDeclPaddingOctets<IdentifierType, Untyped> r)
            throws JPRACompilerCheckerException
          {
            return JPRAChecker.this.checkTypeDeclRecordFieldPaddingOctets(
              r, fields_ordered, b);
          }

          @Override
          public RecordFieldDeclType<IdentifierType, TType> matchValue(
            final RecordFieldDeclValue<IdentifierType, Untyped> r)
            throws JPRACompilerCheckerException
          {
            return JPRAChecker.this.checkTypeDeclRecordFieldValue(
              r, fields_ordered, fields_named, b);
          }
        });
    }

    Preconditions.checkPreconditionV(
      fields_ordered.size() == orig_ordered.size(), "%d == %d",
      Integer.valueOf(fields_ordered.size()),
      Integer.valueOf(orig_ordered.size()));
    Preconditions.checkPreconditionV(
      fields_named.size() == orig_named.size(), "%d == %d",
      Integer.valueOf(fields_named.size()),
      Integer.valueOf(orig_named.size()));

    fields_named.forEach((k, value) -> {
      Preconditions.checkPreconditionV(
        orig_named.containsKey(k), "Names must contain %s", k);
    });

    final TRecord type = b.build();
    Preconditions.checkPreconditionV(
      type.getFieldsInDeclarationOrder().size() == orig_ordered.size(),
      "%d == %d",
      Integer.valueOf(type.getFieldsInDeclarationOrder().size()),
      Integer.valueOf(orig_ordered.size()));
    Preconditions.checkPreconditionV(
      type.getFieldsByName().size() == orig_named.size(),
      "%d == %d",
      Integer.valueOf(type.getFieldsByName().size()),
      Integer.valueOf(orig_named.size()));

    type.getFieldsByName().forEach((k, value) -> {
      Preconditions.checkPreconditionV(
        orig_named.containsKey(k), "Names must contain %s", k);
    });

    return new TypeDeclRecord<>(
      t.getIdentifier(),
      type,
      HashMap.ofAll(fields_named),
      t.getName(),
      List.ofAll(fields_ordered));
  }

  private RecordFieldDeclType<IdentifierType, TType>
  checkTypeDeclRecordFieldValue(
    final RecordFieldDeclValue<IdentifierType, Untyped> r,
    final ArrayList<RecordFieldDeclType<IdentifierType, TType>>
      fields_ordered,
    final java.util.HashMap<FieldName, RecordFieldDeclValue<IdentifierType, TType>>
      fields_named,
    final TRecordBuilderType b)
    throws JPRACompilerCheckerException
  {
    final RecordFieldDeclValue<IdentifierType, TType> rv =
      this.checkRecordFieldValue(r);
    final TypeExprType<IdentifierType, TType> rvt = rv.getType();

    fields_ordered.add(rv);
    fields_named.put(rv.getName(), rv);
    b.addField(rv.getName(), rv.getIdentifier(), rvt.getType());
    return rv;
  }

  private RecordFieldDeclType<IdentifierType, TType>
  checkTypeDeclRecordFieldPaddingOctets(
    final RecordFieldDeclPaddingOctets<IdentifierType, Untyped> r,
    final ArrayList<RecordFieldDeclType<IdentifierType, TType>>
      fields_ordered,
    final TRecordBuilderType b)
    throws JPRACompilerCheckerException
  {
    final RecordFieldDeclPaddingOctets<IdentifierType, TType> rv =
      this.checkRecordFieldPaddingOctets(r);
    final Size<SizeUnitOctetsType> size =
      evaluateSize(rv.getSizeExpression());

    if (size.getValue().compareTo(BigInteger.ZERO) <= 0) {
      throw JPRACompilerCheckerException.paddingSizeInvalid(
        r.lexical(), size.getValue());
    }

    fields_ordered.add(rv);
    b.addPaddingOctets(r.lexical(), size);
    return rv;
  }

  private RecordFieldDeclValue<IdentifierType, TType> checkRecordFieldValue(
    final RecordFieldDeclValue<IdentifierType, Untyped> r)
    throws JPRACompilerCheckerException
  {
    final TypeExprType<IdentifierType, TType> type =
      this.checkTypeExpression(r.getType());
    return new RecordFieldDeclValue<>(r.getIdentifier(), r.getName(), type);
  }

  private RecordFieldDeclPaddingOctets<IdentifierType, TType>
  checkRecordFieldPaddingOctets(
    final RecordFieldDeclPaddingOctets<IdentifierType, Untyped> r)
    throws JPRACompilerCheckerException
  {
    final SizeExprType<IdentifierType, TType> size =
      this.checkSizeExpr(r.getSizeExpression());
    return new RecordFieldDeclPaddingOctets<>(r.lexical(), size);
  }

  @Override
  public TypeExprType<IdentifierType, TType> checkTypeExpression(
    final TypeExprType<IdentifierType, Untyped> expr)
    throws JPRACompilerCheckerException
  {
    Objects.requireNonNull(expr, "Expression");
    return expr.matchType(new CheckTypeExpression());
  }

  @Override
  public StatementCommandType<IdentifierType, TType> checkCommandType(
    final StatementCommandType<IdentifierType, Untyped> s)
    throws JPRACompilerCheckerException
  {
    return new StatementCommandType<>(
      this.checkTypeExpression(s.getExpression()));
  }

  private TypeExprName<IdentifierType, TType> checkTypeExprName(
    final TypeExprName<IdentifierType, Untyped> e)
  {
    final IdentifierType id = e.getIdentifier();
    final TypeUserDefinedType type = this.context.getType(id);
    return new TypeExprName<>(id, (TType) type, e.getReference());
  }

  private TypeExprMatrix<IdentifierType, TType> checkTypeExprMatrix(
    final TypeExprMatrix<IdentifierType, Untyped> e)
    throws JPRACompilerCheckerException
  {
    final SizeExprType<IdentifierType, TType> e_width =
      this.checkSizeExpr(e.getWidth());
    final SizeExprType<IdentifierType, TType> e_height =
      this.checkSizeExpr(e.getHeight());
    final TypeExprType<IdentifierType, TType> te_type_pre =
      this.checkTypeExpression(e.getElementType());
    final Size<?> t_width = evaluateSize(e_width);
    final Size<?> t_height = evaluateSize(e_height);

    final BigInteger tw = t_width.getValue();
    final BigInteger th = t_height.getValue();
    if (!this.caps.isMatrixSizeElementsSupported(tw, th)) {
      throw JPRACompilerCheckerException.matrixNotSupported(
        e, tw, th, this.caps.getMatrixSizeElementsSupported());
    }

    final TType tt = te_type_pre.getType();
    if (!(tt instanceof TypeScalarType)) {
      throw JPRACompilerCheckerException.matrixNonScalarElement(e);
    }

    final TypeScalarType te_type = TypeScalarType.class.cast(tt);
    te_type.matchTypeScalar(new CheckMatrixScalarType());

    final TType t_type =
      new TMatrix(e.lexical(), t_width, t_height, te_type);
    return new TypeExprMatrix<>(
      t_type, e.lexical(), e_width, e_height, te_type_pre);
  }

  private TypeExprVector<IdentifierType, TType> checkTypeExprVector(
    final TypeExprVector<IdentifierType, Untyped> e)
    throws JPRACompilerCheckerException
  {
    final SizeExprType<IdentifierType, TType> e_count =
      this.checkSizeExpr(e.getElementCount());
    final TypeExprType<IdentifierType, TType> e_type =
      this.checkTypeExpression(e.getElementType());

    if (!(e_type.getType() instanceof TypeScalarType)) {
      throw JPRACompilerCheckerException.vectorNonScalarElement(e);
    }

    final TypeScalarType t_type = (TypeScalarType) e_type.getType();
    final Size<?> t_size = evaluateSize(e_count);
    if (!this.caps.isVectorSizeElementsSupported(t_size.getValue())) {
      throw JPRACompilerCheckerException.vectorSizeNotSupported(
        e, t_size.getValue(), this.caps.getVectorSizeSupported());
    }

    t_type.matchTypeScalar(new CheckVectorScalarType());

    final TType type = new TVector(e.lexical(), t_size, t_type);
    return new TypeExprVector<>(
      type, e.lexical(), e_count, e_type);
  }

  private TypeExprBooleanSet<IdentifierType, TType> checkTypeExprBooleanSet(
    final TypeExprBooleanSet<IdentifierType, Untyped> e)
    throws JPRACompilerCheckerException
  {
    final SizeExprType<IdentifierType, TType> size_expr =
      this.checkSizeExpr(e.getSizeExpression());
    final Size<SizeUnitOctetsType> size_octets = evaluateSize(
      size_expr);

    if (!BOOLEAN_SET_SIZES.includesValue(size_octets.getValue())) {
      throw JPRACompilerCheckerException.booleanSetSizeInvalid(
        e, BOOLEAN_SET_SIZES, size_octets.getValue());
    }

    final BigInteger required =
      BigInteger.valueOf((long) e.getFieldsInDeclarationOrder().size());
    final Size<SizeUnitBitsType> bits = Size.toBits(size_octets);
    if (bits.getValue().compareTo(required) < 0) {
      throw JPRACompilerCheckerException.booleanSetSizeLessThanRequired(
        e, required, bits.getValue());
    }

    final TBooleanSet type = new TBooleanSet(
      e.lexical(), e.getFieldsInDeclarationOrder(), size_octets);
    return new TypeExprBooleanSet<>(
      type,
      e.lexical(),
      e.getFieldsInDeclarationOrder(),
      size_expr);
  }

  private TypeExprArray<IdentifierType, TType> checkTypeExprArray(
    final TypeExprArray<IdentifierType, Untyped> e)
    throws JPRACompilerCheckerException
  {
    final TypeExprType<IdentifierType, TType> e_type =
      this.checkTypeExpression(e.getElementType());
    final SizeExprType<IdentifierType, TType> e_count =
      this.checkSizeExpr(e.getElementCount());

    final Size<?> ex_count = evaluateSize(e_count);
    final TType ex_type = e_type.getType();
    final TArray type =
      new TArray(e.lexical(), ex_count, ex_type);
    return new TypeExprArray<>(
      type, e.lexical(), e_count, e_type);
  }

  private TypeExprString<IdentifierType, TType> checkTypeExprString(
    final TypeExprString<IdentifierType, Untyped> e)
    throws JPRACompilerCheckerException
  {
    final SizeExprType<IdentifierType, TType> se =
      this.checkSizeExpr(e.getSize());
    final Size<SizeUnitOctetsType> size = evaluateSize(se);

    if (this.caps.isStringEncodingSupported(e.getEncoding())) {
      final TString type =
        new TString(e.lexical(), e.getEncoding(), size);
      return new TypeExprString<>(
        type, e.lexical(), se, e.getEncoding());
    }

    throw JPRACompilerCheckerException.stringEncodingNotSupported(
      e, e.getEncoding(), this.caps.getStringEncodingsSupported());
  }

  private TypeExprFloat<IdentifierType, TType> checkTypeExprFloat(
    final TypeExprFloat<IdentifierType, Untyped> e)
    throws JPRACompilerCheckerException
  {
    final SizeExprType<IdentifierType, TType> se =
      this.checkSizeExpr(e.getSize());
    final Size<SizeUnitBitsType> size = evaluateSize(se);

    if (this.caps.isRecordFloatSizeBitsSupported(size.getValue())) {
      final TType type = new TFloat(e.lexical(), size);
      return new TypeExprFloat<>(type, e.lexical(), se);
    }

    throw JPRACompilerCheckerException.floatSizeNotSupported(
      e, size, this.caps.getRecordFloatSizeBitsSupported());
  }

  private TypeExprIntegerUnsigned<IdentifierType, TType>
  checkTypeExprIntegerUnsigned(
    final TypeExprIntegerUnsigned<IdentifierType, Untyped> e)
    throws JPRACompilerCheckerException
  {
    final SizeExprType<IdentifierType, TType> se =
      this.checkSizeExpr(e.getSize());
    final Size<SizeUnitBitsType> size = evaluateSize(se);

    final TType type = new TIntegerUnsigned(e.lexical(), size);
    final TypeExprIntegerUnsigned<IdentifierType, TType> rv =
      new TypeExprIntegerUnsigned<>(type, e.lexical(), se);

    this.checkTypeExprIntegerSize(e, size);
    return rv;
  }

  private TypeExprIntegerSignedNormalized<IdentifierType, TType>
  checkTypeExprIntegerSignedNormalized(
    final TypeExprIntegerSignedNormalized<IdentifierType, Untyped> e)
    throws JPRACompilerCheckerException
  {
    final SizeExprType<IdentifierType, TType> se =
      this.checkSizeExpr(e.getSize());
    final Size<SizeUnitBitsType> size = evaluateSize(se);

    final TType type =
      new TIntegerSignedNormalized(e.lexical(), size);
    final TypeExprIntegerSignedNormalized<IdentifierType, TType> rv =
      new TypeExprIntegerSignedNormalized<>(
        type, e.lexical(), se);

    this.checkTypeExprIntegerSize(e, size);
    return rv;
  }

  private TypeExprIntegerUnsignedNormalized<IdentifierType, TType>
  checkTypeExprIntegerUnsignedNormalized(
    final TypeExprIntegerUnsignedNormalized<IdentifierType, Untyped> e)
    throws JPRACompilerCheckerException
  {
    final SizeExprType<IdentifierType, TType> se =
      this.checkSizeExpr(e.getSize());
    final Size<SizeUnitBitsType> size = evaluateSize(se);

    final TType type =
      new TIntegerUnsignedNormalized(e.lexical(), size);
    final TypeExprIntegerUnsignedNormalized<IdentifierType, TType> rv =
      new TypeExprIntegerUnsignedNormalized<>(
        type, e.lexical(), se);

    this.checkTypeExprIntegerSize(e, size);
    return rv;
  }

  private void checkTypeExprIntegerSize(
    final TypeExprType<IdentifierType, Untyped> e,
    final Size<SizeUnitBitsType> size)
    throws JPRACompilerCheckerException
  {
    switch (this.type_context) {
      case NONE: {
        break;
      }
      case PACKED: {
        if (!this.caps.isPackedIntegerSizeBitsSupported(size.getValue())) {
          throw JPRACompilerCheckerException.integerSizeNotSupported(
            e, size, this.caps.getPackedIntegerSizeBitsSupported());
        }
        break;
      }
      case RECORD: {
        if (!this.caps.isRecordIntegerSizeBitsSupported(size.getValue())) {
          throw JPRACompilerCheckerException.integerSizeNotSupported(
            e, size, this.caps.getRecordIntegerSizeBitsSupported());
        }
        break;
      }
    }
  }

  private TypeExprIntegerSigned<IdentifierType, TType>
  checkTypeExprIntegerSigned(
    final TypeExprIntegerSigned<IdentifierType, Untyped> e)
    throws JPRACompilerCheckerException
  {
    final SizeExprType<IdentifierType, TType> se =
      this.checkSizeExpr(e.getSize());
    final Size<SizeUnitBitsType> size = evaluateSize(se);

    final TType type = new TIntegerSigned(e.lexical(), size);
    final TypeExprIntegerSigned<IdentifierType, TType> rv =
      new TypeExprIntegerSigned<>(
        type, e.lexical(), se);

    this.checkTypeExprIntegerSize(e, size);
    return rv;
  }

  private SizeExprType<IdentifierType, TType> checkSizeExpr(
    final SizeExprType<IdentifierType, Untyped> e)
    throws JPRACompilerCheckerException
  {
    return e.matchSizeExpression(new CheckSizeExpression());
  }

  private SizeExprType<IdentifierType, TType> checkSizeExprInBits(
    final SizeExprInBits<IdentifierType, Untyped> s)
    throws JPRACompilerCheckerException
  {
    return new SizeExprInBits<>(this.checkTypeExpression(s.getTypeExpression()));
  }

  private SizeExprType<IdentifierType, TType> checkSizeExprInOctets(
    final SizeExprInOctets<IdentifierType, Untyped> s)
    throws JPRACompilerCheckerException
  {
    return new SizeExprInOctets<>(this.checkTypeExpression(s.getTypeExpression()));
  }

  private enum TypeExpressionContext
  {
    NONE,
    RECORD,
    PACKED
  }

  private static final class PackageContext implements PackageContextType
  {
    private final GlobalContextType context;
    private final java.util.HashMap<TypeName, TypeUserDefinedType> types;
    private final PackageNameQualified name;
    private final Map<TypeName, TypeUserDefinedType> types_view;

    PackageContext(
      final GlobalContextType c,
      final PackageNameQualified in_name)
    {
      this.context = Objects.requireNonNull(c, "Context");
      this.types = new java.util.HashMap<>();
      this.types_view = Collections.unmodifiableMap(this.types);
      this.name = Objects.requireNonNull(in_name, "Name");
    }

    void putType(
      final TypeUserDefinedType t)
    {
      Preconditions.checkPreconditionV(
        !this.types.containsKey(t.getName()),
        "Types must not contain %s",
        t.getName());
      this.types.put(t.getName(), t);
    }

    @Override
    public GlobalContextType getGlobalContext()
    {
      return this.context;
    }

    @Override
    public Map<TypeName, TypeUserDefinedType> getTypes()
    {
      return this.types_view;
    }

    @Override
    public PackageNameQualified getName()
    {
      return this.name;
    }

    @Override
    public LexicalPosition<URI> lexical()
    {
      return this.name.lexical();
    }
  }

  private static final class CheckEvaluateSizeExpression<T extends SizeUnitType>
    implements SizeExprMatcherType<IdentifierType, TType, Size<T>, UnreachableCodeException>
  {
    CheckEvaluateSizeExpression()
    {

    }

    @Override
    public Size<T> matchConstant(
      final SizeExprConstant<IdentifierType, TType> s)
    {
      return new Size<>(s.getValue());
    }

    @Override
    public Size<T> matchInOctets(
      final SizeExprInOctets<IdentifierType, TType> s)
    {
      // TODO: Generated method stub!
      throw new UnimplementedCodeException();
    }

    @Override
    public Size<T> matchInBits(
      final SizeExprInBits<IdentifierType, TType> s)
    {
      // TODO: Generated method stub!
      throw new UnimplementedCodeException();
    }
  }

  private final class CheckTypeExpression
    implements TypeExprMatcherType<
    IdentifierType, Untyped, TypeExprType<IdentifierType, TType>, JPRACompilerCheckerException>
  {
    CheckTypeExpression()
    {

    }

    @Override
    public TypeExprType<IdentifierType, TType> matchExprIntegerSigned(
      final TypeExprIntegerSigned<IdentifierType, Untyped> e)
      throws JPRACompilerCheckerException
    {
      return JPRAChecker.this.checkTypeExprIntegerSigned(e);
    }

    @Override
    public TypeExprType<IdentifierType, TType>
    matchExprIntegerSignedNormalized(
      final TypeExprIntegerSignedNormalized<IdentifierType, Untyped> e)
      throws JPRACompilerCheckerException
    {
      return JPRAChecker.this.checkTypeExprIntegerSignedNormalized(e);
    }

    @Override
    public TypeExprType<IdentifierType, TType> matchExprIntegerUnsigned(
      final TypeExprIntegerUnsigned<IdentifierType, Untyped> e)
      throws JPRACompilerCheckerException
    {
      return JPRAChecker.this.checkTypeExprIntegerUnsigned(e);
    }

    @Override
    public TypeExprType<IdentifierType, TType>
    matchExprIntegerUnsignedNormalized(
      final TypeExprIntegerUnsignedNormalized<IdentifierType, Untyped> e)
      throws JPRACompilerCheckerException
    {
      return JPRAChecker.this.checkTypeExprIntegerUnsignedNormalized(e);
    }

    @Override
    public TypeExprType<IdentifierType, TType> matchExprArray(
      final TypeExprArray<IdentifierType, Untyped> e)
      throws JPRACompilerCheckerException
    {
      return JPRAChecker.this.checkTypeExprArray(e);
    }

    @Override
    public TypeExprType<IdentifierType, TType> matchExprFloat(
      final TypeExprFloat<IdentifierType, Untyped> e)
      throws JPRACompilerCheckerException
    {
      return JPRAChecker.this.checkTypeExprFloat(e);
    }

    @Override
    public TypeExprType<IdentifierType, TType> matchExprVector(
      final TypeExprVector<IdentifierType, Untyped> e)
      throws JPRACompilerCheckerException
    {
      return JPRAChecker.this.checkTypeExprVector(e);
    }

    @Override
    public TypeExprType<IdentifierType, TType> matchExprMatrix(
      final TypeExprMatrix<IdentifierType, Untyped> e)
      throws JPRACompilerCheckerException
    {
      return JPRAChecker.this.checkTypeExprMatrix(e);
    }

    @Override
    public TypeExprType<IdentifierType, TType> matchExprString(
      final TypeExprString<IdentifierType, Untyped> e)
      throws JPRACompilerCheckerException
    {
      return JPRAChecker.this.checkTypeExprString(e);
    }

    @Override
    public TypeExprType<IdentifierType, TType> matchName(
      final TypeExprName<IdentifierType, Untyped> e)
    {
      return JPRAChecker.this.checkTypeExprName(e);
    }

    @Override
    public TypeExprType<IdentifierType, TType> matchTypeOfField(
      final TypeExprTypeOfField<IdentifierType, Untyped> e)
    {
      // TODO: Generated method stub!
      throw new UnimplementedCodeException();
    }

    @Override
    public TypeExprType<IdentifierType, TType> matchBooleanSet(
      final TypeExprBooleanSet<IdentifierType, Untyped> e)
      throws JPRACompilerCheckerException
    {
      return JPRAChecker.this.checkTypeExprBooleanSet(e);
    }
  }

  private final class CheckMatrixScalarType
    implements TypeScalarMatcherType<Void, JPRACompilerCheckerException>
  {
    CheckMatrixScalarType()
    {

    }

    @Override
    public Void matchScalarInteger(
      final TIntegerType t)
      throws JPRACompilerCheckerException
    {
      final BigInteger size = t.getSizeInBits().getValue();
      if (!JPRAChecker.this.caps.isMatrixIntegerSizeSupported(size)) {
        throw JPRACompilerCheckerException.matrixIntegerSizeNotSupported(
          t, size, JPRAChecker.this.caps.getMatrixIntegerSizeSupported());
      }
      return null;
    }

    @Override
    public Void matchScalarFloat(
      final TFloat t)
      throws JPRACompilerCheckerException
    {
      final BigInteger size = t.getSizeInBits().getValue();
      if (!JPRAChecker.this.caps.isMatrixFloatSizeSupported(size)) {
        throw JPRACompilerCheckerException.matrixFloatSizeNotSupported(
          t, size, JPRAChecker.this.caps.getMatrixFloatSizeSupported());
      }
      return null;
    }
  }

  private final class CheckVectorScalarType
    implements TypeScalarMatcherType<Void, JPRACompilerCheckerException>
  {
    CheckVectorScalarType()
    {

    }

    @Override
    public Void matchScalarInteger(
      final TIntegerType t)
      throws JPRACompilerCheckerException
    {
      final BigInteger size = t.getSizeInBits().getValue();
      if (!JPRAChecker.this.caps.isVectorIntegerSizeSupported(size)) {
        throw JPRACompilerCheckerException.vectorIntegerSizeNotSupported(
          t, size, JPRAChecker.this.caps.getVectorIntegerSizeSupported());
      }
      return null;
    }

    @Override
    public Void matchScalarFloat(
      final TFloat t)
      throws JPRACompilerCheckerException
    {
      final BigInteger size = t.getSizeInBits().getValue();
      if (!JPRAChecker.this.caps.isVectorFloatSizeSupported(size)) {
        throw JPRACompilerCheckerException.vectorFloatSizeNotSupported(
          t, size, JPRAChecker.this.caps.getVectorFloatSizeSupported());
      }
      return null;
    }
  }

  private final class CheckSizeExpression
    implements SizeExprMatcherType<IdentifierType, Untyped,
    SizeExprType<IdentifierType, TType>, JPRACompilerCheckerException>
  {
    CheckSizeExpression()
    {

    }

    @Override
    public SizeExprType<IdentifierType, TType> matchConstant(
      final SizeExprConstant<IdentifierType, Untyped> s)
    {
      return checkSizeExprConstant(s);
    }

    @Override
    public SizeExprType<IdentifierType, TType> matchInOctets(
      final SizeExprInOctets<IdentifierType, Untyped> s)
      throws JPRACompilerCheckerException
    {
      return JPRAChecker.this.checkSizeExprInOctets(s);
    }

    @Override
    public SizeExprType<IdentifierType, TType> matchInBits(
      final SizeExprInBits<IdentifierType, Untyped> s)
      throws JPRACompilerCheckerException
    {
      return JPRAChecker.this.checkSizeExprInBits(s);
    }
  }
}
