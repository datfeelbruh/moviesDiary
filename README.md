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
### Общая структура
![alt-text](https://github.com/datfeelbruh/moviesDiary/blob/main/%D0%94%D0%B8%D0%B0%D0%B3%D1%80%D0%B0%D0%BC%D0%BC%D0%B0%20%D0%BA%D0%BE%D0%BD%D1%82%D0%B5%D0%B9%D0%BD%D0%B5%D1%80%D0%B0(1).drawio.png)
### Структура базы данных
![alt-text](https://github.com/datfeelbruh/moviesDiary/blob/main/%D1%81%D1%82%D1%80%D1%83%D0%BA%D1%82%D1%83%D1%80%D0%B0%20%D1%82%D0%B0%D0%B1%D0%BB%D0%B8%D1%86.png)
## Внутренний flow приложения
![alt-text](https://github.com/datfeelbruh/moviesDiary/blob/main/%D0%B4%D0%B8%D0%B0%D0%B3%D1%80%D0%B0%D0%BC%D0%B0%20flow.drawio.png)
### Реализованный функционал
Диаграмма выше лишь краткая инструкция как пользоваться приложением. С полной документацией можно ознакомиться по [ссылке](http://77.232.129.176/api-docs.html)
## Используемые технологии
* Spring
  + Data
  + Security
  + Web
  + Validation
  + Mail
* QueryDsl + Blazebit
* Jsonwebtoken
* Lombok
* Testcontainers
* PostgreSQL
* Liquibase
* Gradle
* Sonarqube
* Kinopoisk API

# Installation
1. git clone --recurse-submodules --depth 1 --no-single-branch https://github.com/datfeelbruh/moviesDiary.git
2. cd ./moviesDiary
3. git checkout dev
4. docker-compose up -d --build

# Development
Created with [spring](https://spring.io/) on [gradle](https://gradle.org/).
