# Java 1.8 & Maven Dockerfile
# pull base image.
FROM java:8
# maintainer
MAINTAINER Bill
# update packages and install maven
RUN  \
  export DEBIAN_FRONTEND=noninteractive && \
  sed -i 's/# \(.*multiverse$\)/\1/g' /etc/apt/sources.list && \
  apt-get update && \
  apt-get -y upgrade && \
  apt-get install -y vim wget curl maven
# attach volumes
VOLUME /vol/development
# create working directory
RUN mkdir -p /vol/development
WORKDIR /vol/development
# maven exec: , "exec:java"
CMD ["mvn", "clean", "install"]