import transform_functions

// works with files
def reordered = transform_functions.reorderXml(new File('examples/simple_model.xml'), new File('examples/simple_ex1.xml'))
def expected = new File('examples/simple_ex1_expected.xml').text

assert reordered == expected

// works with groovy XML DSL files
reordered = transform_functions.reorderXmlWithGroovyModel(new File('examples/simple_model_xml.groovy'), new File('examples/simple_ex1.xml'))

assert reordered == expected

// also works with text
reordered = transform_functions.reorderXml(new File('examples/simple_model.xml').text, new File('examples/simple_ex1.xml').text)

assert reordered == expected

// simple pretty print
reordered = transform_functions.prettyPrintXml(new File('examples/simple_ex1.xml'))
expected = new File('examples/simple_ex1_prettied.xml').text

assert reordered == expected

reordered = transform_functions.prettyPrintXml(new File('examples/simple_ex1.xml').text)
assert reordered == expected
