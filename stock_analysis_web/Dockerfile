FROM java:8
ARG app_jar

EXPOSE 8080
COPY $app_jar /opt/stock_analysis_web.jar

CMD ["java", "-jar", "/opt/stock_analysis_web.jar"]