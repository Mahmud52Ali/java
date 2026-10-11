# CourseHub — лабораторная 3

Реализованы задания 1–15 из `3.txt`. JDK 21+, без Maven и внешних библиотек.

## Запуск

Из корня проекта:

```sh
mkdir -p out
find src/main/java -name '*.java' > out/sources.txt
javac --release 21 -encoding UTF-8 -d out @out/sources.txt
java -cp out coursehub.Main
```

Все задания запускаются по порядку; перед каждым выводится номер. Ожидаемые учебные исключения перехватываются внутри примеров.

## Где проверять

Пути относительно `src/main/java/coursehub/`.

| № | Файлы | Проверка |
|---|---|---|
| 1–5 | `demo/Task01Lists` … `Task05Removal` | Списки, уникальность, индекс, FIFO, удаление при обходе |
| 6–9 | `generics/`, `demo/Task06GenericResult` … `Task09Pecs` | Generic-тип и методы, Comparable, PECS |
| 10 | `model/Course`, `demo/Task10KeyContract` | Стабильный ключ по ID и намеренно сломанный хеш |
| 11 | `demo/Task11CollectionChoice` | Шесть сценариев выбора коллекций |
| 12 | `repository/`, `demo/Task12Repository` | CRUD, повторный save, null и снимок коллекции |
| 13 | `index/TagIndex`, `demo/Task13TagIndex` | Добавление, удаление, пересечение тегов, удаление пустых наборов |
| 14 | `cache/LruCache`, `demo/Task14LruCache` | Недавность, вытеснение, неизменяемые ключи |
| 15 | `graph/`, `demo/Task15Prerequisites` | Порядок prerequisites, замкнутый путь цикла, неизвестная вершина |

Краткие объяснения и сложности: [docs/explanations.md](docs/explanations.md).
Ошибки компиляции для №6 и №9 показаны одним блоком комментария в соответствующих примерах: проверять строки отдельно.

## Отчёт

Автор: Горбачев Д.В.

- [Word](output/docx/Горбачев_ДВ_Отчет_практическая_работа_5.docx)
- [PDF](output/pdf/Горбачев_ДВ_Отчет_практическая_работа_5.pdf)
