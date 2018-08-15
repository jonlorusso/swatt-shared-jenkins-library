def call(String input) {
  return input
    .replaceFirst('^[#\\.]', '') // delete the first letter if it is a period or dash
    .replaceAll('[^a-zA-Z0-9_#\\.]', '_'); // replace everything that's not allowed with an underscore
}
