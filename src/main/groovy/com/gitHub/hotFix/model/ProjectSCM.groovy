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
 * 	 ..
 *   svn {
 *   	url = 'http:...'
 *   	username = 'test'
 *   	password = 'test'
 *   }
 *   ...
 * }
 * </pre>
 */
class ProjectSCM {
	/**
	 * 仓库访问地址
	 * <p>
	 * See {@link ProjectSCM} for an example.
	 */
	String url
	
	/**
	 * 访问用户名
	 * <p>
	 * See {@link ProjectSCM} for an example.
	 */
	String username
	
	/**
	 * 访问密码
	 * <p>
	 * See {@link ProjectSCM} for an example.
	 */
	String password
	
	/**
	 * SCM工作目录
	 * <p>
	 * See {@link ProjectSCM} for an example.
	 */
	String workingPath
	
	/**
	 * SCM类型  git, svn, local
	 */
	String type
	
	/**
	 * 项目所在SCM根路径的相对路径， 用来过滤SCM的log信息<br>
	 * 如：svn地址https://7m0gp72.comtop.local/svn/testSVN/，项目目录https://7m0gp72.comtop.local/svn/testSVN/trunk/<br>
	 * 则targetPath可以配置为trunk
	 */
	String targetPath
	
	/**
	 * 从本地文件中读取
	 *
	 * <pre autoTested=''>
	 * apply plugin: 'java'
	 *
	 * hotFix {
	 * 	 ..
	 *   local {
	 *   	location = 'hotFix.txt'
	 *   }
	 *   ...
	 * }
	 * </pre>
	 */
	def location
	
	boolean isSVN() {
		return type == 'svn';
	}
	
	boolean isLocal() {
		return type == 'local';
	}
	
	boolean isGIT() {
		return type == 'git';
	}
	
	@Override
	String toString() {
		if(local) {
			return "type=${this.type}, location=${location}"
		}else {
			return "type=${this.type}, url=${this.url}, username=${this.username}, password=${this.password}, workingPath=${this.workingPath}"
		}
	}
	
}