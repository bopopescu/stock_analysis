#!/bin/bash
APP_CONTAINER_NAME="stock_analysis_api"
STOCKS_FILE="/var/stock_analysis/cotacoes.txt"

function build_image {
  docker build -t $APP_CONTAINER_NAME --build-arg app_jar="build/libs/stock_analysis_api-1.0-SNAPSHOT.jar" .
}

function run_container {
  docker rm $APP_CONTAINER_NAME
  docker run --name $APP_CONTAINER_NAME  -v $STOCKS_FILE:/var/stock_analysis/cotacoes.txt -p 8080:8080 -p 8000:8000  --link stock_analysis_db stock_analysis_api
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
