def call(Map pipelineParams) {
	pipeline {
		agent any
		environment {
			JOB = "Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})"

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
					createGitBranch branchName: "release/${pipelineParams.version}"
					
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
