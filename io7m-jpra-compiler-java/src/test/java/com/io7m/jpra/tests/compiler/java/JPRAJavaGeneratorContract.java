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
import com.io7m.jpra.model.names.IdentifierType;
import com.io7m.jpra.model.names.PackageNameQualified;
import com.io7m.jpra.model.names.PackageNameUnqualified;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.jpra.model.types.TRecord;
import com.io7m.jpra.model.types.TRecordBuilderType;
import org.junit.Test;

import java.nio.file.Path;
import java.util.Optional;

public abstract class JPRAJavaGeneratorContract
{
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
    final TRecordBuilderType rb =
      TRecord.newBuilder(pc, id, new TypeName(no_lex, "Empty"));

    final TRecord r = rb.build();
    g.generateRecordImplementation(r, System.out);
  }
}
