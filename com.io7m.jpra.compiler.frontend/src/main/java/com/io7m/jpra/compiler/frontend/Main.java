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
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.io7m.jpra.compiler.core.JPRAProblemFormatter;
import com.io7m.jpra.compiler.core.JPRAProblemFormatterType;
import com.io7m.jpra.compiler.core.checker.JPRACheckerStandardCapabilities;
import com.io7m.jpra.compiler.core.driver.JPRADriver;
import com.io7m.jpra.compiler.core.driver.JPRADriverType;
import com.io7m.jpra.compiler.java.JPRAJavaGenerator;
import com.io7m.jpra.compiler.java.JPRAJavaGeneratorType;
import com.io7m.jpra.compiler.java.JPRAJavaWriter;
import com.io7m.jpra.compiler.java.JPRAJavaWriterType;
import com.io7m.jpra.core.JPRAException;
import com.io7m.jpra.model.contexts.GlobalContextType;
import com.io7m.jpra.model.contexts.PackageContextType;
import com.io7m.jpra.model.loading.JPRAModelLoadingException;
import com.io7m.jpra.model.names.PackageNameQualified;
import com.io7m.jpra.model.names.TypeName;
import com.io7m.jpra.model.types.TypeUserDefinedType;
import com.io7m.junreachable.UnreachableCodeException;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

  private final String[] args;
  private final HashMap<String, CommandType> commands;
  private final JCommander commander;
  private int exit_code;

  private Main(
    final String[] in_args)
  {
    this.args =
      Objects.requireNonNull(in_args, "Command line arguments");

    final CommandCheck check = new CommandCheck();
    final CommandGenerateJava generate = new CommandGenerateJava();

    this.commands = new HashMap<>(8);
    this.commands.put("check", check);
    this.commands.put("generate", generate);

    this.commander = new JCommander();
    this.commander.setProgramName("jpra-c");
    this.commander.addCommand("check", check);
    this.commander.addCommand("generate", generate);
  }

  /**
   * Main entry point.
   *
   * @param args Command line arguments
   */

  public static void main(
    final String[] args)
  {
    final Main cm = new Main(args);
    cm.run();
    System.exit(cm.exitCode());
  }

  private int exitCode()
  {
    return this.exit_code;
  }

  private void run()
  {
    try {
      this.commander.parse(this.args);

      final String cmd = this.commander.getParsedCommand();
      if (cmd == null) {
        final StringBuilder sb = new StringBuilder(128);
        this.commander.usage(sb);
        LOG.info("Arguments required.\n{}", sb.toString());
        this.exit_code = 1;
        return;
      }

      final CommandType command = this.commands.get(cmd);
      final CommandType.Status status = command.run();
      this.exit_code = status.exitCode();
    } catch (final ParameterException e) {
      final StringBuilder sb = new StringBuilder(128);
      this.commander.usage(sb);
      LOG.error("{}\n{}", e.getMessage(), sb.toString());
      this.exit_code = 1;
    } catch (final Exception e) {
      LOG.error("{}", e.getMessage(), e);
      this.exit_code = 1;
    }
  }

  abstract static class CommandType
  {
    @Parameter(
      names = "--debug",
      description = "Enable debug logging")
    private boolean debug;

    CommandType()
    {

    }

    protected abstract Status run();

    protected final void setup()
    {
      final Logger root = (Logger) LoggerFactory.getLogger(
        Logger.ROOT_LOGGER_NAME);
      if (this.debug) {
        root.setLevel(Level.TRACE);
      }
    }

    enum Status
    {
      SUCCESS,
      FAILURE;

      int exitCode()
      {
        switch (this) {
          case SUCCESS:
            return 0;
          case FAILURE:
            return 1;
        }
        throw new UnreachableCodeException();
      }
    }
  }

  /**
   * A check command.
   */

  @Parameters(commandDescription = "Check a list of packages")
  public static final class CommandCheck extends CommandType
  {
    @Parameter(
      description = "Source directory",
      names = "--source-directory",
      required = true)
    private String source_directory;

    @Parameter(
      description = "Packages to be checked (may be specified multiple times)",
      names = "--package")
    private List<String> packages = new ArrayList<>();

    /**
     * Construct a command.
     */

    public CommandCheck()
    {

    }

    @Override
    public Status run()
    {
      this.setup();

      final JPRAProblemFormatterType fmt = JPRAProblemFormatter.newFormatter();

      boolean error = false;
      final JPRADriverType driver = JPRADriver.newDriver(
        Paths.get(this.source_directory),
        JPRACheckerStandardCapabilities.newCapabilities());
      final GlobalContextType gc = driver.getGlobalContext();

      final List<PackageNameQualified> names = this.packages.stream().map(
        PackageNameQualified::valueOf).collect(Collectors.toList());

      for (final PackageNameQualified name : names) {
        LOG.debug("checking {}", name);

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
        return Status.FAILURE;
      }
      return Status.SUCCESS;
    }
  }

  /**
   * A {@code generate-java} command.
   */

  @Parameters(commandDescription = "Generate Java code")
  public static final class CommandGenerateJava extends CommandType
  {
    @Parameter(
      arity = 1,
      description = "Source directory",
      names = "--source-directory",
      required = true)
    private String source_directory;

    @Parameter(
      arity = 1,
      description = "Target directory",
      names = "--target-directory",
      required = true)
    private String target_directory;

    @Parameter(
      description = "Packages to be exported (may be specified multiple times)",
      names = "--package")
    private List<String> packages = new ArrayList<>();

    /**
     * Construct a command.
     */

    public CommandGenerateJava()
    {

    }

    @Override
    public Status run()
    {
      this.setup();

      final JPRAJavaGeneratorType gen = JPRAJavaGenerator.newGenerator();
      final JPRAProblemFormatterType fmt = JPRAProblemFormatter.newFormatter();
      final JPRAJavaWriterType writer = JPRAJavaWriter.newWriter(gen);

      boolean error = false;
      final JPRADriverType driver = JPRADriver.newDriver(
        Paths.get(this.source_directory),
        JPRACheckerStandardCapabilities.newCapabilities());
      final GlobalContextType gc = driver.getGlobalContext();

      final List<PackageNameQualified> pack_names = this.packages.stream().map(
        PackageNameQualified::valueOf).collect(Collectors.toList());

      for (final PackageNameQualified pack_name : pack_names) {
        LOG.debug("checking {}", pack_name);

        try {
          driver.compilePackage(pack_name);
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
      }

      if (!error) {
        LOG.debug("generating code");

        final Map<PackageNameQualified, PackageContextType> packs =
          gc.getPackages();

        for (final PackageNameQualified pack_name : pack_names) {
          final PackageContextType pack = packs.get(pack_name);
          final Map<TypeName, TypeUserDefinedType> types = pack.getTypes();
          for (final TypeName t_name : types.keySet()) {
            final TypeUserDefinedType type = types.get(t_name);
            try {
              writer.writeType(Paths.get(this.target_directory), type);
            } catch (final IOException e) {
              error = true;
              System.err.printf("i/o error: %s", e);
              System.err.println();
            }
          }
        }
      }

      System.err.flush();
      if (error) {
        return Status.FAILURE;
      }
      return Status.SUCCESS;
    }
  }
}
