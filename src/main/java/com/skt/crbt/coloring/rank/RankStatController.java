package com.skt.crbt.coloring.rank;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.skt.crbt.coloring.common.HtmlWriter;
import com.skt.crbt.coloring.common.ResultTable;

@Controller
public class RankStatController {

    private static final String SET = "\uc124\uc815";
    private static final String GIFT = "\uc120\ubb3c";
    private static final String ALL = "\uc804\uccb4";
    private static final String CHANNEL = "\ucc44\ub110";
    private static final String SERVICE = "\uc11c\ube44\uc2a4";
    private static final String STAT = "\ud1b5\uacc4";
    private static final String RANK_LABEL = "\uc21c\uc704";
    private static final String SUM = "\ud569\uacc4";
    private static final String NORMAL = "\uc77c\ubc18";
    private static final String NORMAL_T = "\uc77c\ubc18(T\ub9c1)";
    private static final String LONG_T = "LONG(T\ub9c1)";
    private static final String HQ = "\uace0\uc74c\uc9c8";
    private static final String OUTSIDE = "\uc678\ubd80\uc5f0\ub3d9";
    private static final String ETC = "\uae30\ud0c0";
    private static final String USAGE = "\uc0ac\uc6a9\ub7c9";
    private static final String USER_STAT = "\uc0ac\uc6a9\uc790\ud1b5\uacc4";
    private static final String DATE_LABEL = "\ub0a0\uc9dc";
    private static final String REGIST_LABEL = "\uac00\uc785\uc218";
    private static final String UNREGIST_LABEL = "\ud574\uc9c0\uc790\uc218";
    private static final String DIFF_LABEL = "\uac00\uc785-\ud574\uc9c0";
    private static final String MEMBER_LABEL = "\ud604\uc7ac \uac00\uc785\ud68c\uc6d0\uc218";
    private static final String USER_NOTE = "\u203b \uc0ac\uc6a9\uc790 \ud1b5\uacc4\ub294 '\uc124\uc815 \uace0\uac1d \uc720\ub2c8\ud06c \ud1b5\uacc4 + \uc120\ubb3c\ubc1b\uc740 \uace0\uac1d \uc720\ub2c8\ud06c \ud1b5\uacc4' \uc785\ub2c8\ub2e4.<br>"
            + "&nbsp;&nbsp;&nbsp;ex.1) A\uace0\uac1d\uc774 2\uac74 \uceec\ub7ec\ub9c1 \uc124\uc815\ud558\uba74 \uc0ac\uc6a9\uc790 \ud1b5\uacc4\ub294 1\uba85 <br>"
            + "&nbsp;&nbsp;&nbsp;ex.2) B\uace0\uac1d\uc774 C, D, E \uace0\uac1d\uc5d0\uac8c \uceec\ub7ec\ub9c1 2\uac74\uc2dd \uc120\ubb3c\ud558\uba74 \uc0ac\uc6a9\uc790 \ud1b5\uacc4\ub294 3\uba85";
    private static final String NS = "com.skt.crbt.coloring.rank.RankStaticMapper.";

    private final RankQueryService queryService;
    private final CpInfoLoader cpInfoLoader;
    private final JdbcTemplate crbtSts;
    private final JdbcTemplate couplring;

    public RankStatController(RankQueryService queryService, CpInfoLoader cpInfoLoader,
                              JdbcTemplate crbtSts,
                              @Qualifier("couplringJdbcTemplate") JdbcTemplate couplring) {
        this.queryService = queryService;
        this.cpInfoLoader = cpInfoLoader;
        this.crbtSts = crbtSts;
        this.couplring = couplring;
    }

