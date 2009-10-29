import transform_functions

inputFilename = this.args[0]
replaceContentScript = this.args[1]

def args = this.args[2..-1]
def result = transform_functions.transformXml(new File(inputFilename), new File(replaceContentScript), args)

print result



