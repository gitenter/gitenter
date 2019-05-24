resource "aws_elasticache_subnet_group" "redis_session" {
  name       = "${local.aws_redis_session}"
  subnet_ids = ["${aws_subnet.public.*.id}"]
}

resource "aws_elasticache_replication_group" "redis_session" {
  replication_group_id          = "${local.aws_redis_session}"
  replication_group_description = "Redis for Spring Session"

  subnet_group_name             = "${aws_elasticache_subnet_group.redis_session.name}"
  security_group_ids            = ["${aws_security_group.redis.id}"]

  node_type                     = "cache.t2.micro"

  # `cluster_mode` disabled: no `cluster_mode` block. Then we need to asign
  # `number_cache_clusters`. 2 means a single shard primary with single read replica.
  #
  # For this case, we need dreplication but not data partitions. So we only want
  # one shard (cluster mode disabled).
  # https://docs.aws.amazon.com/AmazonElastiCache/latest/red-ug/ParameterGroups.Redis.html#ParameterGroups.Redis.5-0-3
  number_cache_clusters         = 2
  automatic_failover_enabled    = true
  # availability_zones            = ["${data.aws_availability_zones.available.names}"]

  port                          = 6379

  engine                        = "redis"
  engine_version                = "5.0.3"
  parameter_group_name          = "default.redis5.0"
}
