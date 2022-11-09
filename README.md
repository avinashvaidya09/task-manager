# Project Set up and Details

At the end of this hands on excercise, you will accomplish below tasks - 

1. Created a CAP Java application to create user and assign roles. This project will be
further enhanced to add tasks for the users and remind him/her for completion of the tasks.
2. Integrated with HANA database (HANA HDI on SAP BTP). Test it from local.
3. Deployed the project on SAP BTP Cloud Foundary environment.
4. Enabled authentication and authorization using SAP BTP XSUAA service.
5. Test REST APIs through REST client (POSTMAN).
6. Dont forget to push your changes to GIT.

```
Task - 1 : Learn and create CAP JAVA project.
```
## Basic Set up for JAVA CAP application

There are 2 options to create a new CAP java project, I used the `Option 2` as I could name the project and 
package as per my requirements.

### Option 1

1. Open Business Application Studio.
2. On the "Get Started" page, select "Start from template".
3. From the templates, select "CAP Project".
4. Enter the CAP project details and select the run time. For this project, I have used JAVA as runtime.
5. Click finish. 
6. You should see a basic directory structure along with some template files.

### Option 2

1. From the menu, open terminal
2. Go inside project directory
3. Execute the command

    ``` 
    mvn -B archetype:generate -DarchetypeArtifactId=cds-services-archetype -DarchetypeGroupId=com.sap.cds \ -DarchetypeVersion=RELEASE \
    -DgroupId=com.sap.cap -DartifactId=task-manager -Dpackage=com.sap.cap.taskmanager 
    ```
4. This will initialize maven archtype and create a new project with name `task-mamager`.
5. The `db` folder contains data-model.cds where you define database entities/artifacts.
6. The `srv` folder contains the JAVA application files.

## Define Data Model
### In this section we will define a domain model which will be used by the service

1. Open `data-model.cds` file.
2. On the top your will define the namespace. This is the name with which the file will be imported in service.
3. This file also imports the package `@sap/cds/common` which imports common definitions, eg. cuid, managed which I haved used in the entity definition.
3. In this I have defined two entities (Roles and Users) with columns. This will be enhanced further to add third entity - Tasks.
4. In this I have tried to defined relationship between entities using `Association`
5. Relationships can be of two types
    * Associations - This relationship is loosely coupled. By this, I mean, if parent is deleted, it will not affect children.
    * Compositions - This relationship is tightly coupled. By this, I mean, parent is a container for all children. If parent is deleted, it will delete all the children.
6. To go to the definition of the CDS keywords, just press `CTRL` and hover on the keyword. This will take you to the reference material.

There is lot to understand and learn in the CDS Definition Language (CDL). For more details - https://cap.cloud.sap/docs/cds/cdl#entities 

## Define Admin Service
### In this section we will define the admin service to expose the data model created in data-model.cds

1. In the srv folder, create a file - `admin-service.cds`
2. Refer the content of the file in the project.
3. On the first line, it is using the data-model by importing the namespace
    ```
    using { sap.capire.taskmanager as db  } from '../db/data-model';
    ```

## Out Of The Box Persistence

#### OOTB, CAP JAVA SDK uses H2 database with no additional configuration to quickly test your application.  
#### But the data will be lost once you stop the application (spring container).
#### So, the next step is to integrate with HANA db.
#### Before that, just to ensure there are no issues in our journey so far, let's run and test our project.


## Test application locally

1. Open terminal and execite the below command
    ```
    mvn spring-boot:run
    ```
2. To test, open `requests.http` and test the application. Refer the `requests.http` file in the repo.
3. I have added few requests for reference. 
4. Stop your application after testing.

## Application/Project Reuse

1. To set up your application for reuse in future so that, the entities can be reused, perform the below tasks
    * Open `package.json` from the root project.
    * Change the name from maven provided name. 
      For example - I changed it from `task-manager-cds` -> `@sap/capire-task-manager`
    * Also, create `index.cds`. Refer the content in the repository.
```
Congratulations - Great work! You have completed Task - 1. Time for a BREAK.
```

```
Task - 2 : Integrate your project with HANA database on BTP (Cloud)
```
### In Task - 1, we created CAP project and tested it locally on Business Application Studio.
### In Task - 2, let us integrate with HANA database and publish the database artifacts to HANA database on BTP.
### This will make our project ready to be deployed on cloud.


## Set up and deploy HANA database artifacts

1. Open your application dev space in the sub account in which you wish to create HANA db service.
2. Go to -> `Cloud Foundry Environment` and get the `API endpoint`. Copy it to clipboard.
3. Open terminal and run below commands
    * cf api <cf api url which you copied in step 2>
    * cf login (This will ask your email and passwords for authentication)
4. Open `pom.xml` and add below dependency to enable hana database integration through CDS.
    ```
    <dependency>
         <groupId>com.sap.cds</groupId>
         <artifactId>cds-feature-hana</artifactId>
    </dependency>
    ```
5. Open `.cdsrc.json` and add the below hana deploy-format to the existing JSON. Do not overwrite the contents of the file.
    ```
    {
        "hana" : {
        "deploy-format": "hdbtable"
        }
    }
    ```
