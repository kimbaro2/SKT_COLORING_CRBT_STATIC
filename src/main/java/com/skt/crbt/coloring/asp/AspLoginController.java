package com.skt.crbt.coloring.asp;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.skt.crbt.coloring.common.CookieNames;
import com.skt.crbt.coloring.common.CookieSupport;
import com.skt.crbt.coloring.common.ScriptRedirect;

@Controller
public class AspLoginController {

    private static final Logger log = LoggerFactory.getLogger(AspLoginController.class);

    private final JdbcTemplate crbtStsJdbcTemplate;

    public AspLoginController(JdbcTemplate crbtStsJdbcTemplate) {
        this.crbtStsJdbcTemplate = crbtStsJdbcTemplate;
    }

    @PostMapping("/aspstatic/auth")
    public void login(@RequestParam(value = "id", required = false) String id,
                      @RequestParam(value = "passwd", required = false) String passwd,
                      HttpServletResponse response) throws IOException {
        if (id == null) {
            id = "";
        }

        try {
            List<Map<String, Object>> rows = crbtStsJdbcTemplate.queryForList(
                    "select LEVEL from cp_users where ACCOUNT=? and PASSWD=?",
                    id, passwd);
            if (rows.size() == 1) {
                String level = String.valueOf(rows.get(0).get("LEVEL"));
                if ("guest".equals(level)) {
                    CookieSupport.add(response, CookieNames.ADMIN_ID, id, CookieNames.PATH_ASP);
                    ScriptRedirect.submit(response, "/aspstatic/admin_main.html");
                    return;
                }
                if (level != null && level.indexOf("asp") >= 0) {
                    CookieSupport.add(response, CookieNames.MASTER_ID, id, CookieNames.PATH_ASP);
                    CookieSupport.add(response, CookieNames.MASTER_LV, level, CookieNames.PATH_ASP);
                    ScriptRedirect.submit(response, "/mstatic/master_main.html");
                    return;
                }
                if ("lmaster".equals(level)) {
                    CookieSupport.add(response, CookieNames.MASTER_ID, id, CookieNames.PATH_ASP);
                    ScriptRedirect.submit(response, "/lstatic/master_main.html");
                    return;
                }
                ScriptRedirect.submit(response, "err_main.html");
                return;
            }
        } catch (DataAccessException e) {
            log.error("asp login db error", e);
        }

        CookieSupport.add(response, CookieNames.ADMIN_ID, id, CookieNames.PATH_ASP);
        ScriptRedirect.submit(response, "err_main.html");
    }
}
