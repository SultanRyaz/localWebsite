package com.laba5.gameportal.mapper;

import com.laba5.gameportal.dto.UserCreateDTO;
import com.laba5.gameportal.dto.UserResponseDTO;
import com.laba5.gameportal.entity.User;
import com.laba5.gameportal.exception.FileUploadException;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class UserMapper {

    private static final String DEFAULT_ROLE = "USER";

    private final static String IMAGE_PATH = "C:\\Users\\Admin\\Documents\\Университетская учеба" +
            "\\Основы веб программирования\\Лабораторная работа 5\\gameportal\\src\\main" +
            "\\resources\\static\\images";

    private final static String IMAGE_URL = "http://localhost:8080/images/";

    private final static String DEFAULT_IMAGE_URL = "http://localhost:8080/images/DEFAULT.png";

    public static User userCreateDtoToUser (UserCreateDTO userCreateDTO){
        User user = new User();
        user.setUsername(userCreateDTO.getUsername());
        user.setPassword(userCreateDTO.getPassword());
        user.setEmail(userCreateDTO.getEmail());
        user.setRole(DEFAULT_ROLE);
        user.setAvatar(processAvatar(userCreateDTO.getAvatar(), userCreateDTO.getUsername()));
        return user;
    }

    public static UserResponseDTO userToUserResponseDto (User user){
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAvatar(),
                user.getRole()
        );
    }

    public static String processAvatar(MultipartFile avatar, String username){
        if (avatar != null) {
            try {
                String Filename = username + "_" + avatar.getOriginalFilename();
                File file = new File(IMAGE_PATH, Filename);
                avatar.transferTo(file);
                return IMAGE_URL + Filename;
            } catch (IOException e) {
                throw new FileUploadException("Не удалось загрузить аватар");
            }
        }else {
            return DEFAULT_IMAGE_URL;
        }
    }

    public static void deleteAvatar(String avatar){
        if(avatar.equals(DEFAULT_IMAGE_URL)){
            return;
        }
        try{
            String filename = avatar.substring(avatar.lastIndexOf("/")+1);
            Files.delete(Paths.get(IMAGE_PATH, filename));
        }catch (IOException ex){
            throw new FileUploadException("Не удалось удалить аватар");
        }
    }

    public static List<UserResponseDTO> usersListToUsersResponseDtoList(List<User> users){
        List<UserResponseDTO> userResponseDTOS = new ArrayList<>();
        for(User user:users){
            userResponseDTOS.add(userToUserResponseDto(user));
        }
        return userResponseDTOS;
    }

    public static String getDefaultImageUrl(){
        return DEFAULT_IMAGE_URL;
    }
}
