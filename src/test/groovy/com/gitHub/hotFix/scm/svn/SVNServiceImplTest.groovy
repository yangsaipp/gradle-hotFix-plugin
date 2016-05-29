package com.gitHub.hotFix.scm.svn;

import static org.junit.Assert.*

import org.junit.Before
import org.junit.Test

import com.gitHub.hotFix.model.ProjectSCM
import com.gitHub.hotFix.scm.model.SCMLog

class SVNServiceImplTest extends spock.lang.Specification {
	SCMLog scmLog
	ProjectSCM scmInfo
	SVNServiceImpl svnServiceImpl  = new SVNServiceImpl()
	def setup() {
		scmInfo = new ProjectSCM(username:'testyss', password: 'testyss', workingPath: 'e:\\Workspaces\\workspace_java\\gradle_plugin_test')
		println "init scmInfo : ${scmInfo}"
	}
	
//	@Test
//	public void testGetLog() {
//		scmLog = new SVNServiceImpl().getLog(scmInfo, "0", "-1")
//		assertNotNull("log can not be null", scmLog)
//	}
	
	def "scm log 不能为空"() {
		expect:
		svnServiceImpl.getLog(scmInfo, "0", "-1") != null
	}

}
