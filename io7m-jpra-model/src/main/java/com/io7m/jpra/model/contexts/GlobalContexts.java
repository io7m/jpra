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

import com.gs.collections.api.map.MutableMap;
import com.gs.collections.impl.factory.Maps;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.loading.JPRAModelLoadingException;
import com.io7m.jpra.model.loading.JPRAPackageLoaderType;
import com.io7m.jpra.model.names.IdentifierType;
import com.io7m.jpra.model.names.PackageNameQualified;
import com.io7m.jpra.model.types.TypeUserDefinedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valid4j.Assertive;

import java.math.BigInteger;
import java.util.Map;

/**
 * Access to global contexts.
 */

public final class GlobalContexts implements GlobalContextType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(GlobalContexts.class);
  }

  private final MutableMap<PackageNameQualified, PackageContextType> packages;
  private final JPRAPackageLoaderType                                loader;
  private final MutableMap<IdentifierType, TypeUserDefinedType>      types;
  private       BigInteger                                           id_pool;

  GlobalContexts(final JPRAPackageLoaderType in_loader)
  {
    this.id_pool = BigInteger.ZERO;
    this.packages = Maps.mutable.empty();
    this.loader = NullCheck.notNull(in_loader);
    this.types = Maps.mutable.empty();
    GlobalContexts.LOG.trace("created");
  }

  /**
   * @param in_loader A package loader
   *
   * @return A new global context
   */

  public static GlobalContextType newContext(
    final JPRAPackageLoaderType in_loader)
  {
    return new GlobalContexts(in_loader);
  }

  @Override public IdentifierType getFreshIdentifier()
  {
    this.id_pool = this.id_pool.add(BigInteger.ONE);
    GlobalContexts.LOG.trace("fresh identifier: {}", this.id_pool);
    return new Identifier(this.id_pool);
  }

  @Override public Map<PackageNameQualified, PackageContextType> getPackages()
  {
    return this.packages.asUnmodifiable();
  }

  @Override public PackageContextType getPackage(
    final PackageNameQualified p)
    throws JPRAModelLoadingException
  {
    NullCheck.notNull(p);

    GlobalContexts.LOG.debug("get package: {}", p);

    if (this.packages.containsKey(p)) {
      GlobalContexts.LOG.debug("returning loaded package: {}", p);
      return this.packages.get(p);
    }

    GlobalContexts.LOG.debug("loading package: {}", p);
    final PackageContextType r = this.loader.evaluate(this, p);
    this.packages.put(p, r);
    return r;
  }

  @Override public void putType(final TypeUserDefinedType t)
  {
    NullCheck.notNull(t);
    final IdentifierType id = t.getIdentifier();
    Assertive.require(!this.types.containsKey(id));
    this.types.put(id, t);
  }

  @Override public TypeUserDefinedType getType(final IdentifierType id)
  {
    NullCheck.notNull(id);
    Assertive.require(this.types.containsKey(id));
    return this.types.get(id);
  }
}
