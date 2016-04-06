FROM httpd:2.4
ARG web_content
ARG httpd_conf_file
ARG web_app_vhosts_file

EXPOSE 80
#COPY $web_content /usr/local/apache2/htdocs/
COPY $httpd_conf_file /usr/local/apache2/conf/httpd.conf
COPY $web_app_vhosts_file /usr/local/apache2/conf/extra/vhosts/stock_analysis_web.conf
