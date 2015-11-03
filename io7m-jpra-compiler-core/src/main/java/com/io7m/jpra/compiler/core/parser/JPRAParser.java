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
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.impl.factory.Sets;
import com.gs.collections.impl.list.mutable.FastList;
import com.io7m.jfunctional.Unit;
import com.io7m.jlexing.core.ImmutableLexicalPosition;
import com.io7m.jlexing.core.ImmutableLexicalPositionType;
import com.io7m.jlexing.core.LexicalPositionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.compiler.core.JPRACompilerException;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.names.PackageNameQualified;
import com.io7m.jpra.model.names.PackageNameUnqualified;
import com.io7m.jpra.model.SizeUnitOctetsType;
import com.io7m.jpra.model.SizeUnitType;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.jpra.model.type_expressions.TypeExprArray;
import com.io7m.jpra.model.type_expressions.TypeExprBooleanSet;
import com.io7m.jpra.model.type_expressions.TypeExprFloat;
import com.io7m.jpra.model.type_expressions.TypeExprIntegerSigned;
import com.io7m.jpra.model.type_expressions.TypeExprIntegerSignedNormalized;
import com.io7m.jpra.model.type_expressions.TypeExprIntegerType;
import com.io7m.jpra.model.type_expressions.TypeExprIntegerUnsigned;
import com.io7m.jpra.model.type_expressions.TypeExprIntegerUnsignedNormalized;
import com.io7m.jpra.model.type_expressions.TypeExprName;
import com.io7m.jpra.model.type_expressions.TypeExprScalarType;
import com.io7m.jpra.model.type_expressions.TypeExprString;
import com.io7m.jpra.model.type_expressions.TypeExprType;
import com.io7m.jpra.model.type_expressions.TypeExprVector;
import com.io7m.jsx.SExpressionListType;
import com.io7m.jsx.SExpressionMatcherType;
import com.io7m.jsx.SExpressionQuotedStringType;
import com.io7m.jsx.SExpressionSymbolType;
import com.io7m.jsx.SExpressionType;
import com.io7m.jsx.serializer.JSXSerializerTrivial;
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

  private final JSXSerializerType                            serial;
  private       Optional<ImmutableLexicalPositionType<Path>> current_position;

  private JPRAParser()
  {
    this.serial = JSXSerializerTrivial.newSerializer();
    this.current_position = Optional.empty();
  }

  private static SExpressionSymbolType requireSymbol(final SExpressionType e)
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

  private static PackageNameQualified onPackageNameQualified(
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

  private static PackageNameUnqualified onPackageNameUnqualified(
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

  private static Optional<ImmutableLexicalPositionType<Path>>
  getExpressionLexical(final SExpressionType q)
  {
    final Optional<LexicalPositionType<Path>> lex = q.getLexicalInformation();
    return lex.map(ImmutableLexicalPosition::newFrom);
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

  private static TypeExprName onTypeReference(
    final JSXSerializerType serial,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    return JPRATypeReferenceNameParser.onName(serial, se);
  }

  private static ImmutableList<FieldName> onFieldSet(
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
          final Map<FieldName, Unit> names = new LinkedHashMap<>(e.size());

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

  private static TypeName onTypeName(final SExpressionSymbolType name)
    throws JPRACompilerParseException
  {
    try {
      return new TypeName(
        JPRAParser.getExpressionLexical(name), name.getText());
    } catch (final RequireViolation e) {
      throw JPRACompilerParseException.badTypeName(name, e.getMessage());
    }
  }

  private static FieldName onFieldName(final SExpressionSymbolType name)
    throws JPRACompilerParseException
  {
    try {
      return new FieldName(
        JPRAParser.getExpressionLexical(name), name.getText());
    } catch (final RequireViolation e) {
      throw JPRACompilerParseException.badFieldName(name, e.getMessage());
    }
  }

  /**
   * @return A new statement parser
   */

  public static JPRAParserType newParser()
  {
    return new JPRAParser();
  }

  private void onPackageBegin(
    final JPRAParserEventListenerType listener,
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerException
  {
    Assertive.require(JPRAParser.PACKAGE_BEGIN.equals(se.getText()));

    if (le.size() == 2) {
      final SExpressionType e_name = le.get(1);
      if (e_name instanceof SExpressionSymbolType) {
        final SExpressionSymbolType name = (SExpressionSymbolType) e_name;
        final PackageNameQualified p_name =
          JPRAParser.onPackageNameQualified(name);

        this.updatePosition(le);
        listener.onPackageBegin(this, p_name);
        return;
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

  private void onPackageEnd(
    final JPRAParserEventListenerType listener,
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerException
  {
    Assertive.require(JPRAParser.PACKAGE_END.equals(se.getText()));

    if (le.size() == 1) {
      this.updatePosition(le);
      listener.onPackageEnd(this);
      return;
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

  private void updatePosition(final SExpressionType se)
  {
    this.current_position =
      se.getLexicalInformation().map(ImmutableLexicalPosition::newFrom);
  }

  private void onImport(
    final JPRAParserEventListenerType listener,
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerException
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
          JPRAParser.onPackageNameQualified(q_sym);
        final PackageNameUnqualified up_name =
          JPRAParser.onPackageNameUnqualified(u_sym);

        this.updatePosition(le);
        listener.onImport(this, p_name, up_name);
        return;
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

  private <T extends SizeUnitType> SizeExprType<T> onSizeInBits(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Assertive.require(JPRAParser.SIZE_IN_BITS.equals(se.getText()));

    if (le.size() == 2) {
      final SizeExprInBits v = new SizeExprInBits(
        this.onTypeExpression(le.get(1)));
      return (SizeExprType<T>) v;
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

  private <T extends SizeUnitType> SizeExprType<T> onSizeInOctets(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Assertive.require(JPRAParser.SIZE_IN_OCTETS.equals(se.getText()));

    if (le.size() == 2) {
      final SizeExprInOctets v = new SizeExprInOctets(
        this.onTypeExpression(le.get(1)));
      return (SizeExprType<T>) v;
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

  private TypeExprIntegerType onTypeInteger(
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

        switch (t_name.getText()) {
          case JPRAParser.INTEGER_SIGNED: {
            return new TypeExprIntegerSigned(
              JPRAParser.getExpressionLexical(s_expr),
              this.onSizeExpression(s_expr));
          }
          case JPRAParser.INTEGER_UNSIGNED: {
            return new TypeExprIntegerUnsigned(
              JPRAParser.getExpressionLexical(s_expr),
              this.onSizeExpression(s_expr));
          }
          case JPRAParser.INTEGER_SIGNED_NORMALIZED: {
            return new TypeExprIntegerSignedNormalized(
              JPRAParser.getExpressionLexical(s_expr),
              this.onSizeExpression(s_expr));
          }
          case JPRAParser.INTEGER_UNSIGNED_NORMALIZED: {
            return new TypeExprIntegerUnsignedNormalized(
              JPRAParser.getExpressionLexical(s_expr),
              this.onSizeExpression(s_expr));
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

  private TypeExprType onTypeExpression(
    final SExpressionType e)
    throws JPRACompilerParseException
  {
    NullCheck.notNull(e);

    return e.matchExpression(
      new SExpressionMatcherType<TypeExprType, JPRACompilerParseException>()
      {
        @Override public TypeExprType list(final SExpressionListType le)
          throws JPRACompilerParseException
        {
          if (le.isEmpty()) {
            throw JPRACompilerParseException.expectedNonEmptyList(le);
          }

          final SExpressionSymbolType se = JPRAParser.requireSymbol(le.get(0));
          JPRAParser.checkType(se);

          switch (se.getText()) {
            case JPRAParser.INTEGER: {
              return JPRAParser.this.onTypeInteger(le, se);
            }
            case JPRAParser.FLOAT: {
              return JPRAParser.this.onTypeFloat(le, se);
            }
            case JPRAParser.VECTOR: {
              return JPRAParser.this.onTypeVector(le, se);
            }
            case JPRAParser.MATRIX: {
              return JPRAParser.this.onTypeMatrix(le, se);
            }
            case JPRAParser.ARRAY: {
              return JPRAParser.this.onTypeArray(le, se);
            }
            case JPRAParser.STRING: {
              return JPRAParser.this.onTypeString(le, se);
            }
            case JPRAParser.BOOLEAN_SET: {
              return JPRAParser.this.onTypeBooleanSet(le, se);
            }
          }

          throw new UnreachableCodeException();
        }

        @Override
        public TypeExprType quotedString(final SExpressionQuotedStringType qe)
          throws JPRACompilerParseException
        {
          throw JPRACompilerParseException.expectedSymbolOrListGotQuotedString(
            qe);
        }

        @Override public TypeExprType symbol(final SExpressionSymbolType se)
          throws JPRACompilerParseException
        {
          return JPRAParser.onTypeReference(JPRAParser.this.serial, se);
        }
      });
  }

  private TypeExprVector onTypeVector(
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
      final SizeExprType<?> size = this.onSizeExpression(s_expr);
      final TypeExprScalarType type = this.onTypeExpressionScalar(t_expr);
      return new TypeExprVector(
        lex.map(ImmutableLexicalPosition::newFrom), size, type);
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(se, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append(
        "Expected: (vector <scalar-type-expression> <size-expression>)");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(se, sb.toString());
    } catch (final IOException x) {
      throw new UnreachableCodeException(x);
    }
  }

  private TypeExprBooleanSet onTypeBooleanSet(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Assertive.require(JPRAParser.BOOLEAN_SET.equals(se.getText()));

    if (le.size() == 3) {
      final SExpressionType s_expr = le.get(1);
      final SExpressionType f_expr = le.get(2);
      final SizeExprType<SizeUnitOctetsType> size =
        this.onSizeExpression(s_expr);

      final ImmutableList<FieldName> fields = JPRAParser.onFieldSet(f_expr);
      return TypeExprBooleanSet.newSet(fields, size);
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

  private TypeExprArray onTypeArray(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Assertive.require(JPRAParser.ARRAY.equals(se.getText()));

    if (le.size() == 3) {
      final SExpressionType t_expr = le.get(1);
      final SExpressionType s_expr = le.get(2);
      final SizeExprType<?> size = this.onSizeExpression(s_expr);
      final TypeExprType type = this.onTypeExpression(t_expr);
      return new TypeExprArray(
        JPRAParser.getExpressionLexical(le), size, type);
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

  private TypeExprString onTypeString(
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
        final SizeExprType<SizeUnitOctetsType> size =
          this.onSizeExpression(s_expr);
        return new TypeExprString(
          JPRAParser.getExpressionLexical(le), qe.getText(), size);
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

  private TypeExprMatrix onTypeMatrix(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Assertive.require(JPRAParser.MATRIX.equals(se.getText()));

    if (le.size() == 4) {
      final SExpressionType t_expr = le.get(1);
      final SExpressionType w_expr = le.get(2);
      final SExpressionType h_expr = le.get(3);

      final SizeExprType<?> width = this.onSizeExpression(w_expr);
      final SizeExprType<?> height = this.onSizeExpression(h_expr);

      final TypeExprScalarType type = this.onTypeExpressionScalar(t_expr);
      return new TypeExprMatrix(
        JPRAParser.getExpressionLexical(le), width, height, type);
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

  private TypeExprScalarType onTypeExpressionScalar(
    final SExpressionType e)
    throws JPRACompilerParseException
  {
    final TypeExprType r = this.onTypeExpression(e);
    if (r instanceof TypeExprScalarType) {
      return (TypeExprScalarType) r;
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(e, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Expected a scalar type expression.");
      sb.append(System.lineSeparator());
      sb.append("Expected: <scalar-type-expression>");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.expectedScalarTypeExpression(
        e, sb.toString());
    } catch (final IOException x) {
      throw new UnreachableCodeException(x);
    }
  }

  private TypeExprFloat onTypeFloat(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Assertive.require(JPRAParser.FLOAT.equals(se.getText()));

    if (le.size() == 2) {
      final SExpressionType s_expr = le.get(1);
      return new TypeExprFloat(
        JPRAParser.getExpressionLexical(s_expr), this.onSizeExpression(s_expr));
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

  private <T extends SizeUnitType> SizeExprType<T> onSizeExpression(
    final SExpressionType e)
    throws JPRACompilerParseException
  {
    return e.matchExpression(
      new SExpressionMatcherType<SizeExprType<T>, JPRACompilerParseException>()
      {
        @Override public SizeExprType<T> list(final SExpressionListType le)
          throws JPRACompilerParseException
        {
          if (le.isEmpty()) {
            throw JPRACompilerParseException.expectedNonEmptyList(le);
          }

          final SExpressionSymbolType se = JPRAParser.requireSymbol(le.get(0));
          JPRAParser.checkSizeFunction(se);

          switch (se.getText()) {
            case JPRAParser.SIZE_IN_BITS: {
              return JPRAParser.this.onSizeInBits(le, se);
            }
            case JPRAParser.SIZE_IN_OCTETS: {
              return JPRAParser.this.onSizeInOctets(le, se);
            }
          }

          throw new UnreachableCodeException();
        }

        @Override public SizeExprType<T> quotedString(
          final SExpressionQuotedStringType qe)
          throws JPRACompilerParseException
        {
          throw JPRACompilerParseException.expectedSymbolOrListGotQuotedString(
            qe);
        }

        @Override public SizeExprType<T> symbol(final SExpressionSymbolType se)
          throws JPRACompilerParseException
        {
          try {
            return new SizeExprConstant<>(
              JPRAParser.getExpressionLexical(se),
              new BigInteger(se.getText()));
          } catch (final NumberFormatException x) {
            throw JPRACompilerParseException.invalidIntegerConstant(se);
          }
        }
      });
  }

  private void onRecord(
    final JPRAParserEventListenerType listener,
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerException
  {
    Assertive.require(JPRAParser.RECORD.equals(se.getText()));

    if (le.size() == 3) {
      final SExpressionType n_expr = le.get(1);
      final SExpressionType f_expr = le.get(2);

      if (n_expr instanceof SExpressionSymbolType
          && f_expr instanceof SExpressionListType) {
        final TypeName t_name =
          JPRAParser.onTypeName((SExpressionSymbolType) n_expr);
        final SExpressionListType fl_expr = (SExpressionListType) f_expr;

        this.updatePosition(le);
        listener.onRecordBegin(this, t_name);

        this.onRecordFields(listener, fl_expr);

        this.updatePosition(le);
        listener.onRecordEnd(this);
        return;
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

  private void onRecordFields(
    final JPRAParserEventListenerType listener,
    final SExpressionListType fields)
    throws JPRACompilerException
  {
    final MutableSet<FieldName> field_names = Sets.mutable.empty();

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
          this.onRecordFieldValue(listener, field_names, l_expr, e_count);
          continue;
        }
        case JPRAParser.PADDING_OCTETS: {
          this.onRecordPaddingOctets(listener, l_expr, e_count);
          continue;
        }
      }

      throw new UnreachableCodeException();
    }
  }

  private void onRecordPaddingOctets(
    final JPRAParserEventListenerType listener,
    final SExpressionListType l_expr,
    final int e_count)
    throws JPRACompilerException
  {
    if (e_count == 2) {
      this.updatePosition(l_expr);
      listener.onRecordFieldPaddingOctets(
        this, this.onSizeExpression(l_expr.get(1)));
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

  private void onRecordFieldValue(
    final JPRAParserEventListenerType listener,
    final Set<FieldName> fields_name,
    final SExpressionListType l_expr,
    final int e_count)
    throws JPRACompilerParseException
  {
    if (e_count == 3 && l_expr.get(1) instanceof SExpressionSymbolType) {
      final SExpressionSymbolType f_name =
        (SExpressionSymbolType) l_expr.get(1);
      final FieldName name = JPRAParser.onFieldName(f_name);
      if (!fields_name.contains(name)) {
        final TypeExprType te = this.onTypeExpression(l_expr.get(2));

        this.updatePosition(l_expr);
        listener.onRecordFieldValue(this, name, te);
        fields_name.add(name);
      } else {
        final StringBuilder sb = new StringBuilder(128);
        sb.append("Duplicate field name.");
        sb.append(System.lineSeparator());
        sb.append("Name: ");
        sb.append(name);
        throw JPRACompilerParseException.duplicateFieldName(
          f_name, sb.toString());
      }
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

  private void onCommandType(
    final JPRAParserREPLEventListenerType listener,
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Assertive.require(JPRAParser.COMMAND_TYPE.equals(se.getText()));

    if (le.size() == 2) {
      this.updatePosition(le);
      listener.onREPLType(this, this.onTypeExpression(le.get(1)));
      return;
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(le, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("Expected: (:type <type-expression>)");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(le, sb.toString());
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  private void onCommandSize(
    final JPRAParserREPLEventListenerType listener,
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Assertive.require(JPRAParser.COMMAND_SIZE.equals(se.getText()));

    if (le.size() == 2) {
      this.updatePosition(le);
      listener.onREPLSize(this, this.onSizeExpression(le.get(1)));
      return;
    }

    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      this.serial.serialize(le, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Syntax error.");
      sb.append(System.lineSeparator());
      sb.append("Expected: (:size <size-expression>)");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      throw JPRACompilerParseException.syntaxError(le, sb.toString());
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  @Override public void parseStatement(
    final SExpressionType e,
    final JPRAParserREPLEventListenerType listener)
    throws JPRACompilerParseException, JPRACompilerException
  {
    NullCheck.notNull(e);
    NullCheck.notNull(listener);

    e.matchExpression(
      new SExpressionMatcherType<Unit, JPRACompilerException>()
      {
        @Override public Unit list(final SExpressionListType le)
          throws JPRACompilerException
        {
          if (le.isEmpty()) {
            throw JPRACompilerParseException.expectedNonEmptyList(le);
          }

          final SExpressionSymbolType se = JPRAParser.requireSymbol(le.get(0));
          JPRAParser.checkKeyword(se);

          switch (se.getText()) {
            case JPRAParser.PACKAGE_BEGIN: {
              JPRAParser.this.onPackageBegin(listener, le, se);
              return Unit.unit();
            }
            case JPRAParser.IMPORT: {
              JPRAParser.this.onImport(listener, le, se);
              return Unit.unit();
            }
            case JPRAParser.PACKAGE_END: {
              JPRAParser.this.onPackageEnd(listener, le, se);
              return Unit.unit();
            }
            case JPRAParser.RECORD: {
              JPRAParser.this.onRecord(listener, le, se);
              return Unit.unit();
            }
            case JPRAParser.COMMAND_TYPE: {
              JPRAParser.this.onCommandType(listener, le, se);
              return Unit.unit();
            }
            case JPRAParser.COMMAND_SIZE: {
              JPRAParser.this.onCommandSize(listener, le, se);
              return Unit.unit();
            }
          }

          throw new UnreachableCodeException();
        }

        @Override public Unit quotedString(final SExpressionQuotedStringType qe)
          throws JPRACompilerParseException
        {
          throw JPRACompilerParseException.expectedListGotQuotedString(qe);
        }

        @Override public Unit symbol(final SExpressionSymbolType se)
          throws JPRACompilerParseException
        {
          throw JPRACompilerParseException.expectedListGotSymbol(se);
        }
      });
  }

  @Override
  public Optional<ImmutableLexicalPositionType<Path>> getParsingPosition()
  {
    return this.current_position;
  }
}
