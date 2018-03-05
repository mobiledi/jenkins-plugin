pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        slackSend(channel: '#wds-jenkins-plugin', teamDomain: 'mobiledi', message: 'Building started')
      }
    }
  }
}