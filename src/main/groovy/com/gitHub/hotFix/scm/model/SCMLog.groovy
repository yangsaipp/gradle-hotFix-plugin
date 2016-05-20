package com.gitHub.hotFix.scm.model

class SCMLog {
	/** SCM里hotFix文件路径集合 */ 
	Set<String> pathSet = []
	
	/** SCM里hotFix中删除文件路径集合 */
	Set<String> deletePathSet = []
	
	/**
	 * 增加文件路径
	 * @param path
	 */
	void addPath(String path) {
		pathSet << path
	}
	
	void addDeletePath(String path) {
		pathSet.remove(path)
		deletePathSet << path
	}
	
	/**
	 * 增加重命名文件路径
	 * @param newPath
	 * @param oldPath
	 */
	void addRenamePath(String newPath, String oldPath) {
		addPath(newPath)
		addDeletePath(oldPath);
	}
}
