package com.gitHub.hotFix.model;

import org.gradle.util.ConfigureUtil

/**
 * hotFix插件扩展参数
 * @author ys
 *
 */
class HotFixModel {
	/**
	 * hotFix输出路径，包括最终生成的hotFix文件名
	 */
	String targetDir = 'build/hotFix/' + new Date().format('yyyy-MM-dd_HHmm')

	ProjectSCM projectSCM = new ProjectSCM(location: 'hotFix.txt', type:'local')

	HotFixComponent java = new HotFixComponent(name: 'java', processType: 'java')
	HotFixComponent resource = new HotFixComponent(name: 'resource', processType: 'resource')
	HotFixComponent webapp = new HotFixComponent(name: 'webapp', processType: 'webapp')
	
	void local(Closure closure) {
		ConfigureUtil.configure(closure, projectSCM)
	}
	
	void git(Closure closure) {
		projectSCM = new ProjectSCM(type:'git')
        ConfigureUtil.configure(closure, projectSCM)
    }
	
	void svn(Closure closure) {
		projectSCM = new ProjectSCM(type:'svn')
		ConfigureUtil.configure(closure, projectSCM)
	}
	
	void java(Closure closure) {
		ConfigureUtil.configure(closure, java)
	}
	
	void resource(Closure closure) {
		ConfigureUtil.configure(closure, resource)
	}
	
	void webapp(Closure closure) {
		ConfigureUtil.configure(closure, webapp)
	}
	
	void dumps() {
		println targetDir
		println projectSCM
		println "java:[${java.toString()}]"
		println "source:[${resource.toString()}]"
		println "webapp:[${webapp.toString()}]"
	}
}