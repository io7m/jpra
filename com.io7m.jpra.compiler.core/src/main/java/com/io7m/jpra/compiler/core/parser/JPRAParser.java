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
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.factory.Maps;
import com.gs.collections.impl.list.mutable.FastList;
import com.io7m.jaffirm.core.PreconditionViolationException;
import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jlexing.core.LexicalPositionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.Unresolved;
import com.io7m.jpra.model.Untyped;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.names.PackageNameQualified;
import com.io7m.jpra.model.names.PackageNameUnqualified;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.jpra.model.size_expressions.SizeExprConstant;
import com.io7m.jpra.model.size_expressions.SizeExprInBits;
import com.io7m.jpra.model.size_expressions.SizeExprInOctets;
import com.io7m.jpra.model.size_expressions.SizeExprType;
import com.io7m.jpra.model.statements.StatementCommandSize;
import com.io7m.jpra.model.statements.StatementCommandType;
import com.io7m.jpra.model.statements.StatementPackageBegin;
import com.io7m.jpra.model.statements.StatementPackageEnd;
import com.io7m.jpra.model.statements.StatementPackageImport;
import com.io7m.jpra.model.statements.StatementType;
import com.io7m.jpra.model.type_declarations.PackedFieldDeclPaddingBits;
import com.io7m.jpra.model.type_declarations.PackedFieldDeclType;
import com.io7m.jpra.model.type_declarations.PackedFieldDeclValue;
import com.io7m.jpra.model.type_declarations.RecordFieldDeclPaddingOctets;
import com.io7m.jpra.model.type_declarations.RecordFieldDeclType;
import com.io7m.jpra.model.type_declarations.RecordFieldDeclValue;
import com.io7m.jpra.model.type_declarations.TypeDeclPacked;
import com.io7m.jpra.model.type_declarations.TypeDeclRecord;
import com.io7m.jpra.model.type_expressions.TypeExprArray;
import com.io7m.jpra.model.type_expressions.TypeExprBooleanSet;
import com.io7m.jpra.model.type_expressions.TypeExprFloat;
import com.io7m.jpra.model.type_expressions.TypeExprIntegerSigned;
import com.io7m.jpra.model.type_expressions.TypeExprIntegerSignedNormalized;
import com.io7m.jpra.model.type_expressions.TypeExprIntegerUnsigned;
import com.io7m.jpra.model.type_expressions.TypeExprIntegerUnsignedNormalized;
import com.io7m.jpra.model.type_expressions.TypeExprMatrix;
import com.io7m.jpra.model.type_expressions.TypeExprName;
import com.io7m.jpra.model.type_expressions.TypeExprString;
import com.io7m.jpra.model.type_expressions.TypeExprType;
import com.io7m.jpra.model.type_expressions.TypeExprVector;
import com.io7m.jsx.SExpressionListType;
import com.io7m.jsx.SExpressionMatcherType;
import com.io7m.jsx.SExpressionQuotedStringType;
import com.io7m.jsx.SExpressionSymbolType;
import com.io7m.jsx.SExpressionType;
import com.io7m.jsx.api.serializer.JSXSerializerType;
import com.io7m.junreachable.UnreachableCodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * The default implementation of the {@link JPRAParserType} interface.
 */

public final class JPRAParser implements JPRAParserType
{
  private static final Logger LOG;

  private static final Set<String> KEYWORDS;
  private static final Set<String> TYPES;
  private static final Set<String> INTEGER_TYPES;
  private static final Set<String> SIZE_FUNCTIONS;
  private static final Set<String> RECORD_FIELD_KEYWORDS;
  private static final Set<String> PACKED_FIELD_KEYWORDS;

  private static final String PACKAGE_BEGIN = "package-begin";
  private static final String PACKAGE_END = "package-end";
  private static final String IMPORT = "import";
  private static final String RECORD = "record";
  private static final String PACKED = "packed";
  private static final String COMMAND_TYPE = ":type";
  private static final String COMMAND_SIZE = ":size";

  private static final String INTEGER = "integer";
  private static final String INTEGER_SIGNED = "signed";
  private static final String INTEGER_UNSIGNED = "unsigned";
  private static final String INTEGER_SIGNED_NORMALIZED = "signed-normalized";
  private static final String INTEGER_UNSIGNED_NORMALIZED =
    "unsigned-normalized";

  private static final String SIZE_IN_OCTETS = "size-in-octets";
  private static final String SIZE_IN_BITS = "size-in-bits";

  private static final String FLOAT = "float";
  private static final String VECTOR = "vector";
  private static final String MATRIX = "matrix";
  private static final String ARRAY = "array";
  private static final String STRING = "string";
  private static final String BOOLEAN_SET = "boolean-set";

  private static final String FIELD = "field";
  private static final String PADDING_OCTETS = "padding-octets";
  private static final String PADDING_BITS = "padding-bits";

