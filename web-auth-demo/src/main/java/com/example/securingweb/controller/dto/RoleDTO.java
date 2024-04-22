package com.example.securingweb.controller.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RoleDTO {
    private Long id;
    private String name;
    private Collection<PrivilegeDTO> privileges = new ArrayList<>();

    public RoleDTO() {}

    public RoleDTO(Long id, String name, Collection<PrivilegeDTO> privileges) {
        this.id = id;
        this.name = name;
        this.privileges = privileges;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<PrivilegeDTO> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(Collection<PrivilegeDTO> privileges) {
        this.privileges = privileges;
    }
}
