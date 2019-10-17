package com.mojang.rubydung;

import org.lwjgl.LWJGLException;

public class Starter {

    public static void main(String[] args) {
        try {
            RubyDung.main(args);
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
    }

}
