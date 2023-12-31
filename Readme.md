# Курсовой проект Skypro первый курс

# Структура проекта "Учет заработной платы сотрудников"
    - Интерактивный ввод/вывод через консоль (парсер консольных команд см. ниже)
    - ВСЕ изменения фиксируются в СУБД sqlite -> jdbc
    - Проект выполнен по шаблону DAO (data Access Object)
    - Модульное тестирование бизнес логики
## Бизнес модели:
    models.Emploee 	    справочник сотрудников в БД Emploees
	models.Deparment    справочник подразделений в БД Departments
	models.Position     справочник должности в БД Positions 
	models.Salaries	    модель для начислений заработной платы в БД Salaries
   models.Statistics     Моделль сводных данных по статистике начислений
## DAO управление бизнес логикой
    models.EmploeeDAO
    models.DepartmentDAO
    models.PositionDAO
    models.SalariesDAO
    devlAPI.APIyymm управление датами при начислении    

    models.SalariesCombine -> статистика по начислениям
    models.DAOcomnAPI вспомогательный API методы доступа к БД через скрипты sql 
    
    devlAPI.* -> API для разработки
    DevlInterface.* функциональные и другие интерфейсы 
    
    anyData/SQLscript/* скрипты sqlCode

## Парсер консольного ввода
    консольный ввод разделяется на две категории:
    - ввод команды -> отображение данных 
    - ввод команды -> интерактивный ввод данных в консоль
    - базовый класс  ConsRunComand
    - для интерактивного ввода ConsRunComand -> DAObaseConsComand 

    devlAPI.ConsRunComand -> методы и переменные
        - parserConsoleComand парсер консольного ввода 
        - ConsParserItem буфер консольного ввода
    models.
        - DAOabstract super class for 
            DeparmentDAO, EmploeeDAO, DAObaseConsComd
        - DAObaseConsComd super class for DAOEmploeeConsComd
        - DAOEmploeeConsComd используется в диалогом режиме ввода с консоли
        - DAOsalariesConsComand используется при ничислении заработной платы

## Начисление заработной платы
    - расчет статистики начислений через СУБД
    - одна операция на месяц
    - перерасчет заработной платы по ВСЕМ специальностям одновременно    
    - статистика по каждому сотруднику
    - статистика по ВСЕМ сотрудникам:
        последние начисления
        на заданный период
    - статистика за полгода
## Основные принципы изменений данных по сотрудникам
    - по каждому подразделению имеется допустимый набор специальностей
    - особые категории специальностей только для одного сотрудника:
        сотрудники администраций компании, 
        начОтдела, 
        зам. начОтдела
    - используется штатное расписание по каждой специальности
    - ввод нового сотрудника проходит проверку
        лимит по штатному расписанию
        принадлежность к особой категории специальности
    - изменение данных сотрудника
        если данные не изменялись операция отменяется
        изменение в БД только по тем полям, в которые были изменения
    - удаление сотрудника
        в БД запись не удаляется, а устанавливается маркер idUse = false
        (т.к. запись м/быть использована в начислениях)
    - в список сотрудников попадают записи которые имеют idUse = true
    - начисление заработной платы только по тем сотрудника у которые idUse = true

# Результаты вывода на консоль
демонстрация интерактивного взаимодействия в консольном режиме
```
Курсовой проект 1
Список допустимых команд: print comands
Выход из консольного режима quit

введите команду: print comands
print comands -> вывести список допустимых команд
print position -> штатное расписание организации
print department -> вывод справочника по отделам
print salaries last -> Вывод последних начислений заработной платы
print salaries list -> Вывод данных по начислениям за полгода
print emploee -> Вывод справочника по сотрудникам
help dao emploee -> шаблон команды для изм. справочника по сотрудникам
help dao salaries -> статистика по выплатам на заданный период

введите команду: help dao emploee
     dao emploee --cmd ins --id 0 -> ввод нового сотрудника
     dao emploee --cmd upd --id 2 -> изменение данных сотрудника
     dao emploee --cmd del --id 2 -> удаление сотрудника
     dao emploee --cmd pr --gr 2 -> список сотрудников по отделу

введите команду: dao emploee --cmd pr --gr 2
         Нач. отдела Рыбаков И.Ф.         67200
    Зам. нач. отдела Дмитриев Д.А.        61600
       Администратор Ларионов Л.П.        56000
       Администратор Волков В.П.          56000
       Администратор Орехов О.Н.          56000

введите команду: help dao salaries
dao salaries --cmd stat --yymm 2308 статистика на заданный период
dao salaries --cmd incr --pr 10 Перерасчет заработной платы на 10%
dao salaries --cmd add --yymm 2308 Начисление заработной платы на заданный период
dao salaries --cmd ls --yymm 2308 Список начислений на заданный период

введите команду: print salaries list
yymm:2308 sum:1006500 max:88000  min:27500  avg:59205,882
yymm:2307 sum:915000 max:80000  min:25000  avg:53823,529
yymm:2306 sum:915000 max:80000  min:25000  avg:53823,529
yymm:2305 sum:915000 max:80000  min:25000  avg:53823,529
yymm:2304 sum:915000 max:80000  min:25000  avg:53823,529
yymm:2303 sum:915000 max:80000  min:25000  avg:53823,529

введите команду: print salaries last
103 yymm:2308 emplId:2    88000 руб. Моисеев Н.И.
104 yymm:2308 emplId:3    77000 руб. Евсеев В.П.
105 yymm:2308 emplId:4    66000 руб. Авдеев В.В.
106 yymm:2308 emplId:5    60500 руб. Дьячков И.А.
107 yymm:2308 emplId:6    55000 руб. Журавлёв И.И.
108 yymm:2308 emplId:7    55000 руб. Соловьёв А.Н.
109 yymm:2308 emplId:8    55000 руб. Савельев А.А.
110 yymm:2308 emplId:9    66000 руб. Рыбаков И.Ф.
111 yymm:2308 emplId:10   60500 руб. Дмитриев Д.А.
112 yymm:2308 emplId:11   55000 руб. Ларионов Л.П.
113 yymm:2308 emplId:12   55000 руб. Волков В.П.
114 yymm:2308 emplId:13   55000 руб. Орехов О.Н.
115 yymm:2308 emplId:14   66000 руб. Морозов И.М.
116 yymm:2308 emplId:15   27500 руб. Котова В.Н.
117 yymm:2308 emplId:16   33000 руб. Ивлева Н.В.
118 yymm:2308 emplId:17   82500 руб. Петров П.П.
119 yymm:2308 emplId:18   49500 руб. Журавлева Н.К.

введите команду: dao salaries --cmd stat --yymm 2308
Расчетный период yymm:2308
	СреднНачисл:59205,882 ОбщСумм:1006500
	МаксЗарпл: 88000 МинЗарпл 27500
	Сотрудник с МаксНичисл Моисеев Н.И. 88000 руб
	Сотрудник с МинНачисл  Котова В.Н. 27500 руб

введите команду: dao salaries --cmd stat --yymm 2307
Расчетный период yymm:2307
	СреднНачисл:53823,529 ОбщСумм:915000
	МаксЗарпл: 80000 МинЗарпл 25000
	Сотрудник с МаксНичисл Моисеев Н.И. 80000 руб
	Сотрудник с МинНачисл  Котова В.Н. 25000 руб

введите команду: print salaries list
yymm:2308 sum:1006500 max:88000  min:27500  avg:59205,882
yymm:2307 sum:915000 max:80000  min:25000  avg:53823,529
yymm:2306 sum:915000 max:80000  min:25000  avg:53823,529
yymm:2305 sum:915000 max:80000  min:25000  avg:53823,529
yymm:2304 sum:915000 max:80000  min:25000  avg:53823,529
yymm:2303 sum:915000 max:80000  min:25000  avg:53823,529

введите команду: dao emploee --cmd ins --id 0
введите fullName: Антонов Н.В.
Справочник подразделений
id:1 Администрация
id:2 Аминистраторы
id:3 Разработка ПО
id:4 Бухгалтерия
id:5 Тех. отдел
введите подразделение: 1
  1  80000 Директор
  2  70000 Зам. директора
  8  45000 Секретарь
 выберите значение: 2
Есть сотрудник на этой должности (только один сотрудник)

введите команду: dao emploee --cmd ins --id 0
введите fullName: Антонов Н.В.
Справочник подразделений
id:1 Администрация
id:2 Аминистраторы
id:3 Разработка ПО
id:4 Бухгалтерия
id:5 Тех. отдел
введите подразделение: 2
  3  60000 Нач. отдела
  4  55000 Зам. нач. отдела
  6  50000 Администратор
 выберите значение: 6
ok

введите команду: dao emploee --cmd upd --id 19
	(Антонов Н.В.) изменение fullName: Антонов И.В.
  3  60000 Нач. отдела
  4  55000 Зам. нач. отдела
  6  50000 Администратор
 (6) выберите значение: 
id:19 отдел:Аминистраторы Администратор Антонов И.В.

введите команду: dao emploee --cmd upd --id 19
	(Антонов И.В.) изменение fullName: 
  3  60000 Нач. отдела
  4  55000 Зам. нач. отдела
  6  50000 Администратор
 (6) выберите значение: 4
Есть сотрудник на этой должности (только один сотрудник)

введите команду: dao emploee --cmd del --id 19
Подтвердите удаление сотрудника (Y-да  N-нет) Y
Выполнено удаление сотрудника

введите команду: dao salaries --cmd incr --pr 12
Перерасчет по заработной плате
   Перерасчет на 12 процентов
   Директор                   80000 -> 89600
   Зам. директора             70000 -> 78400
   Нач. отдела                60000 -> 67200
   Зам. нач. отдела           55000 -> 61600
   Программист                50000 -> 56000
   Администратор              50000 -> 56000
   Гл. бухгалтер              75000 -> 84000
   Секретарь                  45000 -> 50400
   Мастер чистоты             25000 -> 28000
   Бухгалтер                  30000 -> 33600
```   

