package com.skt.crbt.coloring.common;

import java.util.Optional;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.info.GitProperties;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.zaxxer.hikari.HikariDataSource;

/**
 * Logs runtime profile, build (Git), and infrastructure targets at startup (no passwords).
 */
@Component
public class ColoringInfrastructureConnectionLogger {

    private static final Logger log = LoggerFactory.getLogger(ColoringInfrastructureConnectionLogger.class);
    private static final String UNKNOWN = "(unknown)";
    private static final String NOT_CONFIGURED = "(not configured)";

    private final Environment environment;
    private final Optional<GitProperties> gitProperties;
    private final DataSource crbtStsDataSource;
    private final DataSource crbtDataSource;
    private final DataSource copystsDataSource;
    private final DataSource couplringDataSource;

    @Value("${coloring.description:" + UNKNOWN + "}")
    private String coloringDescription;

    public ColoringInfrastructureConnectionLogger(
            Environment environment,
            Optional<GitProperties> gitProperties,
            @Qualifier("crbtStsDataSource") DataSource crbtStsDataSource,
            @Qualifier("crbtDataSource") DataSource crbtDataSource,
            @Qualifier("copystsDataSource") DataSource copystsDataSource,
            @Qualifier("couplringDataSource") DataSource couplringDataSource) {
        this.environment = environment;
        this.gitProperties = gitProperties;
        this.crbtStsDataSource = crbtStsDataSource;
        this.crbtDataSource = crbtDataSource;
        this.copystsDataSource = copystsDataSource;
        this.couplringDataSource = couplringDataSource;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logInfrastructureConnections() {
        String activeProfilesProperty = formatActiveProfilesProperty();
        String resolvedProfile = resolveProfile();

        String gitBranch = GitBuildInfo.branch(environment, gitProperties);
        String gitCommitId = GitBuildInfo.commitId(environment, gitProperties);
        String gitCommitIdAbbrev = GitBuildInfo.commitIdAbbrev(environment, gitProperties);

        HikariDataSource primary = asHikari(crbtStsDataSource);

        log.info("""
                \n========== CRBT Static Infrastructure Connection ==========
                spring.profiles.active (\uC2E4\uD589 \uD504\uB85C\uD37C\uD2F0) : {}
                resolved profile (\uC811\uC18D \uBD84\uAE30 \uD504\uB85C\uD544)     : {}
                coloring.description (\uD0C0\uAC9F \uD658\uACBD)         : {}
                git branch (\uBE4C\uB4DC \uC2DC\uC810)                 : {}
                git commit id (\uBE4C\uB4DC \uC2DC\uC810)              : {}
                git commit id (abbrev)                 : {}
                server.port                            : {}
                minwon.log.dir                         : {}
                ---------- DB (MySQL) ----------
                jdbc source                          : {}
                jdbc url (primary / crbt_sts)          : {}
                hostname (\uACC4\uC815 \uBD84\uAE30)                   : {}
                credential source                    : {}
                username                             : {}
                pool (max / min idle / keepalive ms) : {} / {} / {}
                ---------- schemas (same host/user) ----------
                crbt_sts                             : {}
                crbt                                 : {}
                copysts                              : {}
                couplring                            : {}
                ============================================================
                """,
                activeProfilesProperty,
                resolvedProfile,
                coloringDescription,
                gitBranch,
                gitCommitId,
                gitCommitIdAbbrev,
                envOrUnknown("server.port"),
                envOrUnknown("minwon.log.dir"),
                "DbConstants.java",
                primary != null ? primary.getJdbcUrl() : jdbcUrl(DbConstants.DB_CRBT_STS),
                DbConstants.HOST,
                "DbConstants.java",
                DbConstants.USER,
                poolMax(primary),
                poolMinIdle(primary),
                poolKeepalive(primary),
                poolLine(crbtStsDataSource, DbConstants.DB_CRBT_STS),
                poolLine(crbtDataSource, DbConstants.DB_CRBT),
                poolLine(copystsDataSource, DbConstants.DB_COPYSTS),
                poolLine(couplringDataSource, DbConstants.DB_COUPLRING),
                NOT_CONFIGURED,
                NOT_CONFIGURED,
                NOT_CONFIGURED,
                NOT_CONFIGURED,
                NOT_CONFIGURED,
                NOT_CONFIGURED);
    }

    private String formatActiveProfilesProperty() {
        String property = environment.getProperty("spring.profiles.active");
        if (property != null && !property.isBlank()) {
            return property;
        }
        String[] active = environment.getActiveProfiles();
        if (active.length > 0) {
            return String.join(",", active);
        }
        String defaults = environment.getProperty("spring.profiles.default");
        if (defaults != null && !defaults.isBlank()) {
            return defaults + " (default)";
        }
        return "(default)";
    }

    private String resolveProfile() {
        String[] active = environment.getActiveProfiles();
        if (active.length > 0) {
            return active[0];
        }
        String defaults = environment.getProperty("spring.profiles.default");
        if (defaults != null && !defaults.isBlank()) {
            return defaults;
        }
        return "default";
    }

    private String poolLine(DataSource dataSource, String schema) {
        HikariDataSource hikari = asHikari(dataSource);
        if (hikari == null) {
            return jdbcUrl(schema) + " pool=(unknown)";
        }
        return hikari.getJdbcUrl() + " pool=" + poolMax(hikari) + "/" + poolMinIdle(hikari);
    }

    private static String jdbcUrl(String schema) {
        return DbConstants.jdbcUrl(schema);
    }

    private static HikariDataSource asHikari(DataSource dataSource) {
        if (dataSource instanceof HikariDataSource) {
            return (HikariDataSource) dataSource;
        }
        return null;
    }

    private static int poolMax(HikariDataSource hikari) {
        return hikari != null ? hikari.getMaximumPoolSize() : -1;
    }

    private static int poolMinIdle(HikariDataSource hikari) {
        return hikari != null ? hikari.getMinimumIdle() : -1;
    }

    private static long poolKeepalive(HikariDataSource hikari) {
        return hikari != null ? hikari.getKeepaliveTime() : -1L;
    }

    private String envOrUnknown(String key) {
        String value = environment.getProperty(key);
        return value != null && !value.isBlank() ? value : UNKNOWN;
    }
}
