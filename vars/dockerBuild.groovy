def call(Map params) {
  registry = params.registry ? params.registry : "docker.dev.ruvpfs.swatt.exchange"
  dockerfile = params.dockerfile ? params.dockerfile : 'Dockerfile'
  sh "docker build --file ${dockerfile} --no-cache -t ${registry}/${params.imageName}:${params.tag} ."
}
