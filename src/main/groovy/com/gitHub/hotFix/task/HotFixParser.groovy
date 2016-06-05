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
	static String param_start_revision = 'sRevision'
	static String param_end_revision = 'eRevision'
		
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
		String startRevision
		String endRevision
		if(project.hasProperty(param_start_revision)) {
			startRevision = project.property(param_start_revision)
		}
		if(project.hasProperty(param_end_revision)) {
			endRevision = project.property(param_end_revision)
		}
		if(hotFixModel.svn) {
			scmService = new SVNServiceImpl()
			scmInfo = hotFixModel.svn
			//获取当前project工程文件夹名到working path的相对路径，用于查询对应目录的日志
			scmInfo.workingPath = project.rootProject.projectDir.path
			if(!startRevision) {
				throw new RuntimeException("Please specify the SVN start revision. for example:gradle hotFixGenerate -P${param_start_revision}=2")
			}
			if(!endRevision) {
				endRevision = '-1'
			}
			//FIXME:targetpath处理问题，如复杂工程结构：/root、/root/project1、/root/project1/project1.1
			String targetpath =  project.rootProject == project ? '' : project.projectDir.name 
			scmlog = scmService.getLog(scmInfo, startRevision, endRevision, project.projectDir.name)
		}else if(hotFixModel.git) {
			scmService = new GitServiceImpl()
			scmInfo = hotFixModel.git
			scmlog = scmService.getLog(scmInfo, '0', null, project.projectDir.path - project.rootDir.path)
		}else {
			File localConfigureFile = new File("${project.projectDir}/hotFix.txt")
			buildLogger.debug('read loacl config file:\n{}.', localConfigureFile.path)
			scmlog = new SCMLog()
			localConfigureFile.eachLine('UTF-8'){
				scmlog.addPath(it)
			}
		}
		
		
//		buildLogger.quiet("log:{}",scmlog)
		def ignoreFiles = []
		scmlog.pathSet.each { 
			it = it.replaceAll("\\\\", '/');
			boolean isIgnore = true
			for (component in hotFixModel.components.values()) {
				def index = it.indexOf(component.source)
				if(index > 0 && it.indexOf("${project.projectDir.name}/${component.source}" ) == -1) {
					//比如/g-fileload/src/main/java/..java,而执行命令是在g-web工程下，将忽悠其他工程目录的修改
					//排除非本工程的path
					buildLogger.quiet('ignore->index:{},path:{} [name:{},source:{}]', index, it, component.name, component.source)
					ignoreFiles << it
					break
				}
				
				if(index > -1 ) {
					isIgnore = false
					buildLogger.quiet('add->index:{},path:{} [name:{},source:{}]', index, it, component.name, component.source)
					//FIXME: 判断是否是在excludes列表中
					if(it.length() > (index + component.source.length())) {
						component.addHotFixFile(it.substring(index + component.source.length() + 1, it.length()))
					}
					break
				}
			}
			if(isIgnore) {
				ignoreFiles << it
				buildLogger.quiet('ignore->index:{},path:{}', -1, it)
			}
		}
		
		buildLogger.quiet('parse local config file complete.')
		buildLogger.quiet('ignore {} files:\n{}', ignoreFiles.size(), ignoreFiles)
//		buildLogger.quiet('component:{}', ignoreFiles.size(), ignoreFiles)
		hotFixModel.components.each {key,value ->
			buildLogger.quiet('component -> {}', value.dump())
		}
	}
}
