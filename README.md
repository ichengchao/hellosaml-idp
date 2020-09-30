# 使用说明
### 生成证书

```sh
#create the keypair
openssl req -new -x509 -days 3652 -nodes -out saml.crt -keyout saml.pem

#convert the private key to pkcs8 format
openssl pkcs8 -topk8 -inform PEM -outform DER -in saml.pem -out saml.pkcs8 -nocrypt
```



### 参数配置

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

### 调试

启动类: name.chengchao.hellosaml.MyApplication.main

SSO测试: http://localhost:8080/login

meta xml下载:  http://localhost:8080/metaxml