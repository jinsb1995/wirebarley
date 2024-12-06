#!/bin/bash

while ! mysqladmin ping -h "localhost" --slient; do
  sleep 1
done

mysql -u root -p "$MYSQL_PASSWORD" < /docker/init.sql