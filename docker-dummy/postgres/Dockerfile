FROM ubuntu:16.04
MAINTAINER Cong-Xin Qiu <ozooxo@gmail.com>

RUN apt-get update && \
	apt-get install -y \
		postgresql postgresql-contrib

RUN sed -i "s/local   all             postgres                                peer/local   all             postgres                                md5/g" /etc/postgresql/9.5/main/pg_hba.conf

EXPOSE 5432

COPY database /tmp/database
RUN cd /tmp/database && sh setup.sh