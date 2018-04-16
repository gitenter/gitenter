from django.db import models

class RepositoryManager(models.Manager):

	def get_by_name_and_organization_name(self, name, organization_name):
		return super().get(name=name, organization__name=organization_name)