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
import com.gs.collections.impl.factory.Lists;
import com.io7m.jlexing.core.ImmutableLexicalPosition;
import com.io7m.jlexing.core.ImmutableLexicalPositionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.names.PackageNameUnqualified;
import com.io7m.jpra.model.type_expressions.TypeExprNameF;
import com.io7m.jpra.model.type_expressions.TypeExprNamePT;
import com.io7m.jpra.model.type_expressions.TypeExprNamePTF;
import com.io7m.jpra.model.type_expressions.TypeExprNameT;
import com.io7m.jpra.model.type_expressions.TypeExprNameTF;
import com.io7m.jpra.model.type_expressions.TypeExprNameType;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.jsx.SExpressionSymbolType;
import com.io7m.jsx.serializer.JSXSerializerType;
import com.io7m.junreachable.UnreachableCodeException;
import org.valid4j.Assertive;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A parser for parsing type name references.
 */

public final class JPRATypeReferenceNameParser
{
  private static final Pattern PATTERN_PT;
  private static final Pattern PATTERN_PTF;
  private static final Pattern PATTERN_TF;
  private static final Pattern PATTERN_F;

  static {

    {
      final StringBuilder sb = new StringBuilder(128);
      sb.append("(");
      sb.append(PackageNameUnqualified.PATTERN.toString());
      sb.append(")");
      sb.append(":");
      sb.append("(");
      sb.append(TypeName.PATTERN);
      sb.append(")");
      PATTERN_PT =
        Pattern.compile(sb.toString(), Pattern.UNICODE_CHARACTER_CLASS);
    }

    {
      final StringBuilder sb = new StringBuilder(128);
      sb.append("(");
      sb.append(PackageNameUnqualified.PATTERN.toString());
      sb.append(")");
      sb.append(":");
      sb.append("(");
      sb.append(TypeName.PATTERN);
      sb.append(")");
      sb.append("((\\.");
      sb.append(FieldName.PATTERN);
      sb.append(")+)");
      PATTERN_PTF =
        Pattern.compile(sb.toString(), Pattern.UNICODE_CHARACTER_CLASS);
    }

    {
      final StringBuilder sb = new StringBuilder(128);
      sb.append("(");
      sb.append(TypeName.PATTERN);
      sb.append(")");
      sb.append("((\\.");
      sb.append(FieldName.PATTERN);
      sb.append(")+)");
      PATTERN_TF =
        Pattern.compile(sb.toString(), Pattern.UNICODE_CHARACTER_CLASS);
    }

    {
      final StringBuilder sb = new StringBuilder(128);
      sb.append(FieldName.PATTERN);
      sb.append("((\\.");
      sb.append(FieldName.PATTERN);
      sb.append(")*)");
      PATTERN_F =
        Pattern.compile(sb.toString(), Pattern.UNICODE_CHARACTER_CLASS);
    }
  }

  private JPRATypeReferenceNameParser()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Attempt to parse a type name expression from the given symbol.
   *
   * @param serial A serializer for error messages
   * @param se     The symbol
   *
   * @return A type name expression
   *
   * @throws JPRACompilerParseException On errors
   */

  public static TypeExprNameType onName(
    final JSXSerializerType serial,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    NullCheck.notNull(serial);
    NullCheck.notNull(se);

    final String text = se.getText();
    final Optional<ImmutableLexicalPositionType<Path>> lex =
      se.getLexicalInformation().map(ImmutableLexicalPosition::newFrom);

    {
      final Matcher m = JPRATypeReferenceNameParser.PATTERN_PTF.matcher(text);
      if (m.matches()) {
        final PackageNameUnqualified p_name =
          new PackageNameUnqualified(lex, m.group(1));
        final TypeName t_name = new TypeName(lex, m.group(2));
        final ImmutableList<FieldName> fields =
          JPRATypeReferenceNameParser.getFieldPath(lex, se, m.group(3));
        return new TypeExprNamePTF(p_name, t_name, fields);
      }
    }

    {
      final Matcher m = JPRATypeReferenceNameParser.PATTERN_PT.matcher(text);
      if (m.matches()) {
        final PackageNameUnqualified p_name =
          new PackageNameUnqualified(lex, m.group(1));
        final TypeName t_name = new TypeName(lex, m.group(2));
        return new TypeExprNamePT(p_name, t_name);
      }
    }

    {
      final Matcher m = JPRATypeReferenceNameParser.PATTERN_TF.matcher(text);
      if (m.matches()) {
        final TypeName t_name = new TypeName(lex, m.group(1));
        final ImmutableList<FieldName> fields =
          JPRATypeReferenceNameParser.getFieldPath(lex, se, m.group(2));
        return new TypeExprNameTF(t_name, fields);
      }
    }

    {
      final Matcher m = JPRATypeReferenceNameParser.PATTERN_F.matcher(text);
      if (m.matches()) {
        final ImmutableList<FieldName> fields =
          JPRATypeReferenceNameParser.getFieldPath(lex, se, text);
        return new TypeExprNameF(fields);
      }
    }

    {
      final Matcher m = TypeName.PATTERN.matcher(text);
      if (m.matches()) {
        return new TypeExprNameT(new TypeName(lex, text));
      }
    }

    throw JPRATypeReferenceNameParser.badTypeReference(serial, se);
  }

  private static JPRACompilerParseException badTypeReference(
    final JSXSerializerType serial,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    try (final ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      serial.serialize(se, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Unparseable type reference.");
      sb.append(System.lineSeparator());
      sb.append("Expected:   <package-name-unqualified>:<type-name>");
      sb.append(System.lineSeparator());
      sb.append("          | <package-name-unqualified>:<type-name>.<field>");
      sb.append(System.lineSeparator());
      sb.append("          | <type-name>.<field>");
      sb.append(System.lineSeparator());
      sb.append("          | <type-name>");
      sb.append(System.lineSeparator());
      sb.append("          | <field>");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      return JPRACompilerParseException.badTypeReference(se, sb.toString());
    } catch (final IOException x) {
      throw new UnreachableCodeException(x);
    }
  }

  private static ImmutableList<FieldName> getFieldPath(
    final Optional<ImmutableLexicalPositionType<Path>> lex,
    final SExpressionSymbolType se,
    final String text)
    throws JPRACompilerParseException
  {
    Assertive.require(!text.isEmpty());
    final String[] segments = text.split("\\.");
    return Lists.immutable.of(segments)
      .reject(String::isEmpty)
      .collect(x -> new FieldName(lex, x));
  }
}
