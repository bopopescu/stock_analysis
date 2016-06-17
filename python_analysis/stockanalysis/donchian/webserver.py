'''
Created on Jun 15, 2016

@author: gsantiago
'''
from http.server import BaseHTTPRequestHandler, HTTPServer
import json
import urllib as urllib
from urllib.parse import urlparse
import tradingstrategy as trading
import mysql.connector

class SAStrategy(BaseHTTPRequestHandler):        
    def do_GET(self):       
        dbUser='root'
        dbPwd='a'
        dbHost='stock_analysis_db' 
        dbName='stock_analysis'
        conn = mysql.connector.connect(user=dbUser, password=dbPwd, host=dbHost, database=dbName)
    
        url = urlparse(self.path)
        par=urllib.parse.parse_qs(url.query)
        stockCode=par['stock'][0]
        dateStr=par['date'][0]
        accountId=par['account'][0]
            
        if url.path=='/shouldBuyStock':      
            result = trading.shouldBuyStock(stockCode, dateStr, accountId, conn)
        elif url.path=='/shouldSellStock':
            result = trading.shouldSellStock(stockCode, dateStr, accountId, conn)
        elif url.path=='/calculateStopLossPoint':
            result = trading.calculateStopLossPoint(stockCode, dateStr, accountId, conn)
        elif url.path=='/calculatePositionSize':
            balance=par['balance'][0]
            openPosValue=par['openPosValue'][0]
            result = trading.calculatePositionSize(stockCode, dateStr, accountId, float(balance), float(openPosValue), conn)
        
        conn.close()
            
        self.send_response(200)
        self.send_header('Content-type', 'application/json')
        self.end_headers()
        self.wfile.write(bytes(json.dumps(result), 'UTF-8'))

        
def run(server_class=HTTPServer, handler_class=SAStrategy, port=80):
    server_address = ('', port)
    httpd = server_class(server_address, handler_class)
    print('Starting httpd...')
    httpd.serve_forever()

if __name__ == "__main__":
    from sys import argv

    if len(argv) == 2:
        run(port=int(argv[1]))
    else:
        run()
