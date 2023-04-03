package com.ChinaMarket.Chinamarket.Convertor;

import com.ChinaMarket.Chinamarket.Model.Seller;
import com.ChinaMarket.Chinamarket.RequestDTO.SellerRequestDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SellerConvertor {

    public static Seller SellerRequestDtoToSeller(SellerRequestDto sellerRequestDto){

        return Seller.builder()
                .name(sellerRequestDto.getName())
                .email(sellerRequestDto.getEmail())
                .mobNo(sellerRequestDto.getMobNo())
                .panNo(sellerRequestDto.getPanNo())
                .build();
    }
}
