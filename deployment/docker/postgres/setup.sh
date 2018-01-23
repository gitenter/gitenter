docker stop postgres
docker rm $(docker ps -a -q -f status=exited)
docker rmi ozooxo/postgres
docker build -t ozooxo/postgres .

#docker build --no-cache -t ozooxo/postgres .

docker run -it ozooxo/postgres sh
