import de.skuzzle.semantic.Version

val latestTagHash = git("rev-list", "--tags", "--max-count=1")
val latestTagValue = git("describe", "--tags", latestTagHash, "--match=v[0-9]*")
val latestVersion = latestTagValue.substring(1)
val branch = git("rev-parse", "--abbrev-ref", "HEAD")
val pversion = rootProject.property("version")?.toString()
val status = git("status", "--porcelain")

rootProject.allprojects { this.version = determineVersion() }

fun determineVersion(): String {
    val pversion = rootProject.findProperty("releaseVersion")?.toString()
    if (pversion != null) {
        return pversion.toString()
    }
    return Version.parseVersion(latestVersion).nextPatch("$branch-SNAPSHOT").toString()
}

fun git(vararg args: String): String {
    val fullArgs = listOf("git") + listOf(*args)
    val exec = providers.exec { commandLine(fullArgs) }
    val output = exec.standardOutput.asText.get().trim()

    println("\$ ${fullArgs.joinToString(" ")}: $output")
    return output
}

val checkCleanWorkingCopy by tasks.creating(CheckCleanWorkingCopyTask::class.java) { }

val beforeReleaseHook by tasks.creating(DefaultTask::class.java) {
    outputs.upToDateWhen { false }
    mustRunAfter(checkCleanWorkingCopy)
}

val releaseInternal by tasks.creating(ReleaseInternalTask::class.java) {
    outputs.upToDateWhen { false }
    mustRunAfter(beforeReleaseHook)
}

val afterReleaseHook by tasks.creating(DefaultTask::class.java) {
    outputs.upToDateWhen { false }
    mustRunAfter(releaseInternal)
}

val finalizeRelease by tasks.creating(FinalizeReleaseTask::class.java) {
    outputs.upToDateWhen { false }
    mustRunAfter(releaseInternal, afterReleaseHook)
    releaseDryRun = providers.environmentVariable("RELEASE_DRY_RUN").map { it == "true" }
}

val release by tasks.creating(DefaultTask::class.java) {
    dependsOn(checkCleanWorkingCopy, beforeReleaseHook, releaseInternal, afterReleaseHook, finalizeRelease)
}
