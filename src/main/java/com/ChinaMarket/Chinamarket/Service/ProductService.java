package com.ChinaMarket.Chinamarket.Service;

import com.ChinaMarket.Chinamarket.Convertor.ProductConvertor;
import com.ChinaMarket.Chinamarket.Enum.Category;
import com.ChinaMarket.Chinamarket.Exception.SellerNotFoundException;
import com.ChinaMarket.Chinamarket.Model.Product;
import com.ChinaMarket.Chinamarket.Model.Seller;
import com.ChinaMarket.Chinamarket.Repository.ProductRepository;
import com.ChinaMarket.Chinamarket.Repository.SellerRepository;
import com.ChinaMarket.Chinamarket.RequestDTO.ProductRequestDto;
import com.ChinaMarket.Chinamarket.ResponseDTO.ProductResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    SellerRepository sellerRepository;

    @Autowired
    ProductRepository productRepository;

    public ProductResponseDto addProduct(ProductRequestDto productRequestDto) throws SellerNotFoundException {
        Seller seller;

        try{
            seller =sellerRepository.findById(productRequestDto.getSellerId()).get();
        }
        catch (Exception e){
            throw new SellerNotFoundException("Invalid seller Id");
        }

        Product product= ProductConvertor.productRequestRequestDtotoProduct(productRequestDto);
        product.setSeller(seller);

        seller.getProducts().add(product);

        //save the seller and product -parent and child
        sellerRepository.save(seller);


        //prepare response
        ProductResponseDto productResponseDto=ProductConvertor.productToProductResponseDto(product);
        return productResponseDto;


    }
    public List<ProductResponseDto> getProductByCategory(Category category){

        List<Product> products = productRepository.findAllByCategory(category);


        //prepare a list of response dtos
        List<ProductResponseDto> productResponseDtos=new ArrayList<>();
        for(Product product:products){
            ProductResponseDto productResponseDto=ProductConvertor.productToProductResponseDto(product);
            productResponseDtos.add(productResponseDto);
        }
        return productResponseDtos;
    }
}
