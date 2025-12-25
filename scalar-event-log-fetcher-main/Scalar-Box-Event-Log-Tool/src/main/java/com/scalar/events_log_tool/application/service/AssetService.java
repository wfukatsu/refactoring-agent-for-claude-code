package com.scalar.events_log_tool.application.service;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.scalar.dl.client.exception.ClientException;
import com.scalar.dl.ledger.model.ContractExecutionResult;
import com.scalar.events_log_tool.application.constant.TamperingStatusType;
import com.scalar.events_log_tool.application.dto.CustomEventData;
import com.scalar.events_log_tool.application.exception.GenericException;
import com.scalar.events_log_tool.application.repository.ScalardlRepository;
import com.scalar.events_log_tool.application.responsedto.ApiResponse;
import com.scalar.events_log_tool.application.utility.BoxUtility;
import com.scalar.events_log_tool.application.utility.GenericUtility;
import com.scalar.events_log_tool.application.utility.Translator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

@Service
@Slf4j
public class AssetService {
    private static final String BOX_AND_ENTERPRISE_ID = "BOX-1098769557-";
    private static final String OBJECT_ID = "object_id";
    private static final String PUT_CONTRACT = "object.Put";
    private static final String VALIDATE_CONTRACT = "object.Validate";


    private final ScalardlRepository scalardlRepository;
    private final BoxUtility boxUtility;
    private final ObjectMapper objectMapper;

    @Qualifier("box-connection-for-operation")
    @Autowired
    private BoxAPIConnection connection;

    public AssetService(ScalardlRepository scalardlRepository, @Qualifier("box-connection-for-operation") BoxAPIConnection connection, BoxUtility boxUtility, ObjectMapper objectMapper) {
        this.scalardlRepository = scalardlRepository;
        this.connection = connection;
        this.boxUtility = boxUtility;
        this.objectMapper = objectMapper;
    }


    public ApiResponse addAsset(CustomEventData customEventData) {
        // Initialize ObjectMapper to create JSON nodes
        ObjectMapper objectMapper = new ObjectMapper();

        // Create the 'properties' ObjectNode containing the detailed asset info
        ObjectNode propertiesNode = objectMapper.createObjectNode()
                .put("type", customEventData.getType())
                .put("id", customEventData.getId())
                .put("item_version_id", customEventData.getItemVersionId())
                .put("sha1", customEventData.getSha1())
                .put("name", customEventData.getName())
                .put("description", customEventData.getDescription())
                .put("size", customEventData.getSize())
                .put("created_by_user_id", customEventData.getCreatedByUser().getId())
                .put("created_by_user_name", customEventData.getCreatedByUser().getName())
                .put("created_by_user_email", customEventData.getCreatedByUser().getLogin())
                .put("created_at_date", customEventData.getItemCreatedAtDate())
                .put("modified_by_user_id", customEventData.getModifiedByUser().getId())
                .put("modified_by_user_name", customEventData.getModifiedByUser().getName())
                .put("modified_by_user_email", customEventData.getModifiedByUser().getLogin())
                .put("modified_at_date", customEventData.getItemModifiedAtDate());

        // Construct the 'object_id' and 'hash_value' for the top-level JSON node
        String objectId = BOX_AND_ENTERPRISE_ID + customEventData.getId();
        String hashValue = customEventData.getSha1();  // Assuming SHA1 is the hash value

        // Log the assetId for tracking
        log.info("ObjectId: " + objectId);

        // Create the top-level JSON node that includes 'object_id', 'hash_value', and 'properties'
        ObjectNode jsonNode = objectMapper.createObjectNode()
                .put(OBJECT_ID, objectId)
                .put("hash_value", hashValue)
                .set("metadata", propertiesNode);

        log.info("Add asset jsonNode: " + jsonNode);
        // Execute the contract call with the updated JSON node
        ThrowableFunction f = a -> scalardlRepository.callContract(PUT_CONTRACT, jsonNode);

        // Make the contract call and capture the response
        ApiResponse serve = serve(f, jsonNode);

        // Log the contract execution status
        log.info("Contract Execution Status: " + serve);
        // Return the API response
        return serve;
    }

