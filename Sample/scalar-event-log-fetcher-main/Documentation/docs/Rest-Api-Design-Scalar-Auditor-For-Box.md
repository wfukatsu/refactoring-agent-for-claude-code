To convert this outline into a GitHub-flavored markdown document, you can follow this format:


# REST API Design Document

## 1. User Controller
### 1.1 /user/createUser
### 1.2 /user/getManagedUser
### 1.3 /user/deleteUser
### 1.4 /user/login
### 1.5 /user/updateUserRole
### 1.6 /user/editUser
### 1.7 /user/submitToken
### 1.8 /user/userSignIn
### 1.9 /user/getServiceToken
### 1.10 /user/getListOfExternalAuditors
### 1.11 /user/getOrgList
### 1.12 /user/sendResetPasswordOTP
### 1.13 /user/forgotPassword

## 2. Item Controller
### 1.14 /item/getIntegratedItemDetails

## 3. Folder Controller
### 1.15 /folder/getRootFolder
### 1.16 /folder/getItemList

## 4. File Controller
### 1.17 /file/getFileCopies
### 1.18 /file/getFileVersion
### 1.19 /file/getFileDetails
### 1.20 /file/getFolderDetails
### 1.21 /file/addExternalAuditorEventLog
### 1.22 /file/getItemCollaborator
### 1.23 /file/getFileVersionForExternal
### 1.24 /file/checkTamperingStatus

## 5. Event Log Controller
### 1.25 /eventLog/getEventsByDateRange
### 1.26 /eventLog/getEventsByDateRangeAndUser
### 1.27 /eventLog/getEventsByDateRangeAndEventType
### 1.28 /eventLog/getEventsByDateRangeAndFileId
### 1.29 /eventLog/getEventsByDateRangeAndUserAndItemId
### 1.30 /eventLog/getEventsByDateRangeAndEventTypeAndItemIdAndUserId
### 1.31 /eventLog/getEventsByDateRangeAndEventTypeAndItemId
### 1.32 /eventLog/getEventsByDateRangeAndEventTypeAndUser

## 6. Audit Set Item Controller
### 1.33 /auditSetItem/addItemToAuditSet
### 1.34 /auditSetItem/viewItemsFromSelectedAuditSet
### 1.35 /auditSetItem/getAllowListFromAuditSet
### 1.36 /auditSetItem/getItemFromAuditSet

## 7. Audit Set Controller
### 1.37 /auditSet/createAuditSet
### 1.38 /auditSet/deleteAuditSet
### 1.39 /auditSet/getMyAuditSetList
### 1.40 /auditSet/viewExternalAuditorAccessLog
### 1.41 /auditSet/updateAuditSetInfo
### 1.42 /auditSet/validateAuditSet
### 1.43 /auditSet/updateAuditSetsForItemId
### 1.44 /auditSet/getMyAuditSetListForItemId

## 8. Audit Set Collaborator Controller
### 1.45 /auditSetCollaborator/changeAuditSetOwner
### 1.46 /auditSetCollaborator/getCollaboratorsForAuditSet

## 9. Audit Group Controller
### 1.47 /auditGroup/createAuditGroup
### 1.48 /auditGroup/updateAuditGroup
### 1.49 /auditGroup/deleteAuditGroup
### 1.50 /auditGroup/getListOfAuditGroupMembers



# REST API Design Document

## 1. User Controller

