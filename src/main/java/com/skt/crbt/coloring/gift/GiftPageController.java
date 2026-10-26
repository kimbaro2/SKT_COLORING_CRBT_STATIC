package com.skt.crbt.coloring.gift;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Qualifier;
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
public class GiftPageController {

    private final JdbcTemplate copysts;

    public GiftPageController(@Qualifier("copystsJdbcTemplate") JdbcTemplate copysts) {
        this.copysts = copysts;
    }

    @GetMapping("/static/giftgw/giftstatic/master/admin")
    public void menu(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!CookieSupport.has(request, CookieNames.GIFT_ID)) {
            HtmlWriter.write(response, "<script>location.replace('../index.html');</script>");
            return;
        }
        Calendar to = Calendar.getInstance();
        Calendar from = (Calendar) to.clone();
        from.add(Calendar.DATE, -7);
        StringBuilder html = new StringBuilder();
        html.append("<html><head>");
        html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">");
        html.append("<title>3\uc0ac \uc5f0\ub3d9 \ud1b5\uacc4\ud398\uc774\uc9c0</title>");
        html.append(HtmlWriter.CSS_LINK);
        html.append("<script language=\"JavaScript\">");
        html.append("function logout(){var f=document.forms[0];f.target='_top';f.action='logout';f.submit();}");
        html.append("function PutTerm(t){document.forms[0].terms.value=t;}");
        html.append("function setData(t){var f=document.forms[0];f.dbtable.value=t;");
        html.append("if(t=='SSRVC')f.action='master_sstatic';");
        html.append("else if(t=='SPTLC')f.action='master_pstatic';");
        html.append("else if(t=='SCODC')f.action='master_cstatic';");
        html.append("else if(t=='OTH_SCODC')f.action='master_oth_cstatic';");
        html.append("else if(t=='ERR')f.action='master_estatic';");
        html.append("f.submit();}");
        html.append("</script></head><body bgcolor=#FFFFFF>");
        html.append("<br><p align=center><font color='#006400' size=5><strong>");
        html.append("<a href=main.html target=_top>3\uc0ac\uc5f0\ub3d9 \uc120\ubb3c\ud558\uae30</a></strong></font></p>");
        html.append("<form name=admin method=POST target=body>");
        html.append("<input type=hidden name=terms value=day><input type=hidden name=dbtable>");
        html.append("<table border=0 width=100% align=center>");
        html.append("<tr><td align=center>");
        html.append("<table border=1 bgcolor=#E6E6DA cellspacing=1 cellpadding=3>");
        html.append("<tr><td colspan=2 align=center><strong>\uc870\ud68c\uae30\uac04</strong></td></tr>");
        html.append("<tr><td colspan=2 align=center>");
        html.append(dateFields("from", from)).append(" \ubd80\ud130<br>");
        html.append(dateFields("to", to)).append(" \uae4c\uc9c0");
        html.append("</td></tr>");
        html.append("<tr><td>");
        html.append("<input type=radio name=term value=year onclick=PutTerm(this.value)>\ub144\ubcc4<br>");
        html.append("<input type=radio name=term value=month onclick=PutTerm(this.value)>\uc6d4\ubcc4<br>");
        html.append("</td><td>");
        html.append("<input type=radio name=term value=day onclick=PutTerm(this.value) checked>\uc77c\ubcc4<br>");
        html.append("<input type=radio name=term value=hour onclick=PutTerm(this.value)>\uc2dc\uac04\ub300\ubcc4<br>");
        html.append("</td></tr></table></td></tr>");
        html.append("<tr><td><br>");
        html.append("\u25cf <a href=\"javascript:setData('SSRVC')\">SSRVC \uc11c\ube44\uc2a4 \ud1b5\uacc4</a><br><br>");
        html.append("\u25cf <a href=\"javascript:setData('SPTLC')\">SPTLC \uc5f0\ub3d9 \ud1b5\uacc4</a><br><br>");
        html.append("\u25cf <a href=\"javascript:setData('SCODC')\">SCODC \ucf54\ub4dc \ud1b5\uacc4</a><br><br>");
        html.append("\u25cf <a href=\"javascript:setData('OTH_SCODC')\">OTH</a><br><br>");
        html.append("\u25cf <a href=\"javascript:setData('ERR')\">ERR</a><br><br>");
        html.append("&nbsp;&nbsp;<font color=blue><strong><a href=javascript:logout()>LOGOUT</a></strong></font>");
        html.append("</td></tr></table></form></body></html>");
        HtmlWriter.write(response, html.toString());
    }

    @GetMapping({"/static/admin_main", "/static/admin_main.html"})
    public void legacyAdminMain(HttpServletResponse response) throws IOException {
        response.sendRedirect("/static/giftgw/giftstatic/master/main.html");
    }

    @PostMapping({
            "/static/giftgw/giftstatic/master/master_sstatic",
            "/static/giftgw/giftstatic/master/master_pstatic",
            "/static/giftgw/giftstatic/master/master_cstatic",
            "/static/giftgw/giftstatic/master/mastere_cstatic",
            "/static/giftgw/giftstatic/master/master_oth_cstatic",
            "/static/giftgw/giftstatic/master/master_estatic"
    })
    public void stat(HttpServletRequest request,
                     @RequestParam(value = "terms", required = false) String terms,
                     @RequestParam(value = "from_year", required = false) String fy,
                     @RequestParam(value = "from_month", required = false) String fm,
                     @RequestParam(value = "from_day", required = false) String fd,
                     @RequestParam(value = "to_year", required = false) String ty,
                     @RequestParam(value = "to_month", required = false) String tm,
                     @RequestParam(value = "to_day", required = false) String td,
                     HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI();
        String table = tableOf(uri);
        String codeSql = uri.endsWith("master_estatic") ? " AND CODE LIKE 'E%' " : " AND CODE='DOWNLOAD' ";
        String from = ResultTable.ymd(fy, fm, fd, "000000");
        String to = ResultTable.ymd(ty, tm, td, "230000");
        String sql = "SELECT " + dateLabel(terms) + ", SUM(F1+F5+F9), SUM(F2+F6+F10), SUM(F3+F7+F11),"
                + " SUM(F4+F8+F12), SUM(F1+F2+F3+F4+F5+F6+F7+F8+F9+F10+F11+F12) FROM " + table
                + " WHERE DATE>=? AND DATE<=? " + codeSql + " GROUP BY " + groupBy(terms) + " ORDER BY 1";
        boolean excel = uri.endsWith("mastere_cstatic");
        String body = periodHtml(titleOf(uri), fy, fm, fd, ty, tm, td,
                ResultTable.query(copysts, sql, from, to));
        HtmlWriter.send(response, body, excel, "MSTATIC.xls");
    }

    @PostMapping("/static/giftgw/giftstatic/master/logout")
    public void logout(HttpServletResponse response) throws IOException {
        ScriptRedirect.logoutHome(response);
    }

    private String dateFields(String prefix, Calendar date) {
        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH) + 1;
        int day = date.get(Calendar.DATE);
        return "<input type=text size=4 maxlength=4 name=" + prefix + "_year value=" + year + "> "
                + "<select name=" + prefix + "_month>" + numOptions(12, month) + "</select> "
                + "<select name=" + prefix + "_day>" + numOptions(31, day) + "</select>";
    }

    private String numOptions(int max, int selected) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= max; i++) {
            String num = i < 10 ? "0" + i : String.valueOf(i);
            sb.append("<option value=").append(num);
            if (i == selected) {
                sb.append(" selected");
            }
            sb.append(">").append(num).append("</option>");
        }
        return sb.toString();
    }

    private String tableOf(String uri) {
        if (uri.endsWith("master_pstatic")) {
            return "SPTLC";
        }
        if (uri.endsWith("master_cstatic") || uri.endsWith("mastere_cstatic")) {
            return "SCODC";
        }
        if (uri.endsWith("master_oth_cstatic")) {
            return "OTH_SCODD";
        }
        if (uri.endsWith("master_estatic")) {
            return "SSRVC";
        }
        return "SSRVC";
    }

    private String titleOf(String uri) {
        if (uri.endsWith("master_pstatic")) {
            return "SPTLC \uc5f0\ub3d9 \ud1b5\uacc4";
        }
        if (uri.endsWith("master_cstatic") || uri.endsWith("mastere_cstatic")) {
            return "SCODC \ucf54\ub4dc \ud1b5\uacc4";
        }
        if (uri.endsWith("master_oth_cstatic")) {
            return "\ub9c1 & \ubca8 \ud1b5\uacc4";
        }
        if (uri.endsWith("master_estatic")) {
            return "\uc5d0\ub7ec \ud1b5\uacc4";
        }
        return "SSRVC \uc11c\ube44\uc2a4 \ud1b5\uacc4";
    }

    private String dateLabel(String terms) {
        if ("hour".equals(terms)) {
            return "date_format(DATE,'%H\uc2dc')";
        }
        if ("month".equals(terms)) {
            return "date_format(DATE,'%Y\ub144 %m\uc6d4')";
        }
        if ("year".equals(terms)) {
            return "date_format(DATE,'%Y\ub144')";
        }
        return "date_format(DATE,'%m\uc6d4 %d\uc77c')";
    }

    private String groupBy(String terms) {
        if ("hour".equals(terms)) {
            return "date_format(DATE,'%H')";
        }
        if ("month".equals(terms)) {
            return "date_format(DATE,'%Y%m')";
        }
        if ("year".equals(terms)) {
            return "date_format(DATE,'%Y')";
        }
        return "date_format(DATE,'%Y%m%d')";
    }

    private String periodHtml(String title, String fy, String fm, String fd, String ty, String tm, String td,
                              List<List<Object>> rows) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">");
        html.append("<title>3\uc0ac\uc5f0\ub3d9 \uc120\ubb3c\ud558\uae30</title>");
        html.append(HtmlWriter.CSS_LINK);
        html.append("</head><body>");
        html.append("<br><p align=center><font color='#2f4f4f' size='5'><strong>").append(title).append("</strong></font></p>");
        html.append("<p align=center><font size='2'>(").append(text(fy)).append(".").append(text(fm)).append(".").append(text(fd));
        html.append(" - ").append(text(ty)).append(".").append(text(tm)).append(".").append(text(td)).append(")</font></p>");
        if (rows.isEmpty() || rows.get(0).size() < 6) {
            if (!rows.isEmpty() && rows.get(0).size() < 6 && rows.get(0).get(0) != null) {
                html.append("<br><br><b><font size=2 color=red>&nbsp;&nbsp;").append(text(rows.get(0).get(0))).append("</font></b>");
            } else {
                html.append("<br><br><b><font size=2 color=red>&nbsp;&nbsp;DATA \uac00\uc5c6\uc2b5\ub2c8\ub2e4</font></b>");
            }
            html.append("</body></html>");
            return html.toString();
        }
        html.append("<table border='0' width='98%'><tr align='center' bgcolor='#e6e6fa' height='25'>");
        html.append("<td>DATE</td><td>WEB</td><td>ARS</td><td>WAP</td><td>PDA</td><td>TOTAL</td></tr>");
        NumberFormat format = NumberFormat.getInstance(Locale.KOREA);
        long[] grand = new long[5];
        for (List<Object> row : rows) {
            html.append("<tr align=right bgcolor='#fdf5e6' height='20'>");
            html.append("<td bgcolor='#e6e6fa' align=center>").append(text(row.get(0))).append("</td>");
            for (int i = 1; i <= 5; i++) {
                long value = longValue(row.get(i));
                grand[i - 1] += value;
                html.append("<td align=right>").append(format.format(value)).append("</td>");
            }
            html.append("</tr>");
        }
        html.append("<tr bgcolor='#e6e6fa' height=30><td align=center><b>TOTAL</b></td>");
        for (int i = 0; i < grand.length; i++) {
            html.append("<td align=right>").append(format.format(grand[i])).append("</td>");
        }
        html.append("</tr></table></body></html>");
        return html.toString();
    }

    private long longValue(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (Exception e) {
            return 0L;
        }
    }

    private String text(Object value) {
        if (value == null) {
            return "";
        }
        return String.valueOf(value).replace("&", "&amp;").replace("<", "&lt;").replace("\"", "");
    }
}
