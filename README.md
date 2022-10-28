<img src="https://chengchaosite.oss-cn-hangzhou.aliyuncs.com/resource-container/blog/2021_01_05_14_28_1609828107288.png" style="zoom:33%;" />

# 前言

基本上是各大云产商的SSO都默认支持[SAML协议](https://en.wikipedia.org/wiki/Security_Assertion_Markup_Language). 但是由于配置比较复杂,很多人第一次接触的时候都是觉得比较难以理解.前段时间由于工作需要写了个支持saml协议的简易IDP.下面就来介绍一下怎么使用简易的IDP SSO到阿里云



# 代码

我已经把代码放到Github上: https://github.com/ichengchao/hellosaml-idp ,这个代码本身非常简单,使用了springboot.

- 监听端口: 8080
- 启动类: `name.chengchao.hellosaml.idp.IdPDemoApplication`
- 配置类: `name.chengchao.hellosaml.common.CommonConstants`
- 首页地址:  http://localhost:8080/index.html
- meta.xml地址: http://localhost:8080/metaxml

# 实操

## 生成证书

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

修改name.chengchao.hellosaml.idp.common.CommonConstants

- PUBLIC_KEY_PATH配置成saml.crt的文件的绝对路径,比如: `/Users/charles/config/saml.crt`
- PRIVATE_KEY_PATH配置成saml.pkcs8的文件的绝对路径,比如: `/Users/charles/config/saml.pkcs8`

### [阿里云控制台配置](./README_aliyun.md)



### 附加说明

配置类: name.chengchao.hellosaml.common.CommonConstants

| 参数             | 是否需要调整 | 描述                                                    |
| :--------------- | :----------: | :------------------------------------------------------ |
| PUBLIC_KEY_PATH  |      是      | 公钥文件绝对路径,比如: /Users/charles/config/saml.crt   |
| PRIVATE_KEY_PATH |      是      | 私钥文件绝对路径,比如: /Users/charles/config/saml.pkcs8 |
| IDP_ENTITY_ID    |      是      | IDP的唯一标识,比如: chengchao.name/myIdP                |
