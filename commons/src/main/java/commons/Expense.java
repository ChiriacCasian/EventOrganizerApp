package commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDate;
import java.util.*;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class Expense implements Identifiable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String uuid;
    private String title;

    private double amount;
    private String currency;
    private LocalDate date;
    private boolean splitEqually;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "invite_code", nullable = false)
    @JsonIgnore
    private Event event;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "expense_type_mapping",
            joinColumns = {@JoinColumn(name = "expense_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "expense_type")
    @Column(name = "color")
    private Map<String, String> expenseTypes;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "participants_expenses",
            joinColumns = @JoinColumn(name = "expense_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id"))
    @JsonIgnore
    private List<Participant> participants;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payer_id", nullable = false)
    @JsonIgnore
    private Participant payer;

    /**
     * equals method for the Expense class
     *
     * @param obj an object to compare the instance with
     * @return true if the instance is equal to the object, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj, "id");
    }

    /**
     * hashCode method for the Expense class
     *
     * @return an integer - the hash code of the Expense object
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "id");
    }

    /**
     * toString method of the Expense class
     *
     * @return a textual representation of the object
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }

    /**
     * An empty constructor for creating an Expense object
     */
    public Expense() {
        this.participants = new ArrayList<>();
        this.expenseTypes = new HashMap<>();
    }

    /**
     * A full constructor of Expense
     *
     * @param event        the event the expense is associated with
     * @param payer        the person who paid for that expense
     * @param participants the people who are involved
     * @param title        name/reason of the expense
     * @param amount       the total paid for the expense
     * @param currency     the currency in which the payment was performed
     * @param date         when the payment happened
     * @param splitEqually whether every member should contribute equally
     * @param expenseTypes a label for the type of the expense
     */
    public Expense(Event event, Participant payer, List<Participant> participants,
                   String title, double amount,
                   String currency, LocalDate date,
                   boolean splitEqually, Map<String, String> expenseTypes) {
        this.uuid = UUID.randomUUID().toString();
        this.event = event;
        this.payer = payer;
        this.participants = participants;
        this.title = title;
        this.amount = amount;
        this.currency = currency;
        this.date = date;
        this.splitEqually = splitEqually;
        this.expenseTypes = expenseTypes;
        if (expenseTypes == null) {
            this.expenseTypes = new HashMap<>();
        }
    }

    /**
     * A constructor for an expense with initially just one participant
     *
     * @param event        the event the expense is associated with
     * @param payer        the person who paid for that expense
     * @param title        name/reason of the expense
     * @param amount       the total paid for the expense
     * @param currency     the currency in which the payment was performed
     * @param date         when the payment happened
     * @param splitEqually whether every member should contribute equally
     * @param expenseTypes a label for the type of the expense
     */
    public Expense(Event event, Participant payer, String title,
                   double amount, String currency, LocalDate date,
                   boolean splitEqually, Map<String, String> expenseTypes) {
        this.uuid = UUID.randomUUID().toString();
        this.event = event;
        this.payer = payer;
        this.participants = new ArrayList<>();
        this.title = title;
        this.amount = amount;
        this.currency = currency;
        this.date = date;
        this.splitEqually = splitEqually;
        this.expenseTypes = expenseTypes;
        if (expenseTypes == null) {
            this.expenseTypes = new HashMap<>();
        }
    }

    /**
     * getter for the event
     *
     * @return the event the expense is associated with
     */
    public Event getEvent() {
        return event;
    }

    /**
     * setter for the event
     *
     * @param event the event the expense is associated with
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * Gets the id of the Expense instance
     *
     * @return the id of the current instance
     */
    public long getId() {
        return id;
    }

    /**
     * A getter for the Expense title
     *
     * @return the title of the Expense object
     */
    public String getTitle() {
        return title;
    }

    /**
     * A getter for the Expense amount
     *
     * @return the amount of the Expense object
     */
    public double getAmount() {
        return amount;
    }

    /**
     * A getter for the Expense participants
     *
     * @return the participants of the Expense object
     */
    public List<Participant> getParticipants() {
        return participants;
    }

    /**
     * A getter for the Expense payer
     *
     * @return the payer of the Expense object
     */
    public Participant getPayer() {
        return payer;
    }

    /**
     * A getter for the Expense currency
     *
     * @return the currency of the Expense object
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * A getter for the Expense date
     *
     * @return the date of the Expense object
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * A getter for the Expense splitting method
     *
     * @return the way the amount of the Expense object is split
     */
    public boolean isSplitEqually() {
        return splitEqually;
    }

    /**
     * A getter for the Expense types
     *
     * @return the tags of the Expense object
     */
    public Map<String, String> getExpenseTypes() {
        return expenseTypes;
    }

    /**
     * A setter for the Expense id
     *
     * @param id the new id of the Expense object
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * A setter for the Expense title
     *
     * @param title the new title of the Expense object
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * /*
     * A setter for the Expense amount
     *
     * @param amount the new amount of the Expense object
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * A setter for the Expense participants
     *
     * @param participants the new participants of the Expense object
     */
    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    /**
     * A setter for the Expense payer
     *
     * @param payer the new payer of the Expense object
     */
    public void setPayer(Participant payer) {
        this.payer = payer;
    }

    /**
     * A setter for the Expense currency
     *
     * @param currency the new currency of the Expense object
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * A setter for the Expense date
     *
     * @param date the new date of the Expense object
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * A setter for the Expense way of splitting
     *
     * @param splitEqually the new way of splitting the amount of the Expense object
     */
    public void setSplitEqually(boolean splitEqually) {
        this.splitEqually = splitEqually;
    }

    /**
     * A setter for the Expense types
     *
     * @param expenseTypes the new types of the Expense object
     */
    public void setExpenseTypes(Map<String, String> expenseTypes) {
        this.expenseTypes = expenseTypes;
    }

    /**
     * A getter for the Expense UUID
     *
     * @return the UUID of the Expense object
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * A setter for the Expense UUID
     *
     * @param uuid the new UUID of the Expense object
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * A method for computing the average amount each member has to pay of splitting equally
     *
     * @return the average amount oer participant
     */
    public double amountPerPerson() {
        int numberOfParticipants = this.participants.size();
        return Math.floor(100 * this.amount / numberOfParticipants) / 100;
    }
}
