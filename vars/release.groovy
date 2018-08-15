def call(Map params) {
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
