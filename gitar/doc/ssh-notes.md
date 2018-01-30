# SSH Notes

SSH stands **S**-ecure **Sh**-ell.

+ A secure protocol.
+ Send local commands to remote server through an encrypted SSH tunnel, and execute them there.
+ Login using the account of the remote server, and UI is a remote shell.

Under the client-server model

+ Server: SSH daemon `sshd` for (1) connect to a specific network port, (2) authenticates connection requests, (3) spawns the appropriate environment.
+ Client: execute communication under the given information, includes (1) connection type, (2) host information, (3) username, (4) credentials (password or SSH key).

Authentication

+ Password: Less secure. Not recommended.
+ SSH key: a matching pair of cryptographic keys contains
	+ A public key: listed in server location `~/.ssh/authorized_keys`.
	+ A private key: in client's place.
	+ Authentication procedure:
		+ Client tells server which public key to use.
		+ Server generate a random string and encrypts it using the public key.
		+ Client decrypt the string using primary key.
		+ Client MD5 hash the string and send it back to the server.
		+ Server check the matching of the string.

## SSH key pair

Generation method

+ RSA
	+ User command `ssh-keygen`, or `ssh-keygen -t rsa`.
	+ The default place to store the private key is `~/.ssh/id_rsa` (SSH client to find the keys automatically), and the default place for the public key is `~/.ssh/id_rsa.pub`.
	+ May enter a **passphrase**, then it is needed every time you access the private key. Blank should be okay.
		+ Removing or Changing the Passphrase: `ssh-key -p` then provide old and new passphrases.
		+ May run an SSH agent to avoid typing the passphrase every time connecting to the remote host.
	+ Options:
		+ Default 2048 bits. More bits by `ssh-keygen -b 4096`.
		+ Default using `username@hostname`. Change it by `ssh-keygen -C "another_username@another_hostname_or_domain_name"`
	+ A uniquely identify the keys called **cryptographic fingerprint** can be shown by `ssh-keygen -l`.
+ DSA
+ ECDSA

Copying public key to a server

+ With (1) SSH-Copy-ID utility and (2) password-based SSH access: `ssh-copy-id username@remote_host` then provide the server password. Then `~/.ssh/id_rsa.pub` will be automatically added to the server's `~/.ssh/authorized_keys`.
+ Without SSH-Copy-ID utility but has (1) password-based SSH access: `cat ~/.ssh/id_rsa.pub | ssh username@remote_host "mkdir -p ~/.ssh && cat >> ~/.ssh/authorized_keys"` then provide the server password.
+ Manually without password-based SSH access: `cd` to the remote `~/.ssh` and `echo public_key_string >> ~/.ssh/authorized_keys`.

