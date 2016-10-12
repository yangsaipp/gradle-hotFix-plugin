package com.gitHub.hotFix.task

import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.TaskAction

import com.gitHub.hotFix.model.HotFixParameter;
import com.gitHub.hotFix.model.ProjectSCM
import com.gitHub.hotFix.scm.LoaclServiceImpl
import com.gitHub.hotFix.scm.SCMService
import com.gitHub.hotFix.scm.git.GitServiceImpl
import com.gitHub.hotFix.scm.model.ChangeFileSet
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
	def hotFixModel
	
	@TaskAction
	void parse() {
		SCMService scmService
		ProjectSCM scmInfo = hotFixModel.projectSCM
		ChangeFileSet changeFileSet
		HotFixParameter paramer = new HotFixParameter(project)
		
		if(scmInfo.isLocal()) {
			scmService = new LoaclServiceImpl()
			if(scmInfo.location instanceof String) {
				scmInfo.location = project.file(scmInfo.location);
			}
			changeFileSet = scmService.getChangeFileSet(scmInfo, paramer, null)
		}else if(scmInfo.isSVN()) {
			scmService = new SVNServiceImpl()
			//获取当前project工程文件夹名到working path的相对路径，用于查询对应目录的日志
			scmInfo.workingPath = project.rootProject.projectDir.path
			if(!paramer.startRevision) {
				throw new RuntimeException("Please specify the SVN start revision. for example:gradle hotFixGenerate -P${HotFixParameter.START_REVISION}=2")
			}
			//FIXME:targetpath处理问题，如复杂工程结构：/root、/root/project1、/root/project1/project1.1
//			String targetpath =  project.rootProject == project ? '' : project.projectDir.name
			changeFileSet = scmService.getChangeFileSet(scmInfo, paramer)
		}else if(scmInfo.isGIT()) {
			scmService = new GitServiceImpl()
			changeFileSet = scmService.getChangeFileSet(scmInfo, paramer)
		}
		hotFixModel.ext.changeFileSet = changeFileSet
		
		buildLogger.debug('complete parser changeFileSet : {}', changeFileSet.dump())
	}
}
