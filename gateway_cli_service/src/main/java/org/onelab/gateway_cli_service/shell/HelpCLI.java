package org.onelab.gateway_cli_service.shell;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class HelpCLI {

    @ShellMethod(key = "commands", value = "Показать список команд")
    public String help() {
        return """
               📜 Доступные команды:
               
               🟢 Общие команды:
               - about                 → Информация о приложении
               - commands              → Список всех команд
               - exit                  → Выйти из CLI
               
               🍽️ Меню:
               - get-menu <ID>                        → Просмотреть меню по ID
               - get-menus <page> <size>             → Просмотреть список меню (пример: get-menus 1 10)
               - add-menu <Название> <DishID,...>    → Добавить меню с блюдами (пример: add-menu "Завтрак" dish1,dish2)
               - remove-menu <ID>                    → Удалить меню по ID
               - add-dish-to-menu <MenuID> <DishID,...>  → Добавить блюда в меню
               - remove-dish-from-menu <MenuID> <DishID,...>  → Удалить блюда из меню
               
               🍽️ Блюда:
               - get-dishes <page> <size>             → Просмотреть список блюд
               - add-dish <Название> <Описание> <Цена> → Добавить новое блюдо (пример: add-dish "Пицца" "Сырная" 1200.0)
               - remove-dish <ID>                     → Удалить блюдо по ID
               - search-dishes <Название> <page> <size> → Найти блюдо по названию
               
               🛒 Заказы:
               - create-order <UserID> <DishID,...>  → Создать заказ (пример: create-order user1 dish1,dish2)
               - get-orders <page> <size>           → Просмотреть все заказы
               - get-order <ID>                     → Просмотреть заказ по ID
               - get-orders-by-user <UserID> <page> <size>  → Получить заказы пользователя
               
               👤 Пользователи:
               - get-user <ID>                       → Просмотреть пользователя по ID
               - create-user --name <Имя> --surname <Фамилия> --username <Логин> --phone <Телефон> --password <Пароль>
                                                 → Создать нового пользователя
               - search-users <Имя> <page> <size>    → Найти пользователя по имени
               - get-users <page> <size>             → Просмотреть список пользователей
               - remove-user <ID>                    → Удалить пользователя по ID
               - fill-balance <UserID> <Сумма>       → Пополнить баланс пользователя
               
               💰 Оплата:
               - failed-payments                     → Просмотреть неудачные платежи
               - successful-payments                 → Просмотреть успешные платежи
               
               🔍 Поиск:
               - search-orders <Статус> <page> <size> → Найти заказы по статусу (пример: search-orders NEW 1 10)
               """;
    }

    @ShellMethod(key = "exit", value = "Выйти из CLI")
    public void exit() {
        System.out.println("👋 Завершение работы...");
        System.exit(0);
    }
}
