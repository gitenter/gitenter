from django.core.management.base import BaseCommand
from orm.models import *

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
        #repo = Repository(organization=org, name="ggg", is_public=True)
        #repo.save()
        for x in org.repository_set.all():
        	print(x.name)

        z = Repository.objects.get(name="fff", organization__name="aka")
        print(z.name)

        zz = Repository.objects.get_by_name_and_organization_name("fff", "aka")
        print(zz.name)