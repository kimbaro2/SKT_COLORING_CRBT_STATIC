package com.skt.crbt.coloring.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DbConstantsTest {

    @Test
    void jdbcUrlKeepsLegacyHostAndEncoding() {
        String url = DbConstants.jdbcUrl(DbConstants.DB_CRBT_STS);
        assertTrue(url.contains("crbt-TDBV001P"));
        assertTrue(url.contains("crbt_sts"));
        assertTrue(url.contains("characterEncoding=euckr"));
    }

    @Test
    void giftDomainIsUnchanged() {
        assertEquals("211.234.244.191:5462/static/giftgw/giftstatic", DbConstants.GIFT_DOMAIN);
    }

    @Test
    void cookiePathsAreIsolated() {
        assertEquals("/cp_static", CookieNames.PATH_CP);
        assertEquals("/aspstatic", CookieNames.PATH_ASP);
        assertEquals("/rankstatic", CookieNames.PATH_RANK);
        assertEquals("/static", CookieNames.PATH_GIFT);
    }
}
