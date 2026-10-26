package com.skt.crbt.coloring.cp;

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
public class CpLoginController {

    private static final Logger log = LoggerFactory.getLogger(CpLoginController.class);

    private final JdbcTemplate crbtStsJdbcTemplate;

    public CpLoginController(JdbcTemplate crbtStsJdbcTemplate) {
        this.crbtStsJdbcTemplate = crbtStsJdbcTemplate;
    }

    @PostMapping("/cp_static/auth")
    public void login(@RequestParam(value = "cp_id", required = false) String cpId,
                      @RequestParam(value = "account", required = false) String account,
                      @RequestParam(value = "passwd", required = false) String passwd,
                      HttpServletResponse response) throws IOException {
        try {
            List<Map<String, Object>> rows = crbtStsJdbcTemplate.queryForList(
                    "select LEVEL,DESCRIPTION from cp_users where CP_ID=? and ACCOUNT=? and PASSWD=?",
                    cpId, account, passwd);
            if (rows.size() == 1) {
                String level = String.valueOf(rows.get(0).get("LEVEL"));
                if ("cp".equals(level)) {
                    CookieSupport.add(response, CookieNames.CP_ID, cpId, CookieNames.PATH_CP);
                    CookieSupport.add(response, CookieNames.CP_AC, account, CookieNames.PATH_CP);
                    ScriptRedirect.submit(response, "cp_main.html");
                    return;
                }
                if ("source".equals(level)) {
                    CookieSupport.add(response, CookieNames.SRC_ID, cpId, CookieNames.PATH_CP);
                    CookieSupport.add(response, CookieNames.SRC_AC, account, CookieNames.PATH_CP);
                    ScriptRedirect.submit(response, "source/src_main.html");
                    return;
                }
            }
        } catch (DataAccessException e) {
            log.error("cp login db error", e);
        }

        ScriptRedirect.submit(response, "err_main.html");
    }
}
