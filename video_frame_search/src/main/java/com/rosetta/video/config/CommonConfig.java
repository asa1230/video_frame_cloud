package com.rosetta.video.config;

import org.opencv.core.Core;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {

    static {

        String javaLibraryPath = System.getProperty("java.library.path");
        System.out.println("java.library.path===" + javaLibraryPath);

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }



}
