import transform_functions

modelFilename = this.args[0]
inputFilename = this.args[1]

def result = null

if (modelFilename.endsWith(".groovy"))
  result = reorderXmlWithGroovyModel(new File(modelFilename), new File(inputFilename))
else
  result = reorderXml(new File(modelFilename), new File(inputFilename))

print result
