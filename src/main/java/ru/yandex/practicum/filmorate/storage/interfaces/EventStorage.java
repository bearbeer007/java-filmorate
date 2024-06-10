package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.util.List;

public interface EventStorage {
    List<Event> getEventsByUserId(Long id);

    Event addEvent(Event event);

    Event addEvent(Long userId, Long entityId, EventType eventType, Operation operation);
}