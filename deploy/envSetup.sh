#!/bin/bash
# 更新系统
echo "开始更新系统"
yum -y update
yum -y install wget
yum -y install nginx
# 安装java
echo "安装Java环境"
sudo yum install java-21-openjdk java-21-openjdk-devel -y
# 开放防火墙端口
echo "开始开放80端口"
firewall-cmd --zone=public --add-port=80/tcp --permanent
firewall-cmd --zone=public --add-port=8080/tcp --permanent
# 安装mysql
echo "安装MySQL环境"
wget https://repo.mysql.com//mysql80-community-release-el9-5.noarch.rpm
dnf install mysql80-community-release-el9-5.noarch.rpm
dnf install -y mysql-community-server
systemctl start mysqld
systemctl enable mysqld
firewall-cmd --zone=public --add-port=3306/tcp --permanent
echo "MysqlRoot密码"
grep "password" /var/log/mysqld.log
echo "防火墙已开放的端口"
firewall-cmd --reload
firewall-cmd --list-ports
echo "JDK版本信息"
java --version

mysql_secure_installation