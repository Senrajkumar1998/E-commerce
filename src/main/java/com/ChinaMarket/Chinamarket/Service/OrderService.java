package com.ChinaMarket.Chinamarket.Service;

import com.ChinaMarket.Chinamarket.Enum.ProductStatus;
import com.ChinaMarket.Chinamarket.Exception.CustomerNotFoundException;
import com.ChinaMarket.Chinamarket.Exception.InsufficientQuantityException;
import com.ChinaMarket.Chinamarket.Exception.ProductNotFoundException;
import com.ChinaMarket.Chinamarket.Model.*;
import com.ChinaMarket.Chinamarket.Repository.CustomerRepository;
import com.ChinaMarket.Chinamarket.Repository.ProductRepository;
import com.ChinaMarket.Chinamarket.RequestDTO.OrderRequestDto;
import com.ChinaMarket.Chinamarket.ResponseDTO.ItemResponseDto;
import com.ChinaMarket.Chinamarket.ResponseDTO.OrderResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ItemService itemService;
    @Autowired
    private JavaMailSender emailSender;
    public OrderResponseDto placeOrder(OrderRequestDto orderRequestDto) throws ProductNotFoundException, CustomerNotFoundException, InsufficientQuantityException {
        Customer customer;
        try {
            customer=customerRepository.findById(orderRequestDto.getCustomerId()).get();
        }
        catch (Exception e){
            throw new CustomerNotFoundException("Invalid CustomerId!!");
        }

        Product product;
        try {
            product=productRepository.findById(orderRequestDto.getCustomerId()).get();
        }
        catch (Exception e){
            throw new ProductNotFoundException("Invalid ProductId!!");
        }
        if(product.getQuantity()<orderRequestDto.getRequiredQuantity()){
            throw new InsufficientQuantityException("Sorry! Required quantity not found");
        }

        Ordered ordered=new Ordered();
        ordered.setTotalCost(orderRequestDto.getRequiredQuantity()*product.getPrice());
        ordered.setDeliveryCharge(40);

        Card card=customer.getCards().get(0);
        String cardNo="";
        for(int i=0;i<card.getCardNo().length()-4;i++)
            cardNo+='X';
        cardNo+=card.getCardNo().substring(card.getCardNo().length()-4);
        ordered.setCardUsedForPayment(cardNo);

        Item item=new Item();
        item.setRequiredQuantity(orderRequestDto.getRequiredQuantity());
        item.setProduct(product);
        item.setOrdered(ordered);
        ordered.getOrderedItems().add(item);
        ordered.setCustomer(customer);



        int leftQuantity=product.getQuantity()-orderRequestDto.getRequiredQuantity();
        if(leftQuantity<=0)
            product.setProductStatus(ProductStatus.OUT_OF_STOCK);
        product.setQuantity(leftQuantity);

        customer.getOrders().add(ordered);
        Customer savedCustomer=customerRepository.save(customer);
        Ordered savedOrder=savedCustomer.getOrders().get(savedCustomer.getOrders().size()-1);


        //view item first
       /* try {
            itemService.viewItem(orderRequestDto.getProductId());
        }
        catch (Exception e){
            throw new ProductNotFoundException("Item is not while viewing")
        }

        Item item= product.getItem();*/


        //prepare order object
       /*int totalCost=orderRequestDto.getRequiredQuantity()* product.getPrice();
        int deliveryCharge=0;
        if(totalCost<500){
            deliveryCharge=50;
            totalCost+=deliveryCharge;
        }
        Ordered ordered= Ordered.builder()
                .totalCost(totalCost)
                .deliveryCharge(deliveryCharge)
                .build();

        //prepare the card String;
        Card card= customer.getCards().get(0);
        String cardUsed="";
        int cardNo=card.getCardNo().length();
        for(int i=0;i<cardNo-4;i++){
            cardUsed+="X";
        }
        cardUsed+=card.getCardNo().substring(cardNo-4);
        ordered.setCardUsedForPayment(cardUsed);

        //update customer orderlist
        customer.getOrders().add(ordered);


        //update the quantity left in warehouse
        int leftQuantity=product.getQuantity()-orderRequestDto.getRequiredQuantity();
        if(leftQuantity<0)
            product.setProductStatus(ProductStatus.OUT_OF_STOCK);
        product.setQuantity(leftQuantity);

        //update item
        //create new item
        Item item= Item.builder()
                .requiredQuantity(orderRequestDto.getRequiredQuantity())
                .build();

        //update item in order
        ordered.getItems().add(item);
        item.setOrdered(ordered);
        product.setItem(item);
        item.setProduct(product);

        //save product-item and customer-order
        customerRepository.save(customer);*/

        //prepare responseDto
        OrderResponseDto orderResponseDto= OrderResponseDto.builder()
                .productName(product.getName())
                .orderDate(ordered.getOrderDate())
                .quantityOrdered(orderRequestDto.getRequiredQuantity())
                .cardUsedForPayment(cardNo)
                .itemPrice(product.getPrice())
                .totalCost(ordered.getTotalCost())
                .deliveryCharge(40)
                .build();
        //send an email
        String text = "Congrats!!.your order with total value" + ordered.getTotalCost()+"has been placed" ;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("rajugehlot1603@gmail.com");
        message.setTo(customer.getEmail());
        message.setSubject("Order placed notification");
        message.setText(text);
        emailSender.send(message);

        return orderResponseDto;

    }
}
