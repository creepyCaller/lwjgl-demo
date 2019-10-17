package com.mojang.minecraft.comm;

import com.mojang.minecraft.comm.SocketConnection;

public interface ServerListener {

   void clientConnected(SocketConnection var1);

   void clientException(SocketConnection var1, Exception var2);
}
