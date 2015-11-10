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
import org.apache.commons.lang3.text.WordUtils;

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
    sb.append(name.getValue().toUpperCase());
    sb.append("_OFFSET_OCTETS");
    return sb.toString();
  }

  static String getGetterName(final FieldName name)
  {
    final String raw = name.toString();
    final String text = JPRAGeneratedNames.getRecased(raw);
    final StringBuilder sb = new StringBuilder(128);
    sb.append("get");
    sb.append(text);
    return sb.toString();
  }

  static String getRecased(final String raw)
  {
    final String spaced = raw.replaceAll("_", " ");
    final String capped = WordUtils.capitalize(spaced);
    return capped.replaceAll(" ", "");
  }

  static String getSetterName(final FieldName name)
  {
    final String text = JPRAGeneratedNames.getRecased(name.toString());
    final StringBuilder sb = new StringBuilder(128);
    sb.append("set");
    sb.append(text);
    return sb.toString();
  }

  static String getNormalizedSetterName(final FieldName name)
  {
    final String text = JPRAGeneratedNames.getRecased(name.toString());
    final StringBuilder sb = new StringBuilder(128);
    sb.append("set");
    sb.append(text);
    return sb.toString();
  }

  static String getNormalizedGetterName(final FieldName name)
  {
    final String text = JPRAGeneratedNames.getRecased(name.toString());
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
      JPRAGeneratedNames.getRecased(base_name.toString());
    final String field_text =
      JPRAGeneratedNames.getRecased(field_name.toString());

    final StringBuilder sb = new StringBuilder(128);
    sb.append("get");
    sb.append(base_text);
    sb.append(field_text);
    return sb.toString();
  }

  private static String getSetterBooleanSetName(
    final FieldName base_name,
    final FieldName field_name)
  {
    final String base_text =
      JPRAGeneratedNames.getRecased(base_name.toString());
    final String field_text =
      JPRAGeneratedNames.getRecased(field_name.toString());

    final StringBuilder sb = new StringBuilder(128);
    sb.append("set");
    sb.append(base_text);
    sb.append(field_text);
    return sb.toString();
  }

  static String getGetterRecordReadableName(final TypeName name)
  {
    final String text = JPRAGeneratedNames.getRecased(name.toString());
    final StringBuilder sb = new StringBuilder(128);
    sb.append("get");
    sb.append(text);
    sb.append("Readable");
    return sb.toString();
  }

  static String getGetterRecordWritableName(final TypeName name)
  {
    final String text = JPRAGeneratedNames.getRecased(name.toString());
    final StringBuilder sb = new StringBuilder(128);
    sb.append("get");
    sb.append(text);
    sb.append("Writable");
    return sb.toString();
  }

  static String getRecordImplementationByteBufferedName(
    final TypeName t)
  {
    return t.getValue() + "ByteBuffered";
  }

  static String getRecordInterfaceReadableName(final TypeName t)
  {
    return t.getValue() + "ReadableType";
  }

  static String getRecordInterfaceWritableName(final TypeName t)
  {
    return t.getValue() + "WritableType";
  }

  static String getRecordInterfaceName(final TypeName t)
  {
    return t.getValue() + "Type";
  }

  static String getFieldName(final FieldName f_name)
  {
    return String.format("field_%s", f_name.getValue());
  }

  static String getPackedInterfaceWritableName(final TypeName t)
  {
    return t.getValue() + "WritableType";
  }

  static String getPackedInterfaceReadableName(final TypeName t)
  {
    return t.getValue() + "ReadableType";
  }

  static String getPackedInterfaceName(final TypeName t)
  {
    return t.getValue() + "Type";
  }

  static String getPackedImplementationByteBufferedName(final TypeName t)
  {
    return t.getValue() + "ByteBuffered";
  }

  static String getNormalizedRawGetterName(final FieldName name)
  {
    final String text = JPRAGeneratedNames.getRecased(name.toString());
    final StringBuilder sb = new StringBuilder(128);
    sb.append("get");
    sb.append(text);
    sb.append("Raw");
    return sb.toString();
  }

  static String getNormalizedRawSetterName(final FieldName name)
  {
    final String text = JPRAGeneratedNames.getRecased(name.toString());
    final StringBuilder sb = new StringBuilder(128);
    sb.append("set");
    sb.append(text);
    sb.append("Raw");
    return sb.toString();
  }
}
