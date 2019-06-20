package com.viniland.sales.component.security;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * User entity
 */
@Data
@AllArgsConstructor
public class User {

    private String login;

    private String password;

    private List<String> roles;

}
