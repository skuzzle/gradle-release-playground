import com.github.breadmoirai.githubreleaseplugin.GithubReleaseTask
import de.skuzzle.semantic.Version

plugins {
    id("com.github.breadmoirai.github-release")
}

require(project == rootProject) { "Release plugin should only be applied to root project" }

val releaseExtension = extensions.create<ReleaseExtension>(ReleaseExtension.NAME).apply {
    mainBranch.convention("main")
    devBranch.convention("dev")

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

    githubReleaseToken.convention(
        providers.systemProperty("RELEASE_GITHUB_TOKEN")
            .orElse(providers.gradleProperty("releaseGithubToken"))
            .orElse("<no token>")
    )
    githubRepoName.convention(
        providers.systemProperty("RELEASE_GITHUB_REPO")
            .orElse(providers.gradleProperty("releaseGithubRepo"))
    )
    githubRepoOwner.convention(
        providers.systemProperty("RELEASE_GITHUB_OWNER")
            .orElse(providers.gradleProperty("releaseGithubOwner"))
    )
}

githubRelease {
    draft.set(true)
    token(releaseExtension.githubReleaseToken)
    owner.set(releaseExtension.githubRepoOwner)
    repo.set(releaseExtension.githubRepoName)
    dryRun.set(releaseExtension.dryRun)
    body.set(releaseExtension.releaseNotesContent)
}

val calculatedVersion = calculateVersion()
rootProject.allprojects { this.version = calculatedVersion }

fun calculateVersion(): String {
    val git = Git(providers, releaseExtension.dryRun, releaseExtension.verbose)
    val latestTagValue = git.lastReleaseTag()
    val latestVersion = latestTagValue.substring(1)
    val pversion = rootProject.findProperty("releaseVersion")?.toString()
    if (pversion != null) {
        return pversion.toString()
    }
    return Version.parseVersion(latestVersion).nextPatch("${git.currentBranch()}-SNAPSHOT").toString()
}

// Task execution order:
// - checkCleanWorkingCopy
// - <beforeReleaseHook: release relevant tasks from sub project>
// - releaseInternal
// - <afterReleaseHook: release relevant tasks from sub project>
// - finalizeRelease

val checkCleanWorkingCopy by tasks.creating(CheckCleanWorkingCopyTask::class.java) {
    releaseExtension.wireUp(this)
}

val releaseInternal by tasks.creating(ReleaseInternalTask::class.java) {
    mustRunAfter(checkCleanWorkingCopy)
    releaseExtension.wireUp(this)
}

val afterReleaseHook by tasks.creating(DefaultTask::class.java) {
    mustRunAfter(releaseInternal)
}

tasks.withType(GithubReleaseTask::class.java).configureEach {
    dependsOn(afterReleaseHook)
}

val finalizeRelease by tasks.creating(FinalizeReleaseTask::class.java) {
    mustRunAfter(releaseInternal, afterReleaseHook)
    releaseExtension.wireUp(this)
}

val release by tasks.creating(DefaultTask::class.java) {
    dependsOn(checkCleanWorkingCopy, releaseInternal, afterReleaseHook, finalizeRelease)
}

rootProject.subprojects {
    val beforeReleaseHook by this.tasks.creating(ReleaseHookTask::class.java) {
        mustRunAfter(checkCleanWorkingCopy)
    }
    release.dependsOn(beforeReleaseHook)
}
