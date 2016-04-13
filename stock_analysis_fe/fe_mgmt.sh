#!/bin/bash
APP_CONTAINER_NAME="stock_analysis_fe"

function build_image {
  docker build -t $APP_CONTAINER_NAME --build-arg app_jar="target/stock_analysis_fe-1.0-SNAPSHOT.jar" .
}

function run_container {
  docker rm $APP_CONTAINER_NAME
  docker run --name $APP_CONTAINER_NAME  -p 8081:8081 -p 8001:8001  --link stock_analysis_api stock_analysis_fe
}

function parse_command {
  local command=$1
  shift

  case $command in
    build )
      build_image $@
      exit;;
    run )
      run_container $@
      exit;;
    *)
      exit;;
  esac
}

parse_command $@
