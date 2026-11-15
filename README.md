# CourseHub — практическая работа 9

Шесть заданий по многопоточности и синхронизации. Автор: Горбачев Д.В.

Отчёт: [PDF](output/pdf/Горбачев_ДВ_Отчет_практическая_работа_9.pdf) · [DOCX](output/docx/Горбачев_ДВ_Отчет_практическая_работа_9.docx).

## Запуск

Нужны JDK 21+ и совместимый с ним Gradle. Проверено на Windows с JDK 26 и Gradle 9.4.1; компиляция выполняется с `--release 21`. JUnit 5.11.4 скачивается из Maven Central при первом запуске.

```sh
gradle clean test run
```

`run` запускает шесть демонстраций, `test` — JUnit. HTML-отчёт тестов: `build/reports/tests/test/index.html`.

Если java в PATH указывает на старую Java, задайте JAVA_HOME на установленный JDK 21+ до запуска Gradle. Пример для PowerShell:

```powershell
$env:JAVA_HOME = '<путь к JDK 21 или новее>'
gradle clean test run
```

## Задания

Пути ниже относительно `src/main/java/coursehub/`.

| № | Демонстрация и реализация | Что проверяется |
|---|---|---|
| 1 | `demo/Task01LostIncrements`, `counter/` | Пять запусков counter++, чтение/вычисление/запись, два исправления через synchronized |
| 2 | `demo/Task02StopVisibility`, `worker/` | Обычный флаг в отдельной JVM с лимитом времени, исправление через volatile |
| 3 | `demo/Task03SessionRegistry`, `session/` | Атомарное создание сессии через ConcurrentHashMap.computeIfAbsent |
| 4 | `demo/Task04ProducerConsumer`, `pipeline/` | Ограниченная очередь, несколько producers/consumers, Thread и ExecutorService |
| 5 | `demo/Task05DeterministicRace`, `counter/ControlledIncrementExperiment` | Два CyclicBarrier; JUnit — в src/test/java/coursehub/CounterTest.java |
| 6 | `demo/Task06CourseEnrollment`, `enrollment/` | Три атомарных варианта записи с ограничением мест |

Неизменяемые модели Course, CourseCode, CourseDuration, CourseLevel сохранены из работы 8. Запись студентов хранится отдельно от описания курса.

### Счётчик и флаг

counter++ — read-modify-write: два потока могут прочитать одно значение и записать одинаковый результат. Обычный stress-запуск зависит от планировщика и JIT и иногда даёт правильную сумму. Он не используется как единственная проверка гонки.

В ControlledIncrementExperiment оба потока сначала читают, проходят первый барьер, записывают и проходят второй барьер. За 100 раундов получаются ровно 100 единиц вместо 200. Поле специально volatile: видимость не делает составную операцию атомарной. Этот сценарий проверяется 25 раз.

Инвариант исправленного счётчика: в пределах диапазона int каждый инкремент увеличивает значение ровно на один, обновления не теряются. Монитор синхронизированного метода — this; у блока — закрытый monitor. Чтение защищено тем же монитором. Для короткого метода область блокировки одинакова; блок позволяет вынести другую работу за её пределы и скрыть монитор. Измерения скорости не проводятся.

Обычный флаг не имеет happens-before между записью и чтением. JVM может не перечитать его. Успешное завершение в одном запуске не доказывает корректность. Некорректный worker изолирован в дочерней JVM с ограниченным ожиданием и принудительным завершением процесса. Вычислительная нагрузка в probe нужна для исследования JIT, не служит синхронизацией. Thread.sleep не используется.

Запись volatile-флага happens-before последующего чтения этого поля. Итог исправленного worker читается после Future.get. volatile counter++ всё ещё может терять обновления.

### Реестр сессий

computeIfAbsent атомарен для ключа. Функция создания короткая, возвращает неизменяемую сессию и не выполняет внешних действий. containsKey с последующим put такой гарантии не даёт. remove(key, value) защищает новую сессию от запоздавшего выхода по старой. size() используется после завершения задач, а не для принятия конкурентного решения.

### Очередь и завершение

Демонстрация: три producers по 100 заявок, два consumers (один при одном доступном ядре), ArrayBlockingQueue вместимостью 8. put ждёт свободное место, take — заявку. Ограниченная очередь тормозит producers при отставании consumers и ограничивает буфер: это backpressure. Активного опроса нет.

После завершения всех producers координатор помещает по одному STOP на consumer. FIFO оставляет перед сигналами обычные заявки; затем координатор ждёт consumers. STOP не попадает в обработчик. Порядок завершения обработки несколькими consumers не гарантируется.

В варианте на executor producers и consumers используют разные пулы: заблокированный producer не вытесняет необходимого ему consumer из маленького общего пула. Задач подаётся ровно по числу потоков; буфер заявок остаётся ограниченным.

