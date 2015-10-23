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

package com.io7m.jpra.tests.compiler.core.parser;

import com.gs.collections.api.list.ImmutableList;
import com.io7m.jpra.compiler.core.JPRACompilerException;
import com.io7m.jpra.compiler.core.parser.JPRAAbstractParserEventListener;
import com.io7m.jpra.compiler.core.parser.JPRAParseErrorCode;
import com.io7m.jpra.compiler.core.parser.JPRAParserREPLEventListenerType;
import com.io7m.jpra.compiler.core.parser.JPRAParserType;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.names.PackageNameQualified;
import com.io7m.jpra.model.names.PackageNameUnqualified;
import com.io7m.jpra.model.SizeExprConstant;
import com.io7m.jpra.model.SizeExprInBits;
import com.io7m.jpra.model.SizeExprInOctets;
import com.io7m.jpra.model.SizeExprType;
import com.io7m.jpra.model.SizeUnitOctetsType;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.jpra.model.type_expressions.TypeExprArray;
import com.io7m.jpra.model.type_expressions.TypeExprBooleanSet;
import com.io7m.jpra.model.type_expressions.TypeExprFloat;
import com.io7m.jpra.model.type_expressions.TypeExprIntegerSigned;
import com.io7m.jpra.model.type_expressions.TypeExprIntegerSignedNormalized;
import com.io7m.jpra.model.type_expressions.TypeExprIntegerUnsigned;
import com.io7m.jpra.model.type_expressions.TypeExprIntegerUnsignedNormalized;
import com.io7m.jpra.model.type_expressions.TypeExprMatrix;
import com.io7m.jpra.model.type_expressions.TypeExprNamePT;
import com.io7m.jpra.model.type_expressions.TypeExprNameT;
import com.io7m.jpra.model.type_expressions.TypeExprScalarType;
import com.io7m.jpra.model.type_expressions.TypeExprString;
import com.io7m.jpra.model.type_expressions.TypeExprType;
import com.io7m.jpra.model.type_expressions.TypeExprVector;
import com.io7m.jsx.parser.JSXParserGrammarException;
import com.io7m.jsx.parser.JSXParserType;
import com.io7m.junreachable.UnreachableCodeException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class JPRAParserContract<P extends JPRAParserType>
{
  @Rule public ExpectedException expected = ExpectedException.none();

  protected abstract JSXParserType newSExpressionParser(String name);

  protected abstract P newParser();

  @Test public final void testImport()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-import-0.jpr");
    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onImport(
          final JPRAParserType p,
          final PackageNameQualified p_name,
          final PackageNameUnqualified up_name)
        {
          final ImmutableList<PackageNameUnqualified> elems = p_name.getValue();
          Assert.assertEquals(3L, (long) elems.size());
          Assert.assertEquals(
            new PackageNameUnqualified(Optional.empty(), "x"), elems.get(0));
          Assert.assertEquals(
            new PackageNameUnqualified(Optional.empty(), "y"), elems.get(1));
          Assert.assertEquals(
            new PackageNameUnqualified(Optional.empty(), "z"), elems.get(2));
          Assert.assertEquals("x.y.z", p_name.toString());
          Assert.assertEquals("k", up_name.getValue());
          called.set(true);
        }
      });

    Assert.assertTrue(called.get());
  }

  @Test public final void testPackageBegin()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-package-begin-0.jpr");
    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onPackageBegin(
          final JPRAParserType p,
          final PackageNameQualified name)
        {
          final ImmutableList<PackageNameUnqualified> elems = name.getValue();
          Assert.assertEquals(3L, (long) elems.size());
          Assert.assertEquals(
            new PackageNameUnqualified(Optional.empty(), "x"), elems.get(0));
          Assert.assertEquals(
            new PackageNameUnqualified(Optional.empty(), "y"), elems.get(1));
          Assert.assertEquals(
            new PackageNameUnqualified(Optional.empty(), "z"), elems.get(2));
          Assert.assertEquals("x.y.z", name.toString());
          called.set(true);
        }
      });

    Assert.assertTrue(called.get());
  }

  @Test public final void testPackageEnd()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-package-end-0.jpr");
    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onPackageEnd(final JPRAParserType p)
        {
          called.set(true);
        }
      });

    Assert.assertTrue(called.get());
  }

  @Test public final void testPackageEndInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-package-end-invalid-0.jpr");

    this.expected.expect(JSXParserGrammarException.class);

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testPackageEndInvalid1()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-package-end-invalid-1.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testPackageBeginInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-package-begin-invalid-0.jpr");

    this.expected.expect(JSXParserGrammarException.class);

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testPackageBeginInvalid1()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-package-begin-invalid-1.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testPackageBeginInvalid2()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-package-begin-invalid-2.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testPackageBeginInvalid3()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-package-begin-invalid-3.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testPackageBeginInvalid4()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-package-begin-invalid-4.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.BAD_PACKAGE_NAME));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testImportInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-import-invalid-0.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testImportInvalid1()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-import-invalid-1.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testImportInvalid2()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-import-invalid-2.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testImportInvalid3()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-import-invalid-3.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testImportInvalid4()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-import-invalid-4.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testImportInvalid5()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-import-invalid-5.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.BAD_PACKAGE_NAME));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testImportInvalid6()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-import-invalid-6.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.BAD_PACKAGE_NAME));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testNonsense0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-nonsense-0.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_LIST_GOT_SYMBOL));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testNonsense1()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-nonsense-1.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_LIST_GOT_QUOTED_STRING));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testNonsense2()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-nonsense-2.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_NON_EMPTY_LIST));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testNonsense3()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-nonsense-3.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.UNRECOGNIZED_KEYWORD));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testNonsense4()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-nonsense-4.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_SYMBOL_GOT_QUOTED_STRING));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testNonsense5()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-nonsense-5.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_SYMBOL_GOT_LIST));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testRecordInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-record-invalid-0.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testRecordInvalid1()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-record-invalid-1.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testRecordInvalid2()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-record-invalid-2.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.BAD_TYPE_NAME));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testRecordInvalid3()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-record-invalid-3.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_LIST_GOT_SYMBOL));

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onRecordBegin(
          final JPRAParserType p,
          final TypeName t)
          throws JPRACompilerException
        {
          Assert.assertEquals(new TypeName(Optional.empty(), "T"), t);
        }
      });
  }

  @Test public final void testRecordInvalid4()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-record-invalid-4.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.UNRECOGNIZED_RECORD_FIELD_KEYWORD));

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onRecordBegin(
          final JPRAParserType p,
          final TypeName t)
          throws JPRACompilerException
        {
          Assert.assertEquals(new TypeName(Optional.empty(), "T"), t);
        }
      });
  }

  @Test public final void testRecordInvalid5()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-record-invalid-5.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_NON_EMPTY_LIST));

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onRecordBegin(
          final JPRAParserType p,
          final TypeName t)
          throws JPRACompilerException
        {
          Assert.assertEquals(new TypeName(Optional.empty(), "T"), t);
        }
      });
  }

  @Test public final void testRecordInvalid6()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-record-invalid-6.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.DUPLICATE_FIELD_NAME));

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onRecordBegin(
          final JPRAParserType p,
          final TypeName t)
          throws JPRACompilerException
        {
          Assert.assertEquals(new TypeName(Optional.empty(), "T"), t);
        }

        @Override public void onRecordFieldValue(
          final JPRAParserType p,
          final FieldName name,
          final TypeExprType type)
        {
          Assert.assertEquals(new FieldName(Optional.empty(), "f0"), name);
        }
      });
  }

  @Test public final void testRecordFieldInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-record-field-invalid-0.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onRecordBegin(
          final JPRAParserType p,
          final TypeName t)
          throws JPRACompilerException
        {
          Assert.assertEquals(t, new TypeName(Optional.empty(), "T"));
        }
      });
  }

  @Test public final void testRecordFieldInvalid1()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-record-field-invalid-1.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.BAD_FIELD_NAME));

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onRecordBegin(
          final JPRAParserType p,
          final TypeName t)
          throws JPRACompilerException
        {
          Assert.assertEquals(t, new TypeName(Optional.empty(), "T"));
        }
      });
  }

  @Test public final void testRecordFieldInvalid3()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-record-field-invalid-3.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.BAD_TYPE_REFERENCE));

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onRecordBegin(
          final JPRAParserType p,
          final TypeName t)
          throws JPRACompilerException
        {
          Assert.assertEquals(t, new TypeName(Optional.empty(), "T"));
        }
      });
  }

  @Test public final void testRecordFieldInvalid6()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-record-field-invalid-6.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_LIST_GOT_QUOTED_STRING));

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onRecordBegin(
          final JPRAParserType p,
          final TypeName t)
          throws JPRACompilerException
        {
          Assert.assertEquals(t, new TypeName(Optional.empty(), "T"));
        }
      });
  }

  @Test public final void testRecordPaddingOctetsInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-record-padding-octets-invalid-0.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onRecordBegin(
          final JPRAParserType p,
          final TypeName t)
          throws JPRACompilerException
        {
          Assert.assertEquals(t, new TypeName(Optional.empty(), "T"));
        }
      });
  }

  @Test public final void testRecord0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-record-0.jpr");
    final AtomicInteger called_begin = new AtomicInteger(0);
    final AtomicInteger called_field = new AtomicInteger(0);
    final AtomicInteger called_end = new AtomicInteger(0);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onRecordBegin(
          final JPRAParserType p,
          final TypeName t)
          throws JPRACompilerException
        {
          Assert.assertEquals(0L, (long) called_begin.get());
          Assert.assertEquals(0L, (long) called_field.get());
          Assert.assertEquals(0L, (long) called_end.get());

          called_begin.incrementAndGet();
          Assert.assertEquals(new TypeName(Optional.empty(), "T"), t);
        }

        @Override public void onRecordEnd(final JPRAParserType p)
          throws JPRACompilerException
        {
          Assert.assertEquals(1L, (long) called_begin.get());
          Assert.assertEquals(1L, (long) called_field.get());
          Assert.assertEquals(0L, (long) called_end.get());

          called_end.incrementAndGet();
        }

        @Override public void onRecordFieldValue(
          final JPRAParserType p,
          final FieldName name,
          final TypeExprType type)
        {
          Assert.assertEquals(1L, (long) called_begin.get());
          Assert.assertEquals(0L, (long) called_field.get());
          Assert.assertEquals(0L, (long) called_end.get());

          called_field.incrementAndGet();

          final FieldName field_name = new FieldName(Optional.empty(), "f0");
          Assert.assertEquals(field_name, name);
        }
      });

    Assert.assertEquals(1L, (long) called_begin.get());
    Assert.assertEquals(1L, (long) called_field.get());
    Assert.assertEquals(1L, (long) called_end.get());
  }

  @Test public final void testRecord1()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-record-1.jpr");
    final AtomicInteger called_begin = new AtomicInteger(0);
    final AtomicInteger called_field = new AtomicInteger(0);
    final AtomicInteger called_end = new AtomicInteger(0);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onRecordBegin(
          final JPRAParserType p,
          final TypeName t)
          throws JPRACompilerException
        {
          Assert.assertEquals(0L, (long) called_begin.get());
          Assert.assertEquals(0L, (long) called_field.get());
          Assert.assertEquals(0L, (long) called_end.get());

          called_begin.incrementAndGet();
          Assert.assertEquals(new TypeName(Optional.empty(), "T"), t);
        }

        @Override public void onRecordEnd(final JPRAParserType p)
          throws JPRACompilerException
        {
          Assert.assertEquals(1L, (long) called_begin.get());
          Assert.assertEquals(1L, (long) called_field.get());
          Assert.assertEquals(0L, (long) called_end.get());

          called_end.incrementAndGet();
        }

        @Override public void onRecordFieldPaddingOctets(
          final JPRAParserType p,
          final SizeExprType<SizeUnitOctetsType> size)
          throws JPRACompilerException
        {
          Assert.assertEquals(1L, (long) called_begin.get());
          Assert.assertEquals(0L, (long) called_field.get());
          Assert.assertEquals(0L, (long) called_end.get());

          called_field.incrementAndGet();
        }
      });

    Assert.assertEquals(1L, (long) called_begin.get());
    Assert.assertEquals(1L, (long) called_field.get());
    Assert.assertEquals(1L, (long) called_end.get());
  }

  @Test public final void testTypeIntegerInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-integer-invalid-0.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-type-invalid-0.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_NON_EMPTY_LIST));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeInvalid1()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-type-invalid-1.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.UNRECOGNIZED_TYPE_KEYWORD));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeInvalid2()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-type-invalid-2.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_SYMBOL_OR_LIST_GOT_QUOTED_STRING));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeInvalid3()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-type-invalid-3.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeIntegerInvalid1()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-integer-invalid-1.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeIntegerInvalid2()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-integer-invalid-2.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.UNRECOGNIZED_INTEGER_TYPE_KEYWORD));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeIntegerUnsignedInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-integer-unsigned-invalid-0.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.INVALID_INTEGER_CONSTANT));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeIntegerUnsigned0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-integer-unsigned-0.jpr");

    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onREPLType(
          final JPRAParserType p,
          final TypeExprType t)
        {
          called.set(true);
          Assert.assertEquals(TypeExprIntegerUnsigned.class, t.getClass());
        }
      });

    Assert.assertTrue(called.get());
  }

  @Test public final void testTypeIntegerUnsignedNormalized0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-integer-unsigned-normalized-0.jpr");

    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onREPLType(
          final JPRAParserType p,
          final TypeExprType t)
        {
          called.set(true);
          Assert.assertEquals(
            TypeExprIntegerUnsignedNormalized.class, t.getClass());
        }
      });

    Assert.assertTrue(called.get());
  }

  @Test public final void testTypeIntegerSignedNormalized0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-integer-signed-normalized-0.jpr");

    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onREPLType(
          final JPRAParserType p,
          final TypeExprType t)
        {
          called.set(true);
          Assert.assertEquals(
            TypeExprIntegerSignedNormalized.class, t.getClass());
        }
      });

    Assert.assertTrue(called.get());
  }

  @Test public final void testTypeIntegerUnsignedNormalizedInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser(
      "t-type-integer-unsigned-normalized-invalid-0.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.INVALID_INTEGER_CONSTANT));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeIntegerSignedInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-integer-signed-invalid-0.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.INVALID_INTEGER_CONSTANT));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeIntegerSignedNormalizedInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser(
      "t-type-integer-signed-normalized-invalid-0.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.INVALID_INTEGER_CONSTANT));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeIntegerSigned0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-integer-signed-0.jpr");

    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onREPLType(
          final JPRAParserType p,
          final TypeExprType t)
        {
          called.set(true);
          Assert.assertEquals(TypeExprIntegerSigned.class, t.getClass());
        }
      });

    Assert.assertTrue(called.get());
  }

  @Test public final void testSizeConstant0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-size-constant-0.jpr");
    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onREPLSize(
          final JPRAParserType p,
          final SizeExprType<?> t)
        {
          called.set(true);

          Assert.assertEquals(SizeExprConstant.class, t.getClass());
          final SizeExprConstant<?> sc = (SizeExprConstant<?>) t;
          Assert.assertEquals(BigInteger.valueOf(23L), sc.getValue());
        }
      });

    Assert.assertTrue(called.get());
  }

  @Test public final void testSizeInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-size-invalid-0.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.INVALID_INTEGER_CONSTANT));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testSizeInvalid1()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-size-invalid-1.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_SYMBOL_OR_LIST_GOT_QUOTED_STRING));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testSizeInvalid2()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-size-invalid-2.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_NON_EMPTY_LIST));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testSizeInvalid3()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-size-invalid-3.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testSizeInvalid4()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-size-invalid-4.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.UNRECOGNIZED_SIZE_FUNCTION));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testSizeOctetsInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-size-octets-invalid-0.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testSizeOctets0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-size-octets-0.jpr");

    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onREPLSize(
          final JPRAParserType p,
          final SizeExprType<?> t)
        {
          called.set(true);
          Assert.assertEquals(SizeExprInOctets.class, t.getClass());
          final SizeExprInOctets s = (SizeExprInOctets) t;
          final TypeExprType e = s.getTypeExpression();
          Assert.assertEquals(TypeExprNameT.class, e.getClass());
          final TypeExprNameT r = (TypeExprNameT) e;
          final TypeName n = r.getName();
          Assert.assertEquals(new TypeName(Optional.empty(), "T"), n);
        }
      });

    Assert.assertTrue(called.get());
  }

  @Test public final void testSizeBits0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-size-bits-0.jpr");

    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onREPLSize(
          final JPRAParserType p,
          final SizeExprType<?> t)
        {
          called.set(true);
          Assert.assertEquals(SizeExprInBits.class, t.getClass());
          final SizeExprInBits s = (SizeExprInBits) t;
          final TypeExprType e = s.getTypeExpression();
          Assert.assertEquals(TypeExprNameT.class, e.getClass());
          final TypeExprNameT r = (TypeExprNameT) e;
          final TypeName n = r.getName();
          Assert.assertEquals(new TypeName(Optional.empty(), "T"), n);
        }
      });

    Assert.assertTrue(called.get());
  }

  @Test public final void testSizeOctetsInvalid1()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-size-octets-invalid-1.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testSizeBitsInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-size-bits-invalid-0.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testSizeBitsInvalid1()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-size-bits-invalid-1.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeFloatInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-float-invalid-0.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeFloatInvalid1()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-float-invalid-1.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.INVALID_INTEGER_CONSTANT));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeFloatInvalid2()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-float-invalid-2.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeVectorInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-vector-invalid-0.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeVectorInvalid1()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-vector-invalid-1.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeVectorInvalid2()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-vector-invalid-2.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.INVALID_INTEGER_CONSTANT));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeVectorInvalid3()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-vector-invalid-3.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_SCALAR_TYPE_EXPRESSION));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeMatrixInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-matrix-invalid-0.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeMatrixInvalid1()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-matrix-invalid-1.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeMatrixInvalid2()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-matrix-invalid-2.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeMatrixInvalid3()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-matrix-invalid-3.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.INVALID_INTEGER_CONSTANT));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeMatrixInvalid4()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-matrix-invalid-4.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.INVALID_INTEGER_CONSTANT));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeMatrixInvalid5()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-matrix-invalid-5.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_SCALAR_TYPE_EXPRESSION));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeVector0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-type-vector-0.jpr");

    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onREPLType(
          final JPRAParserType p,
          final TypeExprType t)
        {
          called.set(true);
          Assert.assertEquals(TypeExprVector.class, t.getClass());
          final TypeExprVector v = (TypeExprVector) t;
          final TypeExprScalarType et = v.getElementType();
          Assert.assertEquals(TypeExprFloat.class, et.getClass());
          final SizeExprType<?> se = v.getElementCountExpression();
          Assert.assertEquals(SizeExprConstant.class, se.getClass());
          final SizeExprConstant<?> s = (SizeExprConstant<?>) se;
          Assert.assertEquals(BigInteger.valueOf(3L), s.getValue());
        }
      });

    Assert.assertTrue(called.get());
  }

  @Test public final void testTypeMatrix0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-type-matrix-0.jpr");

    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onREPLType(
          final JPRAParserType p,
          final TypeExprType t)
        {
          called.set(true);
          Assert.assertEquals(TypeExprMatrix.class, t.getClass());
          final TypeExprMatrix v = (TypeExprMatrix) t;
          final TypeExprScalarType et = v.getElementType();
          Assert.assertEquals(TypeExprFloat.class, et.getClass());
          final SizeExprType<?> we = v.getWidthExpression();
          final SizeExprType<?> he = v.getHeightExpression();
          Assert.assertEquals(SizeExprConstant.class, we.getClass());
          Assert.assertEquals(SizeExprConstant.class, he.getClass());
          final SizeExprConstant<?> w = (SizeExprConstant<?>) we;
          final SizeExprConstant<?> h = (SizeExprConstant<?>) he;
          Assert.assertEquals(BigInteger.valueOf(2L), w.getValue());
          Assert.assertEquals(BigInteger.valueOf(4L), h.getValue());
        }
      });

    Assert.assertTrue(called.get());
  }

  @Test public final void testTypeFloat0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-type-float-0.jpr");

    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onREPLType(
          final JPRAParserType p,
          final TypeExprType t)
        {
          called.set(true);
          Assert.assertEquals(TypeExprFloat.class, t.getClass());
        }
      });

    Assert.assertTrue(called.get());
  }

  @Test public final void testTypeArrayInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-array-invalid-0.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeArrayInvalid1()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-array-invalid-1.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeArrayInvalid2()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-array-invalid-2.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.INVALID_INTEGER_CONSTANT));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeArrayInvalid3()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-array-invalid-3.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.INVALID_INTEGER_CONSTANT));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeStringInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-string-invalid-0.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeStringInvalid1()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-string-invalid-1.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeStringInvalid2()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-string-invalid-2.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeStringInvalid3()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-string-invalid-3.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeString0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-type-string-0.jpr");

    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onREPLType(
          final JPRAParserType p,
          final TypeExprType t)
        {
          called.set(true);
          Assert.assertEquals(TypeExprString.class, t.getClass());
          final TypeExprString v = (TypeExprString) t;
          Assert.assertEquals("UTF-8", v.getEncoding());
          final SizeExprType<SizeUnitOctetsType> se = v.getSizeExpression();
          Assert.assertEquals(SizeExprConstant.class, se.getClass());
          final SizeExprConstant<SizeUnitOctetsType> s =
            (SizeExprConstant<SizeUnitOctetsType>) se;
          Assert.assertEquals(BigInteger.valueOf(64L), s.getValue());
        }
      });

    Assert.assertTrue(called.get());
  }

  @Test public final void testTypeArray0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-type-array-0.jpr");

    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onREPLType(
          final JPRAParserType p,
          final TypeExprType t)
        {
          called.set(true);
          Assert.assertEquals(TypeExprArray.class, t.getClass());
          final TypeExprArray v = (TypeExprArray) t;
          final TypeExprType et = v.getElementType();
          Assert.assertEquals(TypeExprFloat.class, et.getClass());
          final SizeExprType<?> se = v.getElementCountExpression();
          Assert.assertEquals(SizeExprConstant.class, se.getClass());
          final SizeExprConstant<?> s = (SizeExprConstant<?>) se;
          Assert.assertEquals(BigInteger.valueOf(64L), s.getValue());
        }
      });

    Assert.assertTrue(called.get());
  }

  @Test public final void testTypeBooleanSetInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-boolean-set-invalid-0.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeBooleanSetInvalid1()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-boolean-set-invalid-1.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeBooleanSetInvalid2()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-boolean-set-invalid-2.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.INVALID_INTEGER_CONSTANT));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeBooleanSetInvalid3()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-boolean-set-invalid-3.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_LIST_GOT_SYMBOL));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeBooleanSetInvalid4()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-boolean-set-invalid-4.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.BAD_FIELD_NAME));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeBooleanSetInvalid5()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-boolean-set-invalid-5.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.DUPLICATE_FIELD_NAME));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeBooleanSetInvalid6()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-boolean-set-invalid-6.jpr");

    this.expected.expect(
      new JPRACompilerParseExceptionMatcher(
        JPRAParseErrorCode.EXPECTED_LIST_GOT_QUOTED_STRING));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeReference0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q = this.newSExpressionParser("t-type-reference-0.jpr");

    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onREPLType(
          final JPRAParserType p,
          final TypeExprType t)
        {
          called.set(true);
          Assert.assertEquals(TypeExprNamePT.class, t.getClass());
          final TypeExprNamePT v = (TypeExprNamePT) t;
          final PackageNameUnqualified pn = v.getPackage();
          Assert.assertEquals("x", pn.getValue());
          Assert.assertEquals("T", v.getType().getValue());
        }
      });

    Assert.assertTrue(called.get());
  }

  @Test public final void testTypeBooleanSet0()
    throws Exception
  {
    final P p = this.newParser();
    final JSXParserType q =
      this.newSExpressionParser("t-type-boolean-set-0.jpr");

    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onREPLType(
          final JPRAParserType p,
          final TypeExprType t)
        {
          called.set(true);
          Assert.assertEquals(TypeExprBooleanSet.class, t.getClass());
          final TypeExprBooleanSet v = (TypeExprBooleanSet) t;
          final ImmutableList<FieldName> fields =
            v.getFieldsInDeclarationOrder();
          Assert.assertEquals(3L, (long) fields.size());
          Assert.assertEquals("a", fields.get(0).getValue());
          Assert.assertEquals("b", fields.get(1).getValue());
          Assert.assertEquals("c", fields.get(2).getValue());

          final SizeExprType<SizeUnitOctetsType> se = v.getSizeExpression();
          Assert.assertEquals(SizeExprConstant.class, se.getClass());
          final SizeExprConstant<?> sc = (SizeExprConstant<?>) se;
          Assert.assertEquals(BigInteger.valueOf(1L), sc.getValue());
        }
      });

    Assert.assertTrue(called.get());
  }

  private static class CheckedListener extends JPRAAbstractParserEventListener
    implements JPRAParserREPLEventListenerType
  {
    private static final Logger LOG;

    static {
      LOG = LoggerFactory.getLogger(CheckedListener.class);
    }

    @Override public void onImport(
      final JPRAParserType p,
      final PackageNameQualified p_name,
      final PackageNameUnqualified up_name)
      throws JPRACompilerException
    {
      super.onImport(p, p_name, up_name);
      throw new UnreachableCodeException();
    }

    @Override
    public void onPackageBegin(
      final JPRAParserType p,
      final PackageNameQualified name)
      throws JPRACompilerException
    {
      super.onPackageBegin(p, name);
      throw new UnreachableCodeException();
    }

    @Override public void onPackageEnd(final JPRAParserType p)
      throws JPRACompilerException
    {
      super.onPackageEnd(p);
      throw new UnreachableCodeException();
    }

    @Override
    public void onRecordBegin(
      final JPRAParserType p,
      final TypeName t)
      throws JPRACompilerException
    {
      super.onRecordBegin(p, t);
      throw new UnreachableCodeException();
    }

    @Override public void onRecordEnd(final JPRAParserType p)
      throws JPRACompilerException
    {
      super.onRecordEnd(p);
      throw new UnreachableCodeException();
    }

    @Override public void onRecordFieldPaddingOctets(
      final JPRAParserType p,
      final SizeExprType<SizeUnitOctetsType> size)
      throws JPRACompilerException
    {
      super.onRecordFieldPaddingOctets(p, size);
      throw new UnreachableCodeException();
    }

    @Override public void onRecordFieldValue(
      final JPRAParserType p,
      final FieldName name,
      final TypeExprType type)
    {
      super.onRecordFieldValue(p, name, type);
      throw new UnreachableCodeException();
    }

    @Override
    public void onREPLSize(
      final JPRAParserType p,
      final SizeExprType<?> t)
    {
      super.onREPLSize(p, t);
      throw new UnreachableCodeException();
    }

    @Override
    public void onREPLType(
      final JPRAParserType p,
      final TypeExprType t)
    {
      super.onREPLType(p, t);
      throw new UnreachableCodeException();
    }
  }
}
