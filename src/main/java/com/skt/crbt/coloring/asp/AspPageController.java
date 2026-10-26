package com.skt.crbt.coloring.asp;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
public class AspPageController {

    private final JdbcTemplate jdbc;

    public AspPageController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/aspstatic/admin_menu")
    public void menu(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!CookieSupport.has(request, CookieNames.ADMIN_ID)) {
            HtmlWriter.write(response, "<script>location.replace('index.html');</script>");
            return;
        }
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        int year = yesterday.get(Calendar.YEAR);
        int month = yesterday.get(Calendar.MONTH) + 1;
        int day = yesterday.get(Calendar.DATE);
        StringBuilder html = new StringBuilder();
        html.append("<html><head>");
        html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">");
        html.append("<title>\uceec\ub7ec\ub9c1 \ud1b5\uacc4</title>");
        html.append(HtmlWriter.CSS_LINK);
        html.append("<script language=\"JavaScript\">");
        html.append("function PutTerm(t){document.forms[0].terms.value=t;}");
        html.append("function win_select(s){");
        html.append("if(s=='\ud604\uc7ac\ucc3d\uc5d0'){document.forms[0].target='body';document.forms[0].win_sel.value='\uc0c8\ucc3d\uc5d0';}");
        html.append("else{document.forms[0].target='new';document.forms[0].win_sel.value='\ud604\uc7ac\ucc3d\uc5d0';}}");
        html.append("function logout(){var f=document.forms[0];f.target='_top';f.action='logout';f.submit();}");
        html.append("function selMsg(s){document.forms[0].action='admin_static';document.forms[0].dbtable.value='SSRVD';document.forms[0].msg.value=s;document.forms[0].submit();}");
        html.append("function selMsgLong(s){document.forms[0].action='admin_static';document.forms[0].dbtable.value='LONG_SSRVD';document.forms[0].msg.value=s;document.forms[0].submit();}");
        html.append("function goRingnBell(){document.forms[0].action='admin_oth_static';document.forms[0].submit();}");
        html.append("function goCodeRing(){window.open('/aspstatic/codering/index.html');}");
        html.append("function goCopyRing(){window.open('/aspstatic/copyring/index.html');}");
        html.append("function goCDRPortal(){window.open('/cdpstatic/auth');}");
        html.append("function goFreeZone(){window.open('/rankstatic/minwon/freezone_albumring.html');}");
        html.append("function goGiftStatic(){window.open('/static/giftgw/giftstatic/index.html');}");
        html.append("function goStatic2(){window.open('/static2/admin_main.html');}");
        html.append("</script></head><body bgcolor=#FFFFFF>");
        html.append("<form action=admin_static method=POST target=body>");
        html.append("<input type=hidden name=dbtable value=SSRVD>");
        html.append("<input type=hidden name=terms value=day>");
        html.append("<input type=hidden name=msg value=SNDREG>");
        html.append("<input type=hidden name=order value=1>");
        html.append("<input type=hidden name=win_sel value='\uc0c8\ucc3d\uc5d0'>");
        html.append("<br><p align=center><font color='#006400' size=5><strong>");
        html.append("<a href=admin_main.html target=_top>\uceec\ub7ec\ub9c1 \ud1b5\uacc4</a></strong></font></p>");
        html.append("<table border=0 width=100% align=center>");
        html.append("<tr><td align=center>");
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
        html.append("</td></tr></table></td></tr>");
        html.append("<tr><td><br>\u25cf \uc11c\ube44\uc2a4 \uc120\ud0dd<br>");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;. <a href=\"javascript:selMsg('SNDREG')\"><font size=2>\uc74c\uc6d0\ub4f1\ub85d</font></a><br>");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;. <a href=\"javascript:selMsgLong('SNDREG')\"><font size=2>\uc74c\uc6d0\ub4f1\ub85d(Long)</font></a><br>");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;. <a href=\"javascript:selMsg('SNDINS')\"><font size=2>\uc74c\uc6d0\uc120\ubb3c</font></a><br>");
        html.append("<br>\u25cf <a href=\"javascript:goRingnBell()\">\ub9c1 & \ubca8 \ud1b5\uacc4</a><br>");
        html.append("<br>\u25cf <a href=\"javascript:goCodeRing()\">\ucf54\ub4dc\ub9c1 \ud1b5\uacc4</a><br>");
        html.append("<br>\u25cf <a href=\"javascript:goCopyRing()\">\uce74\ud53c\ub9c1 \ud1b5\uacc4</a><br>");
        html.append("<br>\u25cf <a href=\"javascript:goCDRPortal()\">\ucf54\ub4dc\ub9c1 \ud3ec\ud0c8</a><br>");
        html.append("<br>\u25cf <a href=\"javascript:goFreeZone()\">\ud504\ub9ac\uc874/\uc568\ubc94\ub9c1 \ud1b5\uacc4</a><br>");
        html.append("<br>\u25cf <a href=\"javascript:goStatic2()\"><b>\uc774\uc804 \ud1b5\uacc4 UI</b></a><br><br>");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;<font color=blue><strong><a href=javascript:logout()>LOGOUT</a></strong></font>");
        html.append("</td></tr></table></form></body></html>");
        HtmlWriter.write(response, html.toString());
    }

    @GetMapping("/aspstatic/user_menu")
    public void userMenu(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!CookieSupport.has(request, CookieNames.ADMIN_ID)) {
            HtmlWriter.write(response, "<script>location.replace('index.html');</script>");
            return;
        }
        Calendar prev = Calendar.getInstance();
        prev.add(Calendar.MONTH, -1);
        int inputYear = prev.get(Calendar.YEAR);
        Calendar now = Calendar.getInstance();
        Calendar selected = (Calendar) now.clone();
        if (now.get(Calendar.DATE) <= 25) {
            selected.add(Calendar.MONTH, -1);
        }
        int selectMonth = selected.get(Calendar.MONTH) + 1;
        StringBuilder html = new StringBuilder();
        html.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">");
        html.append("<title>\uceec\ub7ec\ub9c1 \ud1b5\uacc4</title>");
        html.append(HtmlWriter.CSS_LINK);
        html.append("<script language=\"JavaScript\">");
        html.append("function goUserStatic(){");
        html.append("document.forms[0].year.value=document.forms[0].input_year.value;");
        html.append("document.forms[0].month.value=document.forms[0].input_month.value;");
        html.append("if(parseInt(document.forms[0].year.value)<2005");
        html.append("||(parseInt(document.forms[0].year.value)==2005&&document.forms[0].month.value<7)){");
        html.append("alert('\uc774 \ud1b5\uacc4\ub294 2005\ub144 7\uc6d4 \ubd80\ud130 \ub370\uc774\ud130\uac00 \uc874\uc7ac\ud569\ub2c8\ub2e4.');return;}");
        html.append("document.forms[0].action='user_static';document.forms[0].submit();}");
        html.append("</script></head><body bgcolor=#FFFFFF>");
        html.append("<form action=user_static method=POST target=body>");
        html.append("<input type=hidden name=year><input type=hidden name=month>");
        html.append("<br><br><p align=center><font color='#2f4f4f' size=5><strong>CP \ubcc4 UNIQUE<br>USER \ud1b5\uacc4</strong></font></p>");
        html.append("<table><tr><td align=left>\u25cf ");
        html.append("<input type=text size=4 maxlength=4 name=input_year value=").append(inputYear).append(">\ub144 ");
        html.append("<select name=input_month size=1>").append(numOptions(12, selectMonth)).append("</select>\uc6d4");
        html.append("</td></tr><tr><td><b><a href='javascript:goUserStatic()'>Unique User Static Search</a></b></td></tr>");
        html.append("</table></form></body></html>");
        HtmlWriter.write(response, html.toString());
    }

    @PostMapping({"/aspstatic/user_static", "/aspstatic/user_excel"})
    public void userStatic(@RequestParam(value = "year", required = false) String year,
                           @RequestParam(value = "month", required = false) String month,
                           @RequestParam(value = "order_flag", required = false) String orderFlag,
                           HttpServletRequest request,
                           HttpServletResponse response) throws IOException {
        String y = year != null && year.matches("(19|20)\\d{2}") ? year : String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        String m = month != null && month.matches("0[1-9]|1[0-2]") ? month : "01";
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        String table = String.valueOf(thisYear).equals(y) ? "static_unique_user" : "static_unique_user_" + y;
        String sum = "m" + m + "_wap + m" + m + "_web + m" + m + "_ars + m" + m + "_code_ars + m" + m + "_code_potal";
        String where = " where cp_id != 210 and cp_id != 240 and cp_id != 290 and cp_id != 260 and cp_id != 300 and cp_id != 310";
        String order = userOrder(orderFlag, m, sum);
        String sql = "SELECT cp_id, m" + m + "_wap, m" + m + "_web, m" + m + "_ars, m" + m + "_code_ars, m" + m
                + "_code_potal, " + sum + " FROM " + table + where + order;
        List<List<Object>> rows = ResultTable.query(jdbc, sql);
        Map<String, String> names = cpNames();
        boolean excel = request.getRequestURI().indexOf("excel") >= 0;
        HtmlWriter.send(response, userTable(y, m, orderFlag, rows, names, excel), excel, "UNIQUE_USER_STATIC.xls");
    }

    @PostMapping({"/aspstatic/admin_static", "/aspstatic/admin_static_excel"})
    public void stat(HttpServletRequest request,
                     @RequestParam(value = "dbtable", required = false) String dbtable,
                     @RequestParam(value = "msg", required = false) String msg,
                     @RequestParam(value = "order", required = false) String order,
                     @RequestParam(value = "from_year", required = false) String fy,
                     @RequestParam(value = "from_month", required = false) String fm,
                     @RequestParam(value = "from_day", required = false) String fd,
                     @RequestParam(value = "to_year", required = false) String ty,
                     @RequestParam(value = "to_month", required = false) String tm,
                     @RequestParam(value = "to_day", required = false) String td,
                     HttpServletResponse response) throws IOException {
        if (!ResultTable.oneOf(dbtable, "SSRVD", "LONG_SSRVD")) {
            dbtable = "SSRVD";
        }
        String fields = fields(msg);
        int orderCol = orderCol(order);
        String orderSql = orderCol > 1 ? orderCol + " desc" : String.valueOf(orderCol);
        String from = ResultTable.ymd(fy, fm, fd, "000000");
        String to = ResultTable.ymd(ty, tm, td, "230000");
        String sql = "SELECT did," + fields + " FROM " + dbtable
                + " WHERE DATE>=? AND DATE<=? AND CODE='DOWNLOAD' AND DID >= 100 GROUP BY DID ORDER BY " + orderSql;
        List<List<Object>> rows = ResultTable.query(jdbc, sql, from, to);
        boolean excel = request.getRequestURI().indexOf("excel") >= 0;
        String file = "SNDREG".equals(msg) ? "MUSIC_REGISTER_STATIC.xls" : "MUSIC_REGISTER_STATIC_ALL.xls";
        String body = cpStatHtml(statTitle(msg), "admin_static", "admin_static_excel", excel,
                dbtable, msg, order, fy, fm, fd, ty, tm, td, rows, false);
        HtmlWriter.send(response, body, excel, file);
    }

    @PostMapping({"/aspstatic/admin_oth_static", "/aspstatic/admin_oth_static_excel"})
    public void oth(HttpServletRequest request,
                    @RequestParam(value = "order", required = false) String order,
                    @RequestParam(value = "from_year", required = false) String fy,
                    @RequestParam(value = "from_month", required = false) String fm,
                    @RequestParam(value = "from_day", required = false) String fd,
                    @RequestParam(value = "to_year", required = false) String ty,
                    @RequestParam(value = "to_month", required = false) String tm,
                    @RequestParam(value = "to_day", required = false) String td,
                    HttpServletResponse response) throws IOException {
        int orderCol = orderCol(order);
        if (orderCol > 6) {
            orderCol = 2;
        }
        String orderSql = orderCol > 1 ? orderCol + " desc" : String.valueOf(orderCol);
        String from = ResultTable.ymd(fy, fm, fd, "000000");
        String to = ResultTable.ymd(ty, tm, td, "230000");
        String sql = "SELECT did, SUM(F1+F2+F3+F4+F5+F6+F7+F8+F9+F10+F11+F12),"
                + " SUM(F1+F5+F9), SUM(F2+F6+F10), SUM(F3+F7+F11), SUM(F4+F8+F12)"
                + " FROM OTH_SSRVD WHERE DATE>=? AND DATE<=? AND CODE='DOWNLOAD' GROUP BY DID ORDER BY " + orderSql;
        boolean excel = request.getRequestURI().indexOf("excel") >= 0;
        String body = cpStatHtml("\ub9c1 & \ubca8 \ud1b5\uacc4", "admin_oth_static", "admin_oth_static_excel", excel,
                "OTH_SSRVD", "", order, fy, fm, fd, ty, tm, td,
                ResultTable.query(jdbc, sql, from, to), true);
        HtmlWriter.send(response, body, excel, "RING&BELL_STATIC.xls");
    }

    @PostMapping("/aspstatic/logout")
    public void logout(HttpServletResponse response) throws IOException {
        ScriptRedirect.logoutHome(response);
    }

    private int orderCol(String order) {
        try {
            int value = Integer.parseInt(order);
            if (value >= 1 && value <= 12) {
                return value;
            }
        } catch (Exception ignored) {
            // default
        }
        return 1;
    }

    private String statTitle(String msg) {
        if ("SNDINS".equals(msg)) {
            return "\uc74c\uc6d0 \uc120\ubb3c \ud1b5\uacc4";
        }
        if ("REGINS".equals(msg)) {
            return "\uc74c\uc6d0 \ub4f1\ub85d&\uc120\ubb3c \ud1b5\uacc4";
        }
        if ("ALL".equals(msg)) {
            return "\uc74c\uc6d0 \ub4f1\ub85d \ud1b5\uacc4(ALL)";
        }
        return "\uc74c\uc6d0 \ub4f1\ub85d \ud1b5\uacc4";
    }

    private String cpStatHtml(String title, String action, String excelAction, boolean excel,
                              String dbtable, String msg, String order,
                              String fy, String fm, String fd, String ty, String tm, String td,
                              List<List<Object>> rows, boolean byDid) {
        Map<String, String> names = cpNames();
        final Map<String, long[]> values = new HashMap<String, long[]>();
        String error = null;
        for (List<Object> row : rows) {
            if (row.size() < 6) {
                if (error == null && !row.isEmpty() && row.get(0) != null) {
                    error = String.valueOf(row.get(0));
                }
                continue;
            }
            String id = String.valueOf(row.get(0));
            if (!names.containsKey(id)) {
                continue;
            }
            long[] item = new long[5];
            for (int i = 0; i < 5; i++) {
                item[i] = longValue(row.get(i + 1));
            }
            values.put(id, item);
        }
        final int sortCol = orderCol(order);
        List<String> ids = new ArrayList<String>(names.keySet());
        Collections.sort(ids, new Comparator<String>() {
            @Override
            public int compare(String left, String right) {
                if (byDid || sortCol <= 1) {
                    return compareId(left, right);
                }
                int cmp = compareLong(cell(values, right, sortCol - 2), cell(values, left, sortCol - 2));
                return cmp != 0 ? cmp : compareId(left, right);
            }
        });
        List<String> ranked = new ArrayList<String>(names.keySet());
        Collections.sort(ranked, new Comparator<String>() {
            @Override
            public int compare(String left, String right) {
                int cmp = compareLong(cell(values, right, 0), cell(values, left, 0));
                return cmp != 0 ? cmp : compareId(left, right);
            }
        });
        Map<String, Integer> rank = new HashMap<String, Integer>();
        int place = 0;
        int zeroRank = 1;
        for (String id : ranked) {
            if (cell(values, id, 0) == 0L) {
                rank.put(id, Integer.valueOf(zeroRank));
            } else {
                place++;
                zeroRank = place + 1;
                rank.put(id, Integer.valueOf(place));
            }
        }
        long[] channelTotal = new long[5];
        for (String id : names.keySet()) {
            for (int i = 0; i < 5; i++) {
                channelTotal[i] += cell(values, id, i);
            }
        }
        StringBuilder html = new StringBuilder();
        html.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">");
        html.append("<title>\uceec\ub7ec\ub9c1 \ud1b5\uacc4</title>");
        html.append("<link rel=\"StyleSheet\" type=\"text/css\" href=\"/aspstatic/default.css\">");
        html.append("<script language=javascript>");
        html.append("function excel(){document.forms[0].action='").append(excelAction).append("';document.forms[0].submit();}");
        html.append("function setOrder(num){document.forms[0].order.value=num;document.forms[0].action='");
        html.append(action).append("';document.forms[0].submit();}");
        html.append("</script></head><body>");
        html.append("<br><p align=center><font color='#2f4f4f' size='5'><strong>").append(title).append("</strong></font><br>");
        html.append("<br><font size='2'>(").append(text(fy)).append(".").append(text(fm)).append(".").append(text(fd));
        html.append(" - ").append(text(ty)).append(".").append(text(tm)).append(".").append(text(td)).append(")</font></p><br>");
        html.append("<center><form action='").append(action).append("' method='post'>");
        hidden(html, "dbtable", dbtable);
        hidden(html, "from_year", fy);
        hidden(html, "from_month", fm);
        hidden(html, "from_day", fd);
        hidden(html, "to_year", ty);
        hidden(html, "to_month", tm);
        hidden(html, "to_day", td);
        hidden(html, "order", order);
        hidden(html, "msg", msg);
        html.append("</form>");
        if (error != null) {
            html.append("<b><font size=2 color=red>").append(text(error)).append("</font></b>");
        }
        if (!excel) {
            html.append("<table border='0' width='85%'><tr><td colspan=3 align=right><b><a href=javascript:excel()>");
            html.append("<font color=blue>").append(ResultTable.EXCEL_LABEL).append("</font></a></b></td></tr></table>");
        }
        html.append("<table border='0' width='85%' cellpadding='5'>");
        html.append("<tr align='center' bgcolor='#e6e6fa' height='25'>");
        html.append("<td rowspan=2>\uc5c5\uccb4\uba85</td>");
        html.append("<td rowspan=2><a href=javascript:setOrder(2)>\uc21c\uc704</a></td>");
        html.append("<td colspan=2><a href=javascript:setOrder(2)>\uc804\uccb4</a></td>");
        html.append("<td colspan=2><a href=javascript:setOrder(3)>WEB</a></td>");
        html.append("<td colspan=2><a href=javascript:setOrder(4)>ARS</a></td>");
        html.append("<td colspan=2><a href=javascript:setOrder(5)>WAP</a></td>");
        html.append("<td colspan=2><a href=javascript:setOrder(6)>PDA</a></td></tr>");
        html.append("<tr align='center' bgcolor='#e6e6fa' height='25'>");
        for (int i = 0; i < 5; i++) {
            html.append("<td>\uc0ac\uc6a9\ub7c9</td><td>%</td>");
        }
        html.append("</tr>");
        NumberFormat format = NumberFormat.getInstance(Locale.KOREA);
        long[] shown = new long[5];
        if (ids.isEmpty() && error == null) {
            html.append("<tr><td colspan=12><b><font size=2 color=red>&nbsp;&nbsp;DATA \uac00\uc5c6\uc2b5\ub2c8\ub2e4</font></b></td></tr>");
        }
        for (String id : ids) {
            html.append("<tr height='15' bgcolor='#fdf5e6'>");
            html.append("<td align='left' bgcolor='#e6e6fa'>&nbsp;&nbsp;").append(text(names.get(id))).append("</td>");
            html.append("<td align='center' bgcolor='#e6e6fa'>").append(rank.get(id)).append("</td>");
            for (int i = 0; i < 5; i++) {
                long value = cell(values, id, i);
                shown[i] += value;
                html.append("<td align='right'>").append(format.format(value)).append("</td>");
                html.append("<td align='right' bgcolor='#e6e6ef'>").append(share(value, channelTotal[i])).append("</td>");
            }
            html.append("</tr>");
        }
        html.append("<tr height='30' bgcolor='#e6e6fa'><td colspan='2' align='center'>T O T A L</td>");
        for (int i = 0; i < 5; i++) {
            html.append("<td align='right'>").append(format.format(shown[i])).append("</td>");
            html.append("<td align='right' bgcolor='#e6e6ef'>").append(share(shown[i], channelTotal[0])).append("</td>");
        }
        html.append("</tr><tr><td colspan=12><br><br>");
        html.append("&nbsp;&nbsp;\ucc38\uace0) % : TOTAL \uac74\uc218\uc5d0 \ub300\ud55c \uc810\uc720\uc728<br>");
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; \uc21c\uc704\ub294 \uc804\uccb4 \uac74\uc218\ub97c \uae30\uc900\uc73c\ub85c \ud569\ub2c8\ub2e4.");
        html.append("</td></tr></table></center></body></html>");
        return html.toString();
    }

    private long cell(Map<String, long[]> values, String id, int index) {
        long[] item = values.get(id);
        if (item == null || index < 0 || index >= item.length) {
            return 0L;
        }
        return item[index];
    }

    private int compareId(String left, String right) {
        return compareLong(longId(left), longId(right));
    }

    private int compareLong(long left, long right) {
        return left < right ? -1 : (left > right ? 1 : 0);
    }

    private long longId(String id) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private int share(long part, long whole) {
        if (part == 0L || whole == 0L) {
            return 0;
        }
        return (int) Math.round(part * 100.0d / whole);
    }

    private void hidden(StringBuilder html, String name, String value) {
        html.append("<input type=hidden name=").append(name).append(" value=\"").append(text(value)).append("\">");
    }

    private String fields(String msg) {
        if ("SNDREG".equals(msg)) {
            return "SUM(F1+F2+F3+F4),SUM(F1),SUM(F2),SUM(F3),SUM(F4)";
        }
        if ("SNDINS".equals(msg)) {
            return "SUM(F5+F6+F7+F8),SUM(F5),SUM(F6),SUM(F7),SUM(F8)";
        }
        if ("REGINS".equals(msg)) {
            return "SUM(F9+F10+F11+F12),SUM(F9),SUM(F10),SUM(F11),SUM(F12)";
        }
        return "SUM(F1+F2+F3+F4+F5+F6+F7+F8+F9+F10+F11+F12),SUM(F1+F5+F9),SUM(F2+F6+F10),SUM(F3+F7+F11),SUM(F4+F8+F12)";
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

    private String userOrder(String flag, String month, String sum) {
        if ("0".equals(flag)) {
            return " ORDER BY cp_id";
        }
        if ("2".equals(flag)) {
            return " ORDER BY m" + month + "_wap desc";
        }
        if ("3".equals(flag)) {
            return " ORDER BY m" + month + "_web desc";
        }
        if ("4".equals(flag)) {
            return " ORDER BY m" + month + "_ars desc";
        }
        if ("5".equals(flag)) {
            return " ORDER BY m" + month + "_code_ars desc";
        }
        if ("6".equals(flag)) {
            return " ORDER BY m" + month + "_code_potal desc";
        }
        return " ORDER BY " + sum + " desc, cp_id asc";
    }

    private Map<String, String> cpNames() {
        Map<String, String> names = new HashMap<String, String>();
        List<List<Object>> rows = ResultTable.query(jdbc,
                "select distinct CP_ID, DESCRIPTION from cp_id where cp_id != 210 and cp_id != 240 and cp_id != 290"
                        + " and cp_id != 260 and cp_id != 300 and cp_id != 310 order by cp_id");
        for (List<Object> row : rows) {
            if (row.size() >= 2 && row.get(0) != null && row.get(1) != null) {
                names.put(String.valueOf(row.get(0)), String.valueOf(row.get(1)));
            }
        }
        return names;
    }

    private String userTable(String year, String month, String orderFlag, List<List<Object>> rows,
                             Map<String, String> names, boolean excel) {
        NumberFormat format = NumberFormat.getInstance(Locale.KOREA);
        Map<String, Integer> rank = new HashMap<String, Integer>();
        long[] total = new long[7];
        int n = 0;
        for (List<Object> row : rows) {
            if (row.size() < 7) {
                continue;
            }
            String id = String.valueOf(row.get(0));
            if (!names.containsKey(id)) {
                continue;
            }
            rank.put(id, Integer.valueOf(++n));
            for (int i = 1; i <= 6; i++) {
                total[i] += longValue(row.get(i));
            }
        }
        StringBuilder html = new StringBuilder();
        html.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">");
        html.append(HtmlWriter.CSS_LINK);
        html.append("<script language=javascript>");
        html.append("function excel(){document.user_static.action='user_excel';document.user_static.submit();}");
        html.append("function setOrder(num){document.user_static.order_flag.value=num;document.user_static.submit();}");
        html.append("</script></head><body><center>");
        html.append("<form action=user_static method=post name=user_static>");
        html.append("<input type=hidden name=year value=").append(year).append(">");
        html.append("<input type=hidden name=month value=").append(month).append(">");
        html.append("<input type=hidden name=order_flag value=\"").append(orderFlag == null ? "" : orderFlag).append("\">");
        html.append("<br><p align=center><font color='#2f4f4f' size=5><strong>CP \ubcc4 UNIQUE-USER \ud1b5\uacc4</strong></font><br>");
        html.append("<font size=2>(").append(year).append("\ub144").append(month).append("\uc6d4 \ud1b5\uacc4)</font></p>");
        if (!excel) {
            html.append("<table border=0 width=85%><tr><td colspan=3 align=right><b><a href=javascript:excel()>");
            html.append("<font color=blue>").append(ResultTable.EXCEL_LABEL).append("</font></a></b></td></tr></table>");
        }
        html.append("<table border=0 width=85% cellpadding=5>");
        html.append("<tr align=center bgcolor='#e6e6fa' height=25>");
        html.append("<td rowspan=2><a href=javascript:setOrder(0)>\uc5c5\uccb4\uba85</a></td>");
        html.append("<td rowspan=2><a href=javascript:setOrder(1)>\uc21c\uc704</a></td>");
        html.append("<td colspan=2><a href=javascript:setOrder(2)>WAP</a></td>");
        html.append("<td colspan=2><a href=javascript:setOrder(3)>WEB</a></td>");
        html.append("<td colspan=2><a href=javascript:setOrder(4)>ARS</a></td>");
        html.append("<td colspan=2><a href=javascript:setOrder(5)>CODE_ARS</a></td>");
        html.append("<td colspan=2><a href=javascript:setOrder(6)>CODE_PORTAL</a></td>");
        html.append("<td colspan=2><a href=javascript:setOrder(7)>TOTAL</a></td></tr>");
        html.append("<tr align=center bgcolor='#e6e6fa' height=25>");
        for (int i = 0; i < 6; i++) {
            html.append("<td>\uc0ac\uc6a9\ub7c9</td><td>%</td>");
        }
        html.append("</tr>");
        for (List<Object> row : rows) {
            if (row.size() < 7) {
                html.append("<tr><td colspan=14 align=left>").append(text(row.get(0))).append("</td></tr>");
                continue;
            }
            String id = String.valueOf(row.get(0));
            if (!names.containsKey(id)) {
                continue;
            }
            html.append("<tr height=15 bgcolor='#fdf5e6'>");
            html.append("<td align=left bgcolor='#e6e6fa'>&nbsp;&nbsp;").append(text(names.get(id))).append("</td>");
            html.append("<td align=center bgcolor='#e6e6fa'>").append(rank.get(id)).append("</td>");
            for (int i = 1; i <= 6; i++) {
                long value = longValue(row.get(i));
                long rate = total[i] == 0L ? 0L : Math.round(value * 100.0d / total[i]);
                html.append("<td align=right>").append(format.format(value)).append("</td>");
                html.append("<td align=right bgcolor='#e6e6ef'>").append(rate).append("</td>");
            }
            html.append("</tr>");
        }
        html.append("<tr height=15 bgcolor='#fdf5e6'><td align=center bgcolor='#e6e6fa' colspan=2>TOTAL</td>");
        for (int i = 1; i <= 5; i++) {
            long rate = total[6] == 0L ? 0L : Math.round(total[i] * 100.0d / total[6]);
            html.append("<td align=right>").append(format.format(total[i])).append("</td>");
            html.append("<td align=right bgcolor='#e6e6ef'>\u2191100<br>\u2192").append(rate).append("</td>");
        }
        html.append("<td align=right>").append(format.format(total[6])).append("</td>");
        html.append("<td align=right bgcolor='#e6e6ef'>\u2191100<br>\u2192100</td></tr>");
        html.append("<tr><td colspan=14><br>&nbsp;&nbsp;% : TOTAL \uac74\uc218\uc5d0 \ub300\ud55c \uc810\uc720\uc728<br>");
        html.append("&nbsp;&nbsp;\uc21c\uc704\ub294 \uc804\uccb4 \uac74\uc218\ub97c \uae30\uc900\uc73c\ub85c \ud569\ub2c8\ub2e4.</td></tr>");
        html.append("</table></form></center></body></html>");
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
        return String.valueOf(value).replace("&", "&amp;").replace("<", "&lt;");
    }
}
