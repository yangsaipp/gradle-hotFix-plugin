# gradle-hotFix-plugin
Gradle增量自动生成插件，顾名思义是基于Gradle而开发的插件，目的在于准确、高效生成的增量。

发布增量是一个高频率事件，增量包是把修改的文件按照其所在目录依次创建对应目录然后存放，一般增量包是手动整理或者使用一些简单脚本（每次生成都要修改配置），效率不高且容易出错，Gradle增量自动生成正是为解决这个问题而开发 ，使用它只需一次配置，每次生成增量运行命令就可以生成增量。

gradle-hotFix-plugin可以读取svn提交记录获取对应变更文件生成增量，配置简单，使用方便、高效，主要适用于gradle管理的java项目。

## 入门

使用前提：使用gradle管理和构建项目。

#### 第一步：引入hotFix插件
build.xml里引入hotFix插件

	apply plugin: 'java'
	//引入hotFix插件
    apply plugin: 'hotFix'	

配置插件使用的依赖jar，注意是配置在`buildscript`块中

    ...
	buildscript {
		repositories {
			mavenLocal()
			maven { url 'http://10.10.50.24:8081/nexus/content/groups/public' }
		}
		
		dependencies {
			classpath 'com.github:gradle-hotFix-plugin:1.0-SNAPSHOT'
		}
	}
	...

#### 第二步：配置hotFix插件信息
配置svn访问账号，账号要求有读取log权限

	...    
	hotFix {
		// 配置增量存放目录和名称，相对路径（项目根目录）
		targetDir = "build/hotFix/${project.name}_hotfix_"+ new Date().format('yyyy-MM-dd_HHmm')
		svn {
			username = 'test'
			password = 'test'
		}
	}
	...

#### 第三步：运行命令生成增量
打开CMD控制台，定位到项目所在目录运行以下命令：

    gradlew hotFix -Pstart=svnStartNum -Pend=svnEndNum

`svnStartNum`、`svnEndNum`是svn的版本号，以上命令会将svn版本号为`svnStartNum`到`svnEndNum`这之间的变更文件提取生成增量。若不配置`PeRevision`表示到最新版本。运行完后会在第二步中配置的`targetDir`目录生成对应的增量文件。

## Gradle任务

引入本插件后会增加三个gradle task，用户只需使用hotFix任务即可。

task | description
----|------------
`hotFix` | 增量生成任务
`process` | 分析处理变更文件
`parse` | 解析SCM或者自定义文件得到变更文件集合

#### hotFix 任务参数

name | description
----|------------
`start` | 指定本次增量变更文件的svn开始版本号
`end` | 指定本次增量变更文件的svn结束版本号，若不指定为最后提交版本号
`author` | 指定本次增量变更文件的修改人，若有多个使用,分割如：-Pauthor=A,B。

场景：版本发布后发现问题处理后需要出增量，同事A处理问题，同事B开发新功能，文件修改提交记录在1000(SVN版本号)到1020之间，那么可以使用如下命令生成增量：

    gradlew hotFix -Pstart=1000 -Pend=1020 -Pauthor=A


## 详细参数

#### HotFix 参数

key | default | description
----|---------|------------
`targetDir` | 'build/hotFix/yyyy-MM-dd_HHmm' | 增量生成存放的目录
`local` |  | 使用自定义变更文件的配置方式生成增量，`local`和`svn`只能配置一种
`svn` |  | 使用svn的提交log配置方式生成增量，`local`和`svn`只能配置一种
`java` |  | java代码相关配置
`resource` |  | 项目资源文件相关配置
`webapp` |  | web资源文件相关配置

 
#### local 参数

key | default | description
----|---------|------------
`location` | hotFix.txt | 记录自定义变更文件的配置文件存放路径，UTF-8编码，默认值为项目根目录下`hotFix.txt`文件。

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

`myHotFix.txt`样例：

    src\main\java\org\gradle\sample\Greeter.java
	src\main\java\org\gradle\GradleCopy.java
	src\main\resources\greeting.txt
	src\main\resources\gradleCopy.xml
	src\main\webapp\WEB-INF\web.xml
	src\main\webapp\index.jsp

自定义变更文件配置文件使用说明：
1. 一行文本只能描述一个变更文件记录
2. 可以使用通配符*，例如：src\main\java\org\**\*.java
3. 使用该配置方式时运行命令`gradlew hotFix`无需指定`-Pstart` 和 `-Pend`

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


## 常见问题