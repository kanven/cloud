#!/bin/bash
# 
# author by liuguangliang
# 后台服务关闭shell脚本
# 这里的服务是指不需要web等容器加载的,直接java命令关闭
# 
#

cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`


#启动服务的服务名和端口号,一般建议配在配置文件当中,启动和关闭时方便处理
#服务名称和部署目录名称一般保持一致吧
SERVER_NAME=$DEPLOY_DIR

if [ -z "$SERVER_NAME" ]; then
	SERVER_NAME=`hostname`
fi

echo "begin stopping $SERVER_NAME"

#找到服务对应的pid来停掉,根据服务名或者端口号来找都可以吧
PID=`ps -ef|grep "$SERVER_NAME"|grep -v "grep"|awk '{print $2}'|head -n 1`

if [ -z "$PID" ]; then
    echo "ERROR: The $SERVER_NAME does not started!"
    exit 1
fi

echo "Find server name[$SERVER_NAME] macth PID is:$PID"

echo -e "Stopping the $SERVER_NAME ..."

#kill target pid
kill -9 $PID


echo "Stopped $SERVER_NAME [$PID] Done!!!"

