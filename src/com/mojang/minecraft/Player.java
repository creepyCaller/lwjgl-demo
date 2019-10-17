package com.mojang.minecraft;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.level.Level;

public class Player extends Entity {

   public static final int KEY_UP = 0;
   public static final int KEY_DOWN = 1;
   public static final int KEY_LEFT = 2;
   public static final int KEY_RIGHT = 3;
   public static final int KEY_JUMP = 4;
   private boolean[] keys = new boolean[10];


   public Player(Level level) {
      super(level);
      this.heightOffset = 1.62F;
   }

   public void setKey(int key, boolean state) {
      byte id = -1;
      if(key == 200 || key == 17) {
         id = 0;
      }

      if(key == 208 || key == 31) {
         id = 1;
      }

      if(key == 203 || key == 30) {
         id = 2;
      }

      if(key == 205 || key == 32) {
         id = 3;
      }

      if(key == 57 || key == 219) {
         id = 4;
      }

      if(id >= 0) {
         this.keys[id] = state;
      }

   }

   public void releaseAllKeys() {
      for(int i = 0; i < 10; ++i) {
         this.keys[i] = false;
      }

   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      float xa = 0.0F;
      float ya = 0.0F;
      boolean inWater = this.isInWater();
      boolean inLava = this.isInLava();
      if(this.keys[0]) {
         --ya;
      }

      if(this.keys[1]) {
         ++ya;
      }

      if(this.keys[2]) {
         --xa;
      }

      if(this.keys[3]) {
         ++xa;
      }

      if(this.keys[4]) {
         if(inWater) {
            this.yd += 0.04F;
         } else if(inLava) {
            this.yd += 0.04F;
         } else if(this.onGround) {
            this.yd = 0.42F;
            this.keys[4] = false;
         }
      }

      float yo;
      if(inWater) {
         yo = this.y;
         this.moveRelative(xa, ya, 0.02F);
         this.move(this.xd, this.yd, this.zd);
         this.xd *= 0.8F;
         this.yd *= 0.8F;
         this.zd *= 0.8F;
         this.yd = (float)((double)this.yd - 0.02D);
         if(this.horizontalCollision && this.isFree(this.xd, this.yd + 0.6F - this.y + yo, this.zd)) {
            this.yd = 0.3F;
         }
      } else if(inLava) {
         yo = this.y;
         this.moveRelative(xa, ya, 0.02F);
         this.move(this.xd, this.yd, this.zd);
         this.xd *= 0.5F;
         this.yd *= 0.5F;
         this.zd *= 0.5F;
         this.yd = (float)((double)this.yd - 0.02D);
         if(this.horizontalCollision && this.isFree(this.xd, this.yd + 0.6F - this.y + yo, this.zd)) {
            this.yd = 0.3F;
         }
      } else {
         this.moveRelative(xa, ya, this.onGround?0.1F:0.02F);
         this.move(this.xd, this.yd, this.zd);
         this.xd *= 0.91F;
         this.yd *= 0.98F;
         this.zd *= 0.91F;
         this.yd = (float)((double)this.yd - 0.08D);
         if(this.onGround) {
            this.xd *= 0.6F;
            this.zd *= 0.6F;
         }
      }

   }
}
