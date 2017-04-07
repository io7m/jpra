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

import com.io7m.jnull.NullCheck;
import com.io7m.jpra.compiler.core.parser.JPRACompilerParseException;
import com.io7m.jpra.compiler.core.parser.JPRAParseErrorCode;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JPRACompilerParseExceptionMatcher
  extends TypeSafeDiagnosingMatcher<JPRACompilerParseException>
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(JPRACompilerParseExceptionMatcher.class);
  }

  private final JPRAParseErrorCode code;

  public JPRACompilerParseExceptionMatcher(final JPRAParseErrorCode in_code)
  {
    this.code = NullCheck.notNull(in_code);
  }

  @Override
  protected boolean matchesSafely(
    final JPRACompilerParseException item,
    final Description mismatchDescription)
  {
    JPRACompilerParseExceptionMatcher.LOG.debug("exception: ", item);

    final JPRAParseErrorCode ec = item.getErrorCode();
    final boolean ok = ec.equals(this.code);
    mismatchDescription.appendText("has error code " + ec);
    return ok;
  }

  @Override
  public void describeTo(final Description description)
  {
    description.appendText("has error code " + this.code);
  }
}
