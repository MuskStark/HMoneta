
<!-- PROJECT LOGO -->
<table align="center"><tr><td align="center" width="9999">
<p align="center">
  <a href="https://git.summer.fan:9443/jack/hmoneta/">
    <img src="asset/projectLogo.png" alt="Logo" width="300" height="80">
  </a>
</p>

<h3>
<div style="text-align: center;">HMoneta</div>
</h3>
  <p align="center">
    专注于为家庭提供统一的家庭服务管理平台
    <br />
    <a href="https://github.com/MuskStark/HMoneta/blob/main/doc/wiki.md"><strong>探索本项目的文档 »</strong></a>
    <br />
    <br />
    ·
    <a href="https://github.com/MuskStark/HMoneta/issues">报告Bug</a>
    ·
    <a href="https://github.com/MuskStark/HMoneta/issues">提出新特性</a>
  </p>
</td></tr></table>
<br />

## 目录

- [快速开始](#快速开始)
    - [运行环境](#运行环境)
    - [项目安装](#项目安装)
- [文件目录说明](#文件目录说明)
- [开发的架构](#开发的架构)
- [部署](#部署)
- [使用到的框架](#使用到的框架)
- [软件功能](#软件功能)
- [贡献者](#贡献者)
    - [如何参与开源项目](#如何参与开源项目)
- [版本控制](#版本控制)
- [作者](#作者)
- [鸣谢](#鸣谢)

### 快速开始
###### 运行环境
- Java-21
- Nodejs-20.12.2
###### **项目安装**

1. 拉取项目至本地
```sh
git clone https://github.com/MuskStark/HMoneta.git
```
2. 项目依赖安装
  - 后端依赖安装
```sh
mvn dependency:copy-dependency
```
  - 前端依赖安装
```sh
cd /project/web
yarn
```


### 文件目录说明

```
filetree 
.
├── LICENSE
├── README.md
├── asset
│   ├── projectLogo.png
│   └── webLogo.png
├── deploy
│   ├── envSetup.sh
│   ├── setUp-Ubuntu.sh
│   └── startup.sh
├── doc
│   └── wiki.md
├── pom.xml
├── src
│   ├── main
│   └── test
├── test
│   └── api
└── web
    ├── README.md
    ├── index.html
    ├── jsconfig.json
    ├── node_modules
    ├── package.json
    ├── public
    ├── src
    ├── vite.config.js
    └── yarn.lock
```
### 开发的架构
### 部署
### 使用到的框架
- [SpringBoot3](https://spring.io/projects/spring-boot)
- [Vue3](https://cn.vuejs.org/)
- [Mysql8.0](https://www.mysql.com/)
- [Ant-Design-Vue](https://antdv.com/)
### 软件功能
- [x] ~~家庭Ip地址池管理~~
- [x] ~~家庭服务器管理~~
- [ ] 家庭服务器状态监控
- [ ] 家庭DDNS、内网穿透
- [ ] 家庭服务器反向代理
### 贡献者
- [MuskStark](https://github.com/MuskStark)
#### 如何参与开源项目

### 作者
- [MuskStark](https://github.com/MuskStark)

### 版权说明
本项目依据[GNU Affero General Public License v3.0](https://github.com/MuskStark/HMoneta?tab=AGPL-3.0-1-ov-file)进行开源

### 鸣谢




