# Database Setting

### Basic

The script needs a postgres user with username `postgres` and password `postgres`.

```
sudo apt-get install postgresql postgresql-contrib
sudo -u postgres psql postgres
\password postgres
```

### PgAdmin III

By using PgAdmin III to show the postgres data, extra setup is needed.

```
sudo gedit /etc/postgresql/9.5/main/pg_hba.conf
```

and change the line

```
# Database administrative login by Unix domain socket
local   all             postgres                                peer
```

to

```
# Database administrative login by Unix domain socket
local   all             postgres                                md5
```

## References

1. https://help.ubuntu.com/community/PostgreSQL
