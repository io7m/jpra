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

package com.io7m.jpra.compiler.java;

import com.io7m.jnull.NullCheck;
import com.io7m.jpra.model.types.TFloat;
import com.io7m.jpra.model.types.TIntegerSigned;
import com.io7m.jpra.model.types.TIntegerSignedNormalized;
import com.io7m.jpra.model.types.TIntegerType;
import com.io7m.jpra.model.types.TIntegerUnsigned;
import com.io7m.jpra.model.types.TIntegerUnsignedNormalized;
import com.io7m.jpra.model.types.TVector;
import com.io7m.jpra.model.types.TypeIntegerMatcherType;
import com.io7m.jpra.model.types.TypeScalarMatcherType;
import com.io7m.jpra.model.types.TypeScalarType;
import com.io7m.jtensors.Vector2DType;
import com.io7m.jtensors.Vector2FType;
import com.io7m.jtensors.Vector2IType;
import com.io7m.jtensors.Vector2LType;
import com.io7m.jtensors.Vector3DType;
import com.io7m.jtensors.Vector3FType;
import com.io7m.jtensors.Vector3IType;
import com.io7m.jtensors.Vector3LType;
import com.io7m.jtensors.Vector4DType;
import com.io7m.jtensors.Vector4FType;
import com.io7m.jtensors.Vector4IType;
import com.io7m.jtensors.Vector4LType;
import com.io7m.jtensors.VectorReadable2DType;
import com.io7m.jtensors.VectorReadable2FType;
import com.io7m.jtensors.VectorReadable2IType;
import com.io7m.jtensors.VectorReadable2LType;
import com.io7m.jtensors.VectorReadable3DType;
import com.io7m.jtensors.VectorReadable3FType;
import com.io7m.jtensors.VectorReadable3IType;
import com.io7m.jtensors.VectorReadable3LType;
import com.io7m.jtensors.VectorReadable4DType;
import com.io7m.jtensors.VectorReadable4FType;
import com.io7m.jtensors.VectorReadable4IType;
import com.io7m.jtensors.VectorReadable4LType;
import com.io7m.jtensors.bytebuffered.VectorByteBuffered2DType;
import com.io7m.jtensors.bytebuffered.VectorByteBuffered2FType;
import com.io7m.jtensors.bytebuffered.VectorByteBuffered2IType;
import com.io7m.jtensors.bytebuffered.VectorByteBuffered2LType;
import com.io7m.jtensors.bytebuffered.VectorByteBuffered3DType;
import com.io7m.jtensors.bytebuffered.VectorByteBuffered3FType;
import com.io7m.jtensors.bytebuffered.VectorByteBuffered3IType;
import com.io7m.jtensors.bytebuffered.VectorByteBuffered3LType;
import com.io7m.jtensors.bytebuffered.VectorByteBuffered4DType;
import com.io7m.jtensors.bytebuffered.VectorByteBuffered4FType;
import com.io7m.jtensors.bytebuffered.VectorByteBuffered4IType;
import com.io7m.jtensors.bytebuffered.VectorByteBuffered4LType;
import com.io7m.jtensors.bytebuffered.VectorByteBufferedM2D;
import com.io7m.jtensors.bytebuffered.VectorByteBufferedM2F;
import com.io7m.jtensors.bytebuffered.VectorByteBufferedM2I;
import com.io7m.jtensors.bytebuffered.VectorByteBufferedM2L;
import com.io7m.jtensors.bytebuffered.VectorByteBufferedM3D;
import com.io7m.jtensors.bytebuffered.VectorByteBufferedM3F;
import com.io7m.jtensors.bytebuffered.VectorByteBufferedM3I;
import com.io7m.jtensors.bytebuffered.VectorByteBufferedM3L;
import com.io7m.jtensors.bytebuffered.VectorByteBufferedM4D;
import com.io7m.jtensors.bytebuffered.VectorByteBufferedM4F;
import com.io7m.jtensors.bytebuffered.VectorByteBufferedM4I;
import com.io7m.jtensors.bytebuffered.VectorByteBufferedM4L;
import com.io7m.jtensors.ieee754b16.Vector2Db16Type;
import com.io7m.jtensors.ieee754b16.Vector3Db16Type;
import com.io7m.jtensors.ieee754b16.Vector4Db16Type;
import com.io7m.jtensors.ieee754b16.VectorByteBuffered2Db16Type;
import com.io7m.jtensors.ieee754b16.VectorByteBuffered3Db16Type;
import com.io7m.jtensors.ieee754b16.VectorByteBuffered4Db16Type;
import com.io7m.jtensors.ieee754b16.VectorByteBufferedM2Db16;
import com.io7m.jtensors.ieee754b16.VectorByteBufferedM3Db16;
import com.io7m.jtensors.ieee754b16.VectorByteBufferedM4Db16;
import com.io7m.jtensors.ieee754b16.VectorReadable2Db16Type;
import com.io7m.jtensors.ieee754b16.VectorReadable3Db16Type;
import com.io7m.jtensors.ieee754b16.VectorReadable4Db16Type;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Functions to return sets of classes for a given type.
 */

