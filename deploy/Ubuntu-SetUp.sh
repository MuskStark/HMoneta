#!/bin/bash
# 更新系统
echo "开始更新系统"
sudo apt-get update
sudo apt-get install -y wget
# 清除老版本java环境
sudo apt-get remove -y openjdk-\*
# 安装java
echo "安装Java环境"
sudo apt-get install -y openjdk-21-jdk
# 下载并解压Maven
sudo apt-get install -y maven
# 开放防火墙端口
echo "开始开放80端口"
sudo ufw allow 8080/tcp
# 安装mysql
echo "安装MySQL环境"
sudo apt-get install -y mysql-server
sudo cat /etc/mysql/debian.cnf
# HMoneta数据库配置
HMONETA_USERNAME="hmoneta"
PASSWORD_LENGTH=16
CHAR_SET="a-zA-Z0-9!@#$%^&*()_+=-"
HMONETA_PASSWORD=$(tr -dc "$CHAR_SET" < /dev/urandom | head -c "$PASSWORD_LENGTH")
HMONETA_DATABASE="hmoneta"

# 连接到MySQL服务器
echo "临时用户名如上所示"
read -s -p "请用户名密码: " MYSQL_USER
read -s -p "请输入密码: " MYSQL_ROOT_PASSWORD
mysql -u"${MYSQL_USER}" -p"${MYSQL_ROOT_PASSWORD}" <<MYSQL_SCRIPT
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
SPRING_PATH="../src/main/resources/application-prod.yaml"
URL="jdbc:mysql://localhost:3306/$HMONETA_DATABASE?useUnicode=true;characterEncoding=UTF-8"
escaped_url=$(printf '%s\n' "$URL" | sed 's/[&/\?]/\\&/g')
sed -i "s/url:.*/url: $escaped_url/" $SPRING_PATH
sed -i "s/username:.*/username: $HMONETA_USERNAME/" $SPRING_PATH
sed -i "s/password:.*/password: $HMONETA_PASSWORD/" $SPRING_PATH

# 构建应用程序
cd ~/Hmoneta
mvn clean install -PAll-in-one -DskipTests
# 结果输出
echo ">>>>>>>>>>>环境信息>>>>>>>>>>>>"
echo "Mysql用户名: $HMONETA_USERNAME"
echo "Mysql密码: $HMONETA_PASSWORD"
echo "Mysql数据库名: $HMONETA_DATABASE"
echo "防火墙已开放的端口:"
sudo ufw status
echo "JDK版本信息:"
java --version
echo ">>>>>>>>>>>环境信息>>>>>>>>>>>>"