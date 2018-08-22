def call(Closure pipelineParams) {
  pipeline {
    agent any
      tools {
        maven 'maven3'
      }
    environment {
      JOB = "Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})"
        VERSION = readMavenPom().getVersion().replace("-SNAPSHOT", "")
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
          createGitBranch branchName: "release/${VERSION}"
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

