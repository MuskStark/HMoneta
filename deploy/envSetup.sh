#!/bin/bash
# 更新系统
echo "开始更新系统"
sudo dnf -y update
sudo dnf -y install wget
sudo dnf -y install maven
# 安装java
echo "安装Java环境"
sudo dnf install java-21-openjdk java-21-openjdk-devel -y
# 开放防火墙端口
echo "开始开放80端口"
firewall-cmd --zone=public --add-port=8080/tcp --permanent
# 安装mysql
echo "安装MySQL环境"
wget https://repo.mysql.com//mysql80-community-release-el9-5.noarch.rpm
dnf install -y mysql80-community-release-el9-5.noarch.rpm
dnf install -y mysql-community-server
systemctl start mysqld
systemctl enable mysqld
echo "MysqlRoot密码"
grep "password" /var/log/mysqld.log
mysql_secure_installation
# mysql 初始化
# 初始化参数
MYSQL_PASSWORD=$(grep "password" /var/log/mysqld.log)
# 数据库用户创建
HMONETA_USERNAME="hmoneta"
PASSWORD_LENGTH=16
CHAR_SET="a-zA-Z0-9!@#$%^&*()_+=-"
HMONETA_PASSWORD=$(tr -dc "$CHAR_SET" < /dev/urandom | head -c "$PASSWORD_LENGTH")
# 数据名称
HMONETA_DATABASE="hmoneta"
# 连接到MySQL服务器
# shellcheck disable=SC2162
read -s -p "请输入密码: " MYSQL_PASSWORD
mysql -u root -p"${MYSQL_PASSWORD}" <<MYSQL_SCRIPT
# 创建数据库
CREATE DATABASE $HMONETA_DATABASE;
# 创建新用户
CREATE USER '$HMONETA_USERNAME'@'localhost' IDENTIFIED BY '$HMONETA_PASSWORD';

# 授予新增、查询、删除、创建、删除和更新权限
GRANT INSERT, SELECT, DELETE, CREATE, DROP, UPDATE ON $HMONETA_DATABASE.* TO '$HMONETA_USERNAME'@'localhost';

# 刷新权限
FLUSH PRIVILEGES;
MYSQL_SCRIPT
# spring配置信息
SPRING_PATH="../src/main/resources/application.yaml"
URL="jdbc:mysql://localhost:3306/$HMONETA_DATABASE?useSSL=false&useUnicode=true&characterEncoding=utf-8"
sed -i "s/url:.*/url: $URL/" $SPRING_PATH
sed -i "s/username:.*/username: $HMONETA_USERNAME/" $SPRING_PATH
sed -i "s/password:.*/password: $HMONETA_PASSWORD/" $SPRING_PATH
echo">>>>>>>>>>>环境信息>>>>>>>>>>>>"
echo "MysqlRoot密码"
grep "password" /var/log/mysqld.log
echo "Mysql用户名"
echo $HMONETA_USERNAME
echo "Mysql密码"
echo "$HMONETA_PASSWORD"
echo "Mysql数据库名"
echo $HMONETA_DATABASE
echo "防火墙已开放的端口"
firewall-cmd --reload
firewall-cmd --list-ports
echo "JDK版本信息"
java --version
echo">>>>>>>>>>>环境信息>>>>>>>>>>>>"