    @PostMapping("/rankstatic/proc3")
    public void proc3(@RequestParam(value = "title", required = false) String title,
                      @RequestParam(value = "syear", required = false) String syear,
                      @RequestParam(value = "smon", required = false) String smon,
                      @RequestParam(value = "sday", required = false) String sday,
                      @RequestParam(value = "eyear", required = false) String eyear,
                      @RequestParam(value = "emon", required = false) String emon,
                      @RequestParam(value = "eday", required = false) String eday,
                      @RequestParam(value = "order", required = false) String order,
                      @RequestParam(value = "group", required = false) String group,
                      @RequestParam(value = "load", required = false) String load,
                      HttpServletResponse response) throws IOException {
        String table = tableOf(title);
        if (table == null) {
            HtmlWriter.write(response, "menu error");
            return;
        }
        boolean modern = year(syear) >= 2008 && year(eyear) >= 2008;
        String sdate = nvl(syear) + nvl(smon) + nvl(sday) + "000000";
        String edate = nvl(eyear) + nvl(emon) + nvl(eday) + "235959";
        String orderBy = orderBy(order);
        String cpFilter = cpFilter(group);
        Map<String, String> cpName = cpInfoLoader.load();
        boolean excel = "excel".equals(load);

        Map<String, Object> param = param(table, sdate, edate, cpFilter, orderBy);
        List<List<Object>> rankRows = queryService.query(NS + (modern ? "selectRank" : "selectRankT"), param);
        Map<String, Integer> rank = new LinkedHashMap<String, Integer>();
        int n = 0;
        for (List<Object> row : rankRows) {
            n++;
            if (num(row, 1) == 0) {
                n--;
            }
            rank.put(str(row, 0), Integer.valueOf(n));
        }
        List<List<Object>> dataRows = queryService.query(NS + (modern ? "selectData" : "selectDataT"), param);
        List<List<Object>> totalRows = queryService.query(NS + (modern ? "selectTotal" : "selectTotalT"), param);
        List<Object> total = totalRows.isEmpty() ? null : totalRows.get(0);

        int border = excel ? 1 : 0;
        StringBuilder html = new StringBuilder();
        pageHead(html, "proc3");
        hidden(html, "title", title);
        hidden(html, "order", orderBy);
        hidden(html, "syear", syear);
        hidden(html, "smon", smon);
        hidden(html, "sday", sday);
        hidden(html, "eyear", eyear);
        hidden(html, "emon", emon);
        hidden(html, "eday", eday);
        hidden(html, "group", group);
        hidden(html, "load", load);
        html.append("</form>");
        html.append("<table border=0 align=center><tr><td align=left>(").append(esc(syear)).append(".").append(esc(smon)).append(".").append(esc(sday));
        html.append(" ~ ").append(esc(eyear)).append(".").append(esc(emon)).append(".").append(esc(eday)).append(") ");
        html.append(esc(title)).append(STAT).append("</td>");
        if (!excel) {
            html.append("<td align=right><b><a href=javascript:excel()><font color=blue>");
            html.append(ResultTable.EXCEL_LABEL);
            html.append("</font></a></b></td>");
        }
        html.append("</tr></table>");
        html.append("<table border=\"0\" cellpadding=\"0\"><tr valign=\"top\"><td align=\"left\" width=559>");
        html.append("<table border='").append(border).append("' cellspacing=\"1\" cellpadding=\"0\" style=\"table-layout:fixed\">");
        html.append("<tr align=center bgcolor='989898' height=\"30\">");
        html.append("<td rowspan=2 width=90><a href=javascript:ordering('CPID')><font color=white>CP</font></a></td>");
        html.append("<td rowspan=2 width=40><a href=javascript:ordering('TOTAL')><font color=white>").append(RANK_LABEL).append("</font></a></td>");
        html.append("<td colspan=").append(modern ? 3 : 5).append(" width=350><a href=javascript:ordering('TOTAL')><font color=white>");
        html.append(SUM).append("</font></a></td>");
        html.append("<td rowspan=2 width=70><a href=javascript:ordering('TOTAL')><font color=white>").append(ALL).append("</font></a></td></tr>");
        html.append("<tr bgcolor='#eeeeee' height=\"30\" align=center>");
        if (modern) {
            html.append("<td>").append(NORMAL).append("</td><td>LONG</td><td>").append(HQ).append("</td>");
        } else {
            html.append("<td>").append(NORMAL).append("</td><td>").append(NORMAL_T).append("</td><td>LONG</td><td>");
            html.append(LONG_T).append("</td><td>").append(HQ).append("</td>");
        }
        html.append("</tr>");
        int line = 1;
        for (List<Object> row : dataRows) {
            html.append("<tr bgcolor='").append(line % 2 == 1 ? "#fbfbfb" : "#f5f5f5").append("'>");
            String cpid = str(row, 0);
            String name = cpName.containsKey(cpid) ? cpName.get(cpid) : cpid;
            html.append("<td align=right height='20' bgcolor='#eeeeee'>").append(esc(name)).append("</td>");
            html.append("<td align=center height='20' bgcolor='#eeeeee'>");
            html.append(rank.containsKey(cpid) ? rank.get(cpid) : "").append("</td>");
            if (modern) {
                cell(html, row, 25, null);
                cell(html, row, 26, null);
                cell(html, row, 27, null);
                cell(html, row, 28, "#eeeeee");
            } else {
                for (int i = 41; i <= 45; i++) {
                    cell(html, row, i, null);
                }
                cell(html, row, 46, "#eeeeee");
            }
            html.append("</tr>");
            line++;
        }
        html.append("<tr bgcolor='#d0d0d0'><td align=center colspan=2 height='20'>").append(SUM).append("</td>");
        if (modern) {
            for (int i = 24; i <= 27; i++) {
                cell(html, total, i, null);
            }
        } else {
            for (int i = 40; i <= 45; i++) {
                cell(html, total, i, null);
            }
        }
        html.append("</tr></table></td><td align=\"left\"><div style=\"width:700px;overflow-x:auto;\">");
        html.append("<table border='").append(border).append("' cellspacing='1' align=center style=\"table-layout:fixed\" width=2800>");
        String[] channels = {"WAP", "WEB", "ARS", "PDA", "CODE", "COPY", "INFO", "PORTAL"};
        html.append("<tr align=center bgcolor='989898' height=\"30\">");
        for (String channel : channels) {
            html.append("<td colspan=").append(modern ? 3 : 5).append("><a href=javascript:ordering('");
            html.append(channel).append("')><font color=white>").append(channel).append("</font></a></td>");
        }
        html.append("</tr><tr bgcolor='#eeeeee' height=\"30\" align=center>");
        for (int i = 0; i < channels.length; i++) {
            if (modern) {
                html.append("<td>").append(NORMAL).append("</td><td>LONG</td><td>").append(HQ).append("</td>");
            } else {
                html.append("<td>").append(NORMAL).append("</td><td>").append(NORMAL_T).append("</td><td>LONG</td><td>");
                html.append(LONG_T).append("</td><td>").append(HQ).append("</td>");
            }
        }
        html.append("</tr>");
        int detailEnd = modern ? 24 : 40;
        int detailLine = 1;
        for (List<Object> row : dataRows) {
            html.append("<tr bgcolor='").append(detailLine % 2 == 1 ? "#fbfbfb" : "#f5f5f5").append("'>");
            detailLine++;
            for (int i = 1; i <= detailEnd; i++) {
                cell(html, row, i, null);
            }
            html.append("</tr>");
        }
        html.append("<tr bgcolor='#d0d0d0'>");
        for (int i = 0; i < detailEnd; i++) {
            cell(html, total, i, null);
        }
        html.append("</tr></table><br></div></td></tr></table><br>");
        memo(html, nvl(syear) + nvl(smon) + nvl(sday), nvl(eyear) + nvl(emon) + nvl(eday), "600");
        html.append("</body></html>");
        HtmlWriter.send(response, html.toString(), excel, "crbt_static.xls");
    }

