package com.gitHub.hotFix.util


class FileUtil {
	
	/**
	 * 将目录下的文件拷贝到另外的目录下，被拷贝的文件目录结构不变。
	 * @param fromDir 要拷贝文件存放目录（从这个目录拷贝）
	 * @param destDir 文件拷贝后存放目录（拷贝到这个目录）
	 */
	static void copyDir(File fromDir, File destDir) {
		copyDir(fromDir, destDir, null)
	}
	
	/**
	 * 将目录下的文件拷贝到另外的目录下，被拷贝的文件目录结构不变。
	 * @param fromDir 要拷贝文件存放目录（从这个目录拷贝）
	 * @param destDir 文件拷贝后存放目录（拷贝到这个目录）
	 */
	static void copyDir(File fromDir, File destDir, Closure closure) {
		assert fromDir.isDirectory()
		// 创建文件拷贝后存放目录
		if(!destDir.exists()) {
			destDir.mkdirs();
		}
		fromDir.eachFileRecurse { file ->
			if(file.isDirectory()) {
				new File(destDir, file.path - fromDir.path).mkdir();
			}else {
				File destFile = new File(destDir, file.path - fromDir.path)
				if(closure) {
//					closure.delegate = file
					closure(file, destFile.path)
				} else {
					destFile << file.getBytes()
				}
			}
		}
	}
}
