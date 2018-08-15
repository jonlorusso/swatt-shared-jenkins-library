def call(params) {

  def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = params
    body()

    stage('Release') {
      when {
        branch "${params.branch}"
      }
      steps {
          createGitBranch version: "${params.version}"
          dockerTag imageName: "${params.imageName}", sourceTag: "${params.tag}", targetTag: "release-${params.version}" 
      }
    }

}