  static {
    KEYWORDS = new HashSet<>(16);
    KEYWORDS.add(PACKAGE_BEGIN);
    KEYWORDS.add(PACKAGE_END);
    KEYWORDS.add(IMPORT);
    KEYWORDS.add(RECORD);
    KEYWORDS.add(PACKED);
    KEYWORDS.add(COMMAND_TYPE);
    KEYWORDS.add(COMMAND_SIZE);

    RECORD_FIELD_KEYWORDS = new HashSet<>(16);
    RECORD_FIELD_KEYWORDS.add(FIELD);
    RECORD_FIELD_KEYWORDS.add(PADDING_OCTETS);

    PACKED_FIELD_KEYWORDS = new HashSet<>(16);
    PACKED_FIELD_KEYWORDS.add(FIELD);
    PACKED_FIELD_KEYWORDS.add(PADDING_BITS);

    TYPES = new HashSet<>(16);
    TYPES.add(INTEGER);
    TYPES.add(FLOAT);
    TYPES.add(ARRAY);
    TYPES.add(VECTOR);
    TYPES.add(MATRIX);
    TYPES.add(STRING);
    TYPES.add(BOOLEAN_SET);

    INTEGER_TYPES = new HashSet<>(16);
    INTEGER_TYPES.add(INTEGER_SIGNED);
    INTEGER_TYPES.add(INTEGER_UNSIGNED);
    INTEGER_TYPES.add(
      INTEGER_SIGNED_NORMALIZED);
    INTEGER_TYPES.add(
      INTEGER_UNSIGNED_NORMALIZED);

    SIZE_FUNCTIONS = new HashSet<>(16);
    SIZE_FUNCTIONS.add(SIZE_IN_OCTETS);
    SIZE_FUNCTIONS.add(SIZE_IN_BITS);

    LOG = LoggerFactory.getLogger(JPRAParser.class);
  }

  private final JSXSerializerType serial;
  private final JPRAReferenceParserType ref_parser;

  private JPRAParser(
    final JSXSerializerType in_serial,
    final JPRAReferenceParserType in_ref_parser)
  {
    this.serial = NullCheck.notNull(in_serial, "Serializer");
    this.ref_parser = NullCheck.notNull(in_ref_parser, "Parser");
  }

  /**
   * @param serial     A serializer for error messages
   * @param ref_parser A reference parser
   *
   * @return A new parser
   */

  public static JPRAParserType newParser(
    final JSXSerializerType serial,
    final JPRAReferenceParserType ref_parser)
  {
    return new JPRAParser(serial, ref_parser);
  }

  private static SExpressionSymbolType requireSymbol(
    final SExpressionType e)
    throws JPRACompilerParseException
  {
    return e.matchExpression(
      new SExpressionMatcherType<SExpressionSymbolType,
        JPRACompilerParseException>()
      {
        @Override
        public SExpressionSymbolType list(final SExpressionListType le)
          throws JPRACompilerParseException
        {
          throw JPRACompilerParseException.expectedSymbolGotList(le);
        }

        @Override
        public SExpressionSymbolType quotedString(
          final SExpressionQuotedStringType qe)
          throws JPRACompilerParseException
        {
          throw JPRACompilerParseException.expectedSymbolGotQuotedString(qe);
        }

        @Override
        public SExpressionSymbolType symbol(final SExpressionSymbolType se)
          throws JPRACompilerParseException
        {
          return se;
        }
      });
  }

  private static void checkKeyword(final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    if (!KEYWORDS.contains(se.text())) {
      final StringBuilder sb = new StringBuilder(256);
      sb.append("Unrecognized keyword '");
      sb.append(se.text());
      sb.append("'");
      sb.append(System.lineSeparator());
      sb.append("  Expected one of: ");
      sb.append(KEYWORDS);
      sb.append(System.lineSeparator());
      throw JPRACompilerParseException.unrecognizedKeyword(se, sb.toString());
    }
  }

  private static void checkType(final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    if (!TYPES.contains(se.text())) {
      final StringBuilder sb = new StringBuilder(256);
      sb.append("Unrecognized type keyword '");
      sb.append(se.text());
      sb.append("'");
      sb.append(System.lineSeparator());
      sb.append("  Expected one of: ");
      sb.append(TYPES);
      sb.append(System.lineSeparator());
      throw JPRACompilerParseException.unrecognizedTypeKeyword(
        se, sb.toString());
    }
  }

  private static Optional<LexicalPosition<Path>>
  getExpressionLexical(final SExpressionType q)
  {
    return q.lexical().map(LexicalPosition::copyOf);
  }

  private static void checkIntegerTypeKeyword(final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    if (!INTEGER_TYPES.contains(se.text())) {
      final StringBuilder sb = new StringBuilder(256);
      sb.append("Unrecognized integer type keyword '");
      sb.append(se.text());
      sb.append("'");
      sb.append(System.lineSeparator());
      sb.append("  Expected one of: ");
      sb.append(INTEGER_TYPES);
      sb.append(System.lineSeparator());
      throw JPRACompilerParseException.unrecognizedIntegerTypeKeyword(
        se, sb.toString());
    }
  }

  private static void checkSizeFunction(final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    if (!SIZE_FUNCTIONS.contains(se.text())) {
      final StringBuilder sb = new StringBuilder(256);
      sb.append("Unrecognized size function '");
      sb.append(se.text());
      sb.append("'");
      sb.append(System.lineSeparator());
      sb.append("  Expected one of: ");
      sb.append(SIZE_FUNCTIONS);
      sb.append(System.lineSeparator());
      throw JPRACompilerParseException.unrecognizedSizeFunction(
        se, sb.toString());
    }
  }

