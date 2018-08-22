def call(Map params) {
  registry = params.registry ? params.registry : "docker.dev.ruvpfs.swatt.exchange"
  sh "docker push ${registry}/${params.imageName}:${params.tag}"
}
