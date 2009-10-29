import xml_transform

// works with files
def evaluated = xml_transform.xml_transform(new File('examples/transform_ex1_input.xml'), new File('examples/transform_ex1_replacement.groovy'), ['foobar'])
def expected = new File('examples/transform_ex1_output.xml').text

assert evaluated == expected

