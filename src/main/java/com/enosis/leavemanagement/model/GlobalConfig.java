package com.enosis.leavemanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "global_config")
public class GlobalConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "configName is mandatory")
    @Column(name = "config_name", length = 50, nullable = false, unique = true)
    private String configName;

    @NotNull(message = "configValue is mandatory")
    @Column(name = "config_value", nullable = false)
    private Integer configValue;
}
