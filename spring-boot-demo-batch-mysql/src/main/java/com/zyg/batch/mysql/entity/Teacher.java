package com.zyg.batch.mysql.entity;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Data
public class Teacher {
    private Integer id;
    private String name;
    private Integer age;
    private String email;

}
