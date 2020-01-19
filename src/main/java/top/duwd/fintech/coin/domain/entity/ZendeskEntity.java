package top.duwd.fintech.coin.domain.entity;


import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "zendesk")
public class ZendeskEntity {
    @Id
    private String id;

    private String plat;
    private String title;
    private String link;
    private Date createDate;
}
