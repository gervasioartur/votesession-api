# # Create SSH key in order to access the EC2 instance
# resource "tls_private_key" "ssh_key" {
#   algorithm = "RSA"
#   rsa_bits = 4096
# }
#
# resource "aws_key_pair" "deployer" {
#   key_name = var.deployer_key_name
#   public_key = tls_private_key.ssh_key.public_key_openssh
# }
#
# # Security group to allow SSH and HTTP access
# resource "aws_security_group" "allow_ssh_http" {
#   name_prefix = "allow_ssh_http"
#   ingress {
#     from_port = 22
#     to_port   = 22
#     protocol  = "tcp"
#     cidr_blocks = ["0.0.0.0/0"]
#   }
#
#   ingress {
#     from_port   = 80
#     to_port     = 80
#     protocol    = "tcp"
#     cidr_blocks = ["0.0.0.0/0"]
#   }
#
#   egress {
#     from_port   = 0
#     to_port     = 0
#     protocol    = "-1"
#     cidr_blocks = ["0.0.0.0/0"]
#   }
# }
#
# # Create EC2 instance
# resource "aws_instance" "docker_instance" {
#   ami = var.ami_instance
#   instance_type = var.ami_instance_type
#   key_name = aws_key_pair.deployer.key_name
# # Installing docker and run container
#   user_data = <<-EOF
#               #!/bin/bash
#               yum update -y
#               amazon-linux-extras install docker -y
#               service docker start
#               usermod -a -G docker ec2-user
#
#               # Docker hub login
#               echo "${var.docker_password}" | docker login -u ${var.docker_username} --password-stdin
#
#               # Pull docker image
#               docker pull ${var.docker_username}/${var.image_name}:${var.image_tag}
#
#               # Run container
#               docker run -d -p 80:80 ${var.docker_username}/${var.image_name}:${var.image_tag}
#
#               docker run -d -p 80:80 <seu_nome_da_imagem_docker>  # Altere com sua imagem
#               EOF
#
#   # Associate a security group
#   vpc_security_group_ids = [aws_security_group.allow_ssh_http.id]
#
#   tags = {
#     Name = var.server_instance_name
#   }
# }
#
# # Outputs to show instance IP and SSH key
# output "instance_public_ip" {
#   value = aws_instance.docker_instance.public_ip
# }
#
# output "ssh_private_key_pem" {
#   value     = tls_private_key.ssh_key.private_key_pem
#   sensitive = true
# }

# Create database instance
resource "aws_db_instance" "postgres" {
  allocated_storage = var.database_allocated_storage
  max_allocated_storage = var.database_max_allocated_storage
  engine = "postgres"
  engine_version = var.database_engine_version
  instance_class = var.database_instance_class
  db_name = var.database_name
  username = var.database_username
  password = var.database_password
  parameter_group_name = var.database_parameter_group_name
  publicly_accessible = true
  skip_final_snapshot = true

  tags = {
    Name = var.database_instance_name
  }
}

# Create ElasticCache Redis
resource "aws_elasticache_cluster" "redis" {
  cluster_id = var.redis_cluster_id
  engine = "redis"
  node_type =  var.redis_node_type
  num_cache_nodes = 1
  parameter_group_name = "default.redis6.x"

  tags = {
    Name = var.redis_cluster_name
  }
}

# Outputs of postgres database
output "db_endpoint" {
  value = aws_db_instance.postgres.endpoint
}

# Output of Redis cluster
output "redis_endpoint" {
  value = aws_elasticache_cluster.redis.configuration_endpoint
}