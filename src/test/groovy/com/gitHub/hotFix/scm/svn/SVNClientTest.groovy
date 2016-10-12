package com.gitHub.hotFix.scm.svn

import org.tmatesoft.svn.core.SVNDepth
import org.tmatesoft.svn.core.SVNException
import org.tmatesoft.svn.core.SVNURL
import org.tmatesoft.svn.core.wc.ISVNStatusHandler
import org.tmatesoft.svn.core.wc.SVNClientManager
import org.tmatesoft.svn.core.wc.SVNInfo
import org.tmatesoft.svn.core.wc.SVNRevision
import org.tmatesoft.svn.core.wc.SVNStatus
import org.tmatesoft.svn.core.wc.SVNStatusType
import org.tmatesoft.svn.core.wc.SVNWCClient

class SVNClientTest {
	
	//获取工作目录未提交文件列表
	List<File> listModifiedFiles(File path,SVNRevision revision) throws SVNException{
		SVNClientManager svnClientManager = SVNClientManager.newInstance();
		final List<File> fileList = new ArrayList<File>();
		svnClientManager.getStatusClient().doStatus(path, revision,SVNDepth.INFINITY,false,false,false,false, {
			SVNStatus status ->
			SVNStatusType statusType = status.getContentsStatus();
			if(statusType !=SVNStatusType.STATUS_NONE && statusType !=SVNStatusType.STATUS_NORMAL && statusType !=SVNStatusType.STATUS_IGNORED){
				fileList.add(status.getFile());
			}
		} as ISVNStatusHandler ,null);
		return fileList;
	}
	
	//获取svn客户端信息 "d:/Program Files/eclipse4.4/workspace/svnTest/ysTest1"
	static SVNInfo getSVNInfo(String workingPath) {
		SVNWCClient client = SVNClientManager.newInstance().getWCClient();
		SVNInfo info = null;
		try {
			info = client.doInfo(new File(workingPath), SVNRevision.WORKING);
		} catch (SVNException e) {
			e.printStackTrace();
		}
		SVNURL url = info.getURL();
		System.out.println(url);
		return info
	}
	
	public static void main(String[] arg) {
		getSVNInfo('d:\\Code\\workspace\\gradleTest\\');
	}
	
	
}