    @PostMapping("/rankstatic/view1")
    public void view1(@RequestParam(value = "title", required = false) String title,
                      @RequestParam(value = "syear", required = false) String syear,
                      @RequestParam(value = "smon", required = false) String smon,
                      @RequestParam(value = "sday", required = false) String sday,
                      @RequestParam(value = "eyear", required = false) String eyear,
                      @RequestParam(value = "emon", required = false) String emon,
                      @RequestParam(value = "eday", required = false) String eday,
                      @RequestParam(value = "group", required = false) String group,
                      @RequestParam(value = "load", required = false) String load,
                      HttpServletResponse response) throws IOException {
        String sdate = nvl(syear) + nvl(smon) + nvl(sday) + "000000";
        String edate = nvl(eyear) + nvl(emon) + nvl(eday) + "235959";
        String cpFilter = viewFilter(group, nvl(syear) + nvl(smon) + nvl(sday));
        boolean channel = CHANNEL.equals(title);
        String listId = NS + (channel ? "selectViewChannel" : "selectViewService");
        String sumId = NS + (channel ? "selectViewChannelSum" : "selectViewServiceSum");
        int fields = channel ? 9 : 5;
        List<List<Object>> regi = queryService.query(listId, param(RankSqlFragments.TABLE_REGI, sdate, edate, cpFilter, null));
        List<List<Object>> gift = queryService.query(listId, param(RankSqlFragments.TABLE_GIFT, sdate, edate, cpFilter, null));
        List<List<Object>> regiSumRows = queryService.query(sumId, param(RankSqlFragments.TABLE_REGI, sdate, edate, cpFilter, null));
        List<List<Object>> giftSumRows = queryService.query(sumId, param(RankSqlFragments.TABLE_GIFT, sdate, edate, cpFilter, null));
        List<Object> regiSum = regiSumRows.isEmpty() ? null : regiSumRows.get(0);
        List<Object> giftSum = giftSumRows.isEmpty() ? null : giftSumRows.get(0);
        boolean excel = "excel".equals(load);
        int border = excel ? 1 : 0;
        String[] heads = channel
                ? new String[]{"WAP", "WEB", "ARS", "PDA", "CODE", "COPY", "INFO", "PORTAL"}
                : new String[]{"WAP", "WEB", OUTSIDE, ETC};
        StringBuilder html = new StringBuilder();
        pageHead(html, "view1");
        hidden(html, "title", title);
        hidden(html, "syear", syear);
        hidden(html, "smon", smon);
        hidden(html, "sday", sday);
        hidden(html, "eyear", eyear);
        hidden(html, "emon", emon);
        hidden(html, "eday", eday);
        hidden(html, "group", group);
        hidden(html, "load", load);
        html.append("</form>");
        html.append("<table border=0 align=center><tr><td align=left>(").append(esc(syear)).append(".").append(esc(smon)).append(".").append(esc(sday));
        html.append(" ~ ").append(esc(eyear)).append(".").append(esc(emon)).append(".").append(esc(eday)).append(") ");
        html.append(esc(title)).append(STAT).append("</td>");
        if (!excel) {
            html.append("<td align=right><b><a href=javascript:excel()><font color=blue>");
            html.append(ResultTable.EXCEL_LABEL);
            html.append("</font></a></b></td>");
        }
        html.append("</tr></table>");
        html.append("<div style=\"width:1250px;overflow-x:auto;\">");
        html.append("<table border='").append(border).append("' cellspacing='1' align=center style=\"table-layout:fixed\" width=900>");
        html.append("<tr align=center bgcolor='989898' height=\"30\">");
        html.append("<td rowspan=2 width=90><font color=white>CP</font></td>");
        for (String head : heads) {
            html.append("<td colspan=2><font color=white>").append(head).append("</font></td>");
        }
        html.append("</tr><tr bgcolor='#eeeeee' height=\"30\" align=center>");
        for (int i = 0; i < heads.length; i++) {
            html.append("<td>").append(SET).append("</td><td>").append(GIFT).append("</td>");
        }
        html.append("</tr>");
        Map<String, List<Object>> regiByCp = indexByCp(regi, fields);
        Map<String, List<Object>> giftByCp = indexByCp(gift, fields);
        List<String> cpids = new ArrayList<String>(regiByCp.keySet());
        for (String cpid : giftByCp.keySet()) {
            if (!regiByCp.containsKey(cpid)) {
                cpids.add(cpid);
            }
        }
        Collections.sort(cpids, new Comparator<String>() {
            @Override
            public int compare(String left, String right) {
                long a = numId(left);
                long b = numId(right);
                return a < b ? -1 : (a > b ? 1 : 0);
            }
        });
        int line = 0;
        for (String cpid : cpids) {
            List<Object> regiRow = regiByCp.get(cpid);
            List<Object> giftRow = giftByCp.get(cpid);
            String name = regiRow != null ? str(regiRow, 0) : str(giftRow, 0);
            html.append("<tr bgcolor='").append(line % 2 == 1 ? "#fbfbfb" : "#f5f5f5").append("'>");
            html.append("<td align=right height='20'>").append(esc(name)).append("</td>");
            for (int j = 1; j < fields; j++) {
                cell(html, regiRow, j, null);
                cell(html, giftRow, j, null);
            }
            html.append("</tr>");
            line++;
        }
        html.append("<tr bgcolor='#d0d0d0'><td align=center height='20'>").append(SUM).append("</td>");
        for (int i = 0; i < fields - 1; i++) {
            cell(html, regiSum, i, null);
            cell(html, giftSum, i, null);
        }
        html.append("</tr></table></div><br>");
        memo(html, nvl(syear) + nvl(smon) + nvl(sday), nvl(eyear) + nvl(emon) + nvl(eday), "600");
        html.append("</body></html>");
        HtmlWriter.send(response, html.toString(), excel, "crbt_static.xls");
    }

