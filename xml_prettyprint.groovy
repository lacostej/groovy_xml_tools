import transform_functions

inputFilename = this.args[0]

def result = transform_functions.prettyPrintXml(new File(inputFilename))

print result
