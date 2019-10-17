package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.Player;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.tile.Bush;
import com.mojang.minecraft.level.tile.CalmLiquidTile;
import com.mojang.minecraft.level.tile.DirtTile;
import com.mojang.minecraft.level.tile.GrassTile;
import com.mojang.minecraft.level.tile.LiquidTile;
import com.mojang.minecraft.particle.Particle;
import com.mojang.minecraft.particle.ParticleEngine;
import com.mojang.minecraft.phys.AABB;
import com.mojang.minecraft.renderer.Tesselator;
import java.util.Random;

public class Tile {

   public static final int NOT_LIQUID = 0;
   public static final int LIQUID_WATER = 1;
   public static final int LIQUID_LAVA = 2;
   public static final Tile[] tiles = new Tile[256];
   public static final boolean[] shouldTick = new boolean[256];
   public static final Tile empty = null;
   public static final Tile rock = new Tile(1, 1);
   public static final Tile grass = new GrassTile(2);
   public static final Tile dirt = new DirtTile(3, 2);
   public static final Tile stoneBrick = new Tile(4, 16);
   public static final Tile wood = new Tile(5, 4);
   public static final Tile bush = new Bush(6);
   public static final Tile unbreakable = new Tile(7, 17);
   public static final Tile water = new LiquidTile(8, 1);
   public static final Tile calmWater = new CalmLiquidTile(9, 1);
   public static final Tile lava = new LiquidTile(10, 2);
   public static final Tile calmLava = new CalmLiquidTile(11, 2);
   public int tex;
   public final int id;
   protected float xx0;
   protected float yy0;
   protected float zz0;
   protected float xx1;
   protected float yy1;
   protected float zz1;


