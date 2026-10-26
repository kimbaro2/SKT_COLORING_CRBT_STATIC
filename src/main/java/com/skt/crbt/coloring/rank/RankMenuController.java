package com.skt.crbt.coloring.rank;

import java.io.IOException;
import java.util.Calendar;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.skt.crbt.coloring.common.CookieNames;
import com.skt.crbt.coloring.common.CookieSupport;
import com.skt.crbt.coloring.common.HtmlWriter;
import com.skt.crbt.coloring.common.ScriptRedirect;

@Controller
public class RankMenuController {

    private static final String[] YEARS = {
            "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025", "2026"
    };
    private static final String[] USER_YEARS = {
            "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025"
    };

    @GetMapping("/rankstatic/main")
    public void main(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!CookieSupport.has(request, CookieNames.ADMIN_ID)) {
            HtmlWriter.write(response,
                    "<script language='javascript'>alert('\ub85c\uadf8\uc778 \ud6c4 \uc774\uc6a9 \ubc14\ub78d\ub2c8\ub2e4.');"
                            + "location.replace('index.html');</script>");
            return;
        }
        HtmlWriter.write(response,
                "<html><head><title>CRBT</title>"
                        + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">" + HtmlWriter.CSS_LINK + "</head>"
                        + "<frameset rows=\"90,*\" border=\"0\" framespacing=\"0\" frameborder=\"YES\">"
                        + "<frameset cols=\"750,400\" border=\"0\" framespacing=\"0\" frameborder=\"YES\">"
                        + "<frame src=\"/rankstatic/menu\" name=\"sts_menu\">"
                        + "<frame src=\"/rankstatic/user_menu\" name=\"user_menu\">"
                        + "</frameset>"
                        + "<frame src=\"/rankstatic/null.html\" name=\"sts_body\">"
                        + "</frameset></html>");
    }

    @GetMapping("/rankstatic/menu")
    public void menu(HttpServletResponse response) throws IOException {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        String year = String.valueOf(yesterday.get(Calendar.YEAR));
        String mon = two(yesterday.get(Calendar.MONTH) + 1);
        String day = two(yesterday.get(Calendar.DATE));

        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>CRBT</title>");
        html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">");
        html.append("<link rel=\"StyleSheet\" type=\"text/css\" href=\"default.css\">");
        html.append(HtmlWriter.INLINE_STYLE);
        html.append("<script language=\"JavaScript\">");
        html.append("function setData(type){");
        html.append("var time=new Date();time.setDate(time.getDate()-1);");
        html.append("var year=time.getFullYear();var month=time.getMonth()+1;var day=time.getDate();");
        html.append("var f=document.forms[0];");
        html.append("if((parseInt(f.syear.value*10000)+parseInt(f.smon.value*100)+parseInt(f.sday.value))<20060800){");
        html.append("alert('2006-08-01');return;}");
        html.append("if((parseInt(f.eyear.value*10000)+parseInt(f.emon.value*100)+parseInt(f.eday.value))>(year*10000+month*100+day)){");
        html.append("alert('date');return;}");
        html.append("if((parseInt(f.syear.value*10000)+parseInt(f.smon.value*100)+f.sday.value)>(parseInt(f.eyear.value*10000)+parseInt(f.emon.value*100)+f.eday.value)){");
        html.append("alert('range');return;}");
        html.append("if(type=='\uc124\uc815'||type=='\uc120\ubb3c'||type=='\uc804\uccb4'){f.action='proc3';}");
        html.append("else{f.action='view1';}");
        html.append("f.title.value=type;f.submit();}");
        html.append("</script></head><body>");
        html.append("<form name=admin method=POST target='sts_body'>");
        html.append("<input type=hidden name=order value='CPID'>");
        html.append("<input type=hidden name=title><input type=hidden name=load value=\"page\">");
        html.append("<p align=center><font color='#006400' size=4><strong>");
        html.append("<a href=/rankstatic/main.html target=_top>CRBT Statistic v2</a></strong></font></p>");
        html.append("<table border=0 width=96% align=center><tr><td align=center>");
        html.append("<select name=group size=1>");
        html.append("<option value=\"all\" selected>\uc804\uccb4");
        html.append("<option value=\"rank\">\ud3c9\uac00");
        html.append("<option value=\"uangel\">\ud1b5\ud569");
        html.append("</select>&nbsp;&nbsp;");
        html.append(select("syear", YEARS, year)).append("\ub144 ");
        html.append(select("smon", months(), mon)).append("\uc6d4 ");
        html.append(select("sday", days(), day)).append("\uc77c ~&nbsp;&nbsp;");
        html.append(select("eyear", YEARS, year)).append("\ub144 ");
        html.append(select("emon", months(), mon)).append("\uc6d4 ");
        html.append(select("eday", days(), day)).append("\uc77c");
        html.append("&nbsp;&nbsp;<input type=button onClick=\"setData('\uc124\uc815');\" value=\"\uc124\uc815\uc870\ud68c\">");
        html.append("&nbsp;&nbsp;<input type=button onClick=\"setData('\uc120\ubb3c');\" value=\"\uc120\ubb3c\uc870\ud68c\">");
        html.append("&nbsp;&nbsp;<input type=button onClick=\"setData('\uc804\uccb4');\" value=\"\uc804\uccb4\uc870\ud68c\">");
        html.append("&nbsp;&nbsp;<input type=button onClick=\"setData('\ucc44\ub110');\" value=\"\ucc44\ub110\ubcc4\uc870\ud68c\">");
        html.append("&nbsp;&nbsp;<input type=button onClick=\"setData('\uc11c\ube44\uc2a4');\" value=\"\uc11c\ube44\uc2a4\ubcc4\uc870\ud68c\">");
        html.append("</td></tr></table></form></body></html>");
        HtmlWriter.write(response, html.toString());
    }

    @GetMapping("/rankstatic/user_menu")
    public void userMenu(HttpServletResponse response) throws IOException {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        String year = String.valueOf(yesterday.get(Calendar.YEAR));
        String mon = two(yesterday.get(Calendar.MONTH) + 1);
        String day = two(yesterday.get(Calendar.DATE));
        StringBuilder html = new StringBuilder();
        html.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">");
        html.append("<link rel=\"StyleSheet\" type=\"text/css\" href=\"default.css\">");
        html.append(HtmlWriter.INLINE_STYLE);
        html.append("<script language=\"JavaScript\">");
        html.append("function viewmenu(view){if(!view)return;");
        html.append("view.style.display=(view.style.display=='none')?'':'none';}");
        html.append("function goUser(){var f=document.forms[0];f.target='sts_body';f.action='/rankstatic/user';f.submit();}");
        html.append("function goServiceUser(){var f=document.forms[0];f.target='sts_body';f.action='/rankstatic/service_user';f.submit();}");
        html.append("</script></head><body>");
        html.append("<form name=admin method=POST target='sts_body'>");
        html.append("<input type=hidden name=order value='CPID'>");
        html.append("<input type=hidden name=servicetype value='cpl'>");
        html.append("<table border=0 align=left><tr>");
        html.append("<td align=left><font color='#006400' size=2><strong>\uc0ac\uc6a9\uc790 \ud1b5\uacc4</strong></font>");
        html.append("<select name=datetype size=1 onChange=\"viewmenu(document.getElementById('dayview'))\">");
        html.append("<option value=\"day\" selected>\uc77c\ubcc4<option value=\"month\">\uc6d4\ubcc4</select>");
        html.append(select("syear", USER_YEARS, year)).append("\ub144");
        html.append(select("smon", months(), mon)).append("\uc6d4</td>");
        html.append("<td><div id=dayview>");
        html.append(select("sday", days(), day)).append("\uc77c</div></td>");
        html.append("<td><input type=button onClick=\"goUser();\" value=\"\uc870\ud68c\"></td>");
        html.append("</tr><tr><td><font color='#006400' size=2><strong>\ucee4\ud50c\ub9c1 \ud1b5\uacc4</strong></font>");
        html.append("<input type=button onClick=\"goServiceUser();\" value=\"\uc870\ud68c\"></td>");
        html.append("</tr></table></form></body></html>");
        HtmlWriter.write(response, html.toString());
    }

    @RequestMapping({"/rankstatic/logout"})
    public void logout(HttpServletResponse response) throws IOException {
        ScriptRedirect.logoutHome(response);
    }

    private String select(String name, String[] values, String selected) {
        StringBuilder sb = new StringBuilder();
        sb.append("<select name=").append(name).append(" size=1>");
        for (int i = 0; i < values.length; i++) {
            sb.append("<option value=").append(values[i]);
            if (values[i].equals(selected)) {
                sb.append(" selected");
            }
            sb.append(">").append(label(name, values[i], i));
        }
        sb.append("</select>");
        return sb.toString();
    }

    private String label(String name, String value, int index) {
        if ("syear".equals(name) || "eyear".equals(name)) {
            return value;
        }
        return String.valueOf(index + 1);
    }

    private String[] months() {
        String[] values = new String[12];
        for (int i = 0; i < 12; i++) {
            values[i] = two(i + 1);
        }
        return values;
    }

    private String[] days() {
        String[] values = new String[31];
        for (int i = 0; i < 31; i++) {
            values[i] = two(i + 1);
        }
        return values;
    }

    private String two(int value) {
        return value < 10 ? "0" + value : String.valueOf(value);
    }
}
