package com.skt.crbt.coloring.common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public final class ResultTable {

    private ResultTable() {
    }

    public static List<List<Object>> query(JdbcTemplate jdbc, String sql, Object... args) {
        try {
            return jdbc.query(sql, args, new RowMapper<List<Object>>() {
                @Override
                public List<Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
                    List<Object> row = new ArrayList<Object>();
                    int count = rs.getMetaData().getColumnCount();
                    for (int i = 1; i <= count; i++) {
                        row.add(rs.getObject(i));
                    }
                    return row;
                }
            });
        } catch (DataAccessException e) {
            List<List<Object>> error = new ArrayList<List<Object>>();
            List<Object> row = new ArrayList<Object>();
            row.add(e.getMostSpecificCause().getMessage());
            error.add(row);
            return error;
        }
    }

    public static final String EXCEL_LABEL = "EXCEL FILE\ub85c \uc800\uc7a5";

    public static String html(String title, List<List<Object>> rows) {
        return html(title, rows, null);
    }

    public static String html(String title, List<List<Object>> rows, String excelAction, String... nameValue) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">");
        html.append("<title>").append(title).append("</title>");
        html.append(HtmlWriter.CSS_LINK);
        html.append("</head><body>");
        if (excelAction != null) {
            html.append("<form method=POST action='").append(excelAction).append("' name=orderform>");
            for (int i = 0; i + 1 < nameValue.length; i += 2) {
                String value = nameValue[i + 1] == null ? "" : nameValue[i + 1];
                html.append("<input type=hidden name=").append(nameValue[i]);
                html.append(" value=\"").append(value.replace("\"", "")).append("\">");
            }
            html.append("</form>");
            html.append("<p align=right><b><a href=javascript:document.orderform.submit();><font color=blue>");
            html.append(EXCEL_LABEL);
            html.append("</font></a></b></p>");
        }
        html.append("<h3>").append(title).append("</h3>");
        if (rows.isEmpty()) {
            html.append("<br><br><b><font size=2 color=red>&nbsp;&nbsp;DATA \uac00\uc5c6\uc2b5\ub2c8\ub2e4</font></b>");
            html.append("</body></html>");
            return html.toString();
        }
        html.append("<table border=0 cellspacing=1 cellpadding=2 style=\"border:1px solid #989898\">");
        NumberFormat format = NumberFormat.getInstance(Locale.KOREA);
        int line = 1;
        for (List<Object> row : rows) {
            html.append("<tr bgcolor='").append(line % 2 == 1 ? "#fbfbfb" : "#f5f5f5").append("'>");
            line++;
            for (Object cell : row) {
                html.append("<td height='20' align='").append(cell instanceof Number ? "right" : "left").append("'>");
                if (cell instanceof Number) {
                    html.append(format.format(cell));
                } else if (cell != null) {
                    html.append(String.valueOf(cell).replace("<", "&lt;"));
                }
                html.append("</td>");
            }
            html.append("</tr>");
        }
        html.append("</table></body></html>");
        return html.toString();
    }

    public static String ymd(String year, String month, String day, String hms) {
        String y = year == null ? "0000" : year;
        String m = month == null ? "01" : (month.length() == 1 ? "0" + month : month);
        String d = day == null ? "01" : (day.length() == 1 ? "0" + day : day);
        return y + m + d + hms;
    }

    public static boolean oneOf(String value, String... allowed) {
        if (value == null) {
            return false;
        }
        for (String item : allowed) {
            if (item.equals(value)) {
                return true;
            }
        }
        return false;
    }
}
