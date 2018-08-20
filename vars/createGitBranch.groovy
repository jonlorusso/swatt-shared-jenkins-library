def call(params) {
  scmUrl = params.scmUrl ? params.scmUrl : scm.getUserRemoteConfigs()[0].getUrl().replaceAll('https://', '') 
  gitCredentialsId = params.gitCredentialsId ? params.gitCredentialsId : '80610dce-f3b7-428e-b69f-956eb087225d'
  
  sh("echo ${scmUrl}")
  sh("echo ${gitCredentialsId}")
  GIT_CREDENTIALS = credentials(gitCredentialsId)
  sh("echo ${GIT_CREDENTIALS}")
  GIT_USERNAME = "${env.GIT_CREDENTIALS_USR}"
  sh("echo ${GIT_USERNAME}")
  GIT_PASSWORD = java.net.URLEncoder.encode("${env.GIT_CREDENTIALS_PSW}", "UTF-8")
  sh("echo ${GIT_PASSWORD}")

  sh("git checkout -B ${params.branchName}")

  // TODO need to hide git password from jenkins console
  sh("git push https://${GIT_USERNAME}:${GIT_PASSWORD}@${scmUrl} ${params.branchName}")
}
