package server.api;

import commons.Participant;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import server.database.ParticipantRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ParticipantRepositoryTest implements ParticipantRepository {

    public final List<Participant> participants = new ArrayList<>();
    public final List<String> calledMethods = new ArrayList<>();

    private void call(String name) {
        calledMethods.add(name);
    }

    private Optional<Participant> find(Long id) {
        return participants.stream().filter(p -> p.getId() == id).findFirst();
    }

    @Override
    public List<Participant> findAll() {
        calledMethods.add("findAll");
        return participants;
    }

    @Override
    public Participant getById(Long id) {
        call("getById");
        return find(id).get();
    }

    @Override
    public Participant getReferenceById(Long id) {
        call("getReferenceById");
        return find(id).get();
    }

    @Override
    public <S extends Participant> S save(S entity) {
        call("save");
        int index = Math.max(participants.stream().map(Participant::getId).toList()
                .indexOf(entity.getId()), participants.indexOf(entity));
        if (index == -1)
            participants.add(entity);
        else
            participants.set(index, entity);
        return entity;
    }

    @Override
    public boolean existsById(Long id) {
        call("existsById");
        return find(id).isPresent();
    }

    @Override
    public long count() {
        return participants.size();
    }


    @Override
    public void flush() {

    }

    @Override
    public <S extends Participant> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Participant> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Participant> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Participant getOne(Long aLong) {
        return null;
    }


    @Override
    public <S extends Participant> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Participant> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Participant> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Participant> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Participant> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Participant> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Participant, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Participant> List<S> saveAll(Iterable<S> entities) {
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
    public Optional<Participant> findById(Long aLong) {
        call("findById");
        return participants.stream().filter(p -> p.getId() == aLong).findFirst();
    }

    @Override
    public List<Participant> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public void deleteById(Long aLong) {
        call("deleteById");
        participants.removeIf(p -> p.getId() == aLong);
    }

    @Override
    public void delete(Participant entity) {
        call("delete");
        participants.remove(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Participant> entities) {
        for (Participant p : entities) {
            delete(p);
        }
    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Participant> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Participant> findAll(Pageable pageable) {
        return null;
    }
}
