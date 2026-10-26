package com.skt.crbt.coloring.rank;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.skt.crbt.coloring.common.HtmlWriter;
import com.skt.crbt.coloring.common.ResultTable;

@Controller
public class MinwonOpsController {

    private static final String UNIQ_DIR = "/mnt3/uniq_user/backup/";

    private final JdbcTemplate sts;
    private final JdbcTemplate crbt;

    public MinwonOpsController(JdbcTemplate sts, @Qualifier("crbtJdbcTemplate") JdbcTemplate crbt) {
        this.sts = sts;
        this.crbt = crbt;
    }

    @GetMapping("/rankstatic/minwon/index.html")
    public void index(HttpServletResponse response) throws IOException {
        HtmlWriter.write(response,
                "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">" + HtmlWriter.CSS_LINK + "</head><body>"
                        + "<p><a href=daily_menu>daily</a></p>"
                        + "<p><a href=uniq_menu>uniq</a></p>"
                        + "<p><a href=alimisms_menu>alimi</a></p>"
                        + "<p><a href=lte_set_menu>lte</a></p>"
                        + "<p><a href=upload_menu>upload</a></p>"
                        + "<p><a href=memo_input_menu>memo</a></p>"
                        + "<p><a href=dup_menu>dup</a></p>"
                        + "<p><a href=yaho_menu>yaho</a></p>"
                        + "<p><a href=code_event_menu>code</a></p>"
                        + "</body></html>");
    }

    @GetMapping("/rankstatic/minwon/daily_menu")
    public void dailyMenu(HttpServletResponse response) throws IOException {
        form(response, "daily?flag=1", "sdate", "edate");
    }

    @PostMapping("/rankstatic/minwon/daily")
    public void daily(@RequestParam(value = "flag", required = false) String flag,
                      @RequestParam(value = "sdate", required = false) String sdate,
                      @RequestParam(value = "edate", required = false) String edate,
                      HttpServletResponse response) throws IOException {
        MinwonLog.write("########## DAILY CNT (" + flag + ") ##########");
        MinwonLog.write("IN> sdate(" + sdate + "), edate(" + edate + ")");
        int limit = intOf(flag, 1);
        String sql = "select DATE,MDN,REGI_CNT,INS_CNT,REGI_CNT+INS_CNT from DAILY_CNT"
                + " where DATE between ? and ? and REGI_CNT+INS_CNT>=? order by DATE DESC";
        HtmlWriter.write(response, ResultTable.html("DAILY", ResultTable.query(sts, sql, sdate, edate, Integer.valueOf(limit))));
    }

    @GetMapping("/rankstatic/minwon/uniq_menu")
    public void uniqMenu(HttpServletResponse response) throws IOException {
        form(response, "uniq?flag=sel_d", "sdate", "edate");
    }

    @PostMapping("/rankstatic/minwon/uniq")
    public void uniq(@RequestParam(value = "flag", required = false) String flag,
                     @RequestParam(value = "sdate", required = false) String sdate,
                     @RequestParam(value = "edate", required = false) String edate,
                     @RequestParam(value = "date", required = false) String date,
                     HttpServletResponse response) throws IOException {
        MinwonLog.write("########## UNIQ_USER ##########");
        MinwonLog.write("IN> sdate(" + sdate + "), edate(" + edate + "), date(" + date + "), flag(" + flag + ")");
        if ("sel_m".equals(flag)) {
            File file = uniqFile(date, "cnt_uniq_user.txt");
            String text = file != null && file.isFile() ? readSmall(file) : "file not found";
            HtmlWriter.write(response, "<html><head>" + HtmlWriter.CSS_LINK + "</head><body>" + text + "</body></html>");
            return;
        }
        String sql = "select timezone,uniq_cnt from STATIC_USER_DAILY where timezone between ? and ?";
        HtmlWriter.write(response, ResultTable.html("UNIQ", ResultTable.query(sts, sql, dash(sdate), dash(edate))));
    }

