package preti.stock.system;

public interface Recomendation<T> {

    double getSize();

    double getValue();

    T getTarget();
}
