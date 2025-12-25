package com.scalar.events_log_tool.application.dto;

import com.scalar.events_log_tool.application.responsedto.FolderDetailsDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDetails {

    private Integer size;
    private List<FolderDetailsDto> folderDetailsDtoList;
}
