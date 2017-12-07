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

import com.io7m.jlexing.core.LexicalPosition;
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
import com.io7m.jpra.model.types.TPacked;
import com.io7m.jpra.model.types.TPackedBuilderType;
import com.io7m.jpra.model.types.TRecord;
import com.io7m.jpra.model.types.TRecordBuilderType;
import com.io7m.jpra.model.types.TVector;
import io.vavr.collection.List;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * Contracts to test the generated code for validity. In other words, the output
 * is checked to see if it is valid Java, not necessarily that the code is
 * actually correct.
 */

public abstract class JPRAJavaGeneratorContract
{
  static final LexicalPosition<URI> LEX_ZERO =
    LexicalPosition.of(0, 0, Optional.empty());

  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(JPRAJavaGeneratorContract.class);
  }

  private static void compilePackeds(
    final Path path,
    final JPRAJavaGeneratorType g,
    final List<TPacked> types)
    throws IOException
  {
    final JavaCompiler jc = ToolProvider.getSystemJavaCompiler();

    final StandardJavaFileManager fm =
      jc.getStandardFileManager(null, null, null);

    final ArrayList<File> files = new ArrayList<>();

    types.forEach(r -> {
      try {
        final TypeName t_name = r.getName();

        final Path c_file = path.resolve(
          g.getPackedImplementationByteBufferedName(t_name) + ".java");
        final Path r_file =
          path.resolve(g.getPackedInterfaceReadableName(t_name) + ".java");
        final Path w_file =
          path.resolve(g.getPackedInterfaceWritableName(t_name) + ".java");
        final Path i_file =
          path.resolve(g.getPackedInterfaceName(t_name) + ".java");

        try (final OutputStream w = Files.newOutputStream(c_file)) {
          g.generatePackedImplementation(r, w);
        }
        try (final OutputStream w = Files.newOutputStream(r_file)) {
          g.generatePackedInterfaceReadable(r, w);
        }
        try (final OutputStream w = Files.newOutputStream(w_file)) {
          g.generatePackedInterfaceWritable(r, w);
        }
        try (final OutputStream w = Files.newOutputStream(i_file)) {
          g.generatePackedInterface(r, w);
        }

        files.add(c_file.toFile());
        files.add(r_file.toFile());
        files.add(w_file.toFile());
        files.add(i_file.toFile());
      } catch (final IOException e) {
        throw new UncheckedIOException(e);
      }
    });

    final String[] options = {"-verbose", "-Werror", "-d", path.toString()};
    final Iterable<? extends JavaFileObject> fm_files =
      fm.getJavaFileObjectsFromFiles(files);

    final JavaCompiler.CompilationTask task =
      jc.getTask(null, null, null, Arrays.asList(options), null, fm_files);
    final Boolean result = task.call();
    Assert.assertEquals(Boolean.TRUE, result);
    fm.close();
  }

  private static void compileRecords(
    final Path path,
    final JPRAJavaGeneratorType g,
    final List<TRecord> types)
    throws IOException
  {
    final JavaCompiler jc = ToolProvider.getSystemJavaCompiler();

    final StandardJavaFileManager fm =
      jc.getStandardFileManager(null, null, null);

    final ArrayList<File> files = new ArrayList<>();
    types.forEach(r -> {
      try {
        final TypeName t_name = r.getName();

        final Path c_file = path.resolve(
          g.getRecordImplementationByteBufferedName(t_name) + ".java");
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

        files.add(c_file.toFile());
        files.add(r_file.toFile());
        files.add(w_file.toFile());
        files.add(i_file.toFile());
      } catch (final IOException e) {
        throw new UncheckedIOException(e);
      }
    });

    final String[] options = {"-verbose", "-Werror", "-d", path.toString()};
    final Iterable<? extends JavaFileObject> fm_files =
      fm.getJavaFileObjectsFromFiles(files);

    final JavaCompiler.CompilationTask task =
      jc.getTask(
        null,
        null,
        diagnostic -> {
          LOG.error(
            "{}:{}:{}: {}",
            diagnostic.getCode(),
            Long.valueOf(diagnostic.getLineNumber()),
            Long.valueOf(diagnostic.getColumnNumber()),
            diagnostic.getMessage(Locale.getDefault()));
        },
        Arrays.asList(options),
        null,
        fm_files);

    final Boolean result = task.call();
    Assert.assertEquals(Boolean.TRUE, result);
    fm.close();
  }

  private static Path createTemporaryDir()
    throws IOException
  {
    return Files.createTempDirectory("jpra");
  }

  protected abstract JPRAJavaGeneratorType getJavaGenerator();

  @Test
  public final void testRecordEmpty()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final TypeName t_name = TypeName.of(LEX_ZERO, "Empty");
    final TRecordBuilderType rb = TRecord.newBuilder(pc, id, t_name);
    final TRecord r = rb.build();

    compileRecords(
      createTemporaryDir(), g, List.of(r));
  }

  @Test
  public final void testRecordIntegerExhaustive()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final TypeName t_name = TypeName.of(LEX_ZERO, "RecordInteger");
    final TRecordBuilderType rb = TRecord.newBuilder(pc, id, t_name);

    rb.addField(
      FieldName.of(LEX_ZERO, "i8"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(8L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "i16"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(16L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "i32"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(32L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "i64"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(64L)));

    rb.addField(
      FieldName.of(LEX_ZERO, "u8"),
      gc.getFreshIdentifier(),
      new TIntegerUnsigned(LEX_ZERO, Size.valueOf(8L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "u16"),
      gc.getFreshIdentifier(),
      new TIntegerUnsigned(LEX_ZERO, Size.valueOf(16L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "u32"),
      gc.getFreshIdentifier(),
      new TIntegerUnsigned(LEX_ZERO, Size.valueOf(32L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "u64"),
      gc.getFreshIdentifier(),
      new TIntegerUnsigned(LEX_ZERO, Size.valueOf(64L)));

    final TRecord r = rb.build();
    compileRecords(
      createTemporaryDir(), g, List.of(r));
  }

  @Test
  public final void testRecordFloatExhaustive()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final TypeName t_name = TypeName.of(LEX_ZERO, "RecordFloat");
    final TRecordBuilderType rb = TRecord.newBuilder(pc, id, t_name);

    rb.addField(
      FieldName.of(LEX_ZERO, "f16"),
      gc.getFreshIdentifier(),
      new TFloat(LEX_ZERO, Size.valueOf(16L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "f32"),
      gc.getFreshIdentifier(),
      new TFloat(LEX_ZERO, Size.valueOf(32L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "f64"),
      gc.getFreshIdentifier(),
      new TFloat(LEX_ZERO, Size.valueOf(64L)));

    final TRecord r = rb.build();
    compileRecords(
      createTemporaryDir(), g, List.of(r));
  }

  @Test
  public final void testRecordIntegerNormalizedExhaustive()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final TypeName t_name = TypeName.of(LEX_ZERO, "RecordIntegerNormalized");
    final TRecordBuilderType rb = TRecord.newBuilder(pc, id, t_name);

    rb.addField(
      FieldName.of(LEX_ZERO, "isn8"),
      gc.getFreshIdentifier(),
      new TIntegerSignedNormalized(LEX_ZERO, Size.valueOf(8L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "isn16"),
      gc.getFreshIdentifier(),
      new TIntegerSignedNormalized(LEX_ZERO, Size.valueOf(16L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "isn32"),
      gc.getFreshIdentifier(),
      new TIntegerSignedNormalized(LEX_ZERO, Size.valueOf(32L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "isn64"),
      gc.getFreshIdentifier(),
      new TIntegerSignedNormalized(LEX_ZERO, Size.valueOf(64L)));

    rb.addField(
      FieldName.of(LEX_ZERO, "usn8"),
      gc.getFreshIdentifier(),
      new TIntegerUnsignedNormalized(LEX_ZERO, Size.valueOf(8L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "usn16"),
      gc.getFreshIdentifier(),
      new TIntegerUnsignedNormalized(LEX_ZERO, Size.valueOf(16L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "usn32"),
      gc.getFreshIdentifier(),
      new TIntegerUnsignedNormalized(LEX_ZERO, Size.valueOf(32L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "usn64"),
      gc.getFreshIdentifier(),
      new TIntegerUnsignedNormalized(LEX_ZERO, Size.valueOf(64L)));

    final TRecord r = rb.build();
    compileRecords(
      createTemporaryDir(), g, List.of(r));
  }

  @Test
  public final void testRecordPaddingExhaustive()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final TypeName t_name = TypeName.of(LEX_ZERO, "RecordPadding");
    final TRecordBuilderType rb = TRecord.newBuilder(pc, id, t_name);

    rb.addPaddingOctets(LEX_ZERO, Size.valueOf(100L));

    final TRecord r = rb.build();
    compileRecords(
      createTemporaryDir(), g, List.of(r));
  }

  @Test
  public final void testRecordBooleanSetExhaustive()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final TypeName t_name = TypeName.of(LEX_ZERO, "RecordBooleanSet");
    final TRecordBuilderType rb = TRecord.newBuilder(pc, id, t_name);

    rb.addField(
      FieldName.of(LEX_ZERO, "bool"), gc.getFreshIdentifier(), new TBooleanSet(
        LEX_ZERO, List.of(
        FieldName.of(LEX_ZERO, "flag_a"),
        FieldName.of(LEX_ZERO, "flag_b"),
        FieldName.of(LEX_ZERO, "flag_c"),
        FieldName.of(LEX_ZERO, "flag_d"),
        FieldName.of(LEX_ZERO, "flag_e"),
        FieldName.of(LEX_ZERO, "flag_f"),
        FieldName.of(LEX_ZERO, "flag_g"),
        FieldName.of(LEX_ZERO, "flag_h"),
        FieldName.of(LEX_ZERO, "flag_i"),
        FieldName.of(LEX_ZERO, "flag_j"),
        FieldName.of(LEX_ZERO, "flag_k")), Size.valueOf(2L)));

    final TRecord r = rb.build();
    compileRecords(createTemporaryDir(), g, List.of(r));
  }

  @Test
  public final void testRecordFieldRecord()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final Path path = createTemporaryDir();

    final TypeName t_name = TypeName.of(LEX_ZERO, "Empty");
    final TRecordBuilderType teb = TRecord.newBuilder(pc, id, t_name);
    final TRecord te = teb.build();

    final TypeName tr_name = TypeName.of(LEX_ZERO, "RecordRecord");
    final TRecordBuilderType rb = TRecord.newBuilder(pc, id, tr_name);
    rb.addField(FieldName.of(LEX_ZERO, "r"), gc.getFreshIdentifier(), te);

    final TRecord r = rb.build();
    compileRecords(path, g, List.of(te, r));
  }

  @Test
  public final void testRecordFieldRecordTwice()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final Path path = createTemporaryDir();

    final TypeName t_name = TypeName.of(LEX_ZERO, "Empty");
    final TRecordBuilderType teb = TRecord.newBuilder(pc, id, t_name);
    final TRecord te = teb.build();

    final TypeName tr_name = TypeName.of(LEX_ZERO, "RecordRecordTwice");
    final TRecordBuilderType rb = TRecord.newBuilder(pc, id, tr_name);
    rb.addField(FieldName.of(LEX_ZERO, "r0"), gc.getFreshIdentifier(), te);
    rb.addField(FieldName.of(LEX_ZERO, "r1"), gc.getFreshIdentifier(), te);

    final TRecord r = rb.build();
    compileRecords(
      path, g, List.of(te, r));
  }

  @Test
  public final void testPackedEmpty()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final TypeName t_name = TypeName.of(LEX_ZERO, "Empty");
    final TPackedBuilderType rb = TPacked.newBuilder(pc, id, t_name);
    final TPacked r = rb.build();

    compilePackeds(
      createTemporaryDir(), g, List.of(r));
  }

  @Test
  public final void testPackedAllPadding()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final TypeName t_name = TypeName.of(LEX_ZERO, "PackedAllPadding");
    final TPackedBuilderType rb = TPacked.newBuilder(pc, id, t_name);

    rb.addPaddingBits(LEX_ZERO, Size.valueOf(64L));

    final TPacked r = rb.build();
    compilePackeds(createTemporaryDir(), g, List.of(r));
  }

  @Test
  public final void testPackedIntegerU4_U4_U4_U4()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final TypeName t_name = TypeName.of(LEX_ZERO, "PackedIntegerU4_U4_U4_U4");
    final TPackedBuilderType rb = TPacked.newBuilder(pc, id, t_name);

    rb.addField(
      FieldName.of(LEX_ZERO, "r4"),
      gc.getFreshIdentifier(),
      new TIntegerUnsigned(LEX_ZERO, Size.valueOf(4L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "g4"),
      gc.getFreshIdentifier(),
      new TIntegerUnsigned(LEX_ZERO, Size.valueOf(4L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "b4"),
      gc.getFreshIdentifier(),
      new TIntegerUnsigned(LEX_ZERO, Size.valueOf(4L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "a4"),
      gc.getFreshIdentifier(),
      new TIntegerUnsigned(LEX_ZERO, Size.valueOf(4L)));

    final TPacked r = rb.build();
    compilePackeds(
      createTemporaryDir(), g, List.of(r));
  }

  @Test
  public final void testPackedIntegerU8_U8_U8_U8()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final TypeName t_name = TypeName.of(LEX_ZERO, "PackedIntegerU8_U8_U8_U8");
    final TPackedBuilderType rb = TPacked.newBuilder(pc, id, t_name);

    rb.addField(
      FieldName.of(LEX_ZERO, "r8"),
      gc.getFreshIdentifier(),
      new TIntegerUnsigned(LEX_ZERO, Size.valueOf(8L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "g8"),
      gc.getFreshIdentifier(),
      new TIntegerUnsigned(LEX_ZERO, Size.valueOf(8L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "b8"),
      gc.getFreshIdentifier(),
      new TIntegerUnsigned(LEX_ZERO, Size.valueOf(8L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "a8"),
      gc.getFreshIdentifier(),
      new TIntegerUnsigned(LEX_ZERO, Size.valueOf(8L)));

    final TPacked r = rb.build();
    compilePackeds(
      createTemporaryDir(), g, List.of(r));
  }

  @Test
  public final void testPackedIntegerU16_U16_U16_U16()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final TypeName t_name =
      TypeName.of(LEX_ZERO, "PackedIntegerU16_U16_U16_U16");
    final TPackedBuilderType rb = TPacked.newBuilder(pc, id, t_name);

    rb.addField(
      FieldName.of(LEX_ZERO, "r16"),
      gc.getFreshIdentifier(),
      new TIntegerUnsigned(LEX_ZERO, Size.valueOf(16L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "g16"),
      gc.getFreshIdentifier(),
      new TIntegerUnsigned(LEX_ZERO, Size.valueOf(16L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "b16"),
      gc.getFreshIdentifier(),
      new TIntegerUnsigned(LEX_ZERO, Size.valueOf(16L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "a16"),
      gc.getFreshIdentifier(),
      new TIntegerUnsigned(LEX_ZERO, Size.valueOf(16L)));

    final TPacked r = rb.build();
    compilePackeds(
      createTemporaryDir(), g, List.of(r));
  }

  @Test
  public final void testPackedIntegerU2_U2_U2_U2()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final TypeName t_name = TypeName.of(LEX_ZERO, "PackedIntegerU2_U2_U2_U2");
    final TPackedBuilderType rb = TPacked.newBuilder(pc, id, t_name);

    rb.addField(
      FieldName.of(LEX_ZERO, "r2"),
      gc.getFreshIdentifier(),
      new TIntegerUnsigned(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "g2"),
      gc.getFreshIdentifier(),
      new TIntegerUnsigned(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "b2"),
      gc.getFreshIdentifier(),
      new TIntegerUnsigned(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "a2"),
      gc.getFreshIdentifier(),
      new TIntegerUnsigned(LEX_ZERO, Size.valueOf(2L)));

    final TPacked r = rb.build();
    compilePackeds(
      createTemporaryDir(), g, List.of(r));
  }

  @Test
  public final void testPackedIntegerU2_U2_U2_U2_U2_U2_U2_U2()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final TypeName t_name =
      TypeName.of(LEX_ZERO, "PackedIntegerU2_U2_U2_U2_U2_U2_U2_U2");
    final TPackedBuilderType rb = TPacked.newBuilder(pc, id, t_name);

    rb.addField(
      FieldName.of(LEX_ZERO, "r2"),
      gc.getFreshIdentifier(),
      new TIntegerUnsigned(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "g2"),
      gc.getFreshIdentifier(),
      new TIntegerUnsigned(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "b2"),
      gc.getFreshIdentifier(),
      new TIntegerUnsigned(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "a2"),
      gc.getFreshIdentifier(),
      new TIntegerUnsigned(LEX_ZERO, Size.valueOf(2L)));

    rb.addField(
      FieldName.of(LEX_ZERO, "x2"),
      gc.getFreshIdentifier(),
      new TIntegerUnsigned(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "y2"),
      gc.getFreshIdentifier(),
      new TIntegerUnsigned(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "z2"),
      gc.getFreshIdentifier(),
      new TIntegerUnsigned(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "w2"),
      gc.getFreshIdentifier(),
      new TIntegerUnsigned(LEX_ZERO, Size.valueOf(2L)));

    final TPacked r = rb.build();
    compilePackeds(
      createTemporaryDir(), g, List.of(r));
  }

  @Test
  public final void testPackedIntegerS4_S4_S4_S4()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final TypeName t_name = TypeName.of(LEX_ZERO, "PackedIntegerS4_S4_S4_S4");
    final TPackedBuilderType rb = TPacked.newBuilder(pc, id, t_name);

    rb.addField(
      FieldName.of(LEX_ZERO, "r4"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(4L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "g4"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(4L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "b4"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(4L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "a4"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(4L)));

    final TPacked r = rb.build();
    compilePackeds(
      createTemporaryDir(), g, List.of(r));
  }

  @Test
  public final void testPackedIntegerS8_S8_S8_S8()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final TypeName t_name = TypeName.of(LEX_ZERO, "PackedIntegerS8_S8_S8_S8");
    final TPackedBuilderType rb = TPacked.newBuilder(pc, id, t_name);

    rb.addField(
      FieldName.of(LEX_ZERO, "r8"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(8L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "g8"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(8L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "b8"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(8L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "a8"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(8L)));

    final TPacked r = rb.build();
    compilePackeds(
      createTemporaryDir(), g, List.of(r));
  }

  @Test
  public final void testPackedIntegerS16_S16_S16_S16()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final TypeName t_name =
      TypeName.of(LEX_ZERO, "PackedIntegerS16_S16_S16_S16");
    final TPackedBuilderType rb = TPacked.newBuilder(pc, id, t_name);

    rb.addField(
      FieldName.of(LEX_ZERO, "r16"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(16L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "g16"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(16L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "b16"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(16L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "a16"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(16L)));

    final TPacked r = rb.build();
    compilePackeds(
      createTemporaryDir(), g, List.of(r));
  }

  @Test
  public final void testPackedIntegerS2_S2_S2_S2()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final TypeName t_name = TypeName.of(LEX_ZERO, "PackedIntegerS2_S2_S2_S2");
    final TPackedBuilderType rb = TPacked.newBuilder(pc, id, t_name);

    rb.addField(
      FieldName.of(LEX_ZERO, "r2"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "g2"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "b2"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "a2"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(2L)));

    final TPacked r = rb.build();
    compilePackeds(
      createTemporaryDir(), g, List.of(r));
  }

  @Test
  public final void testPackedIntegerS2_S2_S2_S2_S2_S2_S2_S2()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final TypeName t_name =
      TypeName.of(LEX_ZERO, "PackedIntegerS2_S2_S2_S2_S2_S2_S2_S2");
    final TPackedBuilderType rb = TPacked.newBuilder(pc, id, t_name);

    rb.addField(
      FieldName.of(LEX_ZERO, "r2"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "g2"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "b2"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "a2"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(2L)));

    rb.addField(
      FieldName.of(LEX_ZERO, "x2"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "y2"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "z2"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "w2"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(2L)));

    final TPacked r = rb.build();
    compilePackeds(
      createTemporaryDir(), g, List.of(r));
  }

  @Test
  public final void testPackedIntegerUN4_UN4_UN4_UN4()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final TypeName t_name =
      TypeName.of(LEX_ZERO, "PackedIntegerUN4_UN4_UN4_UN4");
    final TPackedBuilderType rb = TPacked.newBuilder(pc, id, t_name);

    rb.addField(
      FieldName.of(LEX_ZERO, "r4"),
      gc.getFreshIdentifier(),
      new TIntegerUnsignedNormalized(LEX_ZERO, Size.valueOf(4L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "g4"),
      gc.getFreshIdentifier(),
      new TIntegerUnsignedNormalized(LEX_ZERO, Size.valueOf(4L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "b4"),
      gc.getFreshIdentifier(),
      new TIntegerUnsignedNormalized(LEX_ZERO, Size.valueOf(4L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "a4"),
      gc.getFreshIdentifier(),
      new TIntegerUnsignedNormalized(LEX_ZERO, Size.valueOf(4L)));

    final TPacked r = rb.build();
    compilePackeds(
      createTemporaryDir(), g, List.of(r));
  }

  @Test
  public final void testPackedIntegerUN8_UN8_UN8_UN8()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final TypeName t_name =
      TypeName.of(LEX_ZERO, "PackedIntegerUN8_UN8_UN8_UN8");
    final TPackedBuilderType rb = TPacked.newBuilder(pc, id, t_name);

    rb.addField(
      FieldName.of(LEX_ZERO, "r8"),
      gc.getFreshIdentifier(),
      new TIntegerUnsignedNormalized(LEX_ZERO, Size.valueOf(8L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "g8"),
      gc.getFreshIdentifier(),
      new TIntegerUnsignedNormalized(LEX_ZERO, Size.valueOf(8L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "b8"),
      gc.getFreshIdentifier(),
      new TIntegerUnsignedNormalized(LEX_ZERO, Size.valueOf(8L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "a8"),
      gc.getFreshIdentifier(),
      new TIntegerUnsignedNormalized(LEX_ZERO, Size.valueOf(8L)));

    final TPacked r = rb.build();
    compilePackeds(
      createTemporaryDir(), g, List.of(r));
  }

  @Test
  public final void testPackedIntegerUN16_UN16_UN16_UN16()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final TypeName t_name =
      TypeName.of(LEX_ZERO, "PackedIntegerUN16_UN16_UN16_UN16");
    final TPackedBuilderType rb = TPacked.newBuilder(pc, id, t_name);

    rb.addField(
      FieldName.of(LEX_ZERO, "r16"),
      gc.getFreshIdentifier(),
      new TIntegerUnsignedNormalized(LEX_ZERO, Size.valueOf(16L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "g16"),
      gc.getFreshIdentifier(),
      new TIntegerUnsignedNormalized(LEX_ZERO, Size.valueOf(16L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "b16"),
      gc.getFreshIdentifier(),
      new TIntegerUnsignedNormalized(LEX_ZERO, Size.valueOf(16L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "a16"),
      gc.getFreshIdentifier(),
      new TIntegerUnsignedNormalized(LEX_ZERO, Size.valueOf(16L)));

    final TPacked r = rb.build();
    compilePackeds(
      createTemporaryDir(), g, List.of(r));
  }

  @Test
  public final void testPackedIntegerUN2_UN2_UN2_UN2()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final TypeName t_name =
      TypeName.of(LEX_ZERO, "PackedIntegerUN2_UN2_UN2_UN2");
    final TPackedBuilderType rb = TPacked.newBuilder(pc, id, t_name);

    rb.addField(
      FieldName.of(LEX_ZERO, "r2"),
      gc.getFreshIdentifier(),
      new TIntegerUnsignedNormalized(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "g2"),
      gc.getFreshIdentifier(),
      new TIntegerUnsignedNormalized(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "b2"),
      gc.getFreshIdentifier(),
      new TIntegerUnsignedNormalized(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "a2"),
      gc.getFreshIdentifier(),
      new TIntegerUnsignedNormalized(LEX_ZERO, Size.valueOf(2L)));

    final TPacked r = rb.build();
    compilePackeds(
      createTemporaryDir(), g, List.of(r));
  }

  @Test
  public final void testPackedIntegerUN2_UN2_UN2_UN2_UN2_UN2_UN2_UN2()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final TypeName t_name =
      TypeName.of(LEX_ZERO, "PackedIntegerUN2_UN2_UN2_UN2_UN2_UN2_UN2_UN2");
    final TPackedBuilderType rb = TPacked.newBuilder(pc, id, t_name);

    rb.addField(
      FieldName.of(LEX_ZERO, "r2"),
      gc.getFreshIdentifier(),
      new TIntegerUnsignedNormalized(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "g2"),
      gc.getFreshIdentifier(),
      new TIntegerUnsignedNormalized(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "b2"),
      gc.getFreshIdentifier(),
      new TIntegerUnsignedNormalized(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "a2"),
      gc.getFreshIdentifier(),
      new TIntegerUnsignedNormalized(LEX_ZERO, Size.valueOf(2L)));

    rb.addField(
      FieldName.of(LEX_ZERO, "x2"),
      gc.getFreshIdentifier(),
      new TIntegerUnsignedNormalized(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "y2"),
      gc.getFreshIdentifier(),
      new TIntegerUnsignedNormalized(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "z2"),
      gc.getFreshIdentifier(),
      new TIntegerUnsignedNormalized(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "w2"),
      gc.getFreshIdentifier(),
      new TIntegerUnsignedNormalized(LEX_ZERO, Size.valueOf(2L)));

    final TPacked r = rb.build();
    compilePackeds(
      createTemporaryDir(), g, List.of(r));
  }

  @Test
  public final void testPackedIntegerSN4_SN4_SN4_SN4()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final TypeName t_name =
      TypeName.of(LEX_ZERO, "PackedIntegerSN4_SN4_SN4_SN4");
    final TPackedBuilderType rb = TPacked.newBuilder(pc, id, t_name);

    rb.addField(
      FieldName.of(LEX_ZERO, "r4"),
      gc.getFreshIdentifier(),
      new TIntegerSignedNormalized(LEX_ZERO, Size.valueOf(4L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "g4"),
      gc.getFreshIdentifier(),
      new TIntegerSignedNormalized(LEX_ZERO, Size.valueOf(4L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "b4"),
      gc.getFreshIdentifier(),
      new TIntegerSignedNormalized(LEX_ZERO, Size.valueOf(4L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "a4"),
      gc.getFreshIdentifier(),
      new TIntegerSignedNormalized(LEX_ZERO, Size.valueOf(4L)));

    final TPacked r = rb.build();
    compilePackeds(
      createTemporaryDir(), g, List.of(r));
  }

  @Test
  public final void testPackedIntegerSN8_SN8_SN8_SN8()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final TypeName t_name =
      TypeName.of(LEX_ZERO, "PackedIntegerSN8_SN8_SN8_SN8");
    final TPackedBuilderType rb = TPacked.newBuilder(pc, id, t_name);

    rb.addField(
      FieldName.of(LEX_ZERO, "r8"),
      gc.getFreshIdentifier(),
      new TIntegerSignedNormalized(LEX_ZERO, Size.valueOf(8L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "g8"),
      gc.getFreshIdentifier(),
      new TIntegerSignedNormalized(LEX_ZERO, Size.valueOf(8L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "b8"),
      gc.getFreshIdentifier(),
      new TIntegerSignedNormalized(LEX_ZERO, Size.valueOf(8L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "a8"),
      gc.getFreshIdentifier(),
      new TIntegerSignedNormalized(LEX_ZERO, Size.valueOf(8L)));

    final TPacked r = rb.build();
    compilePackeds(
      createTemporaryDir(), g, List.of(r));
  }

  @Test
  public final void testPackedIntegerSN16_SN16_SN16_SN16()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final TypeName t_name =
      TypeName.of(LEX_ZERO, "PackedIntegerSN16_SN16_SN16_SN16");
    final TPackedBuilderType rb = TPacked.newBuilder(pc, id, t_name);

    rb.addField(
      FieldName.of(LEX_ZERO, "r16"),
      gc.getFreshIdentifier(),
      new TIntegerSignedNormalized(LEX_ZERO, Size.valueOf(16L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "g16"),
      gc.getFreshIdentifier(),
      new TIntegerSignedNormalized(LEX_ZERO, Size.valueOf(16L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "b16"),
      gc.getFreshIdentifier(),
      new TIntegerSignedNormalized(LEX_ZERO, Size.valueOf(16L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "a16"),
      gc.getFreshIdentifier(),
      new TIntegerSignedNormalized(LEX_ZERO, Size.valueOf(16L)));

    final TPacked r = rb.build();
    compilePackeds(
      createTemporaryDir(), g, List.of(r));
  }

  @Test
  public final void testPackedIntegerSN2_SN2_SN2_SN2()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final TypeName t_name =
      TypeName.of(LEX_ZERO, "PackedIntegerSN2_SN2_SN2_SN2");
    final TPackedBuilderType rb = TPacked.newBuilder(pc, id, t_name);

    rb.addField(
      FieldName.of(LEX_ZERO, "r2"),
      gc.getFreshIdentifier(),
      new TIntegerSignedNormalized(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "g2"),
      gc.getFreshIdentifier(),
      new TIntegerSignedNormalized(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "b2"),
      gc.getFreshIdentifier(),
      new TIntegerSignedNormalized(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "a2"),
      gc.getFreshIdentifier(),
      new TIntegerSignedNormalized(LEX_ZERO, Size.valueOf(2L)));

    final TPacked r = rb.build();
    compilePackeds(
      createTemporaryDir(), g, List.of(r));
  }

  @Test
  public final void testPackedIntegerSN2_SN2_SN2_SN2_SN2_SN2_SN2_SN2()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final TypeName t_name =
      TypeName.of(LEX_ZERO, "PackedIntegerSN2_SN2_SN2_SN2_SN2_SN2_SN2_SN2");
    final TPackedBuilderType rb = TPacked.newBuilder(pc, id, t_name);

    rb.addField(
      FieldName.of(LEX_ZERO, "r2"),
      gc.getFreshIdentifier(),
      new TIntegerSignedNormalized(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "g2"),
      gc.getFreshIdentifier(),
      new TIntegerSignedNormalized(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "b2"),
      gc.getFreshIdentifier(),
      new TIntegerSignedNormalized(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "a2"),
      gc.getFreshIdentifier(),
      new TIntegerSignedNormalized(LEX_ZERO, Size.valueOf(2L)));

    rb.addField(
      FieldName.of(LEX_ZERO, "x2"),
      gc.getFreshIdentifier(),
      new TIntegerSignedNormalized(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "y2"),
      gc.getFreshIdentifier(),
      new TIntegerSignedNormalized(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "z2"),
      gc.getFreshIdentifier(),
      new TIntegerSignedNormalized(LEX_ZERO, Size.valueOf(2L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "w2"),
      gc.getFreshIdentifier(),
      new TIntegerSignedNormalized(LEX_ZERO, Size.valueOf(2L)));

    final TPacked r = rb.build();
    compilePackeds(
      createTemporaryDir(), g, List.of(r));
  }

  @Test
  public final void testPackedIntegerU4_S4_UN4_SN4()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final TypeName t_name = TypeName.of(LEX_ZERO, "PackedIntegerU4_S4_UN4_SN4");
    final TPackedBuilderType rb = TPacked.newBuilder(pc, id, t_name);

    rb.addField(
      FieldName.of(LEX_ZERO, "u4"),
      gc.getFreshIdentifier(),
      new TIntegerUnsigned(LEX_ZERO, Size.valueOf(4L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "s4"),
      gc.getFreshIdentifier(),
      new TIntegerSigned(LEX_ZERO, Size.valueOf(4L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "un4"),
      gc.getFreshIdentifier(),
      new TIntegerUnsignedNormalized(LEX_ZERO, Size.valueOf(4L)));
    rb.addField(
      FieldName.of(LEX_ZERO, "sn4"),
      gc.getFreshIdentifier(),
      new TIntegerSignedNormalized(LEX_ZERO, Size.valueOf(4L)));

    final TPacked r = rb.build();
    compilePackeds(
      createTemporaryDir(), g, List.of(r));
  }

  @Test
  public final void testRecordVectorExhaustive()
    throws Exception
  {
    final JPRAJavaGeneratorType g = this.getJavaGenerator();
    final GlobalContextType gc =
      GlobalContexts.newContext(new AlwaysEmptyLoader());
    final PackageContextType pc = gc.loadPackage(
      new PackageNameQualified(
        List.of(
          PackageNameUnqualified.of(LEX_ZERO, "x"),
          PackageNameUnqualified.of(LEX_ZERO, "y"),
          PackageNameUnqualified.of(LEX_ZERO, "z"))));

    final IdentifierType id = gc.getFreshIdentifier();
    final TypeName t_name = TypeName.of(LEX_ZERO, "RecordVector");
    final TRecordBuilderType rb = TRecord.newBuilder(pc, id, t_name);

    final TIntegerSigned t_int =
      new TIntegerSigned(LEX_ZERO, Size.valueOf(32L));
    final TIntegerSigned t_long =
      new TIntegerSigned(LEX_ZERO, Size.valueOf(64L));
    final TFloat t_float =
      new TFloat(LEX_ZERO, Size.valueOf(32L));
    final TFloat t_double =
      new TFloat(LEX_ZERO, Size.valueOf(64L));

    rb.addField(
      FieldName.of(LEX_ZERO, "v2i"), gc.getFreshIdentifier(),
      new TVector(LEX_ZERO, Size.valueOf(2L), t_int));
    rb.addField(
      FieldName.of(LEX_ZERO, "v3i"), gc.getFreshIdentifier(),
      new TVector(LEX_ZERO, Size.valueOf(3L), t_int));
    rb.addField(
      FieldName.of(LEX_ZERO, "v4i"), gc.getFreshIdentifier(),
      new TVector(LEX_ZERO, Size.valueOf(4L), t_int));

    rb.addField(
      FieldName.of(LEX_ZERO, "v2l"), gc.getFreshIdentifier(),
      new TVector(LEX_ZERO, Size.valueOf(2L), t_long));
    rb.addField(
      FieldName.of(LEX_ZERO, "v3l"), gc.getFreshIdentifier(),
      new TVector(LEX_ZERO, Size.valueOf(3L), t_long));
    rb.addField(
      FieldName.of(LEX_ZERO, "v4l"), gc.getFreshIdentifier(),
      new TVector(LEX_ZERO, Size.valueOf(4L), t_long));

    rb.addField(
      FieldName.of(LEX_ZERO, "v2f"), gc.getFreshIdentifier(),
      new TVector(LEX_ZERO, Size.valueOf(2L), t_float));
    rb.addField(
      FieldName.of(LEX_ZERO, "v3f"), gc.getFreshIdentifier(),
      new TVector(LEX_ZERO, Size.valueOf(3L), t_float));
    rb.addField(
      FieldName.of(LEX_ZERO, "v4f"), gc.getFreshIdentifier(),
      new TVector(LEX_ZERO, Size.valueOf(4L), t_float));

    rb.addField(
      FieldName.of(LEX_ZERO, "v2d"), gc.getFreshIdentifier(),
      new TVector(LEX_ZERO, Size.valueOf(2L), t_double));
    rb.addField(
      FieldName.of(LEX_ZERO, "v3d"), gc.getFreshIdentifier(),
      new TVector(LEX_ZERO, Size.valueOf(3L), t_double));
    rb.addField(
      FieldName.of(LEX_ZERO, "v4d"), gc.getFreshIdentifier(),
      new TVector(LEX_ZERO, Size.valueOf(4L), t_double));

    final TRecord r = rb.build();
    compileRecords(
      createTemporaryDir(), g, List.of(r));
  }
}
