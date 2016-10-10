package com.gitHub.hotFix.model

import nebula.test.ProjectSpec

class HotFixParameterTest extends ProjectSpec{

	def 'should set right value'() {
		given: 'set project property'
		project.ext[HotFixParameter.START_REVISION] = '0'
		project.ext[HotFixParameter.END_REVISION] = '100'
		project.ext[HotFixParameter.AUTHOR] = 'testAuthor,testAuthor212'
		
		when: 'create HotFixParameter with given project'
		HotFixParameter parameter = new HotFixParameter(project)
		
		then: 'HotFixParameter set right value'
		parameter.startRevision == '0'
		parameter.endRevision == '100'
		parameter.get(HotFixParameter.AUTHOR) == 'testAuthor,testAuthor212'
		
	}
}
