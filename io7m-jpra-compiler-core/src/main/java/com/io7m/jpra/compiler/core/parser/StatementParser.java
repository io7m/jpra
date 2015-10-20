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

package com.io7m.jpra.compiler.core.parser;

import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.compiler.core.LexicalContextType;
import com.io7m.jpra.model.FieldName;
import com.io7m.jpra.model.ModelElementType;
import com.io7m.jpra.model.PackageNameQualified;
import com.io7m.jpra.model.PackageNameUnqualified;
import com.io7m.jpra.model.RecordFieldDeclPaddingOctets;
import com.io7m.jpra.model.RecordFieldDeclType;
import com.io7m.jpra.model.RecordFieldDeclValue;
import com.io7m.jpra.model.SizeConstant;
import com.io7m.jpra.model.SizeExprType;
import com.io7m.jpra.model.SizeInBits;
import com.io7m.jpra.model.SizeInOctets;
import com.io7m.jpra.model.SizeUnitOctetsType;
import com.io7m.jpra.model.SizeUnitType;
import com.io7m.jpra.model.TypeDeclRecord;
import com.io7m.jpra.model.TypeExprArray;
import com.io7m.jpra.model.TypeExprBooleanSet;
import com.io7m.jpra.model.TypeExprFloat;
import com.io7m.jpra.model.TypeExprIntegerSigned;
import com.io7m.jpra.model.TypeExprIntegerSignedNormalized;
import com.io7m.jpra.model.TypeExprIntegerType;
import com.io7m.jpra.model.TypeExprIntegerUnsigned;
import com.io7m.jpra.model.TypeExprIntegerUnsignedNormalized;
import com.io7m.jpra.model.TypeExprMatrix;
import com.io7m.jpra.model.TypeExprReference;
import com.io7m.jpra.model.TypeExprScalarType;
import com.io7m.jpra.model.TypeExprString;
import com.io7m.jpra.model.TypeExprType;
import com.io7m.jpra.model.TypeExprVector;
import com.io7m.jpra.model.TypeName;
import com.io7m.jsx.ListType;
import com.io7m.jsx.QuotedStringType;
import com.io7m.jsx.SExpressionMatcherType;
import com.io7m.jsx.SExpressionType;
import com.io7m.jsx.SymbolType;
import com.io7m.jsx.serializer.SerializerTrivial;
import com.io7m.jsx.serializer.SerializerType;
import com.io7m.junreachable.UnreachableCodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valid4j.Assertive;
import org.valid4j.exceptions.RequireViolation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * The default implementation of the {@link StatementParserType} interface.
 */

public final class StatementParser implements StatementParserType
{
  private static final Logger LOG;

  private static final Set<String> KEYWORDS;
  private static final Set<String> TYPES;
  private static final Set<String> INTEGER_TYPES;
  private static final Set<String> SIZE_FUNCTIONS;
  private static final Set<String> RECORD_FIELD_KEYWORDS;

  private static final String PACKAGE_BEGIN = "package-begin";
  private static final String PACKAGE_END   = "package-end";
  private static final String IMPORT        = "import";
  private static final String RECORD        = "record";
  private static final String COMMAND_TYPE  = ":type";
  private static final String COMMAND_SIZE  = ":size";

  private static final String INTEGER                     = "integer";
  private static final String INTEGER_SIGNED              = "signed";
  private static final String INTEGER_UNSIGNED            = "unsigned";
  private static final String INTEGER_SIGNED_NORMALIZED   = "signed-normalized";
  private static final String INTEGER_UNSIGNED_NORMALIZED =
    "unsigned-normalized";

  private static final String SIZE_IN_OCTETS = "size-in-octets";
  private static final String SIZE_IN_BITS   = "size-in-bits";

  private static final String FLOAT       = "float";
  private static final String VECTOR      = "vector";
  private static final String MATRIX      = "matrix";
  private static final String ARRAY       = "array";
  private static final String STRING      = "string";
  private static final String BOOLEAN_SET = "boolean-set";

  private static final String FIELD          = "field";
  private static final String PADDING_OCTETS = "padding-octets";

