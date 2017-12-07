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

package com.io7m.jpra.compiler.java;

import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.junreachable.UnreachableCodeException;
import io.vavr.collection.List;

import java.util.stream.Collectors;

final class JPRAGeneratedNames
{
  private JPRAGeneratedNames()
  {
    throw new UnreachableCodeException();
  }

  static String getOffsetConstantName(final FieldName name)
  {
    final StringBuilder sb = new StringBuilder(128);
    sb.append("FIELD_");
    sb.append(name.value().toUpperCase());
    sb.append("_OFFSET_OCTETS");
    return sb.toString();
  }

  static String getGetterName(final FieldName name)
  {
    final String raw = name.value();
    final String text = getRecased(raw);
    final StringBuilder sb = new StringBuilder(128);
    sb.append("get");
    sb.append(text);
    return sb.toString();
  }

  static String getRecased(final String raw)
  {
    return List.of(
      raw.replaceAll("_", " ").split(" "))
      .map(JPRAGeneratedNames::capitalizeWord)
      .collect(Collectors.joining(""));
  }

  private static String capitalizeWord(
    final String word)
  {
    return word.substring(0, 1).toUpperCase() + word.substring(1);
  }

  static String getSetterName(final FieldName name)
  {
    final String text = getRecased(name.value());
    final StringBuilder sb = new StringBuilder(128);
    sb.append("set");
    sb.append(text);
    return sb.toString();
  }

  static String getNormalizedSetterName(final FieldName name)
  {
    final String text = getRecased(name.value());
    final StringBuilder sb = new StringBuilder(128);
    sb.append("set");
    sb.append(text);
    return sb.toString();
  }

  static String getNormalizedGetterName(final FieldName name)
  {
    final String text = getRecased(name.value());
    final StringBuilder sb = new StringBuilder(128);
    sb.append("get");
    sb.append(text);
    return sb.toString();
  }

  static String getGetterBooleanSetName(
    final FieldName base_name,
    final FieldName field_name)
  {
    final String base_text =
      getRecased(base_name.value());
    final String field_text =
      getRecased(field_name.value());

    final StringBuilder sb = new StringBuilder(128);
    sb.append("get");
    sb.append(base_text);
    sb.append(field_text);
    return sb.toString();
  }

  static String getSetterBooleanSetName(
    final FieldName base_name,
    final FieldName field_name)
  {
    final String base_text = getRecased(base_name.value());
    final String field_text = getRecased(field_name.value());
    final StringBuilder sb = new StringBuilder(128);
    sb.append("set");
    sb.append(base_text);
    sb.append(field_text);
    return sb.toString();
  }

  static String getGetterRecordReadableName(final FieldName name)
  {
    return getObjectReadableName(name);
  }

  static String getGetterRecordWritableName(final FieldName name)
  {
    return getObjectWritableName(name);
  }

  static String getRecordImplementationByteBufferedName(
    final TypeName t)
  {
    return t.value() + "ByteBuffered";
  }

  static String getRecordInterfaceReadableName(final TypeName t)
  {
    return t.value() + "ReadableType";
  }

  static String getRecordInterfaceWritableName(final TypeName t)
  {
    return t.value() + "WritableType";
  }

  static String getRecordInterfaceName(final TypeName t)
  {
    return t.value() + "Type";
  }

  static String getFieldName(final FieldName f_name)
  {
    return String.format("field_%s", f_name.value());
  }

  static String getPackedInterfaceWritableName(final TypeName t)
  {
    return t.value() + "WritableType";
  }

  static String getPackedInterfaceReadableName(final TypeName t)
  {
    return t.value() + "ReadableType";
  }

  static String getPackedInterfaceName(final TypeName t)
  {
    return t.value() + "Type";
  }

  static String getPackedImplementationByteBufferedName(final TypeName t)
  {
    return t.value() + "ByteBuffered";
  }

  static String getNormalizedRawGetterName(final FieldName name)
  {
    final String text = getRecased(name.value());
    final StringBuilder sb = new StringBuilder(128);
    sb.append("get");
    sb.append(text);
    sb.append("Raw");
    return sb.toString();
  }

  static String getNormalizedRawSetterName(final FieldName name)
  {
    final String text = getRecased(name.value());
    final StringBuilder sb = new StringBuilder(128);
    sb.append("set");
    sb.append(text);
    sb.append("Raw");
    return sb.toString();
  }

  public static String getGetterVectorReadableName(final FieldName name)
  {
    return getObjectReadableName(name);
  }

  private static String getObjectReadableName(final FieldName name)
  {
    final String text = getRecased(name.value());
    final StringBuilder sb = new StringBuilder(128);
    sb.append("get");
    sb.append(text);
    sb.append("Readable");
    return sb.toString();
  }

  public static String getGetterVectorWritableName(final FieldName name)
  {
    return getObjectWritableName(name);
  }

  public static String getGetterMatrixReadableName(final FieldName name)
  {
    return getObjectReadableName(name);
  }

  public static String getGetterMatrixWritableName(final FieldName name)
  {
    return getObjectWritableName(name);
  }

  private static String getObjectWritableName(final FieldName name)
  {
    final String text = getRecased(name.value());
    final StringBuilder sb = new StringBuilder(128);
    sb.append("get");
    sb.append(text);
    sb.append("Writable");
    return sb.toString();
  }

  public static String getGetterStringReadableName(final FieldName name)
  {
    return getObjectReadableName(name);
  }

  public static String getGetterStringWritableName(final FieldName name)
  {
    return getObjectWritableName(name);
  }

  public static String getMetaOffsetTypeReadableName(final FieldName name)
  {
    final String text = getRecased(name.value());
    final StringBuilder sb = new StringBuilder(128);
    sb.append("meta");
    sb.append(text);
    sb.append("OffsetFromType");
    return sb.toString();
  }

  public static String getMetaOffsetStaticTypeReadableName(final FieldName name)
  {
    final String text = getRecased(name.value());
    final StringBuilder sb = new StringBuilder(128);
    sb.append("meta");
    sb.append(text);
    sb.append("StaticOffsetFromType");
    return sb.toString();
  }

  public static String getMetaOffsetCursorReadableName(final FieldName name)
  {
    final String text = getRecased(name.value());
    final StringBuilder sb = new StringBuilder(128);
    sb.append("meta");
    sb.append(text);
    sb.append("OffsetFromCursor");
    return sb.toString();
  }

  public static String getMetaTypeGetName(final FieldName name)
  {
    final String text = getRecased(name.value());
    final StringBuilder sb = new StringBuilder(128);
    sb.append("meta");
    sb.append(text);
    sb.append("Type");
    return sb.toString();
  }

  public static String getMetaTypeFieldName(final FieldName name)
  {
    final StringBuilder sb = new StringBuilder(128);
    sb.append("meta_type_");
    sb.append(name.value());
    return sb.toString();
  }
}
