#!/bin/bash

echo "export SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE" >> /etc/profile
exec "$@"
