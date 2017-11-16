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

import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jpra.core.JPRAException;
import com.io7m.jpra.model.PackageImport;
import io.vavr.collection.List;

import java.util.Objects;

/**
 * An exception encountered due to a circular import.
 */

public final class JPRAModelCircularImportException extends JPRAException
{
  private final List<PackageImport> imports;

  /**
   * Construct a type_model loading exception.
   *
   * @param message    The error message
   * @param in_imports The imports
   */

  public JPRAModelCircularImportException(
    final String message,
    final List<PackageImport> in_imports)
  {
    super(message);
    this.imports = Objects.requireNonNull(in_imports, "Imports");
    Preconditions.checkPrecondition(
      !this.imports.isEmpty(),
      "Imports must not be empty");
  }

  /**
   * @return The circular import path
   */

  public List<PackageImport> getImports()
  {
    return this.imports;
  }
}
