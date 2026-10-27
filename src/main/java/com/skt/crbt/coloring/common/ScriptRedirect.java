package com.skt.crbt.coloring.common;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import jakarta.servlet.http.HttpServletResponse;

public final class ScriptRedirect {

    private ScriptRedirect() {
    }

    public static void submit(HttpServletResponse response, String action) throws IOException {
        response.setContentType("text/html; charset=euc-kr");
        PrintWriter out = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "EUC-KR"), false);
        out.print("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\"></head>");
        out.print("<form name=main action=\"");
        out.print(action);
        out.print("\"></form>");
        out.print("<script language=\"JavaScript\">document.main.submit();</script></html>");
        out.flush();
    }

    public static void submitTop(HttpServletResponse response, String action) throws IOException {
        response.setContentType("text/html; charset=euc-kr");
        PrintWriter out = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "EUC-KR"), false);
        out.print("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\"></head>");
        out.print("<form name=main action=\"");
        out.print(action);
        out.print("\" target=\"_top\"></form>");
        out.print("<script language=\"JavaScript\">document.main.submit();</script></html>");
        out.flush();
    }

    public static void logoutHome(HttpServletResponse response) throws IOException {
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setHeader("Clear-Site-Data", "\"cache\", \"cookies\", \"storage\"");
        CookieSupport.clearAll(response);
        response.setContentType("text/html; charset=euc-kr");
        PrintWriter out = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "EUC-KR"), false);
        out.print("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">");
        out.print("<meta http-equiv=\"Cache-Control\" content=\"no-store, no-cache, must-revalidate\">");
        out.print("<meta http-equiv=\"Pragma\" content=\"no-cache\">");
        out.print("<meta http-equiv=\"Expires\" content=\"0\"></head><body>");
        out.print("<script language=\"JavaScript\">");
        out.print("var w=window.top||window;");
        out.print("try{w.close();}catch(e){}");
        out.print("if(!w.closed){w.location.replace('/');}");
        out.print("</script></body></html>");
        out.flush();
    }
}
