package preti.stock.db.model;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class OperationDBEntity implements Serializable {

    public OperationDBEntity() {
        
    }
    
    private long operationId;
    private long orderId;
    private long walletId;
    private Date creationDate;
    private OperationType type;
    private double size;
    private double value;
    public long getOperationId() {
        return operationId;
    }
    public void setOperationId(long operationId) {
        this.operationId = operationId;
    }
    public long getOrderId() {
        return orderId;
    }
    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }
    public long getWalletId() {
        return walletId;
    }
    public void setWalletId(long walletId) {
        this.walletId = walletId;
    }
    public Date getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    public OperationType getType() {
        return type;
    }
    public void setType(OperationType type) {
        this.type = type;
    }
    public double getSize() {
        return size;
    }
    public void setSize(double size) {
        this.size = size;
    }
    public double getValue() {
        return value;
    }
    public void setValue(double value) {
        this.value = value;
    }
    
    
    

}
