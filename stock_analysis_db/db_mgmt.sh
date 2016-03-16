#!/bin/bash

MYSQL_VERSION="5.7"
DB_CONTAINER_NAME="stock_analysis_db"
DB_DIR="/var/stock_analysis/db"
ROOT_PASSWD="a"

function create_container {
    docker run --name $DB_CONTAINER_NAME -e MYSQL_ROOT_PASSWORD=$ROOT_PASSWD -v $DB_DIR:/var/lib/mysql -d mysql:$MYSQL_VERSION
}

function run_container {
  docker start $DB_CONTAINER_NAME
}

function run_client {
  docker run -it --link $DB_CONTAINER_NAME:mysql --rm mysql:$MYSQL_VERSION sh -c 'exec mysql -h"$MYSQL_PORT_3306_TCP_ADDR" -P"$MYSQL_PORT_3306_TCP_PORT" -uroot -p"$MYSQL_ENV_MYSQL_ROOT_PASSWORD"'
}

function logs_container {
  docker logs $DB_CONTAINER_NAME
}

function parse_command {
  local command=$1
  shift

  case $command in
    create )
      create_container $@
      exit;;
    run )
      run_container $@
      exit;;
    client )
      run_client $@
      exit;;
    logs )
      logs_container $@
      exit;;
    *)
      exit;;
  esac
}

parse_command $@