  static {
    KEYWORDS = new HashSet<>(16);
    StatementParser.KEYWORDS.add(StatementParser.PACKAGE_BEGIN);
    StatementParser.KEYWORDS.add(StatementParser.PACKAGE_END);
    StatementParser.KEYWORDS.add(StatementParser.IMPORT);
    StatementParser.KEYWORDS.add(StatementParser.RECORD);
    StatementParser.KEYWORDS.add(StatementParser.COMMAND_TYPE);
    StatementParser.KEYWORDS.add(StatementParser.COMMAND_SIZE);

    RECORD_FIELD_KEYWORDS = new HashSet<>(16);
    StatementParser.RECORD_FIELD_KEYWORDS.add(StatementParser.FIELD);
    StatementParser.RECORD_FIELD_KEYWORDS.add(StatementParser.PADDING_OCTETS);

    TYPES = new HashSet<>(16);
    StatementParser.TYPES.add(StatementParser.INTEGER);
    StatementParser.TYPES.add(StatementParser.FLOAT);
    StatementParser.TYPES.add(StatementParser.ARRAY);
    StatementParser.TYPES.add(StatementParser.VECTOR);
    StatementParser.TYPES.add(StatementParser.MATRIX);
    StatementParser.TYPES.add(StatementParser.STRING);
    StatementParser.TYPES.add(StatementParser.BOOLEAN_SET);

    INTEGER_TYPES = new HashSet<>(16);
    StatementParser.INTEGER_TYPES.add(StatementParser.INTEGER_SIGNED);
    StatementParser.INTEGER_TYPES.add(StatementParser.INTEGER_UNSIGNED);
    StatementParser.INTEGER_TYPES.add(
      StatementParser.INTEGER_SIGNED_NORMALIZED);
    StatementParser.INTEGER_TYPES.add(
      StatementParser.INTEGER_UNSIGNED_NORMALIZED);

    SIZE_FUNCTIONS = new HashSet<>(16);
    StatementParser.SIZE_FUNCTIONS.add(StatementParser.SIZE_IN_OCTETS);
    StatementParser.SIZE_FUNCTIONS.add(StatementParser.SIZE_IN_BITS);

    LOG = LoggerFactory.getLogger(StatementParser.class);
  }

  private final LexicalContext context;
  private final SerializerType serial;

  private StatementParser()
  {
    this.context = new LexicalContext();
    this.serial = SerializerTrivial.newSerializer();
  }

  private static SymbolType requireSymbol(final SExpressionType e)
    throws CompilerParseException
  {
    return e.matchExpression(
      new SExpressionMatcherType<SymbolType, CompilerParseException>()
      {
        @Override public SymbolType list(final ListType le)
          throws CompilerParseException
        {
          throw CompilerParseException.expectedSymbolGotList(le);
        }

        @Override public SymbolType quotedString(final QuotedStringType qe)
          throws CompilerParseException
        {
          throw CompilerParseException.expectedSymbolGotQuotedString(qe);
        }

        @Override public SymbolType symbol(final SymbolType se)
          throws CompilerParseException
        {
          return se;
        }
      });
  }

  private static ListType requireList(final SExpressionType e)
    throws CompilerParseException
  {
    return e.matchExpression(
      new SExpressionMatcherType<ListType, CompilerParseException>()
      {
        @Override public ListType list(final ListType le)
          throws CompilerParseException
        {
          return le;
        }

        @Override public ListType quotedString(final QuotedStringType qe)
          throws CompilerParseException
        {
          throw CompilerParseException.expectedListGotQuotedString(qe);
        }

        @Override public ListType symbol(final SymbolType se)
          throws CompilerParseException
        {
          throw CompilerParseException.expectedListGotSymbol(se);
        }
      });
  }

  private static void checkKeyword(final SymbolType se)
    throws CompilerParseException
  {
    if (!StatementParser.KEYWORDS.contains(se.getText())) {
      final StringBuilder sb = new StringBuilder(256);
      sb.append("Unrecognized keyword '");
      sb.append(se.getText());
      sb.append("'");
      sb.append(System.lineSeparator());
      sb.append("Expected one of: ");
      sb.append(StatementParser.KEYWORDS);
      sb.append(System.lineSeparator());
      throw CompilerParseException.unrecognizedKeyword(se, sb.toString());
    }
  }

  private static void checkType(final SymbolType se)
    throws CompilerParseException
  {
    if (!StatementParser.TYPES.contains(se.getText())) {
      final StringBuilder sb = new StringBuilder(256);
      sb.append("Unrecognized type keyword '");
      sb.append(se.getText());
      sb.append("'");
      sb.append(System.lineSeparator());
      sb.append("Expected one of: ");
      sb.append(StatementParser.TYPES);
      sb.append(System.lineSeparator());
      throw CompilerParseException.unrecognizedTypeKeyword(se, sb.toString());
    }
  }

