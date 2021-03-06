# GitHub API Notes

I created a sandbox user `enterovirus-sandbox` to install my OAuth and GitHub Apps.

---

GitHub has two active APIs, [v3](https://developer.github.com/v3/) (REST) and [v4](https://developer.github.com/) (GraphGL). Currently only API v3 is fully integrated with GitHub Apps.

My App can integrate with GitHub in 3 different ways:

1. Using personal access token
    + Act as me
1. Using OAuth Apps
    + Act as an authenticated GitHub user (not me)
        + User can "sign up with GitHub"
            + Travis CI
1. Using GitHub Apps
    + Act on a single repository user selected

## OAuth Apps

List of parameters in [here](https://developer.github.com/apps/building-oauth-apps/authorization-options-for-oauth-apps/#web-application-flow). `client_id` and `redirect_uri` are by common protocol. Other ones are GitHub specific.

`client_id`: known from `https://github.com/organizations/{orgName}/settings/applications/{appId}`. Note: while GitHub Apps have public link, OAuth Apps don't. So they can share the same name.

`redirect_uri` *(Seems not useful to be specified in here?? GitHub OAuth Apps need a "Authorization callback URL" and it seems just redirect to there.)*

`scope`: List of scopes in [here](https://developer.github.com/apps/building-oauth-apps/scopes-for-oauth-apps/). Currently chosen `read:user` and `user:email` only.

`state`: State is currently fake. The CSRF one should be generated later by, e.g., Spring Security.

`allow_signup`: For GitHub user development. Not quite of my business.

[Common protocol](https://www.tutorialspoint.com/oauth2.0/oauth2.0_obtaining_an_access_token.htm) should also have one called `response_type` (value `code` or `token`). But it is not in GitHub's document

```
https://github.com/login/oauth/authorize?client_id=bfaf1ea99a9b7d4d10e0&redirect_uri=http%3A%2F%2Flocalhost&response_type=code&scope=read%3Auser%20user%3Aemail&state=012345
```

*(TODO: The current setup is incorrect. If users haven't login GitHub, it can indeed redirect him/her to a GitHub signup page. But if they've already logged in, it simply redirect to the "Authorization callback URL" without ask for authorization!!??)*

## GitHub Apps

### Public/Private

Setup when creating the app.

+ Public = user install it from a public link of the App page `https://github.com/apps/{appName}`
+ Private = only the owner herself can install it

### Permissions

GitHub Apps can be set up a list of fine-grained permissions, from the link `https://github.com/organizations/{orgName}/settings/apps/{appName}/permissions`. For each item, you can choose access level in the list of

+ No access
+ Read-only
+ Read and write

The permissions can be changed later.

The permissions are shown to the user when they try to install your app. The user can decide yes/no to install it based on your permission list. They may also choose which repository/repositories they want to install this app. The user may *review* the permission list, change the repositories involved, or decide to leave. *(Will the user get a writing notice on any change of the permission level?)*

### Authentication

There are two level of authentications:

1. Authenticating as a GitHub App
2. Authenticating as an installation

#### Comparison of authentication methods

##### Scope

+ A GitHub App: authenticates as a whole, using the **issue number** (i.e., the ID found in the [app setting page](https://github.com/organizations/zooo/settings/apps/gitenter)).
+ An installation: authenticates as an installation some specific user/organization make, using the **installation ID**.
    + After you install the App [for yourself](https://github.com/apps/gitenter) or [for the organizations your managed](https://github.com/organizations/zooo/settings/apps/gitenter/installations)
        + Your installations (appliations) can be checked from [here](https://github.com/settings/installations)
        + There seems no standard way to get installation ID (!!??). One method is after query the [list of all installations]((https://github.com/settings/installations)), click the one you need and get the ID from the URL, e.g., `https://github.com/settings/installations/87252`

So each GitHub App has several installations by different users.

##### Token

+ A GitHub App: a [Json web token (JWT)](https://jwt.io/introduction/).
+ An installation: a github generated token (shorter)

##### Token query method

+ A GitHub App: generate using the private key (`.pem` file downloaded from GitHub web UI) and some JWT package.
    + Input
        + Private key
        + Issue number/app ID
+ An installation: generate using a cURL query to the GitHub api
    + Input
        + Privious generated JWT token
        + Installation ID

##### Perform

+ A GitHub App: act on behalf of the application.
    + Using JWT token to identify which application it is.
+ An installation: act on behalf of the user.
    + Using installation token to identify which user it is.

##### Expiration time

+ A GitHub App: User setup in the JWT generating script.
+ An installation: Always one hour.

##### Usage

API queries using either token are similar. The difference is

+ To include different `Authorization` values in the cURL customer header: `Authorization: Bearer YOUR_JWT` for GitHub App, while `Authorization: token YOUR_INSTALLATION_ACCESS_TOKEN` for installation.
+ The API endpoints are different set:
    + A GitHub App in [here](https://developer.github.com/v3/apps/available-endpoints/)
    + An installation in [here](https://developer.github.com/v3/apps/installations/)

```
$ curl -H "Authorization: Bearer $(ruby jwt-generator.rb)" -H "Accept: application/vnd.github.machine-man-preview+json" https://api.github.com/app
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

```
$ curl -X POST -H "Authorization: Bearer $(ruby jwt-generator.rb)" -H "Accept: application/vnd.github.machine-man-preview+json" https://api.github.com/installations/87252/access_tokens
{
  "token": "v1.a4f68dadf6e1e1a0127b73573abe4de99efd7bcf",
  "expires_at": "2018-02-12T19:10:30Z"
}
$ curl -H "Authorization: token v1.a4f68dadf6e1e1a0127b73573abe4de99efd7bcf" -H "Accept: application/vnd.github.machine-man-preview+json" https://api.github.com/installation/repositories
{
  ...
}
```

Also, by using the installation token we may have git access using HTTP method. There's no corresponding thing for the JWT token.

```
git clone https://x-access-token:<token>@github.com/owner/repo.git
```

## References

1. GitHub [Building Apps](https://developer.github.com/apps/): Mostly references about authorization, and basic instruction.
1. GitHub [API v3](https://developer.github.com/v3/): Mostly a dictionary of various queries to make.
