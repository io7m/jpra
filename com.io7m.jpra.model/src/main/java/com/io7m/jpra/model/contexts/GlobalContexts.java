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

import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jpra.core.JPRAException;
import com.io7m.jpra.model.PackageImport;
import com.io7m.jpra.model.loading.JPRAModelCircularImportException;
import com.io7m.jpra.model.loading.JPRAModelLoadingException;
import com.io7m.jpra.model.loading.JPRAPackageLoaderType;
import com.io7m.jpra.model.names.IdentifierType;
import com.io7m.jpra.model.names.PackageNameQualified;
import com.io7m.jpra.model.types.TypeUserDefinedType;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;

/**
 * Access to global contexts.
 */

public final class GlobalContexts implements GlobalContextType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(GlobalContexts.class);
  }

  private final DirectedAcyclicGraph<PackageNameQualified, PackageImport> graph;
  private final HashMap<PackageNameQualified, PackageContextType> packages;
  private final JPRAPackageLoaderType loader;
  private final HashMap<IdentifierType, TypeUserDefinedType> types;

  private final Queue<JPRAException> error_queue;
  private BigInteger id_pool;
  private Optional<PackageNameQualified> loading;

  GlobalContexts(final JPRAPackageLoaderType in_loader)
  {
    this.id_pool = BigInteger.ZERO;
    this.packages = new HashMap<>();
    this.loader = Objects.requireNonNull(in_loader, "Loader");
    this.types = new HashMap<>();
    this.loading = Optional.empty();
    this.error_queue = new ArrayDeque<>(128);

    this.graph = new DirectedAcyclicGraph<>(PackageImport::new);
    LOG.trace("created");
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

  @Override
  public Queue<JPRAException> getErrorQueue()
  {
    return this.error_queue;
  }

  @Override
  public IdentifierType getFreshIdentifier()
  {
    this.id_pool = this.id_pool.add(BigInteger.ONE);
    LOG.trace("fresh identifier: {}", this.id_pool);
    return new Identifier(this.id_pool);
  }

  @Override
  public Map<PackageNameQualified, PackageContextType> getPackages()
  {
    return Collections.unmodifiableMap(this.packages);
  }

  @Override
  public PackageContextType loadPackage(
    final PackageNameQualified p)
    throws JPRAModelLoadingException
  {
    Objects.requireNonNull(p, "Package name");

    LOG.debug("get package: {}", p);

    final Optional<PackageNameQualified> previous_opt = this.loading;
    try {
      this.checkCircularLoad(previous_opt, p);

      this.loading = Optional.of(p);
      if (this.packages.containsKey(p)) {
        LOG.debug("returning loaded package: {}", p);
        return this.packages.get(p);
      }

      LOG.debug("loading package: {}", p);
      final PackageContextType r = this.loader.evaluate(this, p);
      this.packages.put(p, r);
      return r;
    } finally {
      this.loading = previous_opt;
    }
  }

  private void checkCircularLoad(
    final Optional<PackageNameQualified> previous_opt,
    final PackageNameQualified current)
    throws JPRAModelLoadingException
  {
    if (previous_opt.isPresent()) {
      final PackageNameQualified previous = previous_opt.get();

      try {
        this.graph.addVertex(previous);
        this.graph.addVertex(current);
        this.graph.addEdge(previous, current);
      } catch (final IllegalArgumentException e) {

        /*
         * Because a cycle as occurred on an insertion of edge A → B, then
         * there must be some path B → A already in the graph. Use a
         * shortest path algorithm to determine that path.
         */

        final DijkstraShortestPath<PackageNameQualified, PackageImport> djp =
          new DijkstraShortestPath<>(this.graph);
        final List<PackageImport> path =
          new ArrayList<>(djp.getPath(current, previous).getEdgeList());
        path.add(new PackageImport(previous, current));

        final JPRAModelCircularImportException ex =
          new JPRAModelCircularImportException(
            "Circular import detected.", io.vavr.collection.List.ofAll(path));

        this.error_queue.add(ex);
        throw new JPRAModelLoadingException(
          String.format("Failed to load package %s", current));
      }
    }
  }

  @Override
  public void putType(final TypeUserDefinedType t)
  {
    Objects.requireNonNull(t, "Type");
    final IdentifierType id = t.getIdentifier();
    Preconditions.checkPreconditionV(
      id, !this.types.containsKey(id), "Types must not contain %s", id);
    this.types.put(id, t);
  }

  @Override
  public TypeUserDefinedType getType(final IdentifierType id)
  {
    Objects.requireNonNull(id, "Identifier");
    Preconditions.checkPreconditionV(
      id, this.types.containsKey(id), "Types must contain %s", id);
    return this.types.get(id);
  }
}
