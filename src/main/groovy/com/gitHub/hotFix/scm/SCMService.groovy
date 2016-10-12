package com.gitHub.hotFix.scm

import com.gitHub.hotFix.model.HotFixParameter
import com.gitHub.hotFix.model.ProjectSCM
import com.gitHub.hotFix.scm.model.ChangeFileSet

interface SCMService {
	
	/**
	 * 获取SCM log 用于生成hotFix
	 * @param scmInfo
	 * @param paramer 任务构建参数
	 * @param targetPath 需要获取log的path
	 * @return
	 */
	ChangeFileSet getChangeFileSet(ProjectSCM scmInfo, HotFixParameter paramer) 
	
}
