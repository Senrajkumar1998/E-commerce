package com.ChinaMarket.Chinamarket.ResponseDTO;

import com.ChinaMarket.Chinamarket.Enum.Category;
import com.ChinaMarket.Chinamarket.Enum.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemResponseDto {

    private String name;
    private int price;
    private Category category;

    private ProductStatus productStatus;
}
