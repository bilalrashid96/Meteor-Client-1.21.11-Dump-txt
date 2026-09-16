package meteordevelopment.meteorclient.renderer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.ByteBuffer;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_243;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

public class MeshBuilder {
   private static final boolean DEBUG = FabricLoader.getInstance().isDevelopmentEnvironment() || Boolean.getBoolean("meteor.render.debug");
   public double alpha;
   private final VertexFormat format;
   private final int primitiveVerticesSize;
   private final int primitiveIndicesCount;
   private ByteBuffer vertices;
   private long verticesPointerStart;
   private long verticesPointer;
   private ByteBuffer indices;
   private long indicesPointer;
   private int vertexI;
   private int indicesCount;
   private boolean building;
   private double cameraX;
   private double cameraZ;

   public MeshBuilder(RenderPipeline pipeline) {
      this(pipeline.getVertexFormat(), pipeline.getVertexFormatMode());
   }

   public MeshBuilder(VertexFormat format, VertexFormat.class_5596 drawMode) {
      this.alpha = (double)1.0F;
      this.vertices = null;
      this.indices = null;
      this.format = format;
      this.primitiveVerticesSize = format.getVertexSize();
      this.primitiveIndicesCount = drawMode.field_27384;
   }

   public MeshBuilder(VertexFormat format, VertexFormat.class_5596 drawMode, int vertexCount, int indexCount) {
      this(format, drawMode);
      this.allocateBuffers(vertexCount, indexCount);
   }

   public void begin() {
      if (this.building) {
         throw new IllegalStateException("Mesh.begin() called while already building.");
      } else {
         this.verticesPointer = this.verticesPointerStart;
         this.vertexI = 0;
         this.indicesCount = 0;
         this.building = true;
         if (Utils.rendering3D) {
            class_243 camera = MeteorClient.mc.field_1773.method_19418().method_71156();
            this.cameraX = camera.field_1352;
            this.cameraZ = camera.field_1350;
         } else {
            this.cameraX = (double)0.0F;
            this.cameraZ = (double)0.0F;
         }

      }
   }

   public MeshBuilder vec3(double x, double y, double z) {
      this.debugVertexBufferCapacity();
      long p = this.verticesPointer;
      MemoryUtil.memPutFloat(p, (float)(x - this.cameraX));
      MemoryUtil.memPutFloat(p + 4L, (float)y);
      MemoryUtil.memPutFloat(p + 8L, (float)(z - this.cameraZ));
      this.verticesPointer += 12L;
      return this;
   }

   public MeshBuilder vec2(double x, double y) {
      this.debugVertexBufferCapacity();
      long p = this.verticesPointer;
      MemoryUtil.memPutFloat(p, (float)x);
      MemoryUtil.memPutFloat(p + 4L, (float)y);
      this.verticesPointer += 8L;
      return this;
   }

   public MeshBuilder color(Color c) {
      this.debugVertexBufferCapacity();
      long p = this.verticesPointer;
      MemoryUtil.memPutByte(p, (byte)c.r);
      MemoryUtil.memPutByte(p + 1L, (byte)c.g);
      MemoryUtil.memPutByte(p + 2L, (byte)c.b);
      MemoryUtil.memPutByte(p + 3L, (byte)((int)((float)c.a * (float)this.alpha)));
      this.verticesPointer += 4L;
      return this;
   }

   public int next() {
      return this.vertexI++;
   }

   public void line(int i1, int i2) {
      this.debugIndexBufferCapacity();
      long p = this.indicesPointer + (long)this.indicesCount * 4L;
      MemoryUtil.memPutInt(p, i1);
      MemoryUtil.memPutInt(p + 4L, i2);
      this.indicesCount += 2;
   }

   public void quad(int i1, int i2, int i3, int i4) {
      this.debugIndexBufferCapacity();
      long p = this.indicesPointer + (long)this.indicesCount * 4L;
      MemoryUtil.memPutInt(p, i1);
      MemoryUtil.memPutInt(p + 4L, i2);
      MemoryUtil.memPutInt(p + 8L, i3);
      MemoryUtil.memPutInt(p + 12L, i3);
      MemoryUtil.memPutInt(p + 16L, i4);
      MemoryUtil.memPutInt(p + 20L, i1);
      this.indicesCount += 6;
   }

