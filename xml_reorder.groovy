import transform_functions

modelFilename = this.args[0]
inputFilename = this.args[1]

def result = null

if (modelFilename.endsWith(".groovy"))
  result = transform_functions.reorderXmlWithGroovyModel(new File(modelFilename), new File(inputFilename))
else
  result = transform_functions.reorderXml(new File(modelFilename), new File(inputFilename))

print result
