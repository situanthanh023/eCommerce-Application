package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CartControllerTest {

    @InjectMocks
    private CartController cartController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ItemRepository itemRepository;

    @Before
    public void setup()
    {
        Mockito.when(userRepository.findByUsername("sam")).thenReturn(createUser());
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(createItem(1)));
    }

    @Test
    public void AddToCart_ShouldSuccessfully() {
        ModifyCartRequest addToCartRequest = new ModifyCartRequest();
        addToCartRequest.setUsername("sam");
        addToCartRequest.setItemId(1);
        addToCartRequest.setQuantity(5);

        ResponseEntity<Cart> response = cartController.addTocart(addToCartRequest);
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());


        Cart actualCart = response.getBody();
        User user = new User();
        user.setId(1L);
        user.setUsername("sam");
        user.setPassword("password123");
        user.setCart(createCart(user));

        Cart generatedCart = createCart(user);
        Assert.assertNotNull(actualCart);
        long itemId = addToCartRequest.getItemId();
        Item item = createItem(itemId);
        BigDecimal itemPrice = item.getPrice();
        BigDecimal expectedTotal = itemPrice.multiply(BigDecimal.valueOf(addToCartRequest.getQuantity())).add(generatedCart.getTotal());



        Assert.assertEquals("sam", actualCart.getUser().getUsername());
        Assert.assertEquals(generatedCart.getItems().size() + addToCartRequest.getQuantity(), actualCart.getItems().size());
        Assert.assertEquals(createItem(1), actualCart.getItems().get(0));
        Assert.assertEquals(expectedTotal, actualCart.getTotal());


        verify(cartRepository, times(1)).save(actualCart);

    }




    @Test
    public void invalidUsernameTestForAddTCart() {

        ModifyCartRequest addToCartRequest = new ModifyCartRequest();
        addToCartRequest.setUsername("user1");
        addToCartRequest.setQuantity(1);
        addToCartRequest.setItemId(1);

        ResponseEntity<Cart> addResponse = cartController.addTocart(addToCartRequest);

        Assert.assertEquals(404, addResponse.getStatusCodeValue());
        Assert.assertNull(addResponse.getBody());
        verify(userRepository, times(1)).findByUsername("user1");

    }


    @Test
    public void removeItemFromCartTest() {

        ModifyCartRequest addToCartRequest = new ModifyCartRequest();
        addToCartRequest.setQuantity(1);
        addToCartRequest.setItemId(1);
        addToCartRequest.setUsername("sam");



        ResponseEntity<Cart> response = cartController.removeFromcart(addToCartRequest);


//        Assert.assertNotNull(response);
        Assert.assertNotNull(String.valueOf(200), response.getStatusCodeValue());

        Cart actualCart = response.getBody();

        User user = new User();

        user.setUsername("sam");
        user.setPassword("password123");
        user.setId(1L);
        user.setCart(createCart(user));


        Cart generatedCart = createCart(user);
        Assert.assertNotNull(actualCart);
        long itemId = addToCartRequest.getItemId();
        Item item = createItem(itemId);




        BigDecimal itemPrice = item.getPrice();
        BigDecimal expectedTotal = generatedCart.getTotal().subtract(itemPrice.multiply(BigDecimal.valueOf(addToCartRequest.getQuantity())));
        Assert.assertEquals("sam", actualCart.getUser().getUsername());

        Assert.assertEquals(generatedCart.getItems().size() - addToCartRequest.getQuantity(), actualCart.getItems().size());
        Assert.assertEquals(createItem(2), actualCart.getItems().get(0));
        BigDecimal total = actualCart.getTotal();

        Assert.assertEquals(expectedTotal, total);

        verify(cartRepository, times(1)).save(actualCart);
    }

    @Test
    public void invalidUsernameTestForRemoveFromCart() {
        ModifyCartRequest addToCartRequest = new ModifyCartRequest();
        addToCartRequest.setUsername("user1");
        addToCartRequest.setQuantity(1);
        addToCartRequest.setItemId(1);



        ResponseEntity<Cart> removeResponse = cartController.removeFromcart(addToCartRequest);
        Assert.assertNotNull(removeResponse);
        int statusCodeValue = removeResponse.getStatusCodeValue();
        Assert.assertEquals(404, statusCodeValue);

        Assert.assertNull(removeResponse.getBody());
        verify(userRepository, times(1)).findByUsername("user1");

    }


    public static User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("sam");
        user.setPassword("password123");
        user.setCart(createCart(user));
        return user;
    }

    public static Item createItem(long id) {
        Item item = new Item();
        item.setId(id);
        item.setName("Laptop");
        BigDecimal bigDecimal = new BigDecimal(9990);
        item.setPrice(bigDecimal);
        item.setDescription("Mi Horizon");
        item.setDescription("Item " + id);
        return item;
    }

    public static Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setId(1L);
        List<Item> items = creatCartItems();
        cart.setItems(creatCartItems());

        boolean seen = false;
        BigDecimal acc = null;
        for (Item item : items) {
            BigDecimal value = item.getPrice();
            if (!seen) {
                seen = true;
                acc = value;
            } else {
                acc = acc.add(value);
            }
        }
        cart.setTotal((seen ? Optional.of(acc) : Optional.<BigDecimal>empty()).get());
        cart.setUser(user);
        return cart;
    }

    public static List<Item> creatCartItems() {
        List<Item> items = new ArrayList<>();
        for (int i = 1; i <= 3; i++)
            items.add(createItem(i));
        return items;
    }








}
