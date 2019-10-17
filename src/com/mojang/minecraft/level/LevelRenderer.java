package com.mojang.minecraft.level;

import com.mojang.minecraft.HitResult;
import com.mojang.minecraft.Player;
import com.mojang.minecraft.level.Chunk;
import com.mojang.minecraft.level.DirtyChunkSorter;
import com.mojang.minecraft.level.DistanceSorter;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.LevelListener;
import com.mojang.minecraft.level.tile.Tile;
import com.mojang.minecraft.phys.AABB;
import com.mojang.minecraft.renderer.Frustum;
import com.mojang.minecraft.renderer.Tesselator;
import com.mojang.minecraft.renderer.Textures;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.lwjgl.opengl.GL11;

public class LevelRenderer implements LevelListener {

   public static final int MAX_REBUILDS_PER_FRAME = 4;
   public static final int CHUNK_SIZE = 16;
   private Level level;
   private Chunk[] chunks;
   private Chunk[] sortedChunks;
   private int xChunks;
   private int yChunks;
   private int zChunks;
   private Textures textures;
   private int surroundLists;
   private int drawDistance = 0;
   float lX = 0.0F;
   float lY = 0.0F;
   float lZ = 0.0F;


   public LevelRenderer(Level level, Textures textures) {
      this.level = level;
      this.textures = textures;
      level.addListener(this);
      this.surroundLists = GL11.glGenLists(2);
      this.allChanged();
   }

   public void allChanged() {
      this.lX = -900000.0F;
      this.lY = -900000.0F;
      this.lZ = -900000.0F;
      this.xChunks = (this.level.width + 16 - 1) / 16;
      this.yChunks = (this.level.depth + 16 - 1) / 16;
      this.zChunks = (this.level.height + 16 - 1) / 16;
      this.chunks = new Chunk[this.xChunks * this.yChunks * this.zChunks];
      this.sortedChunks = new Chunk[this.xChunks * this.yChunks * this.zChunks];

      int i;
      for(i = 0; i < this.xChunks; ++i) {
         for(int y = 0; y < this.yChunks; ++y) {
            for(int z = 0; z < this.zChunks; ++z) {
               int x0 = i * 16;
               int y0 = y * 16;
               int z0 = z * 16;
               int x1 = (i + 1) * 16;
               int y1 = (y + 1) * 16;
               int z1 = (z + 1) * 16;
               if(x1 > this.level.width) {
                  x1 = this.level.width;
               }

               if(y1 > this.level.depth) {
                  y1 = this.level.depth;
               }

               if(z1 > this.level.height) {
                  z1 = this.level.height;
               }

               this.chunks[(i + y * this.xChunks) * this.zChunks + z] = new Chunk(this.level, x0, y0, z0, x1, y1, z1);
               this.sortedChunks[(i + y * this.xChunks) * this.zChunks + z] = this.chunks[(i + y * this.xChunks) * this.zChunks + z];
            }
         }
      }

      GL11.glNewList(this.surroundLists + 0, 4864);
      this.compileSurroundingGround();
      GL11.glEndList();
      GL11.glNewList(this.surroundLists + 1, 4864);
      this.compileSurroundingWater();
      GL11.glEndList();

      for(i = 0; i < this.chunks.length; ++i) {
         this.chunks[i].reset();
      }

   }

   public List getAllDirtyChunks() {
      ArrayList dirty = null;

      for(int i = 0; i < this.chunks.length; ++i) {
         Chunk chunk = this.chunks[i];
         if(chunk.isDirty()) {
            if(dirty == null) {
               dirty = new ArrayList();
            }

            dirty.add(chunk);
         }
      }

      return dirty;
   }

   public void render(Player player, int layer) {
      GL11.glEnable(3553);
      GL11.glBindTexture(3553, this.textures.loadTexture("/terrain.png", 9728));
      float xd = player.x - this.lX;
      float yd = player.y - this.lY;
      float zd = player.z - this.lZ;
      if(xd * xd + yd * yd + zd * zd > 64.0F) {
         this.lX = player.x;
         this.lY = player.y;
         this.lZ = player.z;
         Arrays.sort(this.sortedChunks, new DistanceSorter(player));
      }

      for(int i = 0; i < this.sortedChunks.length; ++i) {
         if(this.sortedChunks[i].visible) {
            float dd = (float)(256 / (1 << this.drawDistance));
            if(this.drawDistance == 0 || this.sortedChunks[i].distanceToSqr(player) < dd * dd) {
               this.sortedChunks[i].render(layer);
            }
         }
      }

      GL11.glDisable(3553);
   }

