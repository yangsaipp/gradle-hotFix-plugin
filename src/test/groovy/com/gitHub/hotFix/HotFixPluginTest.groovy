package com.gitHub.hotFix

import nebula.test.PluginProjectSpec

import com.gitHub.hotFix.util.FileUtil


class HotFixPluginTest extends PluginProjectSpec{
	
	String getPluginName() {
		return "hotFix"
	}
	
	def setup() {
		// apply plugin
		project.apply plugin: "java"
		project.apply plugin: "hotFix"
		// 设置工程java目录和resources目录
		project.sourceSets{
			main {
				java {
//					srcDirs = [getClass().getResource("/testJavaCode").toURI()]
				}
				resources {
//					srcDirs = [getClass().getResource("/testResources").toURI()]
				}
			}
		}
		println project.projectDir
//		println project.sourceSets.main.java.srcDirs
//		println project.sourceSets.main.resources.srcDirs
		// copy java file to Project
		FileUtil.copyDir(new File(getClass().getResource("/testJavaCode").toURI()) ,project.sourceSets.main.java.srcDirs[0]){ file, destPath ->
			if(file.name.endsWith(".java.txt")) {
				new File(destPath.replaceAll('.java.txt$', '.java')) << file.getBytes()
			}
		}
		// copy resources file to Project
		FileUtil.copyDir(new File(getClass().getResource("/testResources").toURI()) ,project.sourceSets.main.resources.srcDirs[0])
		
		FileUtil.copyDir(new File(getClass().getResource("/testWebapp").toURI()) ,project.file('src/main/webapp'))
	}
	
	def 'should generate hotFix'() {
		given:
		def file = createHotFixLocation()
		def changeJavaFile = ['org/gradle/sample/GradleCopy.java','org/test/GradleCopy.java']
		def changeResourceFile = ['gradleCopy.xml','spring/spring.xml']
		
		def changeWebappFile = ['index2.jsp','WEB-INF/web.xml']
		def noExistsWebappFile = ['index1.jsp']
		
		def targetDir = "build/hotFix/${project.name}_hotfix_"+ new Date().format('yyyy-MM-dd_HHmm')
		// 定义hotFix配置参数
		project.hotFix {
			targetDir = targetDir
			local {
				location =  file
			}
			java {
				exclude '**/test/GradleCopy.java'
			}
			resource {
				exclude 'spring/spring.xml'
			}
			webapp {
				exclude '**/web.xml'
			}
		}
		
		writeChangeJavaFile(file, changeJavaFile)
		writeChangeResourceFile(file, changeResourceFile)
		writeChangeWebappFile(file, changeWebappFile)
		writeChangeWebappFile(file, noExistsWebappFile)
			
		when:
		eHotFixGenerate()
		
		then:
		project.file(project.hotFix.targetDir).exists()
		
		javaExits(changeJavaFile[0])
		!javaExits(changeJavaFile[1])
		
		resourceExits(changeResourceFile[0])
		!resourceExits(changeResourceFile[1])
		
		webappExits(changeWebappFile[0])
		!webappExits(changeWebappFile[1])
		!webappExits(noExistsWebappFile[0])
	}
	
	def eHotFixGenerate() {
//		def jar = project.tasks.getByPath("jar")
//		jar.dependsOn.each { task ->
//			if (task instanceof WebCompileTask) {
//				task.execute()
//			}
//		}
		project.tasks.getByPath('compileJava').execute()
		project.tasks.getByPath('processResources').execute()
		project.tasks.getByPath(HotFixPlugin.TASK_PARSE).execute()
		project.tasks.getByPath(HotFixPlugin.TASK_PROCESS).execute()
		project.tasks.getByPath(HotFixPlugin.TASK_GENERATE).execute()
	}
	
	def File createHotFixLocation() {
		File file = new File(project.projectDir,'hotFix.txt')
		if(!file.exists()) {
			file.createNewFile();
		}
		file
	}
	
	def writeChangeJavaFile(file, List<String> fileList) {
		assert file != null
		def text = ''
		fileList.each { 
			text += "src/main/java/$it"
			text += "\n"
		}
		file << text
	}
	
	def writeChangeResourceFile(file, List<String> fileList) {
		assert file != null
		def text = ''
		fileList.each {
			text += "src/main/resources/$it"
			text += "\n"
		}
		file << text
	}
	
	def writeChangeWebappFile(file, List<String> fileList) {
		assert file != null
		def text = ''
		fileList.each {
			text += "src/main/webapp/$it"
			text += "\n"
		}
		file << text
	}
	
	def javaExits(String javaFile) {
		String classFile = javaFile.replaceAll('java$', 'class')
		return new File(project.file(project.hotFix.targetDir), "WEB-INF/classes/$classFile").exists()
	}
	
	def resourceExits(String file) {
		return new File(project.file(project.hotFix.targetDir), "WEB-INF/classes/$file").exists()
	}
	
	def webappExits(String file) {
		return new File(project.file(project.hotFix.targetDir), file).exists()
	}
	
}
