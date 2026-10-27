package com.skt.crbt.coloring.common;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import jakarta.servlet.http.HttpServletResponse;

public final class HtmlWriter {

    public static final String INLINE_STYLE = "<style type=\"text/css\">"
            + "body,td,form{color:#000;font-family:verdana,arial,helvetica,sans-serif;font-size:9pt;}"
            + "a:link{text-decoration:none;color:darkblue;}a:visited{text-decoration:none;color:darkblue;}"
            + "a:hover{color:blue;}</style>";

    public static final String CSS_LINK = "<link rel=\"StyleSheet\" type=\"text/css\" href=\"default.css\">"
            + INLINE_STYLE;

    private HtmlWriter() {
    }

    public static void write(HttpServletResponse response, String html) throws IOException {
        write(response, html, "text/html; charset=euc-kr", null);
    }

    public static void writeExcel(HttpServletResponse response, String html, String filename) throws IOException {
        write(response, html, "application/vnd.ms-excel; charset=euc-kr", "attachment; filename=" + filename);
    }

    public static void send(HttpServletResponse response, String html, boolean excel, String filename) throws IOException {
        if (excel) {
            writeExcel(response, html, filename);
            return;
        }
        write(response, html);
    }

    private static void write(HttpServletResponse response, String html, String contentType, String disposition)
            throws IOException {
        response.setCharacterEncoding("EUC-KR");
        response.setContentType(contentType);
        if (disposition != null) {
            response.setHeader("Content-Disposition", disposition);
        }
        PrintWriter out = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "EUC-KR"), false);
        out.print(html);
        out.flush();
    }
}
