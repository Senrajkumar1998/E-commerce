
package com.ChinaMarket.Chinamarket.Convertor;

import com.ChinaMarket.Chinamarket.Enum.ProductStatus;
import com.ChinaMarket.Chinamarket.Model.Product;
import com.ChinaMarket.Chinamarket.RequestDTO.ProductRequestDto;
import com.ChinaMarket.Chinamarket.ResponseDTO.ProductResponseDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductConvertor {

    public static Product productRequestRequestDtotoProduct(ProductRequestDto productRequestDto){
         return Product.builder()
                 .name(productRequestDto.getName())
                 .price(productRequestDto.getPrice())
                 .quantity(productRequestDto.getQuantity())
                 .category(productRequestDto.getCategory())
                 .productStatus(ProductStatus.AVAILABLE)
                 .build();
    }
    public static ProductResponseDto productToProductResponseDto(Product product){
        return ProductResponseDto.builder()
                .name(product.getName())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .productStatus(product.getProductStatus())
                .build();
    }


}