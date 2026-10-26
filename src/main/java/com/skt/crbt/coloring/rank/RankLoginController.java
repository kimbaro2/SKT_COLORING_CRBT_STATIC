package com.skt.crbt.coloring.rank;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
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
import com.skt.crbt.coloring.common.DbConstants;
import com.skt.crbt.coloring.common.ScriptRedirect;

@Controller
public class RankLoginController {

    private static final Logger log = LoggerFactory.getLogger(RankLoginController.class);

    private final JdbcTemplate crbtStsJdbcTemplate;

    public RankLoginController(JdbcTemplate crbtStsJdbcTemplate) {
        this.crbtStsJdbcTemplate = crbtStsJdbcTemplate;
    }

    @PostMapping("/rankstatic/login")
    public void login(@RequestParam(value = "id", required = false) String id,
                      @RequestParam(value = "passwd", required = false) String passwd,
                      HttpServletRequest request,
                      HttpServletResponse response) throws IOException {
        appendLoginHistory(request.getRemoteAddr());

        if (id == null) {
            id = "";
        }

        try {
            List<Map<String, Object>> rows = crbtStsJdbcTemplate.queryForList(
                    "select LEVEL, CP_ID from cp_users where ACCOUNT=? and PASSWD=?",
                    id, passwd);
            if (rows.size() == 1 && "guest".equals(String.valueOf(rows.get(0).get("LEVEL")))) {
                CookieSupport.add(response, CookieNames.ADMIN_ID, id, CookieNames.PATH_RANK);
                ScriptRedirect.submit(response, "/rankstatic/main");
                return;
            }
        } catch (DataAccessException e) {
            log.error("rank login db error", e);
        }

        CookieSupport.add(response, CookieNames.ADMIN_ID, id, CookieNames.PATH_RANK);
        ScriptRedirect.submit(response, "err.html");
    }

    private void appendLoginHistory(String remoteAddr) {
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(DbConstants.RANK_LOGIN_HISTORY, true), "EUC-KR");
            writer.write("[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
                    + "] client IP : " + remoteAddr + "\n");
        } catch (IOException e) {
            log.warn("login_history write skipped: {}", e.getMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignored) {
                    // ignore
                }
            }
        }
    }
}
