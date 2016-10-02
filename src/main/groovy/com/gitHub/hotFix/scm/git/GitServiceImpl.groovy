package com.gitHub.hotFix.scm.git

import com.gitHub.hotFix.model.ProjectSCM;
import com.gitHub.hotFix.scm.SCMService
import com.gitHub.hotFix.scm.model.ChangeFileSet;

class GitServiceImpl implements SCMService {

	@Override
	public ChangeFileSet getChangeFileSet(ProjectSCM scmInfo, String startRevision, String endRevision, String targetPath) {
		ChangeFileSet changeFileSet = new ChangeFileSet()
		// TODO Auto-generated method stub
		return changeFileSet;
	}

}
