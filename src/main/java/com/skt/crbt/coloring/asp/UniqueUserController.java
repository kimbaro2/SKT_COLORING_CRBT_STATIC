package com.skt.crbt.coloring.asp;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.skt.crbt.coloring.common.CookieNames;
import com.skt.crbt.coloring.common.CookieSupport;
import com.skt.crbt.coloring.common.HtmlWriter;
import com.skt.crbt.coloring.common.ResultTable;

@Controller
public class UniqueUserController {

    private final JdbcTemplate crbt;

    public UniqueUserController(@Qualifier("crbtJdbcTemplate") JdbcTemplate crbt) {
        this.crbt = crbt;
    }

    @GetMapping({"/aspstatic/unique_user/user_main.html", "/static/unique_user/user_main.html"})
    public void main(HttpServletResponse response) throws IOException {
        HtmlWriter.write(response,
                "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">" + HtmlWriter.CSS_LINK + "</head>"
                        + "<frameset cols=\"260,*\" border=0>"
                        + "<frame src=\"user_menu\" name=\"user_menu\">"
                        + "<frame src=\"null.html\" name=\"user_body\">"
                        + "</frameset></html>");
    }

    @GetMapping({"/aspstatic/unique_user/user_menu", "/static/unique_user/user_menu"})
    public void menu(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!CookieSupport.has(request, CookieNames.ADMIN_ID)) {
            HtmlWriter.write(response, "<script>location.replace('/aspstatic/index.html');</script>");
            return;
        }
        HtmlWriter.write(response,
                "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">" + HtmlWriter.CSS_LINK + "</head><body>"
                        + "<form method=POST action=user_static target=user_body>"
                        + "<select name=this_month>" + months() + "</select>"
                        + "<button type=submit>UNIQUE</button>"
                        + "</form></body></html>");
    }

    @PostMapping({
            "/aspstatic/unique_user/user_static", "/aspstatic/unique_user/user_excel",
            "/static/unique_user/user_static", "/static/unique_user/user_excel"
    })
    public void stat(@RequestParam(value = "this_month", required = false) String month,
                     HttpServletRequest request,
                     HttpServletResponse response) throws IOException {
        String m = monthOf(month);
        String cols = "m" + m + "_wap + m" + m + "_web + m" + m + "_ars + m" + m + "_code_ars + m" + m + "_code_potal";
        String sql = "SELECT cp_id, m" + m + "_wap, m" + m + "_web, m" + m + "_ars, m" + m + "_code_ars, m"
                + m + "_code_potal, " + cols
                + " FROM static_unique_user ORDER BY " + cols + " desc";
        boolean excel = request.getRequestURI().indexOf("excel") >= 0;
        String html = ResultTable.html("UNIQUE " + m, ResultTable.query(crbt, sql),
                excel ? null : "user_excel", "this_month", m);
        HtmlWriter.send(response, html, excel, "UNIQUE_USER_STATIC.xls");
    }

    private String months() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 12; i++) {
            String value = i < 10 ? "0" + i : String.valueOf(i);
            sb.append("<option value=").append(value).append(">").append(i).append("</option>");
        }
        return sb.toString();
    }

    private String monthOf(String month) {
        if (month != null && month.matches("0[1-9]|1[0-2]")) {
            return month;
        }
        return "01";
    }
}
