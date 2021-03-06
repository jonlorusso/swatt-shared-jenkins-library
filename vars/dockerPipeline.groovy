def call(Map pipelineParams) {
  pipeline {
    agent any
      environment {
        JOB = "Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})"
          GIT_CREDENTIALS = credentials('bitbucket-jenkins')
          GIT_USERNAME = "${env.GIT_CREDENTIALS_USR}"
          GIT_PASSWORD = java.net.URLEncoder.encode("${env.GIT_CREDENTIALS_PSW}", "UTF-8")
      }
    stages {

      stage ('Start') {
        steps {
          slackSend (color: '#FFFF00', message: "STARTED: ${JOB}")
        }
      }

      stage('Build Image') {
        steps {
          script {
            for (image in pipelineParams.images) {
              dockerBuild buildArgs: pipelineParams.buildArgs, dockerfile: image.dockerfile, imageName: image.name, tag: image.tag
            }
          }
        }
      }

      // test??

      // since portal has a dedicated staging Dockerfile,
      // we expect the staging tag to be passed in from the outside.
      stage('Push Image(s)') {
        when {
          branch 'develop'
        }
        steps {
          script {
            for (image in pipelineParams.images) {
              dockerPush imageName: image.name, tag: image.tag
            }
          }
        }
      }

      stage('Release') {
        when {
          branch 'develop' // FIXME switch to branch "release/*" ?
        }
        steps {
//          createGitBranch branchName: "release/${pipelineParams.version}", gitUsername: GIT_USERNAME, gitPassword: GIT_PASSWORD

            script {
              for (image in pipelineParams.images) {
                dockerTag imageName: image.name, sourceTag: image.tag, targetTag: image.releaseTag
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
