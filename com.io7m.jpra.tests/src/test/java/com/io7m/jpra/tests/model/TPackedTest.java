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

package com.io7m.jpra.tests.model;

import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jpra.model.contexts.GlobalContextType;
import com.io7m.jpra.model.contexts.GlobalContexts;
import com.io7m.jpra.model.contexts.PackageContextType;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.names.PackageNameQualified;
import com.io7m.jpra.model.names.PackageNameUnqualified;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.jpra.model.types.Size;
import com.io7m.jpra.model.types.TIntegerSigned;
import com.io7m.jpra.model.types.TPacked;
import com.io7m.jpra.model.types.TPackedBuilderType;
import com.io7m.jranges.RangeInclusiveB;
import io.vavr.collection.List;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.net.URI;
import java.util.Optional;

public final class TPackedTest
{
  static final LexicalPosition<URI> LEX_ZERO =
    LexicalPosition.of(0, 0, Optional.empty());

  @Test
  public void testLSBMSBRanges_0()
    throws Exception
  {
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final TPackedBuilderType tpb = TPacked.newBuilder(
      pc, gc.getFreshIdentifier(), TypeName.of(LEX_ZERO, "T"));
    final TPacked tp = tpb.build();
    Assert.assertEquals(Size.zero(), tp.getSizeInBits());
  }

  @Test
  public void testLSBMSBRanges_1()
    throws Exception
  {
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final TPackedBuilderType tpb = TPacked.newBuilder(
      pc, gc.getFreshIdentifier(), TypeName.of(LEX_ZERO, "T"));

    tpb.addField(
      FieldName.of(LEX_ZERO, "f0"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(4L)));
    tpb.addField(
      FieldName.of(LEX_ZERO, "f1"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(4L)));
    tpb.addField(
      FieldName.of(LEX_ZERO, "f2"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(4L)));
    tpb.addField(
      FieldName.of(LEX_ZERO, "f3"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(4L)));

    final TPacked tp = tpb.build();
    Assert.assertEquals(Size.valueOf(16L), tp.getSizeInBits());

    {
      final TPacked.FieldValue f =
        (TPacked.FieldValue) tp.getFieldsInDeclarationOrder().get(3);
      final RangeInclusiveB range = f.getBitRange();
      Assert.assertEquals(BigInteger.valueOf(0L), range.lower());
      Assert.assertEquals(BigInteger.valueOf(3L), range.upper());
    }

    {
      final TPacked.FieldValue f =
        (TPacked.FieldValue) tp.getFieldsInDeclarationOrder().get(2);
      final RangeInclusiveB range = f.getBitRange();
      Assert.assertEquals(BigInteger.valueOf(4L), range.lower());
      Assert.assertEquals(BigInteger.valueOf(7L), range.upper());
    }

    {
      final TPacked.FieldValue f =
        (TPacked.FieldValue) tp.getFieldsInDeclarationOrder().get(1);
      final RangeInclusiveB range = f.getBitRange();
      Assert.assertEquals(BigInteger.valueOf(8L), range.lower());
      Assert.assertEquals(BigInteger.valueOf(11L), range.upper());
    }

    {
      final TPacked.FieldValue f =
        (TPacked.FieldValue) tp.getFieldsInDeclarationOrder().get(0);
      final RangeInclusiveB range = f.getBitRange();
      Assert.assertEquals(BigInteger.valueOf(12L), range.lower());
      Assert.assertEquals(BigInteger.valueOf(15L), range.upper());
    }
  }

  @Test
  public void testLSBMSBRanges_2()
    throws Exception
  {
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final TPackedBuilderType tpb = TPacked.newBuilder(
      pc, gc.getFreshIdentifier(), TypeName.of(LEX_ZERO, "T"));

    tpb.addField(
      FieldName.of(LEX_ZERO, "f0"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(4L)));
    tpb.addPaddingBits(LEX_ZERO, Size.valueOf(4L));
    tpb.addField(
      FieldName.of(LEX_ZERO, "f2"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(4L)));
    tpb.addPaddingBits(LEX_ZERO, Size.valueOf(4L));

    final TPacked tp = tpb.build();
    Assert.assertEquals(Size.valueOf(16L), tp.getSizeInBits());

    {
      final TPacked.FieldPaddingBits f =
        (TPacked.FieldPaddingBits) tp.getFieldsInDeclarationOrder().get(3);
      final RangeInclusiveB range = f.getBitRange();
      Assert.assertEquals(BigInteger.valueOf(0L), range.lower());
      Assert.assertEquals(BigInteger.valueOf(3L), range.upper());
    }

    {
      final TPacked.FieldValue f =
        (TPacked.FieldValue) tp.getFieldsInDeclarationOrder().get(2);
      final RangeInclusiveB range = f.getBitRange();
      Assert.assertEquals(BigInteger.valueOf(4L), range.lower());
      Assert.assertEquals(BigInteger.valueOf(7L), range.upper());
    }

    {
      final TPacked.FieldPaddingBits f =
        (TPacked.FieldPaddingBits) tp.getFieldsInDeclarationOrder().get(1);
      final RangeInclusiveB range = f.getBitRange();
      Assert.assertEquals(BigInteger.valueOf(8L), range.lower());
      Assert.assertEquals(BigInteger.valueOf(11L), range.upper());
    }

    {
      final TPacked.FieldValue f =
        (TPacked.FieldValue) tp.getFieldsInDeclarationOrder().get(0);
      final RangeInclusiveB range = f.getBitRange();
      Assert.assertEquals(BigInteger.valueOf(12L), range.lower());
      Assert.assertEquals(BigInteger.valueOf(15L), range.upper());
    }
  }
}
