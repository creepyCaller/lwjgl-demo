package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.tile.Tile;
import java.util.Random;

public class GrassTile extends Tile {

   protected GrassTile(int id) {
      super(id);
      this.tex = 3;
      this.setTicking(true);
   }

   protected int getTexture(int face) {
      return face == 1?0:(face == 0?2:3);
   }

   public void tick(Level level, int x, int y, int z, Random random) {
      if(random.nextInt(4) == 0) {
         if(!level.isLit(x, y + 1, z)) {
            level.setTile(x, y, z, Tile.dirt.id);
         } else {
            for(int i = 0; i < 4; ++i) {
               int xt = x + random.nextInt(3) - 1;
               int yt = y + random.nextInt(5) - 3;
               int zt = z + random.nextInt(3) - 1;
               if(level.getTile(xt, yt, zt) == Tile.dirt.id && level.isLit(xt, yt + 1, zt)) {
                  level.setTile(xt, yt, zt, Tile.grass.id);
               }
            }
         }

      }
   }
}
