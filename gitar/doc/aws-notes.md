# AWS Notes

## Main products and target aims

Amazon EC2

- Run a virtual server
- Host a dynamic website

Amazon S3

- Store files (frequent I/O with speed concern)
- Host a static website

Amazon RDS

- Run an SQL database

Amazon DynamoDB

- Run the DynamoDB NoSQL database

Amazon EMR

- Analysis data in Amazon S3

AWS Elastic Beanstalk

- Deploy a dynamic website

Amazon Glacier

- Store files (archive purposes)

Amazon Redshift

- Data warehouse

## Console and launch services

Amazon provides a web-based console ([console.aws.amazon.com](console.aws.amazon.com)) you can used for various Amazon services. You log in to that page and choose the desired service (say, EC2).

For EC2, then you can either "launch instance" by following their wizard (choose OS, storage, key pair, security port, ...), or choose an instance in the left panel "instances" list. Select a desired instance in the list, and use command lines (typically by SSH using the previous saved `.pem` file of the SSH key) to connect your instance.

Notes:

1. There are multiple virtual machines located in multiple places. You need to remember where your instances are, or they'll tell you no instance in that location (my current ones are in U.S. Oregon).
1. After connection, the terminal exactly likes an normal virtual machine. (For Ubuntu case) then you can either use their pre-installed packages, or use `sudo apt-get install` to set up the environment as you desired.

## Domain name and DNS setup

In case of buying a domain name from GoDaddy.

1. Get Elastic IP.
2. Go to [Route 53](https://console.aws.amazon.com/route53/), create hosted zone and record set.
3. Go to [GoDaddy DNS management](https://dcc.godaddy.com/manage/gitenter.com/dns) and change the nameservers to the ones provided by Route 53.

Mostly by following [this page](http://www.littlebigextra.com/map-domain-name-amazon-aws-ec2-instance/#Map-domain-name-from-GoDaddy-to-Amazon-EC2-instance).
