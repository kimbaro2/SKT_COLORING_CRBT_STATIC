package com.skt.crbt.coloring.gift;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

    static final String MASTER = "/static/giftgw/giftstatic/master";

    private final JdbcTemplate copysts;
    private final GiftCpInfoLoader cpInfoLoader;

    public GiftPageController(@Qualifier("copystsJdbcTemplate") JdbcTemplate copysts,
                              GiftCpInfoLoader cpInfoLoader) {
        this.copysts = copysts;
        this.cpInfoLoader = cpInfoLoader;
    }

    @GetMapping("/static/giftgw/giftstatic/master/admin")
    public void menu(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!CookieSupport.has(request, CookieNames.GIFT_ID)) {
            HtmlWriter.write(response,
                    "<script>location.replace('/static/giftgw/giftstatic/index.html');</script>");
            return;
        }
        Calendar to = Calendar.getInstance();
        Calendar from = (Calendar) to.clone();
        from.add(Calendar.DATE, -7);
        String fromYmd = ymd(from);
        String toYmd = ymd(to);
        Map<String, String> cps = cpInfoLoader.load();

        StringBuilder html = new StringBuilder();
        html.append("<html><head>");
        html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">");
        html.append("<title>\uc120\ubb3c \ud1b5\uacc4</title>");
        html.append("<link rel=\"StyleSheet\" type=\"text/css\" href=\"").append(MASTER).append("/default.css\">");
        html.append("<script language=\"JavaScript\">");
        html.append("function logout(){var f=document.forms[0];f.target='_top';f.action='").append(MASTER).append("/logout';f.submit();}");
        html.append("function PutTerm(t){document.forms[0].terms.value=t;}");
        html.append("function setGift(code){var f=document.forms[0];f.code.value=code;f.dbtable.value='SSRVC';");
        html.append("f.action='").append(MASTER).append("/master_sstatic';f.target='body';f.submit();}");
        html.append("function setData(t){var f=document.forms[0];f.dbtable.value=t;f.code.value='ALL';");
        html.append("if(t=='SSRVC')f.action='").append(MASTER).append("/master_sstatic';");
        html.append("else if(t=='SPTLC')f.action='").append(MASTER).append("/master_pstatic';");
        html.append("else if(t=='SCODC')f.action='").append(MASTER).append("/master_cstatic';");
        html.append("else if(t=='OTH_SCODC')f.action='").append(MASTER).append("/master_oth_cstatic';");
        html.append("else if(t=='ERR')f.action='").append(MASTER).append("/master_estatic';");
        html.append("f.target='body';f.submit();}");
        html.append("</script></head><body bgcolor=#FFFFFF>");
        html.append("<br><p align=center><font color='#0000cd' size=5><strong>\uc120\ubb3c \ud1b5\uacc4</strong></font></p>");
        html.append("<form name=admin method=POST target=body>");
        html.append("<input type=hidden name=terms value=day>");
        html.append("<input type=hidden name=code value=ALL>");
        html.append("<input type=hidden name=dbtable value=SSRVC>");
        html.append("<table border=0 width=100% align=center>");
        html.append("<tr><td align=center>");
        html.append("<table border=1 bgcolor=#E6E6DA cellspacing=1 cellpadding=3>");
        html.append("<tr><td colspan=2 align=center><strong>\uc870\ud68c\uae30\uac04(YYYYMMDD)</strong></td></tr>");
        html.append("<tr><td colspan=2 align=center>");
        html.append("<input type=text name=from_yyyymmdd size=10 maxlength=8 value=").append(fromYmd).append("> \ubd80\ud130<br>");
        html.append("<input type=text name=to_yyyymmdd size=10 maxlength=8 value=").append(toYmd).append("> \uae4c\uc9c0");
        html.append("</td></tr>");
        html.append("<tr><td colspan=2 align=center>");
        html.append("\ub9e4\uccb4 <select name=media size=1>");
        html.append(mediaOption("ALL", true));
        html.append(mediaOption("WEB", false));
        html.append(mediaOption("ARS", false));
        html.append(mediaOption("WAP", false));
        html.append(mediaOption("PDA", false));
        html.append("</select> &nbsp; CP <select name=did size=1>");
        html.append("<option value=0 selected>ALL -&gt; ALL</option>");
        for (Map.Entry<String, String> entry : cps.entrySet()) {
            html.append("<option value=").append(entry.getKey()).append(">");
            html.append(text(entry.getKey())).append(" -&gt; ").append(text(entry.getValue()));
            html.append("</option>");
        }
        html.append("</select></td></tr>");
        html.append("<tr><td>");
        html.append("<input type=radio name=term value=year onclick=PutTerm(this.value)>\ub144\ubcc4<br>");
        html.append("<input type=radio name=term value=month onclick=PutTerm(this.value)>\uc6d4\ubcc4<br>");
        html.append("</td><td>");
        html.append("<input type=radio name=term value=day onclick=PutTerm(this.value) checked>\uc77c\ubcc4<br>");
        html.append("<input type=radio name=term value=hour onclick=PutTerm(this.value)>\uc2dc\uac04\ub300\ubcc4<br>");
        html.append("</td></tr></table></td></tr>");
        html.append("<tr><td><br>");
        html.append("\u25cf <a href=\"javascript:setGift('SNDREG')\">\uc77c\ubc18\uc120\ubb3c</a><br><br>");
        html.append("\u25cf <a href=\"javascript:setGift('SNDINS')\">\uc77c\ubc18\uc120\ubb3c(T\ub9c1)</a><br><br>");
        html.append("\u25cf <a href=\"javascript:setGift('REGINS')\">\ub871\uc120\ubb3c</a><br><br>");
        html.append("\u25cf <a href=\"javascript:setGift('ALL')\">\ub871\uc120\ubb3c(T\ub9c1)</a><br><br>");
        html.append("<br>\u25cf <a href=\"javascript:setData('SSRVC')\">SSRVC \uc11c\ube44\uc2a4 \ud1b5\uacc4</a><br><br>");
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
        response.setCharacterEncoding("EUC-KR");
        response.setContentType("text/html; charset=EUC-KR");
        String html = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">"
                + "<title>\uc120\ubb3c \ud1b5\uacc4</title></head>"
                + "<frameset cols=\"300,*\" border=\"0\" framespacing=\"0\" frameborder=\"YES\">"
                + "<frame src=\"" + MASTER + "/admin\" name=\"menu\" marginwidth=\"3\" marginheight=\"0\" "
                + "scrolling=\"YES\" noresize frameborder=\"NO\">"
                + "<frame src=\"" + MASTER + "/null.html\" name=\"body\" marginwidth=\"0\" marginheight=\"0\" "
                + "scrolling=\"YES\" noresize frameborder=\"NO\">"
                + "</frameset></html>";
        HtmlWriter.write(response, html);
    }

    @PostMapping({
            MASTER + "/master_sstatic",
            MASTER + "/master_pstatic",
            MASTER + "/master_cstatic",
            MASTER + "/mastere_cstatic",
            MASTER + "/master_oth_cstatic",
            MASTER + "/master_estatic"
    })
    public void stat(HttpServletRequest request,
                     @RequestParam(value = "terms", required = false) String terms,
                     @RequestParam(value = "code", required = false) String code,
                     @RequestParam(value = "media", required = false) String media,
                     @RequestParam(value = "did", required = false) String did,
                     @RequestParam(value = "dbtable", required = false) String dbtable,
                     @RequestParam(value = "from_yyyymmdd", required = false) String fromYmd,
                     @RequestParam(value = "to_yyyymmdd", required = false) String toYmd,
                     @RequestParam(value = "from_year", required = false) String fy,
                     @RequestParam(value = "from_month", required = false) String fm,
                     @RequestParam(value = "from_day", required = false) String fd,
                     @RequestParam(value = "to_year", required = false) String ty,
                     @RequestParam(value = "to_month", required = false) String tm,
                     @RequestParam(value = "to_day", required = false) String td,
                     HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI();
        if (uri.endsWith("master_estatic")) {
            legacyErrorStat(terms, fromYmd, toYmd, fy, fm, fd, ty, tm, td, response);
            return;
        }
        String table = GiftStatSql.safeTable(dbtable != null ? dbtable : tableOf(uri));
        String safeCode = GiftStatSql.safeCode(code);
        String safeMedia = GiftStatSql.safeMedia(media);
        String[] from = resolveFrom(fromYmd, fy, fm, fd);
        String[] to = resolveTo(toYmd, ty, tm, td);
        String fromTs = from[0] + from[1] + from[2] + "000000";
        String toTs = to[0] + to[1] + to[2] + "235959";
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ").append(GiftStatSql.selectList(terms, safeCode, safeMedia));
        sql.append(" FROM ").append(table);
        sql.append(" WHERE DATE>=? AND DATE<=? AND CODE='DOWNLOAD' ");
        int didValue = parseDid(did);
        if (didValue > 0) {
            sql.append(" AND DID=? ");
        }
        sql.append(" GROUP BY ").append(GiftStatSql.groupBy(terms)).append(" ORDER BY 1");
        List<List<Object>> rows;
        if (didValue > 0) {
            rows = ResultTable.query(copysts, sql.toString(), fromTs, toTs, Integer.valueOf(didValue));
        } else {
            rows = ResultTable.query(copysts, sql.toString(), fromTs, toTs);
        }
        boolean excel = uri.endsWith("mastere_cstatic");
        String title = uri.endsWith("master_sstatic")
                ? GiftStatSql.titleOf(safeCode, safeMedia)
                : titleOf(uri);
        String body = periodHtml(title, from, to, rows);
        HtmlWriter.send(response, body, excel, "MSTATIC.xls");
    }

    @PostMapping(MASTER + "/logout")
    public void logout(HttpServletResponse response) throws IOException {
        ScriptRedirect.logoutHome(response);
    }

    private void legacyErrorStat(String terms, String fromYmd, String toYmd,
                                 String fy, String fm, String fd, String ty, String tm, String td,
                                 HttpServletResponse response) throws IOException {
        String[] from = resolveFrom(fromYmd, fy, fm, fd);
        String[] to = resolveTo(toYmd, ty, tm, td);
        String fromTs = ResultTable.ymd(from[0], from[1], from[2], "000000");
        String toTs = ResultTable.ymd(to[0], to[1], to[2], "230000");
        String sql = "SELECT " + GiftStatSql.dateLabel(terms) + ", SUM(F1+F5+F9), SUM(F2+F6+F10), SUM(F3+F7+F11),"
                + " SUM(F4+F8+F12), SUM(F1+F2+F3+F4+F5+F6+F7+F8+F9+F10+F11+F12) FROM SSRVC"
                + " WHERE DATE>=? AND DATE<=? AND CODE LIKE 'E%' GROUP BY " + GiftStatSql.groupBy(terms)
                + " ORDER BY 1";
        String body = periodHtml("\uc5d0\ub7ec \ud1b5\uacc4", from, to, ResultTable.query(copysts, sql, fromTs, toTs));
        HtmlWriter.write(response, body);
    }

    private String mediaOption(String value, boolean selected) {
        return "<option value=" + value + (selected ? " selected" : "") + ">" + value + "</option>";
    }

    private String ymd(Calendar date) {
        int y = date.get(Calendar.YEAR);
        int m = date.get(Calendar.MONTH) + 1;
        int d = date.get(Calendar.DATE);
        return String.format("%04d%02d%02d", y, m, d);
    }

    private String[] resolveFrom(String ymd, String fy, String fm, String fd) {
        if (ymd != null && ymd.length() == 8) {
            return new String[]{ymd.substring(0, 4), ymd.substring(4, 6), ymd.substring(6, 8)};
        }
        return new String[]{pad(fy), pad(fm), pad(fd)};
    }

    private String[] resolveTo(String ymd, String ty, String tm, String td) {
        if (ymd != null && ymd.length() == 8) {
            return new String[]{ymd.substring(0, 4), ymd.substring(4, 6), ymd.substring(6, 8)};
        }
        return new String[]{pad(ty), pad(tm), pad(td)};
    }

    private String pad(String value) {
        if (value == null || value.length() == 0) {
            return "01";
        }
        return value.length() == 1 ? "0" + value : value;
    }

    private int parseDid(String did) {
        if (did == null || did.length() == 0) {
            return 0;
        }
        try {
            return Integer.parseInt(did.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
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
        return "SSRVC \uc11c\ube44\uc2a4 \ud1b5\uacc4";
    }

    private String periodHtml(String title, String[] from, String[] to, List<List<Object>> rows) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">");
        html.append("<title>\uc120\ubb3c \ud1b5\uacc4</title>");
        html.append("<link rel=\"StyleSheet\" type=\"text/css\" href=\"").append(MASTER).append("/default.css\">");
        html.append("</head><body>");
        html.append("<br><p align=center><font color='#2f4f4f' size='5'><strong>").append(title).append("</strong></font></p>");
        html.append("<p align=center><font size='2'>(").append(text(from[0])).append(".").append(text(from[1])).append(".").append(text(from[2]));
        html.append(" - ").append(text(to[0])).append(".").append(text(to[1])).append(".").append(text(to[2])).append(")</font></p>");
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
