package com.mojang.minecraft;

import org.lwjgl.LWJGLException;

public class Starter {

    public static void main(String[] args) {
        try {
//            Minecraft.main(new String[] {"-fullscreen"});
            Minecraft.main(new String[] {});
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
    }

}
