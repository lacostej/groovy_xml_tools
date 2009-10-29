import xml_reorder

static def xml_transform(File original, File replaceContentScript, List args) {
  return xml_reorder.transformXml(null, original, replaceContentScript.text, args)
}

inputFilename = this.args[0]
replaceContentScript = this.args[1]

def args = this.args[2..-1]
def result = xml_transform(new File(inputFilename), new File(replaceContentScript), args)

print result



