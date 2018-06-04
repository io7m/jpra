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

package com.io7m.jpra.model;

import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jpra.core.JPRAImmutableStyleType;
import com.io7m.jpra.model.names.PackageNameQualified;
import org.immutables.value.Value;

import java.net.URI;

/**
 * A package import declaration.
 */

@JPRAImmutableStyleType
@Value.Immutable
public interface PackageImportType extends ModelElementType
{
  @Override
  @Value.Auxiliary
  default LexicalPosition<URI> lexical()
  {
    return this.from().lexical();
  }

  /**
   * @return The importing package
   */

  @Value.Parameter
  PackageNameQualified from();

  /**
   * @return The imported package
   */

  @Value.Parameter
  PackageNameQualified to();
}