#Wishlists App
[![Java CI with Maven](https://github.com/matteogoldin/Wishlist/actions/workflows/maven_build_linux.yml/badge.svg)](https://github.com/matteogoldin/Wishlist/actions/workflows/maven_build_linux.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=matteogoldin_Wishlist&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=matteogoldin_Wishlist)
[![Coverage Status](https://coveralls.io/repos/github/matteogoldin/Wishlist/badge.svg?branch=main)](https://coveralls.io/github/matteogoldin/Wishlist?branch=main)

##Requirements
- JDK11 or JDK17
- Eclipse IDE
- Maven
- Docker

##Usage
- Import the Git repository in Eclipse IDE
- Build the project with Maven
  ```bash
  mvn clean install
  ```
- Create a Docker container with an instance of MySQL runninng on it
  ```bash
  docker run --name wishlist-app-container -d -p 3309:3306 -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=wishlists-schema -e MYSQL_USER=java-client -e MYSQL_PASSWORD=password --restart unless-stopped -v mysql:/var/lib/mysql mysql:8.0.33
  ```
- Launch the application
  ```bash
  java -jar <full-path-project-root-directory>\target\wishlists-0.0.1-jar-with-dependencies.jar
  ```
