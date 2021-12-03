#Requirements to build/run
The following components need to be installed on the build/running system:
-JAVA 17
-Maven
-Docker Compose(optional)

# build/run project
1. build project using maven with target spring-boot:build-image
FROM CONSOLE: mvnw spring-boot:build-image OR mvn spring-boot:build-image

2. To run project Docker Compose or Maven can br used:
2.1 Docker Compose: docker-compose up --build
2.2 Maven: mvnw spring-boot:run OR mvn spring-boot:run

3. Other
- swagger url http://localhost:7070/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config
- debug port for Docker Compose: 7171

4. Author
- Alexander Yampolsky

P.s. Please reach out the author for any feedback or issues.
