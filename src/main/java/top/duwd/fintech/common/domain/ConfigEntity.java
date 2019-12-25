package top.duwd.fintech.common.domain;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "t_config")
public class ConfigEntity {
    @Id
    private String k;
    private String v;
}