   public void renderSurroundingGround() {
      GL11.glCallList(this.surroundLists + 0);
   }

   public void compileSurroundingGround() {
      GL11.glEnable(3553);
      GL11.glBindTexture(3553, this.textures.loadTexture("/rock.png", 9728));
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      Tesselator t = Tesselator.instance;
      float y = this.level.getGroundLevel() - 2.0F;
      int s = 128;
      if(s > this.level.width) {
         s = this.level.width;
      }

      if(s > this.level.height) {
         s = this.level.height;
      }

      byte d = 5;
      t.begin();

      int zz;
      for(zz = -s * d; zz < this.level.width + s * d; zz += s) {
         for(int zz1 = -s * d; zz1 < this.level.height + s * d; zz1 += s) {
            float yy = y;
            if(zz >= 0 && zz1 >= 0 && zz < this.level.width && zz1 < this.level.height) {
               yy = 0.0F;
            }

            t.vertexUV((float)(zz + 0), yy, (float)(zz1 + s), 0.0F, (float)s);
            t.vertexUV((float)(zz + s), yy, (float)(zz1 + s), (float)s, (float)s);
            t.vertexUV((float)(zz + s), yy, (float)(zz1 + 0), (float)s, 0.0F);
            t.vertexUV((float)(zz + 0), yy, (float)(zz1 + 0), 0.0F, 0.0F);
         }
      }

      t.end();
      GL11.glBindTexture(3553, this.textures.loadTexture("/rock.png", 9728));
      GL11.glColor3f(0.8F, 0.8F, 0.8F);
      t.begin();

      for(zz = 0; zz < this.level.width; zz += s) {
         t.vertexUV((float)(zz + 0), 0.0F, 0.0F, 0.0F, 0.0F);
         t.vertexUV((float)(zz + s), 0.0F, 0.0F, (float)s, 0.0F);
         t.vertexUV((float)(zz + s), y, 0.0F, (float)s, y);
         t.vertexUV((float)(zz + 0), y, 0.0F, 0.0F, y);
         t.vertexUV((float)(zz + 0), y, (float)this.level.height, 0.0F, y);
         t.vertexUV((float)(zz + s), y, (float)this.level.height, (float)s, y);
         t.vertexUV((float)(zz + s), 0.0F, (float)this.level.height, (float)s, 0.0F);
         t.vertexUV((float)(zz + 0), 0.0F, (float)this.level.height, 0.0F, 0.0F);
      }

      GL11.glColor3f(0.6F, 0.6F, 0.6F);

      for(zz = 0; zz < this.level.height; zz += s) {
         t.vertexUV(0.0F, y, (float)(zz + 0), 0.0F, 0.0F);
         t.vertexUV(0.0F, y, (float)(zz + s), (float)s, 0.0F);
         t.vertexUV(0.0F, 0.0F, (float)(zz + s), (float)s, y);
         t.vertexUV(0.0F, 0.0F, (float)(zz + 0), 0.0F, y);
         t.vertexUV((float)this.level.width, 0.0F, (float)(zz + 0), 0.0F, y);
         t.vertexUV((float)this.level.width, 0.0F, (float)(zz + s), (float)s, y);
         t.vertexUV((float)this.level.width, y, (float)(zz + s), (float)s, 0.0F);
         t.vertexUV((float)this.level.width, y, (float)(zz + 0), 0.0F, 0.0F);
      }

      t.end();
      GL11.glDisable(3042);
      GL11.glDisable(3553);
   }

   public void renderSurroundingWater() {
      GL11.glCallList(this.surroundLists + 1);
   }

