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

import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.names.FieldPath;
import com.io7m.jpra.model.names.FieldReference;
import com.io7m.jpra.model.names.PackageNameUnqualified;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.jpra.model.names.TypeReference;
import com.io7m.jsx.SExpressionSymbolType;
import com.io7m.jsx.api.serializer.JSXSerializerType;
import com.io7m.junreachable.UnreachableCodeException;
import io.vavr.collection.List;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The default implementation of the {@link JPRAReferenceParserType} interface.
 */

public final class JPRAReferenceParser implements JPRAReferenceParserType
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

  private final JSXSerializerType serial;

  private JPRAReferenceParser(final JSXSerializerType in_serial)
  {
    this.serial = Objects.requireNonNull(in_serial, "Serializer");
  }

  private static JPRACompilerParseException badTypeReference(
    final JSXSerializerType serial,
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    try (ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      serial.serialize(se, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Unparseable type reference.");
      sb.append(System.lineSeparator());
      sb.append("Expected: <package-name-unqualified>:<type-name>");
      sb.append(System.lineSeparator());
      sb.append("        | <type-name>");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      return JPRACompilerParseException.badTypeReference(se, sb.toString());
    } catch (final IOException x) {
      throw new UnreachableCodeException(x);
    }
  }

  private static JPRACompilerParseException badFieldReference(
    final JSXSerializerType serial,
    final SExpressionSymbolType se)
  {
    try (ByteArrayOutputStream bao = new ByteArrayOutputStream(256)) {
      serial.serialize(se, bao);
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Unparseable field reference.");
      sb.append(System.lineSeparator());
      sb.append("Expected:");
      sb.append(System.lineSeparator());
      sb.append("        <package-name-unqualified>:<type-name>.<field-path>");
      sb.append(System.lineSeparator());
      sb.append("      | <type-name>.<field-path>");
      sb.append(System.lineSeparator());
      sb.append("      | <field-path>");
      sb.append(System.lineSeparator());
      sb.append("Got: ");
      sb.append(bao.toString(StandardCharsets.UTF_8.name()));
      return JPRACompilerParseException.badTypeReference(se, sb.toString());
    } catch (final IOException x) {
      throw new UnreachableCodeException(x);
    }
  }

  private static List<FieldName> getFieldPath(
    final Optional<LexicalPosition<Path>> lex,
    final SExpressionSymbolType se,
    final String text)
    throws JPRACompilerParseException
  {
    Preconditions.checkPrecondition(!text.isEmpty(), "Text must non-empty");
    final String[] segments = text.split("\\.");
    return List.of(segments)
      .filter(s -> !s.isEmpty())
      .map(s -> new FieldName(lex, s));
  }

  /**
   * @param in_serial A serializer for error messages
   *
   * @return A new parser
   */

  public static JPRAReferenceParserType newParser(
    final JSXSerializerType in_serial)
  {
    return new JPRAReferenceParser(in_serial);
  }

  @Override
  public TypeReference parseTypeReference(
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Objects.requireNonNull(se, "Symbol");

    final String text = se.text();
    final Optional<LexicalPosition<Path>> lex =
      se.lexical().map(LexicalPosition::copyOf);

    {
      final Matcher m = PATTERN_PT.matcher(text);
      if (m.matches()) {
        final PackageNameUnqualified p_name =
          new PackageNameUnqualified(lex, m.group(1));
        final TypeName t_name = new TypeName(lex, m.group(2));
        return new TypeReference(Optional.of(p_name), t_name);
      }
    }

    {
      final Matcher m = TypeName.PATTERN.matcher(text);
      if (m.matches()) {
        return new TypeReference(Optional.empty(), new TypeName(lex, text));
      }
    }

    throw badTypeReference(this.serial, se);
  }

  @Override
  public FieldReference parseFieldReference(
    final SExpressionSymbolType se)
    throws JPRACompilerParseException
  {
    Objects.requireNonNull(se, "Symbol");

    final String text = se.text();
    final Optional<LexicalPosition<Path>> lex =
      se.lexical().map(LexicalPosition::copyOf);

    {
      final Matcher m = PATTERN_PTF.matcher(text);
      if (m.matches()) {
        final PackageNameUnqualified p_name =
          new PackageNameUnqualified(lex, m.group(1));
        final TypeName t_name = new TypeName(lex, m.group(2));
        final List<FieldName> fields =
          getFieldPath(lex, se, m.group(3));
        return new FieldReference(
          Optional.of(p_name), Optional.of(t_name), FieldPath.ofList(fields));
      }
    }

    {
      final Matcher m = PATTERN_TF.matcher(text);
      if (m.matches()) {
        final TypeName t_name = new TypeName(lex, m.group(1));
        final List<FieldName> fields =
          getFieldPath(lex, se, m.group(2));
        return new FieldReference(
          Optional.empty(), Optional.of(t_name), FieldPath.ofList(fields));
      }
    }

    {
      final Matcher m = PATTERN_F.matcher(text);
      if (m.matches()) {
        final List<FieldName> fields =
          getFieldPath(lex, se, text);
        return new FieldReference(
          Optional.empty(), Optional.empty(), FieldPath.ofList(fields));
      }
    }

    throw badFieldReference(this.serial, se);
  }
}
