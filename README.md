# Microsoft Graph API Implementation Test
This repository contains a Java application that implements some functionalities of the Microsoft Graph API.

## Running the App
To run the application, you will need to create a file named **oAuth.properties** in the **src/main/resources/graphtutorial** directory of the project. This file should contain the following configurations for your Azure application:

```yaml
{
    app.clientId=your_client_id
    app.tenantId=your_tenant_id
    app.graphUserScopes=User.Read
    app.email=your_email_address
}
```
    
* **app.clientId**= The client ID of your Azure application.
* **app.tenantId**= The tenant ID of your Azure application.
* **app.graphUserScopes**= The Microsoft Graph API permissions that your application requires.
* **app.email**= (Optional) The email address of the user that the application should authenticate as.


## Usage
After configuring the **oAuth.properties** file, you can run the application using the **java -jar** command:

```cmd
{
    java -jar graphtutorial.jar
}
```

**Note**: Please replace the placeholders **your_client_id**, **your_tenant_id**, **User.Read**, and **your_email_address** with your own values.
