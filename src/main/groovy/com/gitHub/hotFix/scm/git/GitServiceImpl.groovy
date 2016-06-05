package com.gitHub.hotFix.scm.git

import com.gitHub.hotFix.model.ProjectSCM;
import com.gitHub.hotFix.scm.SCMService
import com.gitHub.hotFix.scm.model.SCMLog;

class GitServiceImpl implements SCMService {

	@Override
	public SCMLog getLog(ProjectSCM scmInfo, String startRevision, String endRevision, String targetPath) {
		SCMLog log = new SCMLog()
		// TODO Auto-generated method stub
		return log;
	}

}
