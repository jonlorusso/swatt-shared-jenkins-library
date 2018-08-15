def call(Map Params) {
    stage ('Start') {
      steps {
        slackSend (color: '#FFFF00', message: "STARTED: ${params.job}")
      }
    }
}
