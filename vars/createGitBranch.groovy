def call(params) {
  scmUrl = params.scmUrl ? params.scmUrl : scm.getUserRemoteConfigs()[0].getUrl().replaceAll('https://', '') 
  gitCredentialsId = params.gitCredentialsId ? params.gitCredentialsId : '80610dce-f3b7-428e-b69f-956eb087225d'
  
  sh("echo username: ${params.gitUsername}")
  sh("echo password: ${params.gitPassword}")
    
  sh("git checkout -B ${params.branchName}")
  sh("set -x")
  sh("git push https://${params.gitUsername}:${params.gitPassword}@${scmUrl} ${params.branchName}")
  sh("set +x")	
}
