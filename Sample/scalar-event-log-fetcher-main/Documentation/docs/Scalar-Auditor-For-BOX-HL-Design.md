### Scalar Auditor for BOX Overview

Scalar Auditor for BOX is a tool designed to facilitate external auditing of cloud storage services, specifically targeting Box.com. It addresses the challenges faced by organizations requiring external audit logs for compliance and security purposes. The application aims to provide the following features:

1. **Log Storage**: Capture and store operation logs from client storage services (e.g., Box, Google Drive) in an external database. This ensures that audit logs are retained for the required duration, even if the retention period of the cloud vendor's logs is insufficient.

2. **Audit Trail**: Record various file operations to enable external auditors to verify the integrity and security of files. This includes tracking:
   - Date and time of file creation
   - Date and time of file updates
   - Date and time of file deletion
   - Location of copied files

3. **Selective Sharing**: Enable users to create a list of folders and files to be audited and share only those specific items with external auditors. This ensures that auditors have access only to the necessary files and folders, maintaining confidentiality and minimizing exposure of sensitive information.

#### Challenges Addressed
- **Lack of External Audit Logs**: Many cloud storage providers, including BOX, do not provide audit logs for external users, which are often required for compliance and regulatory purposes.
- **Inadequate Partial Sharing**: Existing sharing mechanisms may not support partial sharing, meaning that if a folder is shared with an external auditor, they have access to all contents within the folder, including sensitive files. Scalar Auditor for BOX addresses this issue by enabling selective sharing of files and folders.

By providing comprehensive audit trail capabilities and selective sharing features, Scalar Auditor for BOX ensures that organizations can meet their compliance and security requirements while maintaining control over their data shared with external auditors.



### Functional Blocks of Scalar Auditor for BOX

**1. BOX Event Log Fetching and Storing**

This block is responsible for fetching user events from the BOX application and storing them in the database. The events fetched include:

- ITEM_CREATE
- ITEM_UPLOAD
- ITEM_MOVE
- ITEM_COPY
- ITEM_TRASH
- ITEM_UNDELETE_VIA_TRASH
- ITEM_RENAME
- ITEM_MODIFY

In Release 1.0, the event types to be fetched are not configurable. The event data is stored in ScalarDB and ScalarDL.

**2. Querying BOX Events**

This block enables users to query processed information from the stored event log data. The functionalities implemented include:

- Viewing BOX events by selecting a specific date range
- Viewing BOX events done by a specific user
- Viewing BOX events by specific event type

Additionally, this module provides information such as files with the same hash and versions of files, which can be derived from the event logs stored in the database.

**3. User Role Management**

This block manages user roles within the Scalar Auditor for BOX application. There are two types of application users:

- Users with BOX accounts
- External Users, who are external auditors

Roles implemented in Release 1 include:

- Audit Admin: Administrators of the organization, responsible for managing user roles and permissions.
- General User (GEN User): Regular users within the organization.

Upon installation or registration of the Scalar Auditor for BOX application, an Administrator is assigned to the organization, who is considered an Audit Admin by default. Other users are initially saved as GEN Users. However, an Audit Admin user can modify the roles of any user and assign additional roles.

Possible roles for any Organization User include:

- AUDIT_ADMIN
- GEN_USER
- AUDIT_ADMIN + GEN_USER

Here's a summary of role operations within the Scalar Auditor for BOX application:

| Previous Role           | Operation             | Role after Operation   |
|-------------------------|-----------------------|------------------------|
| AUDIT_ADMIN             | Change (Edit) Role    | GEN_USER               |
| GEN_USER                | Change (Edit) Role    | AUDIT_ADMIN            |
| AUDIT_ADMIN             | Add Role              | AUDIT_ADMIN + GEN_USER |
| GEN_USER                | Add Role              | GEN_USER + AUDIT_ADMIN |
| AUDIT_ADMIN + GEN_USER  | Remove role of AUDIT_ADMIN | GEN_USER           |
| AUDIT_ADMIN + GEN_USER  | Remove role of GEN_USER    | AUDIT_ADMIN        |
	


## Audit Set Management

An Audit Set is a group of files and folders selected for monitoring purposes. It is assigned a name and description for convenience. The Audit Set management module implements the following functionalities:

- **Create an Audit Set**: Allows the creation of a new Audit Set with a specified name and description.
  
- **Delete an Audit Set**: Enables the deletion of an existing Audit Set.

- **Add Collaborators to an Audit Set**: Collaborators can be added to an Audit Set with varying levels of access:
  - Owner: Full control over the Audit Set, including management and collaboration privileges.
  - Co-Owner: Similar to an owner but with slightly restricted permissions.
  - Member: Users with read and limited edit permissions.
  - Reviewer: Users with read-only access for reviewing purposes.

