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

import com.io7m.jpra.model.types.TArray;
import com.io7m.jpra.model.types.TBooleanSet;
import com.io7m.jpra.model.types.TFloat;
import com.io7m.jpra.model.types.TIntegerSigned;
import com.io7m.jpra.model.types.TIntegerSignedNormalized;
import com.io7m.jpra.model.types.TIntegerType;
import com.io7m.jpra.model.types.TIntegerUnsigned;
import com.io7m.jpra.model.types.TIntegerUnsignedNormalized;
import com.io7m.jpra.model.types.TMatrix;
import com.io7m.jpra.model.types.TPacked;
import com.io7m.jpra.model.types.TRecord;
import com.io7m.jpra.model.types.TString;
import com.io7m.jpra.model.types.TType;
import com.io7m.jpra.model.types.TVector;
import com.io7m.jpra.model.types.TypeIntegerMatcherType;
import com.io7m.jpra.model.types.TypeMatcherType;
import com.io7m.jpra.model.types.TypeScalarMatcherType;
import com.io7m.jpra.model.types.TypeScalarType;
import com.io7m.jpra.runtime.java.JPRATypeModel;
import com.io7m.jtensors.core.unparameterized.matrices.MatrixReadable2x2DType;
import com.io7m.jtensors.core.unparameterized.matrices.MatrixReadable3x3DType;
import com.io7m.jtensors.core.unparameterized.matrices.MatrixReadable4x4DType;
import com.io7m.jtensors.core.unparameterized.vectors.VectorReadable2DType;
import com.io7m.jtensors.core.unparameterized.vectors.VectorReadable2LType;
import com.io7m.jtensors.core.unparameterized.vectors.VectorReadable3DType;
import com.io7m.jtensors.core.unparameterized.vectors.VectorReadable3LType;
import com.io7m.jtensors.core.unparameterized.vectors.VectorReadable4DType;
import com.io7m.jtensors.core.unparameterized.vectors.VectorReadable4LType;
import com.io7m.jtensors.storage.api.unparameterized.matrices.MatrixStorage2x2Type;
import com.io7m.jtensors.storage.api.unparameterized.matrices.MatrixStorage3x3Type;
import com.io7m.jtensors.storage.api.unparameterized.matrices.MatrixStorage4x4Type;
import com.io7m.jtensors.storage.api.unparameterized.vectors.VectorStorageFloating2Type;
import com.io7m.jtensors.storage.api.unparameterized.vectors.VectorStorageFloating3Type;
import com.io7m.jtensors.storage.api.unparameterized.vectors.VectorStorageFloating4Type;
import com.io7m.jtensors.storage.api.unparameterized.vectors.VectorStorageIntegral2Type;
import com.io7m.jtensors.storage.api.unparameterized.vectors.VectorStorageIntegral3Type;
import com.io7m.jtensors.storage.api.unparameterized.vectors.VectorStorageIntegral4Type;
import com.io7m.jtensors.storage.bytebuffered.MatrixByteBuffered2x2s32;
import com.io7m.jtensors.storage.bytebuffered.MatrixByteBuffered2x2s64;
import com.io7m.jtensors.storage.bytebuffered.MatrixByteBuffered3x3s32;
import com.io7m.jtensors.storage.bytebuffered.MatrixByteBuffered3x3s64;
import com.io7m.jtensors.storage.bytebuffered.MatrixByteBuffered4x4s32;
import com.io7m.jtensors.storage.bytebuffered.MatrixByteBuffered4x4s64;
import com.io7m.jtensors.storage.bytebuffered.VectorByteBufferedFloating2s16;
import com.io7m.jtensors.storage.bytebuffered.VectorByteBufferedFloating2s32;
import com.io7m.jtensors.storage.bytebuffered.VectorByteBufferedFloating2s64;
import com.io7m.jtensors.storage.bytebuffered.VectorByteBufferedFloating3s16;
import com.io7m.jtensors.storage.bytebuffered.VectorByteBufferedFloating3s32;
import com.io7m.jtensors.storage.bytebuffered.VectorByteBufferedFloating3s64;
import com.io7m.jtensors.storage.bytebuffered.VectorByteBufferedFloating4s16;
import com.io7m.jtensors.storage.bytebuffered.VectorByteBufferedFloating4s32;
import com.io7m.jtensors.storage.bytebuffered.VectorByteBufferedFloating4s64;
import com.io7m.jtensors.storage.bytebuffered.VectorByteBufferedIntegral2s32;
import com.io7m.jtensors.storage.bytebuffered.VectorByteBufferedIntegral2s64;
import com.io7m.jtensors.storage.bytebuffered.VectorByteBufferedIntegral3s32;
import com.io7m.jtensors.storage.bytebuffered.VectorByteBufferedIntegral3s64;
import com.io7m.jtensors.storage.bytebuffered.VectorByteBufferedIntegral4s32;
import com.io7m.jtensors.storage.bytebuffered.VectorByteBufferedIntegral4s64;
import com.io7m.junreachable.UnreachableCodeException;
import com.squareup.javapoet.ClassName;