    @GetMapping("/rankstatic/minwon/alimisms_menu")
    public void alimiMenu(HttpServletResponse response) throws IOException {
        form(response, "alimisms?flag=sel", "sdate", "edate");
    }

    @PostMapping("/rankstatic/minwon/alimisms")
    public void alimi(@RequestParam(value = "flag", required = false) String flag,
                      @RequestParam(value = "sdate", required = false) String sdate,
                      @RequestParam(value = "edate", required = false) String edate,
                      HttpServletResponse response) throws IOException {
        MinwonLog.write("########## ALIMI_SMS ##########");
        MinwonLog.write("IN> sdate(" + sdate + "), edate(" + edate + "), flag(" + flag + ")");
        String sql = "sel_f".equals(flag)
                ? "select DATE,REGI_FLAG2,REGI_FLAG3,REGI_FLAG4,REGI_FLAG5,REGI_FLAG6,REGI_FLAG7,REGI_FLAG8,REGI_FLAG9,REGI_FLAG10,REGI_FLAG11,REGI_FLAG0,REGI_CNT from ALLIMY_STAT where DATE between ? and ? order by DATE DESC"
                : "select DATE,REGI_CNT,UNREGI_CNT,SMS_010+SMS_01X from ALLIMY_STAT where DATE between ? and ? order by DATE DESC";
        HtmlWriter.write(response, ResultTable.html("ALIMI", ResultTable.query(sts, sql, sdate, edate)));
    }

    @GetMapping("/rankstatic/minwon/lte_set_menu")
    public void lteMenu(HttpServletResponse response) throws IOException {
        form(response, "lte_set", "sdate", "edate");
    }

    @PostMapping("/rankstatic/minwon/lte_set")
    public void lte(@RequestParam(value = "sdate", required = false) String sdate,
                    @RequestParam(value = "edate", required = false) String edate,
                    HttpServletResponse response) throws IOException {
        MinwonLog.write("########## LTE_SET CNT ##########");
        MinwonLog.write("IN> sdate(" + sdate + "), edate(" + edate + ")");
        String sql = "select substring(DATE,1,10),sum(OK_CNT),sum(NOK_CNT),sum(RPT_X03),sum(RPT_X04)"
                + " from LTE_STS where DATE between ? and ? group by substring(DATE,1,10)";
        HtmlWriter.write(response, ResultTable.html("LTE", ResultTable.query(sts, sql, sdate + "000000", edate + "235959")));
    }

    @GetMapping("/rankstatic/minwon/upload_menu")
    public void uploadMenu(HttpServletResponse response) throws IOException {
        form(response, "upload", "sdate", "edate");
    }

    @PostMapping("/rankstatic/minwon/upload")
    public void upload(@RequestParam(value = "sdate", required = false) String sdate,
                       @RequestParam(value = "edate", required = false) String edate,
                       HttpServletResponse response) throws IOException {
        MinwonLog.write("########## UPLOAD CNT ##########");
        MinwonLog.write("IN> sdate(" + sdate + "), edate(" + edate + ")");
        String sql = "select count(*) from voice_source where regi_date between ? and ? and cp_id!='400' and cp_id!='360'"
                + " union select count(*) from voice_source where regi_date between ? and ? and cp_id='400'";
        String from = sdate + "000000";
        String to = edate + "235959";
        HtmlWriter.write(response, ResultTable.html("UPLOAD", ResultTable.query(crbt, sql, from, to, from, to)));
    }

    @GetMapping("/rankstatic/minwon/memo_input_menu")
    public void memoMenu(HttpServletResponse response) throws IOException {
        form(response, "memo_input?flag=sel", "date", "memo");
    }

