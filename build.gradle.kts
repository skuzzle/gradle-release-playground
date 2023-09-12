import pl.allegro.tech.build.axion.release.domain.preRelease

plugins {
    id("pl.allegro.tech.build.axion-release") version "1.15.4"
    id("com.github.breadmoirai.github-release") version "2.4.1"
}

scmVersion {
    versionCreator("simple")
}



/*release {
    pushReleaseVersionBranch = "main"
    tagTemplate = "v$version"
    git {
        requireBranch = "dev"
    }
}*/

