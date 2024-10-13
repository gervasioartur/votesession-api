# # Create database instance
# resource "aws_db_instance" "postgres" {
#   allocated_storage = var.database_allocated_storage
#   max_allocated_storage = var.database_max_allocated_storage
#   engine = "postgres"
#   engine_version = var.database_engine_version
#   instance_class = var.database_instance_class
#   db_name = var.database_name
#   username = var.database_username
#   password = var.database_password
#   parameter_group_name = var.database_parameter_group_name
#   publicly_accessible = true
#   skip_final_snapshot = true
#
#   tags = {
#     Name = var.database_instance_name
#   }
# }
#
# # Create ElasticCache Redis
# resource "aws_elasticache_cluster" "redis" {
#   cluster_id = var.redis_cluster_id
#   engine = "redis"
#   node_type =  var.redis_node_type
#   num_cache_nodes = 1
#   parameter_group_name = "default.redis6.x"
#
#   tags = {
#     Name = var.redis_cluster_name
#   }
# }
#
# # Outputs of postgres database
# output "db_endpoint" {
#   value = aws_db_instance.postgres.endpoint
# }
#
# # Output of Redis cluster
# output "redis_endpoint" {
#   value = aws_elasticache_cluster.redis.configuration_endpoint
# }