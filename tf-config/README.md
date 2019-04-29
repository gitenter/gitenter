# AWS

AWS account ID: 662490392829

AWS console sign in page: https://gitenter.signin.aws.amazon.com/console

Manually created a user `Administrator` with permissions `AdministratorAccess`. The `access_key` and `secret_key` of this user is used to setup users in `/iam`.

# Terraform

Executed each single folder under `live`

```
terraform init
terraform get -update
terraform plan
terraform apply
terraform destroy
```

`terraform get -update` will (sometimes?) get the following error.

```
Error loading modules: error downloading 'file:///.../gitenter/tf-config/modules/iam-group-terraform-config': destination exists and is not a symlink
```

The alternative solution is to remove `.terraform/modules` so it gets force updated.

If we are later on using Terragrunt [we can `terragrunt apply --terragrunt-source path/to/the/module`](https://github.com/gruntwork-io/terragrunt#working-locally).
