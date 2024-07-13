package commons;

public interface Identifiable {
    /**
     * Gets the id of an identifiable entity.
     * @return the id.
     */
    long getId();

    /**
     * Sets the id of an identifiable entity.
     * @param id the new id.
     */
    void setId(long id);
}