  private static void onPackageBegin(
    final LexicalContext context,
    final SerializerType serial,
    final StatementParserEventListenerType listener,
    final ListType le,
    final SymbolType se)
    throws CompilerParseException
  {
    Assertive.require(StatementParser.PACKAGE_BEGIN.equals(se.getText()));

    if (le.size() == 2) {
      final SExpressionType e_name = le.get(1);
      if (e_name instanceof SymbolType) {
        final SymbolType name = (SymbolType) e_name;
        final PackageNameQualified p_name =
          StatementParser.onPackageNameQualified(name);
        context.put(p_name, se);
        listener.onPackageBegin(context, p_name);
        return;
      }
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      serial.serialize(le, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("Expected: (package-begin <package-name-qualified>)");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw CompilerParseException.syntaxError(le, sb.toString());
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  private static void onPackageEnd(
    final LexicalContext context,
    final SerializerType serial,
    final StatementParserEventListenerType listener,
    final ListType le,
    final SymbolType se)
    throws CompilerParseException
  {
    Assertive.require(StatementParser.PACKAGE_END.equals(se.getText()));

    if (le.size() == 1) {
      listener.onPackageEnd(context);
      return;
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      serial.serialize(le, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("Expected: (package-end)");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw CompilerParseException.syntaxError(le, sb.toString());
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  private static PackageNameQualified onPackageNameQualified(
    final SymbolType name)
    throws CompilerParseException
  {
    final String text = name.getText();
    Assertive.require(!text.isEmpty());
    final String[] segments = text.split("\\.");

    final MutableList<PackageNameUnqualified> names_base = new FastList<>();
    for (int index = 0; index < segments.length; ++index) {
      final String raw = segments[index];
      try {
        names_base.add(new PackageNameUnqualified(raw));
      } catch (final RequireViolation e) {
        throw CompilerParseException.badPackageName(name, e.getMessage());
      }
    }

    final ImmutableList<PackageNameUnqualified> names =
      names_base.toImmutable();
    return new PackageNameQualified(names);
  }

  private static void onImport(
    final LexicalContext context,
    final SerializerType serial,
    final StatementParserEventListenerType listener,
    final ListType le,
    final SymbolType se)
    throws CompilerParseException
  {
    Assertive.require(StatementParser.IMPORT.equals(se.getText()));

    if (le.size() == 4) {
      final SExpressionType q_name = le.get(1);
      final SExpressionType as = le.get(2);
      final SExpressionType u_name = le.get(3);

      if (q_name instanceof SymbolType
          && u_name instanceof SymbolType
          && as instanceof SymbolType
          && "as".equals(((SymbolType) as).getText())) {
        final SymbolType q_sym = (SymbolType) q_name;
        final SymbolType u_sym = (SymbolType) u_name;

        final PackageNameQualified p_name =
          StatementParser.onPackageNameQualified(q_sym);
        final PackageNameUnqualified up_name =
          StatementParser.onPackageNameUnqualified(u_sym);

        context.put(p_name, q_sym);
        context.put(up_name, u_sym);

        listener.onImport(context, p_name, up_name);
        return;
      }
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      serial.serialize(le, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append(
        "Expected: (import <package-name-qualified> as "
        + "<package-name-unqualified)");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw CompilerParseException.syntaxError(le, sb.toString());
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  private static PackageNameUnqualified onPackageNameUnqualified(
    final SymbolType s)
    throws CompilerParseException
  {
    try {
      return new PackageNameUnqualified(s.getText());
    } catch (final RequireViolation e) {
      throw CompilerParseException.badPackageName(s, e.getMessage());
    }
  }

  private static <T extends SizeUnitType> SizeExprType<T> onSizeExpression(
    final LexicalContext context,
    final SerializerType serial,
    final SExpressionType e)
    throws CompilerParseException
  {
    return e.matchExpression(
      new SExpressionMatcherType<SizeExprType<T>, CompilerParseException>()
      {
        @Override public SizeExprType<T> list(final ListType le)
          throws CompilerParseException
        {
          if (le.isEmpty()) {
            throw CompilerParseException.expectedNonEmptyList(le);
          }

          final SymbolType se = StatementParser.requireSymbol(le.get(0));
          StatementParser.checkSizeFunction(se);

          switch (se.getText()) {
            case StatementParser.SIZE_IN_BITS: {
              return StatementParser.onSizeInBits(context, serial, le, se);
            }
            case StatementParser.SIZE_IN_OCTETS: {
              return StatementParser.onSizeInOctets(context, serial, le, se);
            }
          }

          throw new UnreachableCodeException();
        }

        @Override public SizeExprType<T> quotedString(
          final QuotedStringType qe)
          throws CompilerParseException
        {
          throw CompilerParseException.expectedSymbolOrListGotQuotedString(qe);
        }

        @Override public SizeExprType<T> symbol(final SymbolType se)
          throws CompilerParseException
        {
          try {
            final SizeConstant<T> v =
              new SizeConstant<>(new BigInteger(se.getText()));
            context.put(v, se);
            return v;
          } catch (final NumberFormatException x) {
            throw CompilerParseException.invalidIntegerConstant(se);
          }
        }
      });
  }

  private static <T extends SizeUnitType> SizeExprType<T> onSizeInBits(
    final LexicalContext context,
    final SerializerType serial,
    final ListType le,
    final SymbolType se)
    throws CompilerParseException
  {
    Assertive.require(StatementParser.SIZE_IN_BITS.equals(se.getText()));

    if (le.size() == 2) {
      final SizeInBits v = new SizeInBits(
        StatementParser.onTypeExpression(context, serial, le.get(1)));
      context.put(v, se);
      return (SizeExprType<T>) v;
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      serial.serialize(le, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("Expected: (size-in-bits <type-expression>)");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw CompilerParseException.syntaxError(le, sb.toString());
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  private static <T extends SizeUnitType> SizeExprType<T> onSizeInOctets(
    final LexicalContext context,
    final SerializerType serial,
    final ListType le,
    final SymbolType se)
    throws CompilerParseException
  {
    Assertive.require(StatementParser.SIZE_IN_OCTETS.equals(se.getText()));

    if (le.size() == 2) {
      final SizeInOctets v = new SizeInOctets(
        StatementParser.onTypeExpression(context, serial, le.get(1)));
      context.put(v, se);
      return (SizeExprType<T>) v;
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      serial.serialize(le, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("Expected: (size-in-octets <type-expression>)");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw CompilerParseException.syntaxError(le, sb.toString());
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  private static void checkSizeFunction(final SymbolType se)
    throws CompilerParseException
  {
    if (!StatementParser.SIZE_FUNCTIONS.contains(se.getText())) {
      final StringBuilder sb = new StringBuilder(256);
      sb.append("Unrecognized size function '");
      sb.append(se.getText());
      sb.append("'");
      sb.append(System.lineSeparator());
      sb.append("Expected one of: ");
      sb.append(StatementParser.SIZE_FUNCTIONS);
      sb.append(System.lineSeparator());
      throw CompilerParseException.unrecognizedSizeFunction(se, sb.toString());
    }
  }

  private static TypeExprIntegerType onTypeInteger(
    final LexicalContext context,
    final SerializerType serial,
    final ListType le,
    final SymbolType se)
    throws CompilerParseException
  {
    Assertive.require(StatementParser.INTEGER.equals(se.getText()));

    if (le.size() == 3) {
      final SExpressionType t_expr = le.get(1);
      final SExpressionType s_expr = le.get(2);
      if (t_expr instanceof SymbolType) {
        final SymbolType t_name = (SymbolType) t_expr;

        StatementParser.checkIntegerTypeKeyword(t_name);

        switch (t_name.getText()) {
          case StatementParser.INTEGER_SIGNED: {
            final TypeExprIntegerSigned r = new TypeExprIntegerSigned(
              StatementParser.onSizeExpression(context, serial, s_expr));
            context.put(r, t_name);
            return r;
          }
          case StatementParser.INTEGER_UNSIGNED: {
            final TypeExprIntegerUnsigned r = new TypeExprIntegerUnsigned(
              StatementParser.onSizeExpression(context, serial, s_expr));
            context.put(r, t_name);
            return r;
          }
          case StatementParser.INTEGER_SIGNED_NORMALIZED: {
            final TypeExprIntegerSignedNormalized r =
              new TypeExprIntegerSignedNormalized(
                StatementParser.onSizeExpression(context, serial, s_expr));
            context.put(r, t_name);
            return r;
          }
          case StatementParser.INTEGER_UNSIGNED_NORMALIZED: {
            final TypeExprIntegerUnsignedNormalized r =
              new TypeExprIntegerUnsignedNormalized(
                StatementParser.onSizeExpression(context, serial, s_expr));
            context.put(r, t_name);
            return r;
          }
        }

        throw new UnreachableCodeException();
      }
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      serial.serialize(le, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("Expected: (integer <integer-type> <size-in-bits>)");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw CompilerParseException.syntaxError(le, sb.toString());
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  private static void checkIntegerTypeKeyword(final SymbolType se)
    throws CompilerParseException
  {
    if (!StatementParser.INTEGER_TYPES.contains(se.getText())) {
      final StringBuilder sb = new StringBuilder(256);
      sb.append("Unrecognized integer type keyword '");
      sb.append(se.getText());
      sb.append("'");
      sb.append(System.lineSeparator());
      sb.append("Expected one of: ");
      sb.append(StatementParser.INTEGER_TYPES);
      sb.append(System.lineSeparator());
      throw CompilerParseException.unrecognizedIntegerTypeKeyword(
        se, sb.toString());
    }
  }

  private static TypeExprType onTypeExpression(
    final LexicalContext context,
    final SerializerType serial,
    final SExpressionType e)
    throws CompilerParseException
  {
    NullCheck.notNull(e);

    return e.matchExpression(
      new SExpressionMatcherType<TypeExprType, CompilerParseException>()
      {
        @Override public TypeExprType list(final ListType le)
          throws CompilerParseException
        {
          if (le.isEmpty()) {
            throw CompilerParseException.expectedNonEmptyList(le);
          }

          final SymbolType se = StatementParser.requireSymbol(le.get(0));
          StatementParser.checkType(se);

          switch (se.getText()) {
            case StatementParser.INTEGER: {
              return StatementParser.onTypeInteger(context, serial, le, se);
            }
            case StatementParser.FLOAT: {
              return StatementParser.onTypeFloat(context, serial, le, se);
            }
            case StatementParser.VECTOR: {
              return StatementParser.onTypeVector(context, serial, le, se);
            }
            case StatementParser.MATRIX: {
              return StatementParser.onTypeMatrix(context, serial, le, se);
            }
            case StatementParser.ARRAY: {
              return StatementParser.onTypeArray(context, serial, le, se);
            }
            case StatementParser.STRING: {
              return StatementParser.onTypeString(context, serial, le, se);
            }
            case StatementParser.BOOLEAN_SET: {
              return StatementParser.onTypeBooleanSet(context, serial, le, se);
            }
          }

          throw new UnreachableCodeException();
        }

        @Override public TypeExprType quotedString(final QuotedStringType qe)
          throws CompilerParseException
        {
          throw CompilerParseException.expectedSymbolOrListGotQuotedString(qe);
        }

        @Override public TypeExprType symbol(final SymbolType se)
          throws CompilerParseException
        {
          return StatementParser.onTypeReference(context, serial, se);
        }
      });
  }

  private static TypeExprReference onTypeReference(
    final LexicalContext context,
    final SerializerType serial,
    final SymbolType se)
    throws CompilerParseException
  {
    final String[] segments = se.getText().split("\\.");
    switch (segments.length) {
      case 1: {
        final TypeExprReference v = new TypeExprReference(
          Optional.empty(), StatementParser.onTypeName(se));
        context.put(v, se);
        return v;
      }
      case 2: {
        final TypeName t_name;
        final PackageNameUnqualified p_name;

        try {
          t_name = new TypeName(segments[1]);
        } catch (final RequireViolation e) {
          throw CompilerParseException.badTypeName(se, e.getMessage());
        }

        try {
          p_name = new PackageNameUnqualified(segments[0]);
        } catch (final RequireViolation e) {
          throw CompilerParseException.badPackageName(se, e.getMessage());
        }

        final TypeExprReference v = new TypeExprReference(
          Optional.of(p_name), t_name);
        context.put(v, se);
        return v;
      }
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      serial.serialize(se, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Unparseable type reference.");
      sb.append(System.lineSeparator());
      sb.append(
        "Expected: <package-name-unqualified>.<type-name> | <type-name>");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw CompilerParseException.badTypeReference(se, sb.toString());
    } catch (final IOException x) {
      throw new UnreachableCodeException(x);
    }
  }

  private static TypeExprVector onTypeVector(
    final LexicalContext context,
    final SerializerType serial,
    final ListType le,
    final SymbolType se)
    throws CompilerParseException
  {
    Assertive.require(StatementParser.VECTOR.equals(se.getText()));

    if (le.size() == 3) {
      final SExpressionType t_expr = le.get(1);
      final SExpressionType s_expr = le.get(2);
      final SizeExprType<?> size =
        StatementParser.onSizeExpression(context, serial, s_expr);
      final TypeExprScalarType type =
        StatementParser.onTypeExpressionScalar(context, serial, t_expr);
      final TypeExprVector r = new TypeExprVector(size, type);
      context.put(r, se);
      return r;
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      serial.serialize(se, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append(
        "Expected: (vector <scalar-type-expression> <size-expression>)");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw CompilerParseException.syntaxError(se, sb.toString());
    } catch (final IOException x) {
      throw new UnreachableCodeException(x);
    }
  }

  private static TypeExprBooleanSet onTypeBooleanSet(
    final LexicalContext context,
    final SerializerType serial,
    final ListType le,
    final SymbolType se)
    throws CompilerParseException
  {
    Assertive.require(StatementParser.BOOLEAN_SET.equals(se.getText()));

    if (le.size() == 3) {
      final SExpressionType s_expr = le.get(1);
      final SExpressionType f_expr = le.get(2);
      final SizeExprType<SizeUnitOctetsType> size =
        StatementParser.onSizeExpression(context, serial, s_expr);

      final ImmutableList<FieldName> fields =
        StatementParser.onFieldSet(context, serial, f_expr);
      final TypeExprBooleanSet r = new TypeExprBooleanSet(fields, size);
      context.put(r, se);
      return r;
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      serial.serialize(se, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append(
        "Expected: (boolean-set <size-expression> (<field> ... <field>))");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw CompilerParseException.syntaxError(se, sb.toString());
    } catch (final IOException x) {
      throw new UnreachableCodeException(x);
    }
  }

  private static ImmutableList<FieldName> onFieldSet(
    final LexicalContext context,
    final SerializerType serial,
    final SExpressionType f_expr)
    throws CompilerParseException
  {
    return f_expr.matchExpression(
      new SExpressionMatcherType<ImmutableList<FieldName>,
        CompilerParseException>()
      {
        @Override public ImmutableList<FieldName> list(final ListType e)
          throws CompilerParseException
        {
          final Map<FieldName, Unit> names = new LinkedHashMap<>(e.size());

          for (int index = 0; index < e.size(); ++index) {
            final SExpressionType ei = e.get(index);
            final SymbolType si = StatementParser.requireSymbol(ei);

            final FieldName name;
            try {
              name = new FieldName(si.getText());
            } catch (final RequireViolation x) {
              throw CompilerParseException.badFieldName(si, x.getMessage());
            }

            if (!names.containsKey(name)) {
              names.put(name, Unit.unit());
            } else {
              final StringBuilder sb = new StringBuilder(128);
              sb.append("Duplicate field name.");
              sb.append(System.lineSeparator());
              sb.append("Name: ");
              sb.append(name);
              sb.append(System.lineSeparator());
              sb.append("Fields: ");
              sb.append(names.keySet());
              throw CompilerParseException.duplicateFieldName(
                si, sb.toString());
            }
          }

          final FastList<FieldName> rx = new FastList<>(names.size());
          rx.addAll(names.keySet());
          return rx.toImmutable();
        }

        @Override
        public ImmutableList<FieldName> quotedString(final QuotedStringType e)
          throws CompilerParseException
        {
          throw CompilerParseException.expectedListGotQuotedString(e);
        }

        @Override public ImmutableList<FieldName> symbol(final SymbolType e)
          throws CompilerParseException
        {
          throw CompilerParseException.expectedListGotSymbol(e);
        }
      });
  }

  private static TypeExprArray onTypeArray(
    final LexicalContext context,
    final SerializerType serial,
    final ListType le,
    final SymbolType se)
    throws CompilerParseException
  {
    Assertive.require(StatementParser.ARRAY.equals(se.getText()));

    if (le.size() == 3) {
      final SExpressionType t_expr = le.get(1);
      final SExpressionType s_expr = le.get(2);
      final SizeExprType<?> size =
        StatementParser.onSizeExpression(context, serial, s_expr);
      final TypeExprType type =
        StatementParser.onTypeExpression(context, serial, t_expr);
      final TypeExprArray r = new TypeExprArray(size, type);
      context.put(r, se);
      return r;
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      serial.serialize(se, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("Expected: (array <type-expression> <size-expression>)");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw CompilerParseException.syntaxError(se, sb.toString());
    } catch (final IOException x) {
      throw new UnreachableCodeException(x);
    }
  }

  private static TypeExprString onTypeString(
    final LexicalContext context,
    final SerializerType serial,
    final ListType le,
    final SymbolType se)
    throws CompilerParseException
  {
    Assertive.require(StatementParser.STRING.equals(se.getText()));

    if (le.size() == 3) {
      final SExpressionType s_expr = le.get(1);
      final SExpressionType e_expr = le.get(2);

      if (e_expr instanceof QuotedStringType) {
        final QuotedStringType qe = (QuotedStringType) e_expr;
        final SizeExprType<SizeUnitOctetsType> size =
          StatementParser.onSizeExpression(context, serial, s_expr);
        final TypeExprString r = new TypeExprString(qe.getText(), size);
        context.put(r, se);
        return r;
      }
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      serial.serialize(se, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("Expected: (string <size-expression> \"<encoding>\")");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw CompilerParseException.syntaxError(se, sb.toString());
    } catch (final IOException x) {
      throw new UnreachableCodeException(x);
    }
  }

  private static TypeExprMatrix onTypeMatrix(
    final LexicalContext context,
    final SerializerType serial,
    final ListType le,
    final SymbolType se)
    throws CompilerParseException
  {
    Assertive.require(StatementParser.MATRIX.equals(se.getText()));

    if (le.size() == 4) {
      final SExpressionType t_expr = le.get(1);
      final SExpressionType w_expr = le.get(2);
      final SExpressionType h_expr = le.get(3);

      final SizeExprType<?> width =
        StatementParser.onSizeExpression(context, serial, w_expr);
      final SizeExprType<?> height =
        StatementParser.onSizeExpression(context, serial, h_expr);

      final TypeExprScalarType type =
        StatementParser.onTypeExpressionScalar(context, serial, t_expr);
      final TypeExprMatrix r = new TypeExprMatrix(width, height, type);
      context.put(r, se);
      return r;
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      serial.serialize(se, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("Expected: (matrix <scalar-type-expression> <width> <height>)");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw CompilerParseException.syntaxError(se, sb.toString());
    } catch (final IOException x) {
      throw new UnreachableCodeException(x);
    }
  }

  private static TypeExprScalarType onTypeExpressionScalar(
    final LexicalContext context,
    final SerializerType serial,
    final SExpressionType e)
    throws CompilerParseException
  {
    final TypeExprType r = StatementParser.onTypeExpression(context, serial, e);
    if (r instanceof TypeExprScalarType) {
      return (TypeExprScalarType) r;
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      serial.serialize(e, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Expected a scalar type expression.");
      sb.append(System.lineSeparator());
      sb.append("Expected: <scalar-type-expression>");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw CompilerParseException.expectedScalarTypeExpression(
        e, sb.toString());
    } catch (final IOException x) {
      throw new UnreachableCodeException(x);
    }
  }

  private static TypeExprFloat onTypeFloat(
    final LexicalContext context,
    final SerializerType serial,
    final ListType le,
    final SymbolType se)
    throws CompilerParseException
  {
    Assertive.require(StatementParser.FLOAT.equals(se.getText()));

    if (le.size() == 2) {
      final SExpressionType s_expr = le.get(1);
      final TypeExprFloat r = new TypeExprFloat(
        StatementParser.onSizeExpression(
          context, serial, s_expr));
      context.put(r, se);
      return r;
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      serial.serialize(le, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("Expected: (float <size-in-bits>)");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw CompilerParseException.syntaxError(le, sb.toString());
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  private static void onRecord(
    final LexicalContext context,
    final SerializerType serial,
    final StatementParserEventListenerType listener,
    final ListType le,
    final SymbolType se)
    throws CompilerParseException
  {
    Assertive.require(StatementParser.RECORD.equals(se.getText()));

    if (le.size() == 3) {
      final SExpressionType n_expr = le.get(1);
      final SExpressionType f_expr = le.get(2);

      if (n_expr instanceof SymbolType && f_expr instanceof ListType) {
        final TypeName t_name = StatementParser.onTypeName((SymbolType) n_expr);
        final ListType fl_expr = (ListType) f_expr;

        final MutableMap<FieldName, RecordFieldDeclValue> fields_name =
          new UnifiedMap<>(fl_expr.size());
        final FastList<RecordFieldDeclType> fields_order = new FastList<>();

        StatementParser.onRecordFields(
          context, serial, fl_expr, fields_name, fields_order);

        final TypeDeclRecord r = new TypeDeclRecord(
          fields_name.toImmutable(), t_name, fields_order.toImmutable());

        context.put(r, n_expr);
        listener.onRecord(context, r);
        return;
      }
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      serial.serialize(le, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("Expected: (record <type-name> (<field> ... <field>))");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw CompilerParseException.syntaxError(le, sb.toString());
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  private static void onRecordFields(
    final LexicalContext context,
    final SerializerType serial,
    final ListType fields,
    final Map<FieldName, RecordFieldDeclValue> fields_name,
    final List<RecordFieldDeclType> fields_order)
    throws CompilerParseException
  {
    for (int index = 0; index < fields.size(); ++index) {
      final ListType l_expr = StatementParser.requireList(fields.get(index));
      if (l_expr.isEmpty()) {
        throw CompilerParseException.expectedNonEmptyList(l_expr);
      }

      final SymbolType k = StatementParser.requireSymbol(l_expr.get(0));
      StatementParser.checkRecordFieldKeyword(k);

      final int e_count = l_expr.size();
      switch (k.getText()) {
        case StatementParser.FIELD: {
          StatementParser.onRecordFieldValue(
            context, serial, fields_name, fields_order, l_expr, e_count);
          continue;
        }
        case StatementParser.PADDING_OCTETS: {
          StatementParser.onRecordPaddingOctets(
            context, serial, fields_order, l_expr, e_count);
          continue;
        }
      }

      throw new UnreachableCodeException();
    }
  }

  private static void onRecordPaddingOctets(
    final LexicalContext context,
    final SerializerType serial,
    final List<RecordFieldDeclType> fields_order,
    final ListType l_expr,
    final int e_count)
    throws CompilerParseException
  {
    if (e_count == 2) {
      final SizeExprType<SizeUnitOctetsType> size =
        StatementParser.onSizeExpression(context, serial, l_expr.get(1));
      final RecordFieldDeclPaddingOctets v =
        new RecordFieldDeclPaddingOctets(size);
      context.put(v, l_expr);
      fields_order.add(v);
    } else {
      try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
        serial.serialize(l_expr, bao);
        final StringBuilder sb = new StringBuilder(128);
        sb.append("Syntax error.");
        sb.append(System.lineSeparator());
        sb.append("Expected: (padding-octets <size-expression>)");
        sb.append(System.lineSeparator());
        sb.append("Got: ");
        sb.append(bao.toString(StandardCharsets.UTF_8.name()));
        throw CompilerParseException.syntaxError(l_expr, sb.toString());
      } catch (final IOException x) {
        throw new UnreachableCodeException(x);
      }
    }
  }

  private static void onRecordFieldValue(
    final LexicalContext context,
    final SerializerType serial,
    final Map<FieldName, RecordFieldDeclValue> fields_name,
    final List<RecordFieldDeclType> fields_order,
    final ListType l_expr,
    final int e_count)
    throws CompilerParseException
  {
    if (e_count == 3 && l_expr.get(1) instanceof SymbolType) {
      final SymbolType f_name = (SymbolType) l_expr.get(1);
      final FieldName name = StatementParser.onFieldName(f_name);
      if (!fields_name.containsKey(name)) {
        final RecordFieldDeclValue v = new RecordFieldDeclValue(
          name, StatementParser.onTypeExpression(
          context, serial, l_expr.get(2)));

        context.put(name, f_name);
        context.put(v, l_expr);
        fields_name.put(name, v);
        fields_order.add(v);
      } else {
        final StringBuilder sb = new StringBuilder(128);
        sb.append("Duplicate field name.");
        sb.append(System.lineSeparator());
        sb.append("Name: ");
        sb.append(name);
        sb.append(System.lineSeparator());
        sb.append("Fields: ");
        sb.append(fields_name.keySet());
        throw CompilerParseException.duplicateFieldName(
          f_name, sb.toString());
      }
    } else {
      try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
        serial.serialize(l_expr, bao);
        final StringBuilder sb = new StringBuilder(128);
        sb.append("Syntax error.");
        sb.append(System.lineSeparator());
        sb.append("Expected: (field <field-name> <type-expression>)");
        sb.append(System.lineSeparator());
        sb.append("Got: ");
        sb.append(bao.toString(StandardCharsets.UTF_8.name()));
        throw CompilerParseException.syntaxError(l_expr, sb.toString());
      } catch (final IOException x) {
        throw new UnreachableCodeException(x);
      }
    }
  }

  private static void checkRecordFieldKeyword(final SymbolType se)
    throws CompilerParseException
  {
    if (!StatementParser.RECORD_FIELD_KEYWORDS.contains(se.getText())) {
      final StringBuilder sb = new StringBuilder(256);
      sb.append("Unrecognized record field keyword '");
      sb.append(se.getText());
      sb.append("'");
      sb.append(System.lineSeparator());
      sb.append("Expected one of: ");
      sb.append(StatementParser.RECORD_FIELD_KEYWORDS);
      sb.append(System.lineSeparator());
      throw CompilerParseException.unrecognizedRecordFieldKeyword(
        se, sb.toString());
    }
  }

  private static TypeName onTypeName(final SymbolType name)
    throws CompilerParseException
  {
    try {
      return new TypeName(name.getText());
    } catch (final RequireViolation e) {
      throw CompilerParseException.badTypeName(name, e.getMessage());
    }
  }

  private static FieldName onFieldName(final SymbolType name)
    throws CompilerParseException
  {
    try {
      return new FieldName(name.getText());
    } catch (final RequireViolation e) {
      throw CompilerParseException.badFieldName(name, e.getMessage());
    }
  }

  /**
   * @return A new statement parser
   */

  public static StatementParserType newParser()
  {
    return new StatementParser();
  }

  private static void onCommandType(
    final LexicalContext context,
    final SerializerType serial,
    final StatementParserREPLEventListenerType listener,
    final ListType le,
    final SymbolType se)
    throws CompilerParseException
  {
    Assertive.require(StatementParser.COMMAND_TYPE.equals(se.getText()));

    if (le.size() == 2) {
      listener.onREPLType(
        context, StatementParser.onTypeExpression(context, serial, le.get(1)));
      return;
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      serial.serialize(le, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("Expected: (:type <type-expression>)");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw CompilerParseException.syntaxError(le, sb.toString());
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  private static void onCommandSize(
    final LexicalContext context,
    final SerializerType serial,
    final StatementParserREPLEventListenerType listener,
    final ListType le,
    final SymbolType se)
    throws CompilerParseException
  {
    Assertive.require(StatementParser.COMMAND_SIZE.equals(se.getText()));

    if (le.size() == 2) {
      listener.onREPLSize(
        context, StatementParser.onSizeExpression(context, serial, le.get(1)));
      return;
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      serial.serialize(le, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("Expected: (:size <size-expression>)");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw CompilerParseException.syntaxError(le, sb.toString());
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  @Override public void parseStatement(
    final SExpressionType e,
    final StatementParserREPLEventListenerType listener)
    throws CompilerParseException
  {
    NullCheck.notNull(e);
    NullCheck.notNull(listener);

    e.matchExpression(
      new SExpressionMatcherType<Unit, CompilerParseException>()
      {
        @Override public Unit list(final ListType le)
          throws CompilerParseException
        {
          if (le.isEmpty()) {
            throw CompilerParseException.expectedNonEmptyList(le);
          }

          final SymbolType se = StatementParser.requireSymbol(le.get(0));
          StatementParser.checkKeyword(se);

          switch (se.getText()) {
            case StatementParser.PACKAGE_BEGIN: {
              StatementParser.onPackageBegin(
                StatementParser.this.context,
                StatementParser.this.serial,
                listener,
                le,
                se);
              return Unit.unit();
            }
            case StatementParser.IMPORT: {
              StatementParser.onImport(
                StatementParser.this.context,
                StatementParser.this.serial,
                listener,
                le,
                se);
              return Unit.unit();
            }
            case StatementParser.PACKAGE_END: {
              StatementParser.onPackageEnd(
                StatementParser.this.context,
                StatementParser.this.serial,
                listener,
                le,
                se);
              return Unit.unit();
            }
            case StatementParser.RECORD: {
              StatementParser.onRecord(
                StatementParser.this.context,
                StatementParser.this.serial,
                listener,
                le,
                se);
              return Unit.unit();
            }
            case StatementParser.COMMAND_TYPE: {
              StatementParser.onCommandType(
                StatementParser.this.context,
                StatementParser.this.serial,
                listener,
                le,
                se);
              return Unit.unit();
            }
            case StatementParser.COMMAND_SIZE: {
              StatementParser.onCommandSize(
                StatementParser.this.context,
                StatementParser.this.serial,
                listener,
                le,
                se);
              return Unit.unit();
            }
          }

          throw new UnreachableCodeException();
        }

        @Override public Unit quotedString(final QuotedStringType qe)
          throws CompilerParseException
        {
          throw CompilerParseException.expectedListGotQuotedString(qe);
        }

        @Override public Unit symbol(final SymbolType se)
          throws CompilerParseException
        {
          throw CompilerParseException.expectedListGotSymbol(se);
        }
      });
  }

  private static final class LexicalContext implements LexicalContextType
  {
    private static final Logger LOG_LC;

    static {
      LOG_LC = LoggerFactory.getLogger(LexicalContext.class);
    }

    private final Map<ModelElementType, SExpressionType> elements;

    LexicalContext()
    {
      this.elements = new IdentityHashMap<>(256);
    }

    @Override public SExpressionType getExpressionFor(final ModelElementType e)
    {
      Assertive.require(this.elements.containsKey(e));
      return this.elements.get(e);
    }

    public void put(
      final ModelElementType e,
      final SExpressionType s)
    {
      LexicalContext.LOG_LC.debug("put {} → {}", e, s);
      Assertive.require(!this.elements.containsKey(e));
      this.elements.put(e, s);
    }
  }
}
