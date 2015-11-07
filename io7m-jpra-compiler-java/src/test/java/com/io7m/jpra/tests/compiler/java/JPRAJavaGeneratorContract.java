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

package com.io7m.jpra.tests.compiler.java;

import com.gs.collections.impl.factory.Lists;
import com.io7m.jlexing.core.ImmutableLexicalPositionType;
import com.io7m.jpra.compiler.java.JPRAJavaGeneratorType;
import com.io7m.jpra.model.contexts.GlobalContextType;
import com.io7m.jpra.model.contexts.GlobalContexts;
import com.io7m.jpra.model.contexts.PackageContextType;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.names.IdentifierType;
import com.io7m.jpra.model.names.PackageNameQualified;
import com.io7m.jpra.model.names.PackageNameUnqualified;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.jpra.model.types.Size;
import com.io7m.jpra.model.types.TBooleanSet;
import com.io7m.jpra.model.types.TFloat;
import com.io7m.jpra.model.types.TIntegerSigned;
import com.io7m.jpra.model.types.TIntegerSignedNormalized;
import com.io7m.jpra.model.types.TIntegerUnsigned;
import com.io7m.jpra.model.types.TIntegerUnsignedNormalized;
import com.io7m.jpra.model.types.TRecord;
import com.io7m.jpra.model.types.TRecordBuilderType;
import org.junit.Assert;
import org.junit.Test;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public abstract class JPRAJavaGeneratorContract
{
  private static void compileRecord(
    final JPRAJavaGeneratorType g,
    final TRecord r)
    throws IOException
  {
    final TypeName t_name = r.getName();

    final JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
    final Path path = Files.createTempDirectory("jpra");
    final Path c_file =
      path.resolve(g.getRecordImplementationByteBufferedName(t_name) + ".java");
    final Path r_file =
      path.resolve(g.getRecordInterfaceReadableName(t_name) + ".java");
    final Path w_file =
      path.resolve(g.getRecordInterfaceWritableName(t_name) + ".java");
    final Path i_file =
      path.resolve(g.getRecordInterfaceName(t_name) + ".java");

    try (final OutputStream w = Files.newOutputStream(c_file)) {
      g.generateRecordImplementation(r, w);
    }
    try (final OutputStream w = Files.newOutputStream(r_file)) {
      g.generateRecordInterfaceReadable(r, w);
    }
    try (final OutputStream w = Files.newOutputStream(w_file)) {
      g.generateRecordInterfaceWritable(r, w);
    }
    try (final OutputStream w = Files.newOutputStream(i_file)) {
      g.generateRecordInterface(r, w);
    }

    final int result = jc.run(
      null,
      null,
      null,
      "-verbose",
      "-Werror",
      "-d",
      path.toString(),
      r_file.toString(),
      w_file.toString(),
      i_file.toString(),
      c_file.toString());
    Assert.assertEquals(0L, (long) result);
  }

  protected abstract JPRAJavaGeneratorType getJavaGenerator();

  @Test public final void testEmptyRecord()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.getPackage(
      new PackageNameQualified(
        Lists.immutable.of(
          PackageNameUnqualified.of("x"),
          PackageNameUnqualified.of("y"),
          PackageNameUnqualified.of("z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final Optional<ImmutableLexicalPositionType<Path>> no_lex =
      Optional.empty();
    final TypeName t_name = new TypeName(no_lex, "Empty");
    final TRecordBuilderType rb = TRecord.newBuilder(pc, id, t_name);
    final TRecord r = rb.build();
    JPRAJavaGeneratorContract.compileRecord(g, r);
  }

  @Test public final void testRecordIntegerExhaustive()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.getPackage(
      new PackageNameQualified(
        Lists.immutable.of(
          PackageNameUnqualified.of("x"),
          PackageNameUnqualified.of("y"),
          PackageNameUnqualified.of("z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final Optional<ImmutableLexicalPositionType<Path>> no_lex =
      Optional.empty();
    final TypeName t_name = new TypeName(no_lex, "RecordInteger");
    final TRecordBuilderType rb = TRecord.newBuilder(pc, id, t_name);

    rb.addField(
      new FieldName(no_lex, "i8"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(no_lex, Size.valueOf(8L)));
    rb.addField(
      new FieldName(no_lex, "i16"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(no_lex, Size.valueOf(16L)));
    rb.addField(
      new FieldName(no_lex, "i32"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(no_lex, Size.valueOf(32L)));
    rb.addField(
      new FieldName(no_lex, "i64"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(no_lex, Size.valueOf(64L)));

    rb.addField(
      new FieldName(no_lex, "u8"),
      gc.getFreshIdentifier(),
      new TIntegerUnsigned(no_lex, Size.valueOf(8L)));
    rb.addField(
      new FieldName(no_lex, "u16"),
      gc.getFreshIdentifier(),
      new TIntegerUnsigned(no_lex, Size.valueOf(16L)));
    rb.addField(
      new FieldName(no_lex, "u32"),
      gc.getFreshIdentifier(),
      new TIntegerUnsigned(no_lex, Size.valueOf(32L)));
    rb.addField(
      new FieldName(no_lex, "u64"),
      gc.getFreshIdentifier(),
      new TIntegerUnsigned(no_lex, Size.valueOf(64L)));

    final TRecord r = rb.build();
    JPRAJavaGeneratorContract.compileRecord(g, r);
  }

  @Test public final void testRecordFloatExhaustive()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.getPackage(
      new PackageNameQualified(
        Lists.immutable.of(
          PackageNameUnqualified.of("x"),
          PackageNameUnqualified.of("y"),
          PackageNameUnqualified.of("z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final Optional<ImmutableLexicalPositionType<Path>> no_lex =
      Optional.empty();
    final TypeName t_name = new TypeName(no_lex, "RecordFloat");
    final TRecordBuilderType rb = TRecord.newBuilder(pc, id, t_name);

    rb.addField(
      new FieldName(no_lex, "f16"),
      gc.getFreshIdentifier(),
      new TFloat(no_lex, Size.valueOf(16L)));
    rb.addField(
      new FieldName(no_lex, "f32"),
      gc.getFreshIdentifier(),
      new TFloat(no_lex, Size.valueOf(32L)));
    rb.addField(
      new FieldName(no_lex, "f64"),
      gc.getFreshIdentifier(),
      new TFloat(no_lex, Size.valueOf(64L)));

    final TRecord r = rb.build();
    JPRAJavaGeneratorContract.compileRecord(g, r);
  }

  @Test public final void testRecordIntegerNormalizedExhaustive()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.getPackage(
      new PackageNameQualified(
        Lists.immutable.of(
          PackageNameUnqualified.of("x"),
          PackageNameUnqualified.of("y"),
          PackageNameUnqualified.of("z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final Optional<ImmutableLexicalPositionType<Path>> no_lex =
      Optional.empty();
    final TypeName t_name = new TypeName(no_lex, "RecordIntegerNormalized");
    final TRecordBuilderType rb = TRecord.newBuilder(pc, id, t_name);

    rb.addField(
      new FieldName(no_lex, "isn8"),
      gc.getFreshIdentifier(),
      new TIntegerSignedNormalized(no_lex, Size.valueOf(8L)));
    rb.addField(
      new FieldName(no_lex, "isn16"),
      gc.getFreshIdentifier(),
      new TIntegerSignedNormalized(no_lex, Size.valueOf(16L)));
    rb.addField(
      new FieldName(no_lex, "isn32"),
      gc.getFreshIdentifier(),
      new TIntegerSignedNormalized(no_lex, Size.valueOf(32L)));
    rb.addField(
      new FieldName(no_lex, "isn64"),
      gc.getFreshIdentifier(),
      new TIntegerSignedNormalized(no_lex, Size.valueOf(64L)));

    rb.addField(
      new FieldName(no_lex, "usn8"),
      gc.getFreshIdentifier(),
      new TIntegerUnsignedNormalized(no_lex, Size.valueOf(8L)));
    rb.addField(
      new FieldName(no_lex, "usn16"),
      gc.getFreshIdentifier(),
      new TIntegerUnsignedNormalized(no_lex, Size.valueOf(16L)));
    rb.addField(
      new FieldName(no_lex, "usn32"),
      gc.getFreshIdentifier(),
      new TIntegerUnsignedNormalized(no_lex, Size.valueOf(32L)));
    rb.addField(
      new FieldName(no_lex, "usn64"),
      gc.getFreshIdentifier(),
      new TIntegerUnsignedNormalized(no_lex, Size.valueOf(64L)));

    final TRecord r = rb.build();
    JPRAJavaGeneratorContract.compileRecord(g, r);
  }

  @Test public final void testRecordPaddingExhaustive()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.getPackage(
      new PackageNameQualified(
        Lists.immutable.of(
          PackageNameUnqualified.of("x"),
          PackageNameUnqualified.of("y"),
          PackageNameUnqualified.of("z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final Optional<ImmutableLexicalPositionType<Path>> no_lex =
      Optional.empty();
    final TypeName t_name = new TypeName(no_lex, "RecordPadding");
    final TRecordBuilderType rb = TRecord.newBuilder(pc, id, t_name);

    rb.addPaddingOctets(no_lex, Size.valueOf(100L));

    final TRecord r = rb.build();
    JPRAJavaGeneratorContract.compileRecord(g, r);
  }

  @Test public final void testRecordBooleanSetExhaustive()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.getPackage(
      new PackageNameQualified(
        Lists.immutable.of(
          PackageNameUnqualified.of("x"),
          PackageNameUnqualified.of("y"),
          PackageNameUnqualified.of("z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final Optional<ImmutableLexicalPositionType<Path>> no_lex =
      Optional.empty();
    final TypeName t_name = new TypeName(no_lex, "RecordBooleanSet");
    final TRecordBuilderType rb = TRecord.newBuilder(pc, id, t_name);

    rb.addField(
      new FieldName(no_lex, "bool"), gc.getFreshIdentifier(), new TBooleanSet(
        no_lex, Lists.immutable.of(
        new FieldName(no_lex, "flag_a"),
        new FieldName(no_lex, "flag_b"),
        new FieldName(no_lex, "flag_c"),
        new FieldName(no_lex, "flag_d"),
        new FieldName(no_lex, "flag_e"),
        new FieldName(no_lex, "flag_f"),
        new FieldName(no_lex, "flag_g"),
        new FieldName(no_lex, "flag_h"),
        new FieldName(no_lex, "flag_i"),
        new FieldName(no_lex, "flag_j"),
        new FieldName(no_lex, "flag_k")), Size.valueOf(2L)));

    final TRecord r = rb.build();
    JPRAJavaGeneratorContract.compileRecord(g, r);
  }
}
