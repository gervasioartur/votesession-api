# Create S3 bucket
resource "aws_s3_bucket" "bucket" {
  bucket = var.bucket_name
  aws_s3_bucket_acl = "private"
}

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

# Save SSH private key on S3 bucket
resource "aws_s3_object" "ssh_private_key" {
  bucket = aws_s3_bucket.bucket.bucket
  key    = "ssh-keys/${var.deployer_key_name}-key.pem"
  content = tls_private_key.ssh_key.private_key_pem
  acl    = "private"  
}

# Save SSH public key on S3 bucket
resource "aws_s3_object" "ssh_public_key" {
  bucket = aws_s3_bucket.bucket.bucket
  key    = "ssh-keys/${var.deployer_key_name}-key.pub"
  content = tls_private_key.ssh_key.public_key_openssh
  acl    = "private"  
}

# Associate to EC2
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

 ingress {
    from_port   = 6379
    to_port     = 6379
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

# Outputs to show instance IP and SSH key
output "instance_public_ip" {
  value = aws_instance.docker_instance.public_ip
}

# Outputs for private ssh key
output "ssh_private_key_url" {
  value = "https://${aws_s3_bucket.bucket.bucket}.s3.amazonaws.com/${aws_s3_object.ssh_private_key.key}"
}

# Outputs for public ssh key
output "ssh_public_key" {
  value = "https://${aws_s3_bucket.bucket.bucket}.s3.amazonaws.com/${aws_s3_object.ssh_public_key.key}"
}