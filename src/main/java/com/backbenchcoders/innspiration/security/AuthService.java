package com.backbenchcoders.innspiration.security;

import com.backbenchcoders.innspiration.dto.LoginDto;
import com.backbenchcoders.innspiration.dto.SignUpRequestDto;
import com.backbenchcoders.innspiration.dto.UserDto;
import com.backbenchcoders.innspiration.entity.User;
import com.backbenchcoders.innspiration.entity.enums.Role;
import com.backbenchcoders.innspiration.exception.ResourceNotFoundException;
import com.backbenchcoders.innspiration.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public UserDto signUp(SignUpRequestDto signUpRequestDto){
        User user = userRepository.findByEmail(signUpRequestDto.getEmail()).orElse(null);

        if(user != null){
            throw new RuntimeException("User already exists with this email");
        }

        User newUser = modelMapper.map(signUpRequestDto, User.class);
        newUser.setRoles(Set.of(Role.GUEST));
        newUser.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword()));
        newUser = userRepository.save(newUser);

        return modelMapper.map(newUser, UserDto.class);
    }

    public String[] login(LoginDto loginDto){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(), loginDto.getPassword()
        ));

        User user = (User) authentication.getPrincipal();

        String[] token = new String[2];
        token[0] = jwtService.generateAccessToken(user);
        token[1] = jwtService.getRefreshToken(user);

        return token;
    }

    public String refreshToken(String refreshToken){
        Long id = jwtService.getUserIdFromToken(refreshToken);

        User user = userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("User Not Found with id: "+id));
        return jwtService.generateAccessToken(user);
    }
}
