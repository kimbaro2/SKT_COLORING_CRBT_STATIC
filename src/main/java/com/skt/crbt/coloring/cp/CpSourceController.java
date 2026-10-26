package com.skt.crbt.coloring.cp;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.skt.crbt.coloring.common.CookieNames;
import com.skt.crbt.coloring.common.CookieSupport;
import com.skt.crbt.coloring.common.HtmlWriter;
import com.skt.crbt.coloring.common.ResultTable;
import com.skt.crbt.coloring.common.ScriptRedirect;

@Controller
public class CpSourceController {

    private static final String FANDANGO = "\ud310\ub2f9\uace0";

    private final JdbcTemplate jdbc;

    public CpSourceController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/cp_static/source/src_main.html")
    public void main(HttpServletResponse response) throws IOException {
        HtmlWriter.write(response,
                "<html><head><title>SRC</title>"
                        + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">" + HtmlWriter.CSS_LINK + "</head>"
                        + "<frameset cols=\"260,*\" border=0>"
                        + "<frame src=\"src_menu\" name=\"menu\">"
                        + "<frame src=\"null.html\" name=\"body\">"
                        + "</frameset></html>");
    }

    @GetMapping("/cp_static/source/src_menu")
    public void menu(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!CookieSupport.has(request, CookieNames.SRC_ID)) {
            HtmlWriter.write(response, "<script>location.replace('/cp_static/index.html');</script>");
            return;
        }
        HtmlWriter.write(response,
                "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">" + HtmlWriter.CSS_LINK + "</head><body>"
                        + "<p align=center><a href=src_main.html target=_top>SRC</a></p>"
                        + "<form method=POST action=code_static target=body>"
                        + "<input type=hidden name=order_col value=6><input type=hidden name=order value=desc>"
                        + "from <input name=from_year size=4> <input name=from_month size=2> <input name=from_day size=2><br>"
                        + "to <input name=to_year size=4> <input name=to_month size=2> <input name=to_day size=2><br>"
                        + "<button type=submit>CODE</button>"
                        + "</form>"
                        + "<form method=POST action=logout target=_top><button type=submit>LOGOUT</button></form>"
                        + "</body></html>");
    }

    @PostMapping({"/cp_static/source/code_static", "/cp_static/source/excel_code"})
    public void code(@RequestParam(value = "order_col", required = false) String orderCol,
                     @RequestParam(value = "from_year", required = false) String fy,
                     @RequestParam(value = "from_month", required = false) String fm,
                     @RequestParam(value = "from_day", required = false) String fd,
                     @RequestParam(value = "to_year", required = false) String ty,
                     @RequestParam(value = "to_month", required = false) String tm,
                     @RequestParam(value = "to_day", required = false) String td,
                     HttpServletRequest request,
                     HttpServletResponse response) throws IOException {
        String account = CookieSupport.get(request, CookieNames.SRC_AC);
        String cpId = CookieSupport.get(request, CookieNames.SRC_ID);
        if ("fandango".equals(account)) {
            account = FANDANGO;
        }
        int col = orderCol(orderCol);
        String from = ResultTable.ymd(fy, fm, fd, "000000");
        String to = ResultTable.ymd(ty, tm, td, "230000");
        String sql = "SELECT DID, SUM(F1+F5+F9), SUM(F2+F6+F10), SUM(F3+F7+F11), SUM(F4+F8+F12),"
                + " SUM(F1+F5+F9+F2+F6+F10+F3+F7+F11+F4+F8+F12), v.TITLE, v.AUTHOR"
                + " FROM voice_source v, SCODD c"
                + " WHERE DATE>=? AND DATE<=? AND comment=? AND v.cp_id=?"
                + " AND v.cp_id=round(c.DID/100000000,0) AND v.vsrc_id=c.DID%100000000"
                + " AND CODE='DOWNLOAD' AND (F1+F5+F9+F2+F6+F10+F3+F7+F11+F4+F8+F12) > 0"
                + " GROUP BY DID ORDER BY " + col + " desc";
        boolean excel = request.getRequestURI().indexOf("excel") >= 0;
        String html = ResultTable.html("SRC", ResultTable.query(jdbc, sql, from, to, account, cpId),
                excel ? null : "excel_code",
                "order_col", orderCol, "from_year", fy, "from_month", fm, "from_day", fd,
                "to_year", ty, "to_month", tm, "to_day", td);
        HtmlWriter.send(response, html, excel, "STATIC.xls");
    }

    @PostMapping("/cp_static/source/logout")
    public void logout(HttpServletResponse response) throws IOException {
        ScriptRedirect.logoutHome(response);
    }

    private int orderCol(String orderCol) {
        try {
            int value = Integer.parseInt(orderCol);
            if (value >= 1 && value <= 8) {
                return value;
            }
        } catch (Exception ignored) {
            // default
        }
        return 6;
    }
}
