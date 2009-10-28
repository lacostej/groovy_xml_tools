import groovy.xml.MarkupBuilder


static def eval_groovy_to_xml(String text) {

  def prefix = '''
import groovy.xml.MarkupBuilder

def writer = new StringWriter()
def xml = new MarkupBuilder(writer)
'''

def suffix = '''writer.toString() '''

  return Eval.me(prefix + 'xml.'+text + suffix) + '\n'
}

static def eval_groovy_to_xml(File file) {
  return eval_groovy_to_xml(file.text)
}

print eval_groovy_to_xml(new File(this.args[0]))
