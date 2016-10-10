package com.gitHub.hotFix

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.logging.LogLevel

import com.gitHub.hotFix.model.HotFixComponent;
import com.gitHub.hotFix.model.HotFixModel
import com.gitHub.hotFix.task.*

/**
 * 增量插件
 * @author ys
 *
 */
class HotFixPlugin implements Plugin<Project> {
	static final String TASK_GROUP = "hotFix"
	
	static final String TASK_PARSE = "hotFixParse"
	static final String TASK_PROCESS = "hotFixProcess"
	static final String TASK_GENERATE = "HotFix"
	
	@Override
	public void apply(Project project) {
		HotFixModel model = project.extensions.create("hotFix", HotFixModel)
		configureParseTask(project, model)
		configureProcessTask(project, model)
		configureGenerateTask(project, model)
//		println 'apply project hotfix'
//		println model.dumps()
	}
	
	private Task configureGenerateTask(Project project, HotFixModel model) {
		maybeAddTask(project, TASK_GENERATE, HotFixGenerator) {
			description = 'generate hotfix.'
			hotFixModel = model
			dependsOn TASK_PROCESS
		}
	}
	
	private Task configureProcessTask(Project project, HotFixModel model) {
		maybeAddTask(project, TASK_PROCESS, HotFixProcessor) {
			//task property
			description = 'process hotfix file list.'
			hotFixModel = model
			dependsOn TASK_PARSE
			if(project.plugins.hasPlugin(org.gradle.api.plugins.JavaPlugin.class)) {
				dependsOn 'classes'
			}
		}
	}
	
	
	private Task configureParseTask(Project project, HotFixModel model) {
//		println model.targetDir 输出:null 这时候还未读取信息构建hotFix对象,只有在action里面才能获取到具体的hotFix对象
		maybeAddTask(project, TASK_PARSE, HotFixParser) {
			description = 'parse and get hotFix file list.'
			hotFixModel = model
		}
	}
	
	private Task maybeAddTask(Project project, String taskName, Class taskType, Closure action) {
		if (project.tasks.findByName(taskName)) { 
			return
		}
		def task = project.tasks.create(taskName, taskType)
		task.setGroup(TASK_GROUP)
		project.configure(task, action)
		return task
	}
	
}