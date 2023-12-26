package com.example.familytree.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
@Getter
@Setter
public class SideDto {
    String side;
    Integer personId;

}
