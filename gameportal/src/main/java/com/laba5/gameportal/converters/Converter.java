package com.laba5.gameportal.converters;

import com.laba5.gameportal.dto.GameDTO;
import com.laba5.gameportal.dto.UserDTO;
import com.laba5.gameportal.entity.Game;
import com.laba5.gameportal.entity.User;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Converter {

    public UserDTO convertUserToDTO(User user) {
        List<GameDTO> gameDTOList =  user.getFavorites().stream()
                .map(this::convertGameToDTO)
                .collect(Collectors.toList());

        return new UserDTO(user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAvatar(),
                user.getRole(),
                gameDTOList);
    }

    public GameDTO convertGameToDTO(Game game) {
        return new GameDTO(game.getId(),
                game.getName(),
                game.getImage(),
                game.getDescription());
    }
}

