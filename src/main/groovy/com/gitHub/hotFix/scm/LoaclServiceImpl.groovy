package com.gitHub.hotFix.scm

import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

import com.gitHub.hotFix.model.HotFixParameter;
import com.gitHub.hotFix.model.ProjectSCM
import com.gitHub.hotFix.scm.model.ChangeFileSet

class LoaclServiceImpl implements SCMService {
	
	static Logger buildLogger = Logging.getLogger(LoaclServiceImpl.class);
	@Override
	public ChangeFileSet getChangeFileSet(ProjectSCM scmInfo, HotFixParameter paramer, String targetPath) {
		ChangeFileSet changeFileSet = new ChangeFileSet()
		File localConfigureFile = scmInfo.location;
		buildLogger.debug('read loacl config file: {}.', localConfigureFile.path)
		if(localConfigureFile.exists()) {
			changeFileSet = new ChangeFileSet()
			localConfigureFile.eachLine('UTF-8'){
				changeFileSet.addPath(it)
			}
			return changeFileSet;
		}else {
			throw new RuntimeException(localConfigureFile.path + "不存在, 无法获取变更文件信息");
		}
		
	}

}
