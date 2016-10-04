# gradle-hotFix-plugin
gradle-hotFix-plugin，可以读取svn提交记录获取对应变更文件生成增量，主要针对java项目，配置简单，使用方便。

## 入门

必要：工程里使用java插件且相关任务如jar等能正常使用。

#### 第一步：引入hotFix插件
引入hotFix插件

	apply plugin: 'java'
	//引入hotFix插件
    apply plugin: 'hotFix'	

配置插件使用的依赖jar

    ...
	buildscript {
		repositories {
			mavenLocal()
			//maven { url 'http://repo1.maven.org/maven2' }
			mavenCentral()
		}
		
		dependencies {
			classpath 'com.github:gradle-hotFix-plugin:1.0-SNAPSHOT'
		}
	}
	...

#### 第二步：配置hotFix
配置svn访问账号，账号要求有读取log权限

	...    
	hotFix {
		// 配置增量存放目录和名称
		targetDir = "build/hotFix/${project.name}_hotfix_"+ new Date().format('yyyy-MM-dd_HHmm')
		svn {
			username = 'test'
			password = 'test'
		}
	}
	...

#### 第三步：运行命令生成增量
运行以下命令

    gradlew hotFixGenerate -PsRevision=svnStartNum -PeRevision=svnEndNum

`svnStartNum`、`svnEndNum`是svn的版本号，以上命令会将svn版本号为`svnStartNum`到`svnEndNum`这之间的变更文件提取生成增量。PeRevision参数可选，若不配置则表示到最新版本。

命令运行完后会在第二步中配置的targetDir目录生成对应的增量文件。

## 详细参数

#### HotFix 参数

key | default | description
----|---------|------------
`targetDir` | 'build/hotFix/yyyy-MM-dd_HHmm' | 增量生成存放的目录
`local` |  | 使用读取本地配置文件获取变更文件列表的方式生成增量，`local`和`svn`只能配置一种
`svn` |  | 使用svn的提交log获取变更文件列表的方式生成增量，`local`和`svn`只能配置一种
`java` |  | java代码相关配置
`resource` |  | 项目资源文件相关配置
`webapp` |  | web资源文件相关配置

 
#### local 参数

key | default | description
----|---------|------------
`location` | hotFix.txt | 本地配置文件存放路径，UTF-8编码，默认值为项目根目录下`hotFix.txt`文件。

例:

    ...    
	hotFix {
		...
		local {
			location = 'myHotFix.txt'
		}
		...
	}
	...

`myHotFix.txt`内容：

    src\main\java\org\gradle\sample\Greeter.java
	src\main\java\org\gradle\GradleCopy.java
	src\main\resources\greeting.txt
	src\main\resources\gradleCopy.xml
	src\main\webapp\WEB-INF\web.xml
	src\main\webapp\index.jsp

说明： 使用该配置方式运行命令`gradlew hotFixGenerate`即可，无需指定`-PsRevision` 和 `-PeRevision`参数

#### svn 参数

key | default | description
----|---------|------------
`username` |  | svn账号，svn账号需要有读取log权限
`password` |  | svn密码

例:

    ...    
	hotFix {
		...
		svn {
			username = 'test'
			password = 'test'
		}
		...
	}
	...

#### java 参数
key | default | description
----|---------|------------
`source` | src/main/java | java文件存放文件路径
`exclude` |  | 生成增量时要排除的文件
`include` |  | 生成增量时要包含的文件

例：

    ...    
	hotFix {
		...
		java {
			source = 'src/main/java'
			exclude 'com/test/test.java'
			exclude '**/test2.java'
			include 'com/test/test3.java'
			include '**/test3.java'
		}
		...
	}
	...

#### resource 参数
key | default | description
----|---------|------------
`source` | src/main/resources | 配置文件存放文件路径
`exclude` |  | 生成增量时要排除的文件
`include` |  | 生成增量时要包含的文件

例：

    ...    
	hotFix {
		...
		resource {
			source = 'src/main/resources'
			exclude '**/gradleCopy.xml'
			include '**/greeting.txt'
		}
		...
	}
	...

#### webapp 参数
key | default | description
----|---------|------------
`source` | src/main/webapp | 配置文件存放文件路径
`exclude` |  | 生成增量时要排除的文件
`include` |  | 生成增量时要包含的文件

例：

    ...    
	hotFix {
		...
		webapp {
			source = 'src/main/webapp'
			exclude '**/index.jsp
			include '**/web.xml'
		}
		...
	}
	...