### Overview
User Controller is a REST API Controller for user-related operations related to the 'Scalar Auditor for BOX' Application. The API documentation and execution approach is documented on Swagger [here](http://3.6.109.82:8095/swagger-ui/index.html).

### 1.1 /user/createUser

#### Description
The API `createUser` is a POST API that creates a user in the system and stores the user details.

#### Input Parameters
- **Name of the User**: Should be non-null or empty, size must be less than 64 chars, and should not contain any special characters.
- **Email of the user**: Should be non-null, should be a valid email with @.
- **Password**: Should be non-null and contain a minimum of 8 characters.
- **Organization name**
- **Role of user**

#### Response 
A new user will be created in the system.

#### Exceptions
| Exception              | Error Message                                  |
|------------------------|------------------------------------------------|
| Invalid user emails    | Incorrect user Email                           |
| Invalid username       | Invalid user name                              |
| Invalid password       | Invalid password                               |
| User does not have authority | User does not have the required role to create the user |
| User is already present | User is already exist                          |
| General exception      | Failed to create user                          |

#### Operations performed at each layer of the framework
1. Controller: Accepts {name,email,password,organization,roleJson,imageUrl} object.
2. Business: Accepts {name,email,password,organization,roleJson,imageUrl} object. Starts transaction, calls `createUser` method from UserService, and submits the transaction.
3. UserService: Accepts {name,email,password,organization,roleJson,imageUrl} object. Ensures no duplicate emails, encrypts password, constructs user object, assigns role, saves user to repository, handles exceptions.

### 1.2 /user/getManagedUser

#### Description
The API `getManagedUser` is a GET API that accepts details of users based on orgId and retrieves data including userEmail, id, name, password, roleJson, organizationName, and imageUrl.

#### Input Parameters
- **Token(currentUser)**: Important for fetching user list.

#### Response 
A list of users from the system.

#### Response DTO
- **Id**: User id
- **Name**: Name of user
- **Email**: Email of user
- **Password**: Password
- **roleJson**: Role of user (AUDIT_ADMIN, GENERAL_USER, EXTERNAL_AUDITOR)
- **organizationName**: Organization name
- **imageUrl**: User image

#### Exceptions
| Exception      | Error Message |
|----------------|---------------|
| User not present | User not found |

#### Operations performed at each layer of the framework
1. Controller: Accepts {orgId} object.
2. Business: Accepts {orgId} object. Starts transaction, calls `getManagedList` method from UserService, and submits the transaction.
3. UserService: Accepts {orgId} object. Retrieves user information from the organization, fetches a list of users, handles errors or exceptions.

### 1.3 /user/deleteUser

#### Description
The API `deleteUser` is a DELETE API. It takes user email as input and deletes the corresponding user from the system.

#### Input Parameters
- **User email**: Should check if the user is already deleted or not.

#### Response 
A user will be deleted from the system.

#### Exceptions
| Exception      | Error Message |
|----------------|---------------|
| User is not present | User not found |
| User does not have authority | User does not have the required role to delete the user |
| General exception | An error occurred while deleting the user |

#### Operations performed at each layer of the framework
1. Controller: Accepts {userEmail} object.
2. Business: Accepts {userEmail} object. Starts transaction, calls `deleteUser` method from UserService, and submits the transaction.
3. UserService: Accepts {userEmail} object. Constructs a user object, checks if the user is present, allows only AUDIT_ADMINs to delete users, deletes user from various tables, returns success message if user deleted successfully.


## 1.4 /user/login

### Description
The API `login` is a POST API used for user authentication. It accepts the user's email and password to authenticate the user.

#### Input Parameters
- **User email**: Should be a valid input.
- **Password**: Should not be empty.

#### Response 
Upon successful authentication, the user will be logged into the system. The response includes user data and access tokens.

#### Exceptions
| Exception                 | Error Message                                            |
|---------------------------|----------------------------------------------------------|
| User is not present       | User not found                                           |
| User does not have authority | User does not have the required role to perform the action |
| General exception         | An error occurred while processing the request          |

#### Operations Performed at Each Layer of the Framework
1. **Controller**
   - Accepts the following object: `{userEmail, password}`.

2. **Business**
   - Accepts the following object: `{userEmail, password}`.
   - Starts a transaction.
   - Calls the `login` method from UserService.
   - Initiates a distributed transaction for login.
   - Authenticates the user credentials.
   - Generates a JWT token and retrieves user details.
   - Generates Box access tokens.
   - Commits the transaction upon successful login and returns user data along with tokens.
   - Rolls back and throws a generic exception if an error occurs during the transaction.

3. **UserService**
   - Accepts the following object: `{userEmail, password}`.
   - The `generateToken` method creates a JWT token for the user, including additional claims.
   - Utilizes the `doGenerateToken` method to construct the token with specified claims, subject, issuance, expiration, and signing algorithms.
   - The `getByUserName` method retrieves a user entity from the repository based on the provided username within the context of a distributed transaction.




## 1.5 /user/updateUserRole

### Description
The API `updateUserRole` is a PUT API used to update the role of a user. It accepts details such as user email and a list of user roles to update the user's role.

#### Input Parameters
- **userEmail**: Should be non-null.
- **List of user roles**

#### Response 
As a response, a new user role will be updated in the system.

#### Exceptions
| Exception      | Error Message |
|----------------|---------------|
| Invalid userEmail | Incorrect userEmail |
| User does not have access | User doesn't have access to update role |

#### Operations performed at each layer of the framework
1. Controller
   - In this REST API, following object is accepted - {userEmail, list of roles}.

2. Business
   - Following object is accepted - {userEmail, list of roles}.
   - Start transaction.
   - Call the `updateUserRole` method from UserService, and if no exception occurs, submit the transaction.

3. UserService
   - Following object is accepted - {userEmail, list of roles}.
   - Construct a user object.
   - Retrieve the existing user based on the provided email from the userRepository.
   - Deserialize the stored JSON representation of user roles into a List of Strings.
   - Extract the current roles associated with the user.
   - Fetch the roles from the security token for authority verification.
   - Check if the user has access to update roles, specifically checking for AUDIT_ADMIN role.
   - Iterate through the new roles provided.
   - If a new role is not already present, create a RoleUser entry and add it to the database.
   - Update the list of added roles.
   - Iterate through the existing roles.
   - If an existing role is not in the new roles list, delete its corresponding RoleUser entry from the database.
   - Update the list of deleted roles.
   - Serialize the new roles list into JSON format.
   - Update the user's role JSON representation in the database with the new roles.
   - Create a new user and save it to the user repository.
   - Create a role for the specific user and save it to the UserRoleRepository.
 

## 1.6 /user/editUser

### Description
The API `editUser` is a PUT API that accepts details of the user to be edited, including the user's name, email, and organization name.

#### Input Parameters
- **Name of the User**: Should be non-null or empty. Size must be less than 64 chars. It shall not contain any special characters.
- **Email of user**: Should be non-null or empty.

#### Response 
As a response, a new user will be created in the system.

#### Exceptions
| Exception            | Error Message       |
|----------------------|---------------------|
| Invalid userEmail    | Incorrect userEmail |
| Invalid user name    | Invalid user name  |
| User is already present | User is already exist |
| General exception    | Failed to update user |

#### Operations performed at each layer of the framework
1. Controller
   - In this REST API, following object is accepted - {name, email, organization}.

2. Business
   - Following object is accepted - {name, email, organization}.
   - Start transaction.
   - Call the `createUser` method from UserService, and if no exception occurs, submit the transaction.

3. UserService
   - Following object is accepted – {name, email, organization}.
   - Fetch the current user object based on the provided email within the transaction.
   - If the current user is not found, throw a generic exception.
   - Obtain the roles of the current user.
   - Retrieve the user to be edited using their previous email.
   - If the user to be edited is not found, throw a generic exception.
   - Check if the new email is different from the previous one and if it already exists for another user.
   - Retrieve the roles of the previous user from JSON.
   - If the current user has the authority to update user details,
   - Update user details like email, name, and organization.
   - Update the roles associated with the user.
   - Update collaborators, audit sets, and audit group memberships accordingly.
   - Update the auditor logs if the email is changed.
   - Return a success response if the user details are updated successfully; otherwise, return an appropriate error response.
   - Update the email of collaborators, including owners, co-owners, members, and reviewers, if necessary.
   - Parse JSON to retrieve the member list of an audit group.
   - Update the email of members within the audit group, if necessary.


## 1.7 /user/submitToken

### Description
The API `submitToken` is a PUT API used for submitting user tokens. It accepts details such as accessToken, refreshToken, and expiryIn for the purpose of submitting the token.

#### Input Parameters
- **accessToken**
- **refreshToken**
- **expiryIn**

#### Response 
As a response, a user will be submitted token to the system.

#### Exceptions
| Exception            | Error Message          |
|----------------------|------------------------|
| Invalid userEmail    | Incorrect userEmail    |

### Operations performed at each layer of the framework
1. Controller
   - In this REST API, following object is accepted - {accessToken, refreshToken, expiryIn}.

2. Business
   - Following object is accepted - {accessToken, refreshToken, expiryIn}.
   - Start transaction.
   - The `submitToken` method begins a distributed transaction and initializes a BoxAPIConnection with the provided access token.
   - It retrieves user information from Box, attempts to register the user, and save the token, then commits the transaction.
   - If successful, it proceeds with user login using default credentials and returns the login response.
   - If an error occurs during the transaction, it rolls back and throws a generic exception.
   - Call the `submitToken` method from UserService, and if no exception occurs, submit the transaction.

3. UserService
   - Following object is accepted – {accessToken, refreshToken, expiryIn}.
   - The `registerUserAndSaveToken` method checks if the user is already registered; if not, it creates a new user entry in the database with default role(s) and organization details.
   - Then, it saves the user's access token information, including expiration details.
   - If the user already exists, it updates the access token information.
   - Finally, it returns a response, indicating the success of the operation.

## 1.8 /user/userSignIn

### Description
The API `userSignIn` is a PUT API used for user sign-in. It accepts details such as user email to log in to the system.

#### Input Parameters
- **userEmail**

#### Response 
As a response, a user will be signed in to the system.

#### Exceptions
| Exception            | Error Message          |
|----------------------|------------------------|
| Invalid userEmail    | Incorrect userEmail    |

### Operations performed at each layer of the framework
1. Controller
   - In this REST API, following object is accepted - {userEmail}.

2. Business
   - Following object is accepted - {userEmail}.
   - Start transaction.
   - Call `userSignIn` method from UserService and if no exception occurs submit transaction.

3. UserService
   - Following object is accepted - {userEmail}.
   - Generate a token for a user based on their username and user details, which include their roles.
   - This token is created with an expiration time and signed using a secret key.
   - The token is then returned along with other relevant information, such as a refresh token, access token, and user details, in a response object.
   - Additionally, a Box API connection is established to obtain a service account access token for further operations.


## 1.9 /user/getServiceToken

### Description
The API `getServiceToken` is a GET API designed to retrieve a token by providing details of the user token.

#### Input Parameters
- None

#### Response 
As a response, a user will get a service token from the system.

### Operations performed at each layer of the framework
1. Controller
   - In this REST API, following object is accepted - {token}.

2. Business
   - Following object is accepted - {token}.
   - Start transaction.
   - Call the `getServiceToken` method from UserService, and if no exception occurs, submit the transaction.

3. UserService
   - Following object is accepted – {token}.
   - Get access and refresh tokens for a service account from Box, a cloud storage provider.
   - It starts by establishing a connection to Box's enterprise account using a utility method. Then, it sets the connection to act as itself.
   - After that, it constructs a response object with an HTTP status of OK, an empty message, and a status of true. 
   - The response includes data containing the access token, refresh token, and expiration time obtained from the Box connection.


## 1.10 /user/getListOfExternalAuditors 

### Description
The API `getListOfExternalAuditors` is a GET API that retrieves data including userEmail, id, name, password, roleJson, organizationName, and imageUrl.

#### Input Parameters
- User Token is important for fetching the list of external auditors.

#### Response 
As a response, a list of external auditors from the system.

#### Response DTO: userInfo
- **id**: User id
- **name**: Name of user
- **email**: Email of user
- **password**: Password
- **organizationName**: Organization name
- **imageUrl**: User image

### Exceptions
Following would be the exception conditions:

Exception | Error Message
--- | ---
For external auditors, the list is empty. | User list is empty 

### Operations performed at each layer of the framework
1. Controller
   - In this REST API, following object is accepted - {User Token}.

2. Business
   - Following object is accepted - {User Token}.
   - Start transaction.
   - Call `getListOfExternalAuditors` method from UserService, and if no exception occurs, submit the transaction.

3. UserService
   - Following parameters are accepted - User Token.
   - Fetch a list of external auditors using the `getByRole` method from the role user repository.
   - Obtain the list of external auditors using the userInfo object.


## 1.11 /user/getOrgList

### Description
The API `getOrgList` is a GET API that accepts details of the user based on orgId and retrieves data including the user's email, ID, name, password, role JSON, organization name, and image URL.

#### Input Parameters
- organization id

#### Response 
As a response, a list of organizations will be fetched from the system.

#### Response DTO: orgList
- **orgId**: Organization id 
- **organizationName**: Name of organization

### Operations performed at each layer of the framework
1. Controller
   - In this REST API, following object is accepted - {User Token}.

2. Business
   - Following object is accepted - {User Token}.
   - Start transaction.
   - Call `getOrgList` method from UserService, and if no exception occurs, submit the transaction.

3. UserService
   - Following parameters are accepted - User Token.
   - Fetch a list of organizations using the `getOrganizationList` method from the organization repository.
   - Obtain a list of organizations using the Organization object.


## 1.12 /user/sendResetPasswordOTP

### Description
The API `sendResetPasswordOTP` is a GET method that accepts details such as the user's email and sends a request to generate and send the reset password OTP (one-time password).

#### Input Parameters:
- email: This parameter contains the email id of the user

#### Response 
As a response, an OTP is sent to existing users' email.

#### Response DTO: userInfo
- **userId**: User Id 
- **emailId**: Email Id of user
- **otpValidityTime**: Validity time for OTP
- **OTP**: OTP 

#### Exceptions
Following would be the exception conditions:

| Exception          | Error Message                           |
|--------------------|-----------------------------------------|
| Failed to send an email | Unable to send email                   |
| Invalid userEmail  | Invalid EmailNo User found with this email |

### Operations performed at each layer of the framework
1. Controller 
   - `sendResetPasswordOTP`: In this REST API, Following parameter is accepted - {useremail}

2. Business 
   - Following parameter is accepted -  {useremail}
   - Start transaction
   - Call `sendResetPasswordOTP` method from UserService and if no exception occurs submit transaction

3. UserService
   - Following Object is accepted - {userEmail}
   - Check if the given email is valid or not.
   - Generate OTP by calling `GenericUtility.generateOtp()`.
   - Send OTP to email to reset password.
   - Save OTP details in the user otp repository.
   - Send user ID, user email, validity time, and OTP in response.


## 1.13 /user/forgotPassword

### Description
The API `forgotPassword` is a POST API used for facilitating password resets. It accepts details such as user email, OTP (one-time password), and new password to set a new password.

#### Input Parameters:
- **newpassword**: It should not be null or empty. It should contain a minimum of 8 characters. 
- **Email**: It should not be null or empty.
- **OTP**: It should not be null or empty.

#### Response 
As a response, the password is updated for the user.

#### Exceptions
Following would be the exception conditions:

| Exception                 | Error Message     |
|---------------------------|-------------------|
| User not present in the system | User Not Found ! |
| Entered OTP is invalid    | Invalid OTP       |
| OTP time is expired       | OTP has expired   |

### Operations performed at each layer of the framework
1. Controller
   - In this REST API, Following Object is accepted - {OTP,userEmail,newPassword}

2. Business
   - Following Object is accepted - {OTP,userEmail,newPassword }
   - Start transaction
   - Call the `forgotPassword` method from UserService, and if no exception occurs, submit the transaction

3. UserService:
   - Following Object is accepted - {OTP,userEmail,newPassword }
   - Retrieve the user object using the provided email from the userRepository.
   - Check if the user exists or if they are marked as deleted; if so, throw a NotFoundException.
   - Retrieve the UserOtp object associated with the user's email from the userOptRepository.
   - Verify if the provided OTP matches the OTP stored in the database; if not, throw a GenericException.
   - Ensure that the OTP has not expired by comparing its expiry date with the current date; if expired, throw a GenericException.
   - Encrypt the new password provided in the updatePasswordDTO using the password encoder.
   - Update the user's password in the database with the newly encrypted password.


## 2. Item Controller

### Overview
Item Controller is a REST API Controller for item-related operations on the Box application. Below are the REST API’s for getting integrated item detail related operations. The API documentation and execution approach are documented on Swagger [here](http://20.40.52.159:8095/swagger-ui/index.html).

### 1.14 /item/getIntegratedItemDetails

#### Description
The API `getIntegratedItemDetails` is a GET API that accepts details of the item, including itemId, itemType, and userId, in order to retrieve integrated item details.

#### Input Parameters:
- **itemId**
- **itemType**
- **userId**

#### Response 
As a response, fetch the information related to integrated items from the system.

#### Response DTO:
- **userEmail**: User not found
- **Name**: Name of user
- **jwtToken**: Token 
- **List<String> userRoles**: List of user roles (audit_admin, general_user, external_auditor)
- **refreshToken**: Refresh token of user
- **accessToken**: Access token of user
- **serviceAccAccessToken**: Enterprise access token
- **objectItemDetails**: Item details object

#### Exceptions
Following would be the exception conditions:

| Exception           | Error Message |
|---------------------|---------------|
| For user not present | User not found |

#### Operations performed at each layer of the framework
1. Controller
   - In this REST API, Following Object is accepted - {itemId, itemType, userId}

2. Business
   - Following Object is accepted - {itemId, itemType, userId}
   - Start a distributed transaction
   - Retrieve user details and validate the user.
   - Update the user's latest token for authentication.
   - Generate a JWT token for the user.
   - Establish a connection with the Box API using the user's access token.
   - Fetch details of the item (file or folder) based on the provided ID and type.
   - Convert item details into DTO objects.
   - Commit the transaction and construct the integrated response, including item details and user information.
   - Return an API response with the integrated response.
   - Rollback the transaction in case of errors during the commit process.


## 3. Folder Controller

### Overview
Folder Controller is a REST API Controller for folder-related operations on the Box application. Below are the REST API’s for the get root folder related operations. The API documentation and execution approach are documented on Swagger [here](http://20.40.52.159:8095/swagger-ui/index.html).

### 1.15 /folder/getRootFolder

#### Description
The API `getRootFolder` is a GET API that accepts details of the root folder, including the name of the audit set, description, ownedBy, and auditSetId, to retrieve the root folder.

#### Input Parameters:
- **Token(currentUser)**: Important for fetching the audit setlist.

#### Response 
As a response, fetch the root folder from the system.

#### Exceptions
Following would be the exception conditions:

| Exception           | Error Message |
|---------------------|---------------|
| For user not present | User not found |

#### Operations performed at each layer of the framework
1. Controller
   - In this REST API, Following Object is accepted 

2. Business
   - Following Object is accepted 
   - Start a transaction
   - Call the `getListAuditSet` method from AuditSetService, and if no exception occurs, submit the transaction

3. AuditSetService:
   - Following params are accepted
   - Retrieve information about the root folder from the box connection.
   - Get the folder ID using a box connection.


## 1.16 /folder/getItemList

### Description
The API `getItemList` is a GET API that retrieves a list of items. It accepts details such as id, name, type, description, createdAt, modifiedAt, size, ownedBy, modifiedBy, and path.

### Input Parameters:
- **ItemId**: Item id must not be null.

### Response 
As a response, fetch a list of items from the system using a box connection.


### Response DTO: FolderDetailsDto
| Field Name  | Description            |
|-------------|------------------------|
| Id          | FileId                 |
| Name        | File name              |
| Description | File description       |
| CreatedAt   | Create time when file is created |
| ModifiedAt  | Modified time of file  |
| Size        | Size of file           |
| OwnedBy     | OwnedBy is a file owner details: user type, id, username, login |
| ModifiedBy  | Modified by is a user modified a file details: user type, id, username, login |
| Path        | Folder path from box API|

### Exceptions
Following would be the exception conditions:

| Exception          | Error Message  |
|--------------------|----------------|
| For item not present | Item not found |

### Operations performed at each layer of the framework
1. Controller
   - In this REST API, Following Object is accepted - {itemId}

2. Business
   - Following Object is accepted - {itemId}
   - Start a transaction
   - Call the `getItemList` method from FileService, and if no exception occurs, submit the transaction

3. FileService:
   - Following params are accepted - {itemId}
   - Retrieve information about the folder using a box connection.
   - Fetch a list of items from the box application using the box connection.
   - Obtain the list of items using the `itemDetailsList` object.





## 1.17 /file/getFileCopies

### Description
The API `getFileCopies` is a GET API that accepts details of the file copies, including sha1Hash and itemId, and retrieves data such as sha1Hash, itemId, itemVersionId, itemVersionNumber, itemName, ownerByJson, path, and createdAt.

### Input Parameters:
- **Item id**
- **Sha1Hash**

### Response 
As a response, get file copies from the system.

### Response DTO: copyItems
| Field Name        | Description                                        |
|-------------------|----------------------------------------------------|
| sha1Hash          | SHA1 Hash id of the file                           |
| itemId            | File id                                            |
| itemVersionId     | File version id                                    |
| itemVersionNumber | File version number                                |
| itemName          | File name                                          |
| ownerByJson       | Owner JSON (typeofUser, userid, Username, Userlogin)|
| Path              | File path from folder                              |
| createdAt         | File copy date                                     |

### Exceptions
Following would be the exception conditions:

| Exception       | Error Message      |
|-----------------|--------------------|
| If item ID is null | Item id not found |

### Operations performed at each layer of the framework
1. Controller
   - In this REST API, Following Object is accepted - {itemId, sha1Hash}

2. Business
   - Following Object is accepted - {itemId, sha1Hash }
   - Start transaction
   - Call the `getFileCopies` method from FileService and if no exception occurs submit transaction

3. FileService
   - Following Object is accepted - {itemId, sha1Hash }
   - Verify that the item ID is not empty.
   - Obtain details about the file copies from the item by SHA1 table.
   - Get the file owned by objects
   - Get details of file copies from the item BySha1Repsitory
   - Obtain the `CopyItems` object as a response


## 1.18 /file/getFileVersion

### Description
The API `getFileVersion` is a GET API that accepts details of the file version, such as fileId and auditSetId, and retrieves data including uploaderName, sha1Hash, itemVersionId, itemVersionNumber, itemName, and modifiedAt.

### Input Parameters:
- **fileId**
- **auditSetId**

### Response 
As a response, get the file version using a box connection.

### Response DTO: FileVersions
| Field Name        | Description                                        |
|-------------------|----------------------------------------------------|
| uploaderName      | Name of who upload the version                     |
| sha1Hash          | SHA1 Hash id of the file                           |
| itemVersionId     | File version id                                    |
| itemVersionNumber | File version number                                |
| itemName          | File name                                          |
| modifiedAt        | Modified date of file version                      |

### Operations performed at each layer of the framework
1. Controller
   - In this REST API, Following Object is accepted - {fileId, auditSetId}

2. Business
   - Following Object is accepted - {fileId, auditSetId}
   - Start transaction
   - Call the `getFileVersion` method from FileService, and if no exception occurs, submit the transaction

3. FileService
   - Following Object is accepted - {fileId, auditSetId}
   - Verify that the item ID is not empty.
   - Obtain details about the file version using the box connection.
   - Get details of file versions from the Box application.
   - Get details in the `FileVersions` object.




## 1.19 /file/getFileDetails

### Description
The API `getFileDetails` is a GET API that accepts details of the file, such as itemId and auditSetId, and retrieves data including name, type, description, createdAt, modifiedAt, size, sha1, ownedBy object, modifiedBy object, tamperedStatus, and path.

### Input Parameters:
- **Item id**: Should be non-null.
- **Audit Set Id**: Optional

### Response 
As a response, get file details from the box connection.

### Response DTO: FileDetailsDto
| Field Name    | Description                                            |
|---------------|--------------------------------------------------------|
| Id            | FileId                                                 |
| Name          | File name                                              |
| Description   | File description                                       |
| Created time  | Create time when file is created                       |
| ModifiedAt    | Modified time of file                                  |
| Size          | Size of file                                           |
| OwnedBy       | OwnedBy is a file owner details: user type, id, username, login |
| ModifiedBy    | Modified by is a user modified a file details: user type, id, username, login |
| TamperedStatus| File is monitored or not-monitored                     |
| Path          | File path from folder                                  |

### Exceptions
| Exception          | Error Message      |
|--------------------|--------------------|
| If item ID is null | Item id not found  |

### Operations performed at each layer of the framework
1. Controller
   - In this REST API, Following Object is accepted - {itemId, auditSetId}

2. Business
   - Following Object is accepted - {itemId, auditSetId}
   - Start transaction
   - Call the `getFileDetails` method from FileService, and if no exception occurs, submit the transaction

3. FileService
   - Following Object is accepted - {itemId, auditSetId}
   - Verify that the item ID is not empty.
   - Retrieve the token from the security context holder.
   - If the user has the roles AUDIT_ADMIN or GENERAL_USER, use the token; otherwise, connect to Box.
   - Obtain details about the file using the Box file object.
   - Specify the user making the action or the owner of the file in the ownedBy and modifiedBy objects.
   - Check the tampering status; if the item status is null, set it as non-monitored; otherwise, get the status from the database.
   - Create an object for auditor logs and save it to the AuditorLogRepository.

## 1.20 /file/getFolderDetails

### Description
The API `getFolderDetails` is a GET API that accepts details of the folder, such as folderId and auditSetId, and retrieves data including name, type, description, createdAt, modifiedAt, size, ownedBy object, modifiedBy object, and path.

### Input Parameters:
- **Folder id**: Should be non-null.
- **Audit Set Id**: Optional

### Response 
As a response, get folder details from the box connection.

### Response DTO: FolderDetailsDto
| Field Name    | Description                                            |
|---------------|--------------------------------------------------------|
| Id            | FileId                                                 |
| Name          | File name                                              |
| Description   | File description                                       |
| Created time  | Create time when file is created                       |
| ModifiedAt    | Modified time of file                                  |
| Size          | Size of file                                           |
| OwnedBy       | OwnedBy is a file owner details: user type, id, username, login |
| ModifiedBy    | Modified by is a user modified a file details: user type, id, username, login |
| Path          | File path from folder                                  |

### Exceptions
| Exception           | Error Message       |
|---------------------|---------------------|
| If folder ID is null| Folder id not found |

### Operations performed at each layer of the framework
1. Controller
   - In this REST API, Following Object is accepted - {folderId, auditSetId}

2. Business
   - Following Object is accepted - {folderId, auditSetId}
   - Start transaction
   - Call the `getFolderDetails` method from FileService, and if no exception occurs, submit the transaction

3. FileService
   - Following Object is accepted - {folderId, auditSetId}
   - Confirm that the folder ID is not empty.
   - Retrieve the token from the security context holder.
   - If the user has the roles AUDIT_ADMIN or GENERAL_USER, use the token; otherwise, connect to Box.
   - Obtain the details of the file using the Box folder object.
   - Specify the user making the action or the owner of the folder in the ownedBy and modifiedBy objects.
   - Create an object for auditor logs and save it to the AuditorLogRepository.


## 1.21 /file/addExternalAuditorEventLog

### Description
The API `addExternalAuditorEventLog` is a POST API used to add event logs for files or folders. It accepts parameters such as auditSetId, itemId, actionType, timestamp, and itemType to log external auditor events.

### Input Parameters:
- **auditSetId**: Should not be null or empty.
- **itemId**
- **actionType**
- **timestamp**

### Response 
As a response, an event log will be created in the system.

### Exceptions
| Exception                   | Error Message                                      |
|-----------------------------|----------------------------------------------------|
| For user not present        | User not found                                     |
| If audit set not present    | Audit Set Not Found                                |
| Only external auditor action on the file or folder must be added | No need to add event log |
| If you have other exceptions| An error occurred while adding event logs          |

### Operations performed at each layer of the framework
1. Controller
   - In this REST API, Following Object is accepted - `{auditSetId, itemId, actionType, timestamp}`

2. Business
   - Following Object is accepted - `{auditSetId, itemId, actionType, timestamp}`
   - Start transaction
   - Call the `addExtAuditorEventLog` method from FileService, and if no exception occurs, submit the transaction

3. FileService:
   - Following parameters are accepted - `{auditSetId, itemId, actionType, timestamp}`
   - Retrieve information about the current user using their email address.
   - Ensure that the user exists or not; raise an exception.
   - Verify if the audit set exists or not; raise a not-found exception.
   - Retrieve the user's role from the authentication token.
   - Create a UTC-formatted timestamp for the event.
   - If the user has the role of an external auditor, proceed with adding event logs; otherwise, return a forbidden response.
   - Build an auditor log object with relevant details, such as audit set ID, item ID, user email, event date, and custom JSON event details.
   - Determine the item type (file or folder) based on the provided information.
   - Depending on the action type (ITEM_DOWNLOAD, ITEM_PREVIEW, ITEM_VIEW), perform specific actions:
     - For item view, fetch file details.
     - For item preview, set the event type accordingly.
     - For item download, set the event type accordingly.
   - Save the auditor log to the AuditorLogRepository.

## 1.22 /file/getItemCollaborator

### Description
The API `getItemCollaborator` is a GET API that accepts details of the item collaborator, such as itemId and itemType, and retrieves information about the item's collaborators (userEmail, username, userId) as well as its owner (ownerName, ownerEmail, ownerId).

### Input Parameters:
- **ItemId and ItemType**: Important for fetching item collaborator list

### Response 
As a response, a list of item collaborators will be fetched from the system.

### Response DTO: itemCollaborator
| Field Name        | Description                                          |
|-------------------|------------------------------------------------------|
| itemCollaborator | In this object, we get item collaborator (userEmail, username, userId) |
| itemOwner        | In this object, we get item owner (userEmail, username, userId)       |

### Exceptions
| Exception         | Error Message      |
|-------------------|--------------------|
| The user item is present or not present | Item not found  |

### Operations performed at each layer of the framework
1. Controller
   - In this REST API, Following Object is accepted - `{itemId, itemType}`

2. Business
   - Following Object is accepted - `{itemId, itemType}`
   - Start transaction
   - Call the `getItemCollaborator` method from FileService, and if no exception occurs, submit the transaction

3. FileService:
   - Following parameters are accepted - `{itemId, itemType}`
   - Retrieve information about the item collaborator using the box connection.
   - Get a list of item collaborators using a box connection, and also get details about the owner of the file.
   - Obtain the list of item collaborator and owner details using the item Collaborator object.


## 1.23 /file/getFileVersionForExternal

### Description
The API `getFileVersionForExternal` is a GET API that accepts details of the file version, such as uploaderName, sha1Hash, itemVersionId, itemVersionNumber, itemName, and modifiedAt.

### Input Parameters:
- **fileId**

### Response 
As a response, get the file version using the box connection.

### Response DTO: fileVersions
| Field Name       | Description                                          |
|------------------|------------------------------------------------------|
| uploaderName     | Name of who upload the version                       |
| sha1Hash         | Sha1 Hash id of the file                             |
| itemVersionId    | File version id                                      |
| itemVersionNumber| File version number                                  |
| itemName         | File name                                            |
| modifiedAt       | Modified date of file version                        |

### Operations performed at each layer of the framework
1. Controller
   - In this REST API, Following Object is accepted - `{fileId}`

2. Business
   - Following Object is accepted - `{fileId}`
   - Start transaction
   - Call the `getFileVersionForExternal` method from FileService, and if no exception occurs, submit the transaction

3. FileService
   - Following Object is accepted - `{fileId}`
   - Verify that the item ID is not empty.
   - Obtain details about the file version using the box connection.
   - Get details of file versions from the Box application.
   - Get details in the fileVersions object.

## 1.24 /file/checkTamperingStatus

### Description
The API `checkTamperingStatus` is a GET API that accepts details of the file's tampering status, such as fileId, in order to check its tampering status.

### Input Parameters:
- **fileId**

### Response 
As a response, check the file tampering status on the system.

### Response DTO:
| Field Name       | Description                     |
|------------------|---------------------------------|
| tamperingStatus  | Tampering status of file        |

### Operations performed at each layer of the framework
1. Controller
   - In this REST API, Following Object is accepted - `{fileId}`

2. Business
   - Following Object is accepted - `{fileId}`
   - Start transaction
   - This method checks the tampering status of a file. It retries the process up to three times in the event of failure. 
   - It starts a transaction, retrieves the item status, and then checks for tampering. 
   - If successful, it updates the status and commits the transaction
   - If any errors occur during the transaction, it rolls back and retries.
   - If retries are exceeded three times, it throws a generic exception.

3. FileService
   - Following Object is accepted - `{fileId}`
   - The method checks if a file has been tampered with by comparing its details in Box with its details in Scalar DL. 
   - If the ledger status is "OK,"  it retrieves the file's information from Box and validates it against Scalar DL ledger entries. 
   - If the details match, it returns "NOT_TAMPERED"; if not monitored in Scalar DL, it returns "NOT_MONITORED"; otherwise, it returns "TAMPERED."



## 1.25 /eventLog/getEventsByDateRange

### Description
The API `getEventsByDateRange` is a GET API that accepts details of events such as startDate and endDate, and retrieves a list of events including eventId, eventType, eventCreatedUserName, eventCreatedUserId, eventCreatedAt, itemId, and itemName.

### Input Parameters:
- **startDate**
- **endDate**

### Response 
As a response, a list of events will be fetched from the system.

### Response DTO: EventList
| Field Name          | Description                           |
|---------------------|---------------------------------------|
| eventId             | Event id                              |
| eventType           | Event type (item_create, item_upload, etc.) |
| eventCreatedUserName| Name of event created by              |
| eventCreatedUserId  | User id of event created user         |
| eventCreatedAt      | Event created date                    |
| itemId              | File id                               |
| itemName            | File name                             |

### Exceptions
| Exception                             | Error Message           |
|---------------------------------------|-------------------------|
| The user item is present or not present | Item not found          |

### Operations performed at each layer of the framework
1. Controller
   - In this REST API, Following Object is accepted – `{startDate,endDate}`

2. Business
   - Following Object is accepted - `{startDate,endDate}`
   - Start transaction
   - Call the `getEventsByDateRange` method from eventService, and if no exception occurs, submit the transaction

3. eventService
   - Following params are accepted- `{startDate,endDate}`
   - This method retrieves events within a specified date range.
   - It first parses the input timestamps into the desired format (`yyyy/MM/dd HH:mm:ss:SSS`). 
   - Then, it extracts the start and end dates from the parsed timestamps. 
   - Next, it iterates through each date within the range and retrieves events for each date.
   - If there's only one day in the range, it fetches events for that day. For the first date, it fetches events until midnight. 
   - For the last date, it fetches events until the end of the day. For dates in between, it fetches events for the entire day. 
   - Finally, it returns a list of event details gathered during the process.

## 1.26 /eventLog/getEventsByDateRangeAndUser

### Description
The API `getEventByDateRangeAndUser` is a GET API that accepts details of events such as startDate, endDate, and userId, and retrieves a list of events including eventId, eventType, eventCreatedUserName, eventCreatedUserId, eventCreatedAt, itemId, and itemName.

### Input Parameters:
- **startDate**
- **endDate**
- **userId**

### Response 
As a response, a list of events will be fetched from the system.

### Response DTO: EventList
| Field Name          | Description                           |
|---------------------|---------------------------------------|
| eventId             | Event id                              |
| eventType           | Event type (item_create, item_upload, etc.) |
| eventCreatedUserName| Name of event created by              |
| eventCreatedUserId  | User id of event created user         |
| eventCreatedAt      | Event created date                    |
| itemId              | File id                               |
| itemName            | File name                             |

### Exceptions
| Exception                             | Error Message           |
|---------------------------------------|-------------------------|
| The user item is present or not present | Item not found          |

### Operations performed at each layer of the framework
1. Controller
    - In this REST API, Following Object is accepted – `{startDate,endDate,userId}`

2. Business
   - Following Object is accepted - `{startDate,endDate,userId}`
   - Start transaction
   - Call the `getEventsByDateRangeAndUser` method from eventService, and if no exception occurs, submit the transaction

3. eventService
   - Following parameters are accepted: `{startDate,endDate,userId}`
   - This method retrieves events within a specified date range and user ID.
   - It first parses the input timestamps into the desired format (`yyyy/MM/dd HH:mm:ss:SSS`). 
   - Then, it extracts the start and end dates from the parsed timestamps for the user.
   - Next, it iterates through each date within the range and retrieves events for each date.
   - If there's only one day in the range, it fetches events for that day. For the first date, it fetches events until midnight. 
   - For the last date, it fetches events until the end of the day. For dates in between, it fetches events for the entire day. 
   - Finally, it returns a list of event details gathered during the process.


## 1.27 /eventLog/getEventsByDateRangeAndEventType

### Description
The API `getEventsByDateRangeAndEventType` is a GET API that accepts details of events such as startDate, endDate, and eventType, and retrieves a list of events including eventId, eventType, eventCreatedUserName, eventCreatedUserId, eventCreatedAt, itemId, and itemName.

### Input Parameters:
- **startDate**
- **endDate**
- **eventType**

### Response 
As a response, a list of events will be fetched from the system.

### Response DTO: EventList
| Field Name          | Description                           |
|---------------------|---------------------------------------|
| eventId             | Event id                              |
| eventType           | Event type (item_create, item_upload, etc.) |
| eventCreatedUserName| Name of event created by              |
| eventCreatedUserId  | User id of event created user         |
| eventCreatedAt      | Event created date                    |
| itemId              | File id                               |
| itemName            | File name                             |

### Exceptions
| Exception                             | Error Message           |
|---------------------------------------|-------------------------|
| For user item is present or not present | Item not found          |

### Operations performed at each layer of the framework
1. Controller
   - In this REST API, Following Object is accepted - `{startDate,endDate,eventType}`

2. Business
   - Following Object is accepted - `{startDate,endDate,eventType}`
   - Start transaction
   - Call the `getEventsByDateRangeAndEventType` method from eventService, and if no exception occurs, submit the transaction

3. eventService
   - Following params are accepted - `{startDate,endDate,eventType}`
   - This method retrieves events within a specified date range and event type.
   - It first parses the input timestamps into the desired format (`yyyy/MM/dd HH:mm:ss:SSS`). 
   - Then, it extracts the start and end dates from the parsed timestamps for eventType.
   - Next, it iterates through each date within the range and retrieves events for each date.
   - If there's only one day in the range, it fetches events for that day. For the first date, it fetches events until midnight. 
   - For the last date, it fetches events until the end of the day. For dates in between, it fetches events for the entire day. 
   - Finally, it returns a list of event details gathered during the process.

## 1.28 /eventLog/getEventsByDateRangeAndFileId

### Description
The API `getEventsByDateRangeAndFileId` is a GET API that accepts details of events such as startDate, endDate, and fileId, and retrieves a list of events including eventId, eventType, eventCreatedUserName, eventCreatedUserId, eventCreatedAt, itemId, and itemName.

### Input Parameters:
- **startDate**
- **endDate**
- **fileId**

### Response 
As a response, a list of events will be fetched from the system.

### Response DTO: EventList
| Field Name          | Description                           |
|---------------------|---------------------------------------|
| eventId             | Event id                              |
| eventType           | Event type (item_create, item_upload, etc.) |
| eventCreatedUserName| Name of event created by              |
| eventCreatedUserId  | User id of event created user         |
| eventCreatedAt      | Event created date                    |
| itemId              | File id                               |
| itemName            | File name                             |

### Exceptions
| Exception                             | Error Message           |
|---------------------------------------|-------------------------|
| For user item is present or not present | Item not found          |

### Operations performed at each layer of the framework
1. Controller
   - In this REST API, Following Object is accepted - `{startDate,endDate,fileId}`

2. Business
   - Following Object is accepted - `{startDate,endDate,fileId}`
   - Start transaction
   - Call the `getEventsByDateRangeAndFileId` method from eventService, and if no exception occurs, submit the transaction

3. eventService
   - Following params are accepted - `{startDate,endDate,fileId}`
   - This method retrieves events within a specified date range and file ID.
   - It first parses the input timestamps into the desired format (`yyyy/MM/dd HH:mm:ss:SSS`). 
   - Then, it extracts the start and end dates from the parsed timestamps for fileId.
   - Next, it iterates through each date within the range and retrieves events for each date.
   - If there's only one day in the range, it fetches events for that day. For the first date, it fetches events until midnight. 
   - For the last date, it fetches events until the end of the day. For dates in between, it fetches events for the entire day. 
   - Finally, it returns a list of event details gathered during the process.


## 1.29 /eventLog/getEventsByDateRangeAndUserAndItemId

### Description
The API `getEventsByDateRangeAndUserAndItemId` is a GET API that accepts details of events such as startDate and endDate, and retrieves a list of events including eventId, eventType, eventCreatedUserName, eventCreatedUserId, eventCreatedAt, itemId, and itemName.

### Input Parameters:
- **startDate**
- **endDate**
- **userId**
- **itemId**

### Response 
As a response, a list of events will be fetched from the system.

### Response DTO: EventList
| Field Name          | Description                           |
|---------------------|---------------------------------------|
| eventId             | Event id                              |
| eventType           | Event type (item_create, item_upload, etc.) |
| eventCreatedUserName| Name of event created by              |
| eventCreatedUserId  | User id of event created user         |
| eventCreatedAt      | Event created date                    |
| itemId              | File id                               |
| itemName            | File name                             |

### Exceptions
| Exception                             | Error Message           |
|---------------------------------------|-------------------------|
| For user item is present or not present | Item not found          |

### Operations performed at each layer of the framework
1. Controller
   - In this REST API, Following Object is accepted - `{startDate,endDate, userId,itemId }`

2. Business
   - Following Object is accepted - `{startDate,endDate, userId,itemId}`
   - Start transaction
   - Call the `getEventsByDateRangeAndUserAndItemId` method from eventService, and if no exception occurs, submit the transaction.

3. eventService:
   - Following params are accepted - `{startDate,endDate,userId,itemId}`
   - This method retrieves events within a specified date range (userId, itemId).
   - It first parses the input timestamps into the desired format (`yyyy/MM/dd HH:mm:ss:SSS`). 
   - Then, it extracts the start and end dates from the parsed timestamps for userId and itemId.
   - Next, it iterates through each date within the range and retrieves events for each date.
   - If there's only one day in the range, it fetches events for that day. For the first date, it fetches events until midnight. 
   - For the last date, it fetches events until the end of the day. For dates in between, it fetches events for the entire day. 
   - Finally, it returns a list of event details gathered during the process.

## 1.30 /eventLog/getEventsByDateRangeAndEventTypeAndItemIdAndUserId

### Description
The API `getEventsByDateRangeAndEventTypeAndItemIdAndUserId` is a GET API that accepts details of events such as startDate and endDate, and retrieves a list of events including eventId, eventType, eventCreatedUserName, eventCreatedUserId, eventCreatedAt, itemId, and itemName.

### Input Parameters:
- **startDate**
- **endDate**
- **eventType**
- **itemId**
- **userId**

### Response 
As a response, a list of events will be fetched from the system.

### Response DTO: EventList
| Field Name          | Description                           |
|---------------------|---------------------------------------|
| eventId             | Event id                              |
| eventType           | Event type (item_create, item_upload, etc.) |
| eventCreatedUserName| Name of event created by              |
| eventCreatedUserId  | User id of event created user         |
| eventCreatedAt      | Event created date                    |
| itemId              | File id                               |
| itemName            | File name                             |

### Exceptions
| Exception                             | Error Message           |
|---------------------------------------|-------------------------|
| For user item is present or not present | Item not found          |

### Operations performed at each layer of the framework
1. Controller
   - In this REST API, Following Object is accepted - `{startDate,endDate, userId,itemId, eventType }`

2. Business
   - Following Object is accepted - `{startDate,endDate, userId,itemId,eventType}`
   - Start transaction
   - Call the `getEventsByDateRangeAndEventTypeAndItemIdAndUserId` method from eventService, and if no exception occurs, submit the transaction.

3. eventService:
   - Following params are accepted - `{startDate,endDate,userId,itemId, eventType }`
   - This method retrieves events within a specified date range, user ID, item ID, and eventType.
   - It first parses the input timestamps into the desired format (`yyyy/MM/dd HH:mm:ss:SSS`). 
   - Then, it extracts the start and end dates from the parsed timestamps for userId, itemId, and eventType.
   - Next, it iterates through each date within the range and retrieves events for each date.
   - If there's only one day in the range, it fetches events for that day. For the first date, it fetches events until midnight. 
   - For the last date, it fetches events until the end of the day. For dates in between, it fetches events for the entire day. 
   - Finally, it returns a list of event details gathered during the process.


## 1.31 /eventLog/getEventsByDateRangeAndEventTypeAndItemId

### Description
The API `getEventsByDateRangeAndEventTypeAndItemId` is a GET API that accepts details of events such as startDate and endDate, and retrieves a list of events including eventId, eventType, eventCreatedUserName, eventCreatedUserId, eventCreatedAt, itemId, and itemName.

### Input Parameters:
- **startDate**
- **endDate**
- **eventType**
- **itemId**

### Response 
As a response, a list of events will be fetched from the system.

### Response DTO: EventList
| Field Name          | Description                           |
|---------------------|---------------------------------------|
| eventId             | Event id                              |
| eventType           | Event type (item_create, item_upload, etc.) |
| eventCreatedUserName| Name of event created by              |
| eventCreatedUserId  | User id of event created user         |
| eventCreatedAt      | Event created date                    |
| itemId              | File id                               |
| itemName            | File name                             |

### Exceptions
| Exception                             | Error Message           |
|---------------------------------------|-------------------------|
| For user item is present or not present | Item not found          |

### Operations performed at each layer of the framework
1. Controller
   - In this REST API, Following Object is accepted - `{startDate,endDate,itemId, eventType}`

2. Business
   - Following Object is accepted - `{startDate,endDate,itemId,eventType}`
   - Start transaction
   - Call the `getEventsByDateRangeAndEventTypeAndItemId` method from eventService, and if no exception occurs, submit the transaction.

3. eventService:
   - Following params are accepted - `{startDate,endDate,itemId, eventType }`
   - This method retrieves events within a specified date range, itemId, eventType.
   - It first parses the input timestamps into the desired format (`yyyy/MM/dd HH:mm:ss:SSS`) 
   - Then, it extracts the start and end dates from the parsed timestamps for itemId, eventType.
   - Next, it iterates through each date within the range and retrieves events for each date
   - If there's only one day in the range, it fetches events for that day. For the first date, it fetches events until midnight 
   - For the last date, it fetches events until the end of the day. For dates in between, it fetches events for the entire day 
   - Finally, it returns a list of event details gathered during the process


## 1.32 /eventLog/getEventsByDateRangeAndEventTypeAndUser

### Description
The API `getEventsByDateRangeAndEventTypeAndUser` is a GET API that accepts details of events such as startDate and endDate, and retrieves a list of events including eventId, eventType, eventCreatedUserName, eventCreatedUserId, eventCreatedAt, itemId, and itemName.

### Input Parameters:
- **startDate**
- **endDate**
- **eventType**
- **userId**

### Response 
As a response, a list of events will be fetched from the system.

### Response DTO: EventList
| Field Name          | Description                           |
|---------------------|---------------------------------------|
| eventId             | Event id                              |
| eventType           | Event type (item_create, item_upload, etc.) |
| eventCreatedUserName| Name of event created by              |
| eventCreatedUserId  | User id of event created user         |
| eventCreatedAt      | Event created date                    |
| itemId              | File id                               |
| itemName            | File name                             |

### Exceptions
| Exception                             | Error Message           |
|---------------------------------------|-------------------------|
| For user item is present or not present | Item not found          |

### Operations performed at each layer of the framework
1. Controller
   - In this REST API, Following Object is accepted - `{startDate,endDate,userId,eventType }`

2. Business
   - Following Object is accepted - `{startDate,endDate,userId,eventType }`
   - Start transaction
   - Call the `getEventsByDateRangeAndEventTypeAndItemId` method from eventService, and if no exception occurs, submit the transaction.

3. eventService:
   - Following params are accepted - `{startDate,endDate, userId, eventType }`
   - This method retrieves events within a specified date range, user ID, and event type.
   - It first parses the input timestamps into the desired format (`yyyy/MM/dd HH:mm:ss:SSS`). 
   - Then, it extracts the start and end dates from the parsed timestamps for userId, itemId, and eventType.
   - Next, it iterates through each date within the range and retrieves events for each date.
   - If there's only one day in the range, it fetches events for that day. For the first date, it fetches events until midnight. 
   - For the last date, it fetches events until the end of the day. For dates in between, it fetches events for the entire day. 
   - Finally, it returns a list of event details gathered during the process.


## 1.33 /auditSetItem/addItemToAuditSet

### Description
The API `addItemToAuditSet` is a POST API used to add an item to an audit set. It accepts details such as auditSetId, itemId, itemType, itemName, accessListType, and a list of items to be added to the audit set.

### Input Parameters:
- **auditSetId**
- **itemId**
- **itemType**
- **itemName**
- **accessListType**
- List of items (id, type)
- List (userEmail, userRole)
- List of groupIds

### Response 
As a response, an item will be added to the audit set in the system.

### Exceptions
| Exception                                       | Error Message                                  |
|-------------------------------------------------|------------------------------------------------|
| If audit set is not present                     | Audit set not found                            |
| If a user is not a member of the audit set      | User is not the member of auditSet to add an item |
| If the user does not have authority to create audit set | User does not have the required role to create the audit set |

### Operations performed at each layer of the framework
1. Controller
   - In this REST API, Following Object is accepted - `{auditSetId, itemId, itemType, itemName, accessListType, list of items (id, type), list (userEmail, userRole), list of groupIds}`

2. Business
   - Following Object is accepted - `{auditSetId, itemId, itemType, itemName, accessListType, list of items}`
   - Start transaction
   - Call the `createAuditSet` method from AuditSetService, and if no exception occurs, submit the transaction

3. AuditSetService
   - Following params are accepted - `{auditSetId, itemId, itemType, itemName, accessListType, list of items}`
   - This method adds an item to an audit set.
   - First, it retrieves the audit set based on the provided ID. If the audit set doesn't exist, it throws an exception.
   - Then, it retrieves the user information based on the current user's email within the transaction context.
   - Next, it parses the access control list (ACL) JSON from the audit set. If parsing fails, it throws an exception.
   - The method checks if the current user is a member of the audit set by examining the ACL. If not, it throws an exception.
   - After that, it checks if the item is already present in the audit set. If not, it creates a new audit set item. For folders, it retrieves modification information from Box, constructs an audit set item, and adds it to the repository. For files, it directly constructs and adds the item.
   - If the item is already present, it updates the access control list and adds it to the repository.
   - For files, it also ensures that their status is set to "monitored" if not already done, updating the item status repository accordingly.
   - Finally, it returns a successful ApiResponse with the appropriate message and HTTP status, along with the added or updated items.


## 1.34 /auditSetItem/viewItemsFromSelectedAuditSet

### Description
The API `viewItemsFromSelectedAuditSet` is a GET API that accepts details of the item from an audit set based on auditSetId and retrieves data including itemId, itemName, itemType, isAllowed, auditSetRootItemId, createdAt, modifiedAt, createdBy, modifiedBy, size, createdByEmail, and modifiedByEmail.

### Input Parameters:
- **AuditSetId**: Should not be null or empty

### Response 
If all the Input Parameters met properly, information about the audit set will be fetched from the system.

### Response DTO: AuditSetItemVisibility
| Field Name          | Description                           |
|---------------------|---------------------------------------|
| itemId              | File id                               |
| itemName            | File name                             |
| itemType            | Type of item (file, folder)           |
| isAllowed           | Only allowed item from audit set      |
| auditSetRootItemId  | Audit set root item id                |
| createdAt           | Item created date                     |
| modifiedAt          | Item modified date                    |
| createdBy           | Name of item created by user          |
| modifiedBy          | Name of item modified by user         |
| size                | Size of item                          |
| createdByEmail      | Email id of created by                |
| modifiedByEmail     | Email id of modified by               |

### Exceptions
| Exception                                       | Error Message                                  |
|-------------------------------------------------|------------------------------------------------|
| For user item is present or not present         | Item not found                                 |

### Operations performed at each layer of the framework
1. Controller
   - In this REST API, Following Object is accepted - `{auditSetId}`

2. Business
   - Following Object is accepted - `{auditSetId}`
   - Start transaction
   - Call the `viewItemsFromSelectedAuditSet` method from AuditSetItemService, and if no exception occurs, submit the transaction

3. AuditSetItemService
   - Following params are accepted - `{auditSetId}`
   - The provided code retrieves audit set items and their details, including files and folders, within a specified audit set. It then iterates through each item, fetching information such as name, type, creation/modification dates, creators, and sizes.
   - Additionally, it updates the access status for audit set collaborators to "UNDER_REVIEW".
   - If an error occurs during processing, it logs the error and continues to the next item.
   - Finally, it returns an API response containing the fetched audit set items' details or a message indicating an empty item list.


## 1.35 /auditSetItem/getAllowListFromAuditSet

### Description
The API `getAllowListFromAuditSet` is a GET API that retrieves details of the allow list item from an audit set based on auditSetId and itemId. It returns the allow list with itemId and itemType.

### Input Parameters:
- **AuditSetId**: Should not be null or empty

### Response 
As a response, get an approved list of audit sets from the system.

### Operations performed at each layer of the framework
1. Controller
   - In this REST API, Following Object is accepted - `{auditSetId, itemId}`

2. Business
   - Following Object is accepted - `{auditSetId, itemId}`
   - Start transaction
   - Call `getAllowListFromAuditSet` method from AuditSetItemService, and if no exception occurs, submit the transaction

3. AuditSetItemService
   - Following params are accepted - `{auditSetId, itemId}`	
   - Retrieves all audit set items associated with a specific user within a distributed transaction context.
   - It first fetches the user details based on the email obtained from the security context within the provided transaction.
   - Then, it retrieves all audit set items related to this user from the repository within the transaction.

## 1.36 /auditSetItem/getItemFromAuditSet

### Description
The API `getItemFromAuditSet` is a GET API that accepts details of the item from the audit set based on auditSetId, itemId, and subfolderId. It retrieves data including itemId, itemName, itemType, isAllowed, auditSetRootItemId, createdAt, modifiedAt, createdBy, modifiedBy, size, createdByEmail, and modifiedByEmail.

### Input Parameters:
- **auditSetId**
- **itemId**
- **subFolderId**

### Response 
As a response, fetch the item information from the audit set.

### Response DTO: AuditSetItemVisibility
| Field Name          | Description                           |
|---------------------|---------------------------------------|
| itemId              | File id                               |
| itemName            | File name                             |
| itemType            | Type of item (file, folder)           |
| isAllowed           | Only allowed item from audit set      |
| auditSetRootItemId  | Audit set root item id                |
| createdAt           | Item created date                     |
| modifiedAt          | Item modified date                    |
| createdBy           | Name of item created by user          |
| modifiedBy          | Name of item modified by user         |
| size                | Size of item                          |
| createdByEmail      | Email id of created by                |
| modifiedByEmail     | Email id of modified by               |

### Operations performed at each layer of the framework
1. Controller
   - In this REST API, Following Object is accepted - `{auditSetId, itemId, subfolderId}`

2. Business
   - Following Object is accepted - `{auditSetId, itemId, subfolderId}`
   - Start transaction
   - Call `getItemFromAuditSet` from AuditSetItemService, and if no exception occurs, submit the transaction

3. AuditSetItemService
   - Following params are accepted - `{auditSetId, itemId, subFolderId}`	
   - This method retrieves item information from an Audit Set. It first checks if the item exists in the Audit Set. If not, it returns an empty response. Then, it retrieves basic item information from JSON stored in the Audit Set.
   - It iterates through items in a specified folder, checking if each item is allowed or denied based on the Audit Set.
   - It updates lists of denied and allowed items accordingly. If all items are allowed, it updates the Audit Set with the new allowed items.
   - Finally, it returns the updated list of item visibilities.


## 1.37 /auditSet/createAuditSet

### Description
The API `createAuditSet` is a POST API that accepts details of the audit set, including the name of the audit set, description, list of users (userEmail, userRole), and list of groupIds, to create an audit set.

### Input Parameters:
- **Name of the Audit set**: Should be non-null or empty. Size must be less than 64 chars.
- **Description of audit set**: Description is optional.
- **User List(userEmail,userRole)**
- **List of groupIds**

### Response 
As a response, a new audit set will be created in the system.

### Exceptions
- For user not present: User not found
- If the audit set is empty or null: Audit set name should not be empty
- For duplicate name: Please use another audit set name as there exists an audit set with the same name
- If the user does not have authority to create an audit set: User does not have the required role to create the audit set
- If having other error: An error occurred while creating the audit set

### Operations performed at each layer of the framework
1. Controller
   - In this REST API, Following Object is accepted - `{name, description, userList(userEmail, userRole), list of groupIds}`

2. Business
   - Following Object is accepted - `{name, description, userList(userEmail, userRole), list of groupIds}`
   - Start transaction
   - Call the `createAuditSet` method from AuditSetService, and if no exception occurs, submit the transaction

3. AuditSetService
   - Following params are accepted - `{name, description, userList(userEmail, userRole), list of groupIds}`
   - Creates a new audit set based on input data, ensuring the uniqueness of the audit name, user roles, and permissions. It retrieves the user by email, formats the date, and checks for duplicate names. 
   - If the user has the necessary role, it constructs the audit set object with collaborators, validates and adds groups, and finally creates the audit set. 
   - The method returns an API response indicating success or failure.

## 1.38 /auditSet/deleteAuditSet

### Description
The API `deleteAuditSet` is a DELETE API used to delete an audit set based on the provided auditSetId.

### Input Parameters:
- **Audit set id**: Should be checked if audit set id is present or not 

### Response 
If all the Input Parameters were met properly, an audit set would be deleted successfully.

### Exceptions
- For checking if audit set is present or not: Audit set not found
- For user not present: User not found
- If the user does not have authority to delete an audit set: User does not have the required role to delete the audit set
- If having other error: An error occurred while deleting the audit set

### Operations performed at each layer of the framework
1. Controller
   - In this REST API, Following Object is accepted - `{auditSetId}`

2. Business
   - Following Object is accepted - `{auditSetId}`
   - Start transaction
   - Call the `deleteAuditSet` method from AuditSetService, and if no exception occurs, submit the transaction

3. AuditSetService
   - Following params are accepted - `{auditSetId}`	
   - Retrieve the user and audit set objects.
   - Check if they exist; if not, throw a NotFoundException.
   - Verify if the audit set is already deleted; if so, return ApiResponse.
   - Deserialize ACL JSON into a Collaborator object.
   - Fetch user roles from the security token.
   - Determine if the user is the owner or co-owner.
   - If authorized, delete the audit set and associated collaborators.
   - Clear audit group mappings and update the audit set status.
   - Return a response indicating success or failure.


## 1.39 /auditSet/getMyAuditSetList

### Description
The API `getMyAuditSetList` is a GET API that accepts details of the audit set, including auditSetId, auditSetName, description, ownedBy, createdAt, accessStatus, and isFavourite.

### Input Parameters:
- Current user login

### Response 
As a response, the user will get a list of audit sets from the system.

### The Response DTO: AuditSetList

| Field Name   | Description                               |
|--------------|-------------------------------------------|
| Id           | AuditSetId                                |
| name         | Audit set name                            |
| description  | Audit set description description         |
| ownedBy      | Owner email of audit set                  |
| createdAt    | Date when audit set is created            |
| accessStatus | Access status of file (UNDER_REVIEW, NEWLY_ADDED) |
| isFavourite  | To be implemented                         |

### Exceptions
- For user not present: User not found
- If having other errors: Error when getting my audit setlist

### Operations performed at each layer of the framework
1. Controller
   - In this REST API, Following Object is accepted - `{Token(currentUser)}`

2. Business
   - Following Object is accepted - `{Token(currentUser)}`
   - Start transaction
   - Call the `getMyAuditSetList` method from AuditSetService, and if no exception occurs, submit the transaction

3. AuditSetService:
   - Following params are accepted - `{Token(currentUser)}`
   - Retrieve the user object using the provided email from the userRepository.
   - Check if the user exists; if not, throw a NotFoundException.
   - Based on the user's role, fetch audit set lists:
     - For AUDIT_ADMIN role: Retrieve all audit sets.
     - For GENERAL_USER role: Retrieve audit sets where the user is a collaborator.
     - For EXTERNAL_AUDITOR role: Retrieve audit sets associated with user groups.
   - Construct AuditSetLists objects containing relevant information for each audit set.
   - Return ApiResponse with the constructed audit set lists and appropriate status.

## 1.40 /auditSet/viewExternalAuditorAccessLog

### Description
The API `viewExternalAuditorAccessLog` is a GET API that accepts details of the external auditor event log, such as auditSetId, itemId, and userEmail, and retrieves data including ownerName, eventType, itemType, and eventDate.

### Input Parameters:
- auditSetId
- itemId
- userEmail

### Response 
As a response, get a list of external auditor logs from the system.

### The Response DTO: ExtAuditorAccessLog

| Field Name | Description              |
|------------|--------------------------|
| ownerName  | Owner name of file id    |
| eventType  | Event types             |
| itemType   | Item type               |
| ownedBy    | Owner email id          |
| eventDate  | Date when event is created |

### Exceptions
- If having other errors: Error when getting external auditor access

### Operations performed at each layer of the framework
1. Controller
   - In this REST API, Following Object is accepted - `{auditSetId, itemId, userEmail}`

2. Business
   - Following Object is accepted - `{auditSetId, itemId, userEmail}`
   - Start transaction
   - Call the `getMyAuditSetList` method from AuditSetService, and if no exception occurs, submit the transaction

3. AuditSetService:
   - Following params are accepted - `{auditSetId, itemId, userEmail}`
   - Fetch information about the external auditor event log
   - Check the event log of an external auditor in the database
   - Retrieve the event logs for a specific item in an audit set for a particular user
   - If the list of external auditor logs is not empty, retrieve the list successfully
   - Retrieve ExtAuditorAccessLog list
   - If the list is empty, return an empty list


## 1.41 /auditSet/updateAuditSetInfo

### Description
The API `updateAuditSetInfo` is a Put API used to update audit set information, including the name of the audit set, description, list of users (userEmail, userRole), and list of groupIds.

### Input Parameters:
- Name of the Audit set: Should be non-null or empty. Size must be less than 64 chars.
- Description of audit set: Description is optional.
- User List(userEmail, userRole)
- List of groupIds

### Response 
As a response, an audit set will be updated in the system.

### Exceptions
- For user not present: User not found
- If audit set is empty or null: Audit set name should not be empty
- For duplicate name: Please use another audit set name as there exists an audit set with the same name
- If the user does not have authority to create an audit set: User does not have the required role to create the audit set
- If having other error: An error occurred while creating the audit set

### Operations performed at each layer of the framework
1. Controller
    - In this REST API, Following Object is accepted - `{name, description, userList(userEmail, userRole), list of groupIds}`

2. Business
    - Following Object is accepted - `{name, description, userList(userEmail, userRole), list of groupIds}`
    - Start transaction
    - Call the `createAuditSet` method from AuditSetService, and if no exception occurs, submit the transaction

3. AuditSetService:
    - Following params are accepted - `{name, description, userList(userEmail, userRole), list of groupIds}`
    - Retrieve information about the current user.
    - Ensure that the audit set name is provided and not empty.
    - Fetch the list of existing audit sets to check for the uniqueness of the audit set name.
    - Verify if the current user, determined from the context, possesses owner authority to update the audit set.
    - Only owners have the privilege to update audit sets.
    - Create a new collaborator user instance.
    - Configure the collaborator lists for co-owners, members, and reviewers.
    - Assign roles accordingly: audit_admin is added as a co-owner, general_user is added as a member, and external_auditor is added as a reviewer in the audit set.
    - Replace the existing list of collaborators with the new one.
    - Convert the collaborator object into JSON format.
    - Embed the JSON object within the audit set.
    - Create a new audit set and store it in the AuditSetRepository.
    - Generate a new AuditSetCollaborator and save it in the AuditSetCollaboratorRepository.
    - Confirm the successful update of the audit set.

## 1.42 /auditSet/validateAuditSet

### Description
The API `validateAuditSet` is a GET API that validates an audit set based on the provided auditSetId.

### Input Parameters:
- AuditSetId

### Response 
As a response, an audit set will be validated.

### Exceptions
- Exception: Something Went Wrong !!

### Operations performed at each layer of the framework
1. Controller
    - In this REST API, Following Object is accepted - `{auditSetId}`

2. Business
    - Following Object is accepted - `{auditSetId}`
    - Start transaction
    - Call the `createAuditSet` method from AuditSetService, and if no exception occurs, submit the transaction

3. AuditSetService:
    - Following params are accepted - `{auditSetId}`
    - Get audit set items for the provided ID.
    - Handle CRUD exceptions with a generic error message.
    - Check if audit set item retrieval was successful; if not, return the response.
    - Prepare to collect verification statuses for each item.
    - Establish a Box API connection for enterprise access.
    - Iterate through each audit set item:
        - Retrieve the corresponding item object from the repository.
        - Log and throw an exception if the item is not found.
        - For file items:
            - Manage item status, update validation time, and check tampering status.
            - Collect file metadata and add verification status.
        - For non-file items:
            - Parse JSON data into basic item information.
            - Validate folder contents and update verification statuses.
    - Summarize the tampered files for the response.
    - Build and return a response with total files checked, tampered file count, and details.


## 1.43 /auditSet/updateAuditSetsForItemId

### Description
The API `updateAuditSetsForItemId` is a PUT API used to update audit set information for a specific item, including the name of the item and list of AuditSetLists.

### Input Parameters:
- Name of the Audit set: Should be non-null or empty. Size must be less than 64 chars.
- AuditSetLists (auditSetId, auditSetName, description, ownedBy, createdAt, accessStatus, isFavourite, isItemIdAdded)

### Response 
As a response, an audit set for the item id will be updated in the system.

### Operations performed at each layer of the framework
1. Controller
    - In this REST API, Following Object is accepted - `{itemName, AuditSetLists (auditSetId, auditSetName, description, ownedBy, createdAt, accessStatus, isFavourite, isItemIdAdded)}`

2. Business
    - Following Object is accepted - `{itemName, AuditSetLists (auditSetId, auditSetName, description, ownedBy, createdAt, accessStatus, isFavourite, isItemIdAdded)}`
    - Start transaction
    - Call the `createAuditSet` method from AuditSetService, and if no exception occurs, submit the transaction

3. AuditSetService:
    - Following params are accepted - `{itemName, AuditSetLists (auditSetId, auditSetName, description, ownedBy, createdAt, accessStatus, isFavourite, isItemIdAdded)}`
    - Retrieve the current user from the repository using the email associated with the authenticated user.
    - Iterate through each audit set in the provided update request.
    - If an item is marked as added in the audit set:
        - Check if the item already exists in the audit set.
        - If not, create a new audit set item with the provided details and the current timestamp.
        - If it exists, update its creation timestamp.
    - If an item is not marked as added in the audit set:
        - Check if the item exists in the audit set.
        - If it does, delete the item from the audit set.
    - Return a successful response indicating that the item has been updated in the audit set.

## 1.44 /auditSet/getMyAuditSetListForItemId

### Description
The API `getMyAuditSetListForItemId` is a GET API that accepts details of the audit set using itemId and returns a list of audit sets associated with that item.

### Input Parameters:
- ItemId

### Response 
As a response, the user will get a list of audit sets for the itemId from the system.

### Operations performed at each layer of the framework
1. Controller
    - In this REST API, Following Object is accepted - `{itemId}`

2. Business
    - Following Object is accepted - `{itemId}`
    - Start transaction
    - Call the `getMyAuditSetList` method from AuditSetService, and if no exception occurs, submit the transaction
    - Attempt to retrieve the user's audit set list using their authentication name.
    - Iterate through each audit set in the response.
    - For each audit set, check if the specified item exists within it.

3. AuditSetService:
    - Following params are accepted - `{itemId}`
    - Retrieve the user object using the provided email within the current transaction.
    - If the user is not found, throw a NotFoundException.
    - Initialize an empty list to store audit set lists.
    - Determine the roles of the user from their authorities.
    - If the user is an audit admin, retrieve all audit set lists.
    - If the user is a general user, retrieve audit set lists where they are collaborators.
    - If the user is an external auditor, retrieve audit set lists for collaborators and their user groups.
    - Combine the retrieved audit set lists.
    - If audit set lists are found, return a successful ApiResponse with HttpStatus OK.
    - If no audit set lists are found, return a successful ApiResponse with an empty list and HttpStatus OK.
    - Handle exceptions such as NotFoundException and other unexpected errors, returning appropriate ApiResponse objects with error messages and corresponding HttpStatus codes.


## 1.45 /auditSetCollaborator/changeAuditSetOwner

### Description
The API `changeAuditSetOwner` is a PUT API used to change the owner of an audit set.

### Input Parameters:
- auditSetId: Should be non-null or empty.
- newOwnerId

### Response 
As a response, the owner of the audit set will be changed in the system.

### Exceptions
- Audit Set Not Found: If the audit set is not present.
- New owner not found: If the new owner is not present.
- Only owner and co-owner can change audit set owner: If the user does not have the authority to change the owner.
- The new owner is already an owner: If the new owner is already an owner of the audit set.
- The new owner must be a co-owner before becoming the owner: If the new owner is not a co-owner of the audit set.

### Operations performed at each layer of the framework
1. Controller
    - In this REST API, Following Object is accepted - `{auditSetId, newOwnerId}`

2. Business
    - Following Object is accepted - `{auditSetId, newOwnerId}`
    - Start transaction
    - Call the `changeAuditSetOwner` method from AuditSetCollaboratorService, and if no exception occurs, submit the transaction

3. AuditSetService:
    - Following params are accepted - `{auditSetId, newOwnerId}`
    - Get auditSet object from auditSetId
    - Check permission to see if the user is the owner or co-owner of this operation.
    - Fetch the user details for the new owner.
    - Update the ACL JSON to change the owner and make the current owner a co-owner.
    - Update the AuditSet with the new ACL JSON and set the new owner details.
    - Update the role of current owner to co-owner and new owner to owner.

## 1.46 /auditSetCollaborator/getCollaboratorsForAuditSet

### Description
The API `getCollaboratorsForAuditSet` is a GET API that retrieves a list of audit set collaborators for a given audit set.

### Input Parameters:
- auditSetId: Should not be null or empty.

### Response 
As a response, a list of audit set collaborators will be fetched from the system.

### Response DTO: AuditSetICollaborators

#### Field name	Description
- ownedBy: Owned by (userId, username, emailId, role)
- coOwners: Co-owners list with (userId, username, emailId, role)
- members: Members list with (userId, username, emailId, role)
- reviewers: Reviewers list with (userId, username, emailId, role)
- auditGroupListForAuditSetList: Audit group list with (auditGroupId, auditGroupName, description)

### Exceptions
- Audit Set Not Found: If the audit set is not present.

### Operations performed at each layer of the framework
1. Controller
    - In this REST API, Following Object is accepted - `{auditSetId}`

2. Business
    - Following Object is accepted - `{auditSetId}`
    - Start transaction
    - Call the `getCollaboratorForAuditSet` method from the auditSet Collaborator service, and if no exception occurs, submit the transaction

3. AuditSetCollaboratorService:
    - Following params are accepted - `{auditSetId}`
    - Retrieve the AuditSet using its ID.
    - Ensure the AuditSet exists and is not deleted.
    - Parse the ACL JSON into a collaborator object.
    - Extract the list of audit groups associated with the audit set.
    - Construct an AuditSetCollaboratorList with the collaborator and audit group lists.
    - Return ApiResponse with the constructed AuditSetCollaboratorList.
    - Handle exceptions and return the appropriate ApiResponse for errors.



## 1.47 /auditGroup/createAuditGroup

### Description
The API `createAuditGroup` is a POST API used to create an audit group.

### Input Parameters:
- Name of the Audit group: Should be non-null or empty. Size must be less than 64 chars.
- Description of audit group: Description is optional.
- List of user emails

### Response 
As a response, a new audit group will be created in the system.

### Exceptions
- User not found: If the user is not present.
- Audit group name should not be empty: If the audit group is empty or null.
- Please use another audit group name as there exists an audit group with the same name: For duplicate name.
- User does not have the required role to create the audit group: If the user does not have authority to create an audit group.
- An error occurred while creating the audit group: For other errors.

### Operations performed at each layer of the framework
1. Controller
    - In this REST API, Following Object is accepted - `{name, description, List of users(userEmails)}`

2. Business
    - Following Object is accepted - `{name, description, List of users(userEmails)}`
    - Start transaction
    - Call the `createAuditGroup` method from AuditGroupService, and if no exception occurs, submit the transaction

3. AuditGroupService:
    - Following params are accepted - `{name, description, List of users(userEmails)}`
    - Retrieve the current user object using the provided email from the user repository.
    - Validate the provided audit group name and check for duplicates.
    - Fetch user roles from the security token.
    - If the user has the AUDIT_ADMIN role:
        - Set the owner of the audit group.
        - Set members from the provided auditGroupUserList.
        - Convert the list to JSON format.
        - Generate a unique audit group ID.
        - Create an AuditGroup object with the provided data.
        - Save the audit group in the database.
        - Save member details in the userAuditGroup table.
    - Return ApiResponse indicating successful creation of the audit group or failure with error messages and HTTP status codes.

## 1.48 /auditGroup/updateAuditGroup

### Description
The API `updateAuditGroup` is a PUT API used to update an existing audit group.

### Input Parameters:
- Name of the Audit group: Should be non-null or empty. Size must be less than 64 chars.
- Description of audit group: Description is optional.
- List of user emails

### Response 
As a response, an audit group will be updated in the system.

### Exceptions
- Audit group not found: If the audit group is not present.
- User not found: If the user is not present.
- Audit group name should not be empty: If the audit group is empty or null.
- Please use another audit group name as there exists an audit group with the same name: For duplicate name.
- User does not have the required role to create the audit group: If the user does not have authority to create an audit group.
- An error occurred while creating the audit group: For other errors.

### Operations performed at each layer of the framework
1. Controller
    - In this REST API, Following Object is accepted - `{name, description, List of users(userEmails)}`

2. Business
    - Following Object is accepted - `{name, description, List of users(userEmails)}`
    - Start transaction
    - Call the `updateAuditGroup` method from AuditGroupService, and if no exception occurs, submit the transaction

3. AuditGroupService:
    - Following params are accepted - `{name, description, List of users(userEmails)}`
    - Retrieve the audit group object using the provided auditGroupId from the auditGroupRepository.
    - Validate the provided audit group name and check for duplicates.
    - Fetch user roles from the security token.
    - If the user has the AUDIT_ADMIN role:
        - Update the audit group name and description.
        - Add new members and update existing members accordingly.
        - Remove members as per the provided update.
        - Update the audit group's member list JSON in the database.
        - Update the associated audit set mappings and their group details.
    - Return ApiResponse indicating a successful update or failure with appropriate messages and HTTP status codes.


## 1.49 /auditGroup/deleteAuditGroup

### Description
The API `deleteAuditGroup` is a DELETE API used to delete an audit group.

### Input Parameters:
- Audit group id: Check if the audit group id is present.

### Response 
If all the input parameters are met properly, an audit group will be successfully deleted from the system.

### Exceptions
- Audit group not found: If the audit group is not present.
- User not found: If the user is not present.
- User does not have the required role to delete an audit group: If the user does not have authority to delete the audit group.
- An error occurred while deleting the audit group: For other errors.

### Operations performed at each layer of the framework
1. Controller
    - In this REST API, Following Object is accepted - `{auditGroupId}`

2. Business
    - Following Object is accepted - `{auditGroupId}`
    - Start transaction
    - Call the `deleteAuditGroup` method from AuditGroupService, and if no exception occurs, submit the transaction

3. AuditGroupService:
    - Following params are accepted - `{auditGroupId}`
    - Retrieve the audit group object using the provided auditGroupId from the auditGroupRepository.
    - Deserialize the JSON representation of member emails into a list of GroupUserPrivileges objects.
    - Fetch user roles from the security token.
    - Verify if the user has the necessary authority (AUDIT_ADMIN role) to delete the audit group.
    - If authorized, iterate through each member email:
        - Delete user's association with the audit group from the userAuditGroupRepository.
        - Retrieve all audit set mappings associated with the audit group.
        - Delete all audit set mappings related to the audit group from the auditGrpAuditSetMappingRepository.
        - Mark the audit group as deleted by setting the isDeleted flag to true.
        - Update the audit group in the database with the new status.
    - Return an ApiResponse indicating successful deletion of the audit group.

## 1.50 /auditGroup/getListOfAuditGroupMembers

### Description
The API `getListOfAuditGroupMembers` is a GET API that retrieves a list of members belonging to an audit group.

### Input Parameters:
- auditGroupId: The audit group id is important for fetching the audit group member list.

### Response 
As a response, a list of audit group members will be fetched from the system.

### Exceptions
- Audit group not found: If the audit group is not present.

### Operations performed at each layer of the framework
1. Controller
    - In this REST API, Following Object is accepted - `{auditGroupId}`

2. Business
    - Following Object is accepted - `{auditGroupId}`
    - Start transaction
    - Call the `getListAuditSet` method from AuditSetService, and if no exception occurs, submit the transaction

3. AuditSetService:
    - Following params are accepted - `{auditGroupId}`
    - Retrieve information about the audit group. 
    - Verify if the audit group is present or not.
    - Convert memberList json into a list of groupUserPrivileges.
    - Fetch a list of audit group member lists from the memberList json.
    - Obtain the list of audit groups using the auditGroupMemberList object.








