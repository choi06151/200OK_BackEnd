package com._OK._OK.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ImageDto {
    private Long id;
    private Long userId;
    private byte[] image;
}
