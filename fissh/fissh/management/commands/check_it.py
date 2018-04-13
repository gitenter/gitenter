from django.core.management.base import BaseCommand
from fissh.models import *

class Command(BaseCommand):
    def handle(self, *args, **kwargs):
        print("Hello, world")
        #member = Member(username='aaa', password='bbb')
        #member.save()
        tmp = Member.objects.get(username='aaa')
        print(tmp.password)

        #org = Organization(name="aka")
        #org.save()
        org = Organization.objects.get(name="aka")
        print(org.name)
        repo = Repository(organization=org, name="fff", is_public=True)
        repo.save()
        for x in org.repository_set.all():
        	print(x.name)