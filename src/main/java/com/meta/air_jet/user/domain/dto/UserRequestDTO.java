package com.meta.air_jet.user.domain.dto;

public class UserRequestDTO {
    public record loginDTO(
            String loginId,
            String password
    ){}
}