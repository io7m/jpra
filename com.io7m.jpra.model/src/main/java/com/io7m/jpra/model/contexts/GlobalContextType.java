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

package com.io7m.jpra.model.contexts;

import com.io7m.jpra.core.JPRAException;
import com.io7m.jpra.model.loading.JPRAModelLoadingException;
import com.io7m.jpra.model.names.IdentifierType;
import com.io7m.jpra.model.names.PackageNameQualified;
import com.io7m.jpra.model.types.TypeUserDefinedType;

import java.util.Map;
import java.util.Queue;

/**
 * The global context.
 */

public interface GlobalContextType
{
  /**
   * @return The global error queue
   */

  Queue<JPRAException> getErrorQueue();

  /**
   * @return A fresh identifier that has not been returned before
   */

  IdentifierType getFreshIdentifier();

  /**
   * @return A read-only view of the set of packages in the context
   */

  Map<PackageNameQualified, PackageContextType> getPackages();

  /**
   * Load and return a package into the context.
   *
   * @param p The name of the package
   *
   * @return The loaded package
   *
   * @throws JPRAModelLoadingException Iff the package cannot be loaded
   */

  PackageContextType loadPackage(PackageNameQualified p)
    throws JPRAModelLoadingException;

  /**
   * Return a type previously added with {@link #putType(TypeUserDefinedType)}.
   *
   * @param id The identifier
   *
   * @return The type associated with the given identifier
   */

  TypeUserDefinedType getType(IdentifierType id);

  /**
   * Introduce a new type into the global context
   *
   * @param t The type
   */

  void putType(TypeUserDefinedType t);
}
