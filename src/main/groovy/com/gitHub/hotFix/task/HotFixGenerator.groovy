package com.gitHub.hotFix.task

import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.logging.LoggingManager
import org.gradle.api.tasks.TaskAction

/**
 * hotFix生成任务类
 * @author yangsai
 *
 */
class HotFixGenerator extends DefaultTask {
	Logger buildLogger = Logging.getLogger(HotFixGenerator.class);
	def hotFixModel
	
	@TaskAction
	void generate() {
		//delete target directory if exist
		project.delete(hotFixModel.targetDir)
		hotFixModel.components.each {key, component->
		buildLogger.debug('generate hotfix by component')
			if(!component.hotFixFileSet.empty) {
				buildLogger.debug('======== component ========')
				buildLogger.debug(component.dump())
				buildLogger.debug('===========================')
				project.copy {
					// 若processSource没设置代表不会有编译等文件处理过程，那么直接使用source目录
					from component.processSource ? component.processSource: component.source, {
						exclude component.excludes
					}
					into component.output
					include component.hotFixFileSet
				}
			}
		}
	}
}
