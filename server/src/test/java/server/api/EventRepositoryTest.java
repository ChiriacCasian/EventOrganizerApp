/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package server.api;

import commons.Event;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
import server.database.EventRepository;
import server.database.ExpenseRepository;
import server.database.ParticipantRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class EventRepositoryTest implements EventRepository {

    public final List<Event> events = new ArrayList<>();
    public final List<String> calledMethods = new ArrayList<>();
    public final ExpenseRepository expenseRepository;
    public final ParticipantRepository participantRepository;

    public EventRepositoryTest(ExpenseRepository expenseRepository, ParticipantRepository participantRepository) {
        this.expenseRepository = expenseRepository;
        this.participantRepository = participantRepository;
    }

    private void call(String name) {
        calledMethods.add(name);
    }

    private Optional<Event> find(String id) {
        call("find");
        return events.stream().filter(q -> q.getInviteCode().equals(id)).findFirst();
    }

    @Override
    public List<Event> findAll() {
        call("findAll");
        return events;
    }

    @Override
    public List<Event> findAll(Sort sort) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Event> List<S> findAll(Example<S> example) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Event> List<S> findAll(Example<S> example, Sort sort) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Page<Event> findAll(Pageable pageable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Event> Page<S> findAll(Example<S> example, Pageable pageable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Event> findAllById(Iterable<String> ids) {
        call("findAllById");
        List<Event> ret = new ArrayList<>();
        for (String id : ids) {
            if (existsById(id)) {
                ret.add(findById(id).get());
            }
        }
        return ret;
    }

    @Override
    public <S extends Event> List<S> saveAll(Iterable<S> entities) {
        call("saveAll");
        List<S> ret = new ArrayList<>();
        for (S s : entities) {
            save(s);
        }
        return ret;
    }

    @Override
    public void flush() {
        // TODO Auto-generated method stub
    }

    @Override
    public <S extends Event> S saveAndFlush(S entity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Event> List<S> saveAllAndFlush(Iterable<S> entities) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Event> entities) {
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteAllInBatch() {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<String> ids) {
        // TODO Auto-generated method stub
    }

    @Override
    public Event getOne(String id) {
        call("getOne");
        return events.stream().filter(e -> e.getInviteCode().equals(id)).findFirst().get();
    }

    @Override
    public Event getById(String id) {
        call("getById");
        return find(id).get();
    }

    @Override
    public Event getReferenceById(String id) {
        call("getReferenceById");
        return find(id).get();
    }

    @Override
    public <S extends Event> S save(S entity) {
        call("save");
        int index = Math.max(events.stream().map(Event::getInviteCode).toList()
                .indexOf(entity.getInviteCode()), events.indexOf(entity));
        if (index == -1)
            events.add(entity);
        else
            events.set(index, entity);
        participantRepository.deleteAll(new ArrayList<>(entity.getParticipants()));
        expenseRepository.deleteAll(new ArrayList<>(entity.getExpenses()));
        participantRepository.saveAll(entity.getParticipants());
        expenseRepository.saveAll(entity.getExpenses());
        return entity;
    }

    @Override
    public Optional<Event> findById(String id) {
        call("findById");
        return events.stream().filter(e -> e.getInviteCode().equals(id)).findFirst();
    }

    @Override
    public boolean existsById(String id) {
        call("existsById");
        return find(id).isPresent();
    }

    @Override
    public long count() {
        call("count");
        return events.size();
    }

    @Override
    public <S extends Event> long count(Example<S> example) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void deleteById(String id) {
        call("deleteById");
        events.removeIf(e -> e.getInviteCode().equals(id));
    }

    @Override
    public void delete(Event entity) {
        call("delete");
        events.remove(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends String> ids) {
        call("deleteAllById");
        for (String id : ids) {
            deleteById(id);
        }
    }

    @Override
    public void deleteAll(Iterable<? extends Event> entities) {
        call("deleteAll");
        for (Object o : entities) {
            events.removeIf(e -> e.equals(o));
        }
    }

    @Override
    public void deleteAll() {
        call("deleteAll");
        events.removeIf(Objects::nonNull);
    }

    @Override
    public <S extends Event> Optional<S> findOne(Example<S> example) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public <S extends Event> boolean exists(Example<S> example) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <S extends Event, R> R findBy(
            Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
        // TODO Auto-generated method stub
        return null;
    }
}