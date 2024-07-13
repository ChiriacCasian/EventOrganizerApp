package commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDate;
import java.util.UUID;

@Entity
public class Transaction {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payer_id")
    @JsonIgnore
    private Participant payer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payee_id")
    @JsonIgnore
    private Participant payee;

    private double amount;

    private String currency;

    private LocalDate dateOfTransaction;

    /**
     * Default constructor for the Transaction class.
     */
    public Transaction() {
    }

    /**
     * Constructor for the Transaction class.
     *
     * @param payer             the participant who paid the amount
     * @param payee             the participant who received the amount
     * @param amount            the amount of the transaction
     * @param currency          the currency of the transaction
     * @param dateOfTransaction the date of the transaction
     */
    public Transaction(Participant payer, Participant payee, double amount,
                       String currency, LocalDate dateOfTransaction) {
        id = UUID.randomUUID().toString();
        this.payer = payer;
        this.payee = payee;
        this.amount = amount;
        this.currency = currency;
        this.dateOfTransaction = dateOfTransaction;
    }

    /**
     * Gets the ID of the transaction.
     *
     * @return the ID of the transaction
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the transaction.
     *
     * @param id the ID of the transaction
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the participant who paid the amount.
     *
     * @return the participant who paid the amount
     */
    public Participant getPayer() {
        return payer;
    }

    /**
     * Sets the participant who paid the amount.
     *
     * @param payer the participant who paid the amount
     */
    public void setPayer(Participant payer) {
        this.payer = payer;
    }

    /**
     * Gets the participant who received the amount.
     *
     * @return the participant who received the amount
     */
    public Participant getPayee() {
        return payee;
    }

    /**
     * Sets the participant who received the amount.
     *
     * @param payee the participant who received the amount
     */
    public void setPayee(Participant payee) {
        this.payee = payee;
    }

    /**
     * Gets the amount of the transaction.
     *
     * @return the amount of the transaction
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets the amount of the transaction.
     *
     * @param amount the amount of the transaction
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Gets the date of the transaction.
     *
     * @return the date of the transaction
     */
    public LocalDate getDateOfTransaction() {
        return dateOfTransaction;
    }


    /**
     * Sets the date of the transaction.
     *
     * @param dateOfTransaction the date of the transaction
     */
    public void setDateOfTransaction(LocalDate dateOfTransaction) {
        this.dateOfTransaction = dateOfTransaction;
    }

    /**
     * Gets the currency of the transaction.
     *
     * @return the currency of the transaction
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the currency of the transaction.
     *
     * @param currency the currency of the transaction
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Checks if two transactions are equal.
     *
     * @param o the object to compare to
     * @return true if the transactions are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    /**
     * Gets the hash code of the transaction.
     *
     * @return the hash code of the transaction
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * Gets the string representation of the transaction.
     *
     * @return the string representation of the transaction
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
