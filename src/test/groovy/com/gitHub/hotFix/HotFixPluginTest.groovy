package com.gitHub.hotFix

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

class HotFixPluginTest {
	
	@Test
	public void greeterPluginAddsGreetingTaskToProject() {
		Project project = ProjectBuilder.builder().build()
		project.pluginManager.apply 'java'
		project.pluginManager.apply 'hotFix'

//		assertTrue(project.tasks.hello instanceof HotFixPlugin)
		project.tasks.hotFixGenerate.execute()
	}
}
