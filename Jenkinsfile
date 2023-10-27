pipeline {
  agent {
    docker {
      image 'maven:3.6-jdk-11'
      args '-v /home/jenkins/.gradle:/var/gradle/.gradle -v /home/jenkins/.gnupg:/.gnupg -e GRADLE_OPTS=-Duser.home=/var/gradle'
    }
  }
  environment {
    GITHUB = credentials('Github-Username-Pw')
    RELEASE_GITHUB_TOKEN = credentials('github_registry_release')
    ORG_GRADLE_PROJECT_ghToken = credentials('github_registry_release')
    GIT_ASKPASS='./.git-askpass'
  }
  parameters {
    string(name: 'RELEASE_VERSION', defaultValue: '', description: 'Version to be released')
    booleanParam(name: 'RELEASE_DRY_RUN', defaultValue: false, description: 'Whether to push releases to GitHub')
  }
  stages {
    stage ('Set Git Information') {
      steps {
        sh 'echo \'echo \$GITHUB_PSW\' > ./.git-askpass'
        sh 'chmod +x ./.git-askpass'
        sh 'git config url."https://api@github.com/".insteadOf "https://github.com/"'
        sh 'git config url."https://ssh@github.com/".insteadOf "ssh://git@github.com/"'
        sh 'git config url."https://git@github.com/".insteadOf "git@github.com:"'
        sh 'git config user.email "build@taddiken.online"'
        sh 'git config user.name "Jenkins"'
      }
    }
    stage('Show git status') {
      steps {
        sh 'git status'
      }
    }
    stage('Check preconditions') {
      steps {
        withGradle {
          sh './gradlew checkCleanWorkingCopy'
        }
      }
    }
    stage('Build & Test') {
      steps {
        withGradle {
          sh './gradlew verify'
        }
      }
    }
    stage('Perform release') {
      steps {
        withGradle {
          sh './gradlew generateReadmeAndReleaseNotes build'
          sh './gradlew releaseLocal'
          sh './gradlew pushRelease'
        }
      }
    }
  }
}
