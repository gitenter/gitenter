docker stop tomcat
docker rm $(docker ps -a -q -f status=exited)
docker rmi ozooxo/tomcat
docker build -t ozooxo/tomcat .

#docker build --no-cache -t ozooxo/tomcat .

docker run -d -p 58080:80 --name tomcat ozooxo/tomcat
