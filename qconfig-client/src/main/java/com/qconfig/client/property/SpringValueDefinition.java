package com.qconfig.client.property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/12/09:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpringValueDefinition {

    private String key;

    private String placeholder;

    private String property;

}
