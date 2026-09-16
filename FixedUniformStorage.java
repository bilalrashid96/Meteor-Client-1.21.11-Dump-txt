package meteordevelopment.meteorclient.renderer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import net.minecraft.class_11280;
import net.minecraft.class_11285;
import net.minecraft.class_3532;

public class FixedUniformStorage<T extends class_11280.class_11281> {
   private final class_11285 buffer;
   private final int blockSize;
   private final int capacity;
   private int size;

   public FixedUniformStorage(String name, int blockSize, int capacity) {
      GpuDevice gpuDevice = RenderSystem.getDevice();
      this.blockSize = class_3532.method_28139(blockSize, gpuDevice.getUniformOffsetAlignment());
      this.capacity = capacity;
      int alignedCapacity = class_3532.method_15339(capacity);
      this.size = 0;
      this.buffer = new class_11285(() -> name + " x" + this.blockSize, 130, this.blockSize * alignedCapacity);
   }

   public GpuBufferSlice write(T value) {
      if (this.size >= this.capacity) {
         throw new IndexOutOfBoundsException(String.format("Index %s out of bounds for length %s", this.size, this.capacity));
      } else {
         int i = this.size * this.blockSize;
         GpuBufferSlice slice = this.buffer.method_71119().slice((long)i, (long)this.blockSize);
         GpuBuffer.MappedView mappedView = RenderSystem.getDevice().createCommandEncoder().mapBuffer(slice, false, true);

         try {
            value.method_71104(mappedView.data());
         } catch (Throwable var8) {
            if (mappedView != null) {
               try {
                  mappedView.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (mappedView != null) {
            mappedView.close();
         }

         ++this.size;
         return slice;
      }
   }

   public GpuBufferSlice[] writeAll(T[] values) {
      if (values.length == 0) {
         return new GpuBufferSlice[0];
      } else if (this.size + values.length > this.capacity) {
         throw new IndexOutOfBoundsException(String.format("Index %s out of bounds for length %s", this.size + values.length - 1, this.capacity));
      } else {
         int i = this.size * this.blockSize;
         GpuBufferSlice[] gpuBufferSlices = new GpuBufferSlice[values.length];
         GpuBuffer ubo = this.buffer.method_71119();
         GpuBuffer.MappedView mappedView = RenderSystem.getDevice().createCommandEncoder().mapBuffer(ubo.slice((long)i, (long)(values.length * this.blockSize)), false, true);

         try {
            ByteBuffer byteBuffer = mappedView.data();

            for(int j = 0; j < values.length; ++j) {
               T uploadable = values[j];
               gpuBufferSlices[j] = ubo.slice((long)(i + j * this.blockSize), (long)this.blockSize);
               byteBuffer.position(j * this.blockSize);
               uploadable.method_71104(byteBuffer);
            }
         } catch (Throwable var10) {
            if (mappedView != null) {
               try {
                  mappedView.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }
            }

            throw var10;
         }

         if (mappedView != null) {
            mappedView.close();
         }

         this.size += values.length;
         return gpuBufferSlices;
      }
   }

   public void clear() {
      this.size = 0;
      this.buffer.method_71121();
   }

   public void close() {
      this.buffer.close();
   }
}
