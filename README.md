# CourseHub — практическая работа 3

Пятнадцать демонстраций интерфейсов, полиморфизма, наследования, композиции и проектирования правил допуска.

Отчёт: [PDF](output/pdf/Горбачев_ДВ_Отчет_практическая_работа_3.pdf) · [DOCX](output/docx/Горбачев_ДВ_Отчет_практическая_работа_3.docx).

## Запуск

Нужны JDK 21+ и Gradle. Preview API не используются.

```sh
gradle clean run
```

## Задания

| № | Демонстрация | Что показано |
|---|---|---|
| 1 | `Task01CourseFormatter` | Интерфейс, две реализации, выбор метода во время выполнения |
| 2 | `Task02DynamicDispatch` | Compile-time тип, overload resolution и override dispatch |
| 3 | `Task03FormatterArray` | Массив форматтеров без проверок типов |
| 4 | `Task04TemplateMethod` | Общая проверка адресата и `final send` с точкой расширения `doSend` |
| 5 | `Task05TypeChoices` | Выбор интерфейса, абстрактного класса и отсутствия лишнего типа |
| 6 | `Task06OverrideMistakes` | Сигнатуры, доступ, ковариантный результат и случайная перегрузка |
| 7 | `Task07NotificationDecorators` | Logging и metrics как декораторы в двух порядках |
| 8 | `Task08RepositoryLsp` | Разделение `CourseReader` и `CourseWriter` |
| 9 | `Task09EnrollmentStrategy` | Стратегии допуска, передаваемые в конструктор сервиса |
| 10 | `Task10DefaultConflict` | Явное разрешение конфликта default-методов |
| 11 | `Task11Equality` | Симметрия equality, strict class equality и композиция |
| 12 | `Task12SealedResult` | Закрытый результат и исчерпывающий pattern switch без `default` |
| 13 | `Task13RemoveTypeCode` | Реализации `NotificationSender` вместо строки и `switch` |
| 14 | `Task14CourseComposition` | Независимые компоненты формата курса и оплаты |
| 15 | `Task15EnrollmentEngine` | Пять правил, `allOf`/`anyOf` и типизированный результат |

Диаграмма зависимостей движка допуска находится в [docs/enrollment-engine.md](docs/enrollment-engine.md).
