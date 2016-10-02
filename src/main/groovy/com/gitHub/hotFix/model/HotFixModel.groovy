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

	ProjectSCM git
	ProjectSCM svn

	HotFixComponent java
	HotFixComponent resource
	HotFixComponent webapp
	
	
	void git(Closure closure) {
		if(!git) {
			git = new ProjectSCM()
		}
        ConfigureUtil.configure(closure, git)
    }
	
	void svn(Closure closure) {
		if(!svn) {
			svn = new ProjectSCM()
		}
		ConfigureUtil.configure(closure, svn)
	}
	
	void java(Closure closure) {
		if(!java) {
			java = new HotFixComponent(name: 'java', processType: 'java')
		}
		ConfigureUtil.configure(closure, java)
	}
	
	void resource(Closure closure) {
		if(!resource) {
			resource = new HotFixComponent(name: 'resource', processType: 'resource')
		}
		ConfigureUtil.configure(closure, resource)
	}
	
	void webapp(Closure closure) {
		if(!webapp) {
			webapp = new HotFixComponent(name: 'webapp', processType: 'webapp')
		}
		ConfigureUtil.configure(closure, webapp)
	}
	
	void dumps() {
		println targetDir
		if(git){
			println "git:[${git.toString()}]"
		}
		if(svn){
			println "svn:[${svn.toString()}]"
		}
		if(java){
			println "java:[${java.toString()}]"
		}
		if(resource){
			println "source:[${resource.toString()}]"
		}
		if(webapp){
			println "webapp:[${webapp.toString()}]"
		}
	}
}