package com.todoary.ms.src.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostUserReq {
    private String name;
    private String nickname;
    private String email;
    private String password;
    @JsonProperty("isTermsEnable")
    private boolean isTermsEnable;
}
