FROM registry.cn-shanghai.aliyuncs.com/pig4cloud/java:17-jre

MAINTAINER pig4cloud

ENV TZ=Asia/Shanghai JAVA_OPTS="-Xms512m -Xmx1024m"

WORKDIR /app

ADD target/*.jar app.jar

EXPOSE 4200

CMD java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar app.jar
