<h1 align="center">
    vi
</h1>

**For Developers (with source code)**
1. Clone the repository.
2. Navigate to the project root directory.
3. Run the following command :
   <pre> mvn clean install  
    docker compose -f docker-compose-dev.yaml up --build</pre>
4. For detached mode :
   <pre> mvn clean install  
    docker compose -f docker-compose-dev.yaml up -d --build</pre>


**For Testing Only (with prebuilt image)**
1. Clone the repository.
2. Navigate to the project root directory.
3. Run the following command :
   <pre> docker compose up --pull always </pre>
4. For detached mode :
   <pre> docker compose up -d --pull always </pre>


**If you have a ~/.m2/settings.xml that points to your organisations maven repo, please run your application with the following command to use public maven repo settings file (since it is not using docker, redis won't be available) :**

```bash
./mvnw -s settings.xml spring-boot:run
```
**To run checkstyle & pmd please run the following command :**

```bash
./mvnw -s settings.xml checkstyle:check pmd:check
```
**To generate jacoco report, please run the following command :**

```bash
./mvnw -s settings.xml clean test jacoco:report
```
coverage report will be available at target/site/jacoco/index.html

#### Swagger UI
Please check at http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config
