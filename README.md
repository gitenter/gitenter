# enterovirus

Enterovirus is not a computer virus!

## Docker

(Re-)build image

```
sudo docker stop gitar
sudo docker rm $(sudo docker ps -a -q -f status=exited)
sudo docker rmi ozooxo/enterovirus
sudo docker build -t ozooxo/enterovirus .
sudo docker run -d -p 52022:22 -p 52418:9418 --name gitar ozooxo/enterovirus
#sudo docker port gitar
```

Access shell (need to stop containers first...)

```
sudo docker run -it ozooxo/enterovirus sh
```

Log in by SSH

```
ssh git@0.0.0.0 -p 52022
```

Clone git repository

```
git clone ssh://git@0.0.0.0:52418/home/git/server.git
git clone git@0.0.0.0:52418/home/git/server.git
```