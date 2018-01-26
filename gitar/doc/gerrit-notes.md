# Gerrit Notes

A.k.a, Googit Git

Gerrit itself is for code review, which doesn't fit my propose. The part of Gerrit I am interested, is how it handles the access control while everybody is connecting through SSH using the same username `git`.

It does stays in [maven](https://mvnrepository.com/artifact/com.google.gerrit), but it doesn't come as a general library :-(

I haven't local which part of its code is working for that propose. Nor do I find any good references.

## References

1. [Gerrit Code Review - A Quick Introduction](https://review.openstack.org/Documentation/intro-quick.html): Not useful. Mostly the implementation part how to use it as a code review tool.
1. [Gerrit Documentations](https://gerrit-documentation.storage.googleapis.com/Documentation/2.14.5.1/index.html)
1. [Gerrit Code Review - Access Controls](https://review.openstack.org/Documentation/access-control.html): Too detailed and fit to the use case of itself. Mostly comes as a user manual.
