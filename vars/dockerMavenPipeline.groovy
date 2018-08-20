@Library('SwattJenkins') _

def call(Closure pipelineParams) {
	pipeline {
		agent any
		tools { maven 'maven3' }
		environment {
			JOB = "Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})"
			VERSION = readMavenPom().getVersion().replace("-SNAPSHOT", "")
			IMAGE_NAME = readMavenPom().getArtifactId()
			DOCKER_FRIENDLY_BRANCH_NAME = makeDockerTag("${env.BRANCH_NAME}")
			TAG = "${DOCKER_FRIENDLY_BRANCH_NAME}-${VERSION}.${env.BUILD_NUMBER}"
		}
		stages {

			stage ('Start') {
				steps {
					slackSend (color: '#FFFF00', message: "STARTED: ${JOB}")
				}
			}

			stage('Build') {
				steps {
					sh "mvn clean package -DskipTests"
					dockerBuild imageName: IMAGE_NAME, tag: TAG
				}
				post {
					always {
						junit(allowEmptyResults: true, testResults: '**/target/surefire-reports/TEST-*.xml')
					}
				}
			}

			stage('Deploy') {
				steps {
					sh "mvn clean deploy -DskipTests"
					dockerPush imageName: IMAGE_NAME, tag: TAG
				}
				post {
					always {
						junit(allowEmptyResults: true, testResults: '**/target/surefire-reports/TEST-*.xml')
					}
				}
			}

			stage('Release') {
				when { branch "develop"  // FIXME switch to branch "release/*" ?
				}
				steps {
					createGitBranch branchName: "release/${VERSION}"
					dockerTag imageName: IMAGE_NAME, sourceTag: TAG, targetTag: "release-${VERSION}"
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
