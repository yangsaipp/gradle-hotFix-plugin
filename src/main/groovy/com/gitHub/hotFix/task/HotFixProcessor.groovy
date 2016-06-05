package com.gitHub.hotFix.task

import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.TaskAction

import com.gitHub.hotFix.model.HotFixComponent
import com.gitHub.hotFix.util.HotFixUtil

/**
 * hotFix 处理器，parser生成hotFix文件列表，processor进行处理。<br>
 * <p>典型处理场景：java类需要编译，文件也需要从编译路径去获取而不是源码路径</p>
 * @author yangsai
 *
 */
class HotFixProcessor extends DefaultTask {
	static Logger buildLogger = Logging.getLogger(HotFixProcessor.class);
	
	def hotFixModel
	
	def static DEFAULT_PROCESS_TYPE = [
			java:[processType:'java',
					process:{processor, component->
						def project = processor.project
						def hotFixModel = processor.hotFixModel
						if(!(component.processSource?.trim())) {
							if(project.plugins.hasPlugin(org.gradle.api.plugins.JavaPlugin.class)) {
								component.processSource = project.sourceSets.main.output.classesDir
								component.output = "${hotFixModel.targetDir}/WEB-INF/classes"
								buildLogger.debug('set java component compile source and output')
							}
						}
						Set fileSet = []
						component.hotFixFileSet.each {
							def classFileName = it.replaceAll('java$', 'class')
							fileSet << classFileName
						}
						component.hotFixFileSet = fileSet
//						FIXME 依赖task jar
					}
				],
			resource:[processType:'resource',
						process:{processor, component->
							def project = processor.project
							def hotFixModel = processor.hotFixModel
							if(!(component.processSource?.trim())) {
								if(project.plugins.hasPlugin(org.gradle.api.plugins.JavaPlugin.class)) {
									component.processSource = project.sourceSets.main.output.resourcesDir
									component.output = "${hotFixModel.targetDir}/WEB-INF/classes"
									buildLogger.debug('set resource component compile source and output')
								}
							}
						}
					],
			webapp:[processType:'webapp',
					process:{processor, component->
						def project = processor.project
						def hotFixModel = processor.hotFixModel
						component.exclude('**/classes')
						component.output = "${hotFixModel.targetDir}"
						buildLogger.debug('set webapp component exclude and output')
					}
				]
	]
	
	@TaskAction
	void process() {
		hotFixModel.components.each {key, component->
			def sysProcessType = DEFAULT_PROCESS_TYPE.get(component.name)
			if(sysProcessType) {
				HotFixUtil.setStringValueIfBlank(component, 'processType', sysProcessType.processType)
				if(DEFAULT_PROCESS_TYPE.get(component.processType)) {
					DEFAULT_PROCESS_TYPE.get(component.processType).process(this,component)
				}
			}
			//FIXME 执行用户自定义的闭包方法
			buildLogger.quiet('process component[name={}] complete', component.name)
			buildLogger.quiet("component -> {}", component.dump)
		}
	}
}
