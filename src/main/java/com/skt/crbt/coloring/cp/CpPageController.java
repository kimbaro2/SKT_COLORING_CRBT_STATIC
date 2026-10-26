package com.skt.crbt.coloring.cp;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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
public class CpPageController {

    private final JdbcTemplate jdbc;

    public CpPageController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/cp_static/cp_menu")
    public void menu(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!CookieSupport.has(request, CookieNames.CP_ID)) {
            HtmlWriter.write(response, "<script>location.replace('index.html');</script>");
            return;
        }
        String did = CookieSupport.get(request, CookieNames.CP_ID);
        String account = CookieSupport.get(request, CookieNames.CP_AC);
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        int year = yesterday.get(Calendar.YEAR);
        int month = yesterday.get(Calendar.MONTH) + 1;
        int day = yesterday.get(Calendar.DATE);
        String cpName = cpName(did);
        StringBuilder html = new StringBuilder();
        html.append("<html><head>");
        html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">");
        html.append("<title>CRBT CP \ud1b5\uacc4</title>");
        html.append("<link rel=\"StyleSheet\" type=\"text/css\" href=\"/aspstatic/default.css\">");
        html.append(HtmlWriter.INLINE_STYLE);
        html.append("<script language=\"JavaScript\">");
        html.append("function list(view){if(view.style.display!='none')view.style.display='none';else view.style.display='';}");
        html.append("function PutTerm(t){document.forms[0].terms.value=t;}");
        html.append("function win_select(s){");
        html.append("if(s=='\ud604\uc7ac\ucc3d\uc5d0'){document.forms[0].target='body';document.forms[0].win_sel.value='\uc0c8\ucc3d\uc5d0';}");
        html.append("else{document.forms[0].target='new';document.forms[0].win_sel.value='\ud604\uc7ac\ucc3d\uc5d0';}}");
        html.append("function logout(){var f=document.forms[0];f.target='_top';f.action='logout';f.submit();}");
        html.append("function goCodeRing(){window.open('/cp_static/codering/index.html');}");
        html.append("function goCopyRing(){window.open('/cp_static/copyring/index.html');}");
        html.append("function goCDRPortal(){window.open('/cp_cdpstatic/auth');}");
        html.append("function setData(t,c,d,e){var f=document.forms[0];f.dbtable.value=t;f.cclass.value=e;");
        html.append("if(c!='')f.code.value=c;");
        html.append("if(t=='SPTLD'&&f.portal_did)d=f.portal_did.value;");
        html.append("if(d!='')f.did.value=d;");
        html.append("if(e=='err')f.action='err_static';");
        html.append("else if(t=='SCODD')f.action='code_static';");
        html.append("else if(t=='SSRVD')f.action='cp_sstatic';");
        html.append("else if(t=='SPTLD')f.action='cp_pstatic';");
        html.append("else if(t=='OTH_SCODD')f.action='oth_code_static';");
        html.append("f.submit();}");
        html.append("</script></head><body bgcolor=#FFFFFF>");
        html.append("<form action=cp_sstatic method=POST target=body>");
        html.append("<input type=hidden name=terms value=day>");
        html.append("<input type=hidden name=sid><input type=hidden name=did value=\"").append(text(did)).append("\">");
        html.append("<input type=hidden name=code><input type=hidden name=dbtable value=SSRVD>");
        html.append("<input type=hidden name=cclass><input type=hidden name=order_col value=6>");
        html.append("<input type=hidden name=order value=desc>");
        html.append("<input type=hidden name=win_sel value='\uc0c8\ucc3d\uc5d0'>");
        html.append("<br><p align=center><font color='#006400' size=5><strong>");
        html.append("<a href=cp_main.html target=_top>CRBT CP \ud1b5\uacc4</a></strong></font></p>");
        html.append("<table border=0 width=80% align=center><tr><td align=center>");
        html.append("<table border=1 bgcolor=#b0c4da cellspacing=1 cellpadding=3>");
        html.append("<tr><td colspan=2>");
        html.append("<input type=text size=4 maxlength=4 name=from_year value=").append(year).append(">\ub144 ");
        html.append("<select name=from_month size=1>").append(numOptions(12, month)).append("</select>\uc6d4");
        html.append("<select name=from_day size=1>").append(numOptions(31, day)).append("</select>\uc77c<br>\ubd80\ud130<br>");
        html.append("<input type=text size=4 maxlength=4 name=to_year value=").append(year).append(">\ub144 ");
        html.append("<select name=to_month size=1>").append(numOptions(12, month)).append("</select>\uc6d4");
        html.append("<select name=to_day size=1>").append(numOptions(31, day)).append("</select>\uc77c");
        html.append("</td></tr>");
        html.append("<tr><td><input type=radio name=term value=month onclick=PutTerm(this.value)>\uc6d4\ubcc4<br></td>");
        html.append("<td><input type=radio name=term value=day onclick=PutTerm(this.value) checked>\uc77c\ubcc4<br></td></tr>");
        html.append("<tr><td colspan=2>");
        html.append("<input type=radio name=win value='\ud604\uc7ac\ucc3d\uc5d0' onclick=win_select(this.value) checked>\ud604\uc7ac\ucc3d\uc5d0 ");
        html.append("<input type=radio name=win value='\uc0c8\ucc3d\uc5d0' onclick=win_select(this.value)>\uc0c8\ucc3d\uc5d0");
        html.append("</td></tr></table></td></tr><tr><td><br></td></tr>");
        html.append("<tr align=left><td>");
        html.append("<br>\u25cf <a href=\"javascript:list(document.all.srv)\">\uc11c\ube44\uc2a4 \ud1b5\uacc4</a>");
        html.append("<div id=srv style=display:none>");
        html.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;. <a href=\"javascript:setData('SSRVD','SNDREG','").append(text(did)).append("','COMMON')\">\uc74c\uc6d0\ub4f1\ub85d</a>");
        html.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;. <a href=\"javascript:setData('SSRVD','SNDREG','").append(text(did)).append("','LONG')\">\uc74c\uc6d0\ub4f1\ub85d(Long)</a>");
        html.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;. <a href=\"javascript:setData('SSRVD','SNDINS','").append(text(did)).append("','')\">\uc74c\uc6d0\uc120\ubb3c</a>");
        html.append("</div>");
        html.append("<br><br>\u25cf <a href=\"javascript:list(document.all.esrv)\">\uc11c\ube44\uc2a4 \uc5d0\ub7ec \ud1b5\uacc4</a>");
        html.append("<div id=esrv style=display:none>");
        html.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;. <a href=\"javascript:setData('SSRVD','ESNDREG','','err')\">\uc74c\uc6d0\ub4f1\ub85d</a>");
        html.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;. <a href=\"javascript:setData('SSRVD','ESNDINS','','err')\">\uc74c\uc6d0\uc120\ubb3c</a>");
        html.append("</div>");
        html.append("<br><br>\u25cf <a href=\"javascript:list(document.all.ptl)\">\uc5f0\ub3d9 \ud1b5\uacc4</a>");
        html.append("<div id=ptl style=display:none><br>&nbsp;&nbsp;&nbsp;&nbsp;");
        html.append("<select name=portal_did size=1><option value=\"").append(text(did)).append("\" selected>\uc804\uccb4</option></select><br>");
        html.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;. <a href=\"javascript:setData('SPTLD','DOWNLOAD','','')\">\uc74c\uc6d0\uc0ac\uc6a9</a>");
        html.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;. <a href=\"javascript:setData('SPTLD','QUERY','','')\">\uc870\ud68c\uc0ac\uc6a9</a>");
        html.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;. <a href=\"javascript:setData('SPTLD','SETTING','','')\">\uc124\uc815\uc0ac\uc6a9</a>");
        html.append("</div>");
        html.append("<br><br>\u25cf <a href=\"javascript:list(document.all.eptl)\">\uc5f0\ub3d9 \uc5d0\ub7ec \ud1b5\uacc4</a>");
        html.append("<div id=eptl style=display:none>");
        html.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;. <a href=\"javascript:setData('SPTLD','ESNDREG','','err')\">\uc74c\uc6d0\ub4f1\ub85d</a>");
        html.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;. <a href=\"javascript:setData('SPTLD','ESNDINS','','err')\">\uc74c\uc6d0\uc120\ubb3c</a>");
        html.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;. <a href=\"javascript:setData('SPTLD','ESVRQRY','','err')\">\uac00\uc785\uc778\uc99d</a>");
        html.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;. <a href=\"javascript:setData('SPTLD','ESNDQRY','','err')\">\uc74c\uc6d0\uc870\ud68c</a>");
        html.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;. <a href=\"javascript:setData('SPTLD','ESNDIND','','err')\">\uc74c\uc6d0\uc124\uc815</a>");
        html.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;. <a href=\"javascript:setData('SPTLD','ESNDUNR','','err')\">\uc74c\uc6d0\ud574\uc9c0</a>");
        html.append("</div>");
        html.append("<br><br>\u25cf <a href=\"javascript:list(document.all.code_list)\">\uc74c\uc6d0 \ucf54\ub4dc\ubcc4 \ud1b5\uacc4</a>");
        html.append("<div id=code_list style=display:none>");
        html.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;. <a href=\"javascript:setData('SCODD','SNDREG','").append(text(did)).append("','COMMON')\">\uc74c\uc6d0\ub4f1\ub85d</a>");
        html.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;. <a href=\"javascript:setData('SCODD','SNDREG','").append(text(did)).append("','LONG')\">\uc74c\uc6d0\ub4f1\ub85d(Long)</a>");
        html.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;. <a href=\"javascript:setData('SCODD','SNDINS','").append(text(did)).append("','')\">\uc74c\uc6d0\uc120\ubb3c</a>");
        html.append("</div>");
        html.append("<br><br>\u25cf <a href=\"javascript:setData('OTH_SCODD','DOWNLOAD','").append(text(did)).append("','')\">\ub9c1 & \ubca8 \ucf54\ub4dc\ubcc4 \ud1b5\uacc4</a>");
        html.append("<br><br>\u25cf <a href=\"javascript:goCodeRing()\">\ucf54\ub4dc\ub9c1 \ud1b5\uacc4</a>");
        html.append("<br><br>\u25cf <a href=\"javascript:goCopyRing()\">\uce74\ud53c\ub9c1 \ud1b5\uacc4</a>");
        html.append("<br><br>\u25cf <a href=\"javascript:goCDRPortal()\">\ucf54\ub4dc\ub9c1 \ud3ec\ud0c8</a>");
        html.append("<br><br>&nbsp;&nbsp;&nbsp;&nbsp;<font color=blue><strong><a href=javascript:logout()>LOGOUT</a></strong></font>");
        html.append("<br><br><b>&lt;&lt; \uc811\uc18d CP \uc815\ubcf4 &gt;&gt;<br><br>");
        html.append("CP_ID : ").append(text(did)).append("<br>");
        html.append("CP_NAME : ").append(text(cpName)).append("<br>");
        html.append("LOGIN_ID: ").append(text(account)).append("</b>");
        html.append("</td></tr></table></form></body></html>");
        HtmlWriter.write(response, html.toString());
    }

    @PostMapping({"/cp_static/code_static", "/cp_static/cp_sstatic", "/cp_static/cp_pstatic",
            "/cp_static/oth_code_static", "/cp_static/err_static",
            "/cp_static/excel_code", "/cp_static/excel_oth_code",
            "/cp_static/copyring/code_static", "/cp_static/copyring/cp_sstatic", "/cp_static/copyring/err_static",
            "/cp_static/copyring/excel_code",
            "/cp_static/codering/code_static", "/cp_static/codering/cp_sstatic", "/cp_static/codering/err_static",
            "/cp_static/codering/excel_code"})
    public void stat(HttpServletRequest request,
                     @RequestParam(value = "dbtable", required = false) String dbtable,
                     @RequestParam(value = "code", required = false) String code,
                     @RequestParam(value = "cclass", required = false) String cclass,
                     @RequestParam(value = "terms", required = false) String terms,
                     @RequestParam(value = "order_col", required = false) String orderCol,
                     @RequestParam(value = "did", required = false) String requestDid,
                     @RequestParam(value = "from_year", required = false) String fy,
                     @RequestParam(value = "from_month", required = false) String fm,
                     @RequestParam(value = "from_day", required = false) String fd,
                     @RequestParam(value = "to_year", required = false) String ty,
                     @RequestParam(value = "to_month", required = false) String tm,
                     @RequestParam(value = "to_day", required = false) String td,
                     HttpServletResponse response) throws IOException {
        String did = CookieSupport.get(request, CookieNames.CP_ID);
        if (!ResultTable.oneOf(dbtable, "SCODD", "SSRVD", "LONG_SSRVD", "SPTLD", "OTH_SCODD")) {
            dbtable = "SSRVD";
        }
        if ("LONG".equals(cclass) && "SSRVD".equals(dbtable)) {
            dbtable = "LONG_SSRVD";
        }
        String from = ResultTable.ymd(fy, fm, fd, "000000");
        String to = ResultTable.ymd(ty, tm, td, "230000");
        String sql;
        String uri = request.getRequestURI();
        boolean excel = uri.indexOf("excel_") >= 0;
        boolean codePage = uri.endsWith("code_static") || uri.endsWith("excel_code")
                || uri.endsWith("oth_code_static") || uri.endsWith("excel_oth_code");
        if (codePage) {
            boolean oth = uri.indexOf("oth_") >= 0;
            String fields = oth
                    ? "SUM(F1+F2+F3+F4),SUM(F5+F6+F7+F8),SUM(F9+F10+F11+F12),"
                    + "SUM(F1+F2+F3+F4+F5+F6+F7+F8+F9+F10+F11+F12)"
                    : codeFields(code);
            String longCond = oth ? "" : ("LONG".equals(cclass)
                    ? " AND DID%100000000 > 50000000"
                    : " AND DID%100000000 < 50000000");
            String positive = oth
                    ? " AND (F1+F5+F9+F2+F6+F10+F3+F7+F11+F4+F8+F12)>0 "
                    : codePositive(code) + " AND code='DOWNLOAD' ";
            int col = oth && "6".equals(orderCol) ? 5 : orderCol(orderCol);
            sql = "SELECT DID," + fields + ",v.CLASS_ID,v.TITLE,v.AUTHOR,v.comment,v.owner"
                    + " FROM " + dbtable + " c, crbt.voice_source v"
                    + " WHERE DATE>=? AND DATE<=? AND SID=? AND v.cp_id=? AND DID%100000000=v.vsrc_id"
                    + longCond + positive + " GROUP BY DID ORDER BY " + col + " desc";
            String excelAction = oth ? "excel_oth_code" : "excel_code";
            String body = codeHtml(oth, did, code, fy, fm, fd, ty, tm, td, excel, excelAction,
                    dbtable, cclass, orderCol, ResultTable.query(jdbc, sql, from, to, did, did));
            HtmlWriter.send(response, body, excel, "STATIC.xls");
            return;
        }
        boolean rootService = uri.endsWith("/cp_sstatic");
        boolean rootPortal = uri.endsWith("/cp_pstatic");
        boolean rootErr = uri.endsWith("/err_static");
        if (rootService) {
            sql = "SELECT " + dateLabel(terms) + ", " + serviceSums(code)
                    + " FROM " + dbtable
                    + " WHERE DATE>=? AND DATE<=? AND CODE='DOWNLOAD' AND DID=? "
                    + groupSql(terms) + " ORDER BY DATE";
            HtmlWriter.write(response, serviceHtml(code, fy, fm, fd, ty, tm, td,
                    ResultTable.query(jdbc, sql, from, to, did)));
            return;
        }
        if (rootPortal || rootErr) {
            String safeCode = rootErr ? errorCode(code) : portalCode(code);
            String id = numericId(requestDid, did);
            String didSql = portalDid(rootPortal || "SPTLD".equals(dbtable), id);
            sql = "SELECT " + dateLabel(terms, true) + ", SUM(F1),SUM(F2),SUM(F3),SUM(F4),SUM(F5),SUM(F6),"
                    + "SUM(F7),SUM(F8),SUM(F9),SUM(F10),SUM(F11),SUM(F12)"
                    + " FROM " + dbtable
                    + " WHERE DATE>=? AND DATE<=? AND CODE=? " + didSql + " "
                    + groupSql(terms) + " ORDER BY DATE";
            HtmlWriter.write(response, channelHtml(rootErr, dbtable, safeCode, id, fy, fm, fd, ty, tm, td,
                    ResultTable.query(jdbc, sql, from, to, safeCode, id)));
            return;
        }
        sql = "SELECT " + dateLabel(terms) + ", " + serviceSums(code)
                + " FROM " + dbtable
                + " WHERE DATE>=? AND DATE<=? AND CODE='DOWNLOAD' AND DID=? " + groupSql(terms) + " ORDER BY DATE";
        HtmlWriter.write(response, serviceHtml(code, fy, fm, fd, ty, tm, td,
                ResultTable.query(jdbc, sql, from, to, did)));
    }

    @PostMapping("/cp_static/logout")
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

    private String dateLabel(String terms) {
        return dateLabel(terms, false);
    }

    private String dateLabel(String terms, boolean withYear) {
        if ("hour".equals(terms)) {
            return "date_format(DATE,'%H\uc2dc')";
        }
        if ("year".equals(terms)) {
            return "date_format(DATE,'%Y\ub144')";
        }
        if ("month".equals(terms)) {
            return withYear ? "date_format(DATE,'%Y\ub144 %m\uc6d4')" : "date_format(DATE,'%m\uc6d4')";
        }
        return withYear
                ? "date_format(DATE,'%Y\ub144 %m\uc6d4 %d\uc77c')"
                : "date_format(DATE,'%m\uc6d4 %d\uc77c')";
    }

    private String codePositive(String code) {
        if ("SNDREG".equals(code)) {
            return " AND (F1+F2+F3+F4)>0 ";
        }
        if ("SNDINS".equals(code)) {
            return " AND (F5+F6+F7+F8)>0 ";
        }
        if ("REGINS".equals(code)) {
            return " AND (F9+F10+F11+F12)>0 ";
        }
        return " AND (F1+F5+F9+F2+F6+F10+F3+F7+F11+F4+F8+F12)>0 ";
    }

    private String serviceHtml(String code, String fy, String fm, String fd, String ty, String tm, String td,
                               List<List<Object>> rows) {
        String title = "\uc74c\uc6d0 \ub4f1\ub85d \ud1b5\uacc4";
        if ("SNDINS".equals(code)) {
            title = "\uc74c\uc6d0 \uc120\ubb3c \ud1b5\uacc4";
        } else if ("ALL".equals(code)) {
            title = "\uc804\uccb4 \ud1b5\uacc4";
        }
        StringBuilder html = new StringBuilder();
        html.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">");
        html.append("<title>CRBT CP SERVER MASTER \ud1b5\uacc4</title>");
        html.append("<link rel=\"StyleSheet\" type=\"text/css\" href=\"/aspstatic/default.css\">");
        html.append("</head><body>");
        html.append("<br><p align=center><font color='#2f4f4f' size='5'><strong>").append(title).append("</strong></font></p>");
        html.append("<p align=center><font size='2'>(").append(text(fy)).append(".").append(text(fm)).append(".").append(text(fd));
        html.append(" - ").append(text(ty)).append(".").append(text(tm)).append(".").append(text(td)).append(")</font></p><br>");
        boolean empty = rows.isEmpty() || rows.get(0).size() < 6;
        if (empty) {
            if (!rows.isEmpty() && rows.get(0).size() < 6) {
                html.append("<br><br><b><font size=2 color=red>&nbsp;&nbsp;").append(text(String.valueOf(rows.get(0).get(0)))).append("</font></b>");
            } else {
                html.append("<br><br><b><font size=2 color=red>&nbsp;&nbsp;DATA \uac00\uc5c6\uc2b5\ub2c8\ub2e4</font></b>");
            }
            html.append("</body></html>");
            return html.toString();
        }
        html.append("<table border='0' width='98%'>");
        html.append("<tr align='center' bgcolor='#e6e6fa' height='30'><td>DATE</td>");
        html.append("<td>WEB</td><td>ARS</td><td>WAP</td><td>PDA</td><td>TOTAL</td></tr>");
        NumberFormat format = NumberFormat.getInstance(Locale.KOREA);
        long[] grand = new long[5];
        for (List<Object> row : rows) {
            html.append("<tr align=right bgcolor='#fdf5e6' height='20'>");
            html.append("<td bgcolor='#e6e6fa' align=center>").append(text(String.valueOf(row.get(0)))).append("</td>");
            for (int i = 1; i <= 5; i++) {
                long value = longValue(row.get(i));
                grand[i - 1] += value;
                if (i == 5) {
                    html.append("<td align=right bgcolor='#e6e6fa'>");
                } else {
                    html.append("<td align=right>");
                }
                html.append(format.format(value)).append("</td>");
            }
            html.append("</tr>");
        }
        html.append("<tr bgcolor='#e6e6fa' height=30 align=right><td align=center><b>TOTAL</b></td>");
        for (int i = 0; i < grand.length; i++) {
            html.append("<td align=right>").append(format.format(grand[i])).append("</td>");
        }
        html.append("</tr></table><br><br>");
        html.append("\ucc38\uace0) \uae30\uc874\uc5d0 \ud45c\uc2dc\ub418\ub358 \uc810\uc720\uc728\uc740 SKT \uc694\uccad\uc5d0 \ub530\ub77c \ud45c\uc2dc\ud558\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4");
        html.append("</body></html>");
        return html.toString();
    }

    private long longValue(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value == null) {
            return 0L;
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private String channelHtml(boolean error, String dbtable, String code, String id,
                               String fy, String fm, String fd, String ty, String tm, String td,
                               List<List<Object>> rows) {
        String[] labels = new String[12];
        String title = code;
        List<List<Object>> labelRows = ResultTable.query(jdbc,
                "SELECT DESCR,F1,F2,F3,F4,F5,F6,F7,F8,F9,F10,F11,F12 FROM SLABLE WHERE TBLNAME=? AND CODE=? LIMIT 1",
                dbtable, code);
        if (!labelRows.isEmpty() && labelRows.get(0).size() >= 13) {
            Object descr = labelRows.get(0).get(0);
            if (descr != null && String.valueOf(descr).length() > 0) {
                title = String.valueOf(descr);
            }
            for (int i = 0; i < 12; i++) {
                Object label = labelRows.get(0).get(i + 1);
                labels[i] = label == null ? "" : String.valueOf(label).trim();
            }
        }
        int limit = error ? 12 : 6;
        StringBuilder html = new StringBuilder();
        html.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">");
        html.append("<title>CRBT CP SERVER MASTER \ud1b5\uacc4</title>");
        html.append("<link rel=\"StyleSheet\" type=\"text/css\" href=\"/aspstatic/default.css\">");
        html.append("</head><body>");
        html.append("<br><p align=center><font color='#2f4f4f' size='5'><strong>");
        html.append(text(title)).append(" \ud1b5\uacc4</strong></font></p>");
        period(html, fy, fm, fd, ty, tm, td);
        long parsed = 0L;
        try {
            parsed = Long.parseLong(id);
        } catch (NumberFormatException ignored) {
            parsed = 0L;
        }
        if (!"SSRVD".equals(dbtable) && !"LONG_SSRVD".equals(dbtable)) {
            html.append(parsed < 1000L ? "\uc7a5\ube44 ID : \uc804\uccb4<br>" : "\uc7a5\ube44 ID : " + text(id) + "<br>");
        }
        if (rows.isEmpty() || rows.get(0).size() < 13) {
            if (!rows.isEmpty() && rows.get(0).size() < 13) {
                html.append("<br><br><b><font size=2 color=red>&nbsp;&nbsp;").append(text(String.valueOf(rows.get(0).get(0)))).append("</font></b>");
            } else {
                noData(html);
            }
            html.append("</body></html>");
            return html.toString();
        }
        NumberFormat format = NumberFormat.getInstance(Locale.KOREA);
        html.append("<table border='0' width='98%'><tr align='center' bgcolor='#e6e6fa' height='30'><td>DATE</td>");
        int shown = 0;
        for (int i = 0; i < limit; i++) {
            if (labels[i] == null || labels[i].length() == 0) {
                labels[i] = "F" + (i + 1);
            }
            html.append("<td>").append(text(labels[i])).append("</td>");
            shown++;
        }
        if (error) {
            html.append("<td>TOTAL</td>");
        }
        html.append("</tr>");
        long[] grand = new long[shown];
        long grandTotal = 0L;
        for (List<Object> row : rows) {
            html.append("<tr align=center bgcolor='#fdf5e6'><td bgcolor='#e6e6fa'>").append(text(String.valueOf(row.get(0)))).append("</td>");
            long rowTotal = 0L;
            for (int i = 0; i < shown; i++) {
                long value = longValue(row.get(i + 1));
                grand[i] += value;
                rowTotal += value;
                html.append("<td align=right>").append(format.format(value)).append("</td>");
            }
            if (error) {
                grandTotal += rowTotal;
                html.append("<td align=right>").append(format.format(rowTotal)).append("</td>");
            }
            html.append("</tr>");
        }
        html.append("<tr bgcolor='#e6e6fa' height=30><td align=center><b>TOTAL</b></td>");
        for (int i = 0; i < shown; i++) {
            html.append("<td align=right>").append(format.format(grand[i])).append("</td>");
        }
        if (error) {
            html.append("<td align=right>").append(format.format(grandTotal)).append("</td>");
        }
        html.append("</tr></table>");
        if (error) {
            html.append("<br><br><b>\ucc38\uace0)&nbsp;</b>x03 : \ube44\uac00\uc785\uc624\ub958");
            html.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;x05 : \uba54\uc138\uc9c0 \ucc98\ub9ac\ub3c4\uc911 \uc2e4\ud328(CRBT Server)");
        }
        html.append("</body></html>");
        return html.toString();
    }

    private String codeHtml(boolean oth, String did, String code, String fy, String fm, String fd,
                            String ty, String tm, String td, boolean excel, String excelAction,
                            String dbtable, String cclass, String orderCol, List<List<Object>> rows) {
        String title = oth ? "\ub9c1 & \ubca8 \ud1b5\uacc4" : "\uc74c\uc6d0 \ucf54\ub4dc\ubcc4 \ud1b5\uacc4";
        String kind = "ALL";
        if ("SNDREG".equals(code)) {
            kind = "\uc74c\uc6d0\ub4f1\ub85d";
        } else if ("SNDINS".equals(code)) {
            kind = "\uc74c\uc6d0\uc120\ubb3c";
        } else if ("REGINS".equals(code)) {
            kind = "\uc74c\uc6d0\uc800\uc7a5";
        } else if (oth) {
            kind = "\ub4f1\ub85d+\uc120\ubb3c";
        }
        StringBuilder html = new StringBuilder();
        html.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">");
        html.append("<title>CRBT CP SERVER \ud1b5\uacc4</title>");
        html.append("<link rel=\"StyleSheet\" type=\"text/css\" href=\"/aspstatic/default.css\"></head><body>");
        if (!excel) {
            html.append("<form action='").append(excelAction).append("' method=post name='sort_list'>");
            hidden(html, "code", code);
            hidden(html, "did", did);
            hidden(html, "dbtable", dbtable);
            hidden(html, "from_year", fy);
            hidden(html, "from_month", fm);
            hidden(html, "from_day", fd);
            hidden(html, "to_year", ty);
            hidden(html, "to_month", tm);
            hidden(html, "to_day", td);
            hidden(html, "order_col", orderCol);
            hidden(html, "cclass", cclass);
            html.append("</form>");
        }
        html.append("<br><p align=center><font color='#2f4f4f' size='5'><strong>").append(title).append("</strong></font></p>");
        period(html, fy, fm, fd, ty, tm, td);
        int numbers = oth ? 2 : 5;
        int minSize = oth ? 10 : 11;
        if (rows.isEmpty() || rows.get(0).size() < minSize) {
            if (!rows.isEmpty() && rows.get(0).size() < minSize) {
                html.append("<br><br><b><font size=2 color=red>&nbsp;&nbsp;").append(text(String.valueOf(rows.get(0).get(0)))).append("</font></b>");
            } else {
                noData(html);
            }
            html.append("</body></html>");
            return html.toString();
        }
        html.append("<table border='0' width='98%'><tr><td colspan=").append(oth ? 6 : 7).append(">CP : ");
        html.append(text(did)).append("(").append(kind).append(")</td><td></td>");
        if (!excel) {
            html.append("<td colspan=3 align=right><b><a href=javascript:document.sort_list.submit()><font color=blue>");
            html.append(ResultTable.EXCEL_LABEL).append("</font></a></b></td>");
        }
        html.append("</tr><tr align='center' bgcolor='#e6e6fa' height='30'>");
        html.append("<td rowspan=2>CODE</td>");
        if (oth) {
            html.append("<td rowspan=2>130\uc6d0 \ud560\uc778</td><td rowspan=2>150\uc6d0 \ud560\uc778</td>");
        } else {
            html.append("<td rowspan=2>WEB</td><td rowspan=2>ARS</td><td rowspan=2>WAP</td><td rowspan=2>PDA</td>");
        }
        html.append("<td rowspan=2>TOTAL</td><td rowspan=2>\ubd84\ub958\ucf54\ub4dc</td><td rowspan=2>TITLE</td><td rowspan=2>AUTHOR</td>");
        html.append("<td colspan=2>\uc74c\uc6d0\uc815\ubcf4</td></tr>");
        html.append("<tr align='center' bgcolor='#e6e6fa'><td>\uc81c\uacf5\uc0ac</td><td>\ubcf4\uc720\uc0ac</td></tr>");
        NumberFormat format = NumberFormat.getInstance(Locale.KOREA);
        long[] grand = new long[numbers];
        for (List<Object> row : rows) {
            html.append("<tr align=center bgcolor='#fdf5e6'><td bgcolor='#e6e6fa'>").append(text(String.valueOf(row.get(0)))).append("</td>");
            for (int i = 1; i <= numbers; i++) {
                long value = longValue(row.get(i));
                grand[i - 1] += value;
                html.append("<td align=right>").append(format.format(value)).append("</td>");
            }
            int totalIndex = oth ? 4 : 5;
            int textIndex = oth ? 5 : 6;
            html.append("<td bgcolor='#e6e6fa' align=right>").append(format.format(longValue(row.get(totalIndex)))).append("</td>");
            for (int i = textIndex; i <= textIndex + 4 && i < row.size(); i++) {
                html.append("<td bgcolor='#e6e6fa' align=left>").append(text(row.get(i) == null ? "" : String.valueOf(row.get(i)))).append("</td>");
            }
            html.append("</tr>");
        }
        html.append("<tr bgcolor='#e6e6fa' height='30'><td align=center><b>T O T A L</b></td>");
        for (int i = 0; i < numbers; i++) {
            html.append("<td align=right>").append(format.format(grand[i])).append("</td>");
        }
        html.append("</tr></table></body></html>");
        return html.toString();
    }

    private void period(StringBuilder html, String fy, String fm, String fd, String ty, String tm, String td) {
        html.append("<p align=center><font size='2'>(").append(text(fy)).append(".").append(text(fm)).append(".").append(text(fd));
        html.append(" - ").append(text(ty)).append(".").append(text(tm)).append(".").append(text(td)).append(")</font></p><br>");
    }

    private void noData(StringBuilder html) {
        html.append("<br><br><b><font size=2 color=red>&nbsp;&nbsp;DATA \uac00\uc5c6\uc2b5\ub2c8\ub2e4</font></b>");
    }

    private void hidden(StringBuilder html, String name, String value) {
        html.append("<input type=hidden name=").append(name).append(" value=\"").append(text(value)).append("\">");
    }

    private String serviceSums(String code) {
        if ("SNDREG".equals(code)) {
            return "SUM(F1),SUM(F2),SUM(F3),SUM(F4),SUM(F1+F2+F3+F4)";
        }
        if ("SNDINS".equals(code)) {
            return "SUM(F5),SUM(F6),SUM(F7),SUM(F8),SUM(F5+F6+F7+F8)";
        }
        if ("REGINS".equals(code)) {
            return "SUM(F9),SUM(F10),SUM(F11),SUM(F12),SUM(F9+F10+F11+F12)";
        }
        return "SUM(F1+F5+F9),SUM(F2+F6+F10),SUM(F3+F7+F11),SUM(F4+F8+F12),"
                + "SUM(F1+F5+F9+F2+F6+F10+F3+F7+F11+F4+F8+F12)";
    }

    private String portalCode(String code) {
        if ("QUERY".equals(code) || "SETTING".equals(code)) {
            return code;
        }
        return "DOWNLOAD";
    }

    private String errorCode(String code) {
        if (ResultTable.oneOf(code, "ESNDREG", "ESNDINS", "ESVRQRY", "ESNDQRY", "ESNDIND", "ESNDUNR")) {
            return code;
        }
        return "ESNDREG";
    }

    private String portalDid(boolean portal, String id) {
        if (!portal) {
            return "AND DID=?";
        }
        try {
            if (Long.parseLong(id) > 1000L) {
                return "AND DID=?";
            }
        } catch (NumberFormatException ignored) {
            return "AND DID=?";
        }
        return "AND floor(DID/10)=?";
    }

    private String numericId(String value, String fallback) {
        if (value != null && value.matches("\\d+")) {
            return value;
        }
        if (fallback != null && fallback.matches("\\d+")) {
            return fallback;
        }
        return "0";
    }

    private String codeFields(String code) {
        if ("SNDREG".equals(code)) {
            return "SUM(F1),SUM(F2),SUM(F3),SUM(F4),SUM(F1+F2+F3+F4)";
        }
        if ("SNDINS".equals(code)) {
            return "SUM(F5),SUM(F6),SUM(F7),SUM(F8),SUM(F5+F6+F7+F8)";
        }
        if ("REGINS".equals(code)) {
            return "SUM(F9),SUM(F10),SUM(F11),SUM(F12),SUM(F9+F10+F11+F12)";
        }
        return "SUM(F1+F5+F9),SUM(F2+F6+F10),SUM(F3+F7+F11),SUM(F4+F8+F12),SUM(F1+F5+F9+F2+F6+F10+F3+F7+F11+F4+F8+F12)";
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

    private String groupSql(String terms) {
        return " GROUP BY " + groupBy(terms);
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

    private String cpName(String did) {
        if (did == null || !did.matches("\\d+")) {
            return "";
        }
        List<List<Object>> rows = ResultTable.query(jdbc, "select CP_NAME from cp_id where CP_ID=?", did);
        if (rows.isEmpty() || rows.get(0).isEmpty() || rows.get(0).get(0) == null) {
            return "";
        }
        return String.valueOf(rows.get(0).get(0));
    }

    private String text(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;").replace("<", "&lt;").replace("\"", "");
    }
}
