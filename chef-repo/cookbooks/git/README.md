# git

TODO: Enter the cookbook description here.

## Manual Test

`kitchen converge`

`kitchen login` or `ssh git@192.168.33.8`

`kitchen destroy`

## Automatic (Integration) Test

`kitchen converge` then `kitchen verify` with Serverspec

Or just `kitchen test` for them all.