    public ApiResponse serve(ThrowableFunction f, JsonNode json) {
        try {

            ContractExecutionResult result = f.apply(json);
            String response = result.getContractResult().isPresent() ? result.getContractResult().get() : null;

            log.info("Response :" + result);
            return new ApiResponse(true, "", HttpStatus.OK, response);
        } catch (ClientException e) {
            e.printStackTrace();
            return new ApiResponse(false, Translator.toLocale("com.something.wrongDL") + e.getMessage(), HttpStatus.valueOf(e.getStatusCode().get()), null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(false, Translator.toLocale("com.something.wrongDL") + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    public String getTamperingStatus(String fileId) {
        log.info("Calling Scalar DL getTamperingStatus");

        String tamperingStatus = TamperingStatusType.TAMPERED.toString();

        boolean needsRefresh = connection.needsRefresh();
        log.info("Does Box Connection needs Refresh Call :" + needsRefresh);

        if (needsRefresh) {
            connection = boxUtility.getBoxEnterpriseConnection();
            log.info("After Box Connection Refresh Status :" + connection.needsRefresh());
        }

        BoxFile file = new BoxFile(connection, fileId);
        BoxFile.Info info = file.getInfo("type", "id", "sha1", "name", "description", "size", "created_at", "modified_at", "content_created_at", "content_modified_at", "created_by", "modified_by", "owned_by", "file_version");

        SimpleDateFormat utcDateFormatter = GenericUtility.getUTCDateFormatWithoutMilliseconds();
        ObjectNode metadata = objectMapper.createObjectNode()
                .put("type", info.getType())
                .put("id", info.getID())
                .put("item_version_id", Long.parseLong(info.getVersion().getID()))
                .put("sha1", info.getSha1())
                .put("name", info.getName())
                .put("description", info.getDescription())
                .put("size", info.getSize())
                .put("created_by_user_id", info.getCreatedBy().getID())
                .put("created_by_user_name", info.getCreatedBy().getName())
                .put("created_by_user_email", info.getCreatedBy().getLogin())
                .put("created_at_date", utcDateFormatter.format(info.getCreatedAt()))
                .put("modified_by_user_id", info.getModifiedBy().getID())
                .put("modified_by_user_name", info.getModifiedBy().getName())
                .put("modified_by_user_email", info.getModifiedBy().getLogin())
                .put("modified_at_date", utcDateFormatter.format(info.getModifiedAt()));

        ArrayNode array = objectMapper.createArrayNode()
                .add(objectMapper.createObjectNode()
                        .put("version_id", "latest")
                        .put("hash_value", info.getSha1())
                        .set("metadata", metadata));

        JsonNode versionsNode = objectMapper.valueToTree(array);

        ObjectNode jsonNode = objectMapper.createObjectNode()
                .put(OBJECT_ID, BOX_AND_ENTERPRISE_ID + info.getID())
                .set("versions", versionsNode);

        log.info("jsonNode: {}", jsonNode);
        ThrowableFunction f = a -> scalardlRepository.callContract(VALIDATE_CONTRACT, jsonNode);
        ApiResponse response = serve(f, jsonNode);
        log.info("Response:{}", response);

        if (response.getStatus().equals(true)) {
            try {
                String dataJson = (String) response.getData();
                ObjectNode dataNode = (ObjectNode) objectMapper.readTree(dataJson);
                log.info("Status:{}", dataNode.get("status"));
                String statusValue = dataNode.has("status") ? dataNode.get("status").asText() : null;
                if ("faulty".equals(statusValue)) {
                    return tamperingStatus;
                } else {
                    tamperingStatus = TamperingStatusType.NOT_TAMPERED.toString();
                }

            } catch (JsonProcessingException e) {
                log.info("Something went wrong with parsing result received from ScalarDl ");
                e.printStackTrace();
                throw new GenericException("Something went wrong with parsing result ");
            }
        } else {
            log.info("results don't match");
            tamperingStatus = TamperingStatusType.TAMPERED.toString();
        }
        return tamperingStatus;
    }


    @FunctionalInterface
    public interface ThrowableFunction {
        ContractExecutionResult apply(JsonNode json) throws Exception;
    }
}
