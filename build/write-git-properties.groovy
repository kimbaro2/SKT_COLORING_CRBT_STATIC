import java.nio.file.Files

def outDir = new File(project.build.outputDirectory)
outDir.mkdirs()
def props = new LinkedHashMap<String, String>()

def searchDirs = [
        project.basedir as File,
        new File(project.basedir as File, '..').canonicalFile
]

File gitRoot = null
for (def dir : searchDirs) {
    if (new File(dir, '.git').exists()) {
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
lines.add('#Generated at build time (write-git-properties.groovy)')
props.each { k, v -> lines.add("${k}=${v}") }
Files.write(new File(outDir, 'git.properties').toPath(), lines, java.nio.charset.StandardCharsets.UTF_8)

static String runGit(File dir, String... args) {
    def cmd = new ArrayList<String>()
    cmd.add('git')
    cmd.add('-C')
    cmd.add(dir.absolutePath)
    cmd.addAll(args)
    def proc = new ProcessBuilder(cmd).redirectErrorStream(true).start()
    def text = proc.inputStream.getText('UTF-8').trim()
    if (proc.waitFor() != 0) {
        return null
    }
    return text.isEmpty() ? null : text
}
