def call(Closure pipelineParams) {
  pipeline {
    agent any
      tools {
        maven 'maven3'
      }
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
      stage('Deploy') {
        steps {
          script {
            sh "mvn clean deploy -DskipTests -Ddockerfile.tag=${TAG} --activate-profiles docker"

              // Spotify Docker plugin:
              // mvn package -> .jar
              // mvn deploy -> artifactory
              // docker build -t NAME .
              // docker push NAME
          }
        }
      }
      stage('Release') {
        when {
          branch "develop"
        }
        steps {
          createGitBranch version: "${VERSION}"
            dockerTag imageName: "${IMAGE_NAME}", sourceTag: "${TAG}", targetTag: "release-${VERSION}" 
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
