FROM java:8
ARG app_jar

EXPOSE 8081
COPY $app_jar /opt/stock_analysis_fe.jar

CMD ["java", "-Xdebug", "-Xrunjdwp:server=y,transport=dt_socket,address=8001,suspend=n", "-jar", "/opt/stock_analysis_fe.jar"]

