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

package com.io7m.jpra.tests.compiler.java.output;

import com.io7m.jpra.runtime.java.JPRACursor1DByteBufferedChecked;
import com.io7m.jpra.runtime.java.JPRACursor1DType;
import com.io7m.jpra.runtime.java.JPRAStringCursorType;
import com.io7m.jpra.runtime.java.JPRAStringTruncation;
import com.io7m.jpra.tests.compiler.java.generation.code.NestedString0ByteBuffered;
import com.io7m.jpra.tests.compiler.java.generation.code.NestedString0Type;
import com.io7m.jpra.tests.compiler.java.generation.code.NestedString1ReadableType;
import com.io7m.jpra.tests.compiler.java.generation.code.NestedString1WritableType;
import com.io7m.jpra.tests.compiler.java.generation.code.NestedString2ReadableType;
import com.io7m.jpra.tests.compiler.java.generation.code.NestedString2WritableType;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;

public final class NestedString0Test
{
  @Test
  public void testNesting()
  {
    final ByteBuffer buf = ByteBuffer.allocate(2 * 36);
    final JPRACursor1DType<NestedString0Type> c =
      JPRACursor1DByteBufferedChecked.newCursor(
        buf,
        NestedString0ByteBuffered::newValueWithOffset);

    final NestedString0Type n0 = c.getElementView();
    final JPRAStringCursorType n0s = n0.getSWritable();
    final NestedString1ReadableType n1r = n0.getNReadable();
    final NestedString1WritableType n1w = n0.getNWritable();
    final JPRAStringCursorType n1s = n1w.getSWritable();
    final NestedString2ReadableType n2r = n1r.getNReadable();
    final NestedString2WritableType n2w = n1w.getNWritable();
    final JPRAStringCursorType n2s = n2w.getSWritable();

    Assert.assertEquals(4L, (long) n0.metaSOffsetFromType());
    Assert.assertEquals(4L, (long) n0.metaSOffsetFromCursor());

    Assert.assertEquals(12L, (long) n0.metaNOffsetFromType());
    Assert.assertEquals(12L, (long) n0.metaNOffsetFromCursor());

    Assert.assertEquals(4L, (long) n1r.metaSOffsetFromType());
    Assert.assertEquals(4L + 12L, (long) n1r.metaSOffsetFromCursor());

    Assert.assertEquals(12L, (long) n1r.metaNOffsetFromType());
    Assert.assertEquals(12L + 12L, (long) n1r.metaNOffsetFromCursor());

    Assert.assertEquals(4L, (long) n2r.metaSOffsetFromType());
    Assert.assertEquals(4L + 12L + 12L, (long) n2r.metaSOffsetFromCursor());

    for (int index = 0; index < 2; ++index) {
      c.setElementIndex(index);

      Assert.assertEquals("", n0s.getNewValue());
      Assert.assertEquals("", n1s.getNewValue());
      Assert.assertEquals("", n2s.getNewValue());

      n0s.setValue("ABCD", JPRAStringTruncation.TRUNCATE);
      n1s.setValue("EFGH", JPRAStringTruncation.TRUNCATE);
      n2s.setValue("IJKL", JPRAStringTruncation.TRUNCATE);

      final int offset = index * 36;
      BufferChecks.checkRangeInclusiveIsZero(buf, offset + 0, offset + 3);
      BufferChecks.checkRangeInclusiveIsZero(buf, offset + 12, offset + 15);
      BufferChecks.checkRangeInclusiveIsZero(buf, offset + 24, offset + 27);

      Assert.assertEquals("ABCD", n0s.getNewValue());
      Assert.assertEquals("EFGH", n1s.getNewValue());
      Assert.assertEquals("IJKL", n2s.getNewValue());
    }
  }
}
