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

import com.io7m.jpra.compiler.core.JPRACompilerException;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.names.PackageNameQualified;
import com.io7m.jpra.model.names.PackageNameUnqualified;
import com.io7m.jpra.model.SizeExprType;
import com.io7m.jpra.model.SizeUnitOctetsType;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.jpra.model.type_expressions.TypeExprType;

/**
 * A listener that will receive the results of parsed expressions.
 */

public interface JPRAParserEventListenerType
{
  /**
   * A package was begun.
   *
   * @param p    The parser
   * @param name The package name
   *
   * @throws JPRACompilerException If required
   */

  void onPackageBegin(
    JPRAParserType p,
    PackageNameQualified name)
    throws JPRACompilerException;

  /**
   * A package was imported.
   *
   * @param p       The parser
   * @param p_name  The imported package name
   * @param up_name The name used to refer to the imported package
   *
   * @throws JPRACompilerException If required
   */

  void onImport(
    JPRAParserType p,
    PackageNameQualified p_name,
    PackageNameUnqualified up_name)
    throws JPRACompilerException;

  /**
   * A package was finished.
   *
   * @param p The parser
   *
   * @throws JPRACompilerException If required
   */

  void onPackageEnd(JPRAParserType p)
    throws JPRACompilerException;

  /**
   * A {@code record} type was begun.
   *
   * @param p The parser
   * @param t The type name
   *
   * @throws JPRACompilerException If required
   */

  void onRecordBegin(
    JPRAParserType p,
    TypeName t)
    throws JPRACompilerException;

  /**
   * A {@code padding-octets} record field was declared.
   *
   * @param p    The parser
   * @param size The size expression
   *
   * @throws JPRACompilerException If required
   */

  void onRecordFieldPaddingOctets(
    JPRAParserType p,
    SizeExprType<SizeUnitOctetsType> size)
    throws JPRACompilerException;

  /**
   * A {@code field} declaration was declared.
   *
   * @param p    The parser
   * @param name The field name
   * @param type The field type expression
   */

  void onRecordFieldValue(
    JPRAParserType p,
    FieldName name,
    TypeExprType type);

  /**
   * A {@code record} type was finished.
   *
   * @param p The parser
   *
   * @throws JPRACompilerException If required
   */

  void onRecordEnd(JPRAParserType p)
    throws JPRACompilerException;
}
