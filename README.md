:wave: Spring Boot project - Filmorate.

![ER diagram](ER_diagram.png)
### **Примеры запросов.**

Получение 10 популярных фильмов:

```
SELECT film_id,
       COUNT(film_id)
FROM like_films
GROUP BY film_id
ORDER BY COUNT(film_id) DESC 
LIMIT 10;
```

Получение всех фильмов:

```
SELECT *
FROM films;
```
