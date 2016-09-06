# -*- coding: utf-8 -*-
    
import re
import pandas as pd
from datetime import datetime


#############################################################
###LOAD DATA FROM FILE
#############################################################
initial_date = datetime.strptime('20140101', '%Y%m%d')
regex = re.compile('\d\d(?P<strdate>\d{8})..(?P<code>.{12})(?P<type>.{3})(?P<name>.{12}).{17}(?P<open>\d{13})(?P<high>\d{13})(?P<low>\d{13}).{13}(?P<close>\d{13}).{49}(?P<volume>\d{18})')
stocks_file='/git_repos/github/stock_analysis/python_projects/pytrade/cotacoes.txt'

all_stocks = {}
with open(stocks_file) as f:
    for line in f:
        if(line.strip()):
            result = regex.match(line)
            date = datetime.strptime(result.group('strdate').strip(), '%Y%m%d')
            code = result.group('code').strip()
            open_value = int(result.group('open'))/100
            high_value = int(result.group('high'))/100
            low_value = int(result.group('low'))/100
            close_value = int(result.group('close'))/100
            volume = long(result.group('volume'))/100
            
            if(date<initial_date):
                continue      
            
            stock_data = all_stocks.get(code, [])
            stock_data.append(
                        {'date': date,
                         'code': code,
                         'open': open_value,
                         'high': high_value,
                         'low': low_value,
                         'close': close_value,
                         'volume': volume
                        }
            )
            all_stocks[code] = stock_data

for code in all_stocks.keys():
    stock_data = all_stocks[code]
    frame = pd.DataFrame(stock_data, columns=['date', 'code', 'open', 'high', 'low', 'close', 'volume'])
    frame.index = frame.date
    all_stocks[code] = frame

            
            