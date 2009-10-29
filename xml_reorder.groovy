/**
 * Reorders and pretty-print an XML file based on a model.
 *
 * Keeps comments.
 * Limitation:
 ** not full control on the indentation
 ** loses CDATA
 *
 **/


import eval_groovy_xml_dsl

//groovy.grape.Grape.grab(group:'xmlunit', module:'xmlunit', version:'1.0')

static def reorderXml(String modelText, String inputText) {
  def model = null
  if (modelText != null) {
    model = new XmlParser().parseText(modelText)
  }

  def parser = new CommentCollectingParser()
  def root = parser.parseText(inputText)
  return transformXml(model, root, parser, null, null)
}

static def reorderXmlWithGroovyModel(File groovyModelFile, File inputFile) {
  def model = null
  if (groovyModelFile != null) {
    def modelText = eval_groovy_xml_dsl.eval_groovy_to_xml(groovyModelFile)
    model = new XmlParser().parseText(modelText)
  }

  def parser = new CommentCollectingParser()
  def root = parser.parse(inputFile)
  return transformXml(model, root, parser, null, null)
}

static def reorderXml(File modelFile, File inputFile) {
  def model = null
  if (modelFile != null) {
    model = new XmlParser().parse(modelFile)
  }

  def parser = new CommentCollectingParser()
  def root = parser.parse(inputFile)
  return transformXml(model, root, parser, null, null)
}

static def transformXml(File reorderModelFile, File inputFile, String replaceContentScript, List transformArgs) {
  def reorderModel = null
  if (reorderModelFile != null) {
    reorderModel = new XmlParser().parse(reorderModelFile)
  }

  def parser = new CommentCollectingParser()
  def root = parser.parse(inputFile)
  return transformXml(reorderModel, root, parser, replaceContentScript, transformArgs)
}

static def applyTransformScript(Node root, String transformScript, transformArgs) {
  Binding binding = new Binding()
  binding.setVariable("root", root)
  binding.setVariable("args", transformArgs)
  GroovyShell shell = new GroovyShell(binding)
  return shell.evaluate(transformScript)
}

static def transformXml(Node model, Node root, CommentCollectingParser parser, String transformScript, List transformArgs) {
  def writer = new StringWriter()
  if (transformScript) {
    def res = applyTransformScript(root, transformScript, transformArgs)
    // some debugging help
    //if (res)
    //  println res
  }
  new XmlNodeTransformer(model, parser, new PrintWriter(writer)).print(root)
  return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + writer.toString()
}

static def prettyPrintXml(File inputFile) {
  reorderXml(null, inputFile)
}

static def prettyPrintXml(String inputText) {
  reorderXml(null, inputText)
}

modelFilename = this.args[0]
inputFilename = this.args[1]

def result = null

if (modelFilename.endsWith(".groovy"))
  result = reorderXmlWithGroovyModel(new File(modelFilename), new File(inputFilename))
else
  result = reorderXml(new File(modelFilename), new File(inputFilename))

print result
