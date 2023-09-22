import de.skuzzle.semantic.Version


val releaseExtension = extensions.create<ReleaseExtension>(ReleaseExtension.NAME).apply {
    dryRun.convention(
        providers.systemProperty("RELEASE_DRY_RUN").map { it == "true" }
            .orElse(providers.gradleProperty("releaseDryRun").map { it == "true" })
            .orElse(false)
    )
    verbose.convention(
        providers.systemProperty("RELEASE_VERBOSE").map { it == "true" }
            .orElse(providers.gradleProperty("releaseVerbose").map { it == "true" })
            .orElse(false)
    )
    mainBranch.convention("main")
    devBranch.convention("dev")
}

val git = Git(providers, releaseExtension.dryRun, releaseExtension.verbose)
val latestTagHash = git.git("rev-list", "--tags", "--max-count=1")
val latestTagValue = git.git("describe", "--tags", latestTagHash, "--match=v[0-9]*")
val latestVersion = latestTagValue.substring(1)
val branch = git.git("rev-parse", "--abbrev-ref", "HEAD")
val pversion = rootProject.property("version")?.toString()
val status = git.git("status", "--porcelain")

rootProject.allprojects { this.version = determineVersion() }

fun determineVersion(): String {
    val pversion = rootProject.findProperty("releaseVersion")?.toString()
    if (pversion != null) {
        return pversion.toString()
    }
    return Version.parseVersion(latestVersion).nextPatch("$branch-SNAPSHOT").toString()
}

val checkCleanWorkingCopy by tasks.creating(CheckCleanWorkingCopyTask::class.java) {
    releaseExtension.wireUp(this)
}

val beforeReleaseHook by tasks.creating(DefaultTask::class.java) {
    outputs.upToDateWhen { false }
    mustRunAfter(checkCleanWorkingCopy)
}

val releaseInternal by tasks.creating(ReleaseInternalTask::class.java) {
    outputs.upToDateWhen { false }
    mustRunAfter(beforeReleaseHook)
    releaseExtension.wireUp(this)
}

val afterReleaseHook by tasks.creating(DefaultTask::class.java) {
    outputs.upToDateWhen { false }
    mustRunAfter(releaseInternal)
}

val finalizeRelease by tasks.creating(FinalizeReleaseTask::class.java) {
    outputs.upToDateWhen { false }
    mustRunAfter(releaseInternal, afterReleaseHook)
    releaseExtension.wireUp(this)
}

val release by tasks.creating(DefaultTask::class.java) {
    dependsOn(checkCleanWorkingCopy, beforeReleaseHook, releaseInternal, afterReleaseHook, finalizeRelease)
}
