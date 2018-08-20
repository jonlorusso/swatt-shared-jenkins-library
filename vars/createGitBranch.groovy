def call(params) {
  scmUrl = params.scmUrl ? params.scmUrl : scm.getUserRemoteConfigs()[0].getUrl().replaceAll('https://', '') 
  gitCredentialsId = params.gitCredentialsId ? params.gitCredentialsId : '80610dce-f3b7-428e-b69f-956eb087225d'
  
  GIT_CREDENTIALS = credentials(gitCredentialsId)
  GIT_USERNAME = "${env.GIT_CREDENTIALS_USR}"
  GIT_PASSWORD = java.net.URLEncoder.encode("${env.GIT_CREDENTIALS_PSW}", "UTF-8")

  sh("git checkout -B ${params.branchName}")

  // TODO need to hide git password from jenkins console
  sh("git push https://${GIT_USERNAME}:${GIT_PASSWORD}@${scmUrl} ${params.branchName}")
}
