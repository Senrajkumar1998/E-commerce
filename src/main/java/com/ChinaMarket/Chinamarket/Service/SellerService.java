package com.ChinaMarket.Chinamarket.Service;

import com.ChinaMarket.Chinamarket.Convertor.SellerConvertor;
import com.ChinaMarket.Chinamarket.Model.Seller;
import com.ChinaMarket.Chinamarket.Repository.SellerRepository;
import com.ChinaMarket.Chinamarket.RequestDTO.SellerRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SellerService {

    @Autowired
    SellerRepository sellerRepository;

    public String addSeller(SellerRequestDto sellerRequestDto){

        Seller seller= SellerConvertor.SellerRequestDtoToSeller(sellerRequestDto);


        sellerRepository.save(seller);
        return "Congrats! now you can sell in China Market";

    }


}
