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

package com.io7m.jpra.compiler.core.resolver;

import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jpra.compiler.core.JPRACompilerException;
import com.io7m.jpra.model.names.PackageNameQualified;
import com.io7m.jpra.model.names.PackageNameUnqualified;
import com.io7m.jpra.model.names.TypeName;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * The type of exceptions raised during checking.
 */

public final class JPRACompilerResolverException extends JPRACompilerException
{
  private final JPRAResolverErrorCode code;

  /**
   * Construct an exception.
   *
   * @param in_lex  Lexical information, if any
   * @param in_code The error code
   * @param message The exception message
   */

  public JPRACompilerResolverException(
    final Optional<LexicalPosition<Path>> in_lex,
    final JPRAResolverErrorCode in_code,
    final String message)
  {
    super(in_lex, message);
    this.code = Objects.requireNonNull(in_code, "Code");
  }

  /**
   * Construct an exception.
   *
   * @param in_lex  Lexical information, if any
   * @param in_code The error code
   * @param e       The cause
   */

  public JPRACompilerResolverException(
    final Optional<LexicalPosition<Path>> in_lex,
    final JPRAResolverErrorCode in_code,
    final Exception e)
  {
    super(in_lex, e);
    this.code = Objects.requireNonNull(in_code, "Code");
  }

  /**
   * @param name     The package name
   * @param original The original declaration
   *
   * @return An exception
   *
   * @see JPRAResolverErrorCode#PACKAGE_DUPLICATE
   */

  public static JPRACompilerResolverException duplicatePackage(
    final PackageNameQualified name,
    final PackageNameQualified original)
  {
    final StringBuilder sb = new StringBuilder(128);
    sb.append("Duplicate package.");
    sb.append(System.lineSeparator());
    sb.append("  Name: ");
    sb.append(name);

    final Optional<LexicalPosition<Path>> curr_lex_opt =
      name.lexical();
    final Optional<LexicalPosition<Path>> orig_lex_opt =
      name.lexical();

    if (curr_lex_opt.isPresent() && orig_lex_opt.isPresent()) {
      sb.append(System.lineSeparator());
      sb.append("  Current: At ");
      sb.append(curr_lex_opt.get());
      sb.append(System.lineSeparator());
      sb.append("  Original: At ");
      sb.append(orig_lex_opt.get());
      sb.append(System.lineSeparator());
    }

    return new JPRACompilerResolverException(
      curr_lex_opt, JPRAResolverErrorCode.PACKAGE_DUPLICATE, sb.toString());
  }

  /**
   * @param lex Lexical information
   *
   * @return An exception
   *
   * @see JPRAResolverErrorCode#NO_CURRENT_PACKAGE
   */

  public static JPRACompilerResolverException noCurrentPackage(
    final Optional<LexicalPosition<Path>> lex)
  {
    return new JPRACompilerResolverException(
      lex,
      JPRAResolverErrorCode.NO_CURRENT_PACKAGE,
      "A package must be in the process of being declared to perform this "
        + "action.");
  }

  /**
   * @param name The package name
   *
   * @return An exception
   *
   * @see JPRAResolverErrorCode#PACKAGE_NESTED
   */

  public static JPRACompilerResolverException nestedPackage(
    final PackageNameQualified name)
  {
    return new JPRACompilerResolverException(
      name.lexical().map(LexicalPosition::copyOf),
      JPRAResolverErrorCode.PACKAGE_NESTED,
      "Nested packages are not allowed.");
  }

  /**
   * @param existing_name The original name
   * @param new_name      The new name
   *
   * @return An exception
   *
   * @see JPRAResolverErrorCode#PACKAGE_IMPORT_CONFLICT
   */

  public static JPRACompilerResolverException packageImportConflict(
    final PackageNameUnqualified existing_name,
    final PackageNameUnqualified new_name)
  {
    final StringBuilder sb = new StringBuilder(128);
    sb.append("Package import conflict.");
    sb.append(System.lineSeparator());
    sb.append("  Original: Name ");
    sb.append(existing_name);

    final Optional<LexicalPosition<Path>> lex_orig_opt =
      existing_name.lexical();
    if (lex_orig_opt.isPresent()) {
      sb.append(" at ");
      sb.append(lex_orig_opt.get());
    }

    return new JPRACompilerResolverException(
      new_name.lexical().map(LexicalPosition::copyOf),
      JPRAResolverErrorCode.PACKAGE_IMPORT_CONFLICT,
      sb.toString());
  }

  /**
   * @param current  The current type name
   * @param original The original type name
   *
   * @return An exception
   *
   * @see JPRAResolverErrorCode#TYPE_DUPLICATE
   */

