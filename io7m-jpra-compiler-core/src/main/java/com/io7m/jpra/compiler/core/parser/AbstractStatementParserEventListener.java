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
import com.io7m.jpra.model.TypeExprType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractStatementParserEventListener
  implements StatementParserREPLEventListenerType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(AbstractStatementParserEventListener.class);
  }

  protected AbstractStatementParserEventListener()
  {

  }

  @Override public void onREPLType(
    final LexicalContextType context,
    final TypeExprType t)
  {
    AbstractStatementParserEventListener.LOG.debug("onREPLType: {}", t);
  }

  @Override public void onImport(
    final LexicalContextType context,
    final PackageNameQualified p_name,
    final PackageNameUnqualified up_name)
    throws CompilerParseException
  {
    AbstractStatementParserEventListener.LOG.debug(
      "onImport: {} {}", p_name, up_name);
  }

  @Override public void onPackageBegin(
    final LexicalContextType context,
    final PackageNameQualified name)
    throws CompilerParseException
  {
    AbstractStatementParserEventListener.LOG.debug("onPackageBegin: {}", name);
  }

  @Override public void onPackageEnd(final LexicalContextType context)
    throws CompilerParseException
  {
    AbstractStatementParserEventListener.LOG.debug("onPackageEnd");
  }

  @Override public void onRecord(
    final LexicalContextType context,
    final TypeDeclRecord r)
    throws CompilerParseException
  {
    AbstractStatementParserEventListener.LOG.debug("onRecord: {}", r);
  }
}
