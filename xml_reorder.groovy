/**
 * Reorders and pretty-print an XML file based on a model.
 *
 * Keeps comments.
 * Limitation:
 ** not full control on the indentation
 ** loses CDATA
 *
 **/


// require(groupId:'xmlunit', artifactId:'xmlunit', version:'1.0')
import org.custommonkey.xmlunit.*
import eval_groovy_xml_dsl

groovy.grape.Grape.grab(group:'xmlunit', module:'xmlunit', version:'1.0')

/**
 * an XmlNodePrinter that
 * given the parser that was used to parse the original xml
 *  and a tree representation of the model
 * prints out a pretty-print version of the reordered XML, together with the comments.
 **/
class XmlNodeTransformer extends XmlNodePrinter {
  CommentCollectingParser parser
  Node modelRoot
  
  /* if modelRoot is null, we just pretty print */
  XmlNodeTransformer(Node reorderModelRoot, CommentCollectingParser parser, PrintWriter writer) {
    super(writer)
    super.setPreserveWhitespace(true)
    this.modelRoot = reorderModelRoot
    this.parser = parser
  }

  protected void printComment(String comment) {
    printLineBegin()
    if (comment != null) {
      out.print("<!--")
      out.print(comment);
      out.print("-->")
    }
    printLineEnd()
  }

  public void printList(List list, XmlNodePrinter.NamespaceContext ctx) {
    if (modelRoot != null)
      reorderList(list)
    super.printList(list, ctx)
  }

  private void reorderList(List list) {
    if (list != null && list.size() && list[0] instanceof Node) {
      Node parent = list[0].parent()
      final Node modelParent = findModelParentNode(parent)
      if (modelParent != null) {
        def c= [
         compare: {a,b -> index(modelParent, a).compareTo(index(modelParent, b)) }
        ] as Comparator
        Collections.sort(list, c)
      }
    }
  }

  // return the index of the child of the modelParent with the same name as the specified child, or the number of the children if not found
  int index(Node modelParent, Node childToMatch) {
    int idx = modelParent.children().size() + 1
    int i = 0
    for (Node child: modelParent.children()) { 
      if (localPart(child.name()) == localPart(childToMatch.name())) {
        idx = i
        break
      }
      i++
    }
    return idx
  }

  private String localPart(Object o) {
    if (o instanceof String) {
      return o
    } else if (o instanceof groovy.xml.QName) {
      return o.getLocalPart()
    } else {
      throw new IllegalStateException("Can't find localPart of " + o.getClass() + " " + o);
    }
  }

  // find the matching node in the model or null if not existed
  private Node findModelParentNode(Node n) {
    List l = parentTreeAsListFromTop(n)
    Node modelNode = modelRoot
    def i = 0
    if (localPart(modelNode.name()) != localPart(l[0].name())) {
      // non matching roots..
      return null
    }
    i++
    if (i >= l.size())
      return modelNode
    while (true) {
      // problem !
      if (i + 1 > l.size())
        throw new Exception("We should never traverse more nodes than we have")

      //println "--" +  l[i].name().toString() 
      //modelNode.children().each{ println it.name()}
      modelNode = modelNode.children().find{ localPart(it.name()) == localPart(l[i].name()) }
      if (modelNode == null)
        // node isn't in our model
        return null

      i++
      if (i == l.size()) 
        return modelNode
    }
  }

  List parentTreeAsListFromTop(Node n) {
    List l = []
    while (n != null) {
      l << n
      n = n.parent()
    }
    Collections.reverse(l)
    return l
  }

  // print with comments
  protected void print(Node n, XmlNodePrinter.NamespaceContext ctx) {
    parser.commentsFor(n).each{printComment(it)}
    super.print(n, ctx)
  }
}

static def reorderXml(String modelText, String inputText) {
  def model = null
  if (modelText != null) {
    model = new XmlParser().parseText(modelText)
  }

  def parser = new CommentCollectingParser()
  def root = parser.parseText(inputText)
  return transformXml(model, root, parser, null)
}

static def reorderXmlWithGroovyModel(File groovyModelFile, File inputFile) {
  def model = null
  if (groovyModelFile != null) {
    def modelText = eval_groovy_xml_dsl.eval_groovy_to_xml(groovyModelFile)
    model = new XmlParser().parseText(modelText)
  }

  def parser = new CommentCollectingParser()
  def root = parser.parse(inputFile)
  return transformXml(model, root, parser, null)
}

static def reorderXml(File modelFile, File inputFile) {
  def model = null
  if (modelFile != null) {
    model = new XmlParser().parse(modelFile)
  }

  def parser = new CommentCollectingParser()
  def root = parser.parse(inputFile)
  return transformXml(model, root, parser, null)
}

static def transformXml(File reorderModelFile, File inputFile, String replaceContentScript) {
  def reorderModel = null
  if (reorderModelFile != null) {
    reorderModel = new XmlParser().parse(reorderModelFile)
  }

  def parser = new CommentCollectingParser()
  def root = parser.parse(inputFile)
  return transformXml(reorderModel, root, parser, replaceContentScript)
}

static def applyTransformScript(Node root, String transformScript) {
  Binding binding = new Binding()
  binding.setVariable("root", root)
  GroovyShell shell = new GroovyShell(binding)
  return shell.evaluate(transformScript)
}

static def transformXml(Node model, Node root, CommentCollectingParser parser, String transformScript) {
  def writer = new StringWriter()
  if (transformScript) {
    def res = applyTransformScript(root, transformScript)
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