## Auditor and Audit Group Management

The user management module includes functionalities for managing External Auditors and Audit Groups. External Auditors are users who utilize the web UI of the application rather than the BOX integrations menu. Key functionalities include:

- **Create External Users (External Auditors)**: Allows the creation of users outside the system, who authenticate using their email ID and specified password.
  
- **Delete, Edit, or Update Users**: Provides administrative capabilities to manage user accounts.
  
- **Delete or Edit Groups**: Enables the management of Audit Groups, allowing for the addition or removal of members and adjustment of group settings.

## Audit Data Management and Display

External Auditors require various data for auditing files, including:

- File details.
- Folder details.
- List of file copies (files with the same hash).
- List of file versions.
- Events performed on files in various forms.

This data is processed by the application based on stored event logs or retrieved from the BOX application.

## Validation Functionality

Files or entire Audit Sets can be validated for tampering using the ScalarDL Validation functionality. Validation for an entire Audit Set, containing numerous files and folders, may take a longer time and is suggested for occasional use.

## Managing Addition of Items to Audit Set

Files or folders can be added to an Audit Set for monitoring purposes. This operation is facilitated through the BOX integration menu. Specifically:

- Files are added directly to the Audit Set.
- For folders, the selection of files and subfolders allowed for external auditors is performed at the time of adding to the Audit Set.




●Here are some use cases for the 'Scalar Auditor for BOX' application, categorized by the types of users:

### Organization Users (with BOX account)

#### Audit_Admin
1. **Manage User Roles**: Audit_Admin can assign roles such as Audit_Admin or General_User to organization users.
2. **Manage External User Accounts**: Audit_Admin can create, edit, or delete external user accounts for External Auditors.
3. **Manage External Auditors' Groups**: Audit_Admin can create, edit, or delete groups for External Auditors, facilitating easier management of permissions and access.
4. **Audit Set Creation and Management**: Audit_Admin can create, delete, or modify Audit Sets, organizing files and folders for monitoring purposes.

#### General_User
1. **Add Files to Audit Set**: General_User can add their own files or shared files to an Audit Set, contributing to the pool of monitored documents.
2. **View File Details**: General_User can view details of files within the Audit Set, providing transparency and awareness.
3. **View Audit Operations**: General_User can see the operations performed by External Auditors on the files within the Audit Set, ensuring accountability and oversight.

### External Auditors (Users from External Organizations)

1. **Perform File Audits**: External Auditors can audit files assigned to them, which involves:
   - Checking event details to ensure compliance and security.
   - Reviewing file copies and versions for accuracy and integrity.
   - Previewing files to understand their contents and context.
   
These use cases cover the primary functionalities and interactions of different user roles within the 'Scalar Auditor for BOX' application, ensuring effective auditing and management of files and folders.


Below are the various Use Cases based on the type of user.

Certainly! Below is the GitHub document with the provided use cases formatted in a table:

