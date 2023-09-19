import de.skuzzle.semantic.Version

// General version workflow:
// - find ancestor tag with version format:
//      version = git describe --tags $(git rev-list --tags --max-count=1) --match="v[0-9]*"
// if branch == main
//      return
// - increment version based on strategy
//      version = version.incrementPatch()
// - if branch == dev append -SNAPSHOT
//      version = version + "-SNAPSHOT"
// - if branch != main append -branch-SNAPSHOT
//      branch = git rev-parse --abbrev-ref HEAD
//      version = version + "-" + branch + "-SNAPSHOT"

// New Release Workflow
// - new tasks: releasePatch / releaseMinor / releaseMajor
// - check workingCopy clean
// - checkout main
// - create & checkout release branch from main
// - checkout main & merge dev branch
// - increment version based on strategy
// - Run pre-release tasks (generate readme)
// - Commit & tag in main
// - Build & publish

val latestTagHash = git("rev-list", "--tags", "--max-count=1")
val latestTagValue = git("describe", "--tags", latestTagHash, "--match=v[0-9]*")
val latestVersion = latestTagValue.substring(1)
val branch = git("rev-parse", "--abbrev-ref", "HEAD")
val pversion = rootProject.property("version")?.toString()
val status = git("status", "--porcelain")


rootProject.allprojects { this.version = determineVersion() }

val gitExtension = extensions.create<GitExtension>(GitExtension.NAME).apply {
    latestReleaseTag.set(latestTagValue)
    commitHash.set(latestTagHash)
    commitHashShort.set(latestTagHash.substring(0, 8))
    cleanWorkingCopy.set(git("status", "--porcelain").isEmpty())
    currentBranch.set(git("rev-parse", "--abbrev-ref", "HEAD"))
    //unpushedCommits.set(git("cherry", "-v").isNotEmpty())
}


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

val checkCleanWorkingCopy by tasks.creating(DefaultTask::class.java) {
    if (status.isNotEmpty()) {
        throw IllegalStateException("Can not release: Working copy not clean: \n$status")
    }
}

val beforeReleaseHook by tasks.creating(DefaultTask::class.java) {
    mustRunAfter(checkCleanWorkingCopy)
}

val releaseInternal by tasks.creating(ReleaseTask::class.java) {
    dependsOn(beforeReleaseHook, checkCleanWorkingCopy)
    this.gitExtension = rootProject.the<GitExtension>()
}

val afterReleaseHook by tasks.creating(DefaultTask::class.java) {
    mustRunAfter(releaseInternal)
}

val release by tasks.creating(DefaultTask::class.java) {
    dependsOn(beforeReleaseHook, releaseInternal, afterReleaseHook)
}

// On dev, last release: 0.18.0
// $ gw currentVersion
// 0.19.0-SNAPSHOT
// $ gw releaseMinor
// -> 0.19.0
// ->

// Classic Release Workflow
// - Ensure we're on dev branch
// - Ensure we're on a SNAPSHOT version
// - Create & checkout temp release branch from dev branch
// - Drop "-SNAPSHOT" from version in gradle.properties
// - Run pre-release tasks (generate Readme/Release notes/docs)
// - Create Release commit + tag
// - Build & Publish
// - Checkout main & merge release branch into main
// - Checkout dev & merge release branch into dev
// - Bump version in gradle.properties
// - Commit new version to dev
// - Git push everything
