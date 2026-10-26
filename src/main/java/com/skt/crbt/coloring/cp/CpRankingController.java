package com.skt.crbt.coloring.cp;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.skt.crbt.coloring.common.CookieNames;
import com.skt.crbt.coloring.common.CookieSupport;
import com.skt.crbt.coloring.common.HtmlWriter;
import com.skt.crbt.coloring.common.ResultTable;
import com.skt.crbt.coloring.common.ScriptRedirect;

@Controller
public class CpRankingController {

    private final JdbcTemplate jdbc;

    public CpRankingController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostMapping("/cp_static/ranking/auth")
    public void auth(@RequestParam(value = "cp_id", required = false) String cpId,
                     @RequestParam(value = "account", required = false) String account,
                     @RequestParam(value = "passwd", required = false) String passwd,
                     HttpServletResponse response) throws IOException {
        int id = parseId(cpId);
        if (id < 260) {
            ScriptRedirect.submit(response, "err_main.html");
            return;
        }
        try {
            List<Map<String, Object>> rows = jdbc.queryForList(
                    "select LEVEL,DESCRIPTION from cp_users where CP_ID=? and ACCOUNT=? and PASSWD=?",
                    cpId, account, passwd);
            if (rows.size() == 1) {
                String level = String.valueOf(rows.get(0).get("LEVEL"));
                if ("cp".equals(level)) {
                    CookieSupport.add(response, CookieNames.ADMIN_ID, cpId, CookieNames.PATH_CP);
                    CookieSupport.add(response, CookieNames.ADMIN_AC, account, CookieNames.PATH_CP);
                    ScriptRedirect.submit(response, "admin_main.html");
                    return;
                }
                if ("source".equals(level)) {
                    CookieSupport.add(response, CookieNames.SRC_ID, cpId, CookieNames.PATH_CP);
                    CookieSupport.add(response, CookieNames.SRC_AC, account, CookieNames.PATH_CP);
                    ScriptRedirect.submit(response, "/cp_static/source/src_main.html");
                    return;
                }
            }
        } catch (DataAccessException ignored) {
            // fall through
        }
        ScriptRedirect.submit(response, "err_main.html");
    }

    @GetMapping("/cp_static/ranking/admin_menu")
    public void menu(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!CookieSupport.has(request, CookieNames.ADMIN_ID)) {
            HtmlWriter.write(response, "<script>location.replace('index.html');</script>");
            return;
        }
        String cpId = CookieSupport.get(request, CookieNames.ADMIN_ID);
        String name = cpId;
        try {
            List<Map<String, Object>> rows = jdbc.queryForList("select CP_NAME from cp_id where CP_ID=?", cpId);
            if (!rows.isEmpty() && rows.get(0).get("CP_NAME") != null) {
                name = String.valueOf(rows.get(0).get("CP_NAME"));
            }
        } catch (DataAccessException ignored) {
            // keep id
        }
        HtmlWriter.write(response,
                "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">" + HtmlWriter.CSS_LINK + "</head><body>"
                        + "<p align=center><a href=admin_main.html target=_top>RANK</a></p>"
                        + "<form method=POST action=admin_static target=body>"
                        + "<input type=hidden name=dbtable value=SSRVD>"
                        + "from <input name=from_year size=4> <input name=from_month size=2> <input name=from_day size=2><br>"
                        + "to <input name=to_year size=4> <input name=to_month size=2> <input name=to_day size=2><br>"
                        + "<button type=submit>OK</button>"
                        + "</form>"
                        + "<p>CP_ID: " + cpId + "<br>CP_NAME: " + name + "</p>"
                        + "<form method=POST action=logout target=_top><button type=submit>LOGOUT</button></form>"
                        + "</body></html>");
    }

    @PostMapping("/cp_static/ranking/admin_static")
    public void stat(@RequestParam(value = "dbtable", required = false) String dbtable,
                     @RequestParam(value = "from_year", required = false) String fy,
                     @RequestParam(value = "from_month", required = false) String fm,
                     @RequestParam(value = "from_day", required = false) String fd,
                     @RequestParam(value = "to_year", required = false) String ty,
                     @RequestParam(value = "to_month", required = false) String tm,
                     @RequestParam(value = "to_day", required = false) String td,
                     HttpServletResponse response) throws IOException {
        if (!"SSRVD".equals(dbtable)) {
            dbtable = "SSRVD";
        }
        String from = ResultTable.ymd(fy, fm, fd, "000000");
        String to = ResultTable.ymd(ty, tm, td, "230000");
        String sql = "SELECT DID, SUM(F2+F6+F10) FROM " + dbtable
                + " WHERE DATE>=? AND DATE<=? AND CODE='DOWNLOAD' GROUP BY DID ORDER BY 2 DESC";
        HtmlWriter.write(response, ResultTable.html("CP RANK", ResultTable.query(jdbc, sql, from, to)));
    }

    @RequestMapping("/cp_static/ranking/logout")
    public void logout(HttpServletResponse response) throws IOException {
        ScriptRedirect.logoutHome(response);
    }

    private int parseId(String cpId) {
        try {
            return Integer.parseInt(cpId);
        } catch (Exception e) {
            return 0;
        }
    }
}
