pipeline {
  agent {
    docker {
      image 'maven:3.6-jdk-11'
      args '-v /home/jenkins/.gradle:/var/gradle/.gradle -v /home/jenkins/.gnupg:/.gnupg -e GRADLE_OPTS=-Duser.home=/var/gradle'
    }
  }
  environment {
    GITHUB = credentials('Github-Username-Pw')
    GITHUB_RELEASE_TOKEN = credentials('github_registry_release')
    GIT_ASKPASS='./.git-askpass'
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
    stage('Build') {
      steps {
        sh './gradlew build'
      }
    }
    stage('Show git status') {
      steps {
        sh 'git status'
      }
    }
    stage('Release') {
      steps {
        sh './gradlew release'
      }
    }
    stage('Release GitHub') {
      steps {
        sh 'git checkout main'
        sh './gradlew githubRelease -Pgh_token=${GITHUB_RELEASE_TOKEN}'
      }
    }
  }
}
