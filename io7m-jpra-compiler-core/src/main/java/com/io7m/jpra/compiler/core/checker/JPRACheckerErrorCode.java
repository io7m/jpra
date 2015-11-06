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

package com.io7m.jpra.compiler.core.checker;

/**
 * Type checker error codes.
 */

public enum JPRACheckerErrorCode
{
  /**
   * The specified {@code float} size is not supported when being used as a
   * record field.
   */

  RECORD_FLOAT_SIZE_UNSUPPORTED,

  /**
   * An unsupported string encoding was specified.
   */

  STRING_ENCODING_UNSUPPORTED,

  /**
   * An invalid boolean set size was specified.
   */

  BOOLEAN_SET_SIZE_INVALID,

  /**
   * Not enough space was allocated to hold all of the fields in a boolean set.
   */

  BOOLEAN_SET_SIZE_TOO_SMALL,

  /**
   * An unsupported {@code vector} size was specified.
   */

  VECTOR_SIZE_UNSUPPORTED,

  /**
   * A non-scalar type was specified as the type of {@code vector} elements.
   */

  VECTOR_NON_SCALAR_TYPE,

  /**
   * An unsupported {@code integer} size was specified as a {@code vector}
   * element.
   */

  VECTOR_SIZE_INTEGER_UNSUPPORTED,

  /**
   * An unsupported {@code float} size was specified as a {@code vector}
   * element.
   */

  VECTOR_SIZE_FLOAT_UNSUPPORTED,

  /**
   * An unsupported {@code matrix} size was specified.
   */

  MATRIX_SIZE_UNSUPPORTED,

  /**
   * A non-scalar type was specified as the type of {@code matrix} elements.
   */

  MATRIX_NON_SCALAR_TYPE,

  /**
   * An unsupported {@code integer} size was specified as a {@code vector}
   * element.
   */

  MATRIX_SIZE_INTEGER_UNSUPPORTED,

  /**
   * An unsupported {@code float} size was specified as a {@code vector}
   * element.
   */

  MATRIX_SIZE_FLOAT_UNSUPPORTED,

  /**
   * An invalid padding size was specified.
   */

  PADDING_SIZE_INVALID,

  /**
   * The specified {@code integer} size is not supported when being used as a
   * record field.
   */

  RECORD_INTEGER_SIZE_UNSUPPORTED

}
