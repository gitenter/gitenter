# enterovirus

Enterovirus is not a computer virus!

## Docker

Build image

```
#sudo docker stop gitar
#sudo docker rm $(sudo docker ps -a -q -f status=exited)
sudo docker build -t ozooxo/enterovirus .
sudo docker run -d -P --name gitar ozooxo/enterovirus
sudo docker port gitar
```

Goes to the detached mode

```
sudo docker run -d -p 52022:22 --name gitar ozooxo/enterovirus
```

Goes to command line mode

```
sudo docker run -it ozooxo/enterovirus
```

Goes to command line mode of a bright new version

```
sudo docker run -it --rm ozooxo/enterovirus
```
