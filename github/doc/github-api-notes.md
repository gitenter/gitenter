# GitHub API Notes

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