6. As part of this project, we will be relying on the HANA HDI container schema on shared SAP HANA database. So we do not need to provision HANA database       instance. For more details of what is HDI container refer the help document - [Link](https://help.sap.com/docs/SAP_HANA_PLATFORM/4505d0bdaf4948449b7f7379d24d0f0d/ebf0aa26958443f58f86b862056862d4.html?version=2.0.03&locale=en-US)

7. To create SAP HANA service instance on the cf environment and to push database artifacts, run the following command on terminal
    ```
    cds deploy --to hana:taskmanaher-hana --store-credentials
    ```

    * The above command will create HANA service instance with name `task-manager`
    * This will also create a schema inside SAP HANA HDI container.
    * Also, this will create `.hdbtable` and `.hdbview` files inside (`db/src/gen`) which will have the table and view definitions.
    * This will also add `default-env.json` file in your root directory. Open the file and you will notice a new service - `hana` with credentials. 
    * You can explore tables in `HANA Database Explorer` or by installing `SAP HANA Database Explorer` extension on BAS.

## Test application locally

1. Open application.yaml and update the property `config.activate.on-profile` from `default` to `cloud`
   Open terminal and execute the below command
   ```
   mvn spring-boot:run
   ```

   OR

2. Open terminal and execute the below command
    ```
    mvn spring-boot:run -Dspring-boot.run.profiles=cloud
    ```

3. The above command will ensure that cloud profile is picked up and not the default H2 database.
4. You should see the below line in the logs - `Registered DataSource 'taskmanaher-hana'`.
5. To test, open `requests.http` and test the application. 

```
Congratulations - Great work! You have completed Task - 2. Time for another short BREAK.
```
```
Task - 3 : Deploy application on SAP BTP cloud foundry environment.
```

### In Task - 2, we set up HANA HDI service and deployed artifacts on it.
### In Task - 3, let us deploy our application on SAP BTP cloud foundry environment and bind application to HANA HDI service.

## Create manifest file

1. Create new file `manifest.yml` in your root directory. 
2. Refer the `manifest.yml` file in the repository. Sample content is provided below - 
    ```
    ---
    applications:
        - name: task-manager
          path: srv/target/task-manager-exec.jar
          random-route: true
          services:
            - taskmanager-hana
    ```
    * Manifest.yml defines and describes your application
    * It also defines the services to be used. In this case we have given `taskmanager-hana`. The application will use this database to store data instead of in-memory H2 database.

## Enable application for cloud foundry and publish

1. Open pom.xml and add cloud foundry dependency
    ```
    <dependency>
    	<groupId>com.sap.cds</groupId>
		<artifactId>cds-starter-cloudfoundry</artifactId>
    	<version>${cds.services.version}</version>
	</dependency>
    ```

2. Open terminal and go to your root directory.

3. Build the application
    ```
    mvn clean install
    ```
    Ensure the build is SUCCESS

4. Push the application to cloud foundry
    ```
    cf push
    ```

```
Task - 4 : Enabled authentication and authorization using SAP BTP XSUAA service.
```

1. Create file `xs-security.json` in the root project directory with the help of following command
    ```
    cds compile srv/ --to xsuaa > xs-security.json
    ```
    This will create xs-security.json with a predefined schema.

2. Update the below 2 properties
    * xsappname - Give a desired name
    * role-collections.name - Give a desired name. This will create a role collection with name `task-manager_Administrators`

3. Go to SAP BTP subaccount -> security -> Add your user id to this role collection. if you are not admin, ask admin user to add your user id in the `task-manager_Administrators` role collection.

4. Add service `taskmanager-xsuaa` in `manifest.yml` as shown below
    ```
    ---
    applications:
        - name: task-manager
          path: srv/target/task-manager-exec.jar
          random-route: true
          services:
            - taskmanager-hana
            - taskmanager-xsuaa
    ```

5. Create instance of autorization service on SAP BTP with the help of below command - 
    ```
    cf create-service xsuaa application taskmanager-xsuaa -c xs-security.json
    ```

6. Build the application
    ```
    mvn clean install
    ```

7. Push the application to cloud foundry
    ```
    cf push
    ```
8. To get the details of the application execute the below command
    ```
    cf app task-manager
    ```
    The url of the application will be in front of `route`

```
Congratulations - Great work! Your application is CLOUD READY! 
```
```
Task - 5 : Test REST APIs through REST client (POSTMAN).
```
### In Task - 4, we deployed the CAP project on SAP BTP Cloud and also enabled security.
### In Task - 5, let us test it.

1. Open terminal and execute the below command
    ```
    cf env task-manager
    ```
    * The above command will provide you with the service binding details of your application
    * You will see `hana` and `xsuaa` under `VCAP_SERVICES`

2. Look for the service `xsuaa` -> `credentials`

3. Grab the below 3 attribute values
    * clientid
    * clientsecret
    * url

4. Open REST CLIENT - POSTMAN.

5. Create a collection. For example - SAP BTP

6. In the `Authorization` tab, enter the below details
    * Callback URL - `<This is your application url>`
    * Auth URL - `Authorization URL taken from point 3, appended by /oauth/authorize`
    * Access Token URL - `Authorization URL taken from point 3, appended by /oauth/token`
    * Client ID - `clientid from point 3`
    * Client Secret - `clientsecret from point 3`
    * Client Authentication - Select option -> `Send client credentials in body`

7. After entering all the above details, click on the button `Get New Access Token` and do not forget to save the token in the collection.

8. Now create a new POSTMAN request. Select `Authorization -> Type -> Inherit auth from parent`

9. You should see note - `This request is using OAuth 2.0 from collection SAP BTP`

```
Congratulations - You tested your cloud application successfully! 
```
```
Task - 6 : Don't loose your code and your efforts
```
### Let us learn how to integrate with GIT from SAP Business Application Studio

1. By default, BAS comes with source control extension.

2. Go to your github account and create a new repository with same name as your CAP project.

3. Run the below commands in sequence to add remote and set up git
    ```
    git remote add origin https://github.com/test.git
    git branch -M develop
    git commit -m "first draft"
    git push -u origin develop
    ```
```
Congratulations - You have completed your endeavor! 
```