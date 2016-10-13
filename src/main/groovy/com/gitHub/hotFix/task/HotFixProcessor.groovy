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
			java:[
				perfectComponent:{processor, component->
						def project = processor.project
						def hotFixModel = processor.hotFixModel
						if(project.plugins.hasPlugin(org.gradle.api.plugins.JavaPlugin.class)) {
							HotFixUtil.setStringValueIfBlank(component, 'source', 'src/main/java')
							HotFixUtil.setStringValueIfBlank(component, 'processSource', project.sourceSets.main.output.classesDir)
							HotFixUtil.setStringValueIfBlank(component, 'output', "${hotFixModel.targetDir}/WEB-INF/classes")
						}
					},
				process:{ processor, component->
						def project = processor.project
						def hotFixModel = processor.hotFixModel
						Set fileSet = []
						// java编译任务已经运行完
						component.hotFixFileSet.each {
							def classFileName = it.replaceAll('.java$', '.class')
							fileSet << classFileName
						}
						component.hotFixFileSet = fileSet
						
						Set excludes = []
						// java编译任务已经运行完
						component.excludes.each {
							def classFileName = it.replaceAll('.java$', '.class')
							excludes << classFileName
						}
						component.excludes = excludes
					}
				],
			resource:[
				perfectComponent:{ processor, component->
						def project = processor.project
						def hotFixModel = processor.hotFixModel
						if(project.plugins.hasPlugin(org.gradle.api.plugins.JavaPlugin.class)) {
							HotFixUtil.setStringValueIfBlank(component, 'source', 'src/main/resources')
							HotFixUtil.setStringValueIfBlank(component, 'processSource', project.sourceSets.main.output.resourcesDir)
							HotFixUtil.setStringValueIfBlank(component, 'output', "${hotFixModel.targetDir}/WEB-INF/classes")
						}
					}
				],
			webapp:[
				perfectComponent:{ processor, component->
						def project = processor.project
						def hotFixModel = processor.hotFixModel
						// 设置source的默认值，webapp目录不会编译所以不需要设置processSource值
						HotFixUtil.setStringValueIfBlank(component, 'source', 'src/main/webapp')
						component.exclude('**/classes')
						component.output = "${hotFixModel.targetDir}"
					}
				]
	]
	
	@TaskAction
	void process() {
		def components = [:]
		
		components.put(hotFixModel.java.name, hotFixModel.java)
		components.put(hotFixModel.resource.name, hotFixModel.resource)
		components.put(hotFixModel.webapp.name, hotFixModel.webapp)
		hotFixModel.ext.components = components
		
		// 完善HotFixComponent的属性
		for (component in hotFixModel.components.values()) {
			def sysProcessType = DEFAULT_PROCESS_TYPE.get(component.processType)
			if(sysProcessType && sysProcessType.perfectComponent) {
				sysProcessType.perfectComponent(this, component)
			}
			buildLogger.debug('perfected component : {}', component.dump())
		}
		
		// 遍历changeFileSet，识别java、resource、webapp文件，并进行对应的处理
		def changeFileSet = hotFixModel.changeFileSet
		def ignoreFiles = []	// 不处理的变更文件
		changeFileSet.pathSet.each {
			it = it.replaceAll("\\\\", '/');
			boolean isIgnore = true
			for (component in hotFixModel.components.values()) {
				def index = it.indexOf(component.source)
				// 多项目支持需要重新设计考虑，不能仅仅是根据projectName来判断
//				if(index > 0 && it.indexOf("${project.projectDir.name}/${component.source}" ) == -1) {
					//比如/g-fileload/src/main/java/..java,而执行命令是在g-web工程下，将忽悠其他工程目录的修改
					//排除非本工程的变更文件
//					buildLogger.debug('ignore file -> index={}, path={}, [name={},source={}]', index, it, component.name, component.source)
//					ignoreFiles << it
//					break
//				}
				
				if(index > -1) {
					isIgnore = false
					buildLogger.debug('add file -> index={}, path={}, [name={},source={}]', index, it, component.name, component.source)
					//FIXME: 判断是否是在excludes列表中
					//FIXME: 判断对应文件是否存在
					if(it.length() > (index + component.source.length())) {
						component.addHotFixFile(it.substring(index + component.source.length() + 1, it.length()))
					}
					break
				}
			}
			if(isIgnore) {
				ignoreFiles << it
				buildLogger.debug('ignore file -> index = {}, path={}', -1, it)
			}
		}
		buildLogger.debug('ignore {} files: {}', ignoreFiles.size(), ignoreFiles)
		
		hotFixModel.components.each {key, component->
			def sysProcessType = DEFAULT_PROCESS_TYPE.get(component.processType)
			if(sysProcessType && sysProcessType.process) {
				sysProcessType.process(this, component)
			}
			// 执行用户自定义的闭包方法
			if(component.process) {
				component.process(component, ignoreFiles);
			}
			buildLogger.debug('processed component: name = {}, hotFixFileSet = {}', component.name, component.hotFixFileSet)
		}
	}
}
