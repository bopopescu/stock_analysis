package preti.stock.db.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class AccountDBEntity implements Serializable {
    private long id;
    private double balance;
    private double initialPosition;

    public AccountDBEntity() {

    }

    public AccountDBEntity(long id, double balance, double initialPosition) {
        super();
        this.id = id;
        this.balance = balance;
        this.initialPosition = initialPosition;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getInitialPosition() {
        return initialPosition;
    }

    public void setInitialPosition(double initialPosition) {
        this.initialPosition = initialPosition;
    }

}
