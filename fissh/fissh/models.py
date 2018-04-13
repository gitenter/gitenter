from django.db import models

class Member(models.Model):

	username = models.TextField()
	password = models.TextField()
	display_name = models.TextField()
	email = models.TextField()

	class Meta:
		db_table = 'config\".\"member'


class Organization(models.Model):

	name = models.TextField()
	display_name = models.TextField()

	class Meta:
		db_table = 'config\".\"organization'


class Repository(models.Model):

	organization = models.ForeignKey(Organization, on_delete=models.CASCADE)
	name = models.TextField()
	display_name = models.TextField()
	description = models.TextField()
	
	is_public = models.BooleanField()

	class Meta:
		db_table = 'config\".\"repository'


class RepositoryMemberMap:

	repository = models.ForeignKey(Repository, on_delete=models.CASCADE)
	member = models.ForeignKey(Member, on_delete=models.CASCADE)

	role = models.CharField(max_length=1)

	class Meta:
		db_table = 'config\".\"repository_member_map'