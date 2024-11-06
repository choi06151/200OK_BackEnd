package com._OK._OK.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("/amazon/user")
@Tag(name="User", description = "User Api")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    @Operation(summary = "유저 생성", description = "유저를 생성합니다.<br> id는 자동생성됩니다. id필드는 Body에 포함하지 않아도 됩니다.")
    public ResponseEntity<UserDto> createUser(
            @RequestBody UserDto userDto){
        UserDto createUser = userService.createUser(userDto);
        return new ResponseEntity<>(createUser, HttpStatus.CREATED);
    }
}
