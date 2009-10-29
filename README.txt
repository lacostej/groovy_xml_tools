A small groovy scripts that allows to
* reformat an XML file (aka prettyPrint)
* optionaly reorder an XML file based on a given model
  The model can be written as XML or as a simple Groovy DSL.
* perform transformations to the XML file. The transformations are stored in external groovy script and are 
  implemented following the XmlParser syntax (see http://groovy.codehaus.org/Updating+XML+with+XmlParser)


How to use:

groovy xml_reorder.groovy model.xml original.xml > reformatted.xml

or

groovy xml_reorder.groovy model.groovy original.xml > reformatted.xml


Known limitations
* hardcoded XML declaration...
* no encoding handling (parsing, writing...)
* lack of control of the pretty-printing
* CDATA not handled
* and much more (can be implemented though) !!