    @PostMapping({"/rankstatic/user", "/rankstatic/Tuser", "/rankstatic/user_excel"})
    public void userStat(@RequestParam(value = "syear", required = false) String syear,
                         @RequestParam(value = "smon", required = false) String smon,
                         @RequestParam(value = "sday", required = false) String sday,
                         @RequestParam(value = "datetype", required = false) String datetype,
                         @RequestParam(value = "order", required = false) String order,
                         HttpServletRequest request,
                         HttpServletResponse response) throws IOException {
        String table = "month".equals(datetype) ? "STATIC_USER_MONTH" : "STATIC_USER";
        String day = "month".equals(datetype) ? "01" : nvl(sday);
        String endDay = "month".equals(datetype) ? "31" : nvl(sday);
        String from = nvl(syear) + "-" + nvl(smon) + "-" + day;
        String to = nvl(syear) + "-" + nvl(smon) + "-" + endDay;
        String orderBy = orderBy(order);
        String where = " WHERE timezone between ? and ? and cpid in (100,110,120,130,140,150,170,180,190,200,220,230,510,520) ";
        String sql = "SELECT cpid, sum(WAP), sum(WEB), sum(ARS), sum(CODE), sum(PORTAL),"
                + " sum(WAP)+sum(WEB)+sum(ARS)+sum(CODE)+sum(PORTAL) as TOTAL FROM " + table
                + where + " GROUP BY cpid ORDER BY " + orderBy;
        String rankSql = "SELECT cpid, sum(WAP)+sum(WEB)+sum(ARS)+sum(CODE)+sum(PORTAL) as TOTAL FROM " + table
                + where + " GROUP BY cpid ORDER BY TOTAL DESC";
        String totalSql = "SELECT sum(WAP), sum(WEB), sum(ARS), sum(CODE), sum(PORTAL),"
                + " sum(WAP)+sum(WEB)+sum(ARS)+sum(CODE)+sum(PORTAL) as TOTAL FROM " + table + where;
        List<List<Object>> dataRows = ResultTable.query(crbtSts, sql, from, to);
        List<List<Object>> rankRows = ResultTable.query(crbtSts, rankSql, from, to);
        List<List<Object>> totalRows = ResultTable.query(crbtSts, totalSql, from, to);
        Map<String, Integer> rank = new LinkedHashMap<String, Integer>();
        int n = 0;
        for (List<Object> row : rankRows) {
            if (row.size() < 2) {
                continue;
            }
            n++;
            if (num(row, 1) == 0) {
                n--;
            }
            rank.put(str(row, 0), Integer.valueOf(n));
        }
        double[] totals = new double[6];
        if (!totalRows.isEmpty() && totalRows.get(0).size() >= 6) {
            for (int i = 0; i < 6; i++) {
                totals[i] = num(totalRows.get(0), i);
            }
        }
        double[] base = new double[6];
        for (int i = 0; i < 6; i++) {
            base[i] = totals[i] == 0 ? 1 : totals[i];
        }
        Map<String, String> cpName = cpInfoLoader.load();
        boolean excel = request.getRequestURI().indexOf("excel") >= 0;
        int border = excel ? 1 : 0;
        String[] cols = {"WAP", "WEB", "ARS", "CODE", "PORTAL", "TOTAL"};
        StringBuilder html = new StringBuilder();
        pageHead(html, "user");
        hidden(html, "order", order);
        hidden(html, "datetype", datetype);
        hidden(html, "syear", syear);
        hidden(html, "smon", smon);
        hidden(html, "sday", sday);
        html.append("</form>");
        html.append("<table border='0' width='96%' align=center><tr><td align=left>");
        if ("month".equals(datetype)) {
            html.append(esc(syear)).append("\ub144 ").append(esc(smon)).append("\uc6d4");
        } else {
            html.append(esc(syear)).append("\ub144 ").append(esc(smon)).append("\uc6d4 ").append(esc(sday)).append("\uc77c");
        }
        html.append(" ").append(USER_STAT).append("</td>");
        if (!excel) {
            html.append("<td align=right><b><a href=javascript:excel()><font color=blue>");
            html.append(ResultTable.EXCEL_LABEL);
            html.append("</font></a></b></td>");
        }
        html.append("</tr></table>");
        html.append("<table border='").append(border).append("' width='96%' cellspacing='1' align=center style=\"border:1 solid #989898\">");
        html.append("<tr align='center' bgcolor='989898'>");
        html.append("<td rowspan=2 height='45'><a href=javascript:ordering('CPID')><font color=white>CP</font></a></td>");
        html.append("<td rowspan=2 height='25'><a href=javascript:ordering('TOTAL')><font color=white>").append(RANK_LABEL).append("</font></a></td>");
        for (String col : cols) {
            html.append("<td colspan=2 height='25'><a href=javascript:ordering('").append(col).append("')><font color=white>");
            html.append(col).append("</font></a></td>");
        }
        html.append("</tr><tr align='center' bgcolor='#eeeeee'>");
        for (int i = 0; i < cols.length; i++) {
            html.append("<td height='20'>").append(USAGE).append("</td><td height='20'>%</td>");
        }
        html.append("</tr>");
        int line = 1;
        for (List<Object> row : dataRows) {
            if (row.size() < 7) {
                html.append("<tr><td colspan=14 align=left>").append(esc(str(row, 0))).append("</td></tr>");
                continue;
            }
            html.append("<tr bgcolor='").append(line % 2 == 1 ? "#fbfbfb" : "#f5f5f5").append("'>");
            String cpid = str(row, 0);
            String name = cpName.containsKey(cpid) ? cpName.get(cpid) : cpid;
            html.append("<td align=right height='20' bgcolor='#eeeeee'>").append(esc(name)).append("</td>");
            html.append("<td align=center height='20' bgcolor='#eeeeee'>");
            html.append(rank.containsKey(cpid) ? rank.get(cpid) : "").append("</td>");
            for (int i = 1; i <= 6; i++) {
                String bg = i == 6 ? " bgcolor='#eeeeee'" : "";
                html.append("<td align=right height='20'").append(bg).append(">");
                html.append(NumberFormat.getInstance(Locale.KOREA).format(num(row, i))).append("</td>");
                html.append("<td align=right height='20'").append(bg).append(">");
                html.append(percent(num(row, i), base[i - 1])).append("%</td>");
            }
            html.append("</tr>");
            line++;
        }
        html.append("<tr bgcolor='#d0d0d0'><td align=center colspan=2 height='20'>").append(SUM).append("</td>");
        for (int i = 0; i < 6; i++) {
            double whole = i == 5 ? base[5] : base[5];
            html.append("<td align=right height='20'>");
            html.append(NumberFormat.getInstance(Locale.KOREA).format((long) base[i])).append("</td>");
            html.append("<td align=right height='20'>").append(percent(base[i], whole)).append("%</td>");
        }
        html.append("</tr></table><br>");
        html.append("<table width=96% align=center><tr><td>").append(USER_NOTE).append("</td></tr><tr><td>");
        String memoFrom = "month".equals(datetype) ? nvl(syear) + nvl(smon) + "00" : nvl(syear) + nvl(smon) + nvl(sday);
        String memoTo = "month".equals(datetype) ? nvl(syear) + nvl(smon) + "31" : nvl(syear) + nvl(smon) + nvl(sday);
        memo(html, memoFrom, memoTo, "60%");
        html.append("</td></tr></table></body></html>");
        HtmlWriter.send(response, html.toString(), excel, "user_static.xls");
    }

