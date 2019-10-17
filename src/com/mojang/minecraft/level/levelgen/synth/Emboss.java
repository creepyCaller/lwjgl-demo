package com.mojang.minecraft.level.levelgen.synth;

import com.mojang.minecraft.level.levelgen.synth.Synth;

public class Emboss extends Synth {

   private Synth synth;


   public Emboss(Synth synth) {
      this.synth = synth;
   }

   public double getValue(double x, double y) {
      return this.synth.getValue(x, y) - this.synth.getValue(x + 1.0D, y + 1.0D);
   }
}
