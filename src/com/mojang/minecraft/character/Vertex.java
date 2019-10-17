package com.mojang.minecraft.character;

import com.mojang.minecraft.character.Vec3;

public class Vertex {

   public Vec3 pos;
   public float u;
   public float v;


   public Vertex(float x, float y, float z, float u, float v) {
      this(new Vec3(x, y, z), u, v);
   }

   public Vertex remap(float u, float v) {
      return new Vertex(this, u, v);
   }

   public Vertex(Vertex vertex, float u, float v) {
      this.pos = vertex.pos;
      this.u = u;
      this.v = v;
   }

   public Vertex(Vec3 pos, float u, float v) {
      this.pos = pos;
      this.u = u;
      this.v = v;
   }
}
