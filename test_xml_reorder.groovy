import xml_reorder

def reordered = xml_reorder.reorderXml(new File('examples/simple_model.xml'), new File('examples/simple_ex1.xml'))
def expected = new File('examples/simple_ex1_expected.xml').text

assert reordered == expected

reordered = xml_reorder.prettyPrintXml(new File('examples/simple_ex1.xml'))
expected = new File('examples/simple_ex1_prettied.xml').text

assert reordered == expected
