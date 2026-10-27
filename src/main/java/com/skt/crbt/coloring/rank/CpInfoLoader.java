package com.skt.crbt.coloring.rank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.ServletContext;

import org.springframework.stereotype.Component;

import com.skt.crbt.coloring.common.DbConstants;

@Component
public class CpInfoLoader {

    /** Legacy cpinfo.ini CPName column encoding (ko_KR.eucKR). */
    public static final Charset CP_NAME_CHARSET = Charset.forName("EUC-KR");

    private static final String WAR_CPINFO = "/WEB-INF/legacy/admin_new/cpinfo.ini";

    private final ServletContext servletContext;

    public CpInfoLoader(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public Map<String, String> load() {
        Map<String, String> names = new LinkedHashMap<String, String>();
        File file = new File(DbConstants.RANK_CPINFO);
        if (file.isFile()) {
            parseFile(names, file);
            return names;
        }
        String fallback = servletContext.getRealPath(WAR_CPINFO);
        if (fallback != null) {
            file = new File(fallback);
            if (file.isFile()) {
                parseFile(names, file);
                return names;
            }
        }
        InputStream in = servletContext.getResourceAsStream(WAR_CPINFO);
        if (in != null) {
            parseStream(names, in);
        }
        return names;
    }

    public String resolveName(Map<String, String> names, Object cpid) {
        String key = cpIdKey(cpid);
        if (key.length() == 0) {
            return "";
        }
        String name = names.get(key);
        return name != null ? name : key;
    }

    public static String cpIdKey(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof Number) {
            return String.valueOf(((Number) value).longValue());
        }
        String text = String.valueOf(value).trim();
        if (text.length() == 0) {
            return "";
        }
        int dot = text.indexOf('.');
        if (dot >= 0) {
            try {
                return String.valueOf((long) Double.parseDouble(text));
            } catch (NumberFormatException ignored) {
                return text;
            }
        }
        return text;
    }

    private void parseFile(Map<String, String> names, File file) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), CP_NAME_CHARSET));
            parseReader(names, reader);
        } catch (Exception ignored) {
            names.clear();
        } finally {
            close(reader);
        }
    }

    private void parseStream(Map<String, String> names, InputStream in) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in, CP_NAME_CHARSET));
            parseReader(names, reader);
        } catch (Exception ignored) {
            names.clear();
        } finally {
            close(reader);
        }
    }

    private void parseReader(Map<String, String> names, BufferedReader reader) throws java.io.IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            String trimmed = line.trim();
            if (trimmed.length() == 0 || trimmed.charAt(0) == '#') {
                continue;
            }
            String[] parts = trimmed.split("\\s+", 2);
            if (parts.length == 2) {
                String id = cpIdKey(parts[0]);
                String name = parts[1].trim();
                if (id.length() > 0 && name.length() > 0) {
                    names.put(id, name);
                }
            }
        }
    }

    private void close(BufferedReader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (Exception ignored) {
                // ignore
            }
        }
    }
}
