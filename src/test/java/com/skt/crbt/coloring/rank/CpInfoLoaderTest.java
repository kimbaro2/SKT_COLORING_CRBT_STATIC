package com.skt.crbt.coloring.rank;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CpInfoLoaderTest {

    @Test
    void cpIdKeyNormalizesNumericTypes() {
        assertEquals("100", CpInfoLoader.cpIdKey(Integer.valueOf(100)));
        assertEquals("520", CpInfoLoader.cpIdKey(Long.valueOf(520L)));
        assertEquals("110", CpInfoLoader.cpIdKey("110"));
        assertEquals("110", CpInfoLoader.cpIdKey("110.0"));
    }

    @Test
    void cpNameCharsetMatchesLegacyCpinfoBytes() {
        byte[] witcom = new byte[] {(byte) 0xC0, (byte) 0xA7, (byte) 0xC6, (byte) 0xAE, (byte) 0xC4, (byte) 0xDE};
        String name = new String(witcom, CpInfoLoader.CP_NAME_CHARSET);
        assertEquals("\uc704\ud2b8\ucf64", name);
    }

    @Test
    void resolveNameUsesCpinfoEntry() {
        Map<String, String> names = new HashMap<String, String>();
        names.put("100", "Test CP");
        CpInfoLoader loader = new CpInfoLoader(null);
        assertEquals("Test CP", loader.resolveName(names, Integer.valueOf(100)));
        assertEquals("999", loader.resolveName(names, Integer.valueOf(999)));
    }
}
