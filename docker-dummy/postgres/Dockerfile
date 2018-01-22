FROM ubuntu:16.04
MAINTAINER Cong-Xin Qiu <ozooxo@gmail.com>

RUN apt-get update && \
	apt-get install -y \
		postgresql postgresql-contrib

USER root
RUN echo "host all  all    0.0.0.0/0  md5" >> /etc/postgresql/9.5/main/pg_hba.conf
RUN echo "listen_addresses='*'" >> /etc/postgresql/9.5/main/postgresql.conf

USER postgres
RUN /etc/init.d/postgresql start && \
	psql --command "ALTER USER postgres with encrypted password 'postgres';"

#CMD ["/etc/init.d/postgresql", "start"]


#RUN sed -i "s/local   all             postgres                                peer/local   all             postgres                                md5/g" /etc/postgresql/9.5/main/pg_hba.conf

EXPOSE 5432

COPY database /tmp/database
#RUN cd /tmp/database && sh setup.sh

#RUN echo 'root:secretpassword' | chpasswd
#RUN echo 'postgres:postgres' | chpasswd

# References:
# https://zaiste.net/posts/docker_postgresql_how_to/
# https://www.andreagrandi.it/2015/02/21/how-to-create-a-docker-image-for-postgresql-and-persist-data/