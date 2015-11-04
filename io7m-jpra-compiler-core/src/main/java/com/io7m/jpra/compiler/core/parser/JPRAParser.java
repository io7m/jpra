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
import com.io7m.jlexing.core.ImmutableLexicalPosition;
import com.io7m.jlexing.core.ImmutableLexicalPositionType;
import com.io7m.jlexing.core.LexicalPositionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.Parsed;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.names.PackageNameQualified;
import com.io7m.jpra.model.names.PackageNameUnqualified;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.jpra.model.size_expressions.SizeExprConstant;
import com.io7m.jpra.model.size_expressions.SizeExprInBits;
import com.io7m.jpra.model.size_expressions.SizeExprInOctets;
import com.io7m.jpra.model.size_expressions.SizeExprType;
import com.io7m.jpra.model.statements.StatementPackageBegin;
import com.io7m.jpra.model.statements.StatementPackageEnd;
import com.io7m.jpra.model.statements.StatementPackageImport;
import com.io7m.jpra.model.statements.StatementType;
import com.io7m.jpra.model.type_declarations.RecordFieldDeclPaddingOctets;
import com.io7m.jpra.model.type_declarations.RecordFieldDeclType;
import com.io7m.jpra.model.type_declarations.RecordFieldDeclValue;
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
import com.io7m.jsx.serializer.JSXSerializerType;
import com.io7m.junreachable.UnreachableCodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valid4j.Assertive;
import org.valid4j.exceptions.RequireViolation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
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
    JPRAParser.KEYWORDS.add(JPRAParser.PACKAGE_BEGIN);
    JPRAParser.KEYWORDS.add(JPRAParser.PACKAGE_END);
    JPRAParser.KEYWORDS.add(JPRAParser.IMPORT);
    JPRAParser.KEYWORDS.add(JPRAParser.RECORD);
    JPRAParser.KEYWORDS.add(JPRAParser.COMMAND_TYPE);
    JPRAParser.KEYWORDS.add(JPRAParser.COMMAND_SIZE);

    RECORD_FIELD_KEYWORDS = new HashSet<>(16);
    JPRAParser.RECORD_FIELD_KEYWORDS.add(JPRAParser.FIELD);
    JPRAParser.RECORD_FIELD_KEYWORDS.add(JPRAParser.PADDING_OCTETS);

    TYPES = new HashSet<>(16);
    JPRAParser.TYPES.add(JPRAParser.INTEGER);
    JPRAParser.TYPES.add(JPRAParser.FLOAT);
    JPRAParser.TYPES.add(JPRAParser.ARRAY);
    JPRAParser.TYPES.add(JPRAParser.VECTOR);
    JPRAParser.TYPES.add(JPRAParser.MATRIX);
    JPRAParser.TYPES.add(JPRAParser.STRING);
    JPRAParser.TYPES.add(JPRAParser.BOOLEAN_SET);

    INTEGER_TYPES = new HashSet<>(16);
    JPRAParser.INTEGER_TYPES.add(JPRAParser.INTEGER_SIGNED);
    JPRAParser.INTEGER_TYPES.add(JPRAParser.INTEGER_UNSIGNED);
    JPRAParser.INTEGER_TYPES.add(
      JPRAParser.INTEGER_SIGNED_NORMALIZED);
    JPRAParser.INTEGER_TYPES.add(
      JPRAParser.INTEGER_UNSIGNED_NORMALIZED);

    SIZE_FUNCTIONS = new HashSet<>(16);
    JPRAParser.SIZE_FUNCTIONS.add(JPRAParser.SIZE_IN_OCTETS);
    JPRAParser.SIZE_FUNCTIONS.add(JPRAParser.SIZE_IN_BITS);

    LOG = LoggerFactory.getLogger(JPRAParser.class);
  }

  private final JSXSerializerType       serial;
  private final JPRAReferenceParserType ref_parser;

  private JPRAParser(
    final JSXSerializerType in_serial,
    final JPRAReferenceParserType in_ref_parser)
  {
    this.serial = NullCheck.notNull(in_serial);
    this.ref_parser = NullCheck.notNull(in_ref_parser);
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

        @Override public SExpressionSymbolType quotedString(
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
    if (!JPRAParser.KEYWORDS.contains(se.getText())) {
      final StringBuilder sb = new StringBuilder(256);
      sb.append("Unrecognized keyword '");
      sb.append(se.getText());
      sb.append("'");
      sb.append(System.lineSeparator());
      sb.append("Expected one of: ");
      sb.append(JPRAParser.KEYWORDS);
      sb.append(System.lineSeparator());
      throw JPRACompilerParseException.unrecognizedKeyword(se, sb.toString());
    }
  }

  private static void checkType(final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    if (!JPRAParser.TYPES.contains(se.getText())) {
      final StringBuilder sb = new StringBuilder(256);
      sb.append("Unrecognized type keyword '");
      sb.append(se.getText());
      sb.append("'");
      sb.append(System.lineSeparator());
      sb.append("Expected one of: ");
      sb.append(JPRAParser.TYPES);
      sb.append(System.lineSeparator());
      throw JPRACompilerParseException.unrecognizedTypeKeyword(
        se, sb.toString());
    }
  }

  private static Optional<ImmutableLexicalPositionType<Path>>
  getExpressionLexical(final SExpressionType q)
  {
    final Optional<LexicalPositionType<Path>> lex = q.getLexicalInformation();
    return lex.map(ImmutableLexicalPosition::newFrom);
  }

  private static void checkIntegerTypeKeyword(final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    if (!JPRAParser.INTEGER_TYPES.contains(se.getText())) {
      final StringBuilder sb = new StringBuilder(256);
      sb.append("Unrecognized integer type keyword '");
      sb.append(se.getText());
      sb.append("'");
      sb.append(System.lineSeparator());
      sb.append("Expected one of: ");
      sb.append(JPRAParser.INTEGER_TYPES);
      sb.append(System.lineSeparator());
      throw JPRACompilerParseException.unrecognizedIntegerTypeKeyword(
        se, sb.toString());
    }
  }

  private static void checkSizeFunction(final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    if (!JPRAParser.SIZE_FUNCTIONS.contains(se.getText())) {
      final StringBuilder sb = new StringBuilder(256);
      sb.append("Unrecognized size function '");
      sb.append(se.getText());
      sb.append("'");
      sb.append(System.lineSeparator());
      sb.append("Expected one of: ");
      sb.append(JPRAParser.SIZE_FUNCTIONS);
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
          final Map<FieldName, Parsed> names = new LinkedHashMap<>(e.size());

          for (int index = 0; index < e.size(); ++index) {
            final SExpressionType ei = e.get(index);
            final SExpressionSymbolType si = JPRAParser.requireSymbol(ei);

            final FieldName name;
            try {
              name = new FieldName(
                JPRAParser.getExpressionLexical(si), si.getText());
            } catch (final RequireViolation x) {
              throw JPRACompilerParseException.badFieldName(si, x.getMessage());
            }

            if (!names.containsKey(name)) {
              names.put(name, Parsed.get());
            } else {
              final StringBuilder sb = new StringBuilder(128);
              sb.append("Duplicate field name.");
              sb.append(System.lineSeparator());
              sb.append("Name: ");
              sb.append(name);
              sb.append(System.lineSeparator());
              sb.append("Fields: ");
              sb.append(names.keySet());
              throw JPRACompilerParseException.duplicateFieldName(
                si, sb.toString());
            }
          }

          final FastList<FieldName> rx = new FastList<>(names.size());
          rx.addAll(names.keySet());
          return rx.toImmutable();
        }

        @Override public ImmutableList<FieldName> quotedString(
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
    final String text = name.getText();
    Assertive.require(!text.isEmpty());
    final String[] segments = text.split("\\.");

    final Optional<ImmutableLexicalPositionType<Path>> ilex =
      JPRAParser.getExpressionLexical(name);

    final MutableList<PackageNameUnqualified> names_base = new FastList<>();
    for (int index = 0; index < segments.length; ++index) {
      final String raw = segments[index];
      try {
        names_base.add(new PackageNameUnqualified(ilex, raw));
      } catch (final RequireViolation e) {
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
        JPRAParser.getExpressionLexical(s), s.getText());
    } catch (final RequireViolation e) {
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
        @Override public SExpressionListType list(final SExpressionListType le)
          throws JPRACompilerParseException
        {
          return le;
        }

        @Override public SExpressionListType quotedString(
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
        JPRAParser.getExpressionLexical(name), name.getText());
    } catch (final RequireViolation e) {
      throw JPRACompilerParseException.badTypeName(name, e.getMessage());
    }
  }

  private static void checkRecordFieldKeyword(final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    if (!JPRAParser.RECORD_FIELD_KEYWORDS.contains(se.getText())) {
      final StringBuilder sb = new StringBuilder(256);
      sb.append("Unrecognized record field keyword '");
      sb.append(se.getText());
      sb.append("'");
      sb.append(System.lineSeparator());
      sb.append("Expected one of: ");
      sb.append(JPRAParser.RECORD_FIELD_KEYWORDS);
      sb.append(System.lineSeparator());
      throw JPRACompilerParseException.unrecognizedRecordFieldKeyword(
        se, sb.toString());
    }
  }

  private static FieldName parseFieldName(final SExpressionSymbolType name)
    throws JPRACompilerParseException
  {
    try {
      return new FieldName(
        JPRAParser.getExpressionLexical(name), name.getText());
    } catch (final RequireViolation e) {
      throw JPRACompilerParseException.badFieldName(name, e.getMessage());
    }
  }

  private StatementPackageImport<Parsed> parsePackageImport(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Assertive.require(JPRAParser.IMPORT.equals(se.getText()));

    if (le.size() == 4) {
      final SExpressionType q_name = le.get(1);
      final SExpressionType as = le.get(2);
      final SExpressionType u_name = le.get(3);

      if (q_name instanceof SExpressionSymbolType
          && u_name instanceof SExpressionSymbolType
          && as instanceof SExpressionSymbolType
          && "as".equals(((SExpressionSymbolType) as).getText())) {
        final SExpressionSymbolType q_sym = (SExpressionSymbolType) q_name;
        final SExpressionSymbolType u_sym = (SExpressionSymbolType) u_name;

        final PackageNameQualified p_name =
          JPRAParser.parsePackageNameQualified(q_sym);
        final PackageNameUnqualified up_name =
          JPRAParser.parsePackageNameUnqualified(u_sym);

        return new StatementPackageImport<>(Parsed.get(), p_name, up_name);
      }
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(le, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append(
        "Expected: (import <package-name-qualified> as "
        + "<package-name-unqualified)");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(le, sb.toString());
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  @Override public StatementType<Parsed> parseStatement(
    final SExpressionType expr)
    throws JPRACompilerParseException
  {
    NullCheck.notNull(expr);

    final SExpressionListType le = JPRAParser.requireList(expr);

    if (le.isEmpty()) {
      throw JPRACompilerParseException.expectedNonEmptyList(le);
    }

    final SExpressionSymbolType se = JPRAParser.requireSymbol(le.get(0));
    JPRAParser.checkKeyword(se);

    switch (se.getText()) {
      case JPRAParser.PACKAGE_BEGIN:
        return this.parsePackageBegin(le, se);
      case JPRAParser.PACKAGE_END:
        return this.parsePackageEnd(le, se);
      case JPRAParser.IMPORT:
        return this.parsePackageImport(le, se);
      case JPRAParser.RECORD:
        return this.parseRecord(le, se);
    }

    throw new UnreachableCodeException();
  }

  private StatementType<Parsed> parseRecord(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Assertive.require(JPRAParser.RECORD.equals(se.getText()));

    if (le.size() == 3) {
      final SExpressionType n_expr = le.get(1);
      final SExpressionType f_expr = le.get(2);

      if (n_expr instanceof SExpressionSymbolType
          && f_expr instanceof SExpressionListType) {

        final TypeName t_name =
          JPRAParser.parseTypeName((SExpressionSymbolType) n_expr);
        final SExpressionListType fl_expr = (SExpressionListType) f_expr;

        final MutableMap<FieldName, RecordFieldDeclValue<Parsed>>
          fields_by_name = Maps.mutable.empty();
        final MutableList<RecordFieldDeclType<Parsed>> fields_ordered =
          Lists.mutable.empty();

        this.parseRecordFields(fl_expr, fields_by_name, fields_ordered);

        return new TypeDeclRecord<>(
          Parsed.get(),
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
      sb.append("Expected: (record <type-name> (<field> ... <field>))");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(le, sb.toString());
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  private void parseRecordFields(
    final SExpressionListType fields,
    final MutableMap<FieldName, RecordFieldDeclValue<Parsed>> fields_named,
    final MutableList<RecordFieldDeclType<Parsed>> fields_order)
    throws JPRACompilerParseException
  {
    for (int index = 0; index < fields.size(); ++index) {
      final SExpressionListType l_expr =
        JPRAParser.requireList(fields.get(index));
      if (l_expr.isEmpty()) {
        throw JPRACompilerParseException.expectedNonEmptyList(l_expr);
      }

      final SExpressionSymbolType k = JPRAParser.requireSymbol(l_expr.get(0));
      JPRAParser.checkRecordFieldKeyword(k);

      final int e_count = l_expr.size();
      switch (k.getText()) {
        case JPRAParser.FIELD: {
          final RecordFieldDeclValue<Parsed> f =
            this.parseRecordFieldValue(l_expr, e_count);

          final FieldName f_name = f.getName();
          if (fields_named.containsKey(f_name)) {
            final StringBuilder sb = new StringBuilder(128);
            sb.append("Duplicate field name.");
            sb.append(System.lineSeparator());
            sb.append("Name: ");
            sb.append(f_name.getValue());
            throw JPRACompilerParseException.duplicateFieldName(
              k, sb.toString());
          }

          fields_named.put(f_name, f);
          fields_order.add(f);
          continue;
        }
        case JPRAParser.PADDING_OCTETS: {
          final RecordFieldDeclPaddingOctets<Parsed> f =
            this.parseRecordPaddingOctets(l_expr, e_count);

          fields_order.add(f);
          continue;
        }
      }

      throw new UnreachableCodeException();
    }
  }

  private RecordFieldDeclPaddingOctets<Parsed> parseRecordPaddingOctets(
    final SExpressionListType l_expr,
    final int e_count)
    throws JPRACompilerParseException
  {
    if (e_count == 2) {
      final SizeExprType<Parsed> s = this.parseSizeExpression(l_expr.get(1));
      return new RecordFieldDeclPaddingOctets<>(
        JPRAParser.getExpressionLexical(l_expr), s);
    } else {
      try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
        this.serial.serialize(l_expr, bao);
        final StringBuilder sb = new StringBuilder(128);
        sb.append("Syntax error.");
        sb.append(System.lineSeparator());
        sb.append("Expected: (padding-octets <size-expression>)");
        sb.append(System.lineSeparator());
        sb.append("Got: ");
        sb.append(bao.toString(StandardCharsets.UTF_8.name()));
        throw JPRACompilerParseException.syntaxError(l_expr, sb.toString());
      } catch (final IOException x) {
        throw new UnreachableCodeException(x);
      }
    }
  }

  private RecordFieldDeclValue<Parsed> parseRecordFieldValue(
    final SExpressionListType l_expr,
    final int e_count)
    throws JPRACompilerParseException
  {
    if (e_count == 3 && l_expr.get(1) instanceof SExpressionSymbolType) {
      final SExpressionSymbolType f_name =
        (SExpressionSymbolType) l_expr.get(1);
      final FieldName name = JPRAParser.parseFieldName(f_name);
      final TypeExprType<Parsed> te = this.parseTypeExpression(l_expr.get(2));
      return new RecordFieldDeclValue<>(name, te);
    } else {
      try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
        this.serial.serialize(l_expr, bao);
        final StringBuilder sb = new StringBuilder(128);
        sb.append("Syntax error.");
        sb.append(System.lineSeparator());
        sb.append("Expected: (field <field-name> <type-expression>)");
        sb.append(System.lineSeparator());
        sb.append("Got: ");
        sb.append(bao.toString(StandardCharsets.UTF_8.name()));
        throw JPRACompilerParseException.syntaxError(l_expr, sb.toString());
      } catch (final IOException x) {
        throw new UnreachableCodeException(x);
      }
    }
  }

  private StatementPackageEnd<Parsed> parsePackageEnd(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Assertive.require(JPRAParser.PACKAGE_END.equals(se.getText()));

    if (le.size() == 1) {
      return new StatementPackageEnd<>(
        Parsed.get(), JPRAParser.getExpressionLexical(se));
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(le, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("Expected: (package-end)");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(le, sb.toString());
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  private StatementPackageBegin<Parsed> parsePackageBegin(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Assertive.require(JPRAParser.PACKAGE_BEGIN.equals(se.getText()));

    if (le.size() == 2) {
      final SExpressionType e_name = le.get(1);
      if (e_name instanceof SExpressionSymbolType) {
        final SExpressionSymbolType name = (SExpressionSymbolType) e_name;
        final PackageNameQualified p_name =
          JPRAParser.parsePackageNameQualified(name);
        return new StatementPackageBegin<>(Parsed.get(), p_name);
      }
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(le, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("Expected: (package-begin <package-name-qualified>)");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(le, sb.toString());
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  @Override public TypeExprType<Parsed> parseTypeExpression(
    final SExpressionType expr)
    throws JPRACompilerParseException
  {
    NullCheck.notNull(expr);

    return expr.matchExpression(
      new SExpressionMatcherType<TypeExprType<Parsed>,
        JPRACompilerParseException>()
      {
        @Override public TypeExprType<Parsed> list(
          final SExpressionListType le)
          throws JPRACompilerParseException
        {
          if (le.isEmpty()) {
            throw JPRACompilerParseException.expectedNonEmptyList(le);
          }

          final SExpressionSymbolType se = JPRAParser.requireSymbol(le.get(0));
          JPRAParser.checkType(se);

          switch (se.getText()) {
            case JPRAParser.INTEGER: {
              return JPRAParser.this.parseTypeInteger(le, se);
            }
            case JPRAParser.FLOAT: {
              return JPRAParser.this.parseTypeFloat(le, se);
            }
            case JPRAParser.VECTOR: {
              return JPRAParser.this.parseTypeVector(le, se);
            }
            case JPRAParser.MATRIX: {
              return JPRAParser.this.parseTypeMatrix(le, se);
            }
            case JPRAParser.ARRAY: {
              return JPRAParser.this.parseTypeArray(le, se);
            }
            case JPRAParser.STRING: {
              return JPRAParser.this.parseTypeString(le, se);
            }
            case JPRAParser.BOOLEAN_SET: {
              return JPRAParser.this.parseTypeBooleanSet(le, se);
            }
          }

          throw new UnreachableCodeException();
        }

        @Override public TypeExprType<Parsed> quotedString(
          final SExpressionQuotedStringType qe)
          throws JPRACompilerParseException
        {
          throw JPRACompilerParseException.expectedSymbolOrListGotQuotedString(
            qe);
        }

        @Override public TypeExprType<Parsed> symbol(
          final SExpressionSymbolType se)
          throws JPRACompilerParseException
        {
          return JPRAParser.this.parseTypeReference(se);
        }
      });
  }

  private TypeExprType<Parsed> parseTypeReference(
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    final Optional<ImmutableLexicalPositionType<Path>> lex =
      JPRAParser.getExpressionLexical(se);
    return new TypeExprName<>(
      Parsed.get(), this.ref_parser.parseTypeReference(se));
  }

  private TypeExprType<Parsed> parseTypeBooleanSet(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Assertive.require(JPRAParser.BOOLEAN_SET.equals(se.getText()));

    if (le.size() == 3) {
      final SExpressionType s_expr = le.get(1);
      final SExpressionType f_expr = le.get(2);
      final SizeExprType<Parsed> size = this.parseSizeExpression(s_expr);

      final ImmutableList<FieldName> fields = JPRAParser.parseFieldSet(f_expr);
      final Optional<ImmutableLexicalPositionType<Path>> lex =
        JPRAParser.getExpressionLexical(s_expr);
      return new TypeExprBooleanSet<>(Parsed.get(), lex, fields, size);
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(se, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append(
        "Expected: (boolean-set <size-expression> (<field> ... <field>))");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(se, sb.toString());
    } catch (final IOException x) {
      throw new UnreachableCodeException(x);
    }

  }

  private TypeExprType<Parsed> parseTypeString(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Assertive.require(JPRAParser.STRING.equals(se.getText()));

    if (le.size() == 3) {
      final SExpressionType s_expr = le.get(1);
      final SExpressionType e_expr = le.get(2);

      if (e_expr instanceof SExpressionQuotedStringType) {
        final SExpressionQuotedStringType qe =
          (SExpressionQuotedStringType) e_expr;
        final SizeExprType<Parsed> size = this.parseSizeExpression(s_expr);
        return new TypeExprString<>(
          Parsed.get(),
          JPRAParser.getExpressionLexical(le),
          size,
          qe.getText());
      }
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(se, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("Expected: (string <size-expression> \"<encoding>\")");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(se, sb.toString());
    } catch (final IOException x) {
      throw new UnreachableCodeException(x);
    }

  }

  private TypeExprType<Parsed> parseTypeArray(

    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Assertive.require(JPRAParser.ARRAY.equals(se.getText()));

    if (le.size() == 3) {
      final SExpressionType t_expr = le.get(1);
      final SExpressionType s_expr = le.get(2);
      final SizeExprType<Parsed> size = this.parseSizeExpression(s_expr);
      final TypeExprType<Parsed> type = this.parseTypeExpression(t_expr);
      return new TypeExprArray<>(
        Parsed.get(), JPRAParser.getExpressionLexical(le), size, type);
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(se, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("Expected: (array <type-expression> <size-expression>)");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(se, sb.toString());
    } catch (final IOException x) {
      throw new UnreachableCodeException(x);
    }

  }

  private TypeExprType<Parsed> parseTypeMatrix(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Assertive.require(JPRAParser.MATRIX.equals(se.getText()));

    if (le.size() == 4) {
      final SExpressionType t_expr = le.get(1);
      final SExpressionType w_expr = le.get(2);
      final SExpressionType h_expr = le.get(3);

      final SizeExprType<Parsed> width = this.parseSizeExpression(w_expr);
      final SizeExprType<Parsed> height = this.parseSizeExpression(h_expr);
      final TypeExprType<Parsed> type = this.parseTypeExpression(t_expr);

      return new TypeExprMatrix<>(
        Parsed.get(), JPRAParser.getExpressionLexical(le), width, height, type);
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(se, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("Expected: (matrix <scalar-type-expression> <width> <height>)");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(se, sb.toString());
    } catch (final IOException x) {
      throw new UnreachableCodeException(x);
    }

  }

  private TypeExprType<Parsed> parseTypeVector(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Assertive.require(JPRAParser.VECTOR.equals(se.getText()));

    if (le.size() == 3) {
      final Optional<LexicalPositionType<Path>> lex =
        le.getLexicalInformation();
      final SExpressionType t_expr = le.get(1);
      final SExpressionType s_expr = le.get(2);
      return new TypeExprVector<>(
        Parsed.get(),
        lex.map(ImmutableLexicalPosition::newFrom),
        this.parseSizeExpression(s_expr),
        this.parseTypeExpression(t_expr));
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(se, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("Expected: (vector <type-expression> <size-expression>)");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(se, sb.toString());
    } catch (final IOException x) {
      throw new UnreachableCodeException(x);
    }
  }

  private TypeExprType<Parsed> parseTypeFloat(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Assertive.require(JPRAParser.FLOAT.equals(se.getText()));

    if (le.size() == 2) {
      final SExpressionType s_expr = le.get(1);
      return new TypeExprFloat<>(
        Parsed.get(),
        JPRAParser.getExpressionLexical(s_expr),
        this.parseSizeExpression(s_expr));
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(le, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("Expected: (float <size-in-bits>)");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(le, sb.toString());
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  private TypeExprType<Parsed> parseTypeInteger(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Assertive.require(JPRAParser.INTEGER.equals(se.getText()));

    if (le.size() == 3) {
      final SExpressionType t_expr = le.get(1);
      final SExpressionType s_expr = le.get(2);
      if (t_expr instanceof SExpressionSymbolType) {
        final SExpressionSymbolType t_name = (SExpressionSymbolType) t_expr;

        JPRAParser.checkIntegerTypeKeyword(t_name);

        final SizeExprType<Parsed> size = this.parseSizeExpression(s_expr);

        switch (t_name.getText()) {
          case JPRAParser.INTEGER_SIGNED: {
            return new TypeExprIntegerSigned<>(
              Parsed.get(), JPRAParser.getExpressionLexical(s_expr), size);
          }
          case JPRAParser.INTEGER_UNSIGNED: {
            return new TypeExprIntegerUnsigned<>(
              Parsed.get(), JPRAParser.getExpressionLexical(s_expr), size);
          }
          case JPRAParser.INTEGER_SIGNED_NORMALIZED: {
            return new TypeExprIntegerSignedNormalized<>(
              Parsed.get(), JPRAParser.getExpressionLexical(s_expr), size);
          }
          case JPRAParser.INTEGER_UNSIGNED_NORMALIZED: {
            return new TypeExprIntegerUnsignedNormalized<>(
              Parsed.get(), JPRAParser.getExpressionLexical(s_expr), size);
          }
        }

        throw new UnreachableCodeException();
      }
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(le, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("Expected: (integer <integer-type> <size-in-bits>)");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(le, sb.toString());
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  @Override public SizeExprType<Parsed> parseSizeExpression(
    final SExpressionType e)
    throws JPRACompilerParseException
  {
    return e.matchExpression(
      new SExpressionMatcherType<SizeExprType<Parsed>,
        JPRACompilerParseException>()
      {
        @Override public SizeExprType<Parsed> list(final SExpressionListType le)
          throws JPRACompilerParseException
        {
          if (le.isEmpty()) {
            throw JPRACompilerParseException.expectedNonEmptyList(le);
          }

          final SExpressionSymbolType se = JPRAParser.requireSymbol(le.get(0));
          JPRAParser.checkSizeFunction(se);

          switch (se.getText()) {
            case JPRAParser.SIZE_IN_BITS: {
              return JPRAParser.this.parseSizeInBits(le, se);
            }
            case JPRAParser.SIZE_IN_OCTETS: {
              return JPRAParser.this.parseSizeInOctets(le, se);
            }
          }

          throw new UnreachableCodeException();
        }

        @Override public SizeExprType<Parsed> quotedString(
          final SExpressionQuotedStringType qe)
          throws JPRACompilerParseException
        {
          throw JPRACompilerParseException.expectedSymbolOrListGotQuotedString(
            qe);
        }

        @Override public SizeExprType<Parsed> symbol(
          final SExpressionSymbolType se)
          throws JPRACompilerParseException
        {
          try {
            return new SizeExprConstant<>(
              Parsed.get(),
              JPRAParser.getExpressionLexical(se),
              new BigInteger(se.getText()));
          } catch (final NumberFormatException x) {
            throw JPRACompilerParseException.invalidIntegerConstant(se);
          }
        }
      });
  }

  private SizeExprType<Parsed> parseSizeInBits(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Assertive.require(JPRAParser.SIZE_IN_BITS.equals(se.getText()));

    if (le.size() == 2) {
      return new SizeExprInBits<>(
        Parsed.get(), this.parseTypeExpression(le.get(1)));
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(le, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("Expected: (size-in-bits <type-expression>)");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(le, sb.toString());
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  private SizeExprType<Parsed> parseSizeInOctets(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Assertive.require(JPRAParser.SIZE_IN_OCTETS.equals(se.getText()));

    if (le.size() == 2) {
      return new SizeExprInOctets<>(
        Parsed.get(), this.parseTypeExpression(le.get(1)));
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(le, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("Expected: (size-in-octets <type-expression>)");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(le, sb.toString());
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

}
