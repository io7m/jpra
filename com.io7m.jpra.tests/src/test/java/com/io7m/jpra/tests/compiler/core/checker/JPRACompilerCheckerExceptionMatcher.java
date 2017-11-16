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

package com.io7m.jpra.tests.compiler.core.checker;

import java.util.Objects;
import com.io7m.jpra.compiler.core.checker.JPRACheckerErrorCode;
import com.io7m.jpra.compiler.core.checker.JPRACompilerCheckerException;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class JPRACompilerCheckerExceptionMatcher
  extends TypeSafeDiagnosingMatcher<JPRACompilerCheckerException>
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(JPRACompilerCheckerExceptionMatcher.class);
  }

  private final JPRACheckerErrorCode code;

  JPRACompilerCheckerExceptionMatcher(final JPRACheckerErrorCode in_code)
  {
    this.code = Objects.requireNonNull(in_code);
  }

  @Override
  protected boolean matchesSafely(
    final JPRACompilerCheckerException item,
    final Description mismatchDescription)
  {
    JPRACompilerCheckerExceptionMatcher.LOG.debug("exception: ", item);

    final JPRACheckerErrorCode ec = item.getErrorCode();
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
