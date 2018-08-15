def call(params) {

  def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = params
    body()

    post {
      success {
        slackSend (color: '#00FF00', message: "SUCCESSFUL: ${params.job}")
      }
      failure {
        slackSend (color: '#FF0000', message: "FAILED: ${params.job}")
      }
    }
}
