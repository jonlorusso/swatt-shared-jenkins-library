@Library('SwattJenkins') _

def call(body) {
  // evaluate the body block, and collect configuration into the object
  def pipelineParams= [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = pipelineParams
  body()

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

      startBuild {
	job = ${JOB}
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

      release {
	branch = "develop"
	version = "${VERSION}"
	imageName = "${IMAGE_NAME}""
	tag = "${TAG}"
      }
    }
  }

  endBuild {
    job = ${JOB}
  }

}
