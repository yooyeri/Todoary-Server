package com.todoary.ms.src.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetUserRes {
    private String profileImgUrl;
    private String nickname;
    private String introduce;
    private String email;
}
