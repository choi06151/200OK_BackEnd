package com._OK._OK.Story;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonologueDto {
    private ArrayList<String> monologue; //멀티스레드가 없으니 ArrayList 씀
    private String tag; //대사에 맞는 브금 태그
}
