/**
 * Reorders and pretty-print an XML file based on a model.
 *
 * Keeps comments.
 * Limitation:
 ** not full control on the indentation
 ** loses CDATA
 *
 **/


/**
 * an XmlNodePrinter that
 * given the parser that was used to parse the original xml
 *  and a tree representation of the model
 * prints out a pretty-print version of the reordered XML, together with the comments.
 **/
class MyXmlNodePrinter extends XmlNodePrinter {
  CommentCollectingParser parser
  Node modelRoot
  
  MyXmlNodePrinter(Node modelRoot, CommentCollectingParser parser, PrintWriter writer) {
    super(writer)
    super.setPreserveWhitespace(true)
    this.modelRoot = modelRoot
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

  protected void print(Node n, XmlNodePrinter.NamespaceContext ctx) {
    parser.commentsFor(n).each{printComment(it)}
    super.print(n, ctx)
  }
}

static def reorderXml(File modelFile, File inputFile) {
  def modelParser = new XmlParser()
  def parser = new CommentCollectingParser()

  def model = modelParser.parse(modelFile)
  def root = parser.parse(inputFile)

  def writer = new StringWriter()
  new MyXmlNodePrinter(model, parser, new PrintWriter(writer)).print(root)
  return writer.toString()
}

modelFilename = this.args[0]
inputFilename = this.args[1]

def result = reorderXml(new File(modelFilename), new File(inputFilename))

println "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
print result
