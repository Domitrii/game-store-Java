http://localhost:8080/games : Всі 8 ігор (JSON)
http://localhost:8080/games/1 : Тільки Witcher 3
http://localhost:8080/games/genre/RPG : Всі RPG
http://localhost:8080/games/genre/SHOOTER : Всі шутери



# Реєстрація нового юзера
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@gmail.com","name":"Dmitro","passwordHash":"1234"}'

# Покупка гри (userId з відповіді реєстрації, gameId = 1..8)
curl -X POST "http://localhost:8080/purchase?userId=<твій-uuid>&gameId=2"


# тестовий аккаунт
 { "email": "test@example.com", "password": "test123" }

# Старт
docker compose up --build