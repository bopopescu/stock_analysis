'''
Created on Jun 15, 2016

@author: gsantiago
'''
import datetime
import math

dbUser='root'
dbPwd='a'
dbHost='172.17.0.2'
dbName='stock_analysis'

def getStockData(stockCode, recDate, conn):        
    cur = conn.cursor()
    query=("select sh.close, sh.high, sh.low, sh.volume from stock_history sh inner join stock s on s.stock_id=sh.stock_id where s.stock_code=%s and sh.date=%s")
    cur.execute(query, (stockCode, recDate))
    return cur.fetchone()

def getActiveModelData(stockCode, recDate, accountId, conn):
    cur = conn.cursor()
    query=('''
        select 
            dme.model_id, dme.entry_size, dme.exit_size, dme.risk_rate
        from 
            donchian_model_entry dme 
            inner join stock s on s.stock_id=dme.stock_id 
            inner join model m on m.model_id=dme.model_id 
        where 
            s.stock_code=%s 
            and m.account_id=%s 
            and m.dat_start<=%s and m.dat_end>=%s
        ''')
    cur.execute(query, (stockCode, accountId, recDate, recDate))
    return cur.fetchone()

def getExitDonchianValue(stockCode, recDate, donchianExitSize, conn):
    query = ('''
        select 
        min(history.lowvalue) 
    from 
        (select 
            sh.low as lowvalue 
        from 
            stock_history sh 
            inner join stock s on sh.stock_id=s.stock_id
        where 
            s.stock_code=%s 
            AND sh.date<%s 
        order by sh.date desc 
        limit %s) as history 
    ''')
    cur = conn.cursor()
    cur.execute(query, (stockCode, recDate, donchianExitSize))
    return cur.fetchone()[0]
        

def shouldBuyStock(stockCode, dateStr, accountId, conn):    
    recDate = datetime.datetime.strptime(dateStr, '%Y-%m-%d')
    
    activeModelData = getActiveModelData(stockCode, recDate, accountId, conn)
    if activeModelData==None:
        conn.close()
        return False
    donchianEntrySize=activeModelData[1]

    stockData = getStockData(stockCode, recDate, conn)
    if stockData==None:
        return False
    closeValue = stockData[0]
    volume = stockData[3]

    query=('''
        select 
            max(history.highvalue) 
        from 
            (select 
                sh.high as highvalue 
            from 
                stock_history sh 
                inner join stock s on sh.stock_id=s.stock_id 
            where 
                s.stock_code=%s 
                AND sh.date<%s 
            order by sh.date desc 
            limit %s) as history''')
    cur=conn.cursor()
    cur.execute(query, (stockCode, recDate, donchianEntrySize))
    donchianValue=cur.fetchone()[0]

    return (volume>=10**6) and (closeValue>donchianValue)


def shouldSellStock(stockCode, dateStr, accountId, conn):
    recDate = datetime.datetime.strptime(dateStr, '%Y-%m-%d')
    
    cur = conn.cursor()
    query=('''
        select 
        s.stock_id, m.model_id
    from 
        operation op
        inner join op_order ord on ord.order_id=op.order_id
        inner join model m on m.model_id=ord.model_id
        inner join stock s on s.stock_id=ord.stock_id
    where 
        m.account_id=%s and ord.type='B'  
        and s.stock_code=%s and
        not exists (
            select 1 
            from operation op2
            inner join op_order ord2 on ord2.order_id=op2.order_id
            inner join model m2 on m2.model_id=ord2.model_id
            where m2.account_id=m.account_id
            and ord2.type='S' 
            and ord2.stock_id=s.stock_id
            and op2.dat_creation>op.dat_creation) 
    limit 1
    ''')
    cur.execute(query, (accountId, stockCode))
    (stockId, modelId) = cur.fetchone()
    
    query = ("select exit_size from donchian_model_entry dme where dme.stock_id=%s and dme.model_id=%s")
    cur.execute(query, (stockId, modelId))
    donchianExitSize = cur.fetchone()[0]
    
    stockData = getStockData(stockCode, recDate, conn)
    if stockData==None:
        return False
    closeValue = stockData[0]
    
    donchianValue=getExitDonchianValue(stockCode, recDate, donchianExitSize, conn)
    
    return closeValue<=donchianValue

def calculateStopLossPoint(stockCode, dateStr, accountId, conn):
    recDate = datetime.datetime.strptime(dateStr, '%Y-%m-%d')
    
    activeModelData = getActiveModelData(stockCode, recDate, accountId, conn)
    donchianExitSize=activeModelData[2]
    
    return float(getExitDonchianValue(stockCode, recDate, donchianExitSize, conn))

def calculatePositionSize(stockCode, dateStr, accountId, availableMoney, openPositionsValue, conn):
    recDate = datetime.datetime.strptime(dateStr, '%Y-%m-%d')
    
    
    stopLossPoint = calculateStopLossPoint(stockCode, dateStr, accountId, conn)
    
    stockData = getStockData(stockCode, recDate, conn)
    closeValue = float(stockData[0])
    
    activeModelData = getActiveModelData(stockCode, recDate, accountId, conn)
    riskRate=float(activeModelData[3])
    
    size = math.floor( ( (availableMoney + openPositionsValue) * riskRate ) / (closeValue - stopLossPoint) )
    while ((size * closeValue) > availableMoney) and (size > 1):
        size -=1 
    
    return size
    
