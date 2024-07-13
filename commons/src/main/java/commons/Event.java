package commons;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;

@Entity
public class Event {
    private String title;

    private String description;

    @Id
    private String inviteCode;

    private String currency;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateOfCreation;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateOfModification;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "event", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Participant> participants;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "event", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Expense> expenses;

    @Transient
    private List<Debt> debts;

    /**
     * Constructor for an event.
     *
     * @param title          The title of the event.
     * @param description    A description of the event.
     * @param dateOfCreation The date the event was created.
     */
    public Event(String title, String description, LocalDateTime dateOfCreation) {
        this.title = title;
        this.description = description;
        this.inviteCode = generateInviteCode();
        this.dateOfCreation = dateOfCreation.truncatedTo(ChronoUnit.SECONDS);
        this.dateOfModification = dateOfCreation.truncatedTo(ChronoUnit.SECONDS);
        this.participants = new ArrayList<>();
        this.expenses = new ArrayList<>();
    }

    /**
     * Alternative constructor for an event, including the currency attribute.
     *
     * @param title          The title of the event.
     * @param description    A description of the event.
     * @param currency       The currency of the event.
     * @param dateOfCreation The date the event was created.
     */
    public Event(String title, String description, String currency, LocalDateTime dateOfCreation) {
        this.title = title;
        this.description = description;
        this.inviteCode = generateInviteCode();
        this.currency = currency;
        this.dateOfCreation = dateOfCreation.truncatedTo(ChronoUnit.SECONDS);
        this.dateOfModification = dateOfCreation.truncatedTo(ChronoUnit.SECONDS);
        this.participants = new ArrayList<>();
        this.expenses = new ArrayList<>();
    }

    /**
     * Default constructor.
     */
    public Event() {
        this.participants = new ArrayList<>();
        this.expenses = new ArrayList<>();
    }

    /**
     * Getter for the id attribute.
     *
     * @return id.
     */
    public String getInviteCode() {
        return inviteCode;
    }

    /**
     * Getter for the title attribute.
     *
     * @return title.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Getter for the description attribute.
     *
     * @return description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Getter for the currency attribute.
     *
     * @return the event's currency.
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Getter for the creation date attribute.
     *
     * @return dateOfCreation.
     */
    public LocalDateTime getDateOfCreation() {
        return this.dateOfCreation.truncatedTo(ChronoUnit.SECONDS);
    }

    /**
     * Getter for the modification date attribute.
     *
     * @return dateOfModification.
     */
    public LocalDateTime getDateOfModification() {
        return this.dateOfModification.truncatedTo(ChronoUnit.SECONDS);
    }

    /**
     * Getter for the participants attribute.
     *
     * @return participants.
     */
    public List<Participant> getParticipants() {
        return this.participants;
    }

    /**
     * Getter for the expenses attribute.
     *
     * @return expenses.
     */
    public List<Expense> getExpenses() {
        return this.expenses;
    }

    /**
     * Getter for the sum of all expenses in the expenses list.
     *
     * @return the sum of all expenses as a double.
     */
    public double totalExpenses() {
        double total = 0.0;
        for (Expense expense : expenses) {
            total += expense.getAmount();
        }
        return (double) Math.round(total * 100) / 100;
    }


    /**
     * Getter for the debt list.
     *
     * @return a list of debts.
     */
    public List<Debt> getDebts() {
        return this.debts;
    }

    /**
     * Gets the sum of the remaining amount that needs to be paid for all open debts.
     *
     * @return that sum.
     */
    public double totalOpenDebts() {
        return debts.stream().map(Debt::getTotal).mapToDouble(d -> d).sum();
    }

    /**
     * Setter for the event's id (pkey).
     *
     * @param inviteCode the new id of the event.
     */
    public void setInviteCode(String inviteCode) {
        if (!validateInviteCode(inviteCode)) {
            throw new IllegalArgumentException("Invite code is invalid!");
        }
        this.inviteCode = inviteCode;
    }

    /**
     * Setter for the title attribute, used to rename the event.
     *
     * @param title the new title of the event.
     * @throws IllegalArgumentException if title is null.
     */
    public void setTitle(String title) throws IllegalArgumentException {
        if (title == null) {
            throw new IllegalArgumentException("Title is null!");
        }
        this.title = title;
    }

