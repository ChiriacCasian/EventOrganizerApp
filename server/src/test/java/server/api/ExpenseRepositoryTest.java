package server.api;

import commons.Expense;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import server.database.ExpenseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ExpenseRepositoryTest implements ExpenseRepository {

    public final List<Expense> expenses = new ArrayList<>();
    public final List<String> calledMethods = new ArrayList<>();

    private void call(String name) {
        calledMethods.add(name);
    }

    private Optional<Expense> find(Long id) {
        return expenses.stream().filter(q -> q.getId() == id).findFirst();
    }

    @Override
    public List<Expense> findAll() {
        calledMethods.add("findAll");
        return expenses;
    }

    @Override
    public List<Expense> findAll(Sort sort) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Expense> List<S> findAll(Example<S> example) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Expense> List<S> findAll(Example<S> example, Sort sort) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Page<Expense> findAll(Pageable pageable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Expense> Page<S> findAll(Example<S> example, Pageable pageable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Expense> findAllById(Iterable<Long> ids) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Expense> List<S> saveAll(Iterable<S> entities) {
        call("saveAll");
        List<S> ret = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        for (S s : entities) {
            while (ids.contains(s.getId())) {
                s.setId(s.getId() + 1);
            }
            save(s);
            ids.add(s.getId());
        }
        return ret;
    }

    @Override
    public void flush() {
        // TODO Auto-generated method stub

    }

    @Override
    public <S extends Expense> S saveAndFlush(S entity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Expense> List<S> saveAllAndFlush(Iterable<S> entities) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Expense> entities) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAllInBatch() {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> ids) {
        // TODO Auto-generated method stub

    }

    @Override
    public Expense getOne(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Expense getById(Long id) {
        call("getById");
        return find(id).get();
    }

    @Override
    public Expense getReferenceById(Long id) {
        call("getReferenceById");
        return find(id).get();
    }

    @Override
    public <S extends Expense> S save(S entity) {
        call("save");
        int index = Math.max(expenses.stream().map(Expense::getId).toList()
                .indexOf(entity.getId()), expenses.indexOf(entity));
        if (index == -1)
            expenses.add(entity);
        else
            expenses.set(index, entity);
        return entity;
    }

    @Override
    public Optional<Expense> findById(Long id) {
        call("findById");
        return expenses.stream().filter(q -> q.getId() == id).findFirst();
    }

    @Override
    public boolean existsById(Long id) {
        call("existsById");
        return find(id).isPresent();
    }

    @Override
    public long count() {
        return expenses.size();
    }

    @Override
    public <S extends Expense> long count(Example<S> example) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {
        call("deleteById");
        expenses.removeIf(e -> e.getId() == aLong);
    }

    @Override
    public void delete(Expense entity) {
        call("delete");
        expenses.remove(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAll(Iterable<? extends Expense> entities) {
        for (Expense e : entities) {
            delete(e);
        }
    }

    @Override
    public void deleteAll() {
        // TODO Auto-generated method stub

    }

    @Override
    public <S extends Expense> Optional<S> findOne(Example<S> example) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public <S extends Expense> boolean exists(Example<S> example) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <S extends Expense, R> R findBy(
            Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        // TODO Auto-generated method stub
        return null;
    }
}
