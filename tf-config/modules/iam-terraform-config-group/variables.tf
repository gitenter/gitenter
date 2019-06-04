# The reason to have multiple group, is because each group can only
# attach at most 10 policies.

locals {
  stateless_group_name = "stateless-config-group"
  storage_group_name = "storage-config-group"
  accessary_group_name = "accessary-config-group"
}