   protected Tile(int id) {
      tiles[id] = this;
      this.id = id;
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   protected void setTicking(boolean tick) {
      shouldTick[this.id] = tick;
   }

   protected void setShape(float x0, float y0, float z0, float x1, float y1, float z1) {
      this.xx0 = x0;
      this.yy0 = y0;
      this.zz0 = z0;
      this.xx1 = x1;
      this.yy1 = y1;
      this.zz1 = z1;
   }

   protected Tile(int id, int tex) {
      this(id);
      this.tex = tex;
   }

   public void render(Tesselator t, Level level, int layer, int x, int y, int z) {
      byte c1 = -1;
      byte c2 = -52;
      byte c3 = -103;
      if(this.shouldRenderFace(level, x, y - 1, z, layer, 0)) {
         t.color(c1, c1, c1);
         this.renderFace(t, x, y, z, 0);
      }

      if(this.shouldRenderFace(level, x, y + 1, z, layer, 1)) {
         t.color(c1, c1, c1);
         this.renderFace(t, x, y, z, 1);
      }

      if(this.shouldRenderFace(level, x, y, z - 1, layer, 2)) {
         t.color(c2, c2, c2);
         this.renderFace(t, x, y, z, 2);
      }

      if(this.shouldRenderFace(level, x, y, z + 1, layer, 3)) {
         t.color(c2, c2, c2);
         this.renderFace(t, x, y, z, 3);
      }

      if(this.shouldRenderFace(level, x - 1, y, z, layer, 4)) {
         t.color(c3, c3, c3);
         this.renderFace(t, x, y, z, 4);
      }

      if(this.shouldRenderFace(level, x + 1, y, z, layer, 5)) {
         t.color(c3, c3, c3);
         this.renderFace(t, x, y, z, 5);
      }

   }

   protected boolean shouldRenderFace(Level level, int x, int y, int z, int layer, int face) {
      boolean layerOk = true;
      if(layer == 2) {
         return false;
      } else {
         if(layer >= 0) {
            layerOk = level.isLit(x, y, z) ^ layer == 1;
         }

         return !level.isSolidTile(x, y, z) && layerOk;
      }
   }

   protected int getTexture(int face) {
      return this.tex;
   }

   public void renderFace(Tesselator t, int x, int y, int z, int face) {
      int tex = this.getTexture(face);
      int xt = tex % 16 * 16;
      int yt = tex / 16 * 16;
      float u0 = (float)xt / 256.0F;
      float u1 = ((float)xt + 15.99F) / 256.0F;
      float v0 = (float)yt / 256.0F;
      float v1 = ((float)yt + 15.99F) / 256.0F;
      float x0 = (float)x + this.xx0;
      float x1 = (float)x + this.xx1;
      float y0 = (float)y + this.yy0;
      float y1 = (float)y + this.yy1;
      float z0 = (float)z + this.zz0;
      float z1 = (float)z + this.zz1;
      if(face == 0) {
         t.vertexUV(x0, y0, z1, u0, v1);
         t.vertexUV(x0, y0, z0, u0, v0);
         t.vertexUV(x1, y0, z0, u1, v0);
         t.vertexUV(x1, y0, z1, u1, v1);
      } else if(face == 1) {
         t.vertexUV(x1, y1, z1, u1, v1);
         t.vertexUV(x1, y1, z0, u1, v0);
         t.vertexUV(x0, y1, z0, u0, v0);
         t.vertexUV(x0, y1, z1, u0, v1);
      } else if(face == 2) {
         t.vertexUV(x0, y1, z0, u1, v0);
         t.vertexUV(x1, y1, z0, u0, v0);
         t.vertexUV(x1, y0, z0, u0, v1);
         t.vertexUV(x0, y0, z0, u1, v1);
      } else if(face == 3) {
         t.vertexUV(x0, y1, z1, u0, v0);
         t.vertexUV(x0, y0, z1, u0, v1);
         t.vertexUV(x1, y0, z1, u1, v1);
         t.vertexUV(x1, y1, z1, u1, v0);
      } else if(face == 4) {
         t.vertexUV(x0, y1, z1, u1, v0);
         t.vertexUV(x0, y1, z0, u0, v0);
         t.vertexUV(x0, y0, z0, u0, v1);
         t.vertexUV(x0, y0, z1, u1, v1);
      } else if(face == 5) {
         t.vertexUV(x1, y0, z1, u0, v1);
         t.vertexUV(x1, y0, z0, u1, v1);
         t.vertexUV(x1, y1, z0, u1, v0);
         t.vertexUV(x1, y1, z1, u0, v0);
      }
   }

   public void renderBackFace(Tesselator t, int x, int y, int z, int face) {
      int tex = this.getTexture(face);
      float u0 = (float)(tex % 16) / 16.0F;
      float u1 = u0 + 0.0624375F;
      float v0 = (float)(tex / 16) / 16.0F;
      float v1 = v0 + 0.0624375F;
      float x0 = (float)x + this.xx0;
      float x1 = (float)x + this.xx1;
      float y0 = (float)y + this.yy0;
      float y1 = (float)y + this.yy1;
      float z0 = (float)z + this.zz0;
      float z1 = (float)z + this.zz1;
      if(face == 0) {
         t.vertexUV(x1, y0, z1, u1, v1);
         t.vertexUV(x1, y0, z0, u1, v0);
         t.vertexUV(x0, y0, z0, u0, v0);
         t.vertexUV(x0, y0, z1, u0, v1);
      }

      if(face == 1) {
         t.vertexUV(x0, y1, z1, u0, v1);
         t.vertexUV(x0, y1, z0, u0, v0);
         t.vertexUV(x1, y1, z0, u1, v0);
         t.vertexUV(x1, y1, z1, u1, v1);
      }

      if(face == 2) {
         t.vertexUV(x0, y0, z0, u1, v1);
         t.vertexUV(x1, y0, z0, u0, v1);
         t.vertexUV(x1, y1, z0, u0, v0);
         t.vertexUV(x0, y1, z0, u1, v0);
      }

      if(face == 3) {
         t.vertexUV(x1, y1, z1, u1, v0);
         t.vertexUV(x1, y0, z1, u1, v1);
         t.vertexUV(x0, y0, z1, u0, v1);
         t.vertexUV(x0, y1, z1, u0, v0);
      }

      if(face == 4) {
         t.vertexUV(x0, y0, z1, u1, v1);
         t.vertexUV(x0, y0, z0, u0, v1);
         t.vertexUV(x0, y1, z0, u0, v0);
         t.vertexUV(x0, y1, z1, u1, v0);
      }

      if(face == 5) {
         t.vertexUV(x1, y1, z1, u0, v0);
         t.vertexUV(x1, y1, z0, u1, v0);
         t.vertexUV(x1, y0, z0, u1, v1);
         t.vertexUV(x1, y0, z1, u0, v1);
      }

   }

   public void renderFaceNoTexture(Player player, Tesselator t, int x, int y, int z, int face) {
      float x0 = (float)x + 0.0F;
      float x1 = (float)x + 1.0F;
      float y0 = (float)y + 0.0F;
      float y1 = (float)y + 1.0F;
      float z0 = (float)z + 0.0F;
      float z1 = (float)z + 1.0F;
      if(face == 0 && (float)y > player.y) {
         t.vertex(x0, y0, z1);
         t.vertex(x0, y0, z0);
         t.vertex(x1, y0, z0);
         t.vertex(x1, y0, z1);
      }

      if(face == 1 && (float)y < player.y) {
         t.vertex(x1, y1, z1);
         t.vertex(x1, y1, z0);
         t.vertex(x0, y1, z0);
         t.vertex(x0, y1, z1);
      }

      if(face == 2 && (float)z > player.z) {
         t.vertex(x0, y1, z0);
         t.vertex(x1, y1, z0);
         t.vertex(x1, y0, z0);
         t.vertex(x0, y0, z0);
      }

      if(face == 3 && (float)z < player.z) {
         t.vertex(x0, y1, z1);
         t.vertex(x0, y0, z1);
         t.vertex(x1, y0, z1);
         t.vertex(x1, y1, z1);
      }

      if(face == 4 && (float)x > player.x) {
         t.vertex(x0, y1, z1);
         t.vertex(x0, y1, z0);
         t.vertex(x0, y0, z0);
         t.vertex(x0, y0, z1);
      }

      if(face == 5 && (float)x < player.x) {
         t.vertex(x1, y0, z1);
         t.vertex(x1, y0, z0);
         t.vertex(x1, y1, z0);
         t.vertex(x1, y1, z1);
      }

   }

   public final AABB getTileAABB(int x, int y, int z) {
      return new AABB((float)x, (float)y, (float)z, (float)(x + 1), (float)(y + 1), (float)(z + 1));
   }

   public AABB getAABB(int x, int y, int z) {
      return new AABB((float)x, (float)y, (float)z, (float)(x + 1), (float)(y + 1), (float)(z + 1));
   }

   public boolean blocksLight() {
      return true;
   }

   public boolean isSolid() {
      return true;
   }

   public boolean mayPick() {
      return true;
   }

   public void tick(Level level, int x, int y, int z, Random random) {}

   public void destroy(Level level, int x, int y, int z, ParticleEngine particleEngine) {
      byte SD = 4;

      for(int xx = 0; xx < SD; ++xx) {
         for(int yy = 0; yy < SD; ++yy) {
            for(int zz = 0; zz < SD; ++zz) {
               float xp = (float)x + ((float)xx + 0.5F) / (float)SD;
               float yp = (float)y + ((float)yy + 0.5F) / (float)SD;
               float zp = (float)z + ((float)zz + 0.5F) / (float)SD;
               particleEngine.add(new Particle(level, xp, yp, zp, xp - (float)x - 0.5F, yp - (float)y - 0.5F, zp - (float)z - 0.5F, this.tex));
            }
         }
      }

   }

   public int getLiquidType() {
      return 0;
   }

   public void neighborChanged(Level level, int x, int y, int z, int type) {}
}
