package com.mojang.minecraft.level;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.LevelLoaderListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class LevelIO {

   private static final int MAGIC_NUMBER = 656127880;
   private static final int CURRENT_VERSION = 1;
   private LevelLoaderListener levelLoaderListener;
   public String error = null;


   public LevelIO(LevelLoaderListener levelLoaderListener) {
      this.levelLoaderListener = levelLoaderListener;
   }

   public boolean load(Level level, InputStream in) {
      this.levelLoaderListener.beginLevelLoading("Loading level");
      this.levelLoaderListener.levelLoadUpdate("Reading..");

      try {
         DataInputStream e = new DataInputStream(new GZIPInputStream(in));
         int magic = e.readInt();
         if(magic != 656127880) {
            this.error = "Bad level file format";
            return false;
         } else {
            byte version = e.readByte();
            if(version > 1) {
               this.error = "Bad level file format";
               return false;
            } else {
               String name = e.readUTF();
               String creator = e.readUTF();
               long createTime = e.readLong();
               short width = e.readShort();
               short height = e.readShort();
               short depth = e.readShort();
               byte[] blocks = new byte[width * height * depth];
               e.readFully(blocks);
               e.close();
               level.setData(width, depth, height, blocks);
               level.name = name;
               level.creator = creator;
               level.createTime = createTime;
               return true;
            }
         }
      } catch (Exception var14) {
         var14.printStackTrace();
         this.error = "Failed to load level: " + var14.toString();
         return false;
      }
   }

   public boolean loadLegacy(Level level, InputStream in) {
      this.levelLoaderListener.beginLevelLoading("Loading level");
      this.levelLoaderListener.levelLoadUpdate("Reading..");

      try {
         DataInputStream e = new DataInputStream(new GZIPInputStream(in));
         String name = "--";
         String creator = "unknown";
         long createTime = 0L;
         short width = 256;
         short height = 256;
         byte depth = 64;
         byte[] blocks = new byte[width * height * depth];
         e.readFully(blocks);
         e.close();
         level.setData(width, depth, height, blocks);
         level.name = name;
         level.creator = creator;
         level.createTime = createTime;
         return true;
      } catch (Exception var12) {
         var12.printStackTrace();
         this.error = "Failed to load level: " + var12.toString();
         return false;
      }
   }

   public void save(Level level, OutputStream out) {
      try {
         DataOutputStream e = new DataOutputStream(new GZIPOutputStream(out));
         e.writeInt(656127880);
         e.writeByte(1);
         e.writeUTF(level.name);
         e.writeUTF(level.creator);
         e.writeLong(level.createTime);
         e.writeShort(level.width);
         e.writeShort(level.height);
         e.writeShort(level.depth);
         e.write(level.blocks);
         e.close();
      } catch (Exception var4) {
         var4.printStackTrace();
      }

   }
}
