variable "db_name" {
  description = "RDS db name"
  type        = string
  sensitive   = true
}

variable "db_username" {
  description = "RDS user name"
  type        = string
  sensitive   = true
}

variable "db_password" {
  description = "RDS user password"
  type        = string
  sensitive   = true
}