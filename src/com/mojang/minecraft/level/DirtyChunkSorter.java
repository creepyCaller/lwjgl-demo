package com.mojang.minecraft.level;

import com.mojang.minecraft.Player;
import com.mojang.minecraft.level.Chunk;
import java.util.Comparator;

public class DirtyChunkSorter implements Comparator<Chunk> {

   private Player player;


   public DirtyChunkSorter(Player player) {
      this.player = player;
   }

   @Override
   public int compare(Chunk c0, Chunk c1) {
      boolean i0 = c0.visible;
      boolean i1 = c1.visible;
      return i0 && !i1?-1:(i1 && !i0?1:(c0.distanceToSqr(this.player) < c1.distanceToSqr(this.player)?-1:1));
   }

}