final class JPRAClasses
{
  private JPRAClasses()
  {
    throw new UnreachableCodeException();
  }

  static VectorsClasses getClassesFor(
    final TVector v)
  {
    NullCheck.notNull(v);
    final TypeScalarType e_type = v.getElementType();
    final int e_count = v.getElementCount().getValue().intValue();
    return e_type.matchTypeScalar(
      new VectorClassMatcher(e_count));
  }

  static final class VectorsClasses
  {
    private final Class<?> base_interface;
    private final Class<?> base_readable;
    private final Class<?> buffered_constructors;
    private final Class<?> buffered_interface;

    VectorsClasses(
      final Class<?> in_base,
      final Class<?> in_readable,
      final Class<?> in_buffered_cons,
      final Class<?> in_buffered)
    {
      this.base_interface = NullCheck.notNull(in_base);
      this.base_readable = NullCheck.notNull(in_readable);
      this.buffered_constructors = NullCheck.notNull(in_buffered_cons);
      this.buffered_interface = NullCheck.notNull(in_buffered);
    }

    public Class<?> getBaseInterface()
    {
      return this.base_interface;
    }

    public Class<?> getBaseReadable()
    {
      return this.base_readable;
    }

    public Class<?> getBufferedConstructors()
    {
      return this.buffered_constructors;
    }

    public Class<?> getBufferedInterface()
    {
      return this.buffered_interface;
    }
  }

