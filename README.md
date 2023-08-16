[![Quality Gate Status](http://localhost:9000/api/project_badges/measure?project=moviesDiary&metric=alert_status&token=sqb_f8d1638b4b44d93ae3dc4512267ca13cf4bd776c)](http://localhost:9000/dashboard?id=moviesDiary)
[![Coverage](http://localhost:9000/api/project_badges/measure?project=moviesDiary&metric=coverage&token=sqb_f8d1638b4b44d93ae3dc4512267ca13cf4bd776c)](http://localhost:9000/dashboard?id=moviesDiary)
[![Maintainability Rating](http://localhost:9000/api/project_badges/measure?project=moviesDiary&metric=sqale_rating&token=sqb_f8d1638b4b44d93ae3dc4512267ca13cf4bd776c)](http://localhost:9000/dashboard?id=moviesDiary)
# MovieDiary
## Что это за проект?
Мой pet-project созданный самостоятельно по чтобы закрепить знания полученные при обучении по курсу Java 
на [Hexlet](https://ru.hexlet.io/).
Целью проекта было создание полноценного сервера предоставляющего REST API для ведения 
дневника просмотренных фильмов. Конечной целью проекта было создание полноценного приложения с frontend и backend 
частью.  
Frontend часть полностью написана [моим товарищем](https://github.com/antarktidi4).  
С кодом фронтенда можно ознакомиться перейдя в сабмодуль frontend. Текущая версия проекта успешно собрана в docker
контейнеры и выложена на хостинг, подключен Let's Encrypt сертификат. Приобретено доменное имя(в дальнейшем изменится) :blush:  
С рабочим функционалом можно ознакомиться на сайте [https://киноговно.рф](https://xn--b1abohqcebc.xn--p1ai/)
## Структура проекта
![alt-text](https://github.com/datfeelbruh/moviesDiary/blob/main/%D0%94%D0%B8%D0%B0%D0%B3%D1%80%D0%B0%D0%BC%D0%BC%D0%B0%20%D0%BA%D0%BE%D0%BD%D1%82%D0%B5%D0%B9%D0%BD%D0%B5%D1%80%D0%B0(1).drawio.png)

# Installation
1. git clone --recurse-submodules --depth 1 https://github.com/datfeelbruh/moviesDiary.git
2. cd ./moviesDiary
3. docker-compose up -d --build

# Development
Created with [spring](https://spring.io/) on [gradle](https://gradle.org/).
