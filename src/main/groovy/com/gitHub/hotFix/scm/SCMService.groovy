package com.gitHub.hotFix.scm

import com.gitHub.hotFix.model.ProjectSCM
import com.gitHub.hotFix.scm.model.SCMLog

interface SCMService {
	
	/**
	 * 获取SCM log 用于生成hotFix
	 * @param scmInfo
	 * @param startRevision
	 * @param endRevision
	 * @return
	 */
	SCMLog getLog(ProjectSCM scmInfo, String startRevision, String endRevision) 
	
}
