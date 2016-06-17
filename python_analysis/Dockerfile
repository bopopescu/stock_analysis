FROM python:3.4
ARG pythonProject
ARG pythonCommand

ADD modules/mysql-connector-python-2.0.4 /tmp/mysql-connector
WORKDIR /tmp/mysql-connector
RUN python setup.py install

EXPOSE 8082
ADD stockanalysis /opt/python_stock_analysis

CMD ["python", "/opt/python_stock_analysis/donchian/webserver.py", "8082"]