Выбран CPU-bound обработчик. Размер consumer-пула — min(availableProcessors, 2): здесь достаточно двух независимых обработчиков. На проверенной машине шесть логических процессоров, consumers два; producers имеют отдельные три потока. Для blocking-сценария стартовая оценка — Ncpu * (1 + время ожидания / время вычислений), затем её ограничивают числом соединений/ресурсов и проверяют измерениями. Это не готовый оптимальный размер.

Потоки называются request-producer-N и request-consumer-N, базовые — raw-producer-N и raw-consumer-N. Конвейер имеет общий deadline. При ошибке/отмене задачи прерываются. В finally выполняется shutdown, ожидание до секунды, при необходимости shutdownNow и ещё до секунды ожидания для каждого пула. Ошибка незавершения не скрывается; interrupt-флаг вызывающего потока сохраняется. Обработчик должен реагировать на interruption: Java не позволяет безопасно принудительно остановить произвольный игнорирующий его код.

### Инварианты записи на курс

0 <= students.size() <= capacity; ID уникальны и положительны. true означает одну новую запись. Дубликат или заполненный курс возвращает false без изменения состояния. Неверный ID вызывает исключение до изменения. Снимки студентов неизменяемы.

| Вариант | Атомарная граница | Цена и ограничения |
|---|---|---|
| LockedCourseEnrollment | Проверка и добавление под закрытым монитором | Coarse-grained lock; изменения курса последовательны; добавление ожидаемо O(1) |
| AtomicCourseEnrollment | CAS ссылки на неизменяемый набор | Размер и ID публикуются вместе; копирование O(n), при конфликте повторная проверка |
| ConcurrentMapCourseEnrollment | ConcurrentHashMap.compute по ID курса | Копирование O(n); compute может блокировать обновления, это не lock-free решение |

Отдельные AtomicInteger и concurrent set без общего протокола недостаточны: вместимость и уникальность — единый инвариант. CAS-вариант не публикует промежуточного резерва места при отказе.

## Проверки

60 JUnit-проверок с учётом повторов и параметров: счётчики — 27, worker — 2, сессии — 4, конвейер — 9, завершение executor — 3, запись на курс — 15. Все тестовые классы имеют @Timeout. Созданные executor освобождаются в finally; протоколом управляют latch/barrier и ограниченные ожидания, а не sleep.

Проверяются точные ID всех 300 заявок без дублей, пустой ввод, backpressure, исключение consumer и отмена, отсутствие оставшихся потоков, штатный и аварийный shutdown, interrupt-флаг. Для каждого варианта enroll проверяются 800 конкурентных вызовов на 30 мест, одинаковый студент, снимки, неверные ID и нулевая вместимость.

Результаты примеров гонок и visibility зависят от среды. Воспроизводимость гарантируется управляемым протоколом разложенного инкремента.

## Источники

- [Java Memory Model и happens-before](https://docs.oracle.com/javase/specs/jls/se21/html/jls-17.html#jls-17.4.5).
- [ConcurrentHashMap](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ConcurrentHashMap.html), [BlockingQueue](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/BlockingQueue.html).
- [ExecutorService](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ExecutorService.html), [AtomicReference](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/atomic/AtomicReference.html).
- [JUnit 5.11.4](https://docs.junit.org/5.11.4/user-guide/).

## Предыдущие работы

Автор: Горбачев Д.В.

| Работа | Ветка |
|---|---|
| Лабораторная №3 — коллекции и generics, задания 1–15 | [feat/lab_03](https://github.com/Mahmud52Ali/java/tree/feat/lab_03) |
| Лабораторная №7 — файловый ввод-вывод, задания 1–9 | [feat/pr_07](https://github.com/Mahmud52Ali/java/tree/feat/pr_07) · [отчёт PDF](https://github.com/Mahmud52Ali/java/blob/feat/pr_07/output/pdf/%D0%93%D0%BE%D1%80%D0%B1%D0%B0%D1%87%D0%B5%D0%B2_%D0%94%D0%92_%D0%9E%D1%82%D1%87%D0%B5%D1%82_%D0%BF%D1%80%D0%B0%D0%BA%D1%82%D0%B8%D1%87%D0%B5%D1%81%D0%BA%D0%B0%D1%8F_%D1%80%D0%B0%D0%B1%D0%BE%D1%82%D0%B0_7.pdf) |
| Лабораторная №8 — Stream API, задания 1–7 | [feat/pr_08](https://github.com/Mahmud52Ali/java/tree/feat/pr_08) · [отчёт PDF](https://github.com/Mahmud52Ali/java/blob/feat/pr_08/output/pdf/%D0%93%D0%BE%D1%80%D0%B1%D0%B0%D1%87%D0%B5%D0%B2_%D0%94%D0%92_%D0%9E%D1%82%D1%87%D0%B5%D1%82_%D0%BF%D1%80%D0%B0%D0%BA%D1%82%D0%B8%D1%87%D0%B5%D1%81%D0%BA%D0%B0%D1%8F_%D1%80%D0%B0%D0%B1%D0%BE%D1%82%D0%B0_8.pdf) |

Код, инструкции запуска и отчёт находятся в ветке соответствующей лабораторной.
