#!/usr/bin/env bash

NEW_HOST=$1
find . -name *.py | xargs sed -i "" -e "s/^MQTT_HOST.*=.*$/MQTT_HOST = \"$NEW_HOST\"/g"
