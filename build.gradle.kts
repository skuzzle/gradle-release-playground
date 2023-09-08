import pl.allegro.tech.build.axion.release.domain.preRelease

plugins {
    id("pl.allegro.tech.build.axion-release") version "1.15.4"
    id("com.github.breadmoirai.github-release") version "2.4.1"
}

scmVersion {
    versionCreator("versionWithBranch")
}

allprojects {
//    project.version = scmVersion.version
}



/*project.version = scmVersion.version

release {
    pushReleaseVersionBranch = "main"
    tagTemplate = "v$version"
    git {
        requireBranch = "dev"
    }
}*/


/*tasks.register("Commit files") {
    dependsOn(":readme:generateReadmeAndReleaseNotes")
    "git add README.md RELEASE_NOTES.md".execute()
    "git commit -m \"Update README and Release notes\"".execute()
}*/
