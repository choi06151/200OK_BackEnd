package com._OK._OK.Story;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StroyDto {
    private String content;
    private String choice1;
    private String choice2;
    private String choice3;
    private String describe; //이미지를 묘사하는 텍스트

}
