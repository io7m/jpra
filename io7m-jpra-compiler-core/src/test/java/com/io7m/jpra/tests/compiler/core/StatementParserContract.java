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

package com.io7m.jpra.tests.compiler.core;

import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.map.ImmutableMap;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.compiler.core.LexicalContextType;
import com.io7m.jpra.compiler.core.parser.CompilerParseException;
import com.io7m.jpra.compiler.core.parser.ParseErrorCode;
import com.io7m.jpra.compiler.core.parser.StatementParserREPLEventListenerType;
import com.io7m.jpra.compiler.core.parser.StatementParserType;
import com.io7m.jpra.model.FieldName;
import com.io7m.jpra.model.PackageNameQualified;
import com.io7m.jpra.model.PackageNameUnqualified;
import com.io7m.jpra.model.RecordFieldDeclPaddingOctets;
import com.io7m.jpra.model.RecordFieldDeclType;
import com.io7m.jpra.model.RecordFieldDeclValue;
import com.io7m.jpra.model.SizeConstant;
import com.io7m.jpra.model.SizeExprType;
import com.io7m.jpra.model.SizeInBits;
import com.io7m.jpra.model.SizeInOctets;
import com.io7m.jpra.model.SizeUnitOctetsType;
import com.io7m.jpra.model.TypeDeclRecord;
import com.io7m.jpra.model.TypeExprArray;
import com.io7m.jpra.model.TypeExprBooleanSet;
import com.io7m.jpra.model.TypeExprFloat;
import com.io7m.jpra.model.TypeExprIntegerSigned;
import com.io7m.jpra.model.TypeExprIntegerSignedNormalized;
import com.io7m.jpra.model.TypeExprIntegerUnsigned;
import com.io7m.jpra.model.TypeExprIntegerUnsignedNormalized;
import com.io7m.jpra.model.TypeExprMatrix;
import com.io7m.jpra.model.TypeExprReference;
import com.io7m.jpra.model.TypeExprScalarType;
import com.io7m.jpra.model.TypeExprString;
import com.io7m.jpra.model.TypeExprType;
import com.io7m.jpra.model.TypeExprVector;
import com.io7m.jpra.model.TypeName;
import com.io7m.jsx.parser.ParserGrammarException;
import com.io7m.jsx.parser.ParserType;
import com.io7m.junreachable.UnreachableCodeException;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class StatementParserContract<P extends StatementParserType>
{
  @Rule public ExpectedException expected = ExpectedException.none();

  protected abstract ParserType newSExpressionParser(String name);

  protected abstract P newParser();

  @Test public final void testImport()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-import-0.jpr");
    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onImport(
          final LexicalContextType context,
          final PackageNameQualified p_name,
          final PackageNameUnqualified up_name)
          throws CompilerParseException
        {
          final ImmutableList<PackageNameUnqualified> elems = p_name.getValue();
          Assert.assertEquals(3L, (long) elems.size());
          Assert.assertEquals(new PackageNameUnqualified("x"), elems.get(0));
          Assert.assertEquals(new PackageNameUnqualified("y"), elems.get(1));
          Assert.assertEquals(new PackageNameUnqualified("z"), elems.get(2));
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
    final ParserType q = this.newSExpressionParser("t-package-begin-0.jpr");
    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onPackageBegin(
          final LexicalContextType context,
          final PackageNameQualified name)
          throws CompilerParseException
        {
          final ImmutableList<PackageNameUnqualified> elems = name.getValue();
          Assert.assertEquals(3L, (long) elems.size());
          Assert.assertEquals(new PackageNameUnqualified("x"), elems.get(0));
          Assert.assertEquals(new PackageNameUnqualified("y"), elems.get(1));
          Assert.assertEquals(new PackageNameUnqualified("z"), elems.get(2));
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
    final ParserType q = this.newSExpressionParser("t-package-end-0.jpr");
    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onPackageEnd(final LexicalContextType context)
          throws CompilerParseException
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
    final ParserType q =
      this.newSExpressionParser("t-package-end-invalid-0.jpr");

    this.expected.expect(ParserGrammarException.class);

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testPackageEndInvalid1()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-package-end-invalid-1.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testPackageBeginInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-package-begin-invalid-0.jpr");

    this.expected.expect(ParserGrammarException.class);

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testPackageBeginInvalid1()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-package-begin-invalid-1.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testPackageBeginInvalid2()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-package-begin-invalid-2.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testPackageBeginInvalid3()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-package-begin-invalid-3.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testPackageBeginInvalid4()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-package-begin-invalid-4.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.BAD_PACKAGE_NAME));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testImportInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-import-invalid-0.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testImportInvalid1()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-import-invalid-1.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testImportInvalid2()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-import-invalid-2.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testImportInvalid3()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-import-invalid-3.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testImportInvalid4()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-import-invalid-4.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testImportInvalid5()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-import-invalid-5.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.BAD_PACKAGE_NAME));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testImportInvalid6()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-import-invalid-6.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.BAD_PACKAGE_NAME));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testNonsense0()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-nonsense-0.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.EXPECTED_LIST_GOT_SYMBOL));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testNonsense1()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-nonsense-1.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.EXPECTED_LIST_GOT_QUOTED_STRING));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testNonsense2()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-nonsense-2.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.EXPECTED_NON_EMPTY_LIST));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testNonsense3()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-nonsense-3.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.UNRECOGNIZED_KEYWORD));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testNonsense4()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-nonsense-4.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.EXPECTED_SYMBOL_GOT_QUOTED_STRING));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testNonsense5()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-nonsense-5.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.EXPECTED_SYMBOL_GOT_LIST));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testRecordInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-record-invalid-0.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testRecordInvalid1()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-record-invalid-1.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testRecordInvalid2()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-record-invalid-2.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.BAD_TYPE_NAME));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testRecordInvalid3()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-record-invalid-3.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.EXPECTED_LIST_GOT_SYMBOL));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testRecordInvalid4()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-record-invalid-4.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.UNRECOGNIZED_RECORD_FIELD_KEYWORD));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testRecordInvalid5()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-record-invalid-5.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.EXPECTED_NON_EMPTY_LIST));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testRecordInvalid6()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-record-invalid-6.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.DUPLICATE_FIELD_NAME));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testRecordFieldInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-record-field-invalid-0.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testRecordFieldInvalid1()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-record-field-invalid-1.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.BAD_FIELD_NAME));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testRecordFieldInvalid2()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-record-field-invalid-2.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.BAD_TYPE_NAME));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testRecordFieldInvalid3()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-record-field-invalid-3.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.BAD_PACKAGE_NAME));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testRecordFieldInvalid4()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-record-field-invalid-4.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.BAD_TYPE_NAME));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testRecordFieldInvalid5()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-record-field-invalid-5.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.BAD_TYPE_REFERENCE));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testRecordFieldInvalid6()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-record-field-invalid-6.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.EXPECTED_LIST_GOT_QUOTED_STRING));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testRecordPaddingOctetsInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-record-padding-octets-invalid-0.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testRecord0()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-record-0.jpr");
    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onRecord(
          final LexicalContextType context,
          final TypeDeclRecord r)
          throws CompilerParseException
        {
          called.set(true);

          final ImmutableMap<FieldName, RecordFieldDeclValue> fields_by_name =
            r.getFieldsByName();
          final ImmutableList<RecordFieldDeclType> fields_ordered =
            r.getFieldsInDeclarationOrder();

          Assert.assertEquals(r.getName(), new TypeName("T"));
          Assert.assertEquals(1L, (long) fields_by_name.size());
          Assert.assertEquals(1L, (long) fields_ordered.size());

          final FieldName field_name = new FieldName("f0");
          Assert.assertTrue(fields_by_name.containsKey(field_name));

          final RecordFieldDeclValue v1 = fields_by_name.get(field_name);
          final RecordFieldDeclType v0 = fields_ordered.get(0);
          Assert.assertEquals(RecordFieldDeclValue.class, v0.getClass());
          Assert.assertEquals(v0, v1);
          Assert.assertSame(v0, v1);

          final RecordFieldDeclValue v0v = (RecordFieldDeclValue) v0;
          Assert.assertEquals(field_name, v0v.getName());
        }
      });

    Assert.assertTrue(called.get());
  }

  @Test public final void testRecord1()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-record-1.jpr");
    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onRecord(
          final LexicalContextType context,
          final TypeDeclRecord r)
          throws CompilerParseException
        {
          called.set(true);

          final ImmutableMap<FieldName, RecordFieldDeclValue> fields_by_name =
            r.getFieldsByName();
          final ImmutableList<RecordFieldDeclType> fields_ordered =
            r.getFieldsInDeclarationOrder();

          Assert.assertEquals(r.getName(), new TypeName("T"));
          Assert.assertEquals(0L, (long) fields_by_name.size());
          Assert.assertEquals(1L, (long) fields_ordered.size());

          final RecordFieldDeclType v0 = fields_ordered.get(0);
          Assert.assertEquals(
            RecordFieldDeclPaddingOctets.class, v0.getClass());
        }
      });

    Assert.assertTrue(called.get());
  }

  @Test public final void testTypeIntegerInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-integer-invalid-0.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-type-invalid-0.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.EXPECTED_NON_EMPTY_LIST));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeInvalid1()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-type-invalid-1.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.UNRECOGNIZED_TYPE_KEYWORD));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeInvalid2()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-type-invalid-2.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.EXPECTED_SYMBOL_OR_LIST_GOT_QUOTED_STRING));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeInvalid3()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-type-invalid-3.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeIntegerInvalid1()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-integer-invalid-1.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeIntegerInvalid2()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-integer-invalid-2.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.UNRECOGNIZED_INTEGER_TYPE_KEYWORD));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeIntegerUnsignedInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-integer-unsigned-invalid-0.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.INVALID_INTEGER_CONSTANT));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeIntegerUnsigned0()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-integer-unsigned-0.jpr");

    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onREPLType(
          final LexicalContextType context,
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
    final ParserType q =
      this.newSExpressionParser("t-type-integer-unsigned-normalized-0.jpr");

    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onREPLType(
          final LexicalContextType context,
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
    final ParserType q =
      this.newSExpressionParser("t-type-integer-signed-normalized-0.jpr");

    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onREPLType(
          final LexicalContextType context,
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
    final ParserType q = this.newSExpressionParser(
      "t-type-integer-unsigned-normalized-invalid-0.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.INVALID_INTEGER_CONSTANT));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeIntegerSignedInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-integer-signed-invalid-0.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.INVALID_INTEGER_CONSTANT));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeIntegerSignedNormalizedInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser(
      "t-type-integer-signed-normalized-invalid-0.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.INVALID_INTEGER_CONSTANT));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeIntegerSigned0()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-integer-signed-0.jpr");

    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onREPLType(
          final LexicalContextType context,
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
    final ParserType q = this.newSExpressionParser("t-size-constant-0.jpr");
    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onREPLSize(
          final LexicalContextType context,
          final SizeExprType<?> t)
        {
          called.set(true);

          Assert.assertEquals(SizeConstant.class, t.getClass());
          final SizeConstant<?> sc = (SizeConstant<?>) t;
          Assert.assertEquals(BigInteger.valueOf(23L), sc.getValue());
        }
      });

    Assert.assertTrue(called.get());
  }

  @Test public final void testSizeInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-size-invalid-0.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.INVALID_INTEGER_CONSTANT));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testSizeInvalid1()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-size-invalid-1.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.EXPECTED_SYMBOL_OR_LIST_GOT_QUOTED_STRING));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testSizeInvalid2()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-size-invalid-2.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.EXPECTED_NON_EMPTY_LIST));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testSizeInvalid3()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-size-invalid-3.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testSizeInvalid4()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-size-invalid-4.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.UNRECOGNIZED_SIZE_FUNCTION));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testSizeOctetsInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-size-octets-invalid-0.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testSizeOctets0()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-size-octets-0.jpr");

    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onREPLSize(
          final LexicalContextType context,
          final SizeExprType<?> t)
        {
          called.set(true);
          Assert.assertEquals(SizeInOctets.class, t.getClass());
          final SizeInOctets s = (SizeInOctets) t;
          final TypeExprType e = s.getExpression();
          Assert.assertEquals(TypeExprReference.class, e.getClass());
          final TypeExprReference r = (TypeExprReference) e;
          final TypeName n = r.getName();
          Assert.assertEquals(new TypeName("T"), n);
        }
      });

    Assert.assertTrue(called.get());
  }

  @Test public final void testSizeBits0()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-size-bits-0.jpr");

    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onREPLSize(
          final LexicalContextType context,
          final SizeExprType<?> t)
        {
          called.set(true);
          Assert.assertEquals(SizeInBits.class, t.getClass());
          final SizeInBits s = (SizeInBits) t;
          final TypeExprType e = s.getExpression();
          Assert.assertEquals(TypeExprReference.class, e.getClass());
          final TypeExprReference r = (TypeExprReference) e;
          final TypeName n = r.getName();
          Assert.assertEquals(new TypeName("T"), n);
        }
      });

    Assert.assertTrue(called.get());
  }

  @Test public final void testSizeOctetsInvalid1()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-size-octets-invalid-1.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testSizeBitsInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-size-bits-invalid-0.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testSizeBitsInvalid1()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-size-bits-invalid-1.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeFloatInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-float-invalid-0.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeFloatInvalid1()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-float-invalid-1.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.INVALID_INTEGER_CONSTANT));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeFloatInvalid2()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-float-invalid-2.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeVectorInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-vector-invalid-0.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeVectorInvalid1()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-vector-invalid-1.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeVectorInvalid2()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-vector-invalid-2.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.INVALID_INTEGER_CONSTANT));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeVectorInvalid3()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-vector-invalid-3.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SEMANTIC_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeMatrixInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-matrix-invalid-0.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeMatrixInvalid1()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-matrix-invalid-1.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeMatrixInvalid2()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-matrix-invalid-2.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeMatrixInvalid3()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-matrix-invalid-3.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.INVALID_INTEGER_CONSTANT));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeMatrixInvalid4()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-matrix-invalid-4.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.INVALID_INTEGER_CONSTANT));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeMatrixInvalid5()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-matrix-invalid-5.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SEMANTIC_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeVector0()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-type-vector-0.jpr");

    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onREPLType(
          final LexicalContextType context,
          final TypeExprType t)
        {
          called.set(true);
          Assert.assertEquals(TypeExprVector.class, t.getClass());
          final TypeExprVector v = (TypeExprVector) t;
          final TypeExprScalarType et = v.getType();
          Assert.assertEquals(TypeExprFloat.class, et.getClass());
          final SizeExprType<?> se = v.getSizeExpression();
          Assert.assertEquals(SizeConstant.class, se.getClass());
          final SizeConstant<?> s = (SizeConstant<?>) se;
          Assert.assertEquals(BigInteger.valueOf(3L), s.getValue());
        }
      });

    Assert.assertTrue(called.get());
  }

  @Test public final void testTypeMatrix0()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-type-matrix-0.jpr");

    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onREPLType(
          final LexicalContextType context,
          final TypeExprType t)
        {
          called.set(true);
          Assert.assertEquals(TypeExprMatrix.class, t.getClass());
          final TypeExprMatrix v = (TypeExprMatrix) t;
          final TypeExprScalarType et = v.getType();
          Assert.assertEquals(TypeExprFloat.class, et.getClass());
          final SizeExprType<?> we = v.getWidthExpression();
          final SizeExprType<?> he = v.getHeightExpression();
          Assert.assertEquals(SizeConstant.class, we.getClass());
          Assert.assertEquals(SizeConstant.class, he.getClass());
          final SizeConstant<?> w = (SizeConstant<?>) we;
          final SizeConstant<?> h = (SizeConstant<?>) he;
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
    final ParserType q = this.newSExpressionParser("t-type-float-0.jpr");

    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onREPLType(
          final LexicalContextType context,
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
    final ParserType q =
      this.newSExpressionParser("t-type-array-invalid-0.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SEMANTIC_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeArrayInvalid1()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-array-invalid-1.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SEMANTIC_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeArrayInvalid2()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-array-invalid-2.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.INVALID_INTEGER_CONSTANT));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeArrayInvalid3()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-array-invalid-3.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.INVALID_INTEGER_CONSTANT));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeStringInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-string-invalid-0.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeStringInvalid1()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-string-invalid-1.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeStringInvalid2()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-string-invalid-2.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeStringInvalid3()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-string-invalid-3.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeString0()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-type-string-0.jpr");

    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onREPLType(
          final LexicalContextType context,
          final TypeExprType t)
        {
          called.set(true);
          Assert.assertEquals(TypeExprString.class, t.getClass());
          final TypeExprString v = (TypeExprString) t;
          Assert.assertEquals("UTF-8", v.getEncoding());
          final SizeExprType<SizeUnitOctetsType> se = v.getSizeExpression();
          Assert.assertEquals(SizeConstant.class, se.getClass());
          final SizeConstant<SizeUnitOctetsType> s =
            (SizeConstant<SizeUnitOctetsType>) se;
          Assert.assertEquals(BigInteger.valueOf(64L), s.getValue());
        }
      });

    Assert.assertTrue(called.get());
  }

  @Test public final void testTypeArray0()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-type-array-0.jpr");

    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onREPLType(
          final LexicalContextType context,
          final TypeExprType t)
        {
          called.set(true);
          Assert.assertEquals(TypeExprArray.class, t.getClass());
          final TypeExprArray v = (TypeExprArray) t;
          final TypeExprType et = v.getType();
          Assert.assertEquals(TypeExprFloat.class, et.getClass());
          final SizeExprType<?> se = v.getElementCountExpression();
          Assert.assertEquals(SizeConstant.class, se.getClass());
          final SizeConstant<?> s = (SizeConstant<?>) se;
          Assert.assertEquals(BigInteger.valueOf(64L), s.getValue());
        }
      });

    Assert.assertTrue(called.get());
  }

  @Test public final void testTypeBooleanSetInvalid0()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-boolean-set-invalid-0.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeBooleanSetInvalid1()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-boolean-set-invalid-1.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.SYNTAX_ERROR));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeBooleanSetInvalid2()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-boolean-set-invalid-2.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.INVALID_INTEGER_CONSTANT));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeBooleanSetInvalid3()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-boolean-set-invalid-3.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.EXPECTED_LIST_GOT_SYMBOL));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeBooleanSetInvalid4()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-boolean-set-invalid-4.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.BAD_FIELD_NAME));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeBooleanSetInvalid5()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-boolean-set-invalid-5.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.DUPLICATE_FIELD_NAME));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeBooleanSetInvalid6()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q =
      this.newSExpressionParser("t-type-boolean-set-invalid-6.jpr");

    this.expected.expect(
      new CompilerParseExceptionMatcher(
        ParseErrorCode.EXPECTED_LIST_GOT_QUOTED_STRING));

    p.parseStatement(q.parseExpression(), new CheckedListener());
  }

  @Test public final void testTypeReference0()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-type-reference-0.jpr");

    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onREPLType(
          final LexicalContextType context,
          final TypeExprType t)
        {
          called.set(true);
          Assert.assertEquals(TypeExprReference.class, t.getClass());
          final TypeExprReference v = (TypeExprReference) t;
          final Optional<PackageNameUnqualified> pn = v.getPackageName();
          Assert.assertTrue(pn.isPresent());
          Assert.assertEquals("x", pn.get().getValue());
          Assert.assertEquals("T", v.getName().getValue());
        }
      });

    Assert.assertTrue(called.get());
  }

  @Test public final void testTypeBooleanSet0()
    throws Exception
  {
    final P p = this.newParser();
    final ParserType q = this.newSExpressionParser("t-type-boolean-set-0.jpr");

    final AtomicBoolean called = new AtomicBoolean(false);

    p.parseStatement(
      q.parseExpression(), new CheckedListener()
      {
        @Override public void onREPLType(
          final LexicalContextType context,
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
          Assert.assertEquals(SizeConstant.class, se.getClass());
          final SizeConstant<?> sc = (SizeConstant<?>) se;
          Assert.assertEquals(BigInteger.valueOf(1L), sc.getValue());
        }
      });

    Assert.assertTrue(called.get());
  }

  private static final class CompilerParseExceptionMatcher
    extends TypeSafeDiagnosingMatcher<CompilerParseException>
  {
    private static final Logger LOG;

    static {
      LOG = LoggerFactory.getLogger(CompilerParseExceptionMatcher.class);
    }

    private final ParseErrorCode code;

    CompilerParseExceptionMatcher(final ParseErrorCode in_code)
    {
      this.code = NullCheck.notNull(in_code);
    }

    @Override protected boolean matchesSafely(
      final CompilerParseException item,
      final Description mismatchDescription)
    {
      CompilerParseExceptionMatcher.LOG.debug("exception: ", item);

      final ParseErrorCode ec = item.getErrorCode();
      final boolean ok = ec.equals(this.code);
      mismatchDescription.appendText("has error code " + ec);
      return ok;
    }

    @Override public void describeTo(final Description description)
    {
      description.appendText("has error code " + this.code);
    }
  }

  private static class CheckedListener
    implements StatementParserREPLEventListenerType
  {
    private static final Logger LOG;

    static {
      LOG = LoggerFactory.getLogger(CheckedListener.class);
    }

    @Override public void onImport(
      final LexicalContextType context,
      final PackageNameQualified p_name,
      final PackageNameUnqualified up_name)
      throws CompilerParseException
    {
      CheckedListener.LOG.debug("onImport: {} {}", p_name, up_name);
      throw new UnreachableCodeException();
    }

    @Override public void onPackageBegin(
      final LexicalContextType context,
      final PackageNameQualified name)
      throws CompilerParseException
    {
      CheckedListener.LOG.debug("onPackageBegin: {}", name);
      throw new UnreachableCodeException();
    }

    @Override public void onPackageEnd(final LexicalContextType context)
      throws CompilerParseException
    {
      CheckedListener.LOG.debug("onPackageEnd");
      throw new UnreachableCodeException();
    }

    @Override public void onRecord(
      final LexicalContextType context,
      final TypeDeclRecord r)
      throws CompilerParseException
    {
      CheckedListener.LOG.debug("onRecord: {}", r);
      throw new UnreachableCodeException();
    }

    @Override public void onREPLType(
      final LexicalContextType context,
      final TypeExprType t)
    {
      CheckedListener.LOG.debug("onREPLType: {}", t);
      throw new UnreachableCodeException();
    }

    @Override public void onREPLSize(
      final LexicalContextType context,
      final SizeExprType<?> t)
    {
      CheckedListener.LOG.debug("onREPLSize: {}", t);
      throw new UnreachableCodeException();
    }
  }

}