   public void compileSurroundingWater() {
      GL11.glEnable(3553);
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      GL11.glBindTexture(3553, this.textures.loadTexture("/water.png", 9728));
      float y = this.level.getGroundLevel();
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      Tesselator t = Tesselator.instance;
      int s = 128;
      if(s > this.level.width) {
         s = this.level.width;
      }

      if(s > this.level.height) {
         s = this.level.height;
      }

      byte d = 5;
      t.begin();

      for(int xx = -s * d; xx < this.level.width + s * d; xx += s) {
         for(int zz = -s * d; zz < this.level.height + s * d; zz += s) {
            float yy = y - 0.1F;
            if(xx < 0 || zz < 0 || xx >= this.level.width || zz >= this.level.height) {
               t.vertexUV((float)(xx + 0), yy, (float)(zz + s), 0.0F, (float)s);
               t.vertexUV((float)(xx + s), yy, (float)(zz + s), (float)s, (float)s);
               t.vertexUV((float)(xx + s), yy, (float)(zz + 0), (float)s, 0.0F);
               t.vertexUV((float)(xx + 0), yy, (float)(zz + 0), 0.0F, 0.0F);
               t.vertexUV((float)(xx + 0), yy, (float)(zz + 0), 0.0F, 0.0F);
               t.vertexUV((float)(xx + s), yy, (float)(zz + 0), (float)s, 0.0F);
               t.vertexUV((float)(xx + s), yy, (float)(zz + s), (float)s, (float)s);
               t.vertexUV((float)(xx + 0), yy, (float)(zz + s), 0.0F, (float)s);
            }
         }
      }

      t.end();
      GL11.glDisable(3042);
      GL11.glDisable(3553);
   }

   public void updateDirtyChunks(Player player) {
      List dirty = this.getAllDirtyChunks();
      if(dirty != null) {
         Collections.sort(dirty, new DirtyChunkSorter(player));

         for(int i = 0; i < 4 && i < dirty.size(); ++i) {
            ((Chunk)dirty.get(i)).rebuild();
         }

      }
   }

   public void pick(Player player, Frustum frustum) {
      Tesselator t = Tesselator.instance;
      float r = 2.5F;
      AABB box = player.bb.grow(r, r, r);
      int x0 = (int)box.x0;
      int x1 = (int)(box.x1 + 1.0F);
      int y0 = (int)box.y0;
      int y1 = (int)(box.y1 + 1.0F);
      int z0 = (int)box.z0;
      int z1 = (int)(box.z1 + 1.0F);
      GL11.glInitNames();
      GL11.glPushName(0);
      GL11.glPushName(0);

      for(int x = x0; x < x1; ++x) {
         GL11.glLoadName(x);
         GL11.glPushName(0);

         for(int y = y0; y < y1; ++y) {
            GL11.glLoadName(y);
            GL11.glPushName(0);

            for(int z = z0; z < z1; ++z) {
               Tile tile = Tile.tiles[this.level.getTile(x, y, z)];
               if(tile != null && tile.mayPick() && frustum.isVisible(tile.getTileAABB(x, y, z))) {
                  GL11.glLoadName(z);
                  GL11.glPushName(0);

                  for(int i = 0; i < 6; ++i) {
                     GL11.glLoadName(i);
                     t.begin();
                     tile.renderFaceNoTexture(player, t, x, y, z, i);
                     t.end();
                  }

                  GL11.glPopName();
               }
            }

            GL11.glPopName();
         }

         GL11.glPopName();
      }

      GL11.glPopName();
      GL11.glPopName();
   }

   public void renderHit(Player player, HitResult h, int mode, int tileType) {
      Tesselator t = Tesselator.instance;
      GL11.glEnable(3042);
      GL11.glEnable(3008);
      GL11.glBlendFunc(770, 1);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, ((float)Math.sin((double)System.currentTimeMillis() / 100.0D) * 0.2F + 0.4F) * 0.5F);
      if(mode == 0) {
         t.begin();

         for(int br = 0; br < 6; ++br) {
            Tile.rock.renderFaceNoTexture(player, t, h.x, h.y, h.z, br);
         }

         t.end();
      } else {
         GL11.glBlendFunc(770, 771);
         float var11 = (float)Math.sin((double)System.currentTimeMillis() / 100.0D) * 0.2F + 0.8F;
         GL11.glColor4f(var11, var11, var11, (float)Math.sin((double)System.currentTimeMillis() / 200.0D) * 0.2F + 0.5F);
         GL11.glEnable(3553);
         int id = this.textures.loadTexture("/terrain.png", 9728);
         GL11.glBindTexture(3553, id);
         int x = h.x;
         int y = h.y;
         int z = h.z;
         if(h.f == 0) {
            --y;
         }

         if(h.f == 1) {
            ++y;
         }

         if(h.f == 2) {
            --z;
         }

         if(h.f == 3) {
            ++z;
         }

         if(h.f == 4) {
            --x;
         }

         if(h.f == 5) {
            ++x;
         }

         t.begin();
         t.noColor();
         Tile.tiles[tileType].render(t, this.level, 0, x, y, z);
         Tile.tiles[tileType].render(t, this.level, 1, x, y, z);
         t.end();
         GL11.glDisable(3553);
      }

