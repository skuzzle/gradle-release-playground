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
}