    @PostMapping({"/rankstatic/service_user", "/rankstatic/service_user_excel"})
    public void serviceUser(@RequestParam(value = "syear", required = false) String syear,
                            @RequestParam(value = "smon", required = false) String smon,
                            @RequestParam(value = "sday", required = false) String sday,
                            @RequestParam(value = "datetype", required = false) String datetype,
                            @RequestParam(value = "order", required = false) String order,
                            @RequestParam(value = "servicetype", required = false) String servicetype,
                            HttpServletRequest request,
                            HttpServletResponse response) throws IOException {
        boolean month = "month".equals(datetype);
        String endDay = month ? "31" : nvl(sday);
        String startDay = month ? "01" : nvl(sday);
        String end = nvl(syear) + "-" + nvl(smon) + "-" + endDay + " 23:59:59";
        String start = nvl(syear) + "-" + nvl(smon) + "-" + startDay + " 00:00:00";
        String registSql = "select DATE(SubsTime), count(*) from CPL_USERS"
                + " where MDN>0 and SubsTime<=? and SubsTime>? group by DATE(SubsTime)";
        String unregistSql = "select DATE(UnSubsTime), count(*) from CPL_USERS"
                + " where MDN>0 and UnSubsTime<=? and UnSubsTime>? group by DATE(UnSubsTime)";
        Map<String, long[]> byDate = new TreeMap<String, long[]>();
        StringBuilder error = new StringBuilder();
        addCounts(byDate, ResultTable.query(couplring, registSql, end, start), 0, error);
        addCounts(byDate, ResultTable.query(couplring, unregistSql, end, start), 1, error);
        List<List<Object>> memberRows = ResultTable.query(couplring,
                "SELECT count(*) FROM CPL_USERS WHERE MDN>0 and MemberFlag=1");
        long members = 0L;
        if (!memberRows.isEmpty() && memberRows.get(0).size() >= 1 && memberRows.get(0).get(0) instanceof Number) {
            members = num(memberRows.get(0), 0);
        } else if (!memberRows.isEmpty() && error.length() == 0) {
            error.append(esc(str(memberRows.get(0), 0)));
        }
        boolean excel = request.getRequestURI().indexOf("excel") >= 0;
        int border = excel ? 1 : 0;
        NumberFormat format = NumberFormat.getInstance(Locale.KOREA);
        StringBuilder html = new StringBuilder();
        pageHead(html, "service");
        hidden(html, "servicetype", servicetype);
        hidden(html, "order", order);
        hidden(html, "datetype", datetype);
        hidden(html, "syear", syear);
        hidden(html, "smon", smon);
        hidden(html, "sday", sday);
        html.append("</form>");
        html.append("<table border='0' width='96%' align=center><tr><td align=left>");
        if (month) {
            html.append(esc(syear)).append("\ub144 ").append(esc(smon)).append("\uc6d4");
        } else {
            html.append(esc(syear)).append("\ub144 ").append(esc(smon)).append("\uc6d4 ").append(esc(sday)).append("\uc77c");
        }
        html.append(" ").append(USER_STAT).append("</td>");
        if (!excel) {
            html.append("<td align=right><b><a href=javascript:excel()><font color=blue>");
            html.append(ResultTable.EXCEL_LABEL);
            html.append("</font></a></b></td>");
        }
        html.append("</tr></table>");
        if (error.length() > 0) {
            html.append("<p align=left>").append(error).append("</p>");
        }
        html.append("<table border='").append(border).append("' width='96%' cellspacing='1' align=center style=\"border:1 solid #989898\">");
        html.append("<tr align='center' bgcolor='989898'>");
        html.append("<td height='45'><font color=white>").append(DATE_LABEL).append("</font></td>");
        html.append("<td height='25'><font color=white>").append(REGIST_LABEL).append("</font></td>");
        html.append("<td height='25'><font color=white>").append(UNREGIST_LABEL).append("</font></td>");
        html.append("<td height='25'><font color=white>").append(DIFF_LABEL).append("</font></td>");
        html.append("</tr>");
        long sumRegist = 0L;
        long sumUnregist = 0L;
        int line = 0;
        for (Map.Entry<String, long[]> entry : byDate.entrySet()) {
            long regist = entry.getValue()[0];
            long unregist = entry.getValue()[1];
            long diff = regist - unregist;
            sumRegist += regist;
            sumUnregist += unregist;
            html.append("<tr bgcolor='").append(line % 2 == 1 ? "#fbfbfb" : "#f5f5f5").append("'>");
            html.append("<td align=center height='20' bgcolor='#eeeeee'>").append(esc(entry.getKey())).append("</td>");
            html.append("<td align=right height='20'>").append(format.format(regist)).append("</td>");
            html.append("<td align=right height='20'>").append(format.format(unregist)).append("</td>");
            html.append("<td align=right height='20' bgcolor='#eeeeee'>").append(format.format(diff)).append("</td>");
            html.append("</tr>");
            line++;
        }
        html.append("<tr bgcolor='#d0d0d0'>");
        html.append("<td align=center height='20'>TOTAL</td>");
        html.append("<td align=right height='20'>").append(format.format(sumRegist)).append("</td>");
        html.append("<td align=right height='20'>").append(format.format(sumUnregist)).append("</td>");
        html.append("<td align=right height='20'>").append(format.format(sumRegist - sumUnregist)).append("</td>");
        html.append("</tr><tr bgcolor='#d0d0d0'>");
        html.append("<td align=center height='20'>").append(MEMBER_LABEL).append("</td>");
        html.append("<td colspan=3 align=right height='20'>").append(format.format(members)).append("</td>");
        html.append("</tr></table></body></html>");
        HtmlWriter.send(response, html.toString(), excel, "service_registration_static.xls");
    }

