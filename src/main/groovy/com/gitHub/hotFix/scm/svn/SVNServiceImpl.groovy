package com.gitHub.hotFix.scm.svn

import java.util.Collection;
import java.util.LinkedList;

import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException
import org.tmatesoft.svn.core.SVNLogEntry
import org.tmatesoft.svn.core.SVNLogEntryPath
import org.tmatesoft.svn.core.SVNURL
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl
import org.tmatesoft.svn.core.io.SVNRepository
import org.tmatesoft.svn.core.io.SVNRepositoryFactory
import org.tmatesoft.svn.core.wc.SVNClientManager
import org.tmatesoft.svn.core.wc.SVNInfo
import org.tmatesoft.svn.core.wc.SVNRevision
import org.tmatesoft.svn.core.wc.SVNWCClient
import org.tmatesoft.svn.core.wc.SVNWCUtil

import com.gitHub.hotFix.model.HotFixParameter;
import com.gitHub.hotFix.model.ProjectSCM
import com.gitHub.hotFix.scm.SCMService
import com.gitHub.hotFix.scm.model.ChangeFileSet

/**
 * 提供svn服务，如查询log
 * <p>
 * 参考:<a href='http://wiki.svnkit.com/Printing_Out_Repository_History'>SVN Printing Out Repository History</a>
 * <p>
 * @author yangsai
 *
 */
class SVNServiceImpl implements SCMService {
	static Logger buildLogger = Logging.getLogger(SCMService.class);
	
	@Override
	public ChangeFileSet getChangeFileSet(ProjectSCM scmInfo, HotFixParameter paramer) {
		String url = scmInfo.url
		String name = scmInfo.username
		String password = scmInfo.password
		String workingPath = scmInfo.workingPath
		String targetPath = scmInfo.targetPath
		
		long startR = paramer.startRevision as long
		long endR = paramer.endRevision ? paramer.endRevision as long : -1
		
		// 指定用户名
		String specifiedAuthor = paramer.get(HotFixParameter.AUTHOR) ? "${paramer.get(HotFixParameter.AUTHOR)}," : null
		
		SVNRepository repository = null;
		SVNURL svnUrl = null
		try {
			if(url == null) {
				SVNWCClient client = SVNClientManager.newInstance().getWCClient();
				SVNInfo svnInfo = null;
				try {
					svnInfo = client.doInfo(new File(workingPath), SVNRevision.WORKING);
				} catch (SVNException e) {
					buildLogger.error("error while fetching svn client info: " + e.getMessage());
					throw e
				}
				svnUrl = svnInfo.getURL();
			}else {
				svnUrl = SVNURL.parseURIEncoded(url)
			}
			repository = SVNRepositoryFactory.create(svnUrl);
		} catch (SVNException svne) {
			buildLogger.error("error while creating an SVNRepository for the location '" + url + "': " + svne.getMessage());
			throw svne
		}

		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(name, password.toCharArray());
		repository.setAuthenticationManager(authManager);

		/*
		 * Gets the latest revision number of the repository
		 */
//		try {
//			endRevision = repository.getLatestRevision();
//		} catch (SVNException svne) {
//			buildLogger.error("error while fetching the latest repository revision: " + svne.getMessage());
//		}

		final Collection logEntries = new LinkedList();
		try {
			String[] targetPaths = targetPath ? [targetPath] : ['']
			buildLogger.quiet("svn targetpath : ${targetPaths}")
			repository.log(targetPaths, startR, endR, true, true, 0, true, null, new ISVNLogEntryHandler() {
	            public void handleLogEntry(SVNLogEntry logEntry) {
	                logEntries.add(logEntry);
	            }        
	        });

		} catch (SVNException svne) {
			buildLogger.error("\nerror while collecting log information for '" + svnUrl + "': " + svne.getMessage());
			throw svne
		}
		
		ChangeFileSet changeFileSet = new ChangeFileSet()
		for (Iterator entries = logEntries.iterator(); entries.hasNext();) {
			/*
			 * gets a next SVNLogEntry
			 */
			SVNLogEntry logEntry = (SVNLogEntry) entries.next();
//			System.out.println("---------------------------------------------");
			/*
			 * gets the revision number
			 */
//			System.out.println("revision: " + logEntry.getRevision());
			/*
			 * gets the author of the changes made in that revision
			 */
//			System.out.println("author: " + logEntry.getAuthor());
			/*
			 * gets the time moment when the changes were committed
			 */
//			System.out.println("date: " + logEntry.getDate());
			/*
			 * gets the commit log message
			 */
//			System.out.println("log message: " + logEntry.getMessage());
			/*
			 * displaying all paths that were changed in that revision; cahnged
			 * path information is represented by SVNLogEntryPath.
			 */
			// 过滤提交者，只搜集指定提交者的变更记录
			if (logEntry.getChangedPaths().size() > 0 && filterAuthor(logEntry.getAuthor(), specifiedAuthor)) {
//				System.out.println();
//				System.out.println("changed paths:");
				/*
				 * keys are changed paths
				 */
				Set changedPathsSet = logEntry.getChangedPaths().keySet();

				for (Iterator changedPaths = changedPathsSet.iterator(); changedPaths.hasNext();) {
					SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPaths.next());
					switch(entryPath.getType()) {
						case SVNLogEntryPath.TYPE_ADDED:
							changeFileSet.addPath(entryPath.getPath())
							break
						case SVNLogEntryPath.TYPE_DELETED:
							changeFileSet.addDeletePath(entryPath.getPath())
							break
						case SVNLogEntryPath.TYPE_MODIFIED:
							changeFileSet.addPath(entryPath.getPath())
							break
						case SVNLogEntryPath.TYPE_REPLACED:
							//FIXME replace类型时获oldpath
							changeFileSet.addRenamePath(entryPath.getPath(), entryPath.getCopyPath())
							break
						default:
							break
					}
				}
			}
		}
		return changeFileSet;
	}
	
	/**
	 * 是否为指定提交者， specifiedAuthor为null则不过滤
	 * @param author
	 * @param specifiedAuthor
	 * @return true 是  false 不是
	 */
	private boolean filterAuthor(String author, String specifiedAuthor) {
		buildLogger.debug('filter author : {} in specifiedAuthor : {}',author, specifiedAuthor);
		if(specifiedAuthor) {
			if(author) {
				return specifiedAuthor.indexOf(author + ',') != -1
			}
			return false 
		}
		return true
	}
	
	/*
	 * Initializes the library to work with a repository via
	 * different protocols.
	 */
	private static void setupLibrary() {
		/*
		 * For using over http:// and https://
		 */
		DAVRepositoryFactory.setup();
		/*
		 * For using over svn:// and svn+xxx://
		 */
		SVNRepositoryFactoryImpl.setup();
		
		/*
		 * For using over file:///
		 */
		FSRepositoryFactory.setup();
	}
}
