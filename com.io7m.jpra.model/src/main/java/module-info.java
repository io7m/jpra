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

/**
 * Packed record access (Model)
 */

module com.io7m.jpra.model
{
  requires static org.immutables.value;

  requires com.io7m.jaffirm.core;
  requires com.io7m.jlexing.core;
  requires com.io7m.jpra.core;
  requires com.io7m.jranges.core;
  requires com.io7m.junreachable.core;
  requires io.vavr;
  requires jgrapht.core;
  requires org.slf4j;

  exports com.io7m.jpra.model;
  exports com.io7m.jpra.model.contexts;
  exports com.io7m.jpra.model.loading;
  exports com.io7m.jpra.model.names;
  exports com.io7m.jpra.model.size_expressions;
  exports com.io7m.jpra.model.statements;
  exports com.io7m.jpra.model.type_declarations;
  exports com.io7m.jpra.model.type_expressions;
  exports com.io7m.jpra.model.types;
}