    private Map<String, Object> param(String table, String sdate, String edate, String cpFilter, String orderBy) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("table", table);
        param.put("sdate", sdate);
        param.put("edate", edate);
        param.put("cpFilter", cpFilter);
        param.put("orderBy", orderBy);
        return param;
    }

    private String tableOf(String title) {
        if (SET.equals(title)) {
            return RankSqlFragments.TABLE_REGI;
        }
        if (GIFT.equals(title)) {
            return RankSqlFragments.TABLE_GIFT;
        }
        if (ALL.equals(title)) {
            return RankSqlFragments.TABLE_ALL;
        }
        return null;
    }

    private String cpFilter(String group) {
        if ("rank".equals(group)) {
            return RankSqlFragments.CP_RANK;
        }
        if ("uangel".equals(group)) {
            return RankSqlFragments.CP_UANGEL;
        }
        return RankSqlFragments.CP_ALL;
    }

    private String viewFilter(String group, String ymd) {
        if ("rank".equals(group)) {
            return RankSqlFragments.CP_RANK;
        }
        if ("uangel".equals(group)) {
            return RankSqlFragments.CP_UANGEL;
        }
        if (ymd.compareTo("20150101") < 0) {
            return RankSqlFragments.CP_ALL;
        }
        return RankSqlFragments.CP_ALL_AFTER_2015;
    }

    private String orderBy(String order) {
        if (order == null || order.length() == 0 || "CPID".equals(order)) {
            return "CPID";
        }
        String base = order.replace(" DESC", "").trim();
        if ("TOTAL".equals(base) || "WAP".equals(base) || "WEB".equals(base) || "ARS".equals(base)
                || "PDA".equals(base) || "CODE".equals(base) || "COPY".equals(base)
                || "INFO".equals(base) || "PORTAL".equals(base)) {
            return base + " DESC";
        }
        return "CPID";
    }

    private void addCounts(Map<String, long[]> byDate, List<List<Object>> rows, int slot, StringBuilder error) {
        for (List<Object> row : rows) {
            if (row.size() < 2) {
                if (error.length() == 0) {
                    error.append(esc(str(row, 0)));
                }
                continue;
            }
            String day = str(row, 0);
            long[] counts = byDate.get(day);
            if (counts == null) {
                counts = new long[2];
                byDate.put(day, counts);
            }
            counts[slot] = num(row, 1);
        }
    }

    private void hidden(StringBuilder html, String name, String value) {
        html.append("<input type=hidden name=").append(name).append(" value=\"").append(esc(value)).append("\">");
    }

    private void pageHead(StringBuilder html, String action) {
        html.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=ks_c_5601-1987\">");
        html.append("<title>CRBT ").append(STAT).append("</title></head>");
        html.append("<link rel=\"StyleSheet\" type=\"text/css\" href=\"default.css\">");
        html.append(HtmlWriter.INLINE_STYLE);
        html.append("<script language=\"JavaScript\" type=\"text/JavaScript\">");
        if ("user".equals(action)) {
            html.append("function ordering(order){document.orderform.action='user';document.orderform.order.value=order;document.orderform.submit();}");
            html.append("function excel(){document.orderform.action='user_excel';document.orderform.submit();}");
        } else if ("service".equals(action)) {
            html.append("function excel(){document.orderform.action='service_user_excel';document.orderform.submit();}");
        } else {
            html.append("function ordering(order){document.orderform.order.value=order;document.orderform.submit();}");
            html.append("function excel(){document.orderform.load.value='excel';document.orderform.submit();}");
        }
        html.append("</script><body>");
        if (!"proc3".equals(action) && !"view1".equals(action) && !"user".equals(action) && !"service".equals(action)) {
            html.append("<style type=\"text/css\">table {table-layout:fixed;}</style>");
        }
        html.append("<form action='").append(action).append("' method=post name=orderform>");
    }

    private void memo(StringBuilder html, String sdate, String edate, String width) {
        List<List<Object>> rows = ResultTable.query(crbtSts,
                "select timezone, memo from MEMO where timezone between ? and ? order by timezone", sdate, edate);
        html.append("<center><table border=1 width=").append(width).append(" height=200><tr><td><table border=0>");
        int count = 0;
        for (List<Object> row : rows) {
            if (row.size() < 2) {
                html.append("<tr><td colspan=2 align=left>").append(esc(str(row, 0))).append("</td></tr>");
                count++;
                continue;
            }
            html.append("<tr><td width=90 align=left>").append(esc(str(row, 0))).append("</td>");
            html.append("<td align=left>").append(esc(str(row, 1))).append("</td></tr>");
            count++;
        }
        for (int line = count; line < 10; line++) {
            html.append("<tr><td height=20 align=right></td><td align=center></td></tr>");
        }
        html.append("</table></td></tr></table>");
    }

    private void cell(StringBuilder html, List<Object> row, int index, String bgcolor) {
        html.append("<td align=right height='20'");
        if (bgcolor != null) {
            html.append(" bgcolor='").append(bgcolor).append("'");
        }
        html.append(">").append(NumberFormat.getInstance(Locale.KOREA).format(num(row, index))).append("</td>");
    }

    private String percent(double part, double whole) {
        double base = whole == 0d ? 1d : whole;
        double value = Math.round((part / base) * 10000.0d) / 100.0d;
        if (value == Math.rint(value)) {
            return String.valueOf((long) value);
        }
        String text = String.valueOf(value);
        if (text.endsWith(".0")) {
            return text.substring(0, text.length() - 2);
        }
        return text;
    }

    private long num(List<Object> row, int index) {
        if (row == null || index < 0 || index >= row.size() || row.get(index) == null) {
            return 0L;
        }
        Object value = row.get(index);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private Map<String, List<Object>> indexByCp(List<List<Object>> rows, int keyIndex) {
        Map<String, List<Object>> indexed = new LinkedHashMap<String, List<Object>>();
        for (List<Object> row : rows) {
            if (row.size() <= keyIndex) {
                continue;
            }
            indexed.put(str(row, keyIndex), row);
        }
        return indexed;
    }

    private long numId(String value) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return 0L;
        }
    }

    private String str(List<Object> row, int index) {
        if (row == null || index >= row.size() || row.get(index) == null) {
            return "";
        }
        return String.valueOf(row.get(index));
    }

    private String esc(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;").replace("<", "&lt;").replace("\"", "&quot;");
    }

    private String nvl(String value) {
        return value == null ? "" : value;
    }

    private int year(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }
}
