package org.example.sample.model;

import lombok.Data;

@Data
public class TestDto {

    private String firstName;
    private String lastName;

    private EntityDto entityDto;

    @Data
    public static class EntityDto {
        private String country;
        private Integer postalCode;

    }
}
