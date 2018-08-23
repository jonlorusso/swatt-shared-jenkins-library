def call(Map params) {
  registry = params.registry ? params.registry : "docker.dev.ruvpfs.swatt.exchange"
  sh "docker tag ${registry}/${params.imageName}:${params.sourceTag} ${registry}/${params.imageName}:${params.targetTag}"
  dockerPush imageName: params.imageName, tag: params.targetTag
}
