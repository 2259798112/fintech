package top.duwd.fintech.common.domain;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "t_big_order")
public class BigOrderEntity {
    private String side;
    private Double price;
    private Double size;
    private Date ts;
    private String plat;
    @Id
    private String id;
    private Double amount;
}
