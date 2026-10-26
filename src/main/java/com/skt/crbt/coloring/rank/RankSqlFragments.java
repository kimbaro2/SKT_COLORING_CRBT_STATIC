package com.skt.crbt.coloring.rank;

/**
 * PHP proc3 values that were pasted into sql '?'.
 * Keep the predicates unchanged.
 */
public final class RankSqlFragments {

    public static final String TABLE_REGI = "STATIC_REGI";
    public static final String TABLE_GIFT = "STATIC_GIFT";
    public static final String TABLE_ALL = "STATIC_ALL";

    public static final String CP_RANK = "cpid in (100,110,120,130,140,150,170,180,190,200,220,230,510,520)";
    public static final String CP_UANGEL = "cpid = 400";
    public static final String CP_ALL = "cpid != 160 and cpid != 270 and cpid != 360 and cpid != 400";
    public static final String CP_ALL_AFTER_2015 = "(cpid = 100 OR cpid = 130 OR cpid = 150 OR cpid = 180)";

    private RankSqlFragments() {
    }
}
