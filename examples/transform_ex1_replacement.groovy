def child2s = root.findAll{it.name() == 'child2'}
child2s.each{
  it.name = 'child4'
  it.children().clear()
  it.appendNode('foobar')
}

