def call(params) {
  scmUrl = params.scmUrl ? params.scmUrl : scm.getUserRemoteConfigs()[0].getUrl().replaceAll('https://', '') 
  gitCredentialsId = params.gitCredentialsId ? params.gitCredentialsId : '80610dce-f3b7-428e-b69f-956eb087225d'
  
  withCredentials([usernamePassword(credentialsId: gitCredentialsId, usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
   	 	sh("echo username: ${GIT_USERNAME}")
	    sh("echo password: ${GIT_PASSWORD}")
  
    	urlEncodedPassword = java.net.URLEncoder.encode("${env.GIT_CREDENTIALS_PSW}", "UTF-8")
    	sh("echo ${urlEncodedPassword}")
    
		sh("git checkout -B ${params.branchName}")
  		sh("git push https://${GIT_USERNAME}:${GIT_PASSWORD}@${scmUrl} ${params.branchName}")
	}
}
