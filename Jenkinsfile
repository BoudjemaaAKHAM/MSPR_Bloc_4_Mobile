pipeline {
  agent any
    options {
    timestamps()
  }
  stages {
  stage('Build') {
      steps {
        bat './gradlew assembleDebug'
      }
    }
  stage('Tests') {
      steps {
        bat './gradlew testDebug'
      }
    }
  }
}
