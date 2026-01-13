package com.scalar.events_log_tool.application.utility;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxCCGAPIConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Slf4j
public class BoxUtility {


    private final String boxEnterpriseId;
    private final String boxServerAuthenticationClientId;
    private final String boxServerAuthenticationSecretId;

    public BoxUtility(@Value("${box.enterprise-id}") String boxEnterpriseId, @Value("${box.server-authentication.client-id}") String boxServerAuthenticationClientId, @Value("${box.server-authentication.client-secret}") String boxServerAuthenticationSecretId) {
        this.boxEnterpriseId = boxEnterpriseId;
        this.boxServerAuthenticationClientId = boxServerAuthenticationClientId;
        this.boxServerAuthenticationSecretId = boxServerAuthenticationSecretId;
    }


    public BoxAPIConnection getBoxEnterpriseConnection() {

        BoxAPIConnection connection = BoxCCGAPIConnection.applicationServiceAccountConnection(
                boxServerAuthenticationClientId,
                boxServerAuthenticationSecretId,
                boxEnterpriseId
        );
        connection.asSelf();
        return connection;
    }
}
