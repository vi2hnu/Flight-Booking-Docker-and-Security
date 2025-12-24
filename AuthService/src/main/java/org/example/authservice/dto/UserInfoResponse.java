package org.example.authservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserInfoResponse {
	private Long id;
	private String username;
	private String email;
	private List<String> roles;
    private boolean changePassword;

	public UserInfoResponse(Long id, String username, String email, List<String> roles, boolean changePassword) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.roles = roles;
        this.changePassword = changePassword;
	}
}
