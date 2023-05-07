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
  post {
        success {
            office365ConnectorSend webhookUrl: 'https://ifagparis.webhook.office.com/webhookb2/4c2906f3-cff5-4826-8a2b-52ba2bae874d@b6e77e52-65f6-4dec-b7d9-8e45f36a536f/IncomingWebhook/8091df69c50c4271a62b0be991c079d8/872e452d-5988-4b28-949b-4ebf222982a6',
            message: 'Job Name : ' + "${env.JOB_NAME}" + ' | Build Number : ' + "${env.BUILD_NUMBER}",// + '/ lance par : ' + "${env.COMMIT_AUTHOR}" + '/ commit : ' + "${env.COMMIT_ID}" + '/ lien commit : ' + "${env.BUILD_URL/commit/${env.COMMIT_ID}}",
            status: 'Success',
            color: '#33cc33'
        }
        failure {
            office365ConnectorSend webhookUrl: 'https://ifagparis.webhook.office.com/webhookb2/4c2906f3-cff5-4826-8a2b-52ba2bae874d@b6e77e52-65f6-4dec-b7d9-8e45f36a536f/IncomingWebhook/8091df69c50c4271a62b0be991c079d8/872e452d-5988-4b28-949b-4ebf222982a6',
            message: 'Job Name : ' + "${env.JOB_NAME}" + ' | Build Number : ' + "${env.BUILD_NUMBER}",// + '/ lance par : ' + "${env.COMMIT_AUTHOR}" + '/ commit : ' + "${env.COMMIT_ID}" + '/ lien commit : ' + "${env.BUILD_URL/commit/${env.COMMIT_ID}}",
            status: 'Failure',
            color: '#ff3300'
        }
    }
}
