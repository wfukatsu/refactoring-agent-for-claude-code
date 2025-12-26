## Scalar DL Setup

1. **Navigate to Scalar DL Directory:** Go to the `ScalarDL-Event-Log-Fetcher-Setup` folder.

2. In the 'fixture' folder, we have included certificates for both the ledger and auditor. If needed, you can generate new certificates for either the ledger or auditor and replace the existing ones in this folder.

3. **Start Scalar DL with Docker Compose:** To start Scalar DL with Docker Compose, use the following command:
   
    ```bash
    $ sudo docker compose -f docker-compose-ledger-auditor.yml up -d
    ```

4. **Stop Scalar DL Docker Compose:** To stop Scalar DL Docker Compose, run:
   
    ```bash
    $ sudo docker compose -f docker-compose-ledger-auditor.yml down
    ```

    Keep the Docker Compose file running.




