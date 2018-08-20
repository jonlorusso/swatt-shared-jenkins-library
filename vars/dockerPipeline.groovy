def call(Map pipelineParams) {
	pipeline {
		agent any
		environment {
			JOB = "Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})"
			GIT_CREDENTIALS = credentials('80610dce-f3b7-428e-b69f-956eb087225d')
			GIT_USERNAME = "${env.GIT_CREDENTIALS_USR}"
			GIT_PASSWORD = java.net.URLEncoder.encode("${env.GIT_CREDENTIALS_PSW}", "UTF-8")
		}
		stages {

			stage ('Start') {
				steps {
					slackSend (color: '#FFFF00', message: "STARTED: ${JOB}")
				}
			}
			
			stage('Build') {
				steps {
					script {
						for (image in pipelineParams.images) {
							dockerBuild dockerfile: image.dockerfile, imageName: image.imageName, tag: image.tag
						}
					}
				}
			}

			stage('Deploy') {
				steps {
					script {
						for (image in pipelineParams.images) {
							dockerPush imageName: image.imageName, tag: image.tag
						}
					}
				}
			}

			stage('Release') {
				when {
					branch 'develop' // FIXME switch to branch "release/*" ?
				}
				steps {
					createGitBranch branchName: "release/${pipelineParams.version}", gitUsername: GIT_USERNAME, gitPassword: GIT_PASSWORD
					
					script {
						for (image in pipelineParams.images) {
							dockerTag imageName: image.imageName, sourceTag: image.tag, targetTag: image.releaseTag
						}
					}
				}
			}
			
		}
		post {
			success {
				slackSend (color: '#00FF00', message: "SUCCESSFUL: ${JOB}")
			}
			failure {
				slackSend (color: '#FF0000', message: "FAILED: ${JOB}")
			}
		}
	}
}