    @PostMapping("/rankstatic/minwon/memo_input")
    public void memo(@RequestParam(value = "flag", required = false) String flag,
                     @RequestParam(value = "seq", required = false) String seq,
                     @RequestParam(value = "date", required = false) String date,
                     @RequestParam(value = "memo", required = false) String memo,
                     HttpServletResponse response) throws IOException {
        MinwonLog.write("########## MEMO_INPUT ##########");
        MinwonLog.write("IN> seq(" + seq + "), date(" + date + "), flag(" + flag + ")");
        if ("in".equals(flag)) {
            sts.update("insert into MEMO (seq,timezone,memo) values (?,?,?)", seq, date, memo);
            HtmlWriter.write(response, "ok");
            return;
        }
        if ("mod".equals(flag)) {
            sts.update("update MEMO set memo=? where seq=? and timezone=?", memo, seq, date);
            HtmlWriter.write(response, "ok");
            return;
        }
        if ("del".equals(flag)) {
            sts.update("delete from MEMO where seq=? and timezone=?", seq, date);
            HtmlWriter.write(response, "ok");
            return;
        }
        String sql = "select seq,timezone,memo from MEMO where timezone=?";
        HtmlWriter.write(response, ResultTable.html("MEMO", ResultTable.query(sts, sql, date)));
    }

    @GetMapping("/rankstatic/minwon/dup_menu")
    public void dupMenu(HttpServletResponse response) throws IOException {
        form(response, "dup?flag=sel", "date");
    }

    @PostMapping("/rankstatic/minwon/dup")
    public void dup(@RequestParam(value = "flag", required = false) String flag,
                    @RequestParam(value = "date", required = false) String date,
                    HttpServletResponse response) throws IOException {
        MinwonLog.write("########## DUPLICATION ##########");
        MinwonLog.write("IN> date(" + date + "), flag(" + flag + ")");
        String ts = monthStart(date) + " 00:00:02";
        if ("del".equals(flag)) {
            sts.update("delete from STATIC_REGI where timezone=?", ts);
            sts.update("delete from STATIC_ALL where timezone=?", ts);
            HtmlWriter.write(response, "ok");
            return;
        }
        if ("in".equals(flag)) {
            MinwonLog.write("RUN> shell batch is not executed from the web");
            HtmlWriter.write(response, "shell batch is not executed from the web");
            return;
        }
        String sql = "select c.cp_name, s.wap_n*(-1), s.wap_l*(-1), s.web_n*(-1), s.web_l*(-1)"
                + " from STATIC_REGI s, cp_id c where s.cpid=c.cp_id and s.timezone=?"
                + " and (s.wap_n+s.wap_l+s.web_n+s.web_l) < 0 order by s.cpid";
        HtmlWriter.write(response, ResultTable.html("DUP", ResultTable.query(sts, sql, ts)));
    }

    @GetMapping("/rankstatic/minwon/yaho_menu")
    public void yahoMenu(HttpServletResponse response) throws IOException {
        form(response, "yaho?flag=in", "date", "cpid");
    }

    @PostMapping("/rankstatic/minwon/yaho")
    public void yaho(@RequestParam(value = "flag", required = false) String flag,
                     @RequestParam(value = "date", required = false) String date,
                     @RequestParam(value = "cpid", required = false) String cpid,
                     @RequestParam(value = "regi_n", required = false) String regiN,
                     @RequestParam(value = "regi_l", required = false) String regiL,
                     @RequestParam(value = "gift_n", required = false) String giftN,
                     @RequestParam(value = "gift_l", required = false) String giftL,
                     HttpServletResponse response) throws IOException {
        MinwonLog.write("########## WEB_SEARCH ##########");
        MinwonLog.write("IN> date(" + date + "), cpid(" + cpid + "), flag(" + flag + ")");
        String ts = monthStart(date) + " 00:00:03";
        sts.update("delete from STATIC_REGI where cpid=? and timezone=?", cpid, ts);
        sts.update("delete from STATIC_GIFT where cpid=? and timezone=?", cpid, ts);
        if ("in".equals(flag)) {
            sts.update("insert into STATIC_REGI (cpid,timezone,web_n,web_l) values (?,?,?,?)", cpid, ts, neg(regiN), neg(regiL));
            sts.update("insert into STATIC_GIFT (cpid,timezone,web_n,web_l) values (?,?,?,?)", cpid, ts, neg(giftN), neg(giftL));
        }
        HtmlWriter.write(response, "ok");
    }