import java.util.Objects;

/**
 * Functions to return sets of classes for a given type.
 */

final class JPRAClasses
{
  private JPRAClasses()
  {
    throw new UnreachableCodeException();
  }

  static VectorsClasses getVectorClassesFor(
    final TVector v)
  {
    Objects.requireNonNull(v, "v");
    final TypeScalarType e_type = v.getElementType();
    final int e_count = v.getElementCount().getValue().intValue();
    return e_type.matchTypeScalar(new VectorClassMatcher(e_count));
  }

  public static MatrixClasses getMatrixClassesFor(final TMatrix t)
  {
    Objects.requireNonNull(t, "t");
    final TypeScalarType e_type = t.getElementType();
    final int e_width = t.getWidth().getValue().intValue();
    return e_type.matchTypeScalar(new MatrixClassMatcher(e_width, e_width));
  }

  public static ClassName
  getModelScalarTypeForScalarType(
    final TypeScalarType type)
  {
    return type.matchTypeScalar(new TypeScalarMatcherType<ClassName,
      RuntimeException>()
    {
      @Override
      public ClassName matchScalarInteger(
        final
        TIntegerType t)
      {
        return t.matchTypeInteger(
          new TypeIntegerMatcherType<ClassName,
            RuntimeException>()
          {
            @Override
            public ClassName
            matchIntegerUnsigned(
              final TIntegerUnsigned t)
            {
              return ClassName.get(JPRATypeModel.JPRAIntegerUnsigned.class);
            }

            @Override
            public ClassName
            matchIntegerSigned(
              final TIntegerSigned t)
            {
              return ClassName.get(JPRATypeModel.JPRAIntegerSigned.class);
            }

            @Override
            public ClassName
            matchIntegerSignedNormalized(
              final TIntegerSignedNormalized t)
            {
              return ClassName.get(
                JPRATypeModel.JPRAIntegerSignedNormalized.class);
            }

            @Override
            public ClassName
            matchIntegerUnsignedNormalized(
              final TIntegerUnsignedNormalized t)
            {
              return ClassName.get(
                JPRATypeModel.JPRAIntegerUnsignedNormalized.class);
            }
          });
      }

      @Override
      public ClassName matchScalarFloat(final TFloat t)
      {
        return ClassName.get(JPRATypeModel.JPRAFloat.class);
      }
    });
  }

