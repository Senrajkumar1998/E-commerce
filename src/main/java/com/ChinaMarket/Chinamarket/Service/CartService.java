package com.ChinaMarket.Chinamarket.Service;

import com.ChinaMarket.Chinamarket.Enum.ProductStatus;
import com.ChinaMarket.Chinamarket.Exception.CustomerNotFoundException;
import com.ChinaMarket.Chinamarket.Exception.InsufficientQuantityException;
import com.ChinaMarket.Chinamarket.Exception.ProductNotFoundException;
import com.ChinaMarket.Chinamarket.Model.*;
import com.ChinaMarket.Chinamarket.Repository.CustomerRepository;
import com.ChinaMarket.Chinamarket.Repository.ProductRepository;
import com.ChinaMarket.Chinamarket.RequestDTO.OrderRequestDto;
import com.ChinaMarket.Chinamarket.ResponseDTO.OrderResponseDto;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    OrderService orderService;
    @Autowired
    JavaMailSender emailSender;

    public String addToCart(OrderRequestDto orderRequestDto) throws CustomerNotFoundException, ProductNotFoundException, InsufficientQuantityException {
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

        Cart cart=customer.getCart();

        int newCost= cart.getCartTotal()+ orderRequestDto.getRequiredQuantity()* product.getPrice();
        cart.setCartTotal(newCost);

        //Add item to current cart
        Item item=new Item();
        item.setRequiredQuantity(orderRequestDto.getRequiredQuantity());
        item.setCart(cart);
        item.setProduct(product);
        cart.getItems().add(item);

        customerRepository.save(customer);
        return "Item has been added to your cart!!";

    }

    public List<OrderResponseDto> checkout(int customerId) throws CustomerNotFoundException {

        Customer customer;
        try {
            customer=customerRepository.findById(customerId).get();
        }
        catch (Exception e){
            throw new CustomerNotFoundException("Invalid CustomerId!!");
        }

        List<OrderResponseDto> orderResponseDtos=new ArrayList<>();
        int totalCost= customer.getCart().getCartTotal();
        Cart cart=customer.getCart();
        for(Item item:cart.getItems()) {
            Ordered ordered = new Ordered();
            ordered.setTotalCost(item.getRequiredQuantity() * item.getProduct().getPrice());
            ordered.setDeliveryCharge(0);
            ordered.setCustomer(customer);
            ordered.getOrderedItems().add(item);

            Card card = customer.getCards().get(0);
            String cardNo = "";
            for (int i = 0; i < card.getCardNo().length() - 4; i++)
                cardNo += 'X';
            cardNo += card.getCardNo().substring(card.getCardNo().length() - 4);
            ordered.setCardUsedForPayment(cardNo);

            //update the quantity left in warehouse
            int leftQuantity=item.getProduct().getQuantity()-item.getRequiredQuantity();
            if(leftQuantity<0)
                item.getProduct().setProductStatus(ProductStatus.OUT_OF_STOCK);
            item.getProduct().setQuantity(leftQuantity);

            customer.getOrders().add(ordered);

            //prepare responseDto
            OrderResponseDto orderResponseDto= OrderResponseDto.builder()
                    .productName(item.getProduct().getName())
                    .orderDate(ordered.getOrderDate())
                    .quantityOrdered(item.getRequiredQuantity())
                    .cardUsedForPayment(cardNo)
                    .itemPrice(item.getProduct().getPrice())
                    .totalCost(ordered.getTotalCost())
                    .deliveryCharge(40)
                    .build();

            orderResponseDtos.add(orderResponseDto);
        }

        cart.setItems(new ArrayList<>());
        cart.setCartTotal(0);

        customerRepository.save(customer);

        //send an email
        String text = "Congrats!!.your order with total value" + totalCost+"has been placed" ;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("rajugehlot1603@gmail.com");
        message.setTo(customer.getEmail());
        message.setSubject("Order placed notification");
        message.setText(text);
        emailSender.send(message);

        return orderResponseDtos;
    }
}
