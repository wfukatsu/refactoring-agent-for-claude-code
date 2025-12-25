## Scalar-WebApp-Integration-Menu Setup

### Overview

Scalar-WebApp-Integration-Menu is an application designed to work seamlessly with Box, allowing users to select files or folders and open them using Scalar Auditor for Box.

### Setup Instructions

1. **Clone Repository:**
   - Clone the repository from GitLab.

2. **Navigate to Directory:**
   - Use the command: `$ cd clone/path/to/Scalar-WebApp-Integration-Menu` to navigate to the directory.

3. **System Prerequisites:**
   - Make sure you have Node version 18.17.0 installed on your system.

4. **Configure Constants:**
   - Open the `constant.js` file located in the path `SCALAR-INTEGRATED-MENU\src\utils`.
   - In this file, configure the following constants of web integration app details:
     - `CLIENT_ID`
     - `CLIENT_SECRET`
     - `REDIRECT_URL`
     - `SCALAR_BOX_URL`
     - `BASE_URL`

   ![Constant Configuration](assets/images/frontend.png)

5. **Commands to Build Application:**
   - Run `npm install` to install all the required dependencies.
   - After installation, run `npm run build` to build the application.

### Note

- Ensure that you add the redirect URL in the Box app (Scalar Auditor for Box) developer console. This is crucial for the application's functionality.


