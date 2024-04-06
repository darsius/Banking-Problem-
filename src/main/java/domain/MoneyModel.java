package domain;

public class MoneyModel {
    private double amount;
    private CurrencyType currency;

    public MoneyModel(double amount, CurrencyType currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public CurrencyType getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyType currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "MoneyModel{" +
                "amount=" + amount +
                ", currency=" + currency +
                '}';
    }
}