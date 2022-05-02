# Лабораторная работа №5
## Программирование
**Разработанная программа должна удовлетворять следующим требованиям:**
***
1. Класс, коллекцией экземпляров которого управляет программа, должен реализовывать сортировку по умолчанию.
2. Все требования к полям класса (указанные в виде комментариев) должны быть выполнены.
3. Для хранения необходимо использовать коллекцию типа `java.util.Stack`.
4. При запуске приложения коллекция должна автоматически заполняться значениями из файла.
5. Имя файла должно передаваться программе с помощью: **аргумент командной строки**.
6. Данные должны храниться в файле в формате `csv`.
7. Чтение данных из файла необходимо реализовать с помощью класса `java.io.BufferedInputStream`.
8. Запись данных в файл необходимо реализовать с помощью класса `java.io.BufferedWriter`.
9. Все классы в программе должны быть задокументированы в формате javadoc.
10. Программа должна корректно работать с неправильными данными (ошибки пользовательского ввода, отсутствие прав доступа к файлу и т.п.).

***
**В интерактивном режиме программа должна поддерживать выполнение следующих команд:**
1. `help` : вывести справку по доступным командам.
2. `info` : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.).
3. `show` : вывести в стандартный поток вывода все элементы коллекции в строковом представлении.
4. `add {element}` : добавить новый элемент в коллекцию.
5. `update id {element}` : обновить значение элемента коллекции, id которого равен заданному.
6. `remove_by_id id` : удалить элемент из коллекции по его id.
7. `clear` : очистить коллекцию.
8. `save` : сохранить коллекцию в файл.
9. `execute_script file_name` : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.
10. `exit` : завершить программу (без сохранения в файл).
11. `insert_at index {element}` : добавить новый элемент в заданную позицию.
12. `shuffle` : перемешать элементы коллекции в случайном порядке.
13. `reorder` : отсортировать коллекцию в порядке, обратном нынешнему.
14. `filter_by_best_album bestAlbum` : вывести элементы, значение поля bestAlbum которых равно заданному.
15. `filter_greater_than_best_album bestAlbum` : вывести элементы, значение поля bestAlbum которых больше заданного.
16. `print_field_ascending_number_of_participants` : вывести значения поля numberOfParticipants всех элементов в порядке возрастания.
***
**Формат ввода команд:**
1. Все аргументы команды, являющиеся стандартными типами данных (примитивные типы, классы-оболочки, String, классы для хранения дат), должны вводиться в той же строке, что и имя команды.
2. Все составные типы данных (объекты классов, хранящиеся в коллекции) должны вводиться по одному полю в строку.
3. При вводе составных типов данных пользователю должно показываться приглашение к вводу, содержащее имя поля (например, "Введите дату рождения:")
4. Если поле является enum'ом, то вводится имя одной из его констант (при этом список констант должен быть предварительно выведен).
5. При некорректном пользовательском вводе (введена строка, не являющаяся именем константы в enum'е; введена строка вместо числа; введённое число не входит в указанные границы и т.п.) должно быть показано сообщение об ошибке и предложено повторить ввод поля.
6. Для ввода значений null использовать пустую строку.
7. Поля с комментарием "Значение этого поля должно генерироваться автоматически" не должны вводиться пользователем вручную при добавлении.

# Лабораторная работа №6
## Программирование
**Доработать программу из лабораторной работы №5 следующим образом:**
***
1. Операции обработки объектов коллекции должны быть реализованы с помощью Stream API с использованием лямбда-выражений.
2. Объекты между клиентом и сервером должны передаваться в сериализованном виде.
3. Объекты в коллекции, передаваемой клиенту, должны быть отсортированы по размеру.
4. Клиент должен корректно обрабатывать временную недоступность сервера.
5. Обмен данными между клиентом и сервером должен осуществляться по протоколу UDP.
6. Для обмена данными на сервере необходимо использовать **датаграммы**.
7. Для обмена данными на клиенте необходимо использовать **сетевой канал**.
8. Сетевые каналы должны использоваться в неблокирующем режиме.
***
**Обязанности серверного приложения:**
1. Работа с файлом, хранящим коллекцию.
2. Управление коллекцией объектов.
3. Назначение автоматически генерируемых полей объектов в коллекции.
4. Ожидание подключений и запросов от клиента.
5. Обработка полученных запросов (команд).
6. Сохранение коллекции в файл при завершении работы приложения.
7. Сохранение коллекции в файл при исполнении специальной команды, доступной только серверу (клиент такую команду отправить не может).
***
**Серверное приложение должно состоять из следующих модулей (реализованных в виде одного или нескольких классов):**
1. Модуль приёма подключений.
2. Модуль чтения запроса.
3. Модуль обработки полученных команд.
4. Модуль отправки ответов клиенту.

Сервер должен работать в **однопоточном режиме**.
***
**Обязанности клиентского приложения:**
1. Чтение команд из консоли.
2. Валидация вводимых данных.
3. Сериализация введённой команды и её аргументов.
4. Отправка полученной команды и её аргументов на сервер.
5. Обработка ответа от сервера (вывод результата исполнения команды в консоль).
6. Команду `save` из клиентского приложения необходимо убрать.
7. Команда `exit` завершает работу клиентского приложения.

**Важно!** Команды и их аргументы должны представлять из себя объекты классов. Недопустим обмен "простыми" строками. Так, для команды add или её аналога необходимо сформировать объект, содержащий тип команды и объект, который должен храниться в вашей коллекции.
***


# Лабораторная работа №7
## Программирование
**Доработать программу из лабораторной работы №6 следующим образом:**
***
1. Организовать хранение коллекции в реляционной СУБД (PostgresQL). Убрать хранение коллекции в файле.
2. Для генерации поля id использовать средства базы данных (sequence).
3. Обновлять состояние коллекции в памяти только при успешном добавлении объекта в БД
4. Все команды получения данных должны работать с коллекцией в памяти, а не в БД
5. Организовать возможность регистрации и авторизации пользователей. У пользователя есть возможность указать пароль.
6. Пароли при хранении хэшировать алгоритмом SHA-512
7. Запретить выполнение команд не авторизованным пользователям.
8. При хранении объектов сохранять информацию о пользователе, который создал этот объект.
9. Пользователи должны иметь возможность просмотра всех объектов коллекции, но модифицировать могут только принадлежащие им.
10. Для идентификации пользователя отправлять логин и пароль с каждым запросом.
***
**Необходимо реализовать многопоточную обработку запросов.**
1.	Для многопоточного чтения запросов использовать Cached thread pool
2.	Для многопоточной обработки полученного запроса использовать создание нового потока (java.lang.Thread)
3.	Для многопоточной отправки ответа использовать создание нового потока (java.lang.Thread)
4.	Для синхронизации доступа к коллекции использовать синхронизацию чтения и записи с помощью java.util.concurrent.locks.ReentrantLock
***
Порядок выполнения работы:
1.	В качестве базы данных использовать *PostgreSQL*.
2.	Для подключения к БД на кафедральном сервере использовать хост `pg`, имя базы данных - `studs`, имя пользователя/пароль совпадают с таковыми для подключения к серверу.