There is no upper limit for the number of maximal authorized keys. But as SSH just do a [linear scanning](https://serverfault.com/questions/486598/which-is-the-maximum-number-of-keys-in-authorized-keys-file) on the `authorized_keys` folder, you face bottleneck after 5000 keys (1 second). Possible solutions may refer to [here](https://groups.google.com/forum/#!topic/gitolite/RuN2mGKeDo4) and [here](https://groups.google.com/forum/#!searchin/gitolite/jason$20donenfeld/gitolite/ya-YVHi_YYg/wQwKO1izBZwJ) (didn't read yet).

## (1) Build connection and (2) client-side configuration

Log in to the remote shell (first time connection include host checking warning, so you can user the fingerprint to check whether the remote host is masqueraded...)

```bash
ssh username@remote_host
```

Running a single command only

```bash
ssh username@remote_host command_to_run
```

#### Options

+ Different port (default port is 22): `ssh -p port_num username@remote_host`
+ SSH agent setup (to avoid typing the passphrase every time connecting to the remote host): (1) `eval $(ssh-agent)` to start the agent program in the background, and (2) `ssh-add`.
+ Connect to a server to forward to another server: (1) setup SSH agent in the client side, (2) log in the forward server by `ssh -A username@remote_host`, (3) in the remote shell of the forward server, log in the final server by normal method.

#### Customizing configuration

Configure `~/.ssh/config` and setup various options.

```
Host *
    ForwardX11 no

Host remote_alias
    HostName remote_host
    Port port_num
    User username

Host testhost
    HostName example.com
    ForwardX11 yes
    Port 4444
    User demo
```

+ Later matches can override earlier ones, so the general ones should be on top.
+ Wildcards are okay: `Host *`.
+ Avoid timeout: `ServerAliveInterval 120` to send a packet to the server every two minutes to notify the server not the close the connection.
+ Disabling host checking
	+ Procedure: (1) `StrictHostKeyChecking no` to add new host automatically to the `~/.ssh/known_hosts` file, (2) `UserKnownHostsFile /dev/null` for not warning.
	+ Default values are `StrictHostKeyChecking ask` and `UserKnownHostsFile /home/demo/.ssh/known_hosts`.
+ Multiplexing:
	+ Why to use: reduce the time taking to establish a new TCP connection.
	+ Why it works: Re-uses the same (client-side) TCP connection for multiple SSH sessions. Save time needed for establish new sessions.
	+ Procedure: In `~/.ssh/config`, setup (1) `ControlMaster auto` to allow multiplexing. (2) `ControlPath ~/.ssh/multiplex/%r@%h:%p` to establish the path to control socket. (3) `ControlPersist 1` to allow the initial master connection to be backgrounded, and the TCP connection should automatically terminate one second. after the last SSH session is closed. (4) actually create the directory `mkdir ~/.ssh/multiplex`.
	+ To bypass multiplexing, using `ssh -S none username@remote_host`.

Then simply `ssh remote_alias`.

## Server-side configuration

Various setups are in `/etc/ssh/sshd_config`. Uncomment and change to...

+ Disabling password authentication: Change `PasswordAuthentication no`.
+ Change the port: Change `Port 4444`.
	+ Help decrease the number of authentication attempts your server is subjected to from automated bots.
+ Limiting the (server-side) user who can connect through SSH: Change `AllowUsers user1 user2` or `AllowGroups sshmembers`.
+ Disabling root login: change `PermitRootLogin no`.
	+ For special cases you want certain applications to run correctly, you may need to allow root access for specific commands. Procedure: (1) add SSH key to the root's `~/.ssh/authorized_keys` (recommend creating a new key for each automatic process), (2) in `~/.ssh/authorized_keys` of the root user, add `command="/path/to/command arg1 arg2" ssh-rsa ...` at the beginning of every line the particular key, (3) Change `etc/ssh/sshd_config` corresponding line to `PermitRootLogin forced-commands-only`.
+ Forwarding X application displays to the client: (1) change `X11Forwarding yes`, then (2) use `ssh -X username@remote_host` for log in.

Always remember to `sudo service ssh restart` after change the configuration.

## Tunneling

Aim to tunneling other traffic through a secure SSH tunnel

+ Work around restrictive firewall settings
+ Encrypt otherwise unencrypted network traffic

Local tunneling: Connect from the local side. (From the local machine) access the local port to touch the remote port.

```
local-pc$ ssh -L local-port:remote-pc-IP:remote-port remote-username@remote-pc-IP
local-pc$ ping localhost:local-port   # ----> redirect to remote-IP:remote-port
```

Remote tunneling: Connect from the local side. (From the remote machine) access the remote port to touch the local port.

```
local-pc$ ssh -R remote-port:remote-pc-IP:local-port remote-username@remote-pc-IP
remote-pc$ ping localhost:remote-port   # ----> redirect to remote-IP:local-port
```

Dynamic tunneling: Similar to local tunning but only the local port is specified. Using SOCKS protocol, which will be interpreted to establish a connection to the desired end location. Use SOCKS-aware application (like a web browser) to access the port, and the SOCKS port will be setup somewhere like "proxy configurations".

```
local-pc$ ssh -D local-port username@remote_host
```

Further options: `-f` to let the SSH tunning to run in the background. `-N` does not open a shell or execute a program on the remote side.

## Control connections

Using **SSH escape code** within the terminal.

Withing the SSH shell from the client side

+ Disconnection: `~.`
+ Place to the background: `~[Ctrl-z]`. Return from local shell session to the SSH session: `fg`, and if list multiple ones and choose `jobs` and `fg %2`
+Change port forwarding options: `~C`

## Customization/forced commands

SSH can be used to execute commands remotely. By default, after executed the command, it returns to the local shell. If you don't want user to access the shell, you want to do "forced commands".

`.ssh/authorized_keys` file can be altered, so every line (include a public key) can have a set of (1) options and (2) command= values to restrict the incoming users. With `.ssh/authorized_keys` form:

```
command="[path],[more options] ssh-rsa AAAA...
command="./an-executable-script.sh arg1 arg2",no-port-forwarding,no-x11-forwarding,no-agent-forwarding,no-pty ssh-rsa AAAA....
```

`./an-executable-script.sh` stays at the root of the home directory of the real unix user. Or one may do absolute path starting with `/`.

SSH save the original command the user use to the `SSH_ORIGINAL_COMMAND` environment variable, which the command script may later query from.

Without the `command=` option, ssh gives you back the shell with full authorization.

Tricks:

+ Write the username as an `arg` of the script (hence write explicitly in the `.ssh/authorized_keys` file). So each time a user tries to log in (it triggered the corresponding public key), the username is known.
+ The original `SSH_ORIGINAL_COMMAND` saves the other information, e.g., the git repository name (if it is a git SSH connection).

May refer to [a general introduction](https://binblog.info/2008/10/20/openssh-going-flexible-with-forced-commands/) include a Shell example of command script, and [a perl example of command script](https://blog.farville.com/using-command-in-authorized_keys-to-restrict-shell-like-gitolite/).

[Here](https://research.kudelskisecurity.com/2013/05/14/restrict-ssh-logins-to-a-single-command/) talks about examples how to write forced commands.

[Link to the OFFICIAL](http://man.openbsd.org/sshd.8#AUTHORIZED_KEYS_FILE_FORMAT) `authorized_keys` file format

### `SSH_ORIGINAL_COMMAND` environment variable

How to get it?

+ Shell: `$SSH_ORIGINAL_COMMAND`
+ Perl: `$ENV{SSH_ORIGINAL_COMMAND}`

Form:

`ssh ip uname -a` => `uname -a`

`git clone ssh://git@ip:path/to/a/repo.git` => ?

`git clone git@ip:path/to/a/repo.git` => ?

### Disable aadvanced SSH features

+ `no-port-forwarding`
+ `no-X11-forwarding`
+ `no-agent-forwarding`
+ `no-pty`
+ ...

Full list of [restricting public keys](https://sanctum.geek.nz/arabesque/restricting-public-keys/).

## Versions

+ Different versions of protocols:
	+ SSH-1 (year 1995)
	+ SSH-2 (year 2006)

## Software

+ **SSH**: The client. Disable it means you cannot use SSH to connect to other machines.
	+ OpenSSH: `openssh-client`
+ **SSHD**: the daemon (守护进程). Disable it makes you won't be able to login remotely. It is the technical term of ssh server.
	+ OpenSSH: `openssh-server`. Configuration at `	/etc/ssh/sshd_config`.

## References

1. [SSH Essentials: Working with SSH Servers, Clients, and Keys](https://www.digitalocean.com/community/tutorials/ssh-essentials-working-with-ssh-servers-clients-and-keys)
