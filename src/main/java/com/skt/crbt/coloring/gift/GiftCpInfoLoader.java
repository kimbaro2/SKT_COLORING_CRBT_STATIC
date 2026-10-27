package com.skt.crbt.coloring.gift;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class GiftCpInfoLoader {

    private static final String WAR_CPINFO = "/WEB-INF/legacy/gift/cp.info";
    private static final String CP_QUERY =
            "select distinct CP_ID, CP_NAME from cp_id order by cp_id";

    private final ServletContext servletContext;
    private final JdbcTemplate copysts;

    public GiftCpInfoLoader(ServletContext servletContext,
                            @Qualifier("copystsJdbcTemplate") JdbcTemplate copysts) {
        this.servletContext = servletContext;
        this.copysts = copysts;
    }

    public Map<String, String> load() {
        Map<String, String> names = loadFromDatabase();
        if (!names.isEmpty()) {
            return names;
        }
        return loadFromFile();
    }

    private Map<String, String> loadFromDatabase() {
        Map<String, String> names = new LinkedHashMap<String, String>();
        try {
            List<Map<String, Object>> rows = copysts.queryForList(CP_QUERY);
            for (Map<String, Object> row : rows) {
                Object id = row.get("CP_ID");
                Object name = row.get("CP_NAME");
                if (id == null || name == null) {
                    continue;
                }
                names.put(String.valueOf(id).trim(), String.valueOf(name).trim());
            }
        } catch (DataAccessException ignored) {
            names.clear();
        }
        return names;
    }

    private Map<String, String> loadFromFile() {
        Map<String, String> names = new LinkedHashMap<String, String>();
        InputStream in = servletContext.getResourceAsStream(WAR_CPINFO);
        if (in != null) {
            parseStream(names, in);
            return names;
        }
        String fallback = servletContext.getRealPath(WAR_CPINFO);
        if (fallback != null) {
            File file = new File(fallback);
            if (file.isFile()) {
                parseFile(names, file);
            }
        }
        return names;
    }

    private void parseFile(Map<String, String> names, File file) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "EUC-KR"));
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
            reader = new BufferedReader(new InputStreamReader(in, "EUC-KR"));
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
                names.put(parts[0].trim(), parts[1].trim());
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
