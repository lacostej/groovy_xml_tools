import eval_groovy_xml_dsl

// works with files
def evaluated = eval_groovy_xml_dsl.eval_groovy_to_xml(new File('examples/xml_dsl.groovy'))
def expected = new File('examples/xml_dsl_expected.xml').text

assert evaluated == expected

