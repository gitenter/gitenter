# cURL Notes

The simplest use is to `curl` with the URL to get the reply content:

```
$ curl http://www.gitenter.com/login
```

Command line options can be checked by `curl -h`. Highlights:

`-o` save it in a file, `-O` save the file with the same name as it appears in the remote server. Multiple files can be downloaded together in one cURL command.

```
$ curl -o login.html http://www.gitenter.com/login
$ curl -O http://www.gitenter.com/login # save it as a file names `login`
$ curl -O http://www.gitenter.com/login -O http://www.gitenter.com/register # Save two files together in one command
```

`-X` to specify request method.

```
$ curl -X GET http://www.gitenter.com/login # normal response
$ curl -X POST http://www.gitenter.com/login
{"timestamp":1518452617053,"status":403,"error":"Forbidden","message":"Could not verify the provided CSRF token because your session was not found.","path":"/login"}
```

Get HTTP header information (only) by `-I`. Include header (header and content) by `-i`.

```
$ curl -I http://www.gitenter.com/login
HTTP/1.1 200 OK
Server: Apache-Coyote/1.1
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Set-Cookie: JSESSIONID=3ADEEC023FEA527D4C37E18354409DF8; Path=/; HttpOnly
Content-Type: text/html;charset=ISO-8859-1
Content-Language: en-US
Content-Length: 1643
Date: Sun, 11 Feb 2018 22:28:36 GMT
```

`-H` or `--header` to pass custom header LINE to server. One line after each single `-H`. That (seems) is for case the server need that setup. If not, no matter what header you pass to, the result is just the same.

```
$ curl https://api.github.com/app
{
  "message": "A JSON web token could not be decoded",
  "documentation_url": "https://developer.github.com/v3"
}
```

```
$ curl -H "Authorization: Bearer $(ruby jwt-generator.rb)" -H "Accept: application/vnd.github.machine-man-preview+json" https://api.github.com/app
{
    ...
}
```
