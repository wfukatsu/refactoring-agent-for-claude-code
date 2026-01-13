## ScalarDL Client Certificate and Contract Registration

1. **Navigate to Directory:** Go to the directory `Scalar-Box-Event-Log-Tool-Setup`.

2. **Client Certificates:**
   - Within the 'conf' folder, you'll find client (application) certificates named 'client.pem' and 'client-key.pem'.
   - If necessary, you can generate new certificates.
   - Ensure to replace the existing certificates with the new ones in the 'config' folder also in the Scalar-Box-Event-Log-Tool module.

3. **Configure client.properties:** Modify the 'client.properties' file to update the ledger host and auditor host configurations.

4. **Execute Registration Command:** While inside the 'Scalar-Box-Event-Log-Tool-Setup' folder, execute the following command:

    ```bash
    $pwd
    ```

    This command will give you the exact path of the current system.

6. **Register Client Certificates and Contracts:** After obtaining the path, run the following command:

    ```bash
    $SCALAR_SDK_HOME=/path/to/Scalar-Box-Event-Log-Tool-Setup ./register
    ```

    This command will register the client certificates and contracts to ScalarDL.