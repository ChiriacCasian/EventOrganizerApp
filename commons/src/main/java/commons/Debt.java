package commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDate;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

public class Debt {
    private Participant payer;

    private Participant payee;
    private double total;
    private String currency;

    /**
     * Constructor for the debt class
     *
     * @param payer    the participant who owes the debt
     * @param payee    the participant to whom the debt is owed
     * @param total    the total amount of the debt
     * @param currency the currency of the debt
     */
    public Debt(Participant payer, Participant payee, double total, String currency) {
        this.payer = payer;
        this.payee = payee;
        this.total = total;
        this.currency = currency;
    }

    /**
     * Default constructor
     */
    public Debt() {

    }

    /**
     * Retrieves the participant who owes the debt.
     *
     * @return the participant who owes the debt
     */
    public Participant getPayer() {
        return payer;
    }

    /**
     * Sets the participant who owes the debt.
     *
     * @param payer the participant who owes the debt
     */
    public void setPayer(Participant payer) {
        this.payer = payer;
    }

    /**
     * Retrieves the participant to whom the debt is owed (the payee).
     *
     * @return the participant to whom the debt is owed (the payee)
     */
    public Participant getPayee() {
        return payee;
    }

    /**
     * Sets the participant to whom the debt is owed (the payee).
     *
     * @param payee the participant to whom the debt is owed (the payee)
     */
    public void setPayee(Participant payee) {
        this.payee = payee;
    }

    /**
     * Retrieves the total amount of the debt.
     *
     * @return the total amount of the debt
     */
    public double getTotal() {
        return total;
    }

    /**
     * Sets the total amount of the debt.
     *
     * @param total the total amount of the debt
     */
    public void setTotal(double total) {
        this.total = total;
    }

    /**
     * Retrieves the currency of the debt.
     *
     * @return the currency of the debt.
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the currency of the debt.
     *
     * @param currency the new currency of the debt.
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Equals method to check debts
     *
     * @param o other object
     * @return true is equals false otherwise
     */
    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    /**
     * Creates a unique hashcode for an debt
     *
     * @return a unique hashcode
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * Provides info on who owes whom how much money.
     *
     * @param language the language of the text
     * @return a textual representation of the necessary payment.
     */
    public String basicInfo(String language) {
        return switch (language) {
            case "de" -> payer.getName() + " muss " + payee.getName() + " " +
                    String.format("%.2f", total) + " " + currency + " zahlen.";
            case "nl" -> payer.getName() + " moet " + payee.getName() + " " +
                    String.format("%.2f", total) + " " + currency + " betalen.";
            case "bg" -> payer.getName() + " \u0442\u0440\u044f\u0431\u0432\u0430" +
                    "\u0020\u0434\u0430\u0020\u043f\u043b\u0430\u0442\u0438" +
                    "\u0020\u043d\u0430 " + payee.getName() + " " +
                    String.format("%.2f", total) + " " + currency + ".";
            default -> payer.getName() + " must pay " + payee.getName() + " " +
                    String.format("%.2f", total) + " " + currency + ".";
        };
    }

    /**
     * Provides detailed payment instructions.
     *
     * @param language the language of the text
     * @return a textual representation of the necessary payment.
     */
    @SuppressWarnings("checkstyle:MethodLength")
    public String paymentInstructions(String language) {
        if (!isNullOrEmpty(payee.getIban()) && !isNullOrEmpty(payee.getBic())) {
            return switch (language) {
                case "de" -> "Bankinformationen verf\u00fcgbar, \u00fcberweisen Sie das Geld an:" +
                        "\nKontoinhaber: " + payee.getName() +
                        "\nIBAN: " + payee.getIban() +
                        "\nBIC: " + payee.getBic() +
                        "\nBetrag: " + String.format("%.2f", total) + " " + currency;
                case "nl" -> "Bankgegevens beschikbaar, maak het geld over naar:" +
                        "\nRekeninghouder: " + payee.getName() +
                        "\nIBAN: " + payee.getIban() +
                        "\nBIC: " + payee.getBic() +
                        "\nBedrag: " + String.format("%.2f", total) + " " + currency;
                case "bg" -> "\u0411\u0430\u043d\u043a\u043e\u0432\u0430\u0020\u0438\u043d" +
                        "\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u044f" +
                        "\u0020\u0434\u043e\u0441\u0442\u044a\u043f\u043d\u0430," +
                        "\u0020\u043f\u0440\u0435\u043d\u0435\u0441\u0435\u0442\u0435" +
                        "\u0020\u043f\u0430\u0440\u0438\u0442\u0435\u0020\u043d\u0430" +
                        "\u0020" + payee.getName() +
                        "\u0020\u043d\u0430\u0020\u0441\u043b\u0435\u0434\u043d\u0438\u044f" +
                        "\u0020\u0438\u0431\u0430\u043d\u0020\u0438" +
                        "\u0020\u0431\u0438\u043a:" +
                        "\u0020\u0418\u043c\u0435\u0020\u043d\u0430\u0020\u0441\u0447" +
                        "\u0435\u0442\u0430:" +
                        "\u0020" + payee.getName() +
                        "\u0020\u0418\u0411\u0410\u041d:" +
                        "\u0020" + payee.getIban() +
                        "\u0020\u0411\u0418\u041a:" +
                        "\u0020" + payee.getBic() +
                        "\u0020\u0421\u0443\u043c\u0430:" +
                        "\u0020" + String.format("%.2f", total) + " " + currency;
                default -> "Bank information available, transfer the money to:" +
                        "\nAccount holder: " + payee.getName() +
                        "\nIBAN: " + payee.getIban() +
                        "\nBIC: " + payee.getBic() +
                        "\nAmount: " + String.format("%.2f", total) + " " + currency;
            };
        } else {
            return switch (language) {
                case "de" -> "Bankinformationen nicht verf\u00fcgbar, bitte kontaktieren Sie "
                        + payee.getName() + " f\u00fcr weitere Informationen.";
                case "nl" -> "Bankgegevens niet beschikbaar, neem contact op met "
                        + payee.getName() + " voor meer informatie.";
                case "bg" -> "\u0411\u0430\u043d\u043a\u043e\u0432\u0430\u0020\u0438\u043d" +
                        "\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u044f" +
                        "\u0020\u043d\u0435\u0020\u0435\u0020\u0434\u043e" +
                        "\u0441\u0442\u044a\u043f\u043d\u0430," +
                        "\u0020\u043c\u043e\u043b\u044f\u0020\u0441\u0435\u0020" +
                        "\u0441\u0432\u044a\u0440\u0436\u0435\u0442\u0435" +
                        "\u0020\u0441\u0020" + payee.getName() + "\u0020\u0437\u0430" +
                        "\u0020\u0434\u043e\u043f\u044a\u043b\u043d\u0438" +
                        "\u0020\u0438\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u0438.";
                default -> payee.getName() + " chose not to provide bank information.";
            };
        }
    }

    /**
     * Creates a transaction object
     *
     * @param amount the amount of the transaction
     * @return the transaction object
     */
    public Transaction createTransaction(double amount) {
        return new Transaction(payer, payee, amount, currency, LocalDate.now());
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * toString method
     *
     * @return text representation of the object
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}





