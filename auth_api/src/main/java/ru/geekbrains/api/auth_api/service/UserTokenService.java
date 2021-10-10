package ru.geekbrains.api.auth_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.api.auth_api.exception.AuthApiException;
import ru.geekbrains.api.auth_api.exception.ErrorCode;
import ru.geekbrains.api.auth_api.model.request.UserParams;
import ru.geekbrains.api.auth_api.utils.JwtTokenUtil;
import ru.geekbrains.api.auth_api.model.Token;
import ru.geekbrains.api.auth_api.model.User;
import ru.geekbrains.api.auth_api.model.dto.TokenDto;
import ru.geekbrains.api.auth_api.model.request.UserRegParams;
import ru.geekbrains.api.auth_api.model.response.ReportResponse;
import ru.geekbrains.api.auth_api.model.response.Response;
import ru.geekbrains.api.auth_api.service.interfaces.UserServiceFacade;

import java.util.Arrays;
import java.util.Optional;

@Service
public class UserTokenService implements UserServiceFacade {
    private final UserServiceImpl userService;
    private final TokenServiceImpl tokenService;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public UserTokenService(UserServiceImpl userService, TokenServiceImpl tokenService,
                            JwtTokenUtil jwtTokenUtil) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public Response generateKeyResponse(UserRegParams regParams) {
        Optional<User> optionalUser = userService.findByLoginOrEmail(regParams.getLogin(), regParams.getEmail());
        if (optionalUser.isPresent()) {
            throw new AuthApiException("User with login " + regParams.getLogin()
                    + " or email " + regParams.getEmail() + " is already registered",
                    ErrorCode.USER_ALREADY_REGISTERED_ERROR);
        }

        User user = userService.saveUser(regParams.getLogin(), regParams.getEmail(), regParams.getPassword());

        TokenDto tokenDto = getTokenDto(user);

        return new ReportResponse(tokenDto);
    }

    @Override
    public Response generateNewKeyResponse(UserParams userParams) {
        Optional<User> optionalUser = userService.findByLogin(userParams.getLogin());
        User user = optionalUser
                .orElseThrow(() -> new AuthApiException(ErrorCode.USER_NOT_FOUND, userParams.getLogin()));

        boolean isPasswordCorrect = Arrays.equals(userParams.getPassword(), user.getPassword());
        if (!isPasswordCorrect) {
            throw new AuthApiException(ErrorCode.PASSWORD_NOT_CORRECT, userParams.getLogin());
        }

        TokenDto tokenDto = getTokenDto(user);

        return new ReportResponse(tokenDto);
    }

    private TokenDto getTokenDto(User user) {
        String key = jwtTokenUtil.generateToken(user.getLogin());
        Token token = tokenService.saveToken(user, key);

        return new TokenDto(token);
    }
}
