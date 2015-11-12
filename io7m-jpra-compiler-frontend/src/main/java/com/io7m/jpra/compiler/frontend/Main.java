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

package com.io7m.jpra.compiler.frontend;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.io7m.jpra.compiler.core.JPRAProblemFormatter;
import com.io7m.jpra.compiler.core.JPRAProblemFormatterType;
import com.io7m.jpra.compiler.core.checker.JPRACheckerStandardCapabilities;
import com.io7m.jpra.compiler.core.driver.JPRADriver;
import com.io7m.jpra.compiler.core.driver.JPRADriverType;
import com.io7m.jpra.core.JPRAException;
import com.io7m.jpra.model.contexts.GlobalContextType;
import com.io7m.jpra.model.loading.JPRAModelLoadingException;
import com.io7m.jpra.model.names.PackageNameQualified;
import com.io7m.junreachable.UnreachableCodeException;
import io.airlift.airline.Arguments;
import io.airlift.airline.Cli;
import io.airlift.airline.Command;
import io.airlift.airline.Help;
import io.airlift.airline.Option;
import io.airlift.airline.ParseArgumentsMissingException;
import io.airlift.airline.ParseArgumentsUnexpectedException;
import io.airlift.airline.ParseOptionMissingException;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * Main command line program.
 */

public final class Main
{
  private static final Logger LOG;

  static {
    LOG = (Logger) LoggerFactory.getLogger(Main.class);
  }

  private Main()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Main entry point.
   *
   * @param args Command line arguments
   */

  public static void main(final String[] args)
  {
    final Cli.CliBuilder<Runnable> builder = Cli.builder("jpra");
    builder.withDescription("Packed record access compiler");
    builder.withDefaultCommand(Help.class);
    builder.withCommand(Help.class);
    builder.withCommand(CommandCheck.class);
    final Cli<Runnable> parser = builder.build();

    try {
      parser.parse(args).run();
    } catch (final ParseArgumentsMissingException
      | ParseOptionMissingException
      | ParseArgumentsUnexpectedException e) {
      Main.LOG.error("parse error: {}", e.getMessage());
      Help.help(parser.getMetadata(), Collections.emptyList());
    }
  }

  abstract static class CommandType implements Runnable
  {
    @Option(name = "--debug", description = "Enable debug logging")
    private boolean debug;

    CommandType()
    {

    }

    protected final void setup()
    {
      final Logger root = (Logger) LoggerFactory.getLogger(
        Logger.ROOT_LOGGER_NAME);
      if (this.debug) {
        root.setLevel(Level.TRACE);
      }
    }
  }

  @Command(name = "check", description = "Check a list of packages")
  public static final class CommandCheck extends CommandType
  {
    @Option(
      arity = 1,
      description = "Source directory",
      name = "--source-directory",
      required = true) private String path;

    @Arguments(
      description = "Packages to be checked",
      required = true) private List<String> packages;

    @Override public void run()
    {
      this.setup();

      final JPRAProblemFormatterType fmt = JPRAProblemFormatter.newFormatter();

      boolean error = false;
      final JPRADriverType driver = JPRADriver.newDriver(
        Paths.get(this.path),
        JPRACheckerStandardCapabilities.newCapabilities());
      final GlobalContextType gc = driver.getGlobalContext();

      final List<PackageNameQualified> names = this.packages.stream().map(
        PackageNameQualified::valueOf).collect(Collectors.toList());

      for (final PackageNameQualified name : names) {
        Main.LOG.debug("checking {}", name);

        try {
          driver.compilePackage(name);
        } catch (final JPRAModelLoadingException e) {
          error = true;
        }

        final Queue<JPRAException> q = gc.getErrorQueue();
        final Iterator<JPRAException> iter = q.iterator();
        while (iter.hasNext()) {
          final JPRAException e = iter.next();
          fmt.onJPRAException(System.err, e);
          iter.remove();
          error = true;
        }
        System.err.flush();
      }

      if (error) {
        System.exit(1);
      } else {
        System.exit(0);
      }
    }
  }
}
