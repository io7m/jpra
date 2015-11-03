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
import com.io7m.jpra.model.SizeUnitOctetsType;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.jpra.model.type_expressions.TypeExprType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract implementation of the {@link JPRAParserREPLEventListenerType}
 * that simply logs calls and does nothing.
 */

// CHECKSTYLE:OFF

public abstract class JPRAAbstractParserEventListener
  implements JPRAParserREPLEventListenerType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(JPRAAbstractParserEventListener.class);
  }

  protected JPRAAbstractParserEventListener()
  {

  }

  @Override public void onREPLSize(
    final JPRAParserType p,
    final SizeExprType<?> t)
  {
    JPRAAbstractParserEventListener.LOG.debug("onREPLSize: {}", t);
  }

  @Override
  public void onREPLType(
    final JPRAParserType p,
    final TypeExprType t)
  {
    JPRAAbstractParserEventListener.LOG.debug("onREPLType: {}", t);
  }

  @Override public void onImport(
    final JPRAParserType p,
    final PackageNameQualified p_name,
    final PackageNameUnqualified up_name)
    throws JPRACompilerException
  {
    JPRAAbstractParserEventListener.LOG.debug(
      "onImport: {} {}", p_name, up_name);
  }

  @Override
  public void onPackageBegin(
    final JPRAParserType p,
    final PackageNameQualified name)
    throws JPRACompilerException
  {
    JPRAAbstractParserEventListener.LOG.debug("onPackageBegin: {}", name);
  }

  @Override public void onPackageEnd(final JPRAParserType p)
    throws JPRACompilerException
  {
    JPRAAbstractParserEventListener.LOG.debug("onPackageEnd");
  }

  @Override
  public void onRecordBegin(
    final JPRAParserType p,
    final TypeName t)
    throws JPRACompilerException
  {
    JPRAAbstractParserEventListener.LOG.debug("onRecordBegin: {}", t);
  }

  @Override public void onRecordEnd(final JPRAParserType p)
    throws JPRACompilerException
  {
    JPRAAbstractParserEventListener.LOG.debug("onRecordEnd");
  }

  @Override public void onRecordFieldPaddingOctets(
    final JPRAParserType p,
    final SizeExprType<SizeUnitOctetsType> size)
    throws JPRACompilerException
  {
    JPRAAbstractParserEventListener.LOG.debug(
      "onRecordFieldPaddingOctets: {}", size);
  }

  @Override public void onRecordFieldValue(
    final JPRAParserType p,
    final FieldName name,
    final TypeExprType type)
  {
    JPRAAbstractParserEventListener.LOG.debug(
      "onRecordFieldValue: {} {}", name, type);
  }

}
