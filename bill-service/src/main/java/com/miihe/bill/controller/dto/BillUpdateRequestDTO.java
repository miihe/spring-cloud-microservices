package com.miihe.bill.controller.dto;

import lombok.Getter;

@Getter
public class BillUpdateRequestDTO {

    private Boolean isDefault;

    private Boolean overdraftEnabled;

}
