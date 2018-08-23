def call(Map params) {
  registry = params.registry ? params.registry : "docker.dev.ruvpfs.swatt.exchange"
  dockerfile = params.dockerfile ? params.dockerfile : 'Dockerfile'
  def argsString = buildArgs.collect{ k, v -> "--build-arg $k=$v" }.join(' ')
  sh "docker build --file ${dockerfile} ${argsString} --no-cache -t ${registry}/${params.imageName}:${params.tag} ."
}
