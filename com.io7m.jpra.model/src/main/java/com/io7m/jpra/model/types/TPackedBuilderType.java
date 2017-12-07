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

package com.io7m.jpra.model.types;

import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jpra.model.names.FieldName;
import com.io7m.jpra.model.names.IdentifierType;

import java.net.URI;

/**
 * The type of mutable records for {@link TPacked} values.
 */

public interface TPackedBuilderType
{
  /**
   * Add a field of padding bits.
   *
   * @param lex  The lexical information, if any
   * @param size The size of the padding in bits
   */

  void addPaddingBits(
    LexicalPosition<URI> lex,
    Size<SizeUnitBitsType> size);

  /**
   * Add a value field.
   *
   * @param name The field name
   * @param id   The field identifier
   * @param type The field type
   */

  void addField(
    FieldName name,
    IdentifierType id,
    TIntegerType type);

  /**
   * @return The current size of the type in bits
   */

  Size<SizeUnitBitsType> getCurrentSize();

  /**
   * @return A constructed record
   */

  TPacked build();
}
