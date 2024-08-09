#!/bin/bash
# 基于Ubuntu24.x的安装脚本
echo "欢迎使用HMoneta一键安装脚本"
echo "选择安装功能:"
echo "1) 安装前端环境"
echo "2) 安装后端环境"
echo "3) 一键安装HMoneta"
read -p "请选择功能[1-3]: " choice

# 根据用户的选择调用相应的函数
case $choice in
    1)
      setupFrontEnv
      ;;
    2)
      setupBackEnv
      ;;
    3)
      buildOnePackage
      ;;
    *)
      echo "非法输入"
      ;;
esac

sudo apt-get update
sudo apt-get upgrade -y
function setupFrontEnv() {
  # 安装所需的包
  sudo apt install curl gnupg2 software-properties-common apt-transport-https ca-certificates -y
  # 下载并导入 Yarn 的 GPG 密钥
  sudo curl -sS https://dl.yarnpkg.com/debian/pubkey.gpg | sudo gpg --dearmor -o /usr/share/keyrings/yarn-archive-keyring.gpg
  # 添加 Yarn 仓库到 sources.list.d
  echo "deb [signed-by=/usr/share/keyrings/yarn-archive-keyring.gpg] https://dl.yarnpkg.com/debian/ stable main" | sudo tee /etc/apt/sources.list.d/yarn.list
  # 更新包列表
  sudo apt update
  # 安装 Yarn
  sudo apt install yarn -y
  # 验证安装
  yarn --version
  # 安装 Node.js
  curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
  sudo apt install -y nodejs
  # 安装Nginx
  sudo apt install nginx -y
  read -s -p "请输入当前登陆系统用户名: " username
  if [ -n "$username" ]; then
    sudo usermod -aG "$username" www-data
  fi

}
function setupBackEnv() {
  echo "-------开始配置Java环境-------"
  # 清除老版本java环境
  sudo apt-get remove -y openjdk-\*
  # 安装java
  echo "安装Java环境"
  sudo apt-get install -y openjdk-21-jdk
  echo "-------完成配置Java环境-------"
    
}
function buildOnePackage() {
  setupBackEnv
  echo "-------安装Maven环境-------"
  sudo apt-get install -y maven
  echo "-------开始开放8080端口-------"
  sudo ufw allow 8080/tcp
  echo "-------安装配置MySQL环境-------"
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

  echo "-------配置Spring配置信息-------"
  SPRING_PATH="../src/main/resources/application-prod.yaml"
  URL="jdbc:mysql://localhost:3306/$HMONETA_DATABASE?useUnicode=true;characterEncoding=UTF-8"
  escaped_url=$(printf '%s\n' "$URL" | sed 's/[&/\?]/\\&/g')
  sed -i "s/url:.*/url: $escaped_url/" $SPRING_PATH
  sed -i "s/username:.*/username: $HMONETA_USERNAME/" $SPRING_PATH
  sed -i "s/password:.*/password: $HMONETA_PASSWORD/" $SPRING_PATH

  echo "-------开始构建HMoneta-------"
  cd ~/Hmoneta
  mvn clean install -PAll-in-one -DskipTests
  echo "-------完成构建HMoneta-------"
  echo "-------开始启动HMoneta-------"
  source /etc/profile
  nohup java -jar -Dspring.profiles.active=prod ~/HMoneta.jar > /dev/null 2>&1 &
  echo "-------完成启动HMoneta-------"
    
}