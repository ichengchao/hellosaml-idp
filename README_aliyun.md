# 新增IDP

启动hellosaml-idp应用,保存http://localhost:8080/metaxml 的内容到meta.xml  

登录阿里云[RAM控制台](https://ram.console.aliyun.com/providers) ,在角色SSO的tab页新增一个身份提供商

<img src="https://chengchaosite.oss-cn-hangzhou.aliyuncs.com/resource-container/blog/2021_01_05_14_29_1609828150113.png" style="zoom: 33%;" />

取一个名字比如`superAD`,将保存的meta.xml上传上去. 接着在切换到[RAM角色管理的页面](https://ram.console.aliyun.com/roles),创建一个RAM角色.假设叫`super2`类型选择**身份供应商**

<img src="https://chengchaosite.oss-cn-hangzhou.aliyuncs.com/resource-container/blog/2020_09_30_14_50_1601448633028.png" style="zoom:33%;" />

# 角色授权

接着给这个RAM角色授权,简单点的话可以直接选取一个系统内置的权限,比如AdministratorAccess.

<img src="https://chengchaosite.oss-cn-hangzhou.aliyuncs.com/resource-container/blog/2021_01_05_14_30_1609828231988.png" style="zoom:33%;" />

到这里,阿里云侧的配置就全部完成了,还是非常简单的.