package com.skt.crbt.coloring.cp;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
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
import com.skt.crbt.coloring.common.ScriptRedirect;

@Controller
public class CpRingController {

    private final JdbcTemplate crbt;

    public CpRingController(@Qualifier("crbtJdbcTemplate") JdbcTemplate crbt) {
        this.crbt = crbt;
    }

    @PostMapping({"/cp_static/copyring/auth", "/cp_static/codering/auth"})
    public void auth(@RequestParam(value = "cp_id", required = false) String cpId,
                     @RequestParam(value = "account", required = false) String account,
                     @RequestParam(value = "passwd", required = false) String passwd,
                     HttpServletRequest request,
                     HttpServletResponse response) throws IOException {
        try {
            List<Map<String, Object>> rows = crbt.queryForList(
                    "select LEVEL,DESCRIPTION from cp_users where CP_ID=? and ACCOUNT=? and PASSWD=?",
                    cpId, account, passwd);
            if (rows.size() == 1 && "cp".equals(String.valueOf(rows.get(0).get("LEVEL")))) {
                CookieSupport.add(response, CookieNames.CP_ID, cpId, CookieNames.PATH_CP);
                CookieSupport.add(response, CookieNames.CP_AC, account, CookieNames.PATH_CP);
                ScriptRedirect.submit(response, "cp_main.html");
                return;
            }
        } catch (DataAccessException ignored) {
            // fall through
        }
        ScriptRedirect.submit(response, "err_main.html");
    }

    @GetMapping({"/cp_static/copyring/cp_menu", "/cp_static/codering/cp_menu"})
    public void menu(HttpServletResponse response) throws IOException {
        HtmlWriter.write(response,
                "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">" + HtmlWriter.CSS_LINK + "</head><body>"
                        + "<form method=POST target=body>"
                        + "from <input name=from_year size=4> <input name=from_month size=2> <input name=from_day size=2><br>"
                        + "to <input name=to_year size=4> <input name=to_month size=2> <input name=to_day size=2><br>"
                        + "<button type=submit formaction=code_static name=dbtable value=SCODD>CODE</button>"
                        + "<button type=submit formaction=cp_sstatic name=dbtable value=SSRVD>SERVICE</button>"
                        + "<button type=submit formaction=err_static name=dbtable value=SSRVD>ERR</button>"
                        + "</form></body></html>");
    }
}
