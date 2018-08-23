def call(Map pipelineParams) {
  pipeline {
    agent any
      tools {
        maven 'maven3'
      }
    environment {
      JOB = "Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})"
        VERSION = readMavenPom().getVersion().replace("-SNAPSHOT", "")
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
          sh "mvn clean package -DskipTests"
        }
        post {
          always {
            junit(allowEmptyResults: true, testResults: '**/target/surefire-reports/TEST-*.xml')
          }
        }
      }

      stage('Deploy') {
        when {
          branch 'develop'
        }
        steps {
          sh "mvn clean deploy -DskipTests"
        }
        post {
          always {
            junit(allowEmptyResults: true, testResults: '**/target/surefire-reports/TEST-*.xml')
          }
        }
      }

      stage('Release') {
        when { 
          branch "develop"  // FIXME switch to branch "release/*" ?
        }
        steps {
          createGitBranch branchName: "release/${pipelineParams.version}", gitUsername: GIT_USERNAME, gitPassword: GIT_PASSWORD
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

