# enterovirus

Enterovirus is not a computer virus!

## Docker

Build image of SSH

```
sudo docker stop gitar
sudo docker rm $(sudo docker ps -a -q -f status=exited)
sudo docker build -t ozooxo/enterovirus .
sudo docker run -d -p 52022:22 --name gitar ozooxo/enterovirus
#sudo docker port gitar
```

Log in by user git

```
ssh git@0.0.0.0 -p 52022
```
