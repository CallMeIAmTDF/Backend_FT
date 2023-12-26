package com.example.familytree.enums;

import com.example.familytree.shareds.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HistoryTypeEnum {
    CREATED(Constants.HISTORY_ENUM.CREATED),
    UPDATED(Constants.HISTORY_ENUM.UPDATED),
    DELETED(Constants.HISTORY_ENUM.DELETED);
    private final String description;
}
