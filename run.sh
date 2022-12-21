#!/bin/bash

echo 'insert root dir of project'
read root_dir

cd $root_dir/target

java -jar task-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
read
exit 0