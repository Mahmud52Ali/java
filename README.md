# CourseHub — практическая работа 10

Шесть самостоятельных демонстраций `Future`, `CompletableFuture`, выбора executor, virtual threads и общего deadline. Автор: Горбачев Д.В.

Отчёт: [PDF](output/pdf/Горбачев_ДВ_Отчет_практическая_работа_10.pdf) · [DOCX](output/docx/Горбачев_ДВ_Отчет_практическая_работа_10.docx).

## Запуск

Нужны JDK 21+ и Gradle. Компиляция выполняется с `--release 21`; preview API не используются.

```sh
gradle clean run
```

## Задания

| № | Класс | Что проверяется |
|---|---|---|
| 1 | `Task01CallableFuture` | `Runnable` и `Callable`, checked exception, `Future.get(timeout)`, отмена через `cancel(true)`, interruption и закрытие ресурса |
| 2 | `Task02CompletablePipeline` | `supplyAsync` → `thenApply` → `thenAccept`, явные executor и плоский результат через `thenCompose` |
| 3 | `Task03AllOfResults` | Типизированные результаты в исходном порядке, fail-fast политика и частичный результат с ошибками |
| 4 | `Task04ExecutorChoice` | CPU pool и blocking I/O pool, starvation при одном worker и virtual-thread-per-task executor |
| 5 | `Task05ThreadExperiment` | 240 блокирующих задач, время, platform/virtual task threads и приблизительная разница heap |
| 6 | `Task06SharedDeadline` | Общий monotonic deadline, пересчёт остатка перед каждой стадией, отмена и ожидание завершения |

Эксперимент задания 5 предназначен для наблюдения, а не для полноценного benchmark. В задании 6 структурированная конкурентность объясняется концептуально; preview API не вызывается.
