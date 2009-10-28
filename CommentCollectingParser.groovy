import org.xml.sax.ext.LexicalHandler 

/** 
 * http://www.nabble.com/xml-roundtrip-of-comments-and-cdata-td12583230.html
 * @author hchai 
 */ 
class CommentCollectingParser extends XmlParser implements LexicalHandler { 
    private Map<Node, List<String>> commentMap 
    private List<String> lastComments 
    private Node lastNode
    
    CommentCollectingParser() { 
        this(false, true) 
    } 
    
    CommentCollectingParser(boolean validating, boolean namespaceAware) { 
        super(validating, namespaceAware) 
        setProperty('http://xml.org/sax/properties/lexical-handler', this) 
        
        this.commentMap = [:] 
        this.lastComments = [] 
        this.lastNode = null 
    } 
    
    /** 
     * Overridden to associate comments with the last Node that was created. 
     */ 
    @Override 
    protected Node createNode(Node parent, Object name, Map attributes) { 
        lastNode = new Node(parent, name, attributes) 
        commentMap[lastNode] = lastComments 
        lastComments = [] 
        
        return lastNode 
    } 
    
    /** 
     * Returns a possibly-empty List of comment String's that immediately 
     * precede a given Node. Currently, any comments that follow the final Node 
     * in the document are not retrievable by this method. 
     * 
     * @param node  the Node for which to return comments 
     */ 
    List<String> commentsFor(Node node) { 
        return commentMap[node] 
    } 
    
    void comment(char[] ch, int start, int length) {
        lastComments << ch[start..<start+length].join("") 
    } 
    
    void endCDATA() {
//        println "endCDATA"
    } 
    
    void endDTD() {
//        println "endDTD"
    } 
    
    void endEntity(String name) {
//        println "endEntity " + name
    } 
    
    void startCDATA()  {
        println "WARNING: CDATA lost"
    } 
    
    void startDTD(String name, String publicId, String systemId)  {
        println "WARNING: DTD lost"
    } 
    
    void startEntity(String name) {
        println "WARNING: entity lost"
    }
}
