package com.mojang.rubydung;

import com.mojang.rubydung.HitResult;
import com.mojang.rubydung.Player;
import com.mojang.rubydung.Timer;
import com.mojang.rubydung.level.Chunk;
import com.mojang.rubydung.level.Level;
import com.mojang.rubydung.level.LevelRenderer;
import java.awt.Component;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import javax.swing.JOptionPane;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class RubyDung implements Runnable {

   private static final boolean FULLSCREEN_MODE = false;
   private int width; // 窗口宽度
   private int height; // 窗口高度
   private FloatBuffer fogColor = BufferUtils.createFloatBuffer(4); // 用于承载四个浮点变量的缓冲区,当年没有泛型只能用这种笨比方法。fogColor的意思可能是"雾的颜色"？
   private Timer timer = new Timer(60.0F); // 计时器，一秒钟60tick
   private Level level;
   private LevelRenderer levelRenderer;
   private Player player;
   private IntBuffer viewportBuffer = BufferUtils.createIntBuffer(16); // 用于承载四个整形变量的缓冲区
   private IntBuffer selectBuffer = BufferUtils.createIntBuffer(2000); // 用于承载四个整形变量的缓冲区
   private HitResult hitResult = null;

   /**
    * 初始化游戏
    * @throws LWJGLException 当LWJGL加载失败时
    */
   private void init() throws LWJGLException {
      int col = 920330; // ???不明白为什么会事920330
      float fr = 0.5F;
      float fg = 0.8F;
      float fb = 1.0F;
      // 向缓冲器推入四个浮点数，它们的值分别是：0.05490196，0.043137256，0.039215688，1.0
      this.fogColor.put(new float[] {(float)(col >> 16 & 255) / 255.0F, (float)(col >> 8 & 255) / 255.0F, (float)(col & 255) / 255.0F, 1.0F});
      // 没搞懂这个flip()的意义
      this.fogColor.flip();
      // Display类，封装了给定显示模式的属性，用来设置显示属性，是opengl库的东西
      // 通过public构造函数构造的DisplayMode只能用于指定窗口模式下Display的尺寸。
      // 要获取全屏模式可用的DisplayMode，请使用Display.getAvailableDisplayModes（）
      Display.setDisplayMode(new DisplayMode(1280, 720));
      Display.create(); // Create the OpenGL context.
      Keyboard.create(); // "Create" the keyboard.
      Mouse.create(); // "Create" the mouses.
      // 以下获取窗口的宽度、高度
      this.width = Display.getDisplayMode().getWidth();
      this.height = Display.getDisplayMode().getHeight();
      // 设置GL11的一些配置？
      GL11.glEnable(3553);
      GL11.glShadeModel(7425);
      GL11.glClearColor(fr, fg, fb, 0.0F);
      GL11.glClearDepth(1.0D);
      GL11.glEnable(2929);
      GL11.glDepthFunc(515);
      GL11.glMatrixMode(5889);
      GL11.glLoadIdentity();
      GL11.glMatrixMode(5888);
      this.level = new Level(64, 64, 64); // 创建新level，设置其长宽高，原来的值为256, 256, 64
      this.levelRenderer = new LevelRenderer(this.level); // 实例化渲染器并把level实例传入
      this.player = new Player(this.level); // 创建玩家对象
      Mouse.setGrabbed(true); // 让鼠标抓住光标（也就是游戏正中间那个十字架一样的玩意），然后隐藏鼠标
   }

   /**
    * 游戏主循环结束时或捕获到Exception时调用
    */
   private void destroy() {
      this.level.save();
      Mouse.destroy();
      Keyboard.destroy();
      Display.destroy();
   }

   /**
    * 游戏主循环，继承Runnable，由RubyDung方法的main()函数实例化其本身并调用run()方法执行
    */
   public void run() {
      try {
         this.init(); // 初始化类属性
      } catch (Exception e) {
         JOptionPane.showMessageDialog(null, e.toString(), "Failed to start RubyDung", JOptionPane.ERROR_MESSAGE);
         System.exit(0);
      }
      long lastTime = System.currentTimeMillis(); // 用来记录时间刻
      int frames = 0; // 声明、初始化用于记录每秒帧率的变量
      try {
         while(!Keyboard.isKeyDown(1) && !Display.isCloseRequested()) {
            // 当...和...的时候

            this.timer.advanceTime(); // 令计时器...

//            for(int e = 0; e < this.timer.ticks; ++e) {
//               this.tick(); // tick该怎么翻译，时间刻？
//            }
            int e = 0;
            while(e++ < this.timer.ticks) {
               this.tick(); // tick该怎么翻译，时间刻？
            }

            this.render(this.timer.a); // v.使成为; 使变得; 使处于某状态; 给予; 提供; 回报; 递交; 呈献; 提交;

            ++frames; // 这个变量实际上是表示FPS，每秒框架刷新次数

            while(System.currentTimeMillis() >= lastTime + 1000L) {
               // 这里表示如果时间过了1秒的话该输出帧率和区块更新数量
               System.out.println(frames + " fps, " + Chunk.updates);
               Chunk.updates = 0; // 清零用于记录区块更新数量的变量
               lastTime += 1000L; // 令时间再加上1000毫秒以此判断下一次过了一秒的时候
               frames = 0; // 清零用于记录每秒帧率的变量
            }

         }
      } catch (Exception e) {
         e.printStackTrace(); // 捕获异常
      } finally {
         this.destroy(); // 最终将调用毁灭方法
      }

   }

   public void tick() {
      this.player.tick();
   }

   private void moveCameraToPlayer(float a) {
      GL11.glTranslatef(0.0F, 0.0F, -0.3F);
      GL11.glRotatef(this.player.xRot, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(this.player.yRot, 0.0F, 1.0F, 0.0F);
      float x = this.player.xo + (this.player.x - this.player.xo) * a;
      float y = this.player.yo + (this.player.y - this.player.yo) * a;
      float z = this.player.zo + (this.player.z - this.player.zo) * a;
      GL11.glTranslatef(-x, -y, -z);
   }

   private void setupCamera(float a) {
      GL11.glMatrixMode(5889);
      GL11.glLoadIdentity();
      GLU.gluPerspective(70.0F, (float)this.width / (float)this.height, 0.05F, 1000.0F);
      GL11.glMatrixMode(5888);
      GL11.glLoadIdentity();
      this.moveCameraToPlayer(a);
   }

   private void setupPickCamera(float a, int x, int y) {
      GL11.glMatrixMode(5889);
      GL11.glLoadIdentity();
      this.viewportBuffer.clear();
      GL11.glGetInteger(2978, this.viewportBuffer);
      this.viewportBuffer.flip();
      this.viewportBuffer.limit(16);
      GLU.gluPickMatrix((float)x, (float)y, 5.0F, 5.0F, this.viewportBuffer);
      GLU.gluPerspective(70.0F, (float)this.width / (float)this.height, 0.05F, 1000.0F);
      GL11.glMatrixMode(5888);
      GL11.glLoadIdentity();
      this.moveCameraToPlayer(a);
   }

   private void pick(float a) {
      this.selectBuffer.clear();
      GL11.glSelectBuffer(this.selectBuffer);
      GL11.glRenderMode(7170);
      this.setupPickCamera(a, this.width / 2, this.height / 2);
      this.levelRenderer.pick(this.player);
      int hits = GL11.glRenderMode(7168);
      this.selectBuffer.flip();
      this.selectBuffer.limit(this.selectBuffer.capacity());
      long closest = 0L;
      int[] names = new int[10];
      int hitNameCount = 0;

      for(int i = 0; i < hits; ++i) {
         int nameCount = this.selectBuffer.get();
         long minZ = (long)this.selectBuffer.get();
         this.selectBuffer.get();
         int j;
         if(minZ >= closest && i != 0) {
            for(j = 0; j < nameCount; ++j) {
               this.selectBuffer.get();
            }
         } else {
            closest = minZ;
            hitNameCount = nameCount;

            for(j = 0; j < nameCount; ++j) {
               names[j] = this.selectBuffer.get();
            }
         }
      }

      if(hitNameCount > 0) {
         this.hitResult = new HitResult(names[0], names[1], names[2], names[3], names[4]);
      } else {
         this.hitResult = null;
      }

   }

   public void render(float a) {
      float xo = (float)Mouse.getDX();
      float yo = (float)Mouse.getDY();
      this.player.turn(xo, yo);
      this.pick(a);

      while(Mouse.next()) {
         if(Mouse.getEventButton() == 1 && Mouse.getEventButtonState() && this.hitResult != null) {
            this.level.setTile(this.hitResult.x, this.hitResult.y, this.hitResult.z, 0);
         }

         if(Mouse.getEventButton() == 0 && Mouse.getEventButtonState() && this.hitResult != null) {
            int x = this.hitResult.x;
            int y = this.hitResult.y;
            int z = this.hitResult.z;
            if(this.hitResult.f == 0) {
               --y;
            }

            if(this.hitResult.f == 1) {
               ++y;
            }

            if(this.hitResult.f == 2) {
               --z;
            }

            if(this.hitResult.f == 3) {
               ++z;
            }

            if(this.hitResult.f == 4) {
               --x;
            }

            if(this.hitResult.f == 5) {
               ++x;
            }
            this.level.setTile(x, y, z, 1);
         }
      }

      while(Keyboard.next()) {
         if(Keyboard.getEventKey() == 28 && Keyboard.getEventKeyState()) {
            this.level.save();
         }
      }

      GL11.glClear(16640);
      this.setupCamera(a);
      GL11.glEnable(2884);
      GL11.glEnable(2912);
      GL11.glFogi(2917, 2048);
      GL11.glFogf(2914, 0.2F);
      GL11.glFog(2918, this.fogColor);
      GL11.glDisable(2912);
      this.levelRenderer.render(this.player, 0);
      GL11.glEnable(2912);
      this.levelRenderer.render(this.player, 1);
      GL11.glDisable(3553);
      if(this.hitResult != null) {
         this.levelRenderer.renderHit(this.hitResult);
      }

      GL11.glDisable(2912);
      Display.update();
   }

   public static void checkError() {
      int e = GL11.glGetError();
      if(e != 0) {
         throw new IllegalStateException(GLU.gluErrorString(e));
      }
   }

   public static void main(String[] args) throws LWJGLException {
      RubyDung rubydung = new RubyDung();
      rubydung.run();
   }
}
