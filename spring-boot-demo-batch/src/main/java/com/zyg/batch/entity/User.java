package com.zyg.batch.entity;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Data
public class User {
    private Integer id;
    private String name;
    private Integer age;
    private String email;

}