  public static ClassName
  getModelTypeForType(final TType type)
  {
    return type.matchType(
      new TypeMatcherType<ClassName, RuntimeException>()
      {
        @Override
        public ClassName matchArray(
          final TArray t)
        {
          return ClassName.get(JPRATypeModel.JPRAArray.class);
        }

        @Override
        public ClassName matchString(
          final TString t)
        {
          return ClassName.get(JPRATypeModel.JPRAString.class);
        }

        @Override
        public ClassName matchBooleanSet(
          final TBooleanSet t)
        {
          return ClassName.get(JPRATypeModel.JPRABooleanSet.class);
        }

        @Override
        public ClassName matchInteger(
          final TIntegerType t)
        {
          return getModelScalarTypeForScalarType(t);
        }

        @Override
        public ClassName matchFloat(
          final TFloat t)
        {
          return getModelScalarTypeForScalarType(t);
        }

        @Override
        public ClassName matchVector(
          final TVector t)
        {
          return ClassName.get(JPRATypeModel.JPRAVector.class);
        }

        @Override
        public ClassName matchMatrix(
          final TMatrix t)
        {
          return ClassName.get(JPRATypeModel.JPRAMatrix.class);
        }

        @Override
        public ClassName matchRecord(
          final TRecord t)
        {
          return ClassName.get(JPRATypeModel.JPRAUserDefined.class);
        }

        @Override
        public ClassName matchPacked(
          final TPacked t)
        {
          return ClassName.get(JPRATypeModel.JPRAUserDefined.class);
        }
      });
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
      this.base_interface = Objects.requireNonNull(in_base, "Base");
      this.base_readable = Objects.requireNonNull(in_readable, "Readable");
      this.buffered_constructors = Objects.requireNonNull(
        in_buffered_cons,
        "Buffered_cons");
      this.buffered_interface = Objects.requireNonNull(in_buffered, "Buffered");
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

  static final class MatrixClasses
  {
    private final Class<?> base_interface;
    private final Class<?> base_readable;
    private final Class<?> buffered_constructors;
    private final Class<?> buffered_interface;

    MatrixClasses(
      final Class<?> in_base,
      final Class<?> in_readable,
      final Class<?> in_buffered_cons,
      final Class<?> in_buffered)
    {
      this.base_interface = Objects.requireNonNull(in_base, "Base");
      this.base_readable = Objects.requireNonNull(in_readable, "Readable");
      this.buffered_constructors = Objects.requireNonNull(
        in_buffered_cons,
        "Buffered_cons");
      this.buffered_interface = Objects.requireNonNull(in_buffered, "Buffered");
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

  private static final class MatrixClassMatcher
    implements TypeScalarMatcherType<MatrixClasses, RuntimeException>
  {
    private final int width;
    private final int height;

    MatrixClassMatcher(
      final int in_width,
      final int in_height)
    {
      this.width = in_width;
      this.height = in_height;
    }

    @Override
    public MatrixClasses matchScalarInteger(
      final TIntegerType t)
    {
      throw new UnsupportedOperationException(
        "Integer matrices are not supported");
    }

    @Override
    public MatrixClasses matchScalarFloat(
      final TFloat t)
    {
      final int e_size = t.getSizeInBits().getValue().intValue();
      switch (e_size) {
        case 32: {
          switch (this.width) {
            case 2:
              return new MatrixClasses(
                MatrixStorage2x2Type.class,
                MatrixReadable2x2DType.class,
                MatrixByteBuffered2x2s32.class,
                MatrixStorage2x2Type.class);
            case 3:
              return new MatrixClasses(
                MatrixStorage3x3Type.class,
                MatrixReadable3x3DType.class,
                MatrixByteBuffered3x3s32.class,
                MatrixStorage3x3Type.class);
            case 4:
              return new MatrixClasses(
                MatrixStorage4x4Type.class,
                MatrixReadable4x4DType.class,
                MatrixByteBuffered4x4s32.class,
                MatrixStorage4x4Type.class);
            default:
              throw new UnsupportedOperationException("Unsupported matrix size");
          }
        }

        case 64: {
          switch (this.width) {
            case 2:
              return new MatrixClasses(
                MatrixStorage2x2Type.class,
                MatrixReadable2x2DType.class,
                MatrixByteBuffered2x2s64.class,
                MatrixStorage2x2Type.class);
            case 3:
              return new MatrixClasses(
                MatrixStorage3x3Type.class,
                MatrixReadable3x3DType.class,
                MatrixByteBuffered3x3s64.class,
                MatrixStorage3x3Type.class);
            case 4:
              return new MatrixClasses(
                MatrixStorage4x4Type.class,
                MatrixReadable4x4DType.class,
                MatrixByteBuffered4x4s64.class,
                MatrixStorage4x4Type.class);
            default:
              throw new UnsupportedOperationException("Unsupported matrix size");
          }
        }

        default:
          throw new UnsupportedOperationException(
            "Unsupported matrix element size");
      }
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

    @Override
    public VectorsClasses matchScalarInteger(
      final TIntegerType t)
    {
      return t.matchTypeInteger(new TypeIntegerMatcherType<VectorsClasses,
        RuntimeException>()
      {
        @Override
        public VectorsClasses matchIntegerUnsigned(
          final TIntegerUnsigned t)
        {
          throw new UnsupportedOperationException(
            "Unsigned integer vectors are not supported");
        }

        @Override
        public VectorsClasses matchIntegerSigned(
          final TIntegerSigned t)
        {
          final int e_size = t.getSizeInBits().getValue().intValue();
          switch (e_size) {
            case 32: {
              switch (VectorClassMatcher.this.e_count) {
                case 2: {
                  return new VectorsClasses(
                    VectorStorageIntegral2Type.class,
                    VectorReadable2LType.class,
                    VectorByteBufferedIntegral2s32.class,
                    VectorStorageIntegral2Type.class);
                }
                case 3: {
                  return new VectorsClasses(
                    VectorStorageIntegral3Type.class,
                    VectorReadable3LType.class,
                    VectorByteBufferedIntegral3s32.class,
                    VectorStorageIntegral3Type.class);
                }
                case 4: {
                  return new VectorsClasses(
                    VectorStorageIntegral4Type.class,
                    VectorReadable4LType.class,
                    VectorByteBufferedIntegral4s32.class,
                    VectorStorageIntegral4Type.class);
                }
                default:
                  throw new UnsupportedOperationException(
                    "Unsupported integer vector size");
              }


            }
            case 64: {
              switch (VectorClassMatcher.this.e_count) {
                case 2: {
                  return new VectorsClasses(
                    VectorStorageIntegral2Type.class,
                    VectorReadable2LType.class,
                    VectorByteBufferedIntegral2s64.class,
                    VectorStorageIntegral2Type.class);
                }
                case 3: {
                  return new VectorsClasses(
                    VectorStorageIntegral3Type.class,
                    VectorReadable3LType.class,
                    VectorByteBufferedIntegral3s64.class,
                    VectorStorageIntegral3Type.class);
                }
                case 4: {
                  return new VectorsClasses(
                    VectorStorageIntegral4Type.class,
                    VectorReadable4LType.class,
                    VectorByteBufferedIntegral4s64.class,
                    VectorStorageIntegral4Type.class);
                }
                default:
                  throw new UnsupportedOperationException(
                    "Unsupported integer vector size");
              }
            }

            default:
              throw new UnsupportedOperationException(
                "Unsupported integer element size");
          }
        }

        @Override
        public VectorsClasses matchIntegerSignedNormalized(
          final TIntegerSignedNormalized t)
        {
          throw new UnsupportedOperationException(
            "Signed normalized integer vectors are not supported");
        }

        @Override
        public VectorsClasses matchIntegerUnsignedNormalized(
          final TIntegerUnsignedNormalized t)
        {
          throw new UnsupportedOperationException(
            "Unsigned normalized integer vectors are not supported");
        }
      });
    }

    @Override
    public VectorsClasses matchScalarFloat(
      final TFloat t)
    {
      final int e_size = t.getSizeInBits().getValue().intValue();
      switch (e_size) {
        case 16: {
          switch (this.e_count) {
            case 2: {
              return new VectorsClasses(
                VectorStorageFloating2Type.class,
                VectorReadable2DType.class,
                VectorByteBufferedFloating2s16.class,
                VectorStorageFloating2Type.class);
            }
            case 3: {
              return new VectorsClasses(
                VectorStorageFloating3Type.class,
                VectorReadable3DType.class,
                VectorByteBufferedFloating3s16.class,
                VectorStorageFloating3Type.class);
            }
            case 4: {
              return new VectorsClasses(
                VectorStorageFloating4Type.class,
                VectorReadable4DType.class,
                VectorByteBufferedFloating4s16.class,
                VectorStorageFloating4Type.class);
            }
            default:
              throw new UnsupportedOperationException(
                "Unsupported float vector size");
          }
        }

        case 32: {
          switch (this.e_count) {
            case 2: {
              return new VectorsClasses(
                VectorStorageFloating2Type.class,
                VectorReadable2DType.class,
                VectorByteBufferedFloating2s32.class,
                VectorStorageFloating2Type.class);
            }
            case 3: {
              return new VectorsClasses(
                VectorStorageFloating3Type.class,
                VectorReadable3DType.class,
                VectorByteBufferedFloating3s32.class,
                VectorStorageFloating3Type.class);
            }
            case 4: {
              return new VectorsClasses(
                VectorStorageFloating4Type.class,
                VectorReadable4DType.class,
                VectorByteBufferedFloating4s32.class,
                VectorStorageFloating4Type.class);
            }
            default:
              throw new UnsupportedOperationException(
                "Unsupported float vector size");
          }
        }

        case 64: {
          switch (this.e_count) {
            case 2: {
              return new VectorsClasses(
                VectorStorageFloating2Type.class,
                VectorReadable2DType.class,
                VectorByteBufferedFloating2s64.class,
                VectorStorageFloating2Type.class);
            }
            case 3: {
              return new VectorsClasses(
                VectorStorageFloating3Type.class,
                VectorReadable3DType.class,
                VectorByteBufferedFloating3s64.class,
                VectorStorageFloating3Type.class);
            }
            case 4: {
              return new VectorsClasses(
                VectorStorageFloating4Type.class,
                VectorReadable4DType.class,
                VectorByteBufferedFloating4s64.class,
                VectorStorageFloating4Type.class);
            }
            default:
              throw new UnsupportedOperationException(
                "Unsupported double vector size");
          }
        }

        default:
          throw new UnsupportedOperationException(
            "Unsupported float vector element size");
      }
    }
  }
}
