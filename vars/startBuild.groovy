def call(params) {

  def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = params
    body()

    stage ('Start') {
      steps {
        slackSend (color: '#FFFF00', message: "STARTED: ${params.job}")
      }
    }
}
