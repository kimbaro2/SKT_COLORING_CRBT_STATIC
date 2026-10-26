package com.skt.crbt.coloring.rank;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** PHP minwon DEBUG file. A missing directory must not fail the request. */
@Component
public class MinwonLog {

    public static final String DEFAULT_DIR = "/data/crbt/www/static/admin_new/minwon/log";

    private static final Logger log = LoggerFactory.getLogger(MinwonLog.class);
    private static volatile String dir = DEFAULT_DIR;

    public MinwonLog(@Value("${minwon.log.dir:" + DEFAULT_DIR + "}") String configured) {
        if (configured != null && configured.trim().length() > 0) {
            dir = configured.trim();
        }
        if (ensureDir(dir)) {
            log.info("minwon log dir={}", dir);
        }
    }

    private static boolean ensureDir(String path) {
        File folder = new File(path);
        if (folder.isDirectory()) {
            return true;
        }
        if (folder.mkdirs() && folder.isDirectory()) {
            log.info("minwon log dir created: {}", path);
            return true;
        }
        log.warn("minwon log dir create failed: {}", path);
        return false;
    }

    public static void write(String message) {
        OutputStreamWriter writer = null;
        try {
            File folder = new File(dir);
            if (!ensureDir(dir)) {
                return;
            }
            String name = "sktlevel_" + new SimpleDateFormat("yyyyMM").format(new Date()) + ".log";
            writer = new OutputStreamWriter(new FileOutputStream(new File(folder, name), true), "EUC-KR");
            writer.write("[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "] "
                    + (message == null ? "" : message) + "\n");
        } catch (IOException ignored) {
            // keep the page working when the log directory cannot be created
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignored) {
                    // ignore
                }
            }
        }
    }
}