  public static JPRACompilerResolverException duplicateType(
    final TypeName current,
    final TypeName original)
  {
    final StringBuilder sb = new StringBuilder(128);
    sb.append("Type name conflict.");
    sb.append(System.lineSeparator());
    sb.append("  Original: Name ");
    sb.append(original);

    final Optional<LexicalPosition<Path>> lex_orig_opt =
      original.lexical();
    if (lex_orig_opt.isPresent()) {
      sb.append(" at ");
      sb.append(lex_orig_opt.get());
    }

    return new JPRACompilerResolverException(
      current.lexical().map(LexicalPosition::copyOf),
      JPRAResolverErrorCode.TYPE_DUPLICATE,
      sb.toString());
  }

  /**
   * @param name The package name
   *
   * @return An exception
   *
   * @see JPRAResolverErrorCode#PACKAGE_NONEXISTENT
   */

  public static JPRACompilerResolverException nonexistentPackage(
    final PackageNameQualified name)
  {
    return new JPRACompilerResolverException(
      name.lexical(),
      JPRAResolverErrorCode.PACKAGE_NONEXISTENT,
      String.format("Nonexistent package '%s'", name));
  }

  /**
   * @param name The package name
   *
   * @return An exception
   *
   * @see JPRAResolverErrorCode#PACKAGE_REFERENCE_NONEXISTENT
   */

  public static JPRACompilerResolverException nonexistentPackageReference(
    final PackageNameUnqualified name)
  {
    final StringBuilder sb = new StringBuilder(128);
    sb.append("No package imported via the given name.");
    sb.append(System.lineSeparator());
    sb.append("  Error: ");
    sb.append(name);

    final Optional<LexicalPosition<Path>> lex_opt =
      name.lexical();
    lex_opt.ifPresent(
      lex -> {
        sb.append(" at ");
        sb.append(lex);
      });

    return new JPRACompilerResolverException(
      lex_opt.map(LexicalPosition::copyOf),
      JPRAResolverErrorCode.PACKAGE_REFERENCE_NONEXISTENT,
      sb.toString());
  }

  /**
   * @param q_name The package name, if any
   * @param t_name The type name
   *
   * @return An exception
   *
   * @see JPRAResolverErrorCode#TYPE_NONEXISTENT
   */

  public static JPRACompilerResolverException nonexistentType(
    final Optional<PackageNameQualified> q_name,
    final TypeName t_name)
  {
    final StringBuilder sb = new StringBuilder(128);
    sb.append("No such type with the given name.");
    sb.append(System.lineSeparator());

    q_name.ifPresent(
      q -> {
        sb.append("  Target package: ");
        sb.append(q);
        sb.append(System.lineSeparator());
      });

    sb.append("  Type: ");
    sb.append(t_name);

    final Optional<LexicalPosition<Path>> lex_opt =
      t_name.lexical();
    lex_opt.ifPresent(
      lex -> {
        sb.append(" at ");
        sb.append(lex);
      });

    return new JPRACompilerResolverException(
      lex_opt.map(LexicalPosition::copyOf),
      JPRAResolverErrorCode.TYPE_NONEXISTENT,
      sb.toString());
  }

  /**
   * @param lex_opt Lexical information, if any
   *
   * @return An exception
   *
   * @see JPRAResolverErrorCode#UNEXPECTED_EOF
   */

  public static JPRACompilerResolverException unexpectedEOF(
    final Optional<LexicalPosition<Path>> lex_opt)
  {
    return new JPRACompilerResolverException(
      lex_opt.map(LexicalPosition::copyOf),
      JPRAResolverErrorCode.UNEXPECTED_EOF,
      "Unexpected EOF.");
  }

  /**
   * @param expected The expected package
   * @param got      The received package
   *
   * @return An exception
   *
   * @see JPRAResolverErrorCode#UNEXPECTED_PACKAGE
   */

  public static JPRACompilerResolverException unexpectedPackage(
    final PackageNameQualified expected,
    final PackageNameQualified got)
  {
    final StringBuilder sb = new StringBuilder(128);
    sb.append("Received an unexpected package.");
    sb.append(System.lineSeparator());
    sb.append("  Expected: ");
    sb.append(expected);
    sb.append(System.lineSeparator());
    sb.append("  Got: ");
    sb.append(got);

    return new JPRACompilerResolverException(
      got.lexical(),
      JPRAResolverErrorCode.UNEXPECTED_PACKAGE,
      sb.toString());
  }

  /**
   * @param p The expected package
   *
   * @return An exception
   *
   * @see JPRAResolverErrorCode#EXPECTED_PACKAGE
   */

  public static JPRACompilerResolverException expectedPackage(
    final PackageNameQualified p)
  {
    final StringBuilder sb = new StringBuilder(128);
    sb.append("Expected a package but one was not provided before EOF.");
    sb.append(System.lineSeparator());
    sb.append("  Expected: ");
    sb.append(p);

    return new JPRACompilerResolverException(
      p.lexical(),
      JPRAResolverErrorCode.EXPECTED_PACKAGE,
      sb.toString());
  }

  /**
   * @return The error code
   */

  public JPRAResolverErrorCode getErrorCode()
  {
    return this.code;
  }
}
