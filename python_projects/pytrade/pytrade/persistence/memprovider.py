from pytrade.persistence import DataProvider

class MemoryDataProvider(DataProvider):
    def __init__(self):
        super(MemoryDataProvider, self).__init__()
        self.__cash = None
        self.__shares = {}
        self.__orders = {}

    def loadCash(self):
        return self.__cash

    def loadShares(self):
        return self.__shares

    def loadOrders(self):
        return self.__orders

    def persistCash(self, cash):
        self.__cash = cash

    def persistShares(self, shares):
        self.__shares = shares

    def persistOrders(self, orders):
        self.__orders = orders
