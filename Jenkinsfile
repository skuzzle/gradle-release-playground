pipeline {
  agent {
    docker {
      image 'eclipse-temurin:11'
      args '-v /home/jenkins/.gradle:/var/gradle/.gradle -v /home/jenkins/.gnupg:/.gnupg -e GRADLE_OPTS=-Duser.home=/var/gradle'
    }
  }
  stages {
    stage('Build') {
      steps {
        sh './gradlew build'
      }
    }
    stage('Snapshot') {
      when {
        branch 'dev'
      }
      steps {
        sh './gradlew final'
      }
    }
    stage('Release') {
      when {
        branch 'master'
      }
      steps {
        sh './gradlew final'
      }
    }
  }
}
