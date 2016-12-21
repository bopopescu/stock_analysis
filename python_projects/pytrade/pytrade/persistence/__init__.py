import abc

class DataProvider(object):
    def __init__(self):
        pass

    @abc.abstractmethod
    def loadCash(self):
        raise NotImplementedError()

    @abc.abstractmethod
    def loadShares(self):
        raise NotImplementedError()

    @abc.abstractmethod
    def loadOrders(self):
        raise NotImplementedError()

    @abc.abstractmethod
    def persistCash(self, cash):
        raise NotImplementedError()

    @abc.abstractmethod
    def persistShares(self, shares):
        raise NotImplementedError()

    @abc.abstractmethod
    def persistOrders(self, orders):
        raise NotImplementedError()
