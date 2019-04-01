variable "access_key" {}
variable "secret_key" {}

variable "server_port" {
  description = "The port the server will use for HTTP requests"
  default = 8080
}
