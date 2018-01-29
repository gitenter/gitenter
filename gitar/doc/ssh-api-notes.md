# SSH API Notes

Mostly Java related.

## SSH Client

It seems most people are using them because they want their Java program to do SSH file transferring (may through SFTP). It is mostly like `new SSHClient();` a Java Virtual SSH client, and use it to connect/do operation on a remote server.

Comparisons in [here](https://blog.sandra-parsick.de/2015/07/30/commons-vfs-sshj-and-jsch-in-comparison/) and [here](https://medium.com/@ldclakmal/comparison-of-commons-vfs-sshj-and-jsch-libraries-for-sftp-support-cd5a0db2fbce)

### [Java Secure Channel (SJch)](http://www.jcraft.com/jsch/)

A pure Java implementation of SSH2.

Seems not have tutorials available, but have a list of [examples](http://www.jcraft.com/jsch/examples/).

Seems all examples are on the client side. (1) Virtual SSH client to operation a remote server, (2) Key generation related, (3) Work with client configuration file `~/.ssh/config`. *(Doesn't fit my current needs.)*

Used by:

+ Ant
+ Eclipse
+ Maven Wagon: a transport abstraction that is used in Maven's artifact and repository handling code.
+ JIRA

#### Comparison

The de-facto standard for Java

Pro: A much more concise API than sshj

### [sshj](https://github.com/hierynomus/sshj)

A newer library

Goal: Provide a clear Java API for SSH -- shorter and clearer code.

### Apache’s Commons VFS

Goal: have a clear API for virtual file systems and SFTP is one of the supported protocol.

## SSH Server/Client

### OpenSSH

The single most popular SSH implementation.

+ Currently support SSH-2
+ Has expunged SSH-1 support

### [Apache Mina sshd](http://mina.apache.org/sshd-project/)

*Apache MINA* is a high performance asynchronous IO library. Apache Mima SSHD is its support of SSH protocols on both client/server sides.

Not a replacement for the SSH client or SSH server from Unix operating systems. *(By reading its [tutorials](https://mina.apache.org/sshd-project/documentation.html) it seems not WORKING WITH ssh (the one setup at `.ssh` at the root Unix user), but a IMPLEMENTATION of ssh for Java applications (e.g., if Eclipse need an ssh by itself).)*

Provides support for Java based applications requiring SSH support.

~~Although its official website provides an out-of-dated version which haven't been updated for [5 years](http://svn.apache.org/viewvc/mina/sshd/trunk/sshd-core/src/main/java/org/apache/sshd/), with a lot of TODOs in website, documents.~~ It is actually managed based on [new released maven version](https://mvnrepository.com/artifact/org.apache.sshd/sshd-core), and [github mirror of source code](https://github.com/apache/mina-sshd).

Documents are really poor. There is an out-of-date (v 1.1.0 rather than the newest 1.7.0) [JavaDoc](https://mina.apache.org/sshd-project/apidocs/index.html), which doesn't match the newest version. But one can still guess what is going on. E.g., for the class of editing the `.ssh/authorized_keys` file at [this locatiion](https://mina.apache.org/sshd-project/apidocs/org/apache/sshd/server/config/keys/AuthorizedKeyEntry.html), it is actually in [a package with different name](https://github.com/apache/mina-sshd/blob/master/sshd-core/src/main/java/org/apache/sshd/common/config/keys/AuthorizedKeyEntry.java).

Used by Gerrit.
