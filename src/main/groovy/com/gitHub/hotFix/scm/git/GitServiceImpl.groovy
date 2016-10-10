package com.gitHub.hotFix.scm.git

import com.gitHub.hotFix.model.HotFixParameter;
import com.gitHub.hotFix.model.ProjectSCM;
import com.gitHub.hotFix.scm.SCMService
import com.gitHub.hotFix.scm.model.ChangeFileSet;

class GitServiceImpl implements SCMService {

	@Override
	public ChangeFileSet getChangeFileSet(ProjectSCM scmInfo, HotFixParameter paramer, String targetPath) {
		throw new RuntimeException("读取git日志信息获取变更文件信息暂未实现，请使用本地文件配置的方法来生成增量");
//		ChangeFileSet changeFileSet = new ChangeFileSet()
//		return changeFileSet;
	}

}