  private static ImmutableList<FieldName> parseFieldSet(
    final SExpressionType f_expr)
    throws JPRACompilerParseException
  {
    return f_expr.matchExpression(
      new SExpressionMatcherType<ImmutableList<FieldName>,
        JPRACompilerParseException>()
      {
        @Override
        public ImmutableList<FieldName> list(final SExpressionListType e)
          throws JPRACompilerParseException
        {
          final Map<FieldName, Unresolved> names =
            new LinkedHashMap<>(e.size());

          for (int index = 0; index < e.size(); ++index) {
            final SExpressionType ei = e.get(index);
            final SExpressionSymbolType si = requireSymbol(ei);

            final FieldName name;
            try {
              name = new FieldName(
                getExpressionLexical(si), si.text());
            } catch (final PreconditionViolationException x) {
              throw JPRACompilerParseException.badFieldName(si, x.getMessage());
            }

            if (!names.containsKey(name)) {
              names.put(name, Unresolved.get());
            } else {
              final StringBuilder sb = new StringBuilder(128);
              sb.append("Duplicate field name.");
              sb.append(System.lineSeparator());
              sb.append("  Name: ");
              sb.append(name);
              sb.append(System.lineSeparator());
              sb.append("  Fields: ");
              sb.append(names.keySet());
              throw JPRACompilerParseException.duplicateFieldName(
                si, sb.toString());
            }
          }

          final FastList<FieldName> rx = new FastList<>(names.size());
          rx.addAll(names.keySet());
          return rx.toImmutable();
        }

        @Override
        public ImmutableList<FieldName> quotedString(
          final SExpressionQuotedStringType e)
          throws JPRACompilerParseException
        {
          throw JPRACompilerParseException.expectedListGotQuotedString(e);
        }

        @Override
        public ImmutableList<FieldName> symbol(final SExpressionSymbolType e)
          throws JPRACompilerParseException
        {
          throw JPRACompilerParseException.expectedListGotSymbol(e);
        }
      });
  }

  private static PackageNameQualified parsePackageNameQualified(
    final SExpressionSymbolType name)
    throws JPRACompilerParseException
  {
    final String text = name.text();
    Preconditions.checkPrecondition(!text.isEmpty(), "Text must not be empty");
    final String[] segments = text.split("\\.");

    final Optional<LexicalPosition<Path>> ilex =
      getExpressionLexical(name);

    final MutableList<PackageNameUnqualified> names_base = new FastList<>();
    for (int index = 0; index < segments.length; ++index) {
      final String raw = segments[index];
      try {
        names_base.add(new PackageNameUnqualified(ilex, raw));
      } catch (final PreconditionViolationException e) {
        throw JPRACompilerParseException.badPackageName(name, e.getMessage());
      }
    }

    final ImmutableList<PackageNameUnqualified> names =
      names_base.toImmutable();
    return new PackageNameQualified(names);
  }

  private static PackageNameUnqualified parsePackageNameUnqualified(
    final SExpressionSymbolType s)
    throws JPRACompilerParseException
  {
    try {
      return new PackageNameUnqualified(
        getExpressionLexical(s), s.text());
    } catch (final PreconditionViolationException e) {
      throw JPRACompilerParseException.badPackageName(s, e.getMessage());
    }
  }

  private static SExpressionListType requireList(final SExpressionType e)
    throws JPRACompilerParseException
  {
    return e.matchExpression(
      new SExpressionMatcherType<SExpressionListType,
        JPRACompilerParseException>()
      {
        @Override
        public SExpressionListType list(final SExpressionListType le)
          throws JPRACompilerParseException
        {
          return le;
        }

        @Override
        public SExpressionListType quotedString(
          final SExpressionQuotedStringType qe)
          throws JPRACompilerParseException
        {
          throw JPRACompilerParseException.expectedListGotQuotedString(qe);
        }

        @Override
        public SExpressionListType symbol(final SExpressionSymbolType se)
          throws JPRACompilerParseException
        {
          throw JPRACompilerParseException.expectedListGotSymbol(se);
        }
      });
  }

  private static TypeName parseTypeName(final SExpressionSymbolType name)
    throws JPRACompilerParseException
  {
    try {
      return new TypeName(
        getExpressionLexical(name), name.text());
    } catch (final PreconditionViolationException e) {
      throw JPRACompilerParseException.badTypeName(name, e.getMessage());
    }
  }

  private static void checkRecordFieldKeyword(final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    if (!RECORD_FIELD_KEYWORDS.contains(se.text())) {
      final StringBuilder sb = new StringBuilder(256);
      sb.append("Unrecognized record field keyword '");
      sb.append(se.text());
      sb.append("'");
      sb.append(System.lineSeparator());
      sb.append("Expected one of: ");
      sb.append(RECORD_FIELD_KEYWORDS);
      sb.append(System.lineSeparator());
      throw JPRACompilerParseException.unrecognizedRecordFieldKeyword(
        se, sb.toString());
    }
  }

  private static void checkPackedFieldKeyword(final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    if (!PACKED_FIELD_KEYWORDS.contains(se.text())) {
      final StringBuilder sb = new StringBuilder(256);
      sb.append("Unrecognized packed field keyword '");
      sb.append(se.text());
      sb.append("'");
      sb.append(System.lineSeparator());
      sb.append("  Expected one of: ");
      sb.append(PACKED_FIELD_KEYWORDS);
      sb.append(System.lineSeparator());
      throw JPRACompilerParseException.unrecognizedPackedFieldKeyword(
        se, sb.toString());
    }
  }

