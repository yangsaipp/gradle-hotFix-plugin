package com.gitHub.hotFix.scm.svn;

import com.gitHub.hotFix.model.ProjectSCM
import com.gitHub.hotFix.scm.model.ChangeFileSet

class SVNServiceImplTest extends spock.lang.Specification {
	ChangeFileSet scmLog
	ProjectSCM scmInfo
	SVNServiceImpl svnServiceImpl  = new SVNServiceImpl()
	
	String svnUrl = 'http://www.svnchina.com/svn/gradle_plugin_test'
	String username = 'testyss'
	String password = 'testyss'
	String testWorkingPath = 'e:\\Workspaces\\workspace_java\\gradle_plugin_test'
	def setup() {
//		scmInfo = new ProjectSCM(username:username, password: password, workingPath: testWorkingPath, url: svnUrl)
//		println "init scmInfo : ${scmInfo}"
	}
	
	def "scm log not empty by svn url"() {
		given:
		scmInfo = new ProjectSCM(username:username, password: password, url: svnUrl)
		scmLog = svnServiceImpl.getChangeFileSet(scmInfo, start, end, targetpath)
		expect:
//		println scmLog.pathSet.size()
//		println scmLog.deletePathSet.size()
//		println scmLog.dump()
		!scmLog.empty
		where:
		start | end || targetpath
		'0' | '-1' || 'test-web' //只获取test-web目录的log
		'0' | '-1' || ''
		'0' | '-1' || null
	}
	
	def 'file string minus test'() {
		given:
//		println new File(string1).name
		expect:
		new File(string1).path - new File(string2).path == string3
		where:
		string1 | string2 || string3
		'E:/Workspaces/workspace_java/gradle_plugin_test/test-web' | 'E:/Workspaces/workspace_java/gradle_plugin_test' || '\\test-web'
		'E:/Workspaces/workspace_java/gradle_plugin_test/test-web' | 'E:/Workspaces/workspace_java/gradle_plugin_test/test-web' || ''
	}
	
	def 'string index test'() {
		expect:
		string1.indexOf(string2) == index
		where:
		string1 | string2 || index
		'/g-fileload/src/main/java/com/comtop/cap/component/loader/exception/LoadException.java'|'src/main/java'||12
		'src/main/java/com/comtop/cap/component/loader/exception/LoadException.java'|'src/main/java'||0
	}
}
