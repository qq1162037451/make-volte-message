package com.jhhc.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author xiaojiang
 * @date 2021/1/14 10:15
 */
public class IOUtils {
    public static void closeQuietly(Closeable... closeables) {
        if (closeables != null) {
            Closeable[] arr$ = closeables;
            int len$ = closeables.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                Closeable closeable = arr$[i$];
                closeQuietly(closeable);
            }

        }
    }
    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException var2) {
            ;
        }

    }
}
