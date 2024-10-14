# Database instance variables
variable "database_allocated_storage" {
   type = number
}

variable "database_max_allocated_storage" {
  type = number
}

variable "database_engine_version" {
  type = string
}

variable "database_instance_class" {
  type = string
}

variable "database_name" {
  type = string
}

variable "database_password" {
  type = string
}

variable "database_username" {
  type = string
}

variable "database_parameter_group_name" {
  type = string
}

variable "database_instance_name" {
  type = string
}

# Elastic Redis Cache
variable "redis_cluster_id" {
  type = string
}

variable "redis_node_type" {
  type = string
}

variable "redis_cluster_name" {
  type = string
}

# EC2 variables
variable "deployer_key_name" {
  type = string
}

variable "ami_instance" {
  type = string
}

variable "ami_instance_type" {
  type = string
}

variable "server_instance_name" {
  type = string
}