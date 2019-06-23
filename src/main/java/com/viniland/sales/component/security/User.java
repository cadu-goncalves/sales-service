package com.viniland.sales.component.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * User entity
 */
@Getter
@AllArgsConstructor
public class User {

    private String login;

    private String password;

    private List<String> roles;

}