      GL11.glDisable(3042);
      GL11.glDisable(3008);
   }

   public void renderHitOutline(Player player, HitResult h, int mode, int tileType) {
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
      float x = (float)h.x;
      float y = (float)h.y;
      float z = (float)h.z;
      if(mode == 1) {
         if(h.f == 0) {
            --y;
         }

         if(h.f == 1) {
            ++y;
         }

         if(h.f == 2) {
            --z;
         }

         if(h.f == 3) {
            ++z;
         }

         if(h.f == 4) {
            --x;
         }

         if(h.f == 5) {
            ++x;
         }
      }

      GL11.glBegin(3);
      GL11.glVertex3f(x, y, z);
      GL11.glVertex3f(x + 1.0F, y, z);
      GL11.glVertex3f(x + 1.0F, y, z + 1.0F);
      GL11.glVertex3f(x, y, z + 1.0F);
      GL11.glVertex3f(x, y, z);
      GL11.glEnd();
      GL11.glBegin(3);
      GL11.glVertex3f(x, y + 1.0F, z);
      GL11.glVertex3f(x + 1.0F, y + 1.0F, z);
      GL11.glVertex3f(x + 1.0F, y + 1.0F, z + 1.0F);
      GL11.glVertex3f(x, y + 1.0F, z + 1.0F);
      GL11.glVertex3f(x, y + 1.0F, z);
      GL11.glEnd();
      GL11.glBegin(1);
      GL11.glVertex3f(x, y, z);
      GL11.glVertex3f(x, y + 1.0F, z);
      GL11.glVertex3f(x + 1.0F, y, z);
      GL11.glVertex3f(x + 1.0F, y + 1.0F, z);
      GL11.glVertex3f(x + 1.0F, y, z + 1.0F);
      GL11.glVertex3f(x + 1.0F, y + 1.0F, z + 1.0F);
      GL11.glVertex3f(x, y, z + 1.0F);
      GL11.glVertex3f(x, y + 1.0F, z + 1.0F);
      GL11.glEnd();
      GL11.glDisable(3042);
   }

   public void setDirty(int x0, int y0, int z0, int x1, int y1, int z1) {
      x0 /= 16;
      x1 /= 16;
      y0 /= 16;
      y1 /= 16;
      z0 /= 16;
      z1 /= 16;
      if(x0 < 0) {
         x0 = 0;
      }

      if(y0 < 0) {
         y0 = 0;
      }

      if(z0 < 0) {
         z0 = 0;
      }

      if(x1 >= this.xChunks) {
         x1 = this.xChunks - 1;
      }

      if(y1 >= this.yChunks) {
         y1 = this.yChunks - 1;
      }

      if(z1 >= this.zChunks) {
         z1 = this.zChunks - 1;
      }

      for(int x = x0; x <= x1; ++x) {
         for(int y = y0; y <= y1; ++y) {
            for(int z = z0; z <= z1; ++z) {
               this.chunks[(x + y * this.xChunks) * this.zChunks + z].setDirty();
            }
         }
      }

   }

   public void tileChanged(int x, int y, int z) {
      this.setDirty(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);
   }

   public void lightColumnChanged(int x, int z, int y0, int y1) {
      this.setDirty(x - 1, y0 - 1, z - 1, x + 1, y1 + 1, z + 1);
   }

   public void toggleDrawDistance() {
      this.drawDistance = (this.drawDistance + 1) % 4;
   }

   public void cull(Frustum frustum) {
      for(int i = 0; i < this.chunks.length; ++i) {
         this.chunks[i].visible = frustum.isVisible(this.chunks[i].aabb);
      }

   }
}
