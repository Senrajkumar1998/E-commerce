package com.ChinaMarket.Chinamarket.Convertor;

import com.ChinaMarket.Chinamarket.Model.Customer;
import com.ChinaMarket.Chinamarket.RequestDTO.CustomerRequestDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CustomerConvertor {

    public static Customer customerRequestDtoToCustomer(CustomerRequestDto customerRequestDto){

        return Customer.builder()
                .name(customerRequestDto.getName())
                .age(customerRequestDto.getAge())
                .email(customerRequestDto.getEmail())
                .mobNo(customerRequestDto.getMobNo())
                .build();
    }
}
