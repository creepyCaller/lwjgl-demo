package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.tile.LiquidTile;
import com.mojang.minecraft.level.tile.Tile;
import java.util.Random;

public class CalmLiquidTile extends LiquidTile {

   protected CalmLiquidTile(int id, int liquidType) {
      super(id, liquidType);
      this.tileId = id - 1;
      this.calmTileId = id;
      this.setTicking(false);
   }

   public void tick(Level level, int x, int y, int z, Random random) {}

   public void neighborChanged(Level level, int x, int y, int z, int type) {
      boolean hasAirNeighbor = false;
      if(level.getTile(x - 1, y, z) == 0) {
         hasAirNeighbor = true;
      }

      if(level.getTile(x + 1, y, z) == 0) {
         hasAirNeighbor = true;
      }

      if(level.getTile(x, y, z - 1) == 0) {
         hasAirNeighbor = true;
      }

      if(level.getTile(x, y, z + 1) == 0) {
         hasAirNeighbor = true;
      }

      if(level.getTile(x, y - 1, z) == 0) {
         hasAirNeighbor = true;
      }

      if(hasAirNeighbor) {
         level.setTileNoUpdate(x, y, z, this.tileId);
      }

      if(this.liquidType == 1 && type == Tile.lava.id) {
         level.setTileNoUpdate(x, y, z, Tile.rock.id);
      }

      if(this.liquidType == 2 && type == Tile.water.id) {
         level.setTileNoUpdate(x, y, z, Tile.rock.id);
      }

   }
}
