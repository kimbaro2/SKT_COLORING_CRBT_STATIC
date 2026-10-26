package com.skt.crbt.coloring.asp;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
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
public class RingPageController {

    private final JdbcTemplate crbt;
    private final JdbcTemplate copysts;

    public RingPageController(@Qualifier("crbtJdbcTemplate") JdbcTemplate crbt,
                              @Qualifier("copystsJdbcTemplate") JdbcTemplate copysts) {
        this.crbt = crbt;
        this.copysts = copysts;
    }

    @PostMapping({"/aspstatic/copyring/auth", "/aspstatic/codering/auth"})
    public void auth(@RequestParam(value = "id", required = false) String id,
                     @RequestParam(value = "passwd", required = false) String passwd,
                     HttpServletResponse response) throws IOException {
        String next = "err_main.html";
        try {
            List<Map<String, Object>> rows = crbt.queryForList(
                    "select LEVEL from cp_users where ACCOUNT=? and PASSWD=?", id, passwd);
            if (rows.size() == 1 && "guest".equals(String.valueOf(rows.get(0).get("LEVEL")))) {
                CookieSupport.add(response, CookieNames.ADMIN_ID, id, CookieNames.PATH_ASP);
                next = "admin_main.html";
            }
        } catch (DataAccessException ignored) {
            next = "err_main.html";
        }
        ScriptRedirect.submit(response, next);
    }

    @GetMapping({"/aspstatic/copyring/admin_menu", "/aspstatic/codering/admin_menu"})
    public void menu(HttpServletResponse response) throws IOException {
        HtmlWriter.write(response,
                "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">" + HtmlWriter.CSS_LINK + "</head><body>"
                        + "<form method=POST action=admin_static target=body>"
                        + "<input type=hidden name=dbtable value=SSRVD>"
                        + "from <input name=from_year size=4> <input name=from_month size=2> <input name=from_day size=2><br>"
                        + "to <input name=to_year size=4> <input name=to_month size=2> <input name=to_day size=2><br>"
                        + "<button name=msg value=ALL>ALL</button>"
                        + "<button name=msg value=COPYRING>COPYRING</button>"
                        + "<button name=msg value=INFORING>INFORING</button>"
                        + "<button name=msg value=SNDREG>SNDREG</button>"
                        + "<button name=msg value=SNDINS>SNDINS</button>"
                        + "</form></body></html>");
    }

    @PostMapping({
            "/aspstatic/copyring/admin_static", "/aspstatic/copyring/admin_static_excel",
            "/aspstatic/codering/admin_static", "/aspstatic/codering/admin_static_excel"
    })
    public void stat(jakarta.servlet.http.HttpServletRequest request,
                     @RequestParam(value = "dbtable", required = false) String dbtable,
                     @RequestParam(value = "msg", required = false) String msg,
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
        String from = ResultTable.ymd(fy, fm, fd, "000000");
        String to = ResultTable.ymd(ty, tm, td, "230000");
        boolean code = request.getRequestURI().contains("/codering/");
        String fields = code ? codeFields(msg) : copyFields(msg);
        String sql = code
                ? "SELECT DID,VCODE," + fields + " FROM codering." + dbtable
                + " WHERE DATE>=? AND DATE<=? AND CODE='DOWNLOAD' GROUP BY VCODE ORDER BY 2"
                : "SELECT DID," + fields + " FROM " + dbtable
                + " WHERE DATE>=? AND DATE<=? AND CODE='DOWNLOAD' AND DID != 210 AND DID != 260 AND DID != 310 GROUP BY DID ORDER BY 2 desc";
        JdbcTemplate db = code ? crbt : copysts;
        boolean excel = request.getRequestURI().indexOf("excel") >= 0;
        String file = code ? "CODERING_REGISTER_STATIC.xls" : copyFile(msg);
        String body = ResultTable.html(request.getRequestURI(), ResultTable.query(db, sql, from, to),
                excel ? null : "admin_static_excel",
                "dbtable", dbtable, "msg", msg,
                "from_year", fy, "from_month", fm, "from_day", fd,
                "to_year", ty, "to_month", tm, "to_day", td);
        HtmlWriter.send(response, body, excel, file);
    }

    private String copyFile(String msg) {
        if ("COPYRING".equals(msg)) {
            return "COPYRING_STATIC.xls";
        }
        if ("INFORING".equals(msg)) {
            return "INFORING_STATIC.xls";
        }
        return "COPYRING_INFORING_STATIC_ALL.xls";
    }

    private String copyFields(String msg) {
        if ("COPYRING".equals(msg)) {
            return "SUM(F1)";
        }
        if ("INFORING".equals(msg)) {
            return "SUM(F5)";
        }
        return "SUM(F1+F5)";
    }

    private String codeFields(String msg) {
        if ("SNDREG".equals(msg)) {
            return "SUM(F1)";
        }
        if ("SNDINS".equals(msg)) {
            return "SUM(F2)";
        }
        return "SUM(F1+F2+F3)";
    }
}
