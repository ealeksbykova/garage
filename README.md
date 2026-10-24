# Garage API — лабораторная работа по тестированию

Учебная копия https://github.com/ealeksbykova/garage,
commit `92a9cdadd73fc96eecbd40f38393a8d6163b6e5e`.
Используются PostgreSQL-миграции актуального master. Добавлены валидация, ответы 400/404/409,
83 автоматических теста и 7 нагрузочных сценариев в 15 логических модулях.

## Окружение

Нужны JDK 21, работающий Docker, Python 3.9+ для HTTP-нагрузки.
Gradle 9.0.0 загружается wrapper. Перед запуском убедитесь, что `JAVA_HOME`
указывает на JDK 21, а не на JDK 8. На компьютере, где выполнялась работа:

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home
export DOCKER_HOST="unix://$HOME/.colima/default/docker.sock"
export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock
```

На Docker Desktop последние две переменные обычно не требуются.

## Запуск лабораторного стенда

```bash
docker compose -f compose.lab.yaml up -d --wait
GARAGE_DB_URL=jdbc:postgresql://localhost:54329/garage \
GARAGE_DB_USER=garage GARAGE_DB_PASSWORD=garage_lab_local \
  bash gradlew bootRun
```

API: http://127.0.0.1:8080/api/vehicles.
Переменные GARAGE_DB_URL, GARAGE_DB_USER, GARAGE_DB_PASSWORD, GARAGE_PORT
переопределяют подключение. compose.lab.yaml содержит локальные учебные credentials,
порт PostgreSQL привязан к 127.0.0.1:54329. Данные compose хранятся в volume.
Начальные данные создаются Liquibase в новой PostgreSQL БД.

Исходный docker-compose.yml из master сохранён: PostgreSQL 16 Alpine на порту 5432,
БД и пользователь postgres. Без GARAGE_DB_* приложение использует этот адрес.
Лабораторный стенд запускается явно через -f compose.lab.yaml: закреплённый образ
PostgreSQL 17.11, другой порт и отдельный volume. Его результаты приведены в отчёте.
Для остановки лабораторной БД: docker compose -f compose.lab.yaml stop.

## Автоматические тесты

```bash
bash gradlew --no-daemon test jacocoTestReport bootJar
```

Тесты сами создают отдельный PostgreSQL-контейнер; compose не нужен.
Сервисные тесты используют Mockito и настоящие MapStruct mapper без БД.
HTTP-тесты используют настоящий Tomcat со случайным портом, реальные Spring,
Hibernate, Liquibase и PostgreSQL. Изоляция — новый контейнер на JVM запуска;
каждый изменяющий сценарий создаёт собственные записи с уникальными ключами.

Результаты: build/reports/tests/test/index.html;
покрытие: build/reports/jacoco/test/html/index.html.

## Бенчмарки и стресс

```bash
python3 lab/performance.py --jar build/libs/garage-1.0-SNAPSHOT.jar --mode benchmark --output ../evidence/benchmark
python3 lab/performance.py --jar build/libs/garage-1.0-SNAPSHOT.jar --mode stress --output ../evidence/stress
```

Каждый запуск создаёт собственную PostgreSQL БД и сервер только на loopback.
Скрипт завершает свои процессы и удаляет свой контейнер после измерения.
Исходные поминутные или производственные данные не используются.
Сохраняются все запросы, задержки, HTTP-коды, ошибки и агрегаты в JSON.
Интерпретация замеров и локальные критерии приведены в API_REQUIREMENTS.md.

## Каталог лабораторной работы

- lab/modules.json — ровно 15 логических модулей.
- lab/cases.json — все 90 тест-кейсов с ID, шагами и ожидаемыми результатами.
- API_REQUIREMENTS.md — учебный контракт API и критерии нагрузки.
- src/test/java/com/lab — 13 тестовых классов и общая инфраструктура.
- lab/performance.py — бенчмарки HTTP и ограниченный стресс-тест.

Контрольная версия использует PostgreSQL-миграции master и исправление последовательностей
после seed, но оригинальные Request DTO и не содержит ApiExceptionHandler.
Сравнение изолирует изменения валидации и HTTP-ошибок на одной схеме БД.
Её протокол хранится отдельно от финального прогона. Измерения SQLite не входят
в итоговые показатели лабораторного отчёта.

## Отчёт и зафиксированные результаты

- [Word-отчёт по лабораторной работе](lab/Garage_API_Отчёт_по_тестированию.docx) — 28 страниц, 23 листинга в основной части, все 15 модулей и 90 кейсов.
- [Результаты и ограничения измерений](lab/RESULTS.md).
- [Машиночитаемая сводка](lab/results.json) — статусы кейсов, покрытие и агрегаты нагрузки.

Миграции создают новую PostgreSQL БД и начальные записи. Автоматический перенос
произвольных пользовательских записей из существующего SQLite-файла не реализован.