   public void triangle(int i1, int i2, int i3) {
      this.debugIndexBufferCapacity();
      long p = this.indicesPointer + (long)this.indicesCount * 4L;
      MemoryUtil.memPutInt(p, i1);
      MemoryUtil.memPutInt(p + 4L, i2);
      MemoryUtil.memPutInt(p + 8L, i3);
      this.indicesCount += 3;
   }

   public void ensureQuadCapacity() {
      this.ensureCapacity(4, 6);
   }

   public void ensureTriCapacity() {
      this.ensureCapacity(3, 3);
   }

   public void ensureLineCapacity() {
      this.ensureCapacity(2, 2);
   }

   public void ensureCapacity(int vertexCount, int indexCount) {
      if (DEBUG && indexCount % this.primitiveIndicesCount != 0) {
         throw new IllegalArgumentException("Unexpected amount of indices written to MeshBuilder.");
      } else if (this.vertices != null && this.indices != null) {
         if ((this.vertexI + vertexCount) * this.primitiveVerticesSize >= this.vertices.capacity()) {
            int offset = this.getVerticesOffset();
            int newSize = Math.max(this.vertices.capacity() * 2, this.vertices.capacity() + vertexCount * this.primitiveVerticesSize);
            ByteBuffer newVertices = BufferUtils.createByteBuffer(newSize);
            MemoryUtil.memCopy(MemoryUtil.memAddress0(this.vertices), MemoryUtil.memAddress0(newVertices), (long)offset);
            this.vertices = newVertices;
            this.verticesPointerStart = MemoryUtil.memAddress0(this.vertices);
            this.verticesPointer = this.verticesPointerStart + (long)offset;
         }

         if ((this.indicesCount + indexCount) * 4 >= this.indices.capacity()) {
            int newSize = Math.max(this.indices.capacity() * 2, this.indices.capacity() + indexCount * 4);
            ByteBuffer newIndices = BufferUtils.createByteBuffer(newSize);
            MemoryUtil.memCopy(MemoryUtil.memAddress0(this.indices), MemoryUtil.memAddress0(newIndices), (long)this.indicesCount * 4L);
            this.indices = newIndices;
            this.indicesPointer = MemoryUtil.memAddress0(this.indices);
         }

      } else {
         this.allocateBuffers(1024, 2048);
      }
   }

   private void allocateBuffers(int vertexCount, int indexCount) {
      this.vertices = BufferUtils.createByteBuffer(this.primitiveVerticesSize * vertexCount);
      this.verticesPointer = this.verticesPointerStart = MemoryUtil.memAddress0(this.vertices);
      this.indices = BufferUtils.createByteBuffer(indexCount * 4);
      this.indicesPointer = MemoryUtil.memAddress0(this.indices);
   }

   public void end() {
      if (!this.building) {
         throw new IllegalStateException("Mesh.end() called while not building.");
      } else {
         this.building = false;
      }
   }

   public boolean isBuilding() {
      return this.building;
   }

   public GpuBuffer getVertexBuffer() {
      this.vertices.limit(this.getVerticesOffset());
      return this.format.uploadImmediateVertexBuffer(this.vertices);
   }

   public GpuBuffer getIndexBuffer() {
      this.indices.limit(this.indicesCount * 4);
      return this.format.uploadImmediateIndexBuffer(this.indices);
   }

   public int getIndicesCount() {
      return this.indicesCount;
   }

   private int getVerticesOffset() {
      return (int)(this.verticesPointer - this.verticesPointerStart);
   }

   private void debugVertexBufferCapacity() {
      if (DEBUG && (this.vertices == null || this.vertexI * this.primitiveVerticesSize >= this.vertices.capacity())) {
         throw new IndexOutOfBoundsException("Vertices written to MeshBuilder without calling 'ensureCapacity()' first!");
      }
   }

   private void debugIndexBufferCapacity() {
      if (DEBUG && (this.indices == null || this.indicesCount * 4 >= this.indices.capacity())) {
         throw new IndexOutOfBoundsException("Indices written to MeshBuilder without calling 'ensureCapacity()' first!");
      }
   }
}
