package com.skt.crbt.coloring.gift;

final class GiftStatSql {

    private GiftStatSql() {
    }

    static String selectList(String terms, String code, String media) {
        if ("WEB".equals(media)) {
            return selectSingleChannel(terms, code, 1);
        }
        if ("ARS".equals(media)) {
            return selectSingleChannel(terms, code, 2);
        }
        if ("WAP".equals(media)) {
            return selectSingleChannel(terms, code, 3);
        }
        if ("PDA".equals(media)) {
            return selectSingleChannel(terms, code, 4);
        }
        return selectAllChannels(terms, code);
    }

    private static String selectAllChannels(String terms, String code) {
        if ("SNDREG".equals(code)) {
            return dateLabel(terms) + ", SUM(F1), SUM(F2), SUM(F3), SUM(F4), SUM(F1+F2+F3+F4)";
        }
        if ("SNDINS".equals(code)) {
            return dateLabel(terms) + ", SUM(F5), SUM(F6), SUM(F7), SUM(F8), SUM(F5+F6+F7+F8)";
        }
        if ("REGINS".equals(code)) {
            return dateLabel(terms) + ", SUM(F9), SUM(F10), SUM(F11), SUM(F12), SUM(F9+F10+F11+F12)";
        }
        return dateLabel(terms) + ", SUM(F1+F5+F9), SUM(F2+F6+F10), SUM(F3+F7+F11),"
                + " SUM(F4+F8+F12), SUM(F1+F2+F3+F4+F5+F6+F7+F8+F9+F10+F11+F12)";
    }

    private static String selectSingleChannel(String terms, String code, int channel) {
        String col1 = channel == 1 ? sumExpr(code, 1) : "0";
        String col2 = channel == 2 ? sumExpr(code, 2) : "0";
        String col3 = channel == 3 ? sumExpr(code, 3) : "0";
        String col4 = channel == 4 ? sumExpr(code, 4) : "0";
        String total = sumExpr(code, channel);
        return dateLabel(terms) + ", " + col1 + ", " + col2 + ", " + col3 + ", " + col4 + ", " + total;
    }

    private static String sumExpr(String code, int channel) {
        return "SUM(" + sumField(code, channel) + ")";
    }

    private static String sumField(String code, int channel) {
        if ("ALL".equals(code)) {
            if (channel == 1) {
                return "F1+F5+F9";
            }
            if (channel == 2) {
                return "F2+F6+F10";
            }
            if (channel == 3) {
                return "F3+F7+F11";
            }
            return "F4+F8+F12";
        }
        int index = channel;
        if ("SNDINS".equals(code)) {
            index = channel + 4;
        } else if ("REGINS".equals(code)) {
            index = channel + 8;
        }
        return "F" + index;
    }

    static String dateLabel(String terms) {
        if ("hour".equals(terms)) {
            return "date_format(DATE,'%H\uc2dc')";
        }
        if ("month".equals(terms)) {
            return "date_format(DATE,'%Y\ub144 %m\uc6d4')";
        }
        if ("year".equals(terms)) {
            return "date_format(DATE,'%Y\ub144')";
        }
        return "date_format(DATE,'%m\uc6d4 %d\uc77c')";
    }

    static String groupBy(String terms) {
        if ("hour".equals(terms)) {
            return "date_format(DATE,'%H')";
        }
        if ("month".equals(terms)) {
            return "date_format(DATE,'%Y%m')";
        }
        if ("year".equals(terms)) {
            return "date_format(DATE,'%Y')";
        }
        return "date_format(DATE,'%Y%m%d')";
    }

    static String titleOf(String code, String media) {
        String kind;
        if ("SNDREG".equals(code)) {
            kind = "\uc77c\ubc18\uc120\ubb3c";
        } else if ("SNDINS".equals(code)) {
            kind = "\uc77c\ubc18\uc120\ubb3c(T\ub9c1)";
        } else if ("REGINS".equals(code)) {
            kind = "\ub871\uc120\ubb3c";
        } else {
            kind = "\ub871\uc120\ubb3c(T\ub9c1)";
        }
        if (!"ALL".equals(media)) {
            kind = kind + "(" + media + ")";
        }
        return kind;
    }

    static String safeTable(String dbtable) {
        if ("SPTLC".equals(dbtable)) {
            return "SPTLC";
        }
        if ("SCODC".equals(dbtable)) {
            return "SCODC";
        }
        if ("OTH_SCODD".equals(dbtable) || "OTH_SCODC".equals(dbtable)) {
            return "OTH_SCODD";
        }
        return "SSRVC";
    }

    static String safeCode(String code) {
        if ("SNDREG".equals(code) || "SNDINS".equals(code) || "REGINS".equals(code) || "ALL".equals(code)) {
            return code;
        }
        return "ALL";
    }

    static String safeMedia(String media) {
        if ("WEB".equals(media) || "ARS".equals(media) || "WAP".equals(media) || "PDA".equals(media)) {
            return media;
        }
        return "ALL";
    }
}
