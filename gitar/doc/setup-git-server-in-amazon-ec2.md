# Setup Git Server in Amazon EC2

## Through SSH protocol

### Naive and not working method

Naive connection doesn't work, because the public key is not provided.

```bash
$ git clone ubuntu@ec2-54-68-62-64.us-west-2.compute.amazonaws.com:/home/ubuntu/server.git
Cloning into 'server'...
Permission denied (publickey).
fatal: Could not read from remote repository.

Please make sure you have the correct access rights
and the repository exists.
```

### Working method I

It works if the `.pem` public key is provided by the shell.

```bash
$ ssh-agent bash -c 'ssh-add /path/to/gitar-key-pair.pem; git clone ubuntu@ec2-54-68-62-64.us-west-2.compute.amazonaws.com:/home/ubuntu/server.git'
Identity added: /path/to/gitar-key-pair.pem (/path/to/gitar-key-pair.pem)
Cloning into 'server'...
warning: You appear to have cloned an empty repository.
Checking connectivity... done.
```

### Working method II

The other possibility is to add the client computer's `.ssh/id_rsa.pub` to the EC2 server's `.ssh/authorized_keys`. The way to generate `.ssh/authorized_keys` can be referred roughly to [here](https://help.github.com/articles/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent/). To add it to the `.ssh/authorized_keys` in the EC2 server's side, you want to do something similar to

```bash
$ echo -e "\n\n[materials in .ssh/id_rsa.pub]" >> ~/.ssh/authorized_keys
```

Then the following command will work:

```bash
$ git clone ubuntu@ec2-54-68-62-64.us-west-2.compute.amazonaws.com:/home/ubuntu/server.git
```

Postscript: actually when you open `.ssh/authorized_keys`, you'll find out that the public key of the EC2 provided `.pem` key-pair is already saved in there.

## Through the "dump" HTTP protocol

It doesn't work in exactly the same way as the local case.

In the EC2 server side, I installed `tomcat8`, navigate to `/var/lib/tomcat8/webapps/` and add a bare repository under `dump-git/server.git`.

Then in the client side:

```bash
$ git clone http://ec2-54-68-62-64.us-west-2.compute.amazonaws.com:8080/dump-git/server.git
Cloning into 'server'...
fatal: repository 'http://ec2-54-68-62-64.us-west-2.compute.amazonaws.com:8080/dump-git/server.git/' not found
$ git clone https://ec2-54-68-62-64.us-west-2.compute.amazonaws.com:8080/dump-git/server.git
Cloning into 'server'...
fatal: unable to access 'https://ec2-54-68-62-64.us-west-2.compute.amazonaws.com:8080/dump-git/server.git/': gnutls_handshake() failed: An unexpected TLS packet was received.
```

Basically you know it cannot work, because they'll then ask for the username and password of the EC2 server but I don't know the password (username is `ubuntu`). But as the same `gnutls_handshake()` appears (this time in Ubuntu 16.04 rather than my local 14.04), that means we at least need to solve this problem first.
