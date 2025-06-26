**For Developers (with source code)**
1. Clone the repository.
2. Navigate to the project root directory.
3. Run the following command :
   <pre> mvn clean install  
    docker compose -f docker-compose-dev.yaml up --build</pre>
4. For detached Mode :
   <pre> mvn clean install  
    docker compose -f docker-compose-dev.yaml up -d --build</pre>


**For Testing Only (with prebuilt image)**
1. Clone the repository.
2. Navigate to the project root directory.
3. Run the following command :
   <pre> docker compose up --pull always </pre>
4. For detached Mode :
   <pre> docker compose up -d --pull always </pre>
   
   
