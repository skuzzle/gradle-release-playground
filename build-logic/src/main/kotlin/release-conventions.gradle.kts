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
val version = latestTagValue.substring(1)

val releaseExtension = extensions.create<ReleaseExtension>(ReleaseExtension.NAME).apply {
    releaseBranches.convention(setOf("dev"))
    releaseRequested.convention(false)
    incrementVersionPart.convention(VersionIncrement.PATCH)
}

val gitExtension = extensions.create<GitExtension>(GitExtension.NAME).apply {
    latestReleaseTag.set(latestTagValue)
    commitHash.set(latestTagHash)
    commitHashShort.set(latestTagHash.substring(0, 8))
    cleanWorkingCopy.set(git("status", "--porcelain").isEmpty())
    currentBranch.set(git("rev-parse", "--abbrev-ref", "HEAD"))
    //unpushedCommits.set(git("cherry", "-v").isNotEmpty())
}

val versionExtension = extensions.create<VersionExtension>(VersionExtension.NAME)

val latestRelease = Version.parseVersion(version)
val calculatedVersion = incrementVersion(gitExtension, releaseExtension)
val decoratedVersion = decorateVersion(calculatedVersion, gitExtension, releaseExtension)

versionExtension.apply {
    latestReleaseVersion.set(latestRelease)
    developmentVersion.set(decoratedVersion)
    nextReleaseVersion.set(decoratedVersion.toStable())
}

println(gitExtension)

rootProject.allprojects { this.version = versionExtension.developmentVersion.get().toString() }

fun incrementVersion(gitExt: GitExtension, releaseExtension: ReleaseExtension): Version {
    val tag = gitExt.latestReleaseTag.get()
    val parsedVersion = Version.parseVersion(tag.substring(1))

    when (releaseExtension.incrementVersionPart.get()) {
        VersionIncrement.MAJOR -> return parsedVersion.nextMajor()
        VersionIncrement.MINOR -> return parsedVersion.nextMinor()
        VersionIncrement.PATCH -> return parsedVersion.nextPatch()
    }
}

fun decorateVersion(calculatedVersion: Version, gitExtension: GitExtension, releaseExtension: ReleaseExtension): Version {
    val branch = gitExtension.currentBranch.get()
    val isReleaseBranch = releaseExtension.releaseBranches.get().contains(branch)
    val snapshot = !releaseExtension.releaseRequested.get() || !isReleaseBranch

    println(releaseExtension)

    var preRelease = if (snapshot) "SNAPSHOT" else ""
    if (!isReleaseBranch) {
        preRelease = "$branch-SNAPSHOT"
    }
    return calculatedVersion.withPreRelease(preRelease)
}

fun git(vararg args: String): String {
    val fullArgs = listOf("git") + listOf(*args)
    val exec = providers.exec { commandLine(fullArgs) }
    val output = exec.standardOutput.asText.get().trim()

    println("\$ ${fullArgs.joinToString(" ")}: $output")
    return output
}

val prepareRelease by tasks.creating(PrepareReleaseTask::class.java) {
    description = "Releasesesese"
    group = "release"
    this.gitExtension = project.the<GitExtension>()
    this.versionExtension = project.the<VersionExtension>()

}

/*GradleConnector
    .newConnector()
    .useBuildDistribution()
    .forProjectDirectory(layout.projectDirectory.asFile)
    .connect()
    .use { projectConnection ->
        val buildLauncher = projectConnection
            .newBuild()
            .forTasks(release)
            .setStandardInput(System.`in`)
            .setStandardOutput(System.out)
            .setStandardError(System.err)

        buildLauncher.run()
    }*/

val currentVersion by tasks.creating(DefaultTask::class.java) {
    val exti = project.the<VersionExtension>()
    doLast {
        println(exti.developmentVersion.get())
    }
}

val lastReleasedVersion by tasks.creating(DefaultTask::class.java) {
    val exti = project.the<VersionExtension>()
    doLast {
        println(exti.latestReleaseVersion.get())
    }
}

val nextReleaseVersion by tasks.creating(DefaultTask::class.java) {
    val exti = project.the<VersionExtension>()
    doLast {
        println(exti.nextReleaseVersion.get())
    }
}

val beforeReleaseHook by tasks.creating(DefaultTask::class.java) {
    mustRunAfter(prepareRelease)
}

val release by tasks.creating(DefaultTask::class.java) {
    dependsOn(prepareRelease, beforeReleaseHook)
}

val releaseMinor by tasks.creating(DefaultTask::class.java) {
    dependsOn(prepareRelease, beforeReleaseHook, currentVersion)
    releaseExtension.incrementVersionPart = VersionIncrement.MINOR
    releaseExtension.releaseRequested = true
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
