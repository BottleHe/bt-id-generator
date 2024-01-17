# 环境变量
# VERSION 版本
# DOCKER_REPOSITORY   docker仓库地址

# APP_NAME   应用名称
# INSTALL_PREFIX  安装路径
# LOG_PATH   日志路径
# EXPOSE_PORT    暴露端口
# MIN_MEMORY 最小堆内存
# MAX_MEMORY 最大堆内存
# NEW_MEMORY 新生代内存

# NAMESPACE 命名空间
NEED_BUILD=`docker image ls "${DOCKER_REPOSITORY}/${APP_NAME}:${VERSION}" | wc -l`
echo "NEED_BUILD = $NEED_BUILD"
if [ 1 -eq $NEED_BUILD ]; then # 如果镜像不存在
    # 进入目录
	cd ${WORKSPACE}/src
    # 切换到指定的tag
    git checkout ${VERSION}

#    /usr/local/apache-maven-3.8.6/bin/mvn clean package -DskipTests -N
    cd ${APP_NAME}
    /usr/local/apache-maven-3.8.6/bin/mvn clean package -DskipTests
    cd target
    # 必须打包 lib 目录
    tar -zcf ${WORKSPACE}/${APP_NAME}.tar.gz ${APP_NAME}.jar
    { \
        echo "FROM storage.bottle.work:15000/private/java:1.0.1"; \
        echo 'MAINTAINER BottleHe(bottle@fridayws.com)'; \
        echo "ENV APP_PATH=${INSTALL_PREFIX}/${APP_NAME}/${APP_NAME}.jar"; \
        echo 'ENV LANG=C.UTF-8'; \
        echo "RUN mkdir -p ${INSTALL_PREFIX}/${APP_NAME} && mkdir -p ${LOG_PATH}/${APP_NAME}"; \
        echo "COPY ./${APP_NAME}.tar.gz ${INSTALL_PREFIX}/${APP_NAME}/"; \
        echo "RUN tar -zxf ${INSTALL_PREFIX}/${APP_NAME}/${APP_NAME}.tar.gz -C ${INSTALL_PREFIX}/${APP_NAME}/"; \
        echo "EXPOSE ${EXPOSE_PORT}"; \
        echo 'ENTRYPOINT java -Xms"${MIN_MEMORY:-1024}"M -Xmx"${MAX_MEMORY:-1024}"M -Dspring.profiles.active="${ACTIVE_PROFILE:-test}" -jar "${APP_PATH}"'; \
    } | tee ${WORKSPACE}/Dockerfile
    cd ${WORKSPACE}
    docker build -t ${APP_NAME}:${VERSION} .
    # 打tag
    docker tag ${APP_NAME}:${VERSION} ${DOCKER_REPOSITORY}/${APP_NAME}:${VERSION}
    # push
    docker push ${DOCKER_REPOSITORY}/${APP_NAME}:${VERSION}
fi


#  下面是远程执行代码, TODO 未测试版本.

# 在docker-01 上启动容器
ssh docker-01 '
echo "" > ~/jenkins.log
# 停止原有的容器
docker stop `docker ps | grep ${APP_NAME} | grep -v grep | awk '"'"'{print $1}'"'"' `
# 启动新容器
# 判断是否存在可运行的容器, 其必需是退出状态
CONTAINER_ID=`docker ps -a -f name="^${APP_NAME}-${VERSION}\$" -f status="exited" | grep ${APP_NAME}:${VERSION} | grep -v grep | awk '"'"'{print $1}'"'"'`
if [ -n "$CONTAINER_ID" ]; then
  docker start $CONTAINER_ID   # 直接启动
else
  NEED_BUILD=`docker image ls "${DOCKER_REPOSITORY}/${APP_NAME}:${VERSION}" | wc -l`
  if [ 1 -eq "$NEED_BUILD" ]; then # 如果镜像不存在
    docker pull "${DOCKER_REPOSITORY}/${APP_NAME}:${VERSION}"
    docker run --name="${APP_NAME}-${VERSION}" -v ${LOG_PATH}/${APP_NAME}:${LOG_PATH}/${APP_NAME} -v /usr/local/jdk1.8.0_351:/usr/local/jdk1.8.0_351 -e JAVA_HOME=/usr/local/jdk1.8.0_351 -e MIN_MEMORY=512 -e MAX_MEMORY=512 -e ACTIVE_PROFILE=prod -e BT_INSTANCE_ID=docker-01 -p 17782:17782 -d "${DOCKER_REPOSITORY}/${APP_NAME}:${VERSION}"
  else
    docker run --name="${APP_NAME}-${VERSION}" -v ${LOG_PATH}/${APP_NAME}:${LOG_PATH}/${APP_NAME} -v /usr/local/jdk1.8.0_351:/usr/local/jdk1.8.0_351 -e JAVA_HOME=/usr/local/jdk1.8.0_351 -e MIN_MEMORY=512 -e MAX_MEMORY=512 -e ACTIVE_PROFILE=prod -e BT_INSTANCE_ID=docker-01 -p 17782:17782 -d "${DOCKER_REPOSITORY}/${APP_NAME}:${VERSION}"
  fi
fi
exit 0
'


