## Scalar DB Setup

To set up Scalar DB and Scalar DL, follow these steps:

### Scalar DB Setup:

1. **Clone Repository:** Clone the repository from the provided link:

    ```bash
    $ git clone https://gitlab.com/scalarlabs/partners/percept-consulting-services/scalar-event-log-fetcher.git
    ```

2. **System Prerequisites:** Ensure you have the following installed:
The setup is compatible with either a Windows machine running WSL or any system that supports Linux.

   - Java 17
   - Java 8 (for contract .class file building)
   - Docker
   - Cassandra
   - Gradle


3. **Navigate to Directory:** Go to the directory:

    ```bash
    $ cd clone/path/to/Scalar-Box-Event-Log-Tool
    ```

4. **Start Cassandra:** Ensure your Cassandra database is running.

5. **Configure scalardb.properties:** Modify the scalardb.properties file according to your database settings. Adjust properties like scalar.db.contact_points, scalar.db.username, scalar.db.password.

6. **Execute Schema Loading:** Run the schema loading command:

    ```bash
    $ java -jar schema-loader/scalardb-schema-loader-3.14.0.jar --coordinator -c=scalardb.properties -f=schema-loader/scalar_box_schema.json --replication-factor=1 --replication-strategy=SimpleStrategy
    ```

    Note: Adjust -replication-factor as needed.
    This Command will load the schema to the Cassandra Database.



