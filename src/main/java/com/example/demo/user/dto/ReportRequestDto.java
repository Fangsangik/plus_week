package com.example.demo.user.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ReportRequestDto {
    private List<Long> userIds;

    public ReportRequestDto() {}
}
