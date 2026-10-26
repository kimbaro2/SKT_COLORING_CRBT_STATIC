package com.skt.crbt.coloring.rank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.ServletContext;

import org.springframework.stereotype.Component;

import com.skt.crbt.coloring.common.DbConstants;

@Component
public class CpInfoLoader {

    private final ServletContext servletContext;

    public CpInfoLoader(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public Map<String, String> load() {
        Map<String, String> names = new LinkedHashMap<String, String>();
        File file = new File(DbConstants.RANK_CPINFO);
        if (!file.isFile()) {
            String fallback = servletContext.getRealPath("/WEB-INF/legacy/admin_new/cpinfo.ini");
            if (fallback != null) {
                file = new File(fallback);
            }
        }
        if (!file.isFile()) {
            return names;
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "EUC-KR"));
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.length() == 0 || trimmed.charAt(0) == '#') {
                    continue;
                }
                String[] parts = trimmed.split("\\s+", 2);
                if (parts.length == 2) {
                    names.put(parts[0], parts[1]);
                }
            }
        } catch (Exception ignored) {
            return names;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ignored) {
                    // ignore
                }
            }
        }
        return names;
    }
}
