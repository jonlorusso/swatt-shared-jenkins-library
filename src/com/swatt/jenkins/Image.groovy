class Image {
  String name
  String dockerfile
  String tag
  String releaseTag

  Image(name, dockerfile, tag, releaseTag) {
    this.name = name
    this.dockerfile = dockerfile
    this.tag = tag
    this.releaseTag = releaseTag
  }
}
