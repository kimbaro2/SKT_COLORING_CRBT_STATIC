package com.skt.crbt.coloring.gift;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.skt.crbt.coloring.common.CookieNames;
import com.skt.crbt.coloring.common.CookieSupport;
import com.skt.crbt.coloring.common.HtmlWriter;
import com.skt.crbt.coloring.common.ScriptRedirect;

@Controller
public class GiftLoginController {

    private static final Logger log = LoggerFactory.getLogger(GiftLoginController.class);

    private final JdbcTemplate crbtJdbcTemplate;

    public GiftLoginController(@Qualifier("crbtJdbcTemplate") JdbcTemplate crbtJdbcTemplate) {
        this.crbtJdbcTemplate = crbtJdbcTemplate;
    }

    @PostMapping({
            "/static/giftgw/giftstatic/login",
            "/static/giftgw/giftstatic/login.php",
            "/static/auth",
            "/static/auth.php"
    })
    public void login(@RequestParam(value = "userid", required = false) String userid,
                      @RequestParam(value = "id", required = false) String id,
                      @RequestParam(value = "passwd", required = false) String passwd,
                      HttpServletResponse response) throws IOException {
        if (userid == null || userid.length() == 0) {
            userid = id;
        }
        int loginError = 0;
        try {
            List<Map<String, Object>> rows = crbtJdbcTemplate.queryForList(
                    "select PASSWD from cp_users where ACCOUNT=?",
                    userid);
            if (rows.size() != 1) {
                loginError = 3;
            } else if (!String.valueOf(rows.get(0).get("PASSWD")).equals(passwd)) {
                loginError = 2;
            }
        } catch (DataAccessException e) {
            log.error("gift login db error", e);
            loginError = 1;
        }

        if (loginError > 0) {
            String message;
            if (loginError == 1) {
                message = "UserQuery Database connection error!";
            } else if (loginError == 2) {
                message = "password mismatch!";
            } else {
                message = (userid == null ? "" : userid) + " user not found!";
            }
            HtmlWriter.write(response,
                    "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">"
                            + "<script language=\"JavaScript\">alert('" + escapeJs(message)
                            + "');location.href='./index.html';</script></head></html>");
            return;
        }

        CookieSupport.add(response, CookieNames.GIFT_ID, userid, CookieNames.PATH_GIFT);
        ScriptRedirect.submitTop(response, "/static/admin_main.html");
    }

    private String escapeJs(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("'", "\\'");
    }
}
