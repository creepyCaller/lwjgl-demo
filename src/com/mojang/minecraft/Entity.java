package com.mojang.minecraft;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.phys.AABB;
import java.util.ArrayList;

public class Entity {

   protected Level level;
   public float xo;
   public float yo;
   public float zo;
   public float x;
   public float y;
   public float z;
   public float xd;
   public float yd;
   public float zd;
   public float yRot;
   public float xRot;
   public AABB bb;
   public boolean onGround = false;
   public boolean horizontalCollision = false;
   public boolean removed = false;
   protected float heightOffset = 0.0F;
   protected float bbWidth = 0.6F;
   protected float bbHeight = 1.8F;


   public Entity(Level level) {
      this.level = level;
      this.resetPos();
   }

   protected void resetPos() {
      float x = (float)Math.random() * (float)(this.level.width - 2) + 1.0F;
      float y = (float)(this.level.depth + 10);
      float z = (float)Math.random() * (float)(this.level.height - 2) + 1.0F;
      this.setPos(x, y, z);
   }

   public void remove() {
      this.removed = true;
   }

   protected void setSize(float w, float h) {
      this.bbWidth = w;
      this.bbHeight = h;
   }

   protected void setPos(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
      float w = this.bbWidth / 2.0F;
      float h = this.bbHeight / 2.0F;
      this.bb = new AABB(x - w, y - h, z - w, x + w, y + h, z + w);
   }

   public void turn(float xo, float yo) {
      this.yRot = (float)((double)this.yRot + (double)xo * 0.15D);
      this.xRot = (float)((double)this.xRot - (double)yo * 0.15D);
      if(this.xRot < -90.0F) {
         this.xRot = -90.0F;
      }

      if(this.xRot > 90.0F) {
         this.xRot = 90.0F;
      }

   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
   }

   public boolean isFree(float xa, float ya, float za) {
      AABB box = this.bb.cloneMove(xa, ya, za);
      ArrayList aABBs = this.level.getCubes(box);
      return aABBs.size() > 0?false:!this.level.containsAnyLiquid(box);
   }

   public void move(float xa, float ya, float za) {
      float xaOrg = xa;
      float yaOrg = ya;
      float zaOrg = za;
      ArrayList aABBs = this.level.getCubes(this.bb.expand(xa, ya, za));

      int i;
      for(i = 0; i < aABBs.size(); ++i) {
         ya = ((AABB)aABBs.get(i)).clipYCollide(this.bb, ya);
      }

      this.bb.move(0.0F, ya, 0.0F);

      for(i = 0; i < aABBs.size(); ++i) {
         xa = ((AABB)aABBs.get(i)).clipXCollide(this.bb, xa);
      }

      this.bb.move(xa, 0.0F, 0.0F);

      for(i = 0; i < aABBs.size(); ++i) {
         za = ((AABB)aABBs.get(i)).clipZCollide(this.bb, za);
      }

      this.bb.move(0.0F, 0.0F, za);
      this.horizontalCollision = xaOrg != xa || zaOrg != za;
      this.onGround = yaOrg != ya && yaOrg < 0.0F;
      if(xaOrg != xa) {
         this.xd = 0.0F;
      }

      if(yaOrg != ya) {
         this.yd = 0.0F;
      }

      if(zaOrg != za) {
         this.zd = 0.0F;
      }

      this.x = (this.bb.x0 + this.bb.x1) / 2.0F;
      this.y = this.bb.y0 + this.heightOffset;
      this.z = (this.bb.z0 + this.bb.z1) / 2.0F;
   }

   public boolean isInWater() {
      return this.level.containsLiquid(this.bb.grow(0.0F, -0.4F, 0.0F), 1);
   }

   public boolean isInLava() {
      return this.level.containsLiquid(this.bb, 2);
   }

   public void moveRelative(float xa, float za, float speed) {
      float dist = xa * xa + za * za;
      if(dist >= 0.01F) {
         dist = speed / (float)Math.sqrt((double)dist);
         xa *= dist;
         za *= dist;
         float sin = (float)Math.sin((double)this.yRot * 3.141592653589793D / 180.0D);
         float cos = (float)Math.cos((double)this.yRot * 3.141592653589793D / 180.0D);
         this.xd += xa * cos - za * sin;
         this.zd += za * cos + xa * sin;
      }
   }

   public boolean isLit() {
      int xTile = (int)this.x;
      int yTile = (int)this.y;
      int zTile = (int)this.z;
      return this.level.isLit(xTile, yTile, zTile);
   }

   public void render(float a) {}
}
