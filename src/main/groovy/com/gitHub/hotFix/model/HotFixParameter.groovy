package com.gitHub.hotFix.model

import groovy.transform.Canonical

import org.gradle.api.Project

/**
 * HotFix构建任务参数对象
 * @author yangsai
 *
 */
@Canonical
class HotFixParameter {
	
	/**
	 * 增量生成Svn或者Git开始版本号
	 */
	static String START_REVISION = 'start'
	
	/**
	 * 增量生成Svn或者Git结束版本号
	 */
	static String END_REVISION = 'end'
	
	/**
	 * 增量生成Svn或者Git需要过滤的提交者，多个提交者使用,分割如：gradle hotFix -Pauthor=autor1,autor2
	 */
	static String AUTHOR = 'author'
	
	/**
	 * 插件参数值集合
	 */
	Set<String> paramKey = [START_REVISION, END_REVISION, AUTHOR]
	
	/**
	 * 插件参数信息
	 */
	Map parameter = [:]
	
	public HotFixParameter(Project project) {
		paramKey.each {
			if(project.hasProperty(it)) {
				parameter[it] = project.property(it)
			}
		}
	}
	
	String getStartRevision() {
		return parameter[START_REVISION]
	}
	
	String getEndRevision() {
		return parameter[END_REVISION]
	}
	
	/**
	 * 设置参数
	 * @param key
	 * @return
	 */
	void put(key, value) {
		parameter.put(key, value)
	}
	
	/**
	 * 获取参数
	 * @param key
	 * @return
	 */
	Object get(key) {
		return parameter[key]
	}
	
	
}
