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
sudo aot-get install -y maven
# 开放防火墙端口
echo "开始开放80端口"
sudo ufw allow 8080/tcp
# 安装mysql
echo "安装MySQL环境"
sudo apt-get install -y mysql-server
# 启动MySQL服务
sudo systemctl start mysql
# 初始化参数
# 数据库用户创建
HMONETA_USERNAME="hmoneta"
PASSWORD_LENGTH=16
CHAR_SET="a-zA-Z0-9!@#$%^&*()_+=-"
HMONETA_PASSWORD=$(tr -dc "$CHAR_SET" < /dev/urandom | head -c "$PASSWORD_LENGTH")

# 数据名称
HMONETA_DATABASE="hmoneta"

# 连接到MySQL服务器
mysql -u root -p"${MYSQL_ROOT_PASSWORD}" <<MYSQL_SCRIPT
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
URL="jdbc:mysql://localhost:3306/$HMONETA_DATABASE?useUnicode=true;characterEncoding=UTF-8"
escaped_url=$(printf '%s\n' "$URL" | sed 's/[&/\?]/\\&/g')
sed -i "s/url:.*/url: $escaped_url/" $SPRING_PATH
sed -i "s/username:.*/username: $HMONETA_USERNAME/" $SPRING_PATH
sed -i "s/password:.*/password: $HMONETA_PASSWORD/" $SPRING_PATH

echo ">>>>>>>>>>>环境信息>>>>>>>>>>>>"
echo "MysqlRoot密码: $MYSQL_ROOT_PASSWORD"
echo "Mysql用户名: $HMONETA_USERNAME"
echo "Mysql密码: $HMONETA_PASSWORD"
echo "Mysql数据库名: $HMONETA_DATABASE"
echo "防火墙已开放的端口:"
sudo ufw status
echo "JDK版本信息:"
java --version
echo ">>>>>>>>>>>环境信息>>>>>>>>>>>>"

# 构建应用程序
# cd ~/hmoneta
# mvn clean install -PAll-in-one -DskipTests