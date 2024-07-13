package commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Participant implements Identifiable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    private String email;

    private String iban;

    private String bic;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "invite_code", nullable = false)
    @JsonIgnore
    private Event event;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "payer", cascade = CascadeType.REMOVE)
    private List<Expense> expensesPaid;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "participants")
    private List<Expense> expensesInvolved;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "payer", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Transaction> transactionsFrom;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "payee", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Transaction> transactionsTo;

    /**
     * participant constructor when user opts out of providing banking information
     *
     * @param name  the name of the Participant
     * @param email the name of the Participant
     * @param event the event the Participant is part of
     */
    public Participant(String name, String email, Event event) {
        this.email = email;
        this.name = name;
        this.event = event;
        this.expensesPaid = new ArrayList<>();
        this.expensesInvolved = new ArrayList<>();
        this.transactionsFrom = new ArrayList<>();
        this.transactionsTo = new ArrayList<>();
    }

    /**
     * Constructs a new Participant with iban and bic
     *
     * @param name  Participant name
     * @param email Participant email
     * @param event Participant event
     * @param iban  Participant iban
     * @param bic   Participant bic
     */
    public Participant(String name, String email, Event event, String iban, String bic) {
        this.name = name;
        this.email = email;
        this.event = event;
        this.iban = iban;
        this.bic = bic;
        this.expensesPaid = new ArrayList<>();
        this.expensesInvolved = new ArrayList<>();
        this.transactionsFrom = new ArrayList<>();
        this.transactionsTo = new ArrayList<>();
    }

    /**
     * for object mapper
     */
    public Participant() {
        this.expensesPaid = new ArrayList<>();
        this.expensesInvolved = new ArrayList<>();
        this.transactionsFrom = new ArrayList<>();
        this.transactionsTo = new ArrayList<>();
    }

    /**
     * getter for id
     *
     * @return returns id
     */
    public long getId() {
        return id;
    }

    /**
     * setter for id
     *
     * @param id Participant id to be set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * getter for name
     *
     * @return returns the name of Participant
     */
    public String getName() {
        return name;
    }

    /**
     * sets the Participant name
     *
     * @param name Participant name to be set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * getter for email
     *
     * @return returns the email of Participant
     */
    public String getEmail() {
        return email;
    }

    /**
     * sets the Participant email
     *
     * @param email Participant name to be set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * a function to check the validity of an email address that was entered
     *
     * @param email the email address
     * @return true or false, the validity
     */
    @SuppressWarnings("checkstyle:CyclomaticComplexity")
    public static boolean emailValidator(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        if (uuidValidator(email)) {
            return true;
        }
        String[] parts = email.split("@");
        /// check for the @ symbol, there must be 2 parts, so only 1 instance of @
        if (parts.length != 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
            return false;
        }
        String[] domainParts = parts[1].split("\\.");
        /// check for the dot and something afterward, it has to be longer or equal to 2
        if (domainParts.length < 2) {
            return false;
        }
        for (String part : domainParts) { /// cannot have empty parts
            if (part.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * a function to check the validity of a UUID
     *
     * @param uuid the UUID
     * @return true or false, the validity
     */
    public static boolean uuidValidator(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * checks iban validity, its optional, so it can be null / empty
     *
     * @param iban the iban
     * @return true or false, the validity
     */
    public static boolean ibanValidator(String iban) {
        if (iban == null || iban.isEmpty()) return true;
        iban = iban.replace(" ", "").toUpperCase();
        if (iban.length() < 15 || !iban.matches("[A-Z]{2}[0-9A-Z]{13,}")) {
            return false;
        }
        // move the first 4 chars to the end of the string
        String ibanRearranged = iban.substring(4) + iban.substring(0, 4);
        StringBuilder sb = new StringBuilder();
        for (char ch : ibanRearranged.toCharArray()) {
            if (Character.isLetter(ch)) {
                sb.append((ch - 'A' + 10));
            } else {
                sb.append(ch);
            }
        }
        /// if iban % 97 == 1 then it is valid
        return new java.math.BigInteger(sb.toString())
                .mod(java.math.BigInteger.valueOf(97)).intValue() == 1;
    }

    /**
     * validator function for all attributes
     *
     * @return the validity status of this Participant
     */
    public int validate() {
        if (!emailValidator(this.getEmail())) {
            return 2;
        }
        if (!ibanValidator(this.getIban())) {
            return 3;
        }
        if (!bicValidator(this.getBic())) {
            return 4;
        }
        return 1;
    }

    /**
     * checks bic validity, its optional, so it can be null / empty
     *
     * @param bic the bic
     * @return true or false the validity
     */
    public static boolean bicValidator(String bic) {
        if (bic == null || bic.isEmpty()) return true;
        if (!(bic.length() == 8 || bic.length() == 11)) {
            return false;
        }
        bic = bic.toUpperCase(); /// in case they use lower case
        return bic.matches("[A-Z]{4}[A-Z]{2}[A-Z0-9]{2}([A-Z0-9]{3})?");
    }

    /**
     * getter for iban
     *
     * @return the iban of the Participant
     */
    public String getIban() {
        return iban;
    }

    /**
     * sets the Participant iban
     *
     * @param iban Participant iban to be set
     */
    public void setIban(String iban) {
        this.iban = iban;
    }

    /**
     * getter for bic
     *
     * @return the bic of the Participant
     */
    public String getBic() {
        return bic;
    }

    /**
     * sets the Participant bic
     *
     * @param bic Participant bic to be set
     */
    public void setBic(String bic) {
        this.bic = bic;
    }

    /**
     * returns true if object equal to participant, false otherwise
     *
     * @param o object to compare
     * @return true if equal, else false
     */
    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, "id",
                "iban", "bic", "expensesPaid", "expensesInvolved", "debtsFrom",
                "debtsTo", "transactionsFrom", "transactionsTo");
    }

    /**
     * returns the hashcode
     *
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "id",
                "iban", "bic", "expensesPaid", "expensesInvolved", "debtsFrom",
                "debtsTo", "transactionsFrom", "transactionsTo");
    }

    /**
     * creates a human-readable string representation
     *
     * @return string representation
     */
    @Override
    public String toString() {
        String rez = "participant id : " + this.id +
                "Participant inviteCode : " + event.getInviteCode() + "\n" +
                "Participant name       : " + name + "\n" +
                "Participant email      : " + email + "\n";
        if (bic == null) rez += "Participant chose not to provide bank information.\n";
        else {
            rez += "Participant iban       : " + iban + "\n";
            rez += "Participant bic        : " + bic + "\n";
        }
        return rez + "----------------------------------------------------------------------\n";
    }

    /**
     * cloning function
     *
     * @return a Participant that needs to be type casted
     * @throws CloneNotSupportedException if the object cannot be cloned
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * getter for the event
     *
     * @return the event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * setter for the event
     *
     * @param event the event to be set
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * getter for the list of expenses
     *
     * @return the list of expenses
     */
    public List<Expense> getExpensesPaid() {
        return expensesPaid;
    }

    /**
     * setter for the list of expenses
     *
     * @param expenseList the list of expenses to be set
     */
    public void setExpensesPaid(List<Expense> expenseList) {
        this.expensesPaid = expenseList;
    }

    /**
     * getter for the list of expenses
     *
     * @return the list of expenses
     */
    public List<Expense> getExpensesInvolved() {
        return expensesInvolved;
    }

    /**
     * setter for the list of expenses
     *
     * @param expensesInvolved the list of expenses to be set
     */
    public void setExpensesInvolved(List<Expense> expensesInvolved) {
        this.expensesInvolved = expensesInvolved;
    }

    /**
     * adds an expense to the list of expenses paid
     *
     * @param expense the expense to be added
     * @return true if the expense was added, false otherwise
     */
    public boolean addExpensePaid(Expense expense) {
        return expensesPaid.add(expense);
    }

    /**
     * adds an expense to the list of expenses the participant is involved in
     *
     * @param expense the expense to be added
     * @return true if the expense was added, false otherwise
     */
    public boolean addExpenseInvolved(Expense expense) {
        return expensesInvolved.add(expense);
    }

    /**
     * updates an expense from the list of expenses paid
     *
     * @param expense the expense to be updated
     */
    public void updateExpensesPaid(Expense expense) {
        int index = expensesPaid.stream().map(Expense::getUuid).toList()
                .indexOf(expense.getUuid());
        if (index == -1)
            addExpensePaid(expense);
        else {
            expensesPaid.set(index, expense);
        }
    }

    /**
     * updates an expense from the list of expenses involved
     *
     * @param expense the expense to be updated
     */
    public void updateExpensesInvolved(Expense expense) {
        int index = expensesInvolved.stream().map(Expense::getUuid).toList()
                .indexOf(expense.getUuid());
        if (index == -1)
            addExpenseInvolved(expense);
        else {
            expensesInvolved.set(index, expense);
        }
    }


    /**
     * getter for the list of transactions from the participant
     *
     * @return the list of transactions from the participant
     */
    public List<Transaction> getTransactionsFrom() {
        return transactionsFrom;
    }


    /**
     * setter for the list of transactions from the participant
     *
     * @param transactionsFrom the list of transactions from the participant
     */
    public void setTransactionsFrom(List<Transaction> transactionsFrom) {
        this.transactionsFrom = transactionsFrom;
    }

    /**
     * getter for the list of transactions to the participant
     *
     * @return the list of transactions to the participant
     */
    public List<Transaction> getTransactionsTo() {
        return transactionsTo;
    }

    /**
     * setter for the list of transactions to the participant
     *
     * @param transactionsTo the list of transactions to the participant
     */
    public void setTransactionsTo(List<Transaction> transactionsTo) {
        this.transactionsTo = transactionsTo;
    }

    /**
     * adds a transaction to the list of transactions from the participant
     *
     * @param transaction the transaction to be added
     * @return true if the transaction was added, false otherwise
     */
    public boolean addTransactionFrom(Transaction transaction) {
        return transactionsFrom.add(transaction);
    }

    /**
     * adds a transaction to the list of transactions to the participant
     *
     * @param transaction the transaction to be added
     * @return true if the transaction was added, false otherwise
     */
    public boolean addTransactionTo(Transaction transaction) {
        return transactionsTo.add(transaction);
    }

    /**
     * updates a transaction from the list of transactions from the participant
     *
     * @param transaction the transaction to be updated
     * @return true if the transaction was updated, false otherwise
     */
    public boolean updateTransactionFrom(Transaction transaction) {
        int index = transactionsFrom.stream().map(Transaction::getId).toList()
                .indexOf(transaction.getId());
        if (index == -1)
            return false;
        transactionsFrom.set(index, transaction);
        return true;
    }

    /**
     * updates a transaction from the list of transactions to the participant
     *
     * @param transaction the transaction to be updated
     * @return true if the transaction was updated, false otherwise
     */
    public boolean updateTransactionTo(Transaction transaction) {
        int index = transactionsTo.stream().map(Transaction::getId).toList()
                .indexOf(transaction.getId());
        if (index == -1)
            return false;
        transactionsTo.set(index, transaction);
        return true;
    }
}
