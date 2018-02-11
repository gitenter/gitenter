# cURL Notes

The simplest use is to `curl` with the URL to get the reply content:

```bash
$ curl http://www.gitenter.com/login
```

Command line options can be checked by `curl -h`. Highlights:

`-o` save it in a file, `-O` save the file with the same name as it appears in the remote server. Multiple files can be downloaded together in one cURL command.

```bash
$ curl -o login.html http://www.gitenter.com/login
$ curl -O http://www.gitenter.com/login # save it as a file names `login`
$ curl -O http://www.gitenter.com/login -O http://www.gitenter.com/register # Save two files together in one command
```

Get HTTP header information (only) by `-I`. Include header (header and content) by `-i`.

```bash
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
$ curl -i -H "Authorization: Bearer $(ruby jwt-generator.rb)" -H "Accept: application/vnd.github.machine-man-preview+json" https://api.github.com/app
HTTP/1.1 200 OK
Server: GitHub.com
Date: Sun, 11 Feb 2018 22:39:48 GMT
Content-Type: application/json; charset=utf-8
Content-Length: 1188
Status: 200 OK
Cache-Control: public, max-age=60, s-maxage=60
Vary: Accept
ETag: "5e5a70e782f724736b3ed33f4080f0a6"
X-GitHub-Media-Type: github.machine-man-preview; format=json
Access-Control-Expose-Headers: ETag, Link, Retry-After, X-GitHub-OTP, X-RateLimit-Limit, X-RateLimit-Remaining, X-RateLimit-Reset, X-OAuth-Scopes, X-Accepted-OAuth-Scopes, X-Poll-Interval
Access-Control-Allow-Origin: *
Content-Security-Policy: default-src 'none'
Strict-Transport-Security: max-age=31536000; includeSubdomains; preload
X-Content-Type-Options: nosniff
X-Frame-Options: deny
X-XSS-Protection: 1; mode=block
X-Runtime-rack: 0.018250
X-GitHub-Request-Id: E95C:0318:202A111:3E42670:5A80C633

{
  "id": 8985,
  "owner": {
    "login": "zooo",
    "id": 11991862,
    "avatar_url": "https://avatars2.githubusercontent.com/u/11991862?v=4",
    "gravatar_id": "",
    "url": "https://api.github.com/users/zooo",
    "html_url": "https://github.com/zooo",
    "followers_url": "https://api.github.com/users/zooo/followers",
    "following_url": "https://api.github.com/users/zooo/following{/other_user}",
    "gists_url": "https://api.github.com/users/zooo/gists{/gist_id}",
    "starred_url": "https://api.github.com/users/zooo/starred{/owner}{/repo}",
    "subscriptions_url": "https://api.github.com/users/zooo/subscriptions",
    "organizations_url": "https://api.github.com/users/zooo/orgs",
    "repos_url": "https://api.github.com/users/zooo/repos",
    "events_url": "https://api.github.com/users/zooo/events{/privacy}",
    "received_events_url": "https://api.github.com/users/zooo/received_events",
    "type": "Organization",
    "site_admin": false
  },
  "name": "GitEnter",
  "description": "",
  "external_url": "http://www.gitenter.com",
  "html_url": "https://github.com/apps/gitenter",
  "created_at": "2018-02-10T21:45:40Z",
  "updated_at": "2018-02-10T21:45:40Z"
}
```

For the case of GitHub Apps, the URL `https://api.github.com/app` can be replaced by any end listed in [here](https://developer.github.com/v3/apps/).
