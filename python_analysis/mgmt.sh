#!/bin/bash
APP_CONTAINER_NAME="stock_analysis_service"

function build_image {
  docker build -t $APP_CONTAINER_NAME --build-arg pythonProject="stockanalysis" --build-arg pythonCommand="/opt/python_stock_analysis/donchian/webserver.py" .
}

function run_container {
  docker run --rm --name $APP_CONTAINER_NAME  -p 8082:8082 --link stock_analysis_db $APP_CONTAINER_NAME
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
