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

package com.io7m.jpra.model.loading;

import com.gs.collections.api.list.ImmutableList;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.core.JPRAException;
import com.io7m.jpra.model.PackageImport;
import org.valid4j.Assertive;

/**
 * An exception encountered due to a circular import.
 */

public final class JPRAModelCircularImportException extends JPRAException
{
  private final ImmutableList<PackageImport> imports;

  /**
   * Construct a model loading exception.
   *
   * @param message    The error message
   * @param in_imports The imports
   */

  public JPRAModelCircularImportException(
    final String message,
    final ImmutableList<PackageImport> in_imports)
  {
    super(message);
    this.imports = NullCheck.notNull(in_imports);
    Assertive.require(!this.imports.isEmpty());
  }

  /**
   * @return The circular import path
   */

  public ImmutableList<PackageImport> getImports()
  {
    return this.imports;
  }
}