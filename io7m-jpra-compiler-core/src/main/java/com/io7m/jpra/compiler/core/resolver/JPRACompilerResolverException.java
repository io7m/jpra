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

import com.io7m.jlexing.core.ImmutableLexicalPosition;
import com.io7m.jlexing.core.ImmutableLexicalPositionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.compiler.core.JPRACompilerException;
import com.io7m.jpra.model.PackageImport;
import com.io7m.jpra.model.names.PackageNameQualified;
import com.io7m.jpra.model.names.PackageNameUnqualified;
import com.io7m.jpra.model.names.TypeName;

import java.nio.file.Path;
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
    final Optional<ImmutableLexicalPositionType<Path>> in_lex,
    final JPRAResolverErrorCode in_code,
    final String message)
  {
    super(in_lex, message);
    this.code = NullCheck.notNull(in_code);
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
    sb.append("Name: ");
    sb.append(name);

    final Optional<ImmutableLexicalPositionType<Path>> curr_lex_opt =
      name.getLexicalInformation();
    final Optional<ImmutableLexicalPositionType<Path>> orig_lex_opt =
      name.getLexicalInformation();

    if (curr_lex_opt.isPresent() && orig_lex_opt.isPresent()) {
      sb.append(System.lineSeparator());
      sb.append("Current: At ");
      sb.append(curr_lex_opt.get());
      sb.append(System.lineSeparator());
      sb.append("Original: At ");
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
    final Optional<ImmutableLexicalPositionType<Path>> lex)
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
      name.getLexicalInformation().map(ImmutableLexicalPosition::newFrom),
      JPRAResolverErrorCode.PACKAGE_NESTED,
      "Nested packages are not allowed.");
  }

  /**
   * @param up_name The import name
   * @param i       The original import
   *
   * @return An exception
   *
   * @see JPRAResolverErrorCode#PACKAGE_IMPORT_CONFLICT
   */

  public static JPRACompilerResolverException packageImportConflict(
    final PackageNameUnqualified up_name,
    final PackageImport i)
  {
    final StringBuilder sb = new StringBuilder(128);
    sb.append("Package import conflict.");
    sb.append(System.lineSeparator());
    sb.append("Original: Name ");
    sb.append(up_name);

    final Optional<ImmutableLexicalPositionType<Path>> lex_orig_opt =
      up_name.getLexicalInformation();
    if (lex_orig_opt.isPresent()) {
      sb.append(" at ");
      sb.append(lex_orig_opt.get());
    }

    return new JPRACompilerResolverException(
      up_name.getLexicalInformation().map(ImmutableLexicalPosition::newFrom),
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
    sb.append("Original: Name ");
    sb.append(original);

    final Optional<ImmutableLexicalPositionType<Path>> lex_orig_opt =
      original.getLexicalInformation();
    if (lex_orig_opt.isPresent()) {
      sb.append(" at ");
      sb.append(lex_orig_opt.get());
    }

    return new JPRACompilerResolverException(
      current.getLexicalInformation().map(ImmutableLexicalPosition::newFrom),
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

  public static JPRACompilerException nonexistentPackage(
    final PackageNameQualified name)
  {
    return new JPRACompilerResolverException(
      name.getLexicalInformation(),
      JPRAResolverErrorCode.PACKAGE_NONEXISTENT,
      "Nonexistent package");
  }

  /**
   * @return The error code
   */

  public JPRAResolverErrorCode getJPRAResolverErrorCode()
  {
    return this.code;
  }
}
