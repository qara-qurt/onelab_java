# 🚀 Restaurant Gateway CLI

## 📌 Описание
**Restaurant Gateway CLI** — это консольное приложение, которое предоставляет интерфейс для управления ресторанным сервисом через командную строку. Оно использует **Spring Shell**, интегрируется с **Kafka** и **Elasticsearch**, а также поддерживает выполнение команд для управления меню, блюдами, заказами и пользователями.

## 🛠️ Технологии
- **Spring Boot** (Spring Shell)
- **Apache Kafka** (для обработки событий)
- **Elasticsearch** (для поиска)

## 📥 Установка и запуск

### 📦 Запуск через Docker
```sh
docker-compose up -d
```

### 🔧 Запуск внутри контейнера
```sh
docker exec -it onelab_java-gateway_cli_service-1 sh
java -jar gateway_cli_service.jar
```

Если `Spring Shell` включен, ты попадешь в интерактивный режим командной строки CLI.

## 🔹 Основные команды

### 🟢 Общие команды
- `about` → Информация о приложении
- `commands` → Список всех команд
- `exit` → Выйти из CLI

### 🍽️ Меню
- `get-menu <ID>` → Просмотреть меню по ID
- `get-menus <page> <size>` → Список меню (пример: `get-menus 1 10`)
- `add-menu <Название> <DishID,...>` → Добавить меню (пример: `add-menu "Завтрак" dish1,dish2`)
- `remove-menu <ID>` → Удалить меню
- `add-dish-to-menu <MenuID> <DishID,...>` → Добавить блюда в меню
- `remove-dish-from-menu <MenuID> <DishID,...>` → Удалить блюда из меню

### 🍽️ Блюда
- `get-dishes <page> <size>` → Список блюд
- `add-dish <Название> <Описание> <Цена>` → Добавить блюдо (пример: `add-dish "Пицца" "Сырная" 1200.0`)
- `remove-dish <ID>` → Удалить блюдо
- `search-dishes <Название> <page> <size>` → Найти блюдо по названию

### 🛒 Заказы
- `create-order <UserID> <DishID,...>` → Создать заказ (пример: `create-order user1 dish1,dish2`)
- `get-orders <page> <size>` → Список заказов
- `get-order <ID>` → Просмотреть заказ по ID
- `get-orders-by-user <UserID> <page> <size>` → Заказы пользователя

### 👤 Пользователи
- `get-user <ID>` → Просмотреть пользователя
- `create-user --name <Имя> --surname <Фамилия> --username <Логин> --phone <Телефон> --password <Пароль>` → Создать пользователя
- `search-users <Имя> <page> <size>` → Найти пользователя
- `get-users <page> <size>` → Список пользователей
- `remove-user <ID>` → Удалить пользователя
- `fill-balance <UserID> <Сумма>` → Пополнить баланс

### 💰 Оплата
- `failed-payments` → Просмотр неудачных платежей
- `successful-payments` → Просмотр успешных платежей

### 🔍 Поиск
- `search-orders <Статус> <page> <size>` → Найти заказы по статусу (пример: `search-orders NEW 1 10`)

## 🔄 REST API
- Роли:
- `ADMIN `
- `USER` 
- `MANAGER`

```  
curl -X POST "http://localhost:8081/api/users/register" \
     -H "Content-Type: application/json" \
     -d '{
           "name": "Dias",
           "surname": "User",
           "username": "dias_user",
           "phone": "+77771112233",
           "password": "securepassword",
           "roles": ["ADMIN"]
         }'
```

```
curl -X POST "http://localhost:8081/api/users/login" \
     -H "Content-Type: application/json" \
     -d '{
           "username": "dias_user",
           "password": "securepassword"
         }'

```


```
curl -X GET "http://localhost:8081/api/users/1" \
     -H "Authorization: Bearer <token>"
```

```
curl -X POST "http://localhost:8081/api/users/1/fill-balance" \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer <token>" \
     -d '{"amount": 100.0}'

```

```
curl -X DELETE "http://localhost:8081/api/users/1" \
     -H "Authorization: Bearer <token>"

```

```
curl -X POST "http://localhost:8083/api/dishes" \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer <token>" \
     -d '{
           "name": "Pizza Margherita",
           "price": 10.99,
           "description": "Classic Italian pizza with tomatoes and mozzarella"
         }'

```

```
curl -X DELETE "http://localhost:8083/api/dishes/1" \
     -H "Authorization: Bearer <token>"

```

```
curl -X GET "http://localhost:8083/api/dishes?page=1&size=10" \
     -H "Authorization: Bearer <token>"

```

```
curl -X GET "http://localhost:8083/api/dishes/1" \
     -H "Authorization: Bearer <token>"

```

```
curl -X GET "http://localhost:8083/api/dishes/search?text=Pizza&page=1&size=10" \
     -H "Authorization: Bearer <token>"

```

```
curl -X POST "http://localhost:8083/api/menus" \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer <token>" \
     -d '{
           "name": "Lunch Menu",
           "dishIds": [1, 2, 3]
         }'

```

```
curl -X DELETE "http://localhost:8083/api/menus/1" \
     -H "Authorization: Bearer <token>"
```

```
curl -X GET "http://localhost:8083/api/menus/1" \
     -H "Authorization: Bearer <token>"
```

```
curl -X GET "http://localhost:8083/api/menus?page=1&size=10" \
     -H "Authorization: Bearer <token>"

```

```
curl -X POST "http://localhost:8083/api/orders" \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer <token>" \
     -d '{
           "customerId": 1,
           "dishIds": [1, 2, 3]
         }'

```

```
curl -X GET "http://localhost:8083/api/orders/1" \
     -H "Authorization: Bearer <token>"

```

```
curl -X GET "http://localhost:8083/api/orders/user/1?page=1&size=10" \
     -H "Authorization: Bearer <token>"
```

```
curl -X GET "http://localhost:8083/api/orders?page=1&size=10" \
     -H "Authorization: Bearer <token>"
```
## 🔄 Kafka Debugging

### 📋 Список топиков
```sh
kafka-topics --bootstrap-server kafka:9092 --list
```

### 🔍 Информация о топике
```sh
kafka-topics --bootstrap-server kafka:9092 --describe --topic dish.add
```

### 🎧 Чтение сообщений
```sh
kafka-console-consumer --bootstrap-server kafka:9092 --topic dish.add --from-beginning
```

### ✉️ Отправка сообщения
```sh
kafka-console-producer --broker-list kafka:9092 --topic dish.add
```
Введите сообщение и нажмите `Enter`:
```json
{"name": "Test Dish", "price": 500}
```

### 🎭 Проверка consumer-групп
```sh
kafka-consumer-groups --bootstrap-server kafka:9092 --list
```

```sh
kafka-consumer-groups --bootstrap-server kafka:9092 --describe --group test-group
```