| Sr No | Use Case | Description |
|-------|----------|-------------|
| 1     | Access scalar Box integration application (name- Scalar Auditor for BOX') from Box integrations menu by selecting file or folder in BOX | A user (Audit Admin or Gen user) can see the Scalar Box integration tool menu option when he right clicks on any file or folder in Box. Upon clicking that menu, a file view screen or a folder view screen is displayed properly. |
| 2     | View file properties using Scalar BOX tool | Select Scalar Box integration tool after right-clicking on the file in BOX. The File Details View screen is displayed, showing file properties such as name, file path, Owner name, created date, modified date, modified by, SHA1 hash. |
| 3     | View file versions | A list of all the versions of a selected file is displayed. |
| 4     | View file copies | A list of all files with the same hash is displayed. |
| 5     | Add file to an audit set for Audit Purpose | Audit admin or General user can select a particular file box and right-click and use Scalar Audit tool to add that file to a selected Audit set from the system. |
| 6     | View event history on a particular file | Audit admin or General user can select a particular file box and right-click and use Scalar Audit tool to view the event history of that file. User can filter the event history by date range, by user, by event type. |
| 7     | View folder details using Scalar box tool | Folder properties such as name, path, created at, modified at, and size are displayed on the screen. |
| 8     | Add folder to an audit set by selecting allowed subfolders and files from that folder | A user will select an Audit set in which folder is to be added. A User can select which folders or files will be allowed and add that to a selected Audit Set. The folder that is selected from BOX is considered the root folder in the tree view. All the items are initially seen as unchecked. A User can select a folder to be allowed for Audit purposes by checking inside the checkbox. Once selected the folder and all the items below it are checked. This is a folder that is selected and is indicated with a tick mark. If the user unselects some of the items below a folder, it is considered partially selected and is indicated with a dark rectangle inside the checkbox. If the user does not check any folder, it remains unchecked. Thus there are three states for any folder (having some items below it): - Unselected - Partially selected - Selected A file has two states: - Unselected - Selected After selecting the items to be allowed, the user can add the root folder to a selected audit set. |
| 9     | As a user of the registered organization, the user should be able to sign up to the Scalar Box tool using BOX credentials | A user is an organization user having a BOX account. The user can sign in to the application by clicking 'sign in using BOX'. User will enter the BOX user id as credentials. Once the user id is entered, the sign-in button is enabled. |
| 10    | As a user of the registered organization, the user should be able to sign in to the Scalar Box Integration tool using BOX credentials | A user is an organization user having a BOX account. The user can log in to the application by clicking 'Login using BOX'. If the user is logging in for the first time, the user will be redirected to the BOX login menu. The user will enter the BOX credentials and then will be redirected to the main Menu Page. |
| 11    | As an external auditor, the user should be able to sign in to the Scalar Box Integration tool using user id and password | An external user (Not from the registered organization) can log in to the system by entering user id and password. (The user is created by Audit Admin, and user id and password are assigned). |
| 13    | A User can logout from any screen | There is a logout option on every screen, and a user can logout from any screen by clicking that. The sign on the screen is shown after that. |
| 14    | Create external user | A user having Audit Admin as a role can create a user as an External Auditor. The user name, organization name, login id (mail ID), and password information are entered while creating the user. After the successful addition of this user to the system, the external auditor can log in to the app using the credentials entered by the Audit Admin (the user who created the External user). |
| 15    | Delete external user | An user of type external Auditor can be deleted by the Audit Admin from the system. 1. The user when deleted should not be able to login to the system. 2. The user when deleted should be seen in the External Auditor list of any of the Audit Sets. 3. The user should not appear in the list when a new Audit Set is being created. |
| 16    | Edit external user details | Audit admin can edit the external user information such as name, mail id, organization name, and password. |
| 17    | View list of all users and their role | Audit admin can see a list of all users including General User, Audit Admin, and External user. The information such as user name. |
| 18    | Add user role | A user with Audit Admin role can add a role to another user in the following way: - Can add a role as General User to a user having a role as Audit Admin only. - Can add a role as Audit Admin to a user having a role as General User only. |
| 19    | Edit user role | A user with Audit Admin role can edit a role of a user in the following way: - Can change the role from General User to Audit Admin only and vice versa. - Can remove either of the roles if the user has two roles such as Audit Admin or General User. |


Certainly! Here's the GitHub document with the user roles and their corresponding operations formatted in a table:

---

## User Roles and Operations

Scalar Auditor for BOX application has primarily three roles:

- **Audit_Admin**
- **Gen_User**
- **External Auditor**

| Description | Audit Admin | Gen User | External Auditor |
|-------------|-------------|----------|------------------|
| Use app through BOX integration menu | Y | Y | N |
| Login to WEB UI | Y | Y | Y |
| Add file that is owned or shared to Audit Set for Auditing | Y | Y | N |
| Add folder that is owned or shared to Audit Set for Auditing | Y | Y | N |
| Add / Update User Roles | Y | N | N |
| Create an Audit Set | Y | N | N |
| Update Audit Set details | Y | N | N |
| Delete an Audit Set | Y | N | N |
| Create external auditor | Y | N | N |
| Update external auditor details | Y | N | N |
| Create an audit group | Y | N | N |
| Update an audit group | Y | N | N |
| Delete external Auditor or group | Y | N | N |
| Assign external Auditor to an Audit Set | Y | N | N |
| Change Audit Set Owner | Y | N | N |
| View file event history | Y | Y | Y |
| View Audit Access logs | N | Y | N |
| Preview a file | N | Y | Y |
| Download a file | N | Y | Y |
| View file details of own or shared file | Y | Y | Not Applicable |
| View file details of file assigned for Audit | Not Applicable | Not Applicable | Y |
| View all event History | Y | N | N |


	

5.	User Functionality Flow                       
○	Using BOX Integrations Menu
 
![Callback Configuration](assets/images/boxIntegrationMenu.png)


○	Using WEB UI
 ![Callback Configuration](assets/images/webui.png)


	
○	Using WEB UI  for external auditor  
 ![Callback Configuration](assets/images/webuiexteernalAuditor.png)
 


●	6. Architecture 
The overall architecture of this application can be viewed as follows.                                                
    
○	Application architecture diagram   
 
 
  ![Callback Configuration](assets/images/architecture.png)
The backend application integrates with BOX by actually using two BOX applications. 
The BOX App 1 uses Client Credentials as authentication method and is used for fetching event logs.
The BOX App 1 uses OAuth 2.0 and is integrated with BOX for all the other functionalities implemented.
The frontend application uses React JS.

Got it! Here's the GitHub document with the list of REST APIs used for the application and their respective functionalities formatted in a table:

---

## Rest APIs Implemented

Here's the list of REST APIs implemented:

| No | API Name | Key Functionality |
|----|----------|-------------------|
| 1  | createUser | To create an External Auditor in the system. |
| 2  | getManagedUser | Get a list of users in the system for a particular organization. |
| 3  | deleteUser | Delete user from the system. |
| 4  | login | To login to the application WEB UI. |
| 5  | editUser | To update user information. |
| 6  | updateUserRole | To change or add user role. |
| 7  | submitToken | For Token implementation. |
| 8  | userSignIn | For sign in functionality. |
| 9  | getServiceToken | To get a service token from the system. |
| 10 | getListOfExternalAuditors | To get a list of external auditors from the system. |
| 11 | getOrgList | Get a list of organizations from the system. This is the list of external organizations from which the Auditors are associated for Audit purpose. |
| 12 | getIntegratedItemDetails | Get the information related to integrated items from the system. |
| 13 | getRootFolder | Get the root folder from the system. |
| 14 | getItemList | To get a list of items from the system using a box connection. |
| 15 | getFileCopies | Get file copies from the system. |
| 16 | getFileVersion | Get the file version using a box connection. |
| 17 | getFileDetails | Get file details using a box connection. |
| 18 | getFolderDetails | Get folder details using a box connection. |
| 19 | addExternalAuditorEventLog | An event log of the operation performed by the External Auditor is added in the database. |
| 20 | getItemCollaborator | Get a list of collaborators for the specified Audit Set. |
| 21 | getFileVersionForExternal | Get the file version using the box connection. |
| 22 | checkTamperingStatus | Check the file tampering status by using ScalarDL validate functionality. |
| 23 | getEventsByDateRange | Get a list of events as per the specified time period from the system. |
| 24 | getEventsByDateRangeAndUser | Get a list of events performed by a specific user as per the specified time period. |
| 25 | getEventsByDateRangeAndEventType | Get a list of specific type of events performed as per the specified time period. |
| 26 | getEventsByDateRangeAndFileId | Get a list of events performed on a specific file as per the specified time period. |
| 27 | getEventsByDateRangeAndUserAndItemId | Get a list of events performed on a specific file by a specific user as per the specified time period. |
| 28 | getEventsByDateRangeAndEventTypeAndItemIdAndUserId | Get a list of specific type of events performed on a specific file by a specific user as per the specified time period. |
| 29 | getEventsByDateRangeAndEventTypeAndItemId | Get a list of specific type of events performed on a specific file as per the specified time period. |
| 30 | getEventsByDateRangeAndEventTypeAndUser | Get a list of specific type of events performed by a specific user as per the specified time period. |
| 31 | addItemToAuditSet | Add a file or folder to an Audit Set. |
| 32 | viewItemsFromSelectedAuditSet | View a list of items in the Audit Set. |
| 33 | getAllowListFromAuditSet | Get a list of allowed items from a specified audit set. |
| 34 | getItemFromAuditSet | Get a list of items from the audit set. |
| 35 | createAuditSet | Create a new audit set. |
| 36 | deleteAuditSet | Delete an Audit Set. |
| 37 | getMyAuditSetList | Get the list of Audit sets for which the user is a member. |
| 38 | viewExternalAuditorAccessLog | View the log of activities performed by external auditors on a specified file. |
| 39 | updateAuditSetInfo | Update the Audit Set details. |
| 40 | validateAuditSet | Validate all the items of Audit Set and report if any item/items are tampered. |
| 41 | updateAuditSetsForItemId | Update the association of an item with the Audit Sets. |
| 42 | getMyAuditSetListForItemId | Get the list of Audit Sets in which the specified item is added for monitoring. |
| 43 | changeAuditSetOwner | Change the owner of the audit set. |
| 44 | getCollaboratorsForAuditSet | Get a list of audit set collaborators from the database. |
| 45 | createAuditGroup | Create a new audit group which is a group of external auditors. |
| 46 | updateAuditGroup | Update the details of Audit group such as name or members. |
| 47 | deleteAuditGroup | Delete an Audit group. |
| 48 | getListOfAuditGroupMembers | Get a list of audit group members. |


Refer : 
1.[Rest API Design_Scalar Auditor for BOX](Rest-Api-Design-Scalar-Auditor-For-Box.md) for all the details of rest APIs implemented.
