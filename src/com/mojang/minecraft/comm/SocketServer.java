package com.mojang.minecraft.comm;

import com.mojang.minecraft.comm.ServerListener;
import com.mojang.minecraft.comm.SocketConnection;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

public class SocketServer {

   private ServerSocketChannel ssc;
   private ServerListener serverListener;
   private List connections = new LinkedList();


   public SocketServer(byte[] ips, int port, ServerListener serverListener) throws IOException {
      this.serverListener = serverListener;
      InetAddress hostip = InetAddress.getByAddress(ips);
      this.ssc = ServerSocketChannel.open();
      this.ssc.socket().bind(new InetSocketAddress(hostip, port));
      this.ssc.configureBlocking(false);
   }

   public void tick() throws IOException {
      SocketChannel socketChannel;
      while((socketChannel = this.ssc.accept()) != null) {
         try {
            socketChannel.configureBlocking(false);
            SocketConnection i = new SocketConnection(socketChannel);
            this.connections.add(i);
            this.serverListener.clientConnected(i);
         } catch (IOException var6) {
            socketChannel.close();
            throw var6;
         }
      }

      for(int var7 = 0; var7 < this.connections.size(); ++var7) {
         SocketConnection socketConnection = (SocketConnection)this.connections.get(var7);
         if(!socketConnection.isConnected()) {
            socketConnection.disconnect();
            this.connections.remove(var7--);
         } else {
            try {
               socketConnection.tick();
            } catch (Exception var5) {
               socketConnection.disconnect();
               this.serverListener.clientException(socketConnection, var5);
            }
         }
      }

   }
}
