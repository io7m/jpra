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

package com.io7m.jpra.compiler.core.resolver;

/**
 * Name resolver error codes.
 */

public enum JPRAResolverErrorCode
{
  /**
   * A package must be in the process of being declared to perform this action.
   */

  NO_CURRENT_PACKAGE,

  /**
   * Nested packages are not allowed.
   */

  PACKAGE_NESTED,

  /**
   * An imported package conflicts with an existing import.
   */

  PACKAGE_IMPORT_CONFLICT,

  /**
   * A duplicate type was specified.
   */

  TYPE_DUPLICATE,

  /**
   * An attempt was made to import a nonexistent package.
   */

  PACKAGE_NONEXISTENT,

  /**
   * A duplicate package was specified.
   */

  PACKAGE_DUPLICATE

}
