import xml_reorder

// works with files
def reordered = xml_reorder.reorderXml(new File('examples/simple_model.xml'), new File('examples/simple_ex1.xml'))
def expected = new File('examples/simple_ex1_expected.xml').text

assert reordered == expected

// also works with text
reordered = xml_reorder.reorderXml(new File('examples/simple_model.xml').text, new File('examples/simple_ex1.xml').text)

assert reordered == expected

// simple pretty print
reordered = xml_reorder.prettyPrintXml(new File('examples/simple_ex1.xml'))
expected = new File('examples/simple_ex1_prettied.xml').text

assert reordered == expected

reordered = xml_reorder.prettyPrintXml(new File('examples/simple_ex1.xml').text)
assert reordered == expected
