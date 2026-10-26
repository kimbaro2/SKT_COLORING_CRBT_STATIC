package com.skt.crbt.coloring.common;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class CookieSupport {

    private CookieSupport() {
    }

    public static void add(HttpServletResponse response, String name, String value, String path) {
        Cookie cookie = new Cookie(name, value == null ? "" : value);
        cookie.setPath(path);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    public static String get(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static boolean has(HttpServletRequest request, String name) {
        String value = get(request, name);
        return value != null && value.length() > 0;
    }

    public static void clear(HttpServletResponse response, String name, String path) {
        expire(response, name, path, true);
        expire(response, name, path, false);
    }

    public static void clearAll(HttpServletResponse response) {
        String[] names = {
                CookieNames.ADMIN_ID, CookieNames.ADMIN_AC,
                CookieNames.MASTER_ID, CookieNames.MASTER_LV,
                CookieNames.CP_ID, CookieNames.CP_AC,
                CookieNames.SRC_ID, CookieNames.SRC_AC,
                CookieNames.GIFT_ID, "JSESSIONID"
        };
        String[] paths = {
                "/", CookieNames.PATH_CP, CookieNames.PATH_ASP, CookieNames.PATH_RANK, CookieNames.PATH_GIFT
        };
        for (int i = 0; i < names.length; i++) {
            for (int j = 0; j < paths.length; j++) {
                clear(response, names[i], paths[j]);
            }
        }
    }

    private static void expire(HttpServletResponse response, String name, String path, boolean httpOnly) {
        Cookie cookie = new Cookie(name, "");
        cookie.setPath(path);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(httpOnly);
        response.addCookie(cookie);
    }
}
