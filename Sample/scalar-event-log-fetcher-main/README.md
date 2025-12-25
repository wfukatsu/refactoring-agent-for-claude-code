# Scalar Auditor for BOX

## Overview
Scalar Auditor for BOX is an application integrated with the BOX application, aimed at storing user event logs on files managed by BOX outside the BOX application. These logs are analyzed in various ways, and the data is accessible to external auditors for auditing important files. The application also includes necessary administrative functionalities for users.

Developed using Spring Boot for the backend and React JS for the frontend, Scalar Auditor for BOX utilizes Cassandra as its database, managed by ScalarDB. File records are managed by ScalarDL to detect file tampering using its file validation feature.

## Features Implemented
1. **Store User Event Logs and Display Data:**
   - Store user event logs in the database.
   - View event logs after applying filters such as start date-time, end date-time, type of user event, and user who performed the events.

2. **View File Item Details:**
   - Display file details including name, creator, creation date-time, updater, update date-time, file size, and validation status.
   - View files with the same hash.
   - Access all versions of the selected file.

3. **Manage Organization User Roles:**
   - Manage organization users (BOX app users) with roles as Audit Admin and General User.

4. **Manage External Users:**
   - Create external auditors with name, email ID, and organization name.
   - Update external auditor details.
   - Delete external auditor users.

5. **Assign Files or Folders for Auditing:**
   - Create Audit Sets, groups of files and folders for auditing.
   - Select files or folders from BOX and add them to Audit Sets.
   - Allow partial selections of files and folders for audit assignment.

6. **Manage Audit Sets:**
   - Create and manage Audit sets.
   - Assign collaborators and reviewers (external auditors) to Audit sets.

7. **View Logs of External Auditor Operations:**
   - View access logs of external auditors by BOX users.

8. **Preview and Download Files During Auditing.**

9. **File Validation:**
   - Validate files to check for tampering.
   - Validate Audit Sets to identify any tampered items.

For detailed setup and installation instructions, refer to the following documents:

- [BOX Application Types Used](Documentation/docs/box-application.md)
- [Installation and Setup Procedure for Scalar Auditor for BOX (User OAuth 2.0 Authentication)](Documentation/docs/installation-and-setup-procedure.md)
- [Setup of Scalar Box Event Log Fetcher App - Client Credentials Grant with Server Authentication](Documentation/docs/installation-and-setup-procedure2.md)
- [Scalar DB Setup](Documentation/docs/scalarDB.md)
- [Application Configuration Guide](Documentation/docs/application-guide.md)
- [Scalar DL Setup](Documentation/docs/scalarDL.md)
- [ScalarDL Client Certificate and Contract Registration](Documentation/docs/springbootBackedDL.md)
- [SpringBoot Application Setup](Documentation/docs/springboot-application-setup.md)
- Frontend Application Setup:
  - [Scalar-WebApp-Integration-Menu](Documentation/docs/Scalar-WebApp-Integration-Menu.md)
  - [Scalar-Box-WebApp](Documentation/docs/Scalar-Box-WebApp.md)
- [Features Planned for Release V3.0](Documentation/docs/features.md)