## Setup of Scalar Box Event Log Fetcher App (Client Credentials Grant with Server Authentication)

### Overview

Operates through the Client Credentials Grant with Server Authentication Upon authorization, the app generates its own user, referred to as the Service Account user, with the username which looks like  `Your_AutomationUser_@boxdevedition.com.`.

This app serves a dual purpose:

1. **Events Retrieval:**
   - Fetching user events from Box.
   - Used when a user adds a file or folder to the audit set, where the app adds `Your_AutomationUser_@boxdevedition.com’(your service acc username)` as a collaborator to the respective file or folder.

### Steps to Create a Server Authentication App (Client Credentials)

To create a Server Authentication App using Client Credentials in the Box Developer Console, follow these steps:

#### Create New App:

1. Navigate to the Box Developer Console.
2. Provide a name for your app, such as "Scalar-Box-Event-Log-Fetcher-App", and add a description.
3. Select the purpose as "Automation" and click on "Create App".

#### Configuration:

1. After creating the app, go to the configurations tab.
2. Scroll down to the "App Access Level" section and select "App + Enterprise Access".
3. Application Scopes:
   - Define the required application scopes:
     - Write all files and store in Box.
     - Manage users.
     - Manage groups.
     - Manage enterprise properties.
     - Enable integrations.
4. OAuth 2.0 Credentials:
   - Scroll down to the OAuth 2.0 credentials section.
   - Save the Client ID and Client secret in a secure location for later use.
5. In Advanced Features, enable "Make API calls using the as-user header".    
6. CORS-Domain:
   - Mention you CORS domain (where your front end application is deployed).    

7. Authorization Tab:
   - Go to the Authorization tab.
   - Click on "Review and Submit" to initiate the review request process.

### Approval Process for both the Apps after creation

To approve the review request for both the Scalar Box Event Log Fetcher App (Server Authentication) and the Scalar Auditor for BOX (User OAuth 2.0 Authentication), follow these steps:

1. Navigate to the Box Admin Console.
2. Go to the "Apps" tab and select "Custom Apps Manager".
3. In the Server Authentication tab, click on the "Add App" button.
4. Paste the Client ID of the Scalar Box Event Log Fetcher App (Server Authentication) and click "Next".
5. Check the application scopes and approve the app.