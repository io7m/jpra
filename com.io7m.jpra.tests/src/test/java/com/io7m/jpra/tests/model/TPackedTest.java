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
import java.nio.file.Path;
import java.util.Optional;

public final class TPackedTest
{
  @Test
  public void testLSBMSBRanges_0()
    throws Exception
  {
    final Optional<LexicalPosition<Path>> no_lex =
      Optional.empty();

    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of("x"),
          PackageNameUnqualified.of("y"),
          PackageNameUnqualified.of("z"))));

    final TPackedBuilderType tpb = TPacked.newBuilder(
      pc, gc.getFreshIdentifier(), new TypeName(no_lex, "T"));
    final TPacked tp = tpb.build();
    Assert.assertEquals(Size.zero(), tp.getSizeInBits());
  }

  @Test
  public void testLSBMSBRanges_1()
    throws Exception
  {
    final Optional<LexicalPosition<Path>> no_lex =
      Optional.empty();

    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of("x"),
          PackageNameUnqualified.of("y"),
          PackageNameUnqualified.of("z"))));

    final TPackedBuilderType tpb = TPacked.newBuilder(
      pc, gc.getFreshIdentifier(), new TypeName(no_lex, "T"));

    tpb.addField(
      FieldName.of(no_lex, "f0"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(no_lex, Size.valueOf(4L)));
    tpb.addField(
      FieldName.of(no_lex, "f1"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(no_lex, Size.valueOf(4L)));
    tpb.addField(
      FieldName.of(no_lex, "f2"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(no_lex, Size.valueOf(4L)));
    tpb.addField(
      FieldName.of(no_lex, "f3"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(no_lex, Size.valueOf(4L)));

    final TPacked tp = tpb.build();
    Assert.assertEquals(Size.valueOf(16L), tp.getSizeInBits());

    {
      final TPacked.FieldValue f =
        (TPacked.FieldValue) tp.getFieldsInDeclarationOrder().get(3);
      final RangeInclusiveB range = f.getBitRange();
      Assert.assertEquals(BigInteger.valueOf(0L), range.getLower());
      Assert.assertEquals(BigInteger.valueOf(3L), range.getUpper());
    }

    {
      final TPacked.FieldValue f =
        (TPacked.FieldValue) tp.getFieldsInDeclarationOrder().get(2);
      final RangeInclusiveB range = f.getBitRange();
      Assert.assertEquals(BigInteger.valueOf(4L), range.getLower());
      Assert.assertEquals(BigInteger.valueOf(7L), range.getUpper());
    }

    {
      final TPacked.FieldValue f =
        (TPacked.FieldValue) tp.getFieldsInDeclarationOrder().get(1);
      final RangeInclusiveB range = f.getBitRange();
      Assert.assertEquals(BigInteger.valueOf(8L), range.getLower());
      Assert.assertEquals(BigInteger.valueOf(11L), range.getUpper());
    }

    {
      final TPacked.FieldValue f =
        (TPacked.FieldValue) tp.getFieldsInDeclarationOrder().get(0);
      final RangeInclusiveB range = f.getBitRange();
      Assert.assertEquals(BigInteger.valueOf(12L), range.getLower());
      Assert.assertEquals(BigInteger.valueOf(15L), range.getUpper());
    }
  }

  @Test
  public void testLSBMSBRanges_2()
    throws Exception
  {
    final Optional<LexicalPosition<Path>> no_lex =
      Optional.empty();

    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of("x"),
          PackageNameUnqualified.of("y"),
          PackageNameUnqualified.of("z"))));

    final TPackedBuilderType tpb = TPacked.newBuilder(
      pc, gc.getFreshIdentifier(), new TypeName(no_lex, "T"));

    tpb.addField(
      FieldName.of(no_lex, "f0"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(no_lex, Size.valueOf(4L)));
    tpb.addPaddingBits(no_lex, Size.valueOf(4L));
    tpb.addField(
      FieldName.of(no_lex, "f2"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(no_lex, Size.valueOf(4L)));
    tpb.addPaddingBits(no_lex, Size.valueOf(4L));

    final TPacked tp = tpb.build();
    Assert.assertEquals(Size.valueOf(16L), tp.getSizeInBits());

    {
      final TPacked.FieldPaddingBits f =
        (TPacked.FieldPaddingBits) tp.getFieldsInDeclarationOrder().get(3);
      final RangeInclusiveB range = f.getBitRange();
      Assert.assertEquals(BigInteger.valueOf(0L), range.getLower());
      Assert.assertEquals(BigInteger.valueOf(3L), range.getUpper());
    }

    {
      final TPacked.FieldValue f =
        (TPacked.FieldValue) tp.getFieldsInDeclarationOrder().get(2);
      final RangeInclusiveB range = f.getBitRange();
      Assert.assertEquals(BigInteger.valueOf(4L), range.getLower());
      Assert.assertEquals(BigInteger.valueOf(7L), range.getUpper());
    }

    {
      final TPacked.FieldPaddingBits f =
        (TPacked.FieldPaddingBits) tp.getFieldsInDeclarationOrder().get(1);
      final RangeInclusiveB range = f.getBitRange();
      Assert.assertEquals(BigInteger.valueOf(8L), range.getLower());
      Assert.assertEquals(BigInteger.valueOf(11L), range.getUpper());
    }

    {
      final TPacked.FieldValue f =
        (TPacked.FieldValue) tp.getFieldsInDeclarationOrder().get(0);
      final RangeInclusiveB range = f.getBitRange();
      Assert.assertEquals(BigInteger.valueOf(12L), range.getLower());
      Assert.assertEquals(BigInteger.valueOf(15L), range.getUpper());
    }
  }
}
