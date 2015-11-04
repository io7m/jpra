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
import com.gs.collections.impl.list.mutable.FastList;
import com.io7m.jlexing.core.ImmutableLexicalPosition;
import com.io7m.jlexing.core.ImmutableLexicalPositionType;
import com.io7m.jlexing.core.LexicalPositionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.Untyped;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.names.FieldReference;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.jpra.model.names.TypeReference;
import com.io7m.jpra.model.size_expressions.SizeExprConstant;
import com.io7m.jpra.model.size_expressions.SizeExprInBits;
import com.io7m.jpra.model.size_expressions.SizeExprInOctets;
import com.io7m.jpra.model.size_expressions.SizeExprType;
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
          final Map<FieldName, Untyped> names = new LinkedHashMap<>(e.size());

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
              names.put(name, Untyped.get());
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

  @Override
  public TypeExprType<TypeName, TypeReference, FieldName, FieldReference,
    Untyped> parseTypeExpression(
    final SExpressionType expr)
    throws JPRACompilerParseException
  {
    NullCheck.notNull(expr);

    return expr.matchExpression(
      new SExpressionMatcherType<TypeExprType<TypeName, TypeReference,
        FieldName, FieldReference, Untyped>, JPRACompilerParseException>()
      {
        @Override
        public TypeExprType<TypeName, TypeReference, FieldName,
          FieldReference, Untyped> list(
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

        @Override
        public TypeExprType<TypeName, TypeReference, FieldName,
          FieldReference, Untyped> quotedString(
          final SExpressionQuotedStringType qe)
          throws JPRACompilerParseException
        {
          throw JPRACompilerParseException.expectedSymbolOrListGotQuotedString(
            qe);
        }

        @Override
        public TypeExprType<TypeName, TypeReference, FieldName,
          FieldReference, Untyped> symbol(
          final SExpressionSymbolType se)
          throws JPRACompilerParseException
        {
          return JPRAParser.this.parseTypeReference(se);
        }
      });
  }

  private TypeExprType<TypeName, TypeReference, FieldName, FieldReference,
    Untyped> parseTypeReference(
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    final Optional<ImmutableLexicalPositionType<Path>> lex =
      JPRAParser.getExpressionLexical(se);
    return new TypeExprName<>(
      lex, this.ref_parser.parseTypeReference(se), Untyped.get());
  }

  private TypeExprType<TypeName, TypeReference, FieldName, FieldReference,
    Untyped> parseTypeBooleanSet(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Assertive.require(JPRAParser.BOOLEAN_SET.equals(se.getText()));

    if (le.size() == 3) {
      final SExpressionType s_expr = le.get(1);
      final SExpressionType f_expr = le.get(2);
      final SizeExprType<TypeName, TypeReference, FieldName, FieldReference,
        Untyped>
        size = this.parseSizeExpression(s_expr);

      final ImmutableList<FieldName> fields = JPRAParser.parseFieldSet(f_expr);
      final Optional<ImmutableLexicalPositionType<Path>> lex =
        JPRAParser.getExpressionLexical(s_expr);
      return new TypeExprBooleanSet<>(lex, fields, size);
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

  private TypeExprType<TypeName, TypeReference, FieldName, FieldReference,
    Untyped> parseTypeString(
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
        final SizeExprType<TypeName, TypeReference, FieldName,
          FieldReference, Untyped>
          size = this.parseSizeExpression(s_expr);
        return new TypeExprString<>(
          JPRAParser.getExpressionLexical(le),
          Untyped.get(),
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

  private TypeExprType<TypeName, TypeReference, FieldName, FieldReference,
    Untyped> parseTypeArray(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Assertive.require(JPRAParser.ARRAY.equals(se.getText()));

    if (le.size() == 3) {
      final SExpressionType t_expr = le.get(1);
      final SExpressionType s_expr = le.get(2);
      final SizeExprType<TypeName, TypeReference, FieldName, FieldReference,
        Untyped>
        size = this.parseSizeExpression(s_expr);
      final TypeExprType<TypeName, TypeReference, FieldName, FieldReference,
        Untyped>
        type = this.parseTypeExpression(t_expr);
      return new TypeExprArray<>(
        JPRAParser.getExpressionLexical(le), size, Untyped.get(), type);
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

  private TypeExprType<TypeName, TypeReference, FieldName, FieldReference,
    Untyped> parseTypeMatrix(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Assertive.require(JPRAParser.MATRIX.equals(se.getText()));

    if (le.size() == 4) {
      final SExpressionType t_expr = le.get(1);
      final SExpressionType w_expr = le.get(2);
      final SExpressionType h_expr = le.get(3);

      final SizeExprType<TypeName, TypeReference, FieldName, FieldReference,
        Untyped>
        width = this.parseSizeExpression(w_expr);
      final SizeExprType<TypeName, TypeReference, FieldName, FieldReference,
        Untyped>
        height = this.parseSizeExpression(h_expr);
      final TypeExprType<TypeName, TypeReference, FieldName, FieldReference,
        Untyped>
        type = this.parseTypeExpression(t_expr);

      return new TypeExprMatrix<>(
        JPRAParser.getExpressionLexical(le),
        Untyped.get(),
        width,
        height,
        type);
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

  private TypeExprType<TypeName, TypeReference, FieldName, FieldReference,
    Untyped> parseTypeVector(
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
        lex.map(ImmutableLexicalPosition::newFrom),
        Untyped.get(),
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

  private TypeExprType<TypeName, TypeReference, FieldName, FieldReference,
    Untyped> parseTypeFloat(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Assertive.require(JPRAParser.FLOAT.equals(se.getText()));

    if (le.size() == 2) {
      final SExpressionType s_expr = le.get(1);
      return new TypeExprFloat<>(
        JPRAParser.getExpressionLexical(s_expr),
        Untyped.get(),
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

  private TypeExprType<TypeName, TypeReference, FieldName, FieldReference,
    Untyped> parseTypeInteger(
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

        final SizeExprType<TypeName, TypeReference, FieldName,
          FieldReference, Untyped>
          size = this.parseSizeExpression(s_expr);

        switch (t_name.getText()) {
          case JPRAParser.INTEGER_SIGNED: {
            return new TypeExprIntegerSigned<>(
              JPRAParser.getExpressionLexical(s_expr), Untyped.get(), size);
          }
          case JPRAParser.INTEGER_UNSIGNED: {
            return new TypeExprIntegerUnsigned<>(
              JPRAParser.getExpressionLexical(s_expr), Untyped.get(), size);
          }
          case JPRAParser.INTEGER_SIGNED_NORMALIZED: {
            return new TypeExprIntegerSignedNormalized<>(
              JPRAParser.getExpressionLexical(s_expr), Untyped.get(), size);
          }
          case JPRAParser.INTEGER_UNSIGNED_NORMALIZED: {
            return new TypeExprIntegerUnsignedNormalized<>(
              JPRAParser.getExpressionLexical(s_expr), Untyped.get(), size);
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

  @Override
  public SizeExprType<TypeName, TypeReference, FieldName, FieldReference,
    Untyped> parseSizeExpression(
    final SExpressionType e)
    throws JPRACompilerParseException
  {
    return e.matchExpression(
      new SExpressionMatcherType<SizeExprType<TypeName, TypeReference,
        FieldName, FieldReference, Untyped>, JPRACompilerParseException>()
      {
        @Override
        public SizeExprType<TypeName, TypeReference, FieldName,
          FieldReference, Untyped> list(final SExpressionListType le)
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

        @Override
        public SizeExprType<TypeName, TypeReference, FieldName,
          FieldReference, Untyped> quotedString(
          final SExpressionQuotedStringType qe)
          throws JPRACompilerParseException
        {
          throw JPRACompilerParseException.expectedSymbolOrListGotQuotedString(
            qe);
        }

        @Override
        public SizeExprType<TypeName, TypeReference, FieldName,
          FieldReference, Untyped> symbol(
          final SExpressionSymbolType se)
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

  private SizeExprType<TypeName, TypeReference, FieldName, FieldReference,
    Untyped> parseSizeInBits(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Assertive.require(JPRAParser.SIZE_IN_BITS.equals(se.getText()));

    if (le.size() == 2) {
      return new SizeExprInBits<>(this.parseTypeExpression(le.get(1)));
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

  private SizeExprType<TypeName, TypeReference, FieldName, FieldReference,
    Untyped> parseSizeInOctets(
    final SExpressionListType le,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Assertive.require(JPRAParser.SIZE_IN_OCTETS.equals(se.getText()));

    if (le.size() == 2) {
      return new SizeExprInOctets<>(this.parseTypeExpression(le.get(1)));
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
