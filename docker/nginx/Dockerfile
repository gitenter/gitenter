FROM nginx

COPY nginx.conf /usr/local/etc/nginx/nginx.conf

RUN mkdir -p /usr/local/var/log/nginx/
RUN touch /usr/local/var/log/nginx/access_log
RUN touch /usr/local/var/log/nginx/error_log
