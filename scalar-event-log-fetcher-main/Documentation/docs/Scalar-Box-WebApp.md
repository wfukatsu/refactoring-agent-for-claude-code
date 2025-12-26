## Scalar-Box-WebApp UI Setup

### Overview

Scalar-Box-WebApp UI is a user interface application that interacts with Scalar Auditor for Box and Scalar Box Event Log Fetcher App. It provides a graphical interface for users to perform various tasks related to auditing files and managing events.

### Setup Instructions

1. **Clone Repository:**
   - Clone the repository from GitLab.

2. **Navigate to Directory:**
   - Use the command: `$ cd clone/path/to/Scalar-Box-WebApp` to navigate to the directory.

3. **System Prerequisites:**
   - Ensure you have Node version 18.17.0 installed.

4. **Configure Constants:**
   - Open the `constant.js` file available in the path `Scalar_BOX_UI\src\utils`.
   - Configure the following constants of web integration app details:
     - `CLIENT_ID`
     - `CLIENT_SECRET`
     - `REDIRECT_URL`
     - `BASE_URL`

   ![Constant Configuration](assets/images/frontend2.png)

5. **Commands to Build Application:**
   - Run `npm install` to install all the required dependencies.
   - Run `npm run build` to build the application.

### Notes

- Ensure to add your domain in the Configuration > OAuth 2.0 Redirect URI section.
- Add your domain in the Configuration > CORS Domains Section of Scalar-Box-Event-Log-Fetcher-App for Preview Functionality usage.