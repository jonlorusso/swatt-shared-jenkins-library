def call(params) {
  SCM_URL = scm.getUserRemoteConfigs()[0].getUrl().replaceAll('https://', '')

  REGISTRY = "docker.dev.ruvpfs.swatt.exchange"
  GIT_CREDENTIALS = credentials('80610dce-f3b7-428e-b69f-956eb087225d')
  GIT_USERNAME = "${env.GIT_CREDENTIALS_USR}"
  GIT_PASSWORD = java.net.URLEncoder.encode("${env.GIT_CREDENTIALS_PSW}", "UTF-8")

  sh("git checkout -B release/${params.version}")

  // TODO need to hide git password from jenkins console
  sh("git push https://${GIT_USERNAME}:${GIT_PASSWORD}@${SCM_URL} release/${params.version}")
}
