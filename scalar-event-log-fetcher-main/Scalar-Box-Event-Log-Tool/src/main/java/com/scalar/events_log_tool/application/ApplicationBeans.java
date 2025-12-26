package com.scalar.events_log_tool.application;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxCCGAPIConnection;
import com.box.sdk.EventStream;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.service.TransactionFactory;
import com.scalar.dl.client.config.ClientConfig;
import com.scalar.dl.client.service.ClientService;
import com.scalar.dl.client.service.ClientServiceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

@Configuration
@Slf4j
public class ApplicationBeans {

    public static final String SCALARDB_PROPERTIES =
            System.getProperty("user.dir") + File.separator + "scalardb.properties";
    public static final String CLIENT_PROPERTIES =
            System.getProperty("user.dir") + File.separator + "client.properties";
    private final String boxEnterpriseId;
    private final String boxServerAuthenticationClientId;
    private final String boxServerAuthenticationSecretId;

    public ApplicationBeans(@Value("${box.enterprise-id}") String boxEnterpriseId, @Value("${box.server-authentication.client-id}") String boxServerAuthenticationClientId, @Value("${box.server-authentication.client-secret}") String boxServerAuthenticationSecretId) {
        this.boxEnterpriseId = boxEnterpriseId;
        this.boxServerAuthenticationClientId = boxServerAuthenticationClientId;
        this.boxServerAuthenticationSecretId = boxServerAuthenticationSecretId;
    }

    @Bean
    public static ClientService getService() {
        ClientServiceFactory factory = new ClientServiceFactory();

        ClientService service = null;
        try {
            log.info("Creating bean ..");
            service = factory.create(new ClientConfig(new File(CLIENT_PROPERTIES)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return service;
    }


    @Bean
    DistributedTransactionManager createScalarDBTransactionManager() throws IOException {
        TransactionFactory factory = TransactionFactory.create(SCALARDB_PROPERTIES);
        log.info("Properties File Found On Path:" + SCALARDB_PROPERTIES);
        log.info(" DB Connection Bean Created");
        return factory.getTransactionManager();
    }


    @Bean("box-connection-for-operation")
    public BoxAPIConnection getBoxEnterpriseConnection() {

        BoxCCGAPIConnection connection = BoxCCGAPIConnection.applicationServiceAccountConnection(
                boxServerAuthenticationClientId,
                boxServerAuthenticationSecretId,
                boxEnterpriseId
        );
        connection.asSelf();
        return connection;
    }


    @Bean
    public EventStream eventStream(BoxAPIConnection connection) {
        // Create and configure your EventStream instance here
        return new EventStream(connection);
    }
}
