package preti.stock.client.model;

import java.io.Serializable;
import java.util.Collection;

@SuppressWarnings("serial")
public class Account implements Serializable {

    private long id;
    private double balance;
    private double initialPosition;
    private Collection<Wallet> wallet;

    public Account() {

    }

    public Account(long id, double balance, double initialPosition, Collection<Wallet> wallet) {
        super();
        this.id = id;
        this.balance = balance;
        this.initialPosition = initialPosition;
        this.wallet = wallet;
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

    public Collection<Wallet> getWallet() {
        return wallet;
    }

    public void setWallet(Collection<Wallet> wallet) {
        this.wallet = wallet;
    }

}
