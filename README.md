<img src="https://chengchaosite.oss-cn-hangzhou.aliyuncs.com/resource-container/blog/2020_09_30_11_39_1601437186773.png" alt="img" style="zoom:40%;" />

### 前言

基本上是各大云产商的SSO都默认支持[SAML协议](https://en.wikipedia.org/wiki/Security_Assertion_Markup_Language). 但是由于配置比较复杂,很多人第一次接触的时候都是觉得比较难以理解.前段时间由于工作需要写了个支持saml协议的简易IDP.下面就来介绍一下怎么使用简易的IDP SSO到阿里云



### 代码

我已经把代码放到Github上: https://github.com/ichengchao/hellosaml ,这个代码本身非常简单,使用了springboot.

- 监听端口: 8080
- 启动类: `name.chengchao.hellosaml.MyApplication.main`
- 配置类: `name.chengchao.hellosaml.common.CommonConstants`
- URL配置: `name.chengchao.hellosaml.controller.SampleController`

提供两个功能,分别是:

- SSO 模拟登陆: http://localhost:8080/login
- 下载meta.xml文件: http://localhost:8080/metaxml

### 实操

#### 生成证书

```shell
#create the keypair
openssl req -new -x509 -days 3652 -nodes -out saml.crt -keyout saml.pem

#convert the private key to pkcs8 format
openssl pkcs8 -topk8 -inform PEM -outform DER -in saml.pem -out saml.pkcs8 -nocrypt
```

完成后会生成3个文件:

- saml.crt
- saml.pem
- saml.pkcs8

修改name.chengchao.hellosaml.common.CommonConstants

- PUBLIC_KEY_PATH配置成saml.crt的文件的绝对路径,比如: `/Users/charles/test/saml.crt`
- PRIVATE_KEY_PATH配置成saml.pkcs8的文件的绝对路径,比如: `/Users/charles/test/saml.pkcs8`

#### 配置阿里云

启动hellosaml应用,保存http://localhost:8080/metaxml的内容到meta.xml  

登录阿里云[RAM控制台](https://ram.console.aliyun.com/providers) ,在角色SSO的tab页新增一个身份提供商

<img src="https://chengchaosite.oss-cn-hangzhou.aliyuncs.com/resource-container/blog/2020_09_30_14_45_1601448324422.png" alt="Screen Shot 2020-09-30 at 14.44.47" style="zoom:50%;border-style: dashed;border-width: thin;" />

取一个名字比如`superAD`,将保存的meta.xml上传上去. 接着在切换到[RAM角色管理的页面](https://ram.console.aliyun.com/roles),创建一个RAM角色.假设叫`super2`类型选择**身份供应商**

<img src="https://chengchaosite.oss-cn-hangzhou.aliyuncs.com/resource-container/blog/2020_09_30_14_50_1601448633028.png" alt="image-20200930145032828" style="zoom:50%;border-style: dashed;border-width: thin;" />

接着给这个RAM角色授权,简单点的话可以直接选取一个系统内置的权限,比如AdministratorAccess.

<img src="https://chengchaosite.oss-cn-hangzhou.aliyuncs.com/resource-container/blog/2020_09_30_15_00_1601449225595.png" alt="Screen Shot 2020-09-30 at 14.59.12" style="zoom:50%;border-style: dashed;border-width: thin;" />

到这里,阿里云侧的配置就全部完成了,还是非常简单的.

#### 调试

修改name.chengchao.hellosaml.common.CommonConstants

将ROLE_LIST按照一下格式配置

```
acs:ram::{uid}:role/{rolename},acs:ram::{uid}:saml-provider/{idp_provider_name}

比如: acs:ram::1764263140479999:role/super2,acs:ram::1764263140479999:saml-provider/superAD
```

完成后,再次启动应用,访问http://localhost:8080/login 就能SSO到阿里云了.

#### 代码附加说明

配置类: name.chengchao.hellosaml.common.CommonConstants 

| 参数                            | 是否需要调整 | 描述                                                         |
| :------------------------------ | :----------: | :----------------------------------------------------------- |
| SSO_FORM_AUTO_SUBMIT            |      是      | 是否自动提交表单,默认为false.测试时需要手动提交saml表单      |
| PUBLIC_KEY_PATH                 |      是      | 公钥文件路径,就是上面生成的saml.crt                          |
| PRIVATE_KEY_PATH                |      是      | 私钥文件路径,就是上面生成的saml.pkcs8                        |
| ROLE_SESSION_NAME               |      是      | 传递给SP的用户名,一般是公司的邮箱地址                        |
| ROLE_LIST                       |      是      | 传递给SP的角色列表,支持多个角色                              |
| ALIYUN_IDENTIFIER               |      否      | 标识符: urn:alibaba:cloudcomputing                           |
| ALIYUN_REPLY_URL                |      否      | URL: https://signin.aliyun.com/saml-role/sso                 |
| ATTRIBUTE_KEY_ROLE_SESSION_NAME |      否      | role_name_key: https://www.aliyun.com/SAML-Role/Attributes/RoleSessionName |
| ATTRIBUTE_KEY_ROLE              |      否      | role_key: https://www.aliyun.com/SAML-Role/Attributes/Role   |

启动类: name.chengchao.hellosaml.MyApplication.main

SSO测试: http://localhost:8080/login

meta xml下载:  http://localhost:8080/metaxml