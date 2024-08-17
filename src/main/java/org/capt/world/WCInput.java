package org.capt.world;

import java.io.InputStream;

public record WCInput(String fileName, InputStream inputStream) {
    public WCInput {
        if (fileName.isEmpty() || fileName.equals("-")) {
            if (inputStream != System.in) {
                throw new RuntimeException("InputStream for fileName: [" + fileName + "] should be [System.in]");
            }
        }
    }
}
