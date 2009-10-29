import xml_reorder

static def xml_transform(File original, File replaceContentScript) {
  return xml_reorder.transformXml(null, original, replaceContentScript.text)
}

inputFilename = this.args[0]
replaceContentScript = this.args[1]

def result = xml_replace(new File(inputFilename), new File(replaceContentScript))

print result



