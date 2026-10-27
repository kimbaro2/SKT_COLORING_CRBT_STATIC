import java.nio.file.Files

def outDir = new File(project.build.outputDirectory)
outDir.mkdirs()
def outFile = new File(outDir, 'git.properties')

if (hasRealCommit(outFile)) {
    return
}

def props = new LinkedHashMap<String, String>()
File gitRoot = null
def searchDirs = [
        project.basedir as File,
        new File(project.basedir as File, '..').canonicalFile
]
for (def dir : searchDirs) {
    if (dir != null && new File(dir, '.git').exists()) {
        gitRoot = dir
        break
    }
}
if (gitRoot != null) {
    def commit = runGit(gitRoot, 'rev-parse', 'HEAD')
    def abbrev = runGit(gitRoot, 'rev-parse', '--short', 'HEAD')
    def branch = runGit(gitRoot, 'rev-parse', '--abbrev-ref', 'HEAD')
    if (commit != null && !commit.isEmpty()) {
        props['git.commit.id'] = commit
        props['git.commit.id.abbrev'] = (abbrev != null && !abbrev.isEmpty()) ? abbrev : commit.take(7)
        props['git.branch'] = (branch != null && !branch.isEmpty()) ? branch : 'HEAD'
    }
}

if (props.isEmpty()) {
    def ts = project.properties.get('maven.build.timestamp')
    if (ts == null || ts.toString().isEmpty()) {
        ts = new java.text.SimpleDateFormat('yyyyMMddHHmmss').format(new Date())
    }
    def version = project.version
    props['git.branch'] = 'local'
    props['git.commit.id'] = "local-${version}-${ts}"
    props['git.commit.id.abbrev'] = props['git.commit.id'].take(7)
}

def lines = new ArrayList<String>()
lines.add('#Generated at build time (write-git-properties.groovy fallback)')
props.each { k, v -> lines.add("${k}=${v}") }
Files.write(outFile.toPath(), lines, java.nio.charset.StandardCharsets.UTF_8)

static boolean hasRealCommit(File outFile) {
    if (!outFile.exists()) {
        return false
    }
    def props = new Properties()
    outFile.withInputStream { props.load(it) }
    def commit = props.getProperty('git.commit.id')
    return commit != null && !commit.isBlank() && !commit.startsWith('local-')
}

static String resolveGitExecutable() {
    def fromEnv = System.getenv('GIT_EXECUTABLE')
    if (fromEnv != null && !fromEnv.isBlank()) {
        return fromEnv.trim()
    }
    if (isWindows()) {
        def candidates = [
                'C:\\Program Files\\Git\\cmd\\git.exe',
                'C:\\Program Files\\Git\\bin\\git.exe',
                'C:\\Program Files (x86)\\Git\\cmd\\git.exe'
        ]
        for (def path : candidates) {
            if (new File(path).exists()) {
                return path
            }
        }
        def where = runCommand(['where.exe', 'git'])
        if (where != null) {
            def first = where.readLines().find { it != null && !it.isBlank() }
            if (first != null) {
                return first.trim()
            }
        }
    }
    return 'git'
}

static boolean isWindows() {
    return System.getProperty('os.name', '').toLowerCase().contains('win')
}

static String runGit(File dir, String... args) {
    def cmd = new ArrayList<String>()
    cmd.add(resolveGitExecutable())
    cmd.add('-C')
    cmd.add(dir.absolutePath)
    cmd.addAll(args)
    return runCommand(cmd)
}

static String runCommand(List<String> cmd) {
    def proc = new ProcessBuilder(cmd).redirectErrorStream(true).start()
    def text = proc.inputStream.getText('UTF-8').trim()
    if (proc.waitFor() != 0) {
        return null
    }
    return text.isEmpty() ? null : text
}
