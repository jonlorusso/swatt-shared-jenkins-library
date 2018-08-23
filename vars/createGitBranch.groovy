def call(params) {
  scmUrl = params.scmUrl ? params.scmUrl : scm.getUserRemoteConfigs()[0].getUrl().replaceAll('https://', '') 

  sh("set -x")
  sh("git checkout -B ${params.branchName}")
  sh("git push https://${params.gitUsername}:${params.gitPassword}@${scmUrl} ${params.branchName}")
  sh("set +x")	
}
