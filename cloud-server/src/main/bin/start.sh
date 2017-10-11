#!/bin/bash
# 
# author by liuguangliang
# 后台服务启动shell脚本
# 这里的服务是指不需要web等容器加载的,直接java命令启动
# 
#
cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`

#日志目录
DIR_NAME=`pwd |sed 's,^\(.*/\)\?\([^/]*\),\2,'`
LOGS_DIR=$DEPLOY_DIR

echo "LOG DIR IS: $LOGS_DIR"

#配置文件目录
APP_CONFIG_PATH=

if [ -z $APP_CONFIG_PATH ]; then
	CONF_DIR=$DEPLOY_DIR/conf
else
	CONF_DIR=$APP_CONFIG_PATH
fi

echo "current deploy-dir is : $DEPLOY_DIR"

#环境变量
ENV=product

#配置文件名称
CONF_FILE=config.properties

echo "now use config filename is : $CONF_FILE"

#程序运行主类
SERVER_MAIN=com.kanven.cloud.server.Bootstrap

echo "start server use main class : $SERVER_MAIN"


#启动服务的服务名和端口号,一般建议配在配置文件当中,启动和关闭时方便处理
#服务名称和部署目录名称一般保持一致吧
SERVER_NAME=`sed '/server.name/!d;s/.*=//' $CONF_DIR/$CONF_FILE | tr -d '\r'`
#端口号
SERVER_PORT=`sed '/server.port/!d;s/.*=//' $CONF_DIR/$CONF_FILE | tr -d '\r'`

echo "init server name[$SERVER_NAME],server port[$SERVER_PORT]"

if [ -z "$SERVER_NAME" ]; then
	SERVER_NAME=`hostname`
fi

#判断一下该服务名称是否已经启动加载了
PIDS=`ps  --no-heading -C java -f --width 1000 | grep "$CONF_DIR" |awk '{print $2}'`
if [ -n "$PIDS" ]; then
    echo "ERROR: The $SERVER_NAME already started!"
    echo "PID: $PIDS"
    exit 1
fi

#判断端口号有没有被占用
if [ -n "$SERVER_PORT" ]; then
	SERVER_PORT_COUNT=`netstat -tln | grep $SERVER_PORT | wc -l`
	if [ $SERVER_PORT_COUNT -gt 0 ]; then
		echo "ERROR: The $SERVER_NAME port $SERVER_PORT already used!"
		exit 1
	fi
fi

#日志目录,一般log4j.properties文件中会加载应用的日志目录配置的
#LOGS_DIR=$DEPLOY_DIR/logs

#控制台日志
STDOUT_FILE=$LOGS_DIR/stdout.log

#所有依赖到的jar文件
LIB_DIR=$DEPLOY_DIR/lib
LIB_JARS=`ls $LIB_DIR|grep .jar|awk '{print "'$LIB_DIR'/"$0}'|tr "\n" ":"`

#java 常规启动
JAVA_OPTS=" -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -Denv=$ENV -DLogPath=$LOGS_DIR -Dcom.sun.management.jmxremote.port=2127 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false"
#debug启动,端口号默认使用8000

#默认启动内存配置
JAVA_MEM_OPTS=""

JAVA_MEM_OPTS=" -server -Xmx1024m -Xms1024m -Xmn512m -XX:PermSize=256m -Xss512k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 "


echo -e "Starting the $SERVER_NAME ...\c"
nohup java $JAVA_OPTS $JAVA_MEM_OPTS   -classpath $CONF_DIR:$LIB_JARS $SERVER_MAIN  &

COUNT=0
while [ $COUNT -lt 1 ]; do    
    echo -e ".\c"
    sleep 1 
#   if [ -n "$SERVER_PORT" ]; then
#   	COUNT=`echo status | nc 127.0.0.1 $SERVER_PORT -i 1 | grep -c OK`
#   else
    	COUNT=`ps  --no-heading -C java -f --width 1000 | grep "$DEPLOY_DIR" | awk '{print $2}' | wc -l`
#  fi
	if [ $COUNT -gt 0 ]; then
		break
	fi
done
echo "OK!"
PIDS=`ps  --no-heading -C java -f --width 1000 | grep "$DEPLOY_DIR" | awk '{print $2}'`
echo "PID: $PIDS"
echo "STDOUT: $STDOUT_FILE"

