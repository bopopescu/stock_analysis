#!/bin/bash
WEB_CONTAINER_NAME="stock_analysis_fe_html"

#FIXME: pq eu preciso colocar o path absoluto aqui?
WEB_CONTENT_DIR="$(pwd)/WebContent/"

HTTPD_CONF_FILE="apache/httpd.conf"
STOCK_ANALYSIS_WEB_CONF="apache/stock_analysis_web.conf"

function build_image {
  docker build -t $WEB_CONTAINER_NAME --build-arg web_content="$WEB_CONTENT_DIR" --build-arg httpd_conf_file="$HTTPD_CONF_FILE" --build-arg web_app_vhosts_file="$STOCK_ANALYSIS_WEB_CONF"  .
}

function run_container_dev_env {
  echo $WEB_CONTENT_DIR
  rm /usr/local/apache2/htdocs
  ln -s $WEB_CONTENT_DIR /usr/local/apache2/htdocs
  docker rm $WEB_CONTAINER_NAME

  docker run --name $WEB_CONTAINER_NAME -v /usr/local/apache2/htdocs/:/usr/local/apache2/htdocs/ -p 80:80 --link stock_analysis_web $WEB_CONTAINER_NAME
}

function parse_command {
  local command=$1
  shift

  case $command in
    build )
      build_image $@
      exit;;
    run )
      run_container_dev_env $@
      exit;;
    *)
      exit;;
  esac
}

parse_command $@
