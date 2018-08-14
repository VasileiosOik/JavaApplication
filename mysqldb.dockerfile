FROM mysql/mysql-server
MAINTAINER Bill
ENV MYSQL_DATABASE=testdb1 \
    MYSQL_ROOT_PASSWORD=root
ENV MYSQL_DATABASE=testdb2 \
    MYSQL_ROOT_PASSWORD=root
# Copy the database initialize script:
# Contents of /docker-entrypoint-initdb.d are run on mysqld startup
#ADD  mysql/ /docker-entrypoint-initdb.d/