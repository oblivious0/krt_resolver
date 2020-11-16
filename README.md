
[TOC]

##简介

>模块化krt_resolver库是基于Android X系统（目前版本最低兼容Android 5.1系统）设备的应用程序接口。 您可以使用该套 SDK开发适用于Android系统移动设备的模块化应用，通过调用模块化SDK接口，您可以轻松访问模块化服务，构建可自由编辑的线上应用程序。


##使用指南

###引用步骤

####Step1：将JitPack存储库添加到项目的构建文件中
``` java
allprojects {
repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
``` 

####Step2：添加依赖项
``` java
dependencies {
	 implementation 'com.github.oblivious0:krt_resolver:Tag'
}
```


###注意事项

>因部分极少数老旧机型系统限制单个App占用的内存，程序打开时会因内存不够闪退需要在AndroidManifest.xml 下的 application标签里加上**android:largeHeap="true"** 

>此库已引入其他以下第三方库，请勿重复引入
 -  api 'com.marcus:baseModule:1.5.0-beta'
 -  api 'com.joooonho:selectableroundedimageview:1.0.1'
 -  api 'com.youth.banner:banner:2.0.12'
 -  api 'com.google.code.gson:gson:2.8.6' 
 -  api 'com.tencent.bugly:crashreport:latest.release'
 -  api 'com.tencent.bugly:nativecrashreport:latest.release'

>项目启动时会首先申请SD卡读写权限



###初始化

>在Application完成初始化操作：
``` java
MProConfig.build()
	.setKrtCode(BuildConfig.M_TAG)
	.setFragmentClz(ModuleFragment.class)
	.setIsPublish(BuildConfig.isBeta ? "1" : "2")
	.generate();
```
> **setKrtCode(String)** 代表项目编号，可以通过模块化后台查询；
> **setFragmentClz(Class <? extends BaseModuleFragment>)** 代表项目内继承BaseModuleFragment的Fragment类
> **setIsPulish(int)** 表示项目启用版本：0测试版 1体验版 2发布版 3历史发布版
> **generate()** 最后执行方法生成配置，此方法会在上一次配置的基础上进行修改，如需重置，以**reset()**替代

