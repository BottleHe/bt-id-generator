# 下面是需要的环境变量
# VERSION    版本号
# DOCKER_REPOSITORY   docker仓库地址
# REPLICATE    副本数

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
    # 清除老旧镜像
    #for ID in $(docker images | grep ${APP_NAME} | awk 'NR>6{print $3}' | sort -u);
    #do
    #docker rmi $ID
    #done
fi
# 生成发布脚本
{ \
  echo '---'; \
  echo '# 创建secret'; \
  echo 'apiVersion: v1'; \
  echo 'kind: Secret'; \
  echo 'metadata: '; \
  echo '    name: jenkins-docker-secret'; \
  echo "    namespace: ${NAMESPACE}"; \
  echo 'data:'; \
  echo '    .dockerconfigjson: ewoJImF1dGhzIjogewoJCSJpdC56bXlsY2xvdWxkLmNvbToxNTAwMCI6IHsKCQkJImF1dGgiOiAiYW1WdWEybHVjenBxUlc1ck1VNXpPRUJsZUhNeCIKCQl9Cgl9Cn0='; \
  echo 'type: kubernetes.io/dockerconfigjson'; \
  echo ''; \
  echo '---'; \
  echo ''; \
  echo 'apiVersion: apps/v1'; \
  echo 'kind: StatefulSet'; \
  echo 'metadata:'; \
  echo "  name: ${APP_NAME}"; \
  echo "  namespace: ${NAMESPACE}"; \
  echo 'spec:'; \
  echo '  selector:'; \
  echo '    matchLabels:'; \
  echo "      app: ${APP_NAME}"; \
  echo "  serviceName: ${APP_NAME}"; \
  echo "  replicas: ${REPLICATE}"; \
  echo '  template: '; \
  echo '    metadata: '; \
  echo '      labels:'; \
  echo "        app: ${APP_NAME}"; \
  echo '    spec:'; \
  echo '      imagePullSecrets:'; \
  echo '      - name: jenkins-docker-secret'; \
  echo '      containers:'; \
  echo "      - name: ${APP_NAME}"; \
  echo "        image: ${DOCKER_REPOSITORY}/${APP_NAME}:${VERSION}"; \
  echo '        ports: '; \
  echo "        - containerPort: ${EXPOSE_PORT}"; \
  echo '        imagePullPolicy: IfNotPresent'; \
  echo ''; \
  echo '---'; \
  echo ''; \
  echo 'apiVersion: v1'; \
  echo 'kind: Service'; \
  echo 'metadata:'; \
  echo "  name: ${APP_NAME}"; \
  echo "  namespace: ${NAMESPACE}"; \
  echo 'spec: '; \
  echo '  type: NodePort'; \
  echo '  ports: '; \
  echo "  - port: ${EXPOSE_PORT}"; \
  echo "    targetPort: ${EXPOSE_PORT}"; \
  echo "    nodePort: ${NODE_PORT}"; \
  echo '  selector:'; \
  echo "    app: ${APP_NAME}"; \
} | tee ${WORKSPACE}/${APP_NAME}-test.yaml
