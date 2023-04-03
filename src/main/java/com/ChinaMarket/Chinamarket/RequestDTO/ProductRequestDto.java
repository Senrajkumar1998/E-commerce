package com.ChinaMarket.Chinamarket.RequestDTO;

import com.ChinaMarket.Chinamarket.Enum.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDto {

    private int sellerId;
    private String name;
    private int price;
    private int quantity;

    private Category category;
}