    @GetMapping("/rankstatic/minwon/code_event_menu")
    public void codeMenu(HttpServletResponse response) throws IOException {
        form(response, "code_event?flag=in", "date", "cpid");
    }

    @PostMapping("/rankstatic/minwon/code_event")
    public void codeEvent(@RequestParam(value = "flag", required = false) String flag,
                          @RequestParam(value = "date", required = false) String date,
                          @RequestParam(value = "cpid", required = false) String cpid,
                          @RequestParam(value = "code_n", required = false) String codeN,
                          @RequestParam(value = "code_l", required = false) String codeL,
                          @RequestParam(value = "potal_n", required = false) String potalN,
                          @RequestParam(value = "potal_l", required = false) String potalL,
                          HttpServletResponse response) throws IOException {
        MinwonLog.write("########## CODE_EVENT ##########");
        MinwonLog.write("IN> date(" + date + "), cpid(" + cpid + "), flag(" + flag + ")");
        String ts = dash(date) + " 00:00:01";
        sts.update("delete from STATIC_GIFT where cpid=? and timezone=?", cpid, ts);
        if ("in".equals(flag)) {
            sts.update("insert into STATIC_GIFT (cpid,timezone,code_n,code_l,portal_n,portal_l) values (?,?,?,?,?,?)",
                    cpid, ts, num(codeN), num(codeL), num(potalN), num(potalL));
        }
        HtmlWriter.write(response, "ok");
    }

    @GetMapping("/rankstatic/minwon/download")
    public void download(@RequestParam(value = "file", required = false) String file,
                         @RequestParam(value = "date", required = false) String date,
                         HttpServletResponse response) throws IOException {
        File target = uniqFile(date, file);
        if (target == null || !target.isFile()) {
            HtmlWriter.write(response, "file not found");
            return;
        }
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + target.getName());
        FileInputStream in = new FileInputStream(target);
        OutputStream out = response.getOutputStream();
        byte[] buf = new byte[8192];
        int n;
        while ((n = in.read(buf)) > 0) {
            out.write(buf, 0, n);
        }
        in.close();
    }

    private void form(HttpServletResponse response, String action, String... fields) throws IOException {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">" + HtmlWriter.CSS_LINK + "</head><body>");
        html.append("<form method=POST action='").append(action).append("'>");
        for (String field : fields) {
            html.append(field).append(" <input name=").append(field).append("><br>");
        }
        html.append("<button type=submit>OK</button></form></body></html>");
        HtmlWriter.write(response, html.toString());
    }

    private File uniqFile(String date, String name) {
        if (date == null || !date.matches("\\d{6}|\\d{8}")) {
            return null;
        }
        if (name == null || !name.matches("[A-Za-z0-9._-]+")) {
            return null;
        }
        return new File(UNIQ_DIR + date, name);
    }

    private String readSmall(File file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        byte[] buf = new byte[256];
        int n = in.read(buf);
        in.close();
        return n <= 0 ? "" : new String(buf, 0, n, "EUC-KR");
    }

    private String dash(String ymd) {
        if (ymd == null) {
            return "";
        }
        String digits = ymd.replace("-", "");
        if (digits.length() < 8) {
            return ymd;
        }
        return digits.substring(0, 4) + "-" + digits.substring(4, 6) + "-" + digits.substring(6, 8);
    }

    private String monthStart(String ymd) {
        String dashed = dash(ymd);
        if (dashed.length() < 7) {
            return dashed;
        }
        return dashed.substring(0, 7) + "-01";
    }

    private int intOf(String value, int fallback) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return fallback;
        }
    }

    private int num(String value) {
        return intOf(value, 0);
    }

    private int neg(String value) {
        return -Math.abs(num(value));
    }
}
