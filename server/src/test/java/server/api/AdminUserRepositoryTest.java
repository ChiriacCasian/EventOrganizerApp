package server.api;

import commons.AdminUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.FluentQuery;
import server.database.AdminUserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class AdminUserRepositoryTest implements AdminUserRepository {
    public final List<AdminUser> admins = new ArrayList<>();
    public final List<String> calledMethods = new ArrayList<>();

    private void call(String name) {
        calledMethods.add(name);
    }

    /**
     * Flushes all pending changes to the database.
     */
    @Override
    public void flush() {

    }

    /**
     * Saves an entity and flushes changes instantly.
     *
     * @param entity entity to be saved. Must not be {@literal null}.
     * @return the saved entity
     */
    @Override
    public <S extends AdminUser> S saveAndFlush(S entity) {
        return null;
    }

    /**
     * Saves all entities and flushes changes instantly.
     *
     * @param entities entities to be saved. Must not be {@literal null}.
     * @return the saved entities
     * @since 2.5
     */
    @Override
    public <S extends AdminUser> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    /**
     * Deletes the given entities in a batch which means it will create a single query. This kind of operation leaves JPAs
     * first level cache and the database out of sync. Consider flushing the {@link EntityManager} before calling this
     * method.
     * <p>
     * It will also NOT honor cascade semantics of JPA, nor will it emit JPA  lifecycle events.
     * </p>
     *
     * @param entities entities to be deleted. Must not be {@literal null}.
     * @since 2.5
     */
    @Override
    public void deleteAllInBatch(Iterable<AdminUser> entities) {

    }

    /**
     * Deletes the entities identified by the given ids using a single query. This kind of operation leaves JPAs first
     * level cache and the database out of sync. Consider flushing the {@link EntityManager} before calling this method.
     *
     * @param strings the ids of the entities to be deleted. Must not be {@literal null}.
     * @since 2.5
     */
    @Override
    public void deleteAllByIdInBatch(Iterable<String> strings) {

    }

    /**
     * Deletes all entities in a batch call.
     */
    @Override
    public void deleteAllInBatch() {

    }

    /**
     * Returns a reference to the entity with the given identifier. Depending on how the JPA persistence provider is
     * implemented this is very likely to always return an instance and throw an
     * {@link EntityNotFoundException} on first access. Some of them will reject invalid identifiers
     * immediately.
     *
     * @param s must not be {@literal null}.
     * @return a reference to the entity with the given identifier.
     * @see EntityManager#getReference(Class, Object) for details on when an exception is thrown.
     * @deprecated use {@link JpaRepository#getReferenceById(ID)} instead.
     */
    @Override
    @Deprecated
    public AdminUser getOne(String s) {
        return null;
    }

    /**
     * Returns a reference to the entity with the given identifier. Depending on how the JPA persistence provider is
     * implemented this is very likely to always return an instance and throw an
     * {@link EntityNotFoundException} on first access. Some of them will reject invalid identifiers
     * immediately.
     *
     * @param s must not be {@literal null}.
     * @return a reference to the entity with the given identifier.
     * @see EntityManager#getReference(Class, Object) for details on when an exception is thrown.
     * @since 2.5
     * @deprecated use {@link JpaRepository#getReferenceById(ID)} instead.
     */
    @Override
    @Deprecated
    public AdminUser getById(String s) {
        return null;
    }

    /**
     * Returns a reference to the entity with the given identifier. Depending on how the JPA persistence provider is
     * implemented this is very likely to always return an instance and throw an
     * {@link EntityNotFoundException} on first access. Some of them will reject invalid identifiers
     * immediately.
     *
     * @param s must not be {@literal null}.
     * @return a reference to the entity with the given identifier.
     * @see EntityManager#getReference(Class, Object) for details on when an exception is thrown.
     * @since 2.7
     */
    @Override
    public AdminUser getReferenceById(String s) {
        return null;
    }

    /**
     * Returns a single entity matching the given {@link Example} or {@link Optional#empty()} if none was found.
     *
     * @param example must not be {@literal null}.
     * @return a single entity matching the given {@link Example} or {@link Optional#empty()} if none was found.
     * @throws IncorrectResultSizeDataAccessException if the Example yields more than one result.
     */
    @Override
    public <S extends AdminUser> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    /**
     * @param example must not be {@literal null}.
     * @param <S>
     * @return
     */
    @Override
    public <S extends AdminUser> List<S> findAll(Example<S> example) {
        return null;
    }

    /**
     * @param example must not be {@literal null}.
     * @param sort    the {@link Sort} specification to sort the results by, may be {@link Sort#unsorted()}, must not be
     *                {@literal null}.
     * @param <S>
     * @return
     */
    @Override
    public <S extends AdminUser> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    /**
     * Returns a {@link Page} of entities matching the given {@link Example}. In case no match could be found, an empty
     * {@link Page} is returned.
     *
     * @param example  must not be {@literal null}.
     * @param pageable the pageable to request a paged result, can be {@link Pageable#unpaged()}, must not be
     *                 {@literal null}.
     * @return a {@link Page} of entities matching the given {@link Example}.
     */
    @Override
    public <S extends AdminUser> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    /**
     * Returns the number of instances matching the given {@link Example}.
     *
     * @param example the {@link Example} to count instances for. Must not be {@literal null}.
     * @return the number of instances matching the {@link Example}.
     */
    @Override
    public <S extends AdminUser> long count(Example<S> example) {
        return 0;
    }

    /**
     * Checks whether the data store contains elements that match the given {@link Example}.
     *
     * @param example the {@link Example} to use for the existence check. Must not be {@literal null}.
     * @return {@literal true} if the data store contains elements that match the given {@link Example}.
     */
    @Override
    public <S extends AdminUser> boolean exists(Example<S> example) {
        return false;
    }

    /**
     * Returns entities matching the given {@link Example} applying the {@link Function queryFunction} that defines the
     * query and its result type.
     *
     * @param example       must not be {@literal null}.
     * @param queryFunction the query function defining projection, sorting, and the result type
     * @return all entities matching the given {@link Example}.
     * @since 2.6
     */
    @Override
    public <S extends AdminUser, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    /**
     * Saves a given entity. Use the returned instance for further operations as the save operation might have changed the
     * entity instance completely.
     *
     * @param entity must not be {@literal null}.
     * @return the saved entity; will never be {@literal null}.
     * @throws IllegalArgumentException          in case the given {@literal entity} is {@literal null}.
     * @throws OptimisticLockingFailureException when the entity uses optimistic locking and has a version attribute with
     *                                           a different value from that found in the persistence store. Also thrown if the entity is assumed to be
     *                                           present but does not exist in the database.
     */
    @Override
    public <S extends AdminUser> S save(S entity) {
        calledMethods.add("save");
        admins.add(entity);
        return entity;
    }

    /**
     * Saves all given entities.
     *
     * @param entities must not be {@literal null} nor must it contain {@literal null}.
     * @return the saved entities; will never be {@literal null}. The returned {@literal Iterable} will have the same size
     * as the {@literal Iterable} passed as an argument.
     * @throws IllegalArgumentException          in case the given {@link Iterable entities} or one of its entities is
     *                                           {@literal null}.
     * @throws OptimisticLockingFailureException when at least one entity uses optimistic locking and has a version
     *                                           attribute with a different value from that found in the persistence store. Also thrown if at least one
     *                                           entity is assumed to be present but does not exist in the database.
     */
    @Override
    public <S extends AdminUser> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    /**
     * Retrieves an entity by its id.
     *
     * @param s must not be {@literal null}.
     * @return the entity with the given id or {@literal Optional#empty()} if none found.
     * @throws IllegalArgumentException if {@literal id} is {@literal null}.
     */
    @Override
    public Optional<AdminUser> findById(String s) {
        calledMethods.add("findById");
        return admins.stream().filter(a -> a.getPassword().equals(s)).findFirst();
    }

    /**
     * Returns whether an entity with the given id exists.
     *
     * @param s must not be {@literal null}.
     * @return {@literal true} if an entity with the given id exists, {@literal false} otherwise.
     * @throws IllegalArgumentException if {@literal id} is {@literal null}.
     */
    @Override
    public boolean existsById(String s) {
        return false;
    }

    /**
     * Returns all instances of the type.
     *
     * @return all entities
     */
    @Override
    public List<AdminUser> findAll() {
        return null;
    }

    /**
     * Returns all instances of the type {@code T} with the given IDs.
     * <p>
     * If some or all ids are not found, no entities are returned for these IDs.
     * <p>
     * Note that the order of elements in the result is not guaranteed.
     *
     * @param strings must not be {@literal null} nor contain any {@literal null} values.
     * @return guaranteed to be not {@literal null}. The size can be equal or less than the number of given
     * {@literal ids}.
     * @throws IllegalArgumentException in case the given {@link Iterable ids} or one of its items is {@literal null}.
     */
    @Override
    public List<AdminUser> findAllById(Iterable<String> strings) {
        return null;
    }

    /**
     * Returns the number of entities available.
     *
     * @return the number of entities.
     */
    @Override
    public long count() {
        return 0;
    }

    /**
     * Deletes the entity with the given id.
     * <p>
     * If the entity is not found in the persistence store it is silently ignored.
     *
     * @param s must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal id} is {@literal null}
     */
    @Override
    public void deleteById(String s) {

    }

    /**
     * Deletes a given entity.
     *
     * @param entity must not be {@literal null}.
     * @throws IllegalArgumentException          in case the given entity is {@literal null}.
     * @throws OptimisticLockingFailureException when the entity uses optimistic locking and has a version attribute with
     *                                           a different value from that found in the persistence store. Also thrown if the entity is assumed to be
     *                                           present but does not exist in the database.
     */
    @Override
    public void delete(AdminUser entity) {

    }

    /**
     * Deletes all instances of the type {@code T} with the given IDs.
     * <p>
     * Entities that aren't found in the persistence store are silently ignored.
     *
     * @param strings must not be {@literal null}. Must not contain {@literal null} elements.
     * @throws IllegalArgumentException in case the given {@literal ids} or one of its elements is {@literal null}.
     * @since 2.5
     */
    @Override
    public void deleteAllById(Iterable<? extends String> strings) {

    }

    /**
     * Deletes the given entities.
     *
     * @param entities must not be {@literal null}. Must not contain {@literal null} elements.
     * @throws IllegalArgumentException          in case the given {@literal entities} or one of its entities is {@literal null}.
     * @throws OptimisticLockingFailureException when at least one entity uses optimistic locking and has a version
     *                                           attribute with a different value from that found in the persistence store. Also thrown if at least one
     *                                           entity is assumed to be present but does not exist in the database.
     */
    @Override
    public void deleteAll(Iterable<? extends AdminUser> entities) {

    }

    /**
     * Deletes all entities managed by the repository.
     */
    @Override
    public void deleteAll() {

    }

    /**
     * Returns all entities sorted by the given options.
     *
     * @param sort the {@link Sort} specification to sort the results by, can be {@link Sort#unsorted()}, must not be
     *             {@literal null}.
     * @return all entities sorted by the given options
     */
    @Override
    public List<AdminUser> findAll(Sort sort) {
        return null;
    }

    /**
     * Returns a {@link Page} of entities meeting the paging restriction provided in the {@link Pageable} object.
     *
     * @param pageable the pageable to request a paged result, can be {@link Pageable#unpaged()}, must not be
     *                 {@literal null}.
     * @return a page of entities
     */
    @Override
    public Page<AdminUser> findAll(Pageable pageable) {
        return null;
    }
}
