# Security group to allow access to RDS
resource "aws_security_group" "rds_sg" {
  name = "rds_sg"

  ingress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "rds_sg"
  }
}

# Create database instance
resource "aws_db_instance" "postgres" {
  identifier = var.database_instance_name
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
  vpc_security_group_ids   = [aws_security_group.rds_sg.id]

  tags = {
    Name = var.database_instance_name
  }
}

# Create SSH key in order to access the EC2 instance
resource "tls_private_key" "ssh_key" {
  algorithm = "RSA"
  rsa_bits = 4096
}

resource "aws_key_pair" "deployer" {
  key_name = var.deployer_key_name
  public_key = tls_private_key.ssh_key.public_key_openssh
}

# Security group to allow SSH and HTTP access on EC2 instance
resource "aws_security_group" "allow_ssh_http" {
  name_prefix = "allow_ssh_http"
  ingress {
    from_port = 22
    to_port   = 22
    protocol  = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# Create EC2 instance
resource "aws_instance" "docker_instance" {
  ami = var.ami_instance
  instance_type = var.ami_instance_type
  key_name = aws_key_pair.deployer.key_name
  # Installing docker and run container
  user_data = <<-EOF
              #!/bin/bash
              yum update -y
              amazon-linux-extras install docker -y
              service docker start
              usermod -a -G docker ec2-user
              docker ps
              EOF

  # Associate a security group
  vpc_security_group_ids = [aws_security_group.allow_ssh_http.id]

  tags = {
    Name = var.server_instance_name
  }
}

# Outputs of postgres database
output "db_endpoint" {
  value = aws_db_instance.postgres.endpoint
}

# Output of Redis cluster
output "redis_endpoint" {
  value = "${aws_elasticache_cluster.redis.cache_nodes[0].address}:${aws_elasticache_cluster.redis.port}"
}

# Outputs to show instance IP and SSH key
output "instance_public_ip" {
  value = aws_instance.docker_instance.public_ip
}

output "ssh_private_key_pem" {
  value     = tls_private_key.ssh_key.private_key_pem
  sensitive = true
}