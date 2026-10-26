package com.skt.crbt.coloring.rank;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.skt.crbt.coloring.common.HtmlWriter;
import com.skt.crbt.coloring.common.ResultTable;

@Controller
public class MinwonController {

    private final JdbcTemplate jdbc;

    public MinwonController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/rankstatic/minwon/freezone_menu")
    public void freezoneMenu(HttpServletResponse response) throws IOException {
        HtmlWriter.write(response, menu("freezone_sts", "FREEZONE",
                "<button name=title value=REGI>REGI</button>"
                        + "<button name=title value=GIFT>GIFT</button>"
                        + "<button name=title value=ALL>ALL</button>"));
    }

    @GetMapping("/rankstatic/minwon/album_menu")
    public void albumMenu(HttpServletResponse response) throws IOException {
        HtmlWriter.write(response, menu("album_sts", "ALBUM",
                "<button name=title value=DATE>DATE</button>"
                        + "<select name=type><option value=all>all</option><option>760</option><option>762</option>"
                        + "<option>763</option><option>764</option><option>765</option></select>"
                        + "<button name=title value=TYPE>TYPE</button>"
                        + " code <input name=code size=8>"
                        + "<button name=title value=CODE>CODE</button>"));
    }

    @PostMapping("/rankstatic/minwon/freezone_sts")
    public void freezone(@RequestParam(value = "title", required = false) String title,
                         @RequestParam(value = "syear", required = false) String syear,
                         @RequestParam(value = "smon", required = false) String smon,
                         @RequestParam(value = "sday", required = false) String sday,
                         @RequestParam(value = "eyear", required = false) String eyear,
                         @RequestParam(value = "emon", required = false) String emon,
                         @RequestParam(value = "eday", required = false) String eday,
                         HttpServletResponse response) throws IOException {
        MinwonLog.write("########## FREEZONE ##########");
        MinwonLog.write("IN> title(" + title + ")");
        String sel = freezoneSelect(title);
        String from = ResultTable.ymd(syear, smon, sday, "000000");
        String to = ResultTable.ymd(eyear, emon, eday, "235959");
        String sql = "select date_format(substring(DATE,1,10),'%Y-%m-%d')," + sel
                + " from FREEZONE_STS where DATE between ? and ? group by substring(DATE,1,10),CPID";
        HtmlWriter.write(response, ResultTable.html("FREEZONE", ResultTable.query(jdbc, sql, from, to)));
    }

    @PostMapping("/rankstatic/minwon/album_sts")
    public void album(@RequestParam(value = "title", required = false) String title,
                      @RequestParam(value = "type", required = false) String type,
                      @RequestParam(value = "code", required = false) String code,
                      @RequestParam(value = "syear", required = false) String syear,
                      @RequestParam(value = "smon", required = false) String smon,
                      @RequestParam(value = "sday", required = false) String sday,
                      @RequestParam(value = "eyear", required = false) String eyear,
                      @RequestParam(value = "emon", required = false) String emon,
                      @RequestParam(value = "eday", required = false) String eday,
                      HttpServletResponse response) throws IOException {
        MinwonLog.write("########## ALBUM ##########");
        MinwonLog.write("IN> title(" + title + "), type(" + type + "), code(" + code + ")");
        String from = ResultTable.ymd(syear, smon, sday, "000000");
        String to = ResultTable.ymd(eyear, emon, eday, "235959");
        String sql;
        Object[] args;
        if ("TYPE".equals(title)) {
            if (type != null && !"all".equals(type) && ResultTable.oneOf(type, "760", "762", "763", "764", "765")) {
                sql = "select ALBUMCODE,TYPE,sum(CNT) from ALBUM_STS_CODE where DATE between ? and ? and TYPE=? group by ALBUMCODE,TYPE order by ALBUMCODE,TYPE";
                args = new Object[] {from, to, type};
            } else {
                sql = "select ALBUMCODE,TYPE,sum(CNT) from ALBUM_STS_CODE where DATE between ? and ? group by ALBUMCODE,TYPE order by ALBUMCODE,TYPE";
                args = new Object[] {from, to};
            }
        } else if ("CODE".equals(title)) {
            sql = "select substring(DATE,1,10),sum(case when TYPE='760' then 1 end),sum(case when TYPE='762' then 1 end),"
                    + "sum(case when TYPE='763' then 1 end),sum(case when TYPE='764' then 1 end),sum(case when TYPE='765' then 1 end),sum(1)"
                    + " from ALBUM_STS_CODE where DATE between ? and ? and ALBUMCODE=? group by ALBUMCODE,substring(DATE,1,10)";
            args = new Object[] {from, to, code};
        } else {
            sql = "select substring(DATE,1,10),sum(A760),sum(A762),sum(A763),sum(A764),sum(A765),sum(A761)"
                    + " from ALBUM_STS_CNT where DATE between ? and ? group by substring(DATE,1,10)";
            args = new Object[] {from, to};
        }
        HtmlWriter.write(response, ResultTable.html("ALBUM", ResultTable.query(jdbc, sql, args)));
    }

    private String freezoneSelect(String title) {
        if ("GIFT".equals(title)) {
            return "sum(INS_N),sum(INS_L),sum(INS_N+INS_L)";
        }
        if ("ALL".equals(title)) {
            return "sum(REGI_N),sum(REGI_L),sum(INS_N),sum(INS_L),sum(REGI_N+REGI_L+INS_N+INS_L)";
        }
        return "sum(REGI_N),sum(REGI_L),sum(REGI_N+REGI_L)";
    }

    private String menu(String action, String title, String buttons) {
        return "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">"
                + HtmlWriter.CSS_LINK + "</head><body>"
                + "<p>" + title + "</p>"
                + "<form method=POST action='" + action + "' target=sts_body>"
                + "from <input name=syear size=4> <input name=smon size=2> <input name=sday size=2><br>"
                + "to <input name=eyear size=4> <input name=emon size=2> <input name=eday size=2><br>"
                + buttons
                + "</form></body></html>";
    }
}
