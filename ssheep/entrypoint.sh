#!/bin/bash

# echo "export SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE" >> /etc/profile

# echo "Host *
#   SetEnv SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE" >> /home/git/.ssh/config

echo "SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE" >> /home/git/.ssh/environment

exec "$@"
