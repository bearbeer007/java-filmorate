package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {

    List<Event> getEventsByUserId(Long id);

    Event addEvent(Event event);
}