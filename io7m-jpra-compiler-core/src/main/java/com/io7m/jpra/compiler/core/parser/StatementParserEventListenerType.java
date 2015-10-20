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

import com.io7m.jpra.compiler.core.LexicalContextType;
import com.io7m.jpra.model.PackageNameQualified;
import com.io7m.jpra.model.PackageNameUnqualified;
import com.io7m.jpra.model.TypeDeclRecord;

/**
 * A listener that will receive the results of parsed expressions.
 */

public interface StatementParserEventListenerType
{
  /**
   * A package was begun.
   *
   * @param context The lexical context
   * @param name    The package name
   *
   * @throws CompilerParseException If required
   */

  void onPackageBegin(
    LexicalContextType context,
    PackageNameQualified name)
    throws CompilerParseException;

  /**
   * A package was imported.
   *
   * @param context The lexical context
   * @param p_name  The imported package name
   * @param up_name The name used to refer to the imported package
   *
   * @throws CompilerParseException If required
   */

  void onImport(
    LexicalContextType context,
    PackageNameQualified p_name,
    PackageNameUnqualified up_name)
    throws CompilerParseException;

  /**
   * A package was finished.
   *
   * @param context The lexical context
   *
   * @throws CompilerParseException If required
   */

  void onPackageEnd(LexicalContextType context)
    throws CompilerParseException;

  /**
   * A {@code record} type was declared.
   *
   * @param context The lexical context
   * @param r       The type
   *
   * @throws CompilerParseException If required
   */

  void onRecord(
    LexicalContextType context,
    TypeDeclRecord r)
    throws CompilerParseException;
}
