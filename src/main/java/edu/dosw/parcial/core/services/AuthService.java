package edu.dosw.parcial.core.services;

import edu.dosw.parcial.controller.dtos.request.LoginRequest;
import edu.dosw.parcial.controller.dtos.response.LoginResponse;

public interface AuthService {

    public LoginResponse login(LoginRequest request);
    
}
