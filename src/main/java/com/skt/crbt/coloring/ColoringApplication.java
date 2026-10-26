package com.skt.crbt.coloring;

import java.io.File;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.skt.crbt.coloring.rank.MinwonLog;

@SpringBootApplication(exclude = MybatisAutoConfiguration.class)
@MapperScan("com.skt.crbt.coloring.rank")
public class ColoringApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        System.setProperty("file.encoding", "EUC-KR");
        System.setProperty("sun.jnu.encoding", "EUC-KR");
        System.setProperty("user.language", "ko");
        System.setProperty("user.country", "KR");
        String dir = logDir(args);
        new File(dir).mkdirs();
        System.setProperty("minwon.log.dir", dir);
        System.setProperty("logging.file.name", new File(dir, "coloring.log").getPath());
        SpringApplication.run(ColoringApplication.class, args);
    }

    static String logDir(String[] args) {
        String dir = MinwonLog.DEFAULT_DIR;
        if (args == null) {
            return dir;
        }
        String prefix = "--minwon.log.dir=";
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg == null) {
                continue;
            }
            if (arg.startsWith(prefix)) {
                String value = arg.substring(prefix.length()).trim();
                if (value.length() > 0) {
                    dir = value;
                }
            } else if ("--minwon.log.dir".equals(arg) && i + 1 < args.length && args[i + 1] != null) {
                String value = args[i + 1].trim();
                if (value.length() > 0) {
                    dir = value;
                }
            }
        }
        return dir;
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ColoringApplication.class);
    }
}
