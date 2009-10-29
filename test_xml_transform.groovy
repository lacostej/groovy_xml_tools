import transform_functions

// works with files
def evaluated = transform_functions.transformXml(new File('examples/transform_ex1_input.xml'), new File('examples/transform_ex1_replacement.groovy'), ['foobar'])
def expected = new File('examples/transform_ex1_output.xml').text

assert evaluated == expected

