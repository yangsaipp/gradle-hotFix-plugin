package com.gitHub.hotFix.task

import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.TaskAction

import com.gitHub.hotFix.model.ProjectSCM
import com.gitHub.hotFix.scm.SCMService
import com.gitHub.hotFix.scm.git.GitServiceImpl
import com.gitHub.hotFix.scm.model.SCMLog
import com.gitHub.hotFix.scm.svn.SVNServiceImpl

/**
 * hotFix解析器，生成hotFix文件列表，供processor和generator使用
 * <p>
 * 	典型解析场景：
 * 	<ul>
 * 		<li>读取本地配置获取本次hotFix文件列表</li>
 * 		<li>访问svn仓库获取本次hotFix文件列表</li>
 * 		<li>访问git仓库获取本次hotFix文件列表</li>
 * 	</ul>
 * </p>
 * @author yangsai
 *
 */
class HotFixParser extends DefaultTask {
	static Logger buildLogger = Logging.getLogger(HotFixParser.class);
//	static Logger buildLogger = LoggerFactory.getLogger(HotFixParser.class)
	
	def hotFixModel
	
	@TaskAction
	void parse() {
		def components = [:]
		
		components.put(hotFixModel.java.name, hotFixModel.java)
		components.put(hotFixModel.resource.name, hotFixModel.resource)
		components.put(hotFixModel.webapp.name, hotFixModel.webapp)
		hotFixModel.ext.components = components
		
		SCMService scmService
		ProjectSCM scmInfo
		SCMLog scmlog
		if(hotFixModel.svn) {
			scmService = new SVNServiceImpl()
			scmInfo = hotFixModel.svn
			scmInfo.workingPath = project.projectDir
			scmlog = scmService.getLog(scmInfo, '150', null)
		}else if(hotFixModel.git) {
			scmService = new GitServiceImpl()
			scmInfo = hotFixModel.git
			scmlog = scmService.getLog(scmInfo, '0', null)
		}else {
			File localConfigureFile = new File("${project.projectDir}/hotFix.txt")
			buildLogger.debug('read loacl config file:\n{}.', localConfigureFile.path)
			scmlog = new SCMLog()
			localConfigureFile.eachLine('UTF-8'){
				scmlog.addPath(it)
			}
		}
		
		buildLogger.quiet("log:{}",scmlog)
		def ignoreFiles = []
		scmlog.pathSet.each { 
			it = it.replaceAll("\\\\", '/');
			boolean isIgnore = true
			for (component in hotFixModel.components.values()) {
				def index = it.indexOf(component.source)
				if(index > -1) {
					isIgnore = false
					buildLogger.quiet('{}:{}:[{},{}]', index, it, component.name, component.source)
					//FIXME: 判断是否是在excludes列表中
					component.addHotFixFile(it.substring(index + component.source.length() + 1, it.length()))
					break
				}
			}
			if(isIgnore) {
				buildLogger.quiet('{}:{}', -1, it)
				ignoreFiles << it
			}
		}
		
		buildLogger.quiet('ignore {} files:\n{}', ignoreFiles.size(), ignoreFiles)
		//project.delete(model.output)
		buildLogger.debug('parse local config file complete.')
	}
}
