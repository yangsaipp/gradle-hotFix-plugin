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
			if(!component.hotFixFileSet.empty) {
				buildLogger.quiet('generate hotfix:{}', component.name)
				buildLogger.quiet(component.dump())
				project.copy {
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
