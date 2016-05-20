package com.gitHub.hotFix.task

import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.TaskAction

import com.gitHub.hotFix.model.HotFixComponent

/**
 * hotFix生成任务类
 * @author yangsai
 *
 */
class HotFixGenerator extends DefaultTask {
	static Logger buildLogger = Logging.getLogger(HotFixGenerator.class);
	def hotFixModel
	
	@TaskAction
	void generate() {
		hotFixModel.components.each {key, component->
			if(!component.hotFixFileSet.empty) {
				buildLogger.quiet('generate hotfix {}', component.name)
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