  private static FieldName parseFieldName(final SExpressionSymbolType name)
    throws JPRACompilerParseException
  {
    try {
      return new FieldName(
        getExpressionLexical(name), name.text());
    } catch (final PreconditionViolationException e) {
      throw JPRACompilerParseException.badFieldName(name, e.getMessage());
    }
  }

  private StatementPackageImport<Unresolved, Untyped> parsePackageImport(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Preconditions.checkPreconditionV(
      Objects.equals(IMPORT, se.text()),
      "Text must be %s",
      IMPORT);

    if (le.size() == 4) {
      final SExpressionType q_name = le.get(1);
      final SExpressionType as = le.get(2);
      final SExpressionType u_name = le.get(3);

      if (q_name instanceof SExpressionSymbolType
        && u_name instanceof SExpressionSymbolType
        && as instanceof SExpressionSymbolType
        && Objects.equals("as", ((SExpressionSymbolType) as).text())) {
        final SExpressionSymbolType q_sym = (SExpressionSymbolType) q_name;
        final SExpressionSymbolType u_sym = (SExpressionSymbolType) u_name;

        final PackageNameQualified p_name =
          parsePackageNameQualified(q_sym);
        final PackageNameUnqualified up_name =
          parsePackageNameUnqualified(u_sym);

        return new StatementPackageImport<>(p_name, up_name);
      }
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(le, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append(
        "  Expected: (import <package-name-qualified> as "
          + "<package-name-unqualified)");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(le, sb.toString());
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  @Override
  public StatementType<Unresolved, Untyped> parseStatement(
    final SExpressionType expr)
    throws JPRACompilerParseException
  {
    NullCheck.notNull(expr, "Expression");

    final SExpressionListType le = requireList(expr);
    if (le.size() == 0) {
      throw JPRACompilerParseException.expectedNonEmptyList(le);
    }

    final SExpressionSymbolType se = requireSymbol(le.get(0));
    checkKeyword(se);

    switch (se.text()) {
      case PACKAGE_BEGIN:
        return this.parsePackageBegin(le, se);
      case PACKAGE_END:
        return this.parsePackageEnd(le, se);
      case IMPORT:
        return this.parsePackageImport(le, se);
      case RECORD:
        return this.parseRecord(le, se);
      case PACKED:
        return this.parsePacked(le, se);
      case COMMAND_SIZE:
        return this.parseCommandSize(le, se);
      case COMMAND_TYPE:
        return this.parseCommandType(le, se);
      default:
        throw new UnreachableCodeException();
    }
  }

  private StatementCommandType<Unresolved, Untyped> parseCommandType(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Preconditions.checkPreconditionV(
      Objects.equals(COMMAND_TYPE, se.text()),
      "Text must be %s",
      COMMAND_TYPE);

    if (le.size() == 2) {
      return new StatementCommandType<>(this.parseTypeExpression(le.get(1)));
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(le, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("  Expected: (:type <type-expression>)");
      sb.append(System.lineSeparator());
      sb.append("  Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(le, sb.toString());
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  private StatementCommandSize<Unresolved, Untyped> parseCommandSize(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Preconditions.checkPreconditionV(
      Objects.equals(COMMAND_SIZE, se.text()),
      "Text must be %s",
      COMMAND_SIZE);

    if (le.size() == 2) {
      return new StatementCommandSize<>(this.parseSizeExpression(le.get(1)));
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(le, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("  Expected: (:size <size-expression>)");
      sb.append(System.lineSeparator());
      sb.append("  Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(le, sb.toString());
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  private StatementType<Unresolved, Untyped> parsePacked(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Preconditions.checkPreconditionV(
      Objects.equals(PACKED, se.text()),
      "Text must be %s",
      PACKED);

    if (le.size() == 3) {
      final SExpressionType n_expr = le.get(1);
      final SExpressionType f_expr = le.get(2);

      if (n_expr instanceof SExpressionSymbolType
        && f_expr instanceof SExpressionListType) {

        final TypeName t_name =
          parseTypeName((SExpressionSymbolType) n_expr);
        final SExpressionListType fl_expr = (SExpressionListType) f_expr;

        final MutableMap<FieldName, PackedFieldDeclValue<Unresolved, Untyped>>
          fields_by_name = Maps.mutable.empty();
        final MutableList<PackedFieldDeclType<Unresolved, Untyped>>
          fields_ordered = Lists.mutable.empty();

        this.parsePackedFields(fl_expr, fields_by_name, fields_ordered);

        return new TypeDeclPacked<>(
          Unresolved.get(),
          Untyped.get(),
          fields_by_name.toImmutable(),
          t_name,
          fields_ordered.toImmutable());
      }
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(le, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("  Expected: (packed <type-name> (<field> ... <field>))");
      sb.append(System.lineSeparator());
      sb.append("  Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(le, sb.toString());
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  private void parsePackedFields(
    final SExpressionListType fields,
    final MutableMap<FieldName, PackedFieldDeclValue<Unresolved, Untyped>>
      fields_named,
    final MutableList<PackedFieldDeclType<Unresolved, Untyped>> fields_order)
    throws JPRACompilerParseException
  {
    for (int index = 0; index < fields.size(); ++index) {
      final SExpressionListType l_expr =
        requireList(fields.get(index));
      if (l_expr.size() == 0) {
        throw JPRACompilerParseException.expectedNonEmptyList(l_expr);
      }

      final SExpressionSymbolType k = requireSymbol(l_expr.get(0));
      checkPackedFieldKeyword(k);

      final int e_count = l_expr.size();
      switch (k.text()) {
        case FIELD: {
          final PackedFieldDeclValue<Unresolved, Untyped> f =
            this.parsePackedFieldValue(l_expr, e_count);

          final FieldName f_name = f.getName();
          if (fields_named.containsKey(f_name)) {
            final StringBuilder sb = new StringBuilder(128);
            sb.append("Duplicate field name.");
            sb.append(System.lineSeparator());
            sb.append("  Name: ");
            sb.append(f_name.getValue());
            throw JPRACompilerParseException.duplicateFieldName(
              k, sb.toString());
          }

          fields_named.put(f_name, f);
          fields_order.add(f);
          continue;
        }
        case PADDING_BITS: {
          final PackedFieldDeclPaddingBits<Unresolved, Untyped> f =
            this.parsePackedPaddingBits(l_expr, e_count);

          fields_order.add(f);
          continue;
        }

        default:
          throw new UnreachableCodeException();
      }
    }
  }

  private PackedFieldDeclPaddingBits<Unresolved, Untyped>
  parsePackedPaddingBits(
    final SExpressionListType l_expr,
    final int e_count)
    throws JPRACompilerParseException
  {
    if (e_count == 2) {
      final SizeExprType<Unresolved, Untyped> s =
        this.parseSizeExpression(l_expr.get(1));
      return new PackedFieldDeclPaddingBits<>(
        getExpressionLexical(l_expr), s);
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(l_expr, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("  Expected: (padding-bits <size-expression>)");
      sb.append(System.lineSeparator());
      sb.append("  Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(l_expr, sb.toString());
    } catch (final IOException x) {
      throw new UnreachableCodeException(x);
    }
  }

  private PackedFieldDeclValue<Unresolved, Untyped> parsePackedFieldValue(
    final SExpressionListType l_expr,
    final int e_count)
    throws JPRACompilerParseException
  {
    if (e_count == 3 && l_expr.get(1) instanceof SExpressionSymbolType) {
      final SExpressionSymbolType f_name =
        (SExpressionSymbolType) l_expr.get(1);
      final FieldName name = parseFieldName(f_name);
      final TypeExprType<Unresolved, Untyped> te =
        this.parseTypeExpression(l_expr.get(2));
      return new PackedFieldDeclValue<>(Unresolved.get(), name, te);
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(l_expr, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("  Expected: (field <field-name> <type-expression>)");
      sb.append(System.lineSeparator());
      sb.append("  Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(l_expr, sb.toString());
    } catch (final IOException x) {
      throw new UnreachableCodeException(x);
    }
  }

  private StatementType<Unresolved, Untyped> parseRecord(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Preconditions.checkPreconditionV(
      Objects.equals(RECORD, se.text()),
      "Text must be %s",
      RECORD);

    if (le.size() == 3) {
      final SExpressionType n_expr = le.get(1);
      final SExpressionType f_expr = le.get(2);

      if (n_expr instanceof SExpressionSymbolType
        && f_expr instanceof SExpressionListType) {

        final TypeName t_name =
          parseTypeName((SExpressionSymbolType) n_expr);
        final SExpressionListType fl_expr = (SExpressionListType) f_expr;

        final MutableMap<FieldName, RecordFieldDeclValue<Unresolved, Untyped>>
          fields_by_name = Maps.mutable.empty();
        final MutableList<RecordFieldDeclType<Unresolved, Untyped>>
          fields_ordered = Lists.mutable.empty();

        this.parseRecordFields(fl_expr, fields_by_name, fields_ordered);

        return new TypeDeclRecord<>(
          Unresolved.get(),
          Untyped.get(),
          fields_by_name.toImmutable(),
          t_name,
          fields_ordered.toImmutable());
      }
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(le, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("  Expected: (record <type-name> (<field> ... <field>))");
      sb.append(System.lineSeparator());
      sb.append("  Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(le, sb.toString());
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  private void parseRecordFields(
    final SExpressionListType fields,
    final MutableMap<FieldName, RecordFieldDeclValue<Unresolved, Untyped>>
      fields_named,
    final MutableList<RecordFieldDeclType<Unresolved, Untyped>> fields_order)
    throws JPRACompilerParseException
  {
    for (int index = 0; index < fields.size(); ++index) {
      final SExpressionListType l_expr =
        requireList(fields.get(index));
      if (l_expr.size() == 0) {
        throw JPRACompilerParseException.expectedNonEmptyList(l_expr);
      }

      final SExpressionSymbolType k = requireSymbol(l_expr.get(0));
      checkRecordFieldKeyword(k);

      final int e_count = l_expr.size();
      switch (k.text()) {
        case FIELD: {
          final RecordFieldDeclValue<Unresolved, Untyped> f =
            this.parseRecordFieldValue(l_expr, e_count);

          final FieldName f_name = f.getName();
          if (fields_named.containsKey(f_name)) {
            final StringBuilder sb = new StringBuilder(128);
            sb.append("Duplicate field name.");
            sb.append(System.lineSeparator());
            sb.append("  Name: ");
            sb.append(f_name.getValue());
            throw JPRACompilerParseException.duplicateFieldName(
              k, sb.toString());
          }

          fields_named.put(f_name, f);
          fields_order.add(f);
          continue;
        }
        case PADDING_OCTETS: {
          final RecordFieldDeclPaddingOctets<Unresolved, Untyped> f =
            this.parseRecordPaddingOctets(l_expr, e_count);

          fields_order.add(f);
          continue;
        }
        default:
          throw new UnreachableCodeException();
      }
    }
  }

  private RecordFieldDeclPaddingOctets<Unresolved, Untyped>
  parseRecordPaddingOctets(
    final SExpressionListType l_expr,
    final int e_count)
    throws JPRACompilerParseException
  {
    if (e_count == 2) {
      final SizeExprType<Unresolved, Untyped> s =
        this.parseSizeExpression(l_expr.get(1));
      return new RecordFieldDeclPaddingOctets<>(
        getExpressionLexical(l_expr), s);
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(l_expr, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("  Expected: (padding-octets <size-expression>)");
      sb.append(System.lineSeparator());
      sb.append("  Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(l_expr, sb.toString());
    } catch (final IOException x) {
      throw new UnreachableCodeException(x);
    }
  }

  private RecordFieldDeclValue<Unresolved, Untyped> parseRecordFieldValue(
    final SExpressionListType l_expr,
    final int e_count)
    throws JPRACompilerParseException
  {
    if (e_count == 3 && l_expr.get(1) instanceof SExpressionSymbolType) {
      final SExpressionSymbolType f_name =
        (SExpressionSymbolType) l_expr.get(1);
      final FieldName name = parseFieldName(f_name);
      final TypeExprType<Unresolved, Untyped> te =
        this.parseTypeExpression(l_expr.get(2));
      return new RecordFieldDeclValue<>(Unresolved.get(), name, te);
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(l_expr, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("  Expected: (field <field-name> <type-expression>)");
      sb.append(System.lineSeparator());
      sb.append("  Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(l_expr, sb.toString());
    } catch (final IOException x) {
      throw new UnreachableCodeException(x);
    }
  }

  private StatementPackageEnd<Unresolved, Untyped> parsePackageEnd(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Preconditions.checkPreconditionV(
      Objects.equals(PACKAGE_END, se.text()),
      "Text must be %s",
      PACKAGE_END);

    if (le.size() == 1) {
      return new StatementPackageEnd<>(getExpressionLexical(se));
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(le, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("  Expected: (package-end)");
      sb.append(System.lineSeparator());
      sb.append("  Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(le, sb.toString());
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  private StatementPackageBegin<Unresolved, Untyped> parsePackageBegin(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Preconditions.checkPreconditionV(
      Objects.equals(PACKAGE_BEGIN, se.text()),
      "Text must be %s",
      PACKAGE_BEGIN);

    if (le.size() == 2) {
      final SExpressionType e_name = le.get(1);
      if (e_name instanceof SExpressionSymbolType) {
        final SExpressionSymbolType name = (SExpressionSymbolType) e_name;
        final PackageNameQualified p_name =
          parsePackageNameQualified(name);
        return new StatementPackageBegin<>(p_name);
      }
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(le, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("  Expected: (package-begin <package-name-qualified>)");
      sb.append(System.lineSeparator());
      sb.append("  Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(le, sb.toString());
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  @Override
  public TypeExprType<Unresolved, Untyped> parseTypeExpression(
    final SExpressionType expr)
    throws JPRACompilerParseException
  {
    NullCheck.notNull(expr);

    return expr.matchExpression(
      new SExpressionMatcherType<TypeExprType<Unresolved, Untyped>,
        JPRACompilerParseException>()
      {
        @Override
        public TypeExprType<Unresolved, Untyped> list(
          final SExpressionListType le)
          throws JPRACompilerParseException
        {
          if (le.size() == 0) {
            throw JPRACompilerParseException.expectedNonEmptyList(le);
          }

          final SExpressionSymbolType se = requireSymbol(le.get(0));
          checkType(se);

          switch (se.text()) {
            case INTEGER: {
              return JPRAParser.this.parseTypeInteger(le, se);
            }
            case FLOAT: {
              return JPRAParser.this.parseTypeFloat(le, se);
            }
            case VECTOR: {
              return JPRAParser.this.parseTypeVector(le, se);
            }
            case MATRIX: {
              return JPRAParser.this.parseTypeMatrix(le, se);
            }
            case ARRAY: {
              return JPRAParser.this.parseTypeArray(le, se);
            }
            case STRING: {
              return JPRAParser.this.parseTypeString(le, se);
            }
            case BOOLEAN_SET: {
              return JPRAParser.this.parseTypeBooleanSet(le, se);
            }
            default:
              throw new UnreachableCodeException();
          }
        }

        @Override
        public TypeExprType<Unresolved, Untyped> quotedString(
          final SExpressionQuotedStringType qe)
          throws JPRACompilerParseException
        {
          throw JPRACompilerParseException.expectedSymbolOrListGotQuotedString(
            qe);
        }

        @Override
        public TypeExprType<Unresolved, Untyped> symbol(
          final SExpressionSymbolType se)
          throws JPRACompilerParseException
        {
          return JPRAParser.this.parseTypeReference(se);
        }
      });
  }

  private TypeExprType<Unresolved, Untyped> parseTypeReference(
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    final Optional<LexicalPosition<Path>> lex =
      getExpressionLexical(se);
    return new TypeExprName<>(
      Unresolved.get(), Untyped.get(), this.ref_parser.parseTypeReference(se));
  }

  private TypeExprType<Unresolved, Untyped> parseTypeBooleanSet(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Preconditions.checkPreconditionV(
      Objects.equals(BOOLEAN_SET, se.text()),
      "Text must be %s",
      BOOLEAN_SET);

    if (le.size() == 3) {
      final SExpressionType s_expr = le.get(1);
      final SExpressionType f_expr = le.get(2);
      final SizeExprType<Unresolved, Untyped> size =
        this.parseSizeExpression(s_expr);

      final ImmutableList<FieldName> fields = parseFieldSet(f_expr);
      final Optional<LexicalPosition<Path>> lex =
        getExpressionLexical(s_expr);
      return new TypeExprBooleanSet<>(Untyped.get(), lex, fields, size);
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(se, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append(
        "  Expected: (boolean-set <size-expression> (<field> ... <field>))");
      sb.append(System.lineSeparator());
      sb.append("  Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(se, sb.toString());
    } catch (final IOException x) {
      throw new UnreachableCodeException(x);
    }

  }

  private TypeExprType<Unresolved, Untyped> parseTypeString(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Preconditions.checkPreconditionV(
      Objects.equals(STRING, se.text()),
      "Text must be %s",
      STRING);

    if (le.size() == 3) {
      final SExpressionType s_expr = le.get(1);
      final SExpressionType e_expr = le.get(2);

      if (e_expr instanceof SExpressionQuotedStringType) {
        final SExpressionQuotedStringType qe =
          (SExpressionQuotedStringType) e_expr;
        final SizeExprType<Unresolved, Untyped> size =
          this.parseSizeExpression(s_expr);
        return new TypeExprString<>(
          Untyped.get(),
          getExpressionLexical(le),
          size,
          qe.text());
      }
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(se, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("  Expected: (string <size-expression> \"<encoding>\")");
      sb.append(System.lineSeparator());
      sb.append("  Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(se, sb.toString());
    } catch (final IOException x) {
      throw new UnreachableCodeException(x);
    }

  }

  private TypeExprType<Unresolved, Untyped> parseTypeArray(

    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Preconditions.checkPreconditionV(
      Objects.equals(ARRAY, se.text()),
      "Text must be %s",
      ARRAY);

    if (le.size() == 3) {
      final SExpressionType t_expr = le.get(1);
      final SExpressionType s_expr = le.get(2);
      final SizeExprType<Unresolved, Untyped> size =
        this.parseSizeExpression(s_expr);
      final TypeExprType<Unresolved, Untyped> type =
        this.parseTypeExpression(t_expr);
      return new TypeExprArray<>(
        Untyped.get(), getExpressionLexical(le), size, type);
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(se, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("  Expected: (array <type-expression> <size-expression>)");
      sb.append(System.lineSeparator());
      sb.append("  Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(se, sb.toString());
    } catch (final IOException x) {
      throw new UnreachableCodeException(x);
    }

  }

  private TypeExprType<Unresolved, Untyped> parseTypeMatrix(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Preconditions.checkPreconditionV(
      Objects.equals(MATRIX, se.text()),
      "Text must be %s",
      MATRIX);

    if (le.size() == 4) {
      final SExpressionType t_expr = le.get(1);
      final SExpressionType w_expr = le.get(2);
      final SExpressionType h_expr = le.get(3);

      final SizeExprType<Unresolved, Untyped> width =
        this.parseSizeExpression(w_expr);
      final SizeExprType<Unresolved, Untyped> height =
        this.parseSizeExpression(h_expr);
      final TypeExprType<Unresolved, Untyped> type =
        this.parseTypeExpression(t_expr);

      return new TypeExprMatrix<>(
        Untyped.get(),
        getExpressionLexical(le),
        width,
        height,
        type);
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(se, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append(
        "  Expected: (matrix <scalar-type-expression> <width> <height>)");
      sb.append(System.lineSeparator());
      sb.append("  Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(se, sb.toString());
    } catch (final IOException x) {
      throw new UnreachableCodeException(x);
    }

  }

  private TypeExprType<Unresolved, Untyped> parseTypeVector(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Preconditions.checkPreconditionV(
      Objects.equals(VECTOR, se.text()),
      "Text must be %s",
      VECTOR);

    if (le.size() == 3) {
      final Optional<LexicalPositionType<Path>> lex = le.lexical();
      final SExpressionType t_expr = le.get(1);
      final SExpressionType s_expr = le.get(2);
      return new TypeExprVector<>(
        Untyped.get(),
        lex.map(LexicalPosition::copyOf),
        this.parseSizeExpression(s_expr),
        this.parseTypeExpression(t_expr));
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(se, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("  Expected: (vector <type-expression> <size-expression>)");
      sb.append(System.lineSeparator());
      sb.append("  Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(se, sb.toString());
    } catch (final IOException x) {
      throw new UnreachableCodeException(x);
    }
  }

  private TypeExprType<Unresolved, Untyped> parseTypeFloat(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Preconditions.checkPreconditionV(
      Objects.equals(FLOAT, se.text()),
      "Text must be %s",
      FLOAT);

    if (le.size() == 2) {
      final SExpressionType s_expr = le.get(1);
      return new TypeExprFloat<>(
        Untyped.get(),
        getExpressionLexical(s_expr),
        this.parseSizeExpression(s_expr));
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(le, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("  Expected: (float <size-in-bits>)");
      sb.append(System.lineSeparator());
      sb.append("  Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(le, sb.toString());
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  private TypeExprType<Unresolved, Untyped> parseTypeInteger(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Preconditions.checkPreconditionV(
      Objects.equals(INTEGER, se.text()),
      "Text must be %s",
      INTEGER);

    if (le.size() == 3) {
      final SExpressionType t_expr = le.get(1);
      final SExpressionType s_expr = le.get(2);
      if (t_expr instanceof SExpressionSymbolType) {
        final SExpressionSymbolType t_name = (SExpressionSymbolType) t_expr;

        checkIntegerTypeKeyword(t_name);

        final SizeExprType<Unresolved, Untyped> size =
          this.parseSizeExpression(s_expr);

        switch (t_name.text()) {
          case INTEGER_SIGNED: {
            return new TypeExprIntegerSigned<>(
              Untyped.get(), getExpressionLexical(s_expr), size);
          }
          case INTEGER_UNSIGNED: {
            return new TypeExprIntegerUnsigned<>(
              Untyped.get(), getExpressionLexical(s_expr), size);
          }
          case INTEGER_SIGNED_NORMALIZED: {
            return new TypeExprIntegerSignedNormalized<>(
              Untyped.get(), getExpressionLexical(s_expr), size);
          }
          case INTEGER_UNSIGNED_NORMALIZED: {
            return new TypeExprIntegerUnsignedNormalized<>(
              Untyped.get(), getExpressionLexical(s_expr), size);
          }
          default:
            throw new UnreachableCodeException();
        }
      }
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(le, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("  Expected: (integer <integer-type> <size-in-bits>)");
      sb.append(System.lineSeparator());
      sb.append("  Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(le, sb.toString());
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  @Override
  public SizeExprType<Unresolved, Untyped> parseSizeExpression(
    final SExpressionType e)
    throws JPRACompilerParseException
  {
    return e.matchExpression(
      new SExpressionMatcherType<SizeExprType<Unresolved, Untyped>,
        JPRACompilerParseException>()
      {
        @Override
        public SizeExprType<Unresolved, Untyped> list(
          final SExpressionListType le)
          throws JPRACompilerParseException
        {
          if (le.size() == 0) {
            throw JPRACompilerParseException.expectedNonEmptyList(le);
          }

          final SExpressionSymbolType se = requireSymbol(le.get(0));
          checkSizeFunction(se);

          switch (se.text()) {
            case SIZE_IN_BITS: {
              return JPRAParser.this.parseSizeInBits(le, se);
            }
            case SIZE_IN_OCTETS: {
              return JPRAParser.this.parseSizeInOctets(le, se);
            }
            default:
              throw new UnreachableCodeException();
          }
        }

        @Override
        public SizeExprType<Unresolved, Untyped> quotedString(
          final SExpressionQuotedStringType qe)
          throws JPRACompilerParseException
        {
          throw JPRACompilerParseException.expectedSymbolOrListGotQuotedString(
            qe);
        }

        @Override
        public SizeExprType<Unresolved, Untyped> symbol(
          final SExpressionSymbolType se)
          throws JPRACompilerParseException
        {
          try {
            return new SizeExprConstant<>(
              getExpressionLexical(se),
              new BigInteger(se.text()));
          } catch (final NumberFormatException x) {
            throw JPRACompilerParseException.invalidIntegerConstant(se);
          }
        }
      });
  }

  @Override
  public void parseEOF(
    final Optional<LexicalPosition<Path>> lex)
    throws JPRACompilerParseException
  {
    NullCheck.notNull(lex, "Lexical information");
  }

  private SizeExprType<Unresolved, Untyped> parseSizeInBits(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Preconditions.checkPreconditionV(
      Objects.equals(SIZE_IN_BITS, se.text()),
      "Text must be %s",
      SIZE_IN_BITS);

    if (le.size() == 2) {
      return new SizeExprInBits<>(this.parseTypeExpression(le.get(1)));
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(le, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("  Expected: (size-in-bits <type-expression>)");
      sb.append(System.lineSeparator());
      sb.append("  Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(le, sb.toString());
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  private SizeExprType<Unresolved, Untyped> parseSizeInOctets(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Preconditions.checkPreconditionV(
      Objects.equals(SIZE_IN_OCTETS, se.text()),
      "Text must be %s",
      SIZE_IN_OCTETS);

    if (le.size() == 2) {
      return new SizeExprInOctets<>(this.parseTypeExpression(le.get(1)));
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(le, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("  Expected: (size-in-octets <type-expression>)");
      sb.append(System.lineSeparator());
      sb.append("  Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(le, sb.toString());
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

}
