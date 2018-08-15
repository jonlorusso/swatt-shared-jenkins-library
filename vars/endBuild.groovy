def call(Map params) {
  post {
    success {
      slackSend (color: '#00FF00', message: "SUCCESSFUL: ${params.job}")
    }
    failure {
      slackSend (color: '#FF0000', message: "FAILED: ${params.job}")
    }
  }
}
