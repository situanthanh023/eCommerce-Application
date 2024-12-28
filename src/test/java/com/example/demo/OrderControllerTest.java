package com.example.demo;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Before
    public void setup()
    {
        User user = createUser();
        when(userRepository.findByUsername("sam")).thenReturn(user);
        when(orderRepository.findByUser(any())).thenReturn(createOrders());
    }


    @Test
    public void submitOrder_Test() {
        ResponseEntity<UserOrder> response = orderController.submit("sam");

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());
        UserOrder userOrder = response.getBody();

        Assert.assertNotNull(userOrder);
    }

    @Test
    public void testForInvalidOrder() {
        ResponseEntity<UserOrder> response = orderController.submit("user123");

        Assert.assertNotNull(response);
        Assert.assertEquals(404, response.getStatusCodeValue());
        Assert.assertNull( response.getBody());

        verify(userRepository, times(1)).findByUsername("user123");
    }

//     Assert.assertNotNull(response);
//        Assert.assertEquals(200, response.getStatusCodeValue());
    @Test
    public void GetOrderTest_ShouldSuccessfully() {
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("sam");

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());
        List<UserOrder> orders = response.getBody();
        Assert.assertEquals(createOrders().size(), orders.size());

    }

    @Test
    public void InvalidValidUserTest() {
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("user123");

        Assert.assertNotNull(response);
        Assert.assertEquals(404, response.getStatusCodeValue());
        Assert.assertNull( response.getBody());

        verify(userRepository, times(1)).findByUsername("user123");
    }

    public static User createUser()
    {
        User user = new User();
        user.setId(1L);
        user.setUsername("sam");
        user.setPassword("password123");
        Cart cart = createCart(user);
        user.setCart(cart);
        return user;
    }

    public static List<UserOrder> createOrders()
    {
        List<UserOrder> orders = new ArrayList<>();
        IntStream.range(0,2).forEach(i -> {
            UserOrder userOrder = new UserOrder();
            Cart cart = createCart(createUser());
            userOrder.setItems(cart.getItems());
            userOrder.setTotal(cart.getTotal());
            User user = createUser();
            userOrder.setUser(user);
            userOrder.setId(Long.valueOf(i));
            orders.add(userOrder);
        });
        return orders;
    }

    public static Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setId(1L);
        List<Item> items = createItems();
        cart.setItems(createItems());
        boolean isSeen = false;
        BigDecimal acc = null;
        for (Item item : items) {
            BigDecimal value = item.getPrice();
            if (isSeen == false) {
                isSeen = true;
                acc = value;
            } else {
                acc = acc.add(value);
            }
        }
        if (isSeen) cart.setTotal(Optional.of(acc).get());
        else cart.setTotal(Optional.<BigDecimal>empty().get());
        cart.setUser(user);
        return cart;
    }

    public static List<Item> createItems() {
        List<Item> items = new ArrayList<>();
        for (int i = 1; i <= 3; i++)
        {
            items.add(createItem(i));
        }
        return items;
    }

    public static Item createItem(long id)
    {
        Item item = new Item();
        item.setId(id);
        item.setPrice(BigDecimal.valueOf(id * 1.2));
        item.setName("Item " + item.getId());
        item.setDescription("Item test");
        return item;
    }


}