  private static final class VectorClassMatcher
    implements TypeScalarMatcherType<VectorsClasses, RuntimeException>
  {
    private final int e_count;

    private VectorClassMatcher(final int in_e_count)
    {
      this.e_count = in_e_count;
    }

    @Override public VectorsClasses matchScalarInteger(
      final TIntegerType t)
    {
      return t.matchTypeInteger(
        new TypeIntegerMatcherType<VectorsClasses, RuntimeException>()
        {
          @Override public VectorsClasses matchIntegerUnsigned(
            final TIntegerUnsigned t)
          {
            throw new UnsupportedOperationException(
              "Unsigned integer vectors are not supported");
          }

          @Override public VectorsClasses matchIntegerSigned(
            final TIntegerSigned t)
          {
            final int e_size = t.getSizeInBits().getValue().intValue();
            switch (e_size) {
              case 32: {
                switch (VectorClassMatcher.this.e_count) {
                  case 2: {
                    return new VectorsClasses(
                      Vector2IType.class,
                      VectorReadable2IType.class,
                      VectorByteBufferedM2I.class,
                      VectorByteBuffered2IType.class);
                  }
                  case 3: {
                    return new VectorsClasses(
                      Vector3IType.class,
                      VectorReadable3IType.class,
                      VectorByteBufferedM3I.class,
                      VectorByteBuffered3IType.class);
                  }
                  case 4: {
                    return new VectorsClasses(
                      Vector4IType.class,
                      VectorReadable4IType.class,
                      VectorByteBufferedM4I.class,
                      VectorByteBuffered4IType.class);
                  }
                }

                throw new UnsupportedOperationException(
                  "Unsupported integer vector size");
              }
              case 64: {
                switch (VectorClassMatcher.this.e_count) {
                  case 2: {
                    return new VectorsClasses(
                      Vector2LType.class,
                      VectorReadable2LType.class,
                      VectorByteBufferedM2L.class,
                      VectorByteBuffered2LType.class);
                  }
                  case 3: {
                    return new VectorsClasses(
                      Vector3LType.class,
                      VectorReadable3LType.class,
                      VectorByteBufferedM3L.class,
                      VectorByteBuffered3LType.class);
                  }
                  case 4: {
                    return new VectorsClasses(
                      Vector4LType.class,
                      VectorReadable4LType.class,
                      VectorByteBufferedM4L.class,
                      VectorByteBuffered4LType.class);
                  }
                }

                throw new UnsupportedOperationException(
                  "Unsupported integer vector size");
              }
            }

            throw new UnsupportedOperationException(
              "Unsupported integer element size");
          }

          @Override public VectorsClasses matchIntegerSignedNormalized(
            final TIntegerSignedNormalized t)
          {
            throw new UnsupportedOperationException(
              "Signed normalized integer vectors are not supported");
          }

          @Override public VectorsClasses matchIntegerUnsignedNormalized(
            final TIntegerUnsignedNormalized t)
          {
            throw new UnsupportedOperationException(
              "Unsigned normalized integer vectors are not supported");
          }
        });
    }

    @Override public VectorsClasses matchScalarFloat(
      final TFloat t)
    {
      final int e_size = t.getSizeInBits().getValue().intValue();
      switch (e_size) {
        case 16: {
          switch (this.e_count) {
            case 2: {
              return new VectorsClasses(
                Vector2Db16Type.class,
                VectorReadable2Db16Type.class,
                VectorByteBufferedM2Db16.class,
                VectorByteBuffered2Db16Type.class);
            }
            case 3: {
              return new VectorsClasses(
                Vector3Db16Type.class,
                VectorReadable3Db16Type.class,
                VectorByteBufferedM3Db16.class,
                VectorByteBuffered3Db16Type.class);
            }
            case 4: {
              return new VectorsClasses(
                Vector4Db16Type.class,
                VectorReadable4Db16Type.class,
                VectorByteBufferedM4Db16.class,
                VectorByteBuffered4Db16Type.class);
            }
          }

          throw new UnsupportedOperationException(
            "Unsupported float vector size");
        }

        case 32: {
          switch (this.e_count) {
            case 2: {
              return new VectorsClasses(
                Vector2FType.class,
                VectorReadable2FType.class,
                VectorByteBufferedM2F.class,
                VectorByteBuffered2FType.class);
            }
            case 3: {
              return new VectorsClasses(
                Vector3FType.class,
                VectorReadable3FType.class,
                VectorByteBufferedM3F.class,
                VectorByteBuffered3FType.class);
            }
            case 4: {
              return new VectorsClasses(
                Vector4FType.class,
                VectorReadable4FType.class,
                VectorByteBufferedM4F.class,
                VectorByteBuffered4FType.class);
            }
          }

          throw new UnsupportedOperationException(
            "Unsupported float vector size");
        }
        case 64: {
          switch (this.e_count) {
            case 2: {
              return new VectorsClasses(
                Vector2DType.class,
                VectorReadable2DType.class,
                VectorByteBufferedM2D.class,
                VectorByteBuffered2DType.class);
            }
            case 3: {
              return new VectorsClasses(
                Vector3DType.class,
                VectorReadable3DType.class,
                VectorByteBufferedM3D.class,
                VectorByteBuffered3DType.class);
            }
            case 4: {
              return new VectorsClasses(
                Vector4DType.class,
                VectorReadable4DType.class,
                VectorByteBufferedM4D.class,
                VectorByteBuffered4DType.class);
            }
          }

          throw new UnsupportedOperationException(
            "Unsupported double vector size");
        }
      }

      throw new UnsupportedOperationException(
        "Unsupported float vector element size");
    }
  }
}
