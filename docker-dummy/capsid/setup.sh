docker stop capsid
docker rm $(docker ps -a -q -f status=exited)
docker rmi ozooxo/capsid
docker build -t ozooxo/capsid .

#docker build --no-cache -t ozooxo/tomcat .

docker run -d -p 58080:80 --name capsid ozooxo/capsid
