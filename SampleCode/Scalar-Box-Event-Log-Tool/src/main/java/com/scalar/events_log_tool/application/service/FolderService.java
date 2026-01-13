package com.scalar.events_log_tool.application.service;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxFile;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxItem;
import com.scalar.db.api.DistributedTransaction;
import com.scalar.events_log_tool.application.constant.UserRoles;
import com.scalar.events_log_tool.application.dto.ItemDetails;
import com.scalar.events_log_tool.application.dto.SubmitToken;
import com.scalar.events_log_tool.application.exception.GenericException;
import com.scalar.events_log_tool.application.responsedto.ApiResponse;
import com.scalar.events_log_tool.application.responsedto.FolderDetailsDto;
import com.scalar.events_log_tool.application.utility.BoxUtility;
import com.scalar.events_log_tool.application.utility.GenericUtility;
import com.scalar.events_log_tool.application.utility.Translator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FolderService {

    private final UserService userService;
    private final BoxUtility boxUtility;

    @Qualifier("box-connection-for-operation")
    @Autowired
    private BoxAPIConnection apiConnection;

    public FolderService(UserService userService, BoxUtility boxUtility) {
        this.boxUtility = boxUtility;
        this.userService = userService;
    }

    public ApiResponse getItemList(String folderId, String currentEmail, DistributedTransaction transaction) {

        try {
            //fetch role from token for authority
            List<String> collect = SecurityContextHolder.getContext().getAuthentication()
                    .getAuthorities().stream().map(e -> e.getAuthority()).collect(Collectors.toList());


            boolean needsRefresh = apiConnection.needsRefresh();
            log.info("Does Box Connection needs Refresh Call :" + needsRefresh);

            if (needsRefresh) {
                apiConnection = boxUtility.getBoxEnterpriseConnection();
                log.info("After Box Connection Refresh Status :" + apiConnection.needsRefresh());
            }

            BoxAPIConnection connection;


            if (collect.contains(UserRoles.AUDIT_ADMIN.toString()) || collect.contains(UserRoles.GENERAL_USER.toString())) {
                SubmitToken submitToken = userService.updateLatestToken(currentEmail, transaction);
                String accessToken = submitToken.getAccessToken();
                connection = new BoxAPIConnection(accessToken);
            } else {
                connection = apiConnection;
            }

            BoxFolder folder = new BoxFolder(connection, folderId);
            List<FolderDetailsDto> itemDetailList = new ArrayList<>();
            for (BoxItem.Info itemInfo : folder) {

                if (itemInfo instanceof BoxFile.Info) {
                    BoxFile.Info fileInfo = (BoxFile.Info) itemInfo;
                    // Convert size from bytes to megabytes
                    String sizeInMegabytes = GenericUtility.getStringSizeLengthFile(fileInfo.getSize());
                    itemDetailList.add(FolderDetailsDto.builder()
                            .id(Long.parseLong(fileInfo.getID()))
                            .description(fileInfo.getDescription())
                            .name(fileInfo.getName())
                            .type(fileInfo.getType())
                            .size(sizeInMegabytes)
                            .build());


                } else if (itemInfo instanceof BoxFolder.Info) {
                    BoxFolder.Info folderInfo = (BoxFolder.Info) itemInfo;
                    // Convert size from bytes to megabytes
                    String sizeInMegabytes = GenericUtility.getStringSizeLengthFile(folderInfo.getSize());
                    itemDetailList.add(FolderDetailsDto.builder()
                            .id(Long.parseLong(folderInfo.getID()))
                            .description(folderInfo.getDescription())
                            .name(folderInfo.getName())
                            .size(sizeInMegabytes)
                            .type(folderInfo.getType())
                            .build());
                }
            }
            return ApiResponse.builder()
                    .status(true)
                    .httpStatus(HttpStatus.OK)
                    .data(ItemDetails.builder()
                            .folderDetailsDtoList(itemDetailList)
                            .size(itemDetailList.size())
                            .build())
                    .message("")
                    .build();
        } catch (Exception exception) {
            throw new GenericException(Translator.toLocale("com.something.wrong"));
        }
    }
}
