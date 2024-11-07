package com._OK._OK.User;public class ImageMapper {
    public static ImageDto mapToDto(Image image){
        return new ImageDto(
                image.getId(),
                image.getUserId(),
                image.getImage()
        );
    }

    public static Image mapToEntity(ImageDto imageDto){
        return new Image(
                imageDto.getId(),
                imageDto.getUserId(),
                imageDto.getImage()
        );
    }
}
