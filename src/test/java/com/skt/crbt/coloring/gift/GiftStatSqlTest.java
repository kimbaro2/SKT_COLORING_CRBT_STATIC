package com.skt.crbt.coloring.gift;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GiftStatSqlTest {

    @Test
    void sndregSelectIncludesF1ThroughF4() {
        String sql = GiftStatSql.selectList("day", "SNDREG", "ALL");
        assertTrue(sql.contains("SUM(F1)"));
        assertTrue(sql.contains("SUM(F4)"));
        assertTrue(sql.contains("SUM(F1+F2+F3+F4)"));
    }

    @Test
    void webMediaUsesSingleChannelFields() {
        String sql = GiftStatSql.selectList("day", "SNDREG", "WEB");
        assertTrue(sql.contains("SUM(F1)"));
    }

    @Test
    void safeTableWhitelist() {
        assertTrue("SSRVC".equals(GiftStatSql.safeTable("SSRVC")));
        assertTrue("SPTLC".equals(GiftStatSql.safeTable("SPTLC")));
        assertTrue("SSRVC".equals(GiftStatSql.safeTable("UNKNOWN")));
    }
}
