package com.todoary.ms.src.todo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.todoary.ms.src.category.model.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"todoId", "isPinned", "isChecked", "title", "isAlarmEnabled", "targetTime", "createdTime", "categories" })
@JsonIgnoreProperties({"pinned", "checked", "alarmEnabled"}) // 중복 방지
public class GetTodoByDateRes {
    private Long todoId;
    @JsonProperty("isPinned")
    private boolean isPinned;
    @JsonProperty("isChecked")
    private boolean isChecked;
    private String title;
    @JsonProperty("isAlarmEnabled")
    private boolean isAlarmEnabled;
    private String targetTime;
    private String createdTime;
    private List<Category> categories;
}