    /**
     * Setter for the description attribute, used to edit the description.
     *
     * @param description the new description of the event.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Setter for the date of modification attribute, used when changes in the event happen
     *
     * @param date the new date of modification
     */
    public void setDateOfModification(LocalDateTime date) throws IllegalArgumentException {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null!");
        }
        this.dateOfModification = date.truncatedTo(ChronoUnit.SECONDS);
    }

    /**
     * Setter for the currency attribute.
     *
     * @param currency the new currency.
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Setter for the date of creation attribute.
     *
     * @param dateOfCreation the new date of creation.
     */
    public void setDateOfCreation(LocalDateTime dateOfCreation) {
        this.dateOfCreation = dateOfCreation.truncatedTo(ChronoUnit.SECONDS);
    }

    /**
     * Setter for the participants attribute.
     *
     * @param participants the new list of participants.
     */
    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    /**
     * Setter for the expenses attribute.
     *
     * @param expenses the new list of expenses.
     */
    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

    /**
     * Setter for the debts attribute.
     *
     * @param debts the new list of debts.
     */
    public void setDebts(List<Debt> debts) {
        this.debts = debts;
    }

    /**
     * Adds a participant to the participant list of the event.
     *
     * @param toAdd participant to add to the event.
     * @return 1 if the participant was successfully added, 0 otherwise.
     */
    public int addParticipant(Participant toAdd) {
        if (toAdd == null) {
            return 0;
        }
        toAdd.setEvent(this);
        participants.remove(toAdd);
        boolean bool = participants.add(toAdd);
        if (bool) return 1;
        return 0;
    }

    /**
     * Adds a list of participants to the participant list of the event.
     *
     * @param toAdd participants to add to the event.
     * @return true if the participants were successfully added, false otherwise.
     */
    public boolean addParticipant(List<Participant> toAdd) {
        if (toAdd == null || toAdd.isEmpty() || new HashSet<>(toAdd).size() != toAdd.size()) {
            return false;
        }
        for (Participant p : toAdd) {
            p.setEvent(this);
        }
        return participants.addAll(toAdd);
    }

    /**
     * Remove a participant from the participant list of the event.
     *
     * @param toRemove participant to remove from the event.
     * @return true if the participant was successfully removed, false otherwise.
     */
    public boolean removeParticipant(Participant toRemove) {
        return participants.remove(toRemove);
    }

    /**
     * Removes all participants in the list from the participant list of the event.
     *
     * @param toRemove participants to remove from the event.
     * @return true if the participants were successfully removed, false otherwise.
     */
    public boolean removeParticipant(List<Participant> toRemove) {
        return participants.removeAll(toRemove);
    }

    /**
     * Adds an expense to the expense list of the event.
     *
     * @param toAdd expense to add to the event.
     * @return true if the expense was successfully added, false otherwise.
     */
    public boolean addExpense(Expense toAdd) {
        if (toAdd == null || toAdd.getAmount() <= 0 ||
                new HashSet<>(participants).addAll(toAdd.getParticipants()) ||
                !participants.contains(toAdd.getPayer())) {
            return false;
        }
        toAdd.setEvent(this);
        expenses.remove(toAdd);
        return expenses.add(toAdd);
    }

    /**
     * Edits and expense in the expense list of the event.
     *
     * @param newExpense the new expense to replace the existing one.
     * @return true if the expense was successfully updated, false otherwise.
     */
    public boolean updateExpense(Expense newExpense) {
        int index = -1;
        for (int i = 0; i < expenses.size(); i++) {
            if (expenses.get(i).getUuid().equals((newExpense.getUuid()))) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return false;
        }
        newExpense.setEvent(this);
        expenses.set(index, newExpense);
        return true;
    }

    /**
     * Updates a participant in the participant list of the event.
     *
     * @param newParticipant the new participant to replace the existing one.
     * @return true if the participant was successfully updated, false otherwise.
     */
    public boolean updateParticipant(Participant newParticipant) {
        int index = -1;
        for (int i = 0; i < participants.size(); i++) {
            if (participants.get(i).getId() == ((newParticipant.getId()))) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return false;
        }
        newParticipant.setEvent(this);
        participants.set(index, newParticipant);
        return true;
    }

    /**
     * Remove an expense from the expense list of the event.
     *
     * @param toRemove expense to remove from the event.
     * @return true if the expense was successfully removed, false otherwise.
     */
    public boolean removeExpense(Expense toRemove) {
        return expenses.remove(toRemove);
    }

    /**
     * Calculates the share a participant in the participant list owes to the group.
     *
     * @param toCalculate the participant whose share should be calculated.
     * @return the share of the participant, as a double.
     */
    public double calculateShareOwedFrom(Participant toCalculate) {
        if (toCalculate == null || !this.participants.contains(toCalculate)) {
            throw new IllegalArgumentException("Participant not in list of participants");
        }

        double ret = 0;

        for (Expense ex : expenses) {
            if (ex.getParticipants().contains(toCalculate) && !ex.getPayer().equals(toCalculate)) {
                ret += ex.amountPerPerson();
                int mod = (int) (100 * ex.getAmount()) % ex.getParticipants().size();
                if (ex.getParticipants().indexOf(toCalculate) < mod) {
                    ret += 0.01;
                }
            }
        }

        for (Transaction t : toCalculate.getTransactionsFrom()) {
            ret -= t.getAmount();
        }

        return ret;
    }

    /**
     * Calculates the share a participant in the participant list is owed by the group.
     *
     * @param toCalculate the participant whose share should be calculated.
     * @return the share of the participant, as a double.
     */
    @SuppressWarnings("checkstyle:CyclomaticComplexity")
    public double calculateShareOwedTo(Participant toCalculate) {
        if (toCalculate == null || !this.participants.contains(toCalculate)) {
            throw new IllegalArgumentException("Participant not in list of participants");
        }

        double ret = 0;

        for (Expense ex : expenses) {
            if (ex.getPayer().equals(toCalculate) && ex.getParticipants().contains(toCalculate)) {
                ret += (ex.getParticipants().size() - 1) * ex.amountPerPerson();
                int mod = (int) (100 * ex.getAmount()) % ex.getParticipants().size();
                int index = ex.getParticipants().indexOf(toCalculate);
                if (index >= mod) {
                    ret += (mod) * 0.01;
                } else {
                    ret += (mod - 1) * 0.01;
                }
            } else if (ex.getPayer().equals(toCalculate)) {
                ret += ex.getAmount();
            }
        }

        for (Transaction t : toCalculate.getTransactionsTo()) {
            ret -= t.getAmount();
        }

        return ret;
    }

    /**
     * Calculates the balance (net money owed) for a participant in the participant list.
     *
     * @param toCalculate the participant whose balance should be calculated.
     * @return the balance of the participant, as a double.
     */
    public double calculateBalance(Participant toCalculate) {
        if (toCalculate == null || !this.participants.contains(toCalculate)) {
            throw new IllegalArgumentException("Participant not in list of participants");
        }

        return calculateShareOwedTo(toCalculate) - calculateShareOwedFrom(toCalculate);
    }

    /**
     * Settles the debts of all participants.
     *
     * @return a set of debts of a maximal size n-1, where n is the number of participants
     * in the event
     */
    public Set<Debt> debtOverview() {
        Set<Debt> ret = new HashSet<>();
        Map<Participant, Double> payers = new HashMap<>();
        Map<Participant, Double> payees = new HashMap<>();

        for (Participant p : participants) {
            double balance = (double) Math.round(calculateBalance(p) * 100) / 100;
            if (balance < 0) {
                payers.put(p, -1 * balance);
            } else if (balance > 0) {
                payees.put(p, balance);
            }
        }

        debtCalculate(payers, payees, ret);

        ret.stream().filter(debt -> debt.getTotal() == 0).forEach(ret::remove);
        return ret;
    }


    private void debtCalculate(Map<Participant, Double> payers,
                               Map<Participant, Double> payees, Set<Debt> ret) {
        Set<Participant> payersLeft = payers.keySet();
        Set<Participant> payeesLeft = payees.keySet();
        while (!payersLeft.isEmpty() && !payeesLeft.isEmpty()) {
            Participant payer = payersLeft.stream().findFirst().get();
            Participant payee = payeesLeft.stream().findFirst().get();
            double paying = Math.min(payers.get(payer), payees.get(payee));
            paying = (double) Math.round(paying * 100) / 100;
            double payerNewBalance = payers.get(payer) - paying;
            double payeeNewBalance = payees.get(payee) - paying;
            if (Math.abs(payerNewBalance) < 0.01) {
                payersLeft.remove(payer);
            } else {
                payers.replace(payer, payerNewBalance);
            }
            if (Math.abs(payeeNewBalance) < 0.01) {
                payeesLeft.remove(payee);
            } else {
                payees.replace(payee, payeeNewBalance);
            }
            Debt add = new Debt(payer, payee, paying, currency);
            ret.add(add);
        }
    }

    /**
     * A textual representation of the event instance.
     *
     * @return a String containing a textual representation of all attributes.
     */
    @Override
    public String toString() {
        return "Title: " + title + "\n" + "Description: " + description + "\n" +
                "Invite Code: " + inviteCode + "\n" + "Date of Creation: " + dateOfCreation
                + "\n" + "Date of Modification: " + dateOfModification + "\n"
                + "Participants: " + participants.size() + "\n"
                // Only showing the size, not the full list
                + "Expenses: " + expenses.size(); // Only showing the size, not the full list
    }

    /**
     * Compares two event instances.
     *
     * @param o the object to compare to.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, "inviteCode");
    }


    /**
     * Creates a unique hash code for an object
     *
     * @return a hashcode
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "inviteCode");
    }

    /**
     * Generates a random six-digit alphanumeric invite code.
     *
     * @return a valid invite code.
     */
    public static String generateInviteCode() {
        int length = 6;
        int lowerAsciiBound = 48;
        int upperAsciiBound = 90;
        Random random = new Random();
        return random.ints(lowerAsciiBound, upperAsciiBound + 1)
                .filter(i -> (i <= 57 || i >= 65) && i <= 90)
                .limit(length).collect(StringBuilder::new, StringBuilder::appendCodePoint,
                        StringBuilder::append).toString();
    }

    /**
     * Validates an invite code.
     *
     * @param inviteCode the invite code to validate.
     * @return true if the invite code has six digits and
     * only consists of numbers and uppercase letters, false otherwise.
     */
    public static boolean validateInviteCode(String inviteCode) {
        return inviteCode != null && inviteCode.length() == 6 &&
                Pattern.compile("^[0-9A-Z]*$").matcher(inviteCode).matches();
    }

    /**
     * Returns a String representation of all participants
     *
     * @return a String containing all participants in the event seperated by commas
     */
    public String participantsToString() {
        StringBuilder output = new StringBuilder();
        for (Participant participant : participants) {
            output.append(participant.getName());
            output.append(", ");
        }
        if (output.length() >= 2) {
            output.delete(output.length() - 2, output.length());
        }
        return output.toString();
    }

    /**
     * Updates the fields of the event and all its participants,
     * expenses and transactions to properly represent bidirectional relationships.
     */
    @SuppressWarnings("checkstyle:CyclomaticComplexity")
    public void updateFields() {
        for (Expense expense : expenses) {
            expense.getParticipants().clear();
        }
        for (Participant participant : participants) {
            for (Expense expense : participant.getExpensesPaid()) {
                expense.getParticipants().clear();
            }
        }
        List<Transaction> transactions = new ArrayList<>();
        List<Expense> expenses = new ArrayList<>();
        for (Participant participant : participants) {
            participant.setEvent(this);
            setTransactions(participant, transactions);
            setExpenses(participant, expenses);
        }
        for (Participant participant : participants) {
            participant.getTransactionsFrom().removeIf(transaction -> transaction.getPayer() == null
                    || participants.stream().noneMatch(p -> p.getId() ==
                    transaction.getPayer().getId()));
            participant.getTransactionsTo().removeIf(transaction -> transaction.getPayee() == null
                    || participants.stream().noneMatch(p -> p.getId() ==
                    transaction.getPayee().getId()));
        }
        this.setExpenses(expenses);
    }


    /**
     * Updates the transactions of all participants in the event.
     */
    private void setTransactions(Participant participant, List<Transaction> transactions) {
        for (Transaction transaction : participant.getTransactionsFrom()) {
            int index = transactions.stream().map(Transaction::getId).toList()
                    .indexOf(transaction.getId());
            if (index != -1) {
                Transaction reference = transactions.get(index);
                participant.getTransactionsFrom().set(participant.getTransactionsFrom()
                        .indexOf(transaction), reference);
                transaction = reference;
            } else {
                transactions.add(transaction);
            }
            transaction.setPayer(participant);
        }
        for (Transaction transaction : participant.getTransactionsTo()) {
            int index = transactions.stream().map(Transaction::getId).toList()
                    .indexOf(transaction.getId());
            if (index != -1) {
                Transaction reference = transactions.get(index);
                participant.getTransactionsTo().set(participant.getTransactionsTo()
                        .indexOf(transaction), reference);
                transaction = reference;
            } else {
                transactions.add(transaction);
            }
            transaction.setPayee(participant);
        }
    }

    private void setExpenses(Participant participant, List<Expense> expenses) {
        for (Expense expense : participant.getExpensesPaid()) {
            int index = expenses.stream().map(Expense::getUuid).toList()
                    .indexOf(expense.getUuid());
            if (index != -1) {
                Expense reference = expenses.get(index);
                participant.updateExpensesPaid(reference);
                expense = reference;
            } else {
                expense.setEvent(this);
                expenses.add(expense);
            }
            expense.setPayer(participant);
        }
        for (Expense expense : participant.getExpensesInvolved()) {
            int index = expenses.stream().map(Expense::getUuid).toList()
                    .indexOf(expense.getUuid());
            if (index != -1) {
                Expense reference = expenses.get(index);
                participant.updateExpensesInvolved(reference);
                expense = reference;
            } else {
                expense.setEvent(this);
                expenses.add(expense);
            }
            expense.getParticipants().add(participant);
        }
    }
}
