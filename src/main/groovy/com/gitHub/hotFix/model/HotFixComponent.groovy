package com.gitHub.hotFix.model

/**
 * hotFix组件，一个hotFix组件至少包含来源、输出目录，一次hotFix可以有多个hotFix组件
 * @author yangsai
 *
 */
class HotFixComponent {
	
	String name
	
	/**
	 * hotFix来源目录
	 */
	String source
	
	/**
	 * 处理后目录,例如java编译输出目录、配置文件处理存放目录
	 */
	String processSource
	
	/**
	 * 类型，主要在process阶段使用，目前可配置值为：java、resource、webapp
	 */
	String processType
	/**
	 * 排除文件
	 */
	Set<String> excludes = []
	
	/**
	 * 包含文件
	 */
	Set<String> includes = []
	
	/**
	 * hotfix输出目录
	 */
	String output
	
	void exclude(String exclude) {
		if(exclude.indexOf(',')) {
			exclude.split(',').each {
				if(it) {
					this.excludes << it.trim()
				}
			}
		}else{
			this.excludes << exclude.trim()
		}
	}
	
	void include(String include) {
		if(include.indexOf(',')) {
			include.split(',').each {
				if(it) {
					this.includes << it.trim()
				}
			}
		}else{
			this.includes << include.trim()
		}
	}
	
	/**
	 * hotfix文件集合
	 */
	Set<String> hotFixFileSet = []
	
	boolean addHotFixFile(String file) {
		if(!hotFixFileSet) {
			hotFixFileSet = []
		}
		hotFixFileSet << file
	}
	
	@Override
	String toString() {
		return "[name:${name},source：${source},output：${output},processSource:${processSource},processType:${processType},\n excludes：${excludes};includes：${includes}]"
	}
	
	String dump() {
		return toString() + "\n[name:${name},hotFixFileSet:${hotFixFileSet}]"
	}
}
