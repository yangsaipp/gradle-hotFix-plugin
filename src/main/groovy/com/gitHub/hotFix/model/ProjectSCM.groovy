package com.gitHub.hotFix.model

import org.gradle.plugins.ide.eclipse.model.EclipseClasspath;
import org.gradle.plugins.ide.eclipse.model.EclipseProject;
import org.gradle.plugins.ide.eclipse.model.EclipseWtp;

/**
 * 配置项目使用仓库信息
 *
 * <pre autoTested=''>
 * apply plugin: 'java'
 *
 * hotFix {
 *   svn {
 *   	url = 'http:...'
 *   	username = 'test'
 *   	password = 'test'
 *   }
 * }
 * </pre>
 */
class ProjectSCM {
	/**
	 * 仓库访问地址
	 * <p>
	 * See {@link ProjectRepository} for an example.
	 */
	String url
	
	/**
	 * 访问用户名
	 * <p>
	 * See {@link ProjectRepository} for an example.
	 */
	String username
	
	/**
	 * 访问密码
	 * <p>
	 * See {@link ProjectRepository} for an example.
	 */
	String password
	
	/**
	 * SCM工作目录
	 * <p>
	 * See {@link ProjectRepository} for an example.
	 */
	String workingPath
	
	@Override
	String toString() {
		return "url：${this.url};username：${this.username};password：${this.password}"
	}
	
}