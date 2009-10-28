import xml_reorder

inputFilename = this.args[0]

def result = xml_reorder.prettyPrintXml(new File(inputFilename))

print result
