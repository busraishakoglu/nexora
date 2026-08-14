package com.nexora.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerFilterRequest {
        private String search;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
}

