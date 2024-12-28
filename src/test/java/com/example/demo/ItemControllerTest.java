package com.example.demo;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ItemControllerTest {

    @InjectMocks
    private ItemController itemController;

    @Mock
    private ItemRepository itemRepository;

    @Before
    public void setup() {
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(createItem(1)));
        Mockito.when(itemRepository.findAll()).thenReturn(createItems());
        Mockito.when(itemRepository.findByName("item")).thenReturn(Arrays.asList(createItem(1), createItem(2)));

    }

    @Test
    public void GetAllItemTest() throws Exception {

        Mockito.when(itemRepository.findAll()).thenReturn(Arrays.asList(getItem_Test()));

        ResponseEntity<List<Item>> responseEntity = itemController.getItems();

//        List<Item> itemList = response.getBody();

        Assert.assertEquals(200, responseEntity.getStatusCodeValue());


        verify(itemRepository , times(1)).findAll();
    }



    @Test
    public void getItemById_Test() {
        ResponseEntity<Item> responseEntity = itemController.getItemById(1L);

        Assert.assertNotNull(responseEntity);


        Assert. assertEquals(200, responseEntity.getStatusCodeValue());

        Item item = responseEntity.getBody();
        Assert.assertEquals(createItem(1L), item);

        Assert.assertEquals(getItem_Test().getName(), item.getName());

        Assert.assertEquals(getItem_Test().getDescription(), item.getDescription());


        Assert.assertEquals(getItem_Test().getPrice(), item.getPrice());


        verify(itemRepository, times(1)).findById(1L);
    }


    @Test
    public void getItemByInvalidId_shouldGiveError() {
        ResponseEntity<Item> responseEntity = itemController.getItemById(10L);

        Assert.assertNotNull(responseEntity);

        Assert.assertEquals(404, responseEntity.getStatusCodeValue());

        Assert.assertNull(responseEntity.getBody());

        verify(itemRepository, times(1)).findById(10L);


    }

//    ResponseEntity<List<Item>> response = itemController.getItemsByName("item1");
//    assertNotNull(response);
//    assertEquals(200, response.getStatusCodeValue());

    @Test
    public void getItemByName_Test() {
        ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName("item");


        Assert.assertNotNull(responseEntity);

        Assert.assertEquals(200, responseEntity.getStatusCodeValue());


        List<Item> items = Arrays.asList(createItem(1), createItem(2),createItem(3));

        Assert.assertEquals(createItems(), items);

        verify(itemRepository , times(1)).findByName("item");
    }


    @Test
    public void getItemByInvalidName_shouldGiveError(){

        ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName("inavlid_name");

        Assert.assertNotNull(responseEntity);

        Assert.assertEquals(404, responseEntity.getStatusCodeValue());
    }



    public static List<Item> createItems() {
        List<Item> items = new ArrayList<>();
        for (int i = 1; i <= 3; i++)
        {
            Item item = createItem(i);
            items.add(item);
        }
        return items;
    }

    public static Item createItem(long id) {
        Item cartItem = new Item();

        cartItem.setId(id);

        BigDecimal bigDecimal = BigDecimal.valueOf(id * 1.2);

        cartItem.setPrice(bigDecimal);

        cartItem.setName("Item " + cartItem.getId());


        cartItem.setDescription("Item test");
        return cartItem;

    }

    private static Item getItem_Test() {

        Item item = new Item();


        item.setDescription("Item test");

        BigDecimal bigDecimal = BigDecimal.valueOf(1.2);
        item.setPrice(bigDecimal);

        item.setName("Item 1");

        return item;
    }


}
