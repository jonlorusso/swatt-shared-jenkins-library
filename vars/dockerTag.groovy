def call(Map params) {
  REGISTRY = "docker.dev.ruvpfs.swatt.exchange"
  
  sh "docker tag ${REGISTRY}/${params.imageName}:${params.sourceTag} ${REGISTRY}/${params.imageName}:${params.targetTag}"
  sh "docker push ${REGISTRY}/${params.imageName}:${params.targetTag}
}
