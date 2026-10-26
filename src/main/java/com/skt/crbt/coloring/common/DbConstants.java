package com.skt.crbt.coloring.common;

/**
 * Keep PHP mysql_connect values as-is. Do not change in this TF.
 * Secret externalization is a follow-up task.
 */
public final class DbConstants {

    public static final String HOST = "crbt-TDBV001P";
    public static final String USER = "crbt01";
    public static final String PASSWORD = "qkdqodur";

    public static final String DB_CRBT_STS = "crbt_sts";
    public static final String DB_CRBT = "crbt";
    public static final String DB_COPYSTS = "copysts";
    public static final String DB_COUPLRING = "couplring";

    public static final String JDBC_OPTS = "characterEncoding=euckr&useUnicode=true&autoReconnect=true";

    public static final String RANK_LOGIN_HISTORY = "/data/crbt/www/static/admin_new/login_history";
    public static final String RANK_IPLIST = "/data/crbt/www/static/admin_new/iplist.ini";
    public static final String RANK_CPINFO = "/data/crbt/www/static/admin_new/cpinfo.ini";

    /** gift class.ini value. Do not change. */
    public static final String GIFT_DOMAIN = "211.234.244.191:5462/static/giftgw/giftstatic";

    private DbConstants() {
    }

    public static String jdbcUrl(String database) {
        return "jdbc:mysql://" + HOST + ":3306/" + database + "?" + JDBC_OPTS;
    }
}
