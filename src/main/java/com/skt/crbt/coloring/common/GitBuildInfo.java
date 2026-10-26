package com.skt.crbt.coloring.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

import org.springframework.boot.info.GitProperties;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;

public final class GitBuildInfo {

    static final String UNKNOWN = "(unknown)";

    private GitBuildInfo() {
    }

    public static Optional<GitProperties> resolve(Optional<GitProperties> gitProperties) {
        if (gitProperties.isPresent() && hasCommit(gitProperties.get())) {
            return gitProperties;
        }
        Properties loaded = loadClasspathGitProperties();
        if (loaded != null) {
            return Optional.of(new GitProperties(loaded));
        }
        return Optional.empty();
    }

    public static String branch(Environment environment, Optional<GitProperties> gitProperties) {
        Optional<GitProperties> git = resolve(gitProperties);
        return git.map(GitProperties::getBranch)
                .orElseGet(() -> propertyOrUnknown(environment, "git.branch"));
    }

    public static String commitId(Environment environment, Optional<GitProperties> gitProperties) {
        Optional<GitProperties> git = resolve(gitProperties);
        return git.map(GitProperties::getCommitId)
                .orElseGet(() -> propertyOrUnknown(environment, "git.commit.id"));
    }

    public static String commitIdAbbrev(Environment environment, Optional<GitProperties> gitProperties) {
        Optional<GitProperties> git = resolve(gitProperties);
        return git.map(GitProperties::getShortCommitId)
                .orElseGet(() -> propertyOrUnknown(environment, "git.commit.id.abbrev"));
    }

    /** HTML fragment for portal footer (escaped). Empty when no git metadata. */
    public static String footerHtml(Environment environment, Optional<GitProperties> gitProperties) {
        String branch = branch(environment, gitProperties);
        String abbrev = commitIdAbbrev(environment, gitProperties);
        String full = commitId(environment, gitProperties);
        if (isUnknown(branch) && isUnknown(abbrev) && isUnknown(full)) {
            return "";
        }
        StringBuilder line = new StringBuilder();
        line.append("git branch: ");
        line.append(escapeHtml(isUnknown(branch) ? UNKNOWN : branch));
        line.append(" | commit: ");
        if (!isUnknown(abbrev)) {
            line.append(escapeHtml(abbrev));
        } else if (!isUnknown(full)) {
            line.append(escapeHtml(full.length() > 7 ? full.substring(0, 7) : full));
        } else {
            line.append(UNKNOWN);
        }
        if (!isUnknown(full) && !isUnknown(abbrev) && !full.equals(abbrev)) {
            line.append(" <span class=\"git-full\">(");
            line.append(escapeHtml(full));
            line.append(")</span>");
        }
        return line.toString();
    }

    private static boolean hasCommit(GitProperties gitProperties) {
        try {
            String commit = gitProperties.getCommitId();
            return commit != null && !commit.isBlank();
        } catch (Exception ex) {
            return false;
        }
    }

    private static Properties loadClasspathGitProperties() {
        ClassPathResource resource = new ClassPathResource("git.properties");
        if (!resource.exists()) {
            return null;
        }
        Properties properties = new Properties();
        try (InputStream in = resource.getInputStream()) {
            properties.load(in);
        } catch (IOException ex) {
            return null;
        }
        String commit = properties.getProperty("git.commit.id");
        if (commit == null || commit.isBlank()) {
            return null;
        }
        return properties;
    }

    static String propertyOrUnknown(Environment environment, String key) {
        if (environment == null) {
            return UNKNOWN;
        }
        String value = environment.getProperty(key);
        return value != null && !value.isBlank() ? value : UNKNOWN;
    }

    private static boolean isUnknown(String value) {
        return value == null || value.isBlank() || UNKNOWN.equals(value);
    }

    private static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
