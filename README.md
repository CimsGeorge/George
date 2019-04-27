# kgt

A knowledge graph tool.

以Intellij IDEA编辑器为例，需为编辑器安装插件:
```
IDEA->File->Settings->Plugins->marketplace->搜索lombok->下载lombok插件并重启idea
```
导入项目：
```
idea主界面->Import Project->找到项目所在位置->Import project from external model->maven->一路next
```

调用该包的方法请参考test文件夹下的KnowledgeGraphClientTest类。
使用前要启动图数据库。

打包前，若为英文本体使用，修改RelationshipEnum.class中的内容为：
```
SUB_CLASS("subClass"),
INDIVIDUAL("individual");
```
若为中文本体使用，则改为相应的中文。

双击Intellij IDEA右侧Maven->kgt->Lifecycle->package进行打包。

其他项目使用该包，可安装在本地仓库，以maven构建工具为例，安装命令为：
```
mvn install:install-file -Dfile=文件位置\kgt-0.0.1.jar -DgroupId=edu.tongji.cims -DartifactId=kgt -Dversion=0.0.1 -Dpackaging=jar
```
安装后，如在maven项目中pom.xml文件添加：
```
<dependency>
    <groupId>edu.tongji.cims</groupId>
    <artifactId>kgt</artifactId>
    <version>0.0.1</version>
</dependency>
```

实验室项目使用kgt-0.0.1.jar，在引用的项目中添加第三方依赖：
```
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
    <version>x.x.x</version>
</dependency>

<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>x.x.x</version>
</dependency>
```
x.x.x为版本号，去maven参考搜索并引用较新的版本
```
https://mvnrepository.com/
```

若使用其他构建工具请自行搜索解决。

kgt-0.0.1-jar-with-dependencies.jar给项目合作方，已含有第三方依赖，交付之前重命名为:
```
kgt-0.0.1.jar
```

有任何改动请一定重新打包，并安装引用或交